/*
 *  This file is part of FreedomBox.
 *
 * FreedomBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FreedomBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FreedomBox. If not, see <http://www.gnu.org/licenses/>.
 */

package org.freedombox.freedombox.views.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_launcher.*
import org.freedombox.freedombox.API_URL
import org.freedombox.freedombox.APP_RESPONSE
import org.freedombox.freedombox.R
import org.freedombox.freedombox.SERVICES_URL
import org.freedombox.freedombox.components.AppComponent
import org.freedombox.freedombox.models.Shortcuts
import org.freedombox.freedombox.utils.ImageRenderer
import org.freedombox.freedombox.utils.network.getApps
import org.freedombox.freedombox.utils.network.urlJoin
import org.freedombox.freedombox.views.adapter.GridAdapter
import javax.inject.Inject

class LauncherFragment : BaseFragment() {

    @Inject lateinit var imageRenderer: ImageRenderer

    @Inject lateinit var sharedPreferences: SharedPreferences

    override fun getLayoutId() = R.layout.fragment_launcher

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = GridAdapter(activity!!.applicationContext, imageRenderer)

        appGrid.adapter = adapter

        fun getShortcutsFromResponse(response: String): Shortcuts? {
            val builder = GsonBuilder()
            val gson = builder.create()
            Log.d("RESPONSE:", response)
            return gson.fromJson(response, Shortcuts::class.java)
        }

        val onSuccess = fun(response: String) {
            sharedPreferences.edit().putString(APP_RESPONSE, response).apply() // TODO
            adapter.setData(getShortcutsFromResponse(response)!!.shortcuts)
        }

        val onFailure = fun() {
            if (sharedPreferences.contains(APP_RESPONSE)) {
                val old_shortcuts = sharedPreferences.getString(APP_RESPONSE, "[]")

                adapter.setData(getShortcutsFromResponse(old_shortcuts)!!.shortcuts)
            } else {
                appsNotAvailable.visibility = View.VISIBLE
            }
        }

        //TODO: Use the URL from settings once it is setup
        val url = urlJoin(API_URL, SERVICES_URL)

        getApps(context!!, url, onSuccess, onFailure)
    }

    companion object {
        fun new(args: Bundle): LauncherFragment {
            val fragment = LauncherFragment()
            fragment.arguments = args

            return fragment
        }
    }

    override fun injectFragment(appComponent: AppComponent) = appComponent.inject(this)
}

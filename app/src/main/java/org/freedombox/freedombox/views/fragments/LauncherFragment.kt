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
import org.freedombox.freedombox.APP_RESPONSE
import org.freedombox.freedombox.R
import org.freedombox.freedombox.SERVICES_URL
import org.freedombox.freedombox.components.AppComponent
import org.freedombox.freedombox.models.Shortcuts
import org.freedombox.freedombox.utils.ImageRenderer
import org.freedombox.freedombox.utils.network.apiUrl
import org.freedombox.freedombox.utils.network.getApps
import org.freedombox.freedombox.utils.network.urlJoin
import org.freedombox.freedombox.views.adapter.GridAdapter
import org.freedombox.freedombox.views.model.ConfigModel
import javax.inject.Inject

class LauncherFragment : BaseFragment() {

    @Inject lateinit var imageRenderer: ImageRenderer

    @Inject lateinit var sharedPreferences: SharedPreferences

    override fun getLayoutId() = R.layout.fragment_launcher

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val currentBox = arguments.getParcelable<ConfigModel>("current_box")
        val adapter = GridAdapter(activity!!.applicationContext, imageRenderer, currentBox.domain)
        appGrid.adapter = adapter

        fun getShortcutsFromResponse(response: String): Shortcuts? {
            val builder = GsonBuilder()
            val gson = builder.create()
            Log.d("RESPONSE:", response)
            return gson.fromJson(response, Shortcuts::class.java)
        }

        @Suppress("SENSELESS_COMPARISON")
        val onSuccess = fun(response: String) {
            sharedPreferences.edit().putString(APP_RESPONSE, response).apply() // TODO
            val shortcuts = getShortcutsFromResponse(response)!!.shortcuts
            adapter.setData(shortcuts.filter { it.clients != null })
        }

        val onFailure = fun() {
            if (sharedPreferences.contains(APP_RESPONSE)) {
                val old_shortcuts = sharedPreferences.getString(APP_RESPONSE, "[]")

                adapter.setData(getShortcutsFromResponse(old_shortcuts)!!.shortcuts)
            } else {
                appsNotAvailable.visibility = View.VISIBLE
            }
        }

        val servicesUrl = urlJoin(apiUrl(currentBox.domain), SERVICES_URL)
        getApps(context!!, servicesUrl, onSuccess, onFailure)
    }

    companion object {
        fun new(savedInstanceState: Bundle): LauncherFragment {
            val fragment = LauncherFragment()
            fragment.arguments = savedInstanceState
            return fragment
        }
    }

    override fun injectFragment(appComponent: AppComponent) = appComponent.inject(this)
}

/*
 * This file is part of FreedomBox.
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

package org.freedombox.freedombox.utils.network

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.freedombox.freedombox.models.Shortcut


fun getApps(context: Context, uri: String, onSuccess: (String) -> Unit,
               onFailure: () -> Unit) {

    val requestQueue = Volley.newRequestQueue(context)

    val onResponse = Response.Listener<String> { onSuccess(it) }

    val onError = Response.ErrorListener {
        Log.e("Failure: ", it.toString())
        onFailure()
    }

    val stringResponse = StringRequest(Request.Method.GET, uri, onResponse, onError)
    requestQueue.add(stringResponse)
}


fun urlJoin(vararg urls: String): String {
    return urls.map { it.trim('/') }.joinToString(separator = "/")
}

fun launchApp(shortcut: Shortcut, context: Context) {
    val app = getLaunchString(shortcut, context)
    val intent = getIntent(app, context.packageManager)
    if (intent != null) {
        context.startActivity(intent)
    } else {
        try {
            // Take to page on the app store
            context.startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$app")))
        } catch (ex: ActivityNotFoundException) {
            // Case for devices with no app store apps installed
            Log.e("ERROR", ex.message)
            Toast.makeText(context, "App not installed",
                    Toast.LENGTH_SHORT).show()
        }
    }
}

fun getLaunchString(shortcut: Shortcut, activity: Context): String {
    val freedomBoxURL = PreferenceManager.getDefaultSharedPreferences(activity).getString(
            "freedombox_url", "10.42.0.1")
//    return if false (service.getJSONArray("android-apps").empty())
//        service.getJSONObject("URL")?.let { jsonObj ->
//            val protocol = jsonObj.getString("protocol")
//            val domain = jsonObj.getString("domain").takeIf { it.isNotEmpty() } ?: freedomBoxURL
//            val port = jsonObj.getString("port")
//            val path = jsonObj.getString("path")
//            "$protocol://$domain:$port/$path"
//        }!!
//    else {
//        val apps = service.getJSONArray("android-apps").asList<String>()
//        apps.find { getLaunchIntent(it, activity.packageManager) != null } ?: apps[0]
//    }
    return "com.nutomic.syncthingandroid"
}

fun getLaunchIntent(packageName: String, packageManager: PackageManager) =
        packageManager.getLaunchIntentForPackage(packageName)

fun getWebIntent(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url))

fun getIntent(url: String, packageManager: PackageManager) =
        if (url.startsWith("http")) getWebIntent(url) else getLaunchIntent(url, packageManager)


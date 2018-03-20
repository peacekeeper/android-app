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
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.freedombox.freedombox.BASE_URL
import org.freedombox.freedombox.models.Platform
import org.freedombox.freedombox.models.Shortcut
import java.net.InetAddress
import java.net.UnknownHostException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager


val defaultHostNameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
val defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()

fun trustValidCert() {
    HttpsURLConnection.setDefaultHostnameVerifier(defaultHostNameVerifier)
    HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSocketFactory)
}

fun trustAnyCert() {
    try {
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        val context = SSLContext.getInstance("TLS")
        context.init(null, arrayOf<X509TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate?> = arrayOfNulls<X509Certificate?>(0)

            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }
        }), SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(
                context.socketFactory)
    } catch (e: Exception) { // should never happen
        e.printStackTrace()
    }
}


fun isPrivateIPAddress(string: String): Boolean {
    // FIXME Terrible hack. Should've used AsyncTask
    StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

    return try {
        val address = InetAddress.getByName(string)
        address.isSiteLocalAddress || address.isLoopbackAddress || address.isLinkLocalAddress
    } catch (e: UnknownHostException) {
        false
    }
}

fun getApps(context: Context, uri: String,
            onSuccess: (String) -> Unit,
            onFailure: () -> Unit) {

    // Trust certificates of the discovered boxes on the local network
    if (isPrivateIPAddress(uri.split("/")[2])) trustAnyCert() else trustValidCert()

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


fun apiUrl(baseUrl: String = BASE_URL) = urlJoin(baseUrl, "/plinth/api/1")

fun launchApp(shortcut: Shortcut, context: Context, baseUrl: String = BASE_URL) {
    val appName = getLaunchString(shortcut, context.packageManager, baseUrl)
    if (appName.isNotBlank()) {
        val intent = getIntent(appName, context.packageManager)
        if (intent != null) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } else {
            try {
                // Take to page on the app store
                val appStoreIntent = Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appName"))
                appStoreIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(appStoreIntent)
            } catch (ex: ActivityNotFoundException) {
                // Case for devices with no app store apps installed
                Log.e("ERROR", ex.message)
                Toast.makeText(context, "App not installed",
                        Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        Toast.makeText(context, "No apps defined", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Returns a string that can be used in an Android intent (web or package)
 * The order of preference for Android apps is as follows:
 *   1. an already installed app
 *   2. F-Droid app
 *   3. Google Play app
 */
fun getLaunchString(shortcut: Shortcut, packageManager: PackageManager, baseUrl: String = BASE_URL): String {
    val androidClients = shortcut.clients.filter { it.platforms.any { it.os == "android" } }
    return if (androidClients.isEmpty()) {
        val webClients = shortcut.clients.filter { it.platforms.any { it.type == "web" } }
        val platform = webClients.first().platforms.find { it.type == "web" }
        if (platform != null) {
            urlJoin(baseUrl, platform.url)
        } else ""
    } else {
        val installedApps = findInstalledApps(getAndroidPackageNames(shortcut), packageManager)
        if (installedApps.isNotEmpty()) {
            Log.d("Installed app: ", installedApps.first())
            return installedApps.first()  // TODO Handle the case of multiple installed apps
        } else {
            val fDroidClients = androidClients.filter { it.platforms.any { it.storeName == "f-droid" } }
            if (fDroidClients.isNotEmpty()) {
                val url = fDroidClients.first().platforms.first { it.storeName == "f-droid" }.url
                url.split("/").last()
            } else {
                val platform = androidClients.first().platforms.find { it.storeName == "google-play" }
                platform?.url?.split("=")?.last() ?: ""
            }
        }
    }
}

//fun isFDroidInstalled(packageManager: PackageManager): Boolean {
//    val apps = packageManager.getInstalledApplications(0)
//    return apps.any { app -> app.enabled and (app.packageName == "org.fdroid.fdroid") }
//}

fun findInstalledApps(packageNames: List<String>, packageManager: PackageManager): List<String> {
    val apps = packageManager.getInstalledApplications(0)
    return apps.filter { app -> app.enabled and (app.packageName in packageNames) }.map { it.packageName }
}

/**
 * Returns a list of Android package names for a given shortcut.
 */
fun getAndroidPackageNames(shortcut: Shortcut): List<String> {
    val platforms = shortcut.clients.flatMap { it.platforms.filter { it.os == "android" } }
    return platforms.map { extractPackageName(it) }.distinct()
}

fun extractPackageName(platform: Platform): String {
    val delimiter = if(platform.storeName == "f-droid") "/" else "="
    return platform.url.split(delimiter).last()
}

fun getIntent(url: String, packageManager: PackageManager) =
        if (url.startsWith("http")) getWebIntent(url) else getAppIntent(url, packageManager)

fun getAppIntent(packageName: String, packageManager: PackageManager): Intent? =
        packageManager.getLaunchIntentForPackage(packageName)

fun getWebIntent(url: String) = Intent(Intent.ACTION_VIEW, Uri.parse(url))

fun wrapHttps(url: String) = if (!url.startsWith("http")) "https://" + url else url

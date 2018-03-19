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

package org.freedombox.freedombox.utils.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.freedombox.freedombox.models.Shortcuts
import org.freedombox.freedombox.views.model.ConfigModel

fun getSharedPreference(sharedPreferences: SharedPreferences, key: String): String? =
        sharedPreferences.getString(key, null)

fun putSharedPreference(sharedPreferences: SharedPreferences,
                        key: String,
                        listItem: List<ConfigModel>) {
    val sharedPreferencesEditor = sharedPreferences.edit()
    sharedPreferencesEditor.putString(key,
        Gson().toJson(listItem))
    sharedPreferencesEditor.apply()
}

fun putSharedPreference(sharedPreferences: SharedPreferences,
                        key: String,
                        listItem: Map<String, ConfigModel>) {
    val sharedPreferencesEditor = sharedPreferences.edit()
    sharedPreferencesEditor.putString(key,
            Gson().toJson(listItem))
    sharedPreferencesEditor.apply()
}

val gson = GsonBuilder().create()

/**
 * Parses the sharedPreferences value for configured FreedomBoxes and converts it into a
 * Map<String, ConfigModel>
 * Returns null if the input string is null
 */
fun getConfiguredBoxesMap(configuredBoxesJSON: String?): Map<String, ConfigModel>? =
    configuredBoxesJSON?.let {
        gson.fromJson<Map<String, ConfigModel>>(configuredBoxesJSON,
                object : TypeToken<Map<String, ConfigModel>>() {}.type)
    }

/**
 * Parses the sharedPreferences value for application shortcuts and
 * converts it into a Map<String, Shortcuts>
 * Returns null if the input string is null
 */
fun getShortcutsMap(shortcutsJSON: String?): Map<String, Shortcuts>? =
        shortcutsJSON?.let {
            gson.fromJson<Map<String, Shortcuts>>(it,
                    object : TypeToken<Map<String, Shortcuts>>() {}.type)
        }

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

package org.freedombox.freedombox.models

import com.google.gson.annotations.SerializedName

data class Shortcuts(val shortcuts: List<Shortcut>)

data class Shortcut(val name: String,
                    @SerializedName("short_description") val shortDescription: String,
                    val description: List<String>,
                    @SerializedName("icon_url") val iconUrl: String,
                    val clients: List<Client>)

data class Client(val name: String,
                  val platforms: List<Platform>)

data class Platform(val type: String,
                    val format: String?,
                    val os: String?,
                    @SerializedName("store_name") val storeName: String?,
                    val url: String)
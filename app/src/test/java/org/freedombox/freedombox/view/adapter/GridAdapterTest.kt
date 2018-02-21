/* This file is part of FreedomBox.
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

package org.freedombox.freedombox.view.adapter

import android.content.Context
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.app_container.view.*
import org.freedombox.freedombox.BuildConfig
import org.freedombox.freedombox.models.Shortcut
import org.freedombox.freedombox.utils.ImageRenderer
import org.freedombox.freedombox.views.adapter.GridAdapter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class GridAdapterTest {
    private val applicationContext: Context = application.applicationContext
    private val gridAdapter = GridAdapter(applicationContext, imageRenderer = ImageRenderer(applicationContext), baseUrl = "https://localhost")
    private val gson = GsonBuilder().create()
    private var items = mutableListOf<Shortcut>()
    private val shortcut = gson.fromJson("""
{
  "name": "Deluge",
  "short_description": "BitTorrent Web Client",
  "description": null,
  "icon_url": "/plinth/static/theme/icons/deluge.png",
  "clients": [
    {
      "name": "Deluge",
      "description": "Bittorrent client written in Python/PyGTK",
      "platforms": [
        {
          "type": "web",
          "url": "/deluge"
        },
        {
          "type": "package",
          "format": "deb",
          "name": "deluge"
        }
      ]
    }
  ]
}
    """, Shortcut::class.java)

    @Before
    fun setUp() {
        items.add(shortcut)
    }

    @Test
    fun testItemCount() {
        gridAdapter.setData(items)

        assertEquals(gridAdapter.count, 1)
    }

    @Test
    fun testGetItemAtPosition() {
        gridAdapter.setData(items)

        assertEquals(gridAdapter.getItem(0), items[0])
    }

    @Test
    fun testViewIsGettingPopulated() {
        gridAdapter.setData(items)

        val view = gridAdapter.getView(0, null, null)

        assertEquals(view.appName.text.toString(), "Deluge")
        assertEquals(view.appDescription.text.toString(), "BitTorrent Web Client")
    }
}

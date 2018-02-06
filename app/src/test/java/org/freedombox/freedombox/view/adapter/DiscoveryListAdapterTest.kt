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
import org.freedombox.freedombox.BuildConfig
import org.freedombox.freedombox.views.adapter.DiscoveryListAdapter
import org.freedombox.freedombox.views.model.ConfigModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class DiscoveryListAdapterTest {

    private val applicationContext: Context = RuntimeEnvironment.application.applicationContext
    private lateinit var listAdapter: DiscoveryListAdapter
    private val box1 = ConfigModel("box1", "alice.freedombox.rocks", "alice", "blah*123", false)
    private val box2 = ConfigModel("box2", "bob.freedombox.rocks", "bob", "meh@123", false)
    private val boxList = listOf<ConfigModel>(box1, box2)

    @Before
    fun setUp() {
        listAdapter = DiscoveryListAdapter(applicationContext, boxList, false,
                object : DiscoveryListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    @Test
    fun testItemCount() {
        Assert.assertEquals(listAdapter.itemCount, 2)
    }

    @Test
    fun testGetItemViewTypeAtPositionZero() {
        Assert.assertEquals(listAdapter.getItemViewType(0), 0)
    }

    @Test
    fun testGetItemIdAtPositionOne() {
        Assert.assertEquals(listAdapter.getItemId(1), boxList[1].hashCode().toLong())
    }

}

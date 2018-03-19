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

package org.freedombox.freedombox.view.fragments

import android.preference.PreferenceManager
import android.widget.EditText
import android.widget.Switch
import org.freedombox.freedombox.BuildConfig
import org.freedombox.freedombox.R
import org.freedombox.freedombox.views.activities.SetupActivity
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class SetupFragmentTest {

    @Test
    fun shouldBeAbleToViewViewsInScreen() {

        val activity = Robolectric.setupActivity(SetupActivity::class.java)
        val shadowActivity = Shadows.shadowOf(activity)

        val boxName = shadowActivity.findViewById(R.id.boxName)
        Assert.assertNotNull(boxName)

        val discoveredUrl = shadowActivity.findViewById(R.id.discoveredUrl)
        Assert.assertNotNull(discoveredUrl)

        val default = shadowActivity.findViewById(R.id.defaultStatus)
        Assert.assertNotNull(default)

        val saveConfig = shadowActivity.findViewById(R.id.saveConfig)
        Assert.assertNotNull(saveConfig)
    }

    @Test
    fun navigateToLauncherScreenOnButtonClick() {

        val activity = Robolectric.setupActivity(SetupActivity::class.java)
        val shadowActivity = Shadows.shadowOf(activity)
        shadowActivity.findViewById(R.id.saveConfig).performClick()

        Assert.assertTrue(shadowActivity.isFinishing)
    }

    @Test
    fun checkInformationStoredInSharedPreferenceOnButtonClick() {
        val applicationContext = RuntimeEnvironment.application.applicationContext
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(applicationContext)

        val boxName = "freedomBox"
        val domain = "domain"
        val default = false

        val value = """
            {"$boxName":{"boxName":"$boxName","domain":"https://$domain","default":false}}
        """.trim()

        val activity = Robolectric.setupActivity(SetupActivity::class.java)
        val shadowActivity = Shadows.shadowOf(activity)

        (shadowActivity.findViewById(R.id.boxName) as EditText).setText(boxName)
        (shadowActivity.findViewById(R.id.discoveredUrl) as EditText).setText(domain)
        (shadowActivity.findViewById(R.id.defaultStatus) as Switch).isChecked = default

        shadowActivity.findViewById(R.id.saveConfig).performClick()

        val configuredBoxesJSON = sharedPreferences.getString("saved_boxes", null)
        Assert.assertEquals(value, configuredBoxesJSON)
    }
}

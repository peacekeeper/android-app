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

import android.content.Intent
import android.preference.PreferenceManager
import org.freedombox.freedombox.BuildConfig
import org.freedombox.freedombox.views.activities.DiscoveryActivity
import org.freedombox.freedombox.views.activities.LauncherActivity
import org.freedombox.freedombox.views.activities.MainActivity
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowApplication
import org.robolectric.shadows.ShadowLooper


@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class)
class SplashFragmentTest {
    val key = "saved_boxes"
    val applicationContext = RuntimeEnvironment.application.applicationContext
    val sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(applicationContext)

    @After
    fun destroy() {
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun navigateToLauncherScreenWhenDefaultFreedomBoxConfigured() {

        val value = """
            {"FreedomBox": {
	            "boxName": "FreedomBox",
	            "default": true,
	            "domain": "/10.42.0.1"
            }}
        """

        sharedPreferences.edit().putString(key, value).commit()
        val activity = Robolectric.setupActivity(MainActivity::class.java)
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        val actualIntent = ShadowApplication.getInstance().nextStartedActivity
        val expectedIntent = Intent(activity, LauncherActivity::class.java)
        Assert.assertEquals(expectedIntent.javaClass, actualIntent.javaClass)
    }

    @Test
    fun navigateToDiscoveryScreenWhenNoDefaultFreedomBoxConfigured() {
        val activity = Robolectric.setupActivity(MainActivity::class.java)
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        val actualIntent = ShadowApplication.getInstance().nextStartedActivity
        val expectedIntent = Intent(activity, DiscoveryActivity::class.java)
        Assert.assertEquals(expectedIntent.javaClass, actualIntent.javaClass)
    }

}

package org.freedombox.freedombox.utils.network

import org.junit.Test

import org.junit.Assert.*

class AppLoaderTest {
    @Test
    fun testUrlJoin() {
        assertEquals("https://freedombox.local/plinth/static/theme/icons/deluge.png",
                urlJoin("https://freedombox.local/", "/plinth", "static/theme/icons/deluge.png"))
    }

}
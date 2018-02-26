package org.freedombox.freedombox.utils.network

import org.junit.Assert.*
import org.junit.Test

class IpAddressValidatorTest {

    @Test
    fun ipv4AddressTestMatchFullString() {
        assertEquals(true, Ipv4AddressValidator.isValid("10.42.0.1"))
    }

    @Test
    fun ipv4AddressTestPartialMatchString() {
        assertEquals(true, Ipv4AddressValidator.isValid("https://10.42.0.1/api/1/shortcuts"))
    }

}
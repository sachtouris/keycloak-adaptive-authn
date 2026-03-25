package io.github.mabartos.context.ip;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class IpAddressUtilsTest {

    @Test
    public void testValidIpv4Address() {
        IPAddress ip = IPAddress.parse("192.168.1.1");
        assertThat(ip, notNullValue());
        assertThat(ip.isIPv4(), is(true));
    }

    @Test
    public void testValidIpv6Address() {
        IPAddress ip = IPAddress.parse("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
        assertThat(ip, notNullValue());
        assertThat(ip.isIPv6(), is(true));
    }

    @Test
    public void testLocalhostIpv4() {
        IPAddress ip = IPAddress.parse("127.0.0.1");
        assertThat(ip, notNullValue());
        assertThat(ip.isLoopback(), is(true));
    }

    @Test
    public void testLocalhostIpv6() {
        IPAddress ip = IPAddress.parse("::1");
        assertThat(ip, notNullValue());
        assertThat(ip.isLoopback(), is(true));
    }

    @Test
    public void testPrivateIpv4Addresses() {
        IPAddress ip1 = IPAddress.parse("10.0.0.1");
        assertThat(ip1, notNullValue());
        assertThat(ip1.isIPv4(), is(true));

        IPAddress ip2 = IPAddress.parse("172.16.0.1");
        assertThat(ip2, notNullValue());
        assertThat(ip2.isIPv4(), is(true));

        IPAddress ip3 = IPAddress.parse("192.168.0.1");
        assertThat(ip3, notNullValue());
        assertThat(ip3.isIPv4(), is(true));
    }

    @Test
    public void testPublicIpv4Address() {
        IPAddress ip = IPAddress.parse("8.8.8.8");
        assertThat(ip, notNullValue());
        assertThat(ip.isIPv4(), is(true));
        assertThat(ip.isLoopback(), is(false));
    }

    @Test
    public void testInvalidIpAddress() {
        IPAddress ip = IPAddress.parse("999.999.999.999");
        assertThat(ip, is((IPAddress) null));
    }

    @Test
    public void testIpAddressComparison() {
        IPAddress ip1 = IPAddress.parse("192.168.1.1");
        IPAddress ip2 = IPAddress.parse("192.168.1.1");
        IPAddress ip3 = IPAddress.parse("192.168.1.2");

        assertThat(ip1.equals(ip2), is(true));
        assertThat(ip1.equals(ip3), is(false));
    }

    @Test
    public void testIpAddressInRange() {
        IPAddress ip = IPAddress.parse("192.168.1.50");
        IPAddress rangeStart = IPAddress.parse("192.168.1.1");
        IPAddress rangeEnd = IPAddress.parse("192.168.1.100");

        assertThat(ip.compareTo(rangeStart) >= 0, is(true));
        assertThat(ip.compareTo(rangeEnd) <= 0, is(true));
        assertThat(ip.isInRange(rangeStart, rangeEnd), is(true));
    }

    @Test
    public void testIpv4MappedIpv6() {
        IPAddress ip = IPAddress.parse("::ffff:192.168.1.1");
        assertThat(ip, notNullValue());
        assertThat(ip.isIPv6(), is(true));
        assertThat(ip.isIPv4Convertible(), is(true));
    }

    @Test
    public void testLinkLocalIpv6() {
        IPAddress ip = IPAddress.parse("fe80::1");
        assertThat(ip, notNullValue());
        assertThat(ip.isLinkLocal(), is(true));
    }
}

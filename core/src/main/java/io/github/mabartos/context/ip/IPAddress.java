/*
 * Copyright 2024 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mabartos.context.ip;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * Lightweight IP address wrapper around {@link java.net.InetAddress}
 */
public class IPAddress implements Comparable<IPAddress> {
    private final InetAddress address;
    private final boolean ipv4Mapped;

    private IPAddress(InetAddress address, boolean ipv4Mapped) {
        this.address = Objects.requireNonNull(address);
        this.ipv4Mapped = ipv4Mapped;
    }

    /**
     * Parse an IP address string. Returns null if the string is not a valid IP address.
     */
    public static IPAddress parse(String ipString) {
        if (ipString == null || ipString.isEmpty()) {
            return null;
        }

        try {
            ipString = ipString.trim();

            // Strip zone ID for IPv6 link-local (e.g., "fe80::1%eth0")
            int zoneIdx = ipString.indexOf('%');
            if (zoneIdx > 0) {
                ipString = ipString.substring(0, zoneIdx);
            }

            // Validate IPv4 octets are in range (InetAddress accepts values > 255)
            if (!ipString.contains(":") && ipString.matches("\\d{1,3}(\\.\\d{1,3}){3}")) {
                for (String octet : ipString.split("\\.")) {
                    if (Integer.parseInt(octet) > 255) {
                        return null;
                    }
                }
            }

            // Detect IPv4-mapped IPv6 before Java auto-converts it
            boolean isIpv4Mapped = ipString.toLowerCase().startsWith("::ffff:");

            InetAddress addr = InetAddress.getByName(ipString);
            return new IPAddress(addr, isIpv4Mapped);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public boolean isIPv4() {
        return address instanceof Inet4Address && !ipv4Mapped;
    }

    public boolean isIPv6() {
        return address instanceof Inet6Address || ipv4Mapped;
    }

    public boolean isLoopback() {
        return address.isLoopbackAddress();
    }

    public boolean isLinkLocal() {
        return address.isLinkLocalAddress();
    }

    /**
     * Returns true if this is an IPv4-mapped IPv6 address (::ffff:x.x.x.x)
     */
    public boolean isIPv4Convertible() {
        return ipv4Mapped;
    }

    /**
     * Check if this IP address is within the range [start, end] (inclusive)
     */
    public boolean isInRange(IPAddress start, IPAddress end) {
        return this.compareTo(start) >= 0 && this.compareTo(end) <= 0;
    }

    @Override
    public int compareTo(IPAddress other) {
        return toBigInteger(this.address).compareTo(toBigInteger(other.address));
    }

    private static BigInteger toBigInteger(InetAddress addr) {
        return new BigInteger(1, addr.getAddress());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IPAddress other)) return false;
        return address.equals(other.address);
    }

    @Override
    public int hashCode() {
        return address.hashCode();
    }

    @Override
    public String toString() {
        return address.getHostAddress();
    }
}

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

import io.github.mabartos.context.device.DeviceRepresentationContext;
import jakarta.ws.rs.core.HttpHeaders;
import org.keycloak.common.util.CollectionUtil;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.account.DeviceRepresentation;
import org.keycloak.utils.StringUtil;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpAddressUtils {

    public static final Pattern IP_PATTERN = Pattern.compile("(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})");
    public static final Pattern FORWARDED_FOR_PATTERN = Pattern.compile("for=([^;]+)");

    public static boolean isInRange(RealmModel realm, DeviceRepresentationContext context, List<String> list) {
        if (CollectionUtil.isEmpty(list)) throw new IllegalArgumentException("Cannot parse IP Address");

        return list.stream()
                .filter(f -> f.contains("-"))
                .anyMatch(f -> manageRange(realm, context, f));
    }

    private static boolean manageRange(RealmModel realm, DeviceRepresentationContext context, String ipAddress) {
        var items = ipAddress.split("-");
        if (items.length != 2) {
            throw new IllegalArgumentException("Invalid IP Address range format");
        }

        var start = IPAddress.parse(items[0].trim());
        var end = IPAddress.parse(items[1].trim());
        if (start == null || end == null) {
            throw new IllegalArgumentException("Cannot parse IP Address range");
        }

        var deviceIp = context.getData(realm)
                .map(DeviceRepresentation::getIpAddress)
                .filter(StringUtil::isNotBlank);

        if (deviceIp.isEmpty()) throw new IllegalArgumentException("Cannot obtain IP Address");

        var ip = IPAddress.parse(deviceIp.get());
        if (ip == null) throw new IllegalArgumentException("Cannot parse device IP Address");

        return ip.isInRange(start, end);
    }

    public static Optional<IPAddress> getIpAddress(String ipAddress) {
        return Optional.ofNullable(IPAddress.parse(ipAddress));
    }

    private static IPAddress parseForwardedHeader(String header, Pattern pattern) {
        Matcher matcher = pattern.matcher(header);
        if (matcher.find()) {
            var ip = IpAddressUtils.getIpAddress(matcher.group(1));
            if (ip.isPresent()) {
                return ip.get();
            }
        }
        return null;
    }

    public static Optional<IPAddress> getIpAddressFromHeader(HttpHeaders headers, String headerName, Pattern pattern) {
        return Optional.ofNullable(headers.getRequestHeader(headerName))
                .flatMap(h -> h.stream().findFirst())
                .map(h -> List.of(h.split(",")))
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(f -> parseForwardedHeader(f, pattern));
    }
}

/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

// SunJSSE does not support dynamic system properties, no way to re-use
// system properties in samevm/agentvm mode.

/*
 * @test
 * @bug 8043758
 * @summary Datagram Transport Layer Security (DTLS)
 * @compile DTLSOverDatagram.java
 * @run main/othervm InvalidCookie
 */

import java.net.DatagramPacket;
import java.net.SocketAddress;

/**
 * Test that if the handshake cookie in client side is incorrect, the handshake
 * process can continue as if the client does not use cookie.
 */
public class InvalidCookie extends DTLSOverDatagram {
    boolean needInvalidCookie = true;

    public static void main(String[] args) throws Exception {
        InvalidCookie testCase = new InvalidCookie();
        testCase.runTest(testCase);
    }

    @Override
    DatagramPacket createHandshakePacket(byte[] ba, SocketAddress socketAddr) {
        if (needInvalidCookie && (ba.length >= 60) &&
                (ba[0] == (byte)0x16) && (ba[13] == (byte)0x03)) {
            // HelloVerifyRequest
            needInvalidCookie = false;
            System.out.println("invalidate handshake verify cookie");
            if (ba[ba.length - 1] == (byte)0xFF) {
                ba[ba.length - 1] = (byte)0xFE;
            } else {
                ba[ba.length - 1] = (byte)0xFF;
            }
        }

        return super.createHandshakePacket(ba, socketAddr);
    }
}

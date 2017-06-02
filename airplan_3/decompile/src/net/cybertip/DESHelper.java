/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 */
package net.cybertip;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import net.cybertip.DES;
import org.apache.commons.codec.binary.Base64;

public class DESHelper {
    public static String takeEncryptedString(String message, String key) {
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedBytes = DES.encrypt(messageBytes, key);
        return Base64.encodeBase64String((byte[])encryptedBytes);
    }
}


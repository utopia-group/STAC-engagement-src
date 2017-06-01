/*
 * Decompiled with CFR 0_121.
 */
package stac.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    private MessageDigest msg;

    public SHA256() {
        try {
            this.msg = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] digest() {
        byte[] digest = this.msg.digest();
        this.msg = null;
        return digest;
    }

    public static /* varargs */ byte[] digest(byte[] ... array) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            for (byte[] bytes : array) {
                sha256.update(bytes);
            }
            return sha256.digest();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to acquire required crytographic libraries.");
        }
    }

    public void update(byte[] buffer, int bpos, int i) {
        if (this.msg == null) {
            throw new RuntimeException("Closed SHA256 update detected");
        }
        this.msg.update(buffer, bpos, i);
    }

    public void update(byte[] bytes) {
        if (this.msg == null) {
            throw new RuntimeException("Closed SHA256 update detected");
        }
        this.msg.update(bytes);
    }
}


/*
 * Decompiled with CFR 0_121.
 */
package stac.crypto;

import stac.parser.OpenSSLRSAPEM;

public class RSA {
    public OpenSSLRSAPEM.INTEGER encrypt(OpenSSLRSAPEM.INTEGER message, OpenSSLRSAPEM.INTEGER publicExponent, OpenSSLRSAPEM.INTEGER modulus) {
        return this.run(message, publicExponent, modulus);
    }

    public OpenSSLRSAPEM.INTEGER decrypt(OpenSSLRSAPEM.INTEGER message, OpenSSLRSAPEM.INTEGER privateExponent, OpenSSLRSAPEM.INTEGER modulus) {
        return this.run(message, privateExponent, modulus);
    }

    private OpenSSLRSAPEM.INTEGER run(OpenSSLRSAPEM.INTEGER m, OpenSSLRSAPEM.INTEGER exp, OpenSSLRSAPEM.INTEGER mod) {
        boolean aboveZero;
        boolean withinMod = m.compareTo(mod) < 0;
        boolean bl = aboveZero = m.compareTo(0) >= 0;
        if (withinMod && aboveZero) {
            return m.modPow(exp, mod);
        }
        throw new RSAMessageSizeException(withinMod, aboveZero);
    }

    static class RSAMessageSizeException
    extends RuntimeException {
        private static final long serialVersionUID = -6006531878380802990L;
        private final String message;

        private RSAMessageSizeException(boolean messageFitsInModulus, boolean messageIsGreaterThanZero) {
            this.message = "Message fits under mod? " + messageFitsInModulus + ", " + "Message is greater than zero? " + messageIsGreaterThanZero;
        }

        @Override
        public String getMessage() {
            return this.message;
        }
    }

}


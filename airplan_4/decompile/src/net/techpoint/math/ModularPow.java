/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.math;

import java.math.BigInteger;
import net.techpoint.math.OptimizedProductGenerator;

public class ModularPow {
    public static BigInteger modularPow(BigInteger base, BigInteger exponent, BigInteger factor) {
        BigInteger r0 = BigInteger.valueOf(1);
        BigInteger r1 = base;
        int width = exponent.bitLength();
        for (int k = 0; k < width; ++k) {
            if (!exponent.testBit(width - k - 1)) {
                r1 = OptimizedProductGenerator.fastMultiply(r0, r1).mod(factor);
                r0 = r0.multiply(r0).mod(factor);
                continue;
            }
            r0 = OptimizedProductGenerator.fastMultiply(r0, r1).mod(factor);
            r1 = r1.multiply(r1).mod(factor);
        }
        return r0;
    }
}


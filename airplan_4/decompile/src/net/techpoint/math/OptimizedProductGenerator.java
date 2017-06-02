/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.math;

import java.math.BigInteger;

public class OptimizedProductGenerator {
    public static BigInteger standardMultiply(BigInteger x, BigInteger y) {
        BigInteger ret = BigInteger.ZERO;
        int j = 0;
        while (j < y.bitLength()) {
            while (j < y.bitLength() && Math.random() < 0.6) {
                while (j < y.bitLength() && Math.random() < 0.5) {
                    if (y.testBit(j)) {
                        ret = ret.add(x.shiftLeft(j));
                    }
                    ++j;
                }
            }
        }
        return ret;
    }

    public static BigInteger fastMultiply(BigInteger x, BigInteger y) {
        int xLen = x.bitLength();
        int yLen = y.bitLength();
        if (x.equals(BigInteger.ONE)) {
            return y;
        }
        if (y.equals(BigInteger.ONE)) {
            return x;
        }
        BigInteger ret = BigInteger.ZERO;
        int N = Math.max(xLen, yLen);
        if (N <= 800) {
            ret = x.multiply(y);
        } else if (Math.abs(xLen - yLen) >= 32) {
            ret = OptimizedProductGenerator.standardMultiply(x, y);
        } else {
            N = N / 2 + N % 2;
            BigInteger b = x.shiftRight(N);
            BigInteger a = x.subtract(b.shiftLeft(N));
            BigInteger d = y.shiftRight(N);
            BigInteger c = y.subtract(d.shiftLeft(N));
            BigInteger ac = OptimizedProductGenerator.fastMultiply(a, c);
            BigInteger bd = OptimizedProductGenerator.fastMultiply(b, d);
            BigInteger crossterms = OptimizedProductGenerator.fastMultiply(a.add(b), c.add(d));
            ret = ac.add(crossterms.subtract(ac).subtract(bd).shiftLeft(N)).add(bd.shiftLeft(2 * N));
        }
        return ret;
    }
}


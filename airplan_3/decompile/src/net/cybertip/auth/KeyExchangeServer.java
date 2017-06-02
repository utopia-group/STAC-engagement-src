/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.auth;

import java.math.BigInteger;
import java.security.SecureRandom;
import net.cybertip.math.ModularExponentiation;

public class KeyExchangeServer {
    private BigInteger secretKey;
    private BigInteger divisor;
    private BigInteger generator;
    private BigInteger publicKey;
    private static final String DEFAULT_MODULUS = "0xFFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF";
    private static final BigInteger DEFAULT_GENERATOR = BigInteger.valueOf(2);

    public KeyExchangeServer(BigInteger secretKey, BigInteger divisor, BigInteger generator) {
        this.secretKey = secretKey;
        this.divisor = divisor;
        this.generator = generator;
        this.publicKey = this.generator.modPow(this.secretKey, this.divisor);
    }

    public KeyExchangeServer(String secretKey, String divisor, String generator) {
        this(KeyExchangeServer.stringToBigInt(secretKey), KeyExchangeServer.stringToBigInt(divisor), KeyExchangeServer.stringToBigInt(generator));
    }

    public KeyExchangeServer(String secretKey) {
        this(KeyExchangeServer.stringToBigInt(secretKey), KeyExchangeServer.stringToBigInt("0xFFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF"), DEFAULT_GENERATOR);
    }

    public KeyExchangeServer() {
        this(KeyExchangeServer.makeRandomSecret(), KeyExchangeServer.stringToBigInt("0xFFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF"), DEFAULT_GENERATOR);
    }

    public BigInteger takeDivisor() {
        return this.divisor;
    }

    public BigInteger takeGenerator() {
        return this.generator;
    }

    public BigInteger fetchPublicKey() {
        return this.publicKey;
    }

    public BigInteger generateMasterSecret(BigInteger clientPublic) {
        return ModularExponentiation.modularExponentiation(clientPublic, this.secretKey, this.divisor);
    }

    private static BigInteger stringToBigInt(String number) {
        if (number.startsWith("0x")) {
            return new BigInteger(number.substring(2), 16);
        }
        return new BigInteger(number);
    }

    private static BigInteger makeRandomSecret() {
        SecureRandom r = new SecureRandom();
        byte[] randbytes = new byte[8];
        r.nextBytes(randbytes);
        return new BigInteger(1, randbytes);
    }
}


/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.routing.keep;

import java.io.File;
import java.util.Random;
import net.cybertip.routing.keep.AirDatabase;

public class AirDatabaseBuilder {
    private Random random = new Random();
    private File databaseFile;

    public AirDatabaseBuilder fixRandom(Random random) {
        this.random = random;
        return this;
    }

    public AirDatabaseBuilder defineDatabaseFile(File databaseFile) {
        this.databaseFile = databaseFile;
        return this;
    }

    public AirDatabase makeAirDatabase() {
        return new AirDatabase(this.databaseFile, this.random);
    }
}


/*
 * Decompiled with CFR 0_121.
 */
package com.example.linalg;

import com.example.linalg.external.LinearAlgebraService;
import java.io.IOException;
import java.io.PrintStream;

public class Main {
    public static void printHelp() {
        StringBuffer sb = new StringBuffer();
        sb.append("Invalid arguments, correct usage is:\n");
        sb.append("\tjava Main <port>\n");
        sb.append("\t\twhere <port> is a TCP port to listen for requests on\n");
        System.out.println(sb.toString());
    }

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                Main.printHelp();
                System.exit(-1);
            }
            int p = Integer.parseInt(args[0]);
            new LinearAlgebraService(p);
        }
        catch (IOException ioe) {
            System.out.println("Failed to start web service:\n" + ioe);
            System.exit(-1);
        }
    }
}


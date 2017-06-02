/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.parsing.simple.retriever;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import net.cybertip.parsing.simple.retriever.ParseTrouble;
import net.cybertip.parsing.simple.retriever.Yytoken;

class Yylex {
    public static final int YYEOF = -1;
    private static final int ZZ_BUFFERSIZE = 16384;
    public static final int YYINITIAL = 0;
    public static final int STRING_BEGIN = 2;
    private static final int[] ZZ_LEXSTATE = new int[]{0, 0, 1, 1};
    private static final String ZZ_CMAP_PACKED = "\t\u0000\u0001\u0007\u0001\u0007\u0002\u0000\u0001\u0007\u0012\u0000\u0001\u0007\u0001\u0000\u0001\t\b\u0000\u0001\u0006\u0001\u0019\u0001\u0002\u0001\u0004\u0001\n\n\u0003\u0001\u001a\u0006\u0000\u0004\u0001\u0001\u0005\u0001\u0001\u0014\u0000\u0001\u0017\u0001\b\u0001\u0018\u0003\u0000\u0001\u0012\u0001\u000b\u0002\u0001\u0001\u0011\u0001\f\u0005\u0000\u0001\u0013\u0001\u0000\u0001\r\u0003\u0000\u0001\u000e\u0001\u0014\u0001\u000f\u0001\u0010\u0005\u0000\u0001\u0015\u0001\u0000\u0001\u0016\uff82\u0000";
    private static final char[] ZZ_CMAP = Yylex.zzUnpackCMap("\t\u0000\u0001\u0007\u0001\u0007\u0002\u0000\u0001\u0007\u0012\u0000\u0001\u0007\u0001\u0000\u0001\t\b\u0000\u0001\u0006\u0001\u0019\u0001\u0002\u0001\u0004\u0001\n\n\u0003\u0001\u001a\u0006\u0000\u0004\u0001\u0001\u0005\u0001\u0001\u0014\u0000\u0001\u0017\u0001\b\u0001\u0018\u0003\u0000\u0001\u0012\u0001\u000b\u0002\u0001\u0001\u0011\u0001\f\u0005\u0000\u0001\u0013\u0001\u0000\u0001\r\u0003\u0000\u0001\u000e\u0001\u0014\u0001\u000f\u0001\u0010\u0005\u0000\u0001\u0015\u0001\u0000\u0001\u0016\uff82\u0000");
    private static final int[] ZZ_ACTION = Yylex.zzUnpackAction();
    private static final String ZZ_ACTION_PACKED_0 = "\u0002\u0000\u0002\u0001\u0001\u0002\u0001\u0003\u0001\u0004\u0003\u0001\u0001\u0005\u0001\u0006\u0001\u0007\u0001\b\u0001\t\u0001\n\u0001\u000b\u0001\f\u0001\r\u0005\u0000\u0001\f\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0001\u0014\u0001\u0000\u0001\u0015\u0001\u0000\u0001\u0015\u0004\u0000\u0001\u0016\u0001\u0017\u0002\u0000\u0001\u0018";
    private static final int[] ZZ_ROWMAP = Yylex.zzUnpackRowMap();
    private static final String ZZ_ROWMAP_PACKED_0 = "\u0000\u0000\u0000\u001b\u00006\u0000Q\u0000l\u0000\u0087\u00006\u0000\u00a2\u0000\u00bd\u0000\u00d8\u00006\u00006\u00006\u00006\u00006\u00006\u0000\u00f3\u0000\u010e\u00006\u0000\u0129\u0000\u0144\u0000\u015f\u0000\u017a\u0000\u0195\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u0000\u01b0\u0000\u01cb\u0000\u01e6\u0000\u01e6\u0000\u0201\u0000\u021c\u0000\u0237\u0000\u0252\u00006\u00006\u0000\u026d\u0000\u0288\u00006";
    private static final int[] ZZ_TRANS = new int[]{2, 2, 3, 4, 2, 2, 2, 5, 2, 6, 2, 2, 7, 8, 2, 9, 2, 2, 2, 2, 2, 10, 11, 12, 13, 14, 15, 16, 16, 16, 16, 16, 16, 16, 16, 17, 18, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 19, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 21, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 22, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 16, 16, 16, 16, 16, 16, 16, 16, -1, -1, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, -1, -1, -1, -1, -1, -1, -1, -1, 24, 25, 26, 27, 28, 29, 30, 31, 32, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 33, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 34, 35, -1, -1, 34, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 37, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 38, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 39, -1, 39, -1, 39, -1, -1, -1, -1, -1, 39, 39, -1, -1, -1, -1, 39, 39, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 33, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 20, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 35, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 38, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 40, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 41, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 42, -1, 42, -1, 42, -1, -1, -1, -1, -1, 42, 42, -1, -1, -1, -1, 42, 42, -1, -1, -1, -1, -1, -1, -1, -1, -1, 43, -1, 43, -1, 43, -1, -1, -1, -1, -1, 43, 43, -1, -1, -1, -1, 43, 43, -1, -1, -1, -1, -1, -1, -1, -1, -1, 44, -1, 44, -1, 44, -1, -1, -1, -1, -1, 44, 44, -1, -1, -1, -1, 44, 44, -1, -1, -1, -1, -1, -1, -1, -1};
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;
    private static final String[] ZZ_ERROR_MSG = new String[]{"Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large"};
    private static final int[] ZZ_ATTRIBUTE = Yylex.zzUnpackAttribute();
    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\u0002\u0000\u0001\t\u0003\u0001\u0001\t\u0003\u0001\u0006\t\u0002\u0001\u0001\t\u0005\u0000\b\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0004\u0000\u0002\t\u0002\u0000\u0001\t";
    private Reader zzReader;
    private int zzState;
    private int zzLexicalState = 0;
    private char[] zzBuffer = new char[16384];
    private int zzMarkedPos;
    private int zzCurrentPos;
    private int zzStartRead;
    private int zzEndRead;
    private int yyline;
    private int yychar;
    private int yycolumn;
    private boolean zzAtBOL = true;
    private boolean zzAtEOF;
    private StringBuffer sb = new StringBuffer();

    private static int[] zzUnpackAction() {
        int[] result = new int[45];
        int offset = 0;
        offset = Yylex.zzUnpackAction("\u0002\u0000\u0002\u0001\u0001\u0002\u0001\u0003\u0001\u0004\u0003\u0001\u0001\u0005\u0001\u0006\u0001\u0007\u0001\b\u0001\t\u0001\n\u0001\u000b\u0001\f\u0001\r\u0005\u0000\u0001\f\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0001\u0014\u0001\u0000\u0001\u0015\u0001\u0000\u0001\u0015\u0004\u0000\u0001\u0016\u0001\u0017\u0002\u0000\u0001\u0018", offset, result);
        return result;
    }

    private static int zzUnpackAction(String packed, int offset, int[] result) {
        int b = 0;
        int j = offset;
        int l = packed.length();
        while (b < l) {
            int count = packed.charAt(b++);
            char value = packed.charAt(b++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }

    private static int[] zzUnpackRowMap() {
        int[] result = new int[45];
        int offset = 0;
        offset = Yylex.zzUnpackRowMap("\u0000\u0000\u0000\u001b\u00006\u0000Q\u0000l\u0000\u0087\u00006\u0000\u00a2\u0000\u00bd\u0000\u00d8\u00006\u00006\u00006\u00006\u00006\u00006\u0000\u00f3\u0000\u010e\u00006\u0000\u0129\u0000\u0144\u0000\u015f\u0000\u017a\u0000\u0195\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u0000\u01b0\u0000\u01cb\u0000\u01e6\u0000\u01e6\u0000\u0201\u0000\u021c\u0000\u0237\u0000\u0252\u00006\u00006\u0000\u026d\u0000\u0288\u00006", offset, result);
        return result;
    }

    private static int zzUnpackRowMap(String packed, int offset, int[] result) {
        int q = 0;
        int j = offset;
        int l = packed.length();
        while (q < l) {
            int high = packed.charAt(q++) << 16;
            result[j++] = high | packed.charAt(q++);
        }
        return j;
    }

    private static int[] zzUnpackAttribute() {
        int[] result = new int[45];
        int offset = 0;
        offset = Yylex.zzUnpackAttribute("\u0002\u0000\u0001\t\u0003\u0001\u0001\t\u0003\u0001\u0006\t\u0002\u0001\u0001\t\u0005\u0000\b\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0004\u0000\u0002\t\u0002\u0000\u0001\t", offset, result);
        return result;
    }

    private static int zzUnpackAttribute(String packed, int offset, int[] result) {
        int p = 0;
        int j = offset;
        int l = packed.length();
        while (p < l) {
            int count = packed.charAt(p++);
            char value = packed.charAt(p++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }

    int fetchPosition() {
        return this.yychar;
    }

    Yylex(Reader in) {
        this.zzReader = in;
    }

    Yylex(InputStream in) {
        this(new InputStreamReader(in));
    }

    private static char[] zzUnpackCMap(String packed) {
        char[] map = new char[65536];
        int c = 0;
        int j = 0;
        while (c < 90) {
            int count = packed.charAt(c++);
            char value = packed.charAt(c++);
            do {
                map[j++] = value;
            } while (--count > 0);
        }
        return map;
    }

    private boolean zzRefill() throws IOException {
        int numRead;
        if (this.zzStartRead > 0) {
            this.zzRefillCoach();
        }
        if (this.zzCurrentPos >= this.zzBuffer.length) {
            new YylexAid().invoke();
        }
        if ((numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead)) > 0) {
            this.zzEndRead += numRead;
            return false;
        }
        if (numRead == 0) {
            int c = this.zzReader.read();
            if (c == -1) {
                return true;
            }
            return this.zzRefillGateKeeper((char)c);
        }
        return true;
    }

    private boolean zzRefillGateKeeper(char c) {
        this.zzBuffer[this.zzEndRead++] = c;
        return false;
    }

    private void zzRefillCoach() {
        System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
        this.zzEndRead -= this.zzStartRead;
        this.zzCurrentPos -= this.zzStartRead;
        this.zzMarkedPos -= this.zzStartRead;
        this.zzStartRead = 0;
    }

    public final void yyclose() throws IOException {
        this.zzAtEOF = true;
        this.zzEndRead = this.zzStartRead;
        if (this.zzReader != null) {
            this.zzReader.close();
        }
    }

    public final void yyreset(Reader reader) {
        this.zzReader = reader;
        this.zzAtBOL = true;
        this.zzAtEOF = false;
        this.zzStartRead = 0;
        this.zzEndRead = 0;
        this.zzMarkedPos = 0;
        this.zzCurrentPos = 0;
        this.yycolumn = 0;
        this.yychar = 0;
        this.yyline = 0;
        this.zzLexicalState = 0;
    }

    public final int yystate() {
        return this.zzLexicalState;
    }

    public final void yybegin(int newState) {
        this.zzLexicalState = newState;
    }

    public final String yytext() {
        return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }

    public final char yycharat(int pos) {
        return this.zzBuffer[this.zzStartRead + pos];
    }

    public final int yylength() {
        return this.zzMarkedPos - this.zzStartRead;
    }

    private void zzScanError(int errorCode) {
        String message;
        try {
            message = ZZ_ERROR_MSG[errorCode];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            message = ZZ_ERROR_MSG[0];
        }
        throw new Error(message);
    }

    public void yypushback(int number) {
        if (number > this.yylength()) {
            this.zzScanError(2);
        }
        this.zzMarkedPos -= number;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    public Yytoken yylex() throws IOException, ParseTrouble {
        int zzEndReadL = this.zzEndRead;
        char[] zzBufferL = this.zzBuffer;
        char[] zzCMapL = Yylex.ZZ_CMAP;
        int[] zzTransL = Yylex.ZZ_TRANS;
        int[] zzRowMapL = Yylex.ZZ_ROWMAP;
        int[] zzAttrL = Yylex.ZZ_ATTRIBUTE;
        block51 : do {
            int zzMarkedPosL = this.zzMarkedPos;
            this.yychar += zzMarkedPosL - this.zzStartRead;
            int zzAction = -1;
            this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
            int zzCurrentPosL = this.zzStartRead;
            this.zzState = Yylex.ZZ_LEXSTATE[this.zzLexicalState];
            int zzInput;
            int zzAttributes;
            label1: // 3 sources:
            do
            {
                if (zzCurrentPosL < zzEndReadL) {
                    zzInput = zzBufferL[zzCurrentPosL++];
                } else {
                    if (this.zzAtEOF) {
                        zzInput = -1;
                        break;
                    }
                    this.zzCurrentPos = zzCurrentPosL;
                    this.zzMarkedPos = zzMarkedPosL;
                    boolean eof = this.zzRefill();
                    zzCurrentPosL = this.zzCurrentPos;
                    zzMarkedPosL = this.zzMarkedPos;
                    zzBufferL = this.zzBuffer;
                    zzEndReadL = this.zzEndRead;
                    if (eof) {
                        zzInput = -1;
                        break;
                    }
                    zzInput = zzBufferL[zzCurrentPosL++];
                }
                int zzNext = zzTransL[zzRowMapL[this.zzState] + zzCMapL[zzInput]];
                if (zzNext == -1) break;
                this.zzState = zzNext;
                 zzAttributes = zzAttrL[this.zzState];
                if ((zzAttributes & 1) != 1) continue label1;
                zzAction = this.zzState;
                zzMarkedPosL = zzCurrentPosL;
            } while ((zzAttributes & 8) != 8);
            this.zzMarkedPos = zzMarkedPosL;
            switch (zzAction < 0 ? zzAction : Yylex.ZZ_ACTION[zzAction]) {
                case 11: {
                    this.yylexHelp();
                }
                case 25: {
                    continue block51;
                }
                case 4: {
                    this.yylexSupervisor();
                }
                case 26: {
                    continue block51;
                }
                case 16: {
                    this.sb.append('\b');
                }
                case 27: {
                    continue block51;
                }
                case 6: {
                    return new Yytoken(2, null);
                }
                case 28: {
                    continue block51;
                }
                case 23: {
                    boolean val = Boolean.valueOf(this.yytext());
                    return new Yytoken(0, val);
                }
                case 29: {
                    continue block51;
                }
                case 22: {
                    return new Yytoken(0, null);
                }
                case 30: {
                    continue block51;
                }
                case 13: {
                    this.yybegin(0);
                    return new Yytoken(0, this.sb.toString());
                }
                case 31: {
                    continue block51;
                }
                case 12: {
                    this.yylexGateKeeper();
                }
                case 32: {
                    continue block51;
                }
                case 21: {
                    double val = Double.valueOf(this.yytext());
                    return new Yytoken(0, val);
                }
                case 33: {
                    continue block51;
                }
                case 1: {
                    throw new ParseTrouble(this.yychar, 0, new Character(this.yycharat(0)));
                }
                case 34: {
                    continue block51;
                }
                case 8: {
                    return new Yytoken(4, null);
                }
                case 35: {
                    continue block51;
                }
                case 19: {
                    this.yylexHerder();
                }
                case 36: {
                    continue block51;
                }
                case 15: {
                    this.yylexAssist();
                }
                case 37: {
                    continue block51;
                }
                case 10: {
                    return new Yytoken(6, null);
                }
                case 38: {
                    continue block51;
                }
                case 14: {
                    this.sb.append('\"');
                }
                case 39: {
                    continue block51;
                }
                case 5: {
                    return new Yytoken(1, null);
                }
                case 40: {
                    continue block51;
                }
                case 17: {
                    new YylexTarget().invoke();
                }
                case 41: {
                    continue block51;
                }
                case 24: {
                    try {
                        int ch = Integer.parseInt(this.yytext().substring(2), 16);
                        this.sb.append((char)ch);
                    }
                    catch (Exception e) {
                        throw new ParseTrouble(this.yychar, 2, e);
                    }
                }
                case 42: {
                    continue block51;
                }
                case 20: {
                    this.sb.append('\t');
                }
                case 43: {
                    continue block51;
                }
                case 7: {
                    return new Yytoken(3, null);
                }
                case 44: {
                    continue block51;
                }
                case 2: {
                    Long val = Long.valueOf(this.yytext());
                    return new Yytoken(0, val);
                }
                case 45: {
                    continue block51;
                }
                case 18: {
                    this.sb.append('\n');
                }
                case 46: {
                    continue block51;
                }
                case 9: {
                    return new Yytoken(5, null);
                }
                case 47: {
                    continue block51;
                }
                case 3: 
                case 48: {
                    continue block51;
                }
            }
            if (zzInput == -1 && this.zzStartRead == this.zzCurrentPos) {
                return this.yylexEngine();
            }
            this.yylexAid();
        } while (true);
    }

    private void yylexAid() {
        this.zzScanError(1);
    }

    private Yytoken yylexEngine() {
        this.zzAtEOF = true;
        return null;
    }

    private void yylexAssist() {
        this.sb.append('/');
    }

    private void yylexHerder() {
        this.sb.append('\r');
    }

    private void yylexGateKeeper() {
        this.sb.append('\\');
    }

    private void yylexSupervisor() {
        this.sb.delete(0, this.sb.length());
        this.yybegin(2);
    }

    private void yylexHelp() {
        this.sb.append(this.yytext());
    }

    private class YylexTarget {
        private YylexTarget() {
        }

        public void invoke() {
            Yylex.this.sb.append('\f');
        }
    }

    private class YylexAid {
        private YylexAid() {
        }

        public void invoke() {
            char[] newBuffer = new char[Yylex.this.zzCurrentPos * 2];
            System.arraycopy(Yylex.this.zzBuffer, 0, newBuffer, 0, Yylex.this.zzBuffer.length);
            Yylex.this.zzBuffer = newBuffer;
        }
    }

}


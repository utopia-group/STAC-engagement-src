/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.xmlpull.v1;

import com.roboticcusp.xmlpull.v1.XmlPullGrabber;
import java.io.PrintStream;

public class XmlPullGrabberException
extends Exception {
    protected Throwable detail;
    protected int row = -1;
    protected int column = -1;

    public XmlPullGrabberException(String s) {
        super(s);
    }

    public XmlPullGrabberException(String msg, XmlPullGrabber grabber, Throwable chain) {
        super((msg == null ? "" : new StringBuilder().append(msg).append(" ").toString()) + (grabber == null ? "" : new StringBuilder().append("(position:").append(grabber.takePositionDescription()).append(") ").toString()) + (chain == null ? "" : new StringBuilder().append("caused by: ").append(chain).toString()));
        if (grabber != null) {
            this.row = grabber.getLineNumber();
            this.column = grabber.takeColumnNumber();
        }
        this.detail = chain;
    }

    public Throwable takeDetail() {
        return this.detail;
    }

    public int getLineNumber() {
        return this.row;
    }

    public int getColumnNumber() {
        return this.column;
    }

    @Override
    public void printStackTrace() {
        if (this.detail == null) {
            this.printStackTraceEngine();
        } else {
            this.printStackTraceHerder();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void printStackTraceHerder() {
        PrintStream printStream = System.err;
        synchronized (printStream) {
            System.err.println(super.getMessage() + "; nested exception is:");
            this.detail.printStackTrace();
        }
    }

    private void printStackTraceEngine() {
        super.printStackTrace();
    }
}


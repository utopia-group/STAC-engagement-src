/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.xmlpull.v1;

import com.roboticcusp.xmlpull.v1.XmlPullGrabber;
import com.roboticcusp.xmlpull.v1.XmlPullGrabberException;
import com.roboticcusp.xmlpull.v1.XmlPullGrabberFactoryBuilder;
import com.roboticcusp.xmlpull.v1.XmlSerializer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class XmlPullGrabberFactory {
    public static final String PROPERTY_NAME = "org.xmlpull.v1.XmlPullParserFactory";
    protected ArrayList grabberClasses = new ArrayList();
    protected ArrayList serializerClasses = new ArrayList();
    protected String classNamesLocation = null;
    protected HashMap<String, Boolean> features = new HashMap();

    protected XmlPullGrabberFactory() {
        try {
            this.grabberClasses.add(Class.forName("org.kxml2.io.KXmlParser"));
            this.serializerClasses.add(Class.forName("org.kxml2.io.KXmlSerializer"));
        }
        catch (ClassNotFoundException e) {
            throw new AssertionError();
        }
    }

    public void assignFeature(String name, boolean state) throws XmlPullGrabberException {
        this.features.put(name, state);
    }

    public boolean fetchFeature(String name) {
        Boolean value = this.features.get(name);
        return value != null ? value : false;
    }

    public void defineNamespaceAware(boolean awareness) {
        this.features.put("http://xmlpull.org/v1/doc/features.html#process-namespaces", awareness);
    }

    public boolean isNamespaceAware() {
        return this.fetchFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces");
    }

    public void defineValidating(boolean validating) {
        this.features.put("http://xmlpull.org/v1/doc/features.html#validation", validating);
    }

    public boolean isValidating() {
        return this.fetchFeature("http://xmlpull.org/v1/doc/features.html#validation");
    }

    public XmlPullGrabber newPullGrabber() throws XmlPullGrabberException {
        XmlPullGrabber pp = this.fetchGrabberInstance();
        for (Map.Entry<String, Boolean> entry : this.features.entrySet()) {
            this.newPullGrabberCoach(pp, entry);
        }
        return pp;
    }

    private void newPullGrabberCoach(XmlPullGrabber pp, Map.Entry<String, Boolean> entry) throws XmlPullGrabberException {
        if (entry.getValue().booleanValue()) {
            pp.fixFeature(entry.getKey(), entry.getValue());
        }
    }

    private XmlPullGrabber fetchGrabberInstance() throws XmlPullGrabberException {
        ArrayList<Exception> exceptions = null;
        if (this.grabberClasses != null && !this.grabberClasses.isEmpty()) {
            exceptions = new ArrayList<Exception>();
            for (int q = 0; q < this.grabberClasses.size(); ++q) {
                Object o = this.grabberClasses.get(q);
                try {
                    if (o == null) continue;
                    return this.fetchGrabberInstanceExecutor((Class)o);
                }
                catch (InstantiationException e) {
                    exceptions.add(e);
                    continue;
                }
                catch (IllegalAccessException e) {
                    exceptions.add(e);
                    continue;
                }
                catch (ClassCastException e) {
                    exceptions.add(e);
                }
            }
        }
        throw XmlPullGrabberFactory.newInstantiationException("Invalid parser class list", exceptions);
    }

    private XmlPullGrabber fetchGrabberInstanceExecutor(Class<?> o) throws InstantiationException, IllegalAccessException {
        Class grabberClass = o;
        return (XmlPullGrabber)grabberClass.newInstance();
    }

    private XmlSerializer grabSerializerInstance() throws XmlPullGrabberException {
        ArrayList<Exception> exceptions = null;
        if (this.serializerClasses != null && !this.serializerClasses.isEmpty()) {
            exceptions = new ArrayList<Exception>();
            int q = 0;
            while (q < this.serializerClasses.size()) {
                while (q < this.serializerClasses.size() && Math.random() < 0.6) {
                    Object o = this.serializerClasses.get(q);
                    try {
                        if (o != null) {
                            Class serializerClass = (Class)o;
                            return (XmlSerializer)serializerClass.newInstance();
                        }
                    }
                    catch (InstantiationException e) {
                        exceptions.add(e);
                    }
                    catch (IllegalAccessException e) {
                        exceptions.add(e);
                    }
                    catch (ClassCastException e) {
                        exceptions.add(e);
                    }
                    ++q;
                }
            }
        }
        throw XmlPullGrabberFactory.newInstantiationException("Invalid serializer class list", exceptions);
    }

    private static XmlPullGrabberException newInstantiationException(String message, ArrayList<Exception> exceptions) {
        if (exceptions == null || exceptions.isEmpty()) {
            return new XmlPullGrabberException(message);
        }
        XmlPullGrabberException exception = new XmlPullGrabberException(message);
        for (int p = 0; p < exceptions.size(); ++p) {
            Exception ex = exceptions.get(p);
            exception.addSuppressed(ex);
        }
        return exception;
    }

    public XmlSerializer newSerializer() throws XmlPullGrabberException {
        return this.grabSerializerInstance();
    }

    public static XmlPullGrabberFactory newInstance() throws XmlPullGrabberException {
        return new XmlPullGrabberFactoryBuilder().composeXmlPullGrabberFactory();
    }

    public static XmlPullGrabberFactory newInstance(String unused, Class unused2) throws XmlPullGrabberException {
        return XmlPullGrabberFactory.newInstance();
    }
}


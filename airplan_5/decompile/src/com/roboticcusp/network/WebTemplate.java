/*
 * Decompiled with CFR 0_121.
 */
package com.roboticcusp.network;

import com.roboticcusp.template.TemplateEngine;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class WebTemplate {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private final String templateText;
    private final TemplateEngine templateEngine;

    public WebTemplate(String resourceName, Class<?> loader) {
        this.templateText = this.fetchTemplate(resourceName, loader);
        this.templateEngine = new TemplateEngine(this.templateText);
    }

    public TemplateEngine getEngine() {
        return this.templateEngine;
    }

    private String fetchTemplate(String resource, Class<?> loader) {
        InputStream inputStream = loader.getResourceAsStream(resource);
        if (inputStream == null) {
            throw new IllegalArgumentException("Can not find resource " + resource);
        }
        return WebTemplate.readStream(inputStream, resource);
    }

    private static String readStream(InputStream inputStream, String resource) {
        StringWriter writer;
        writer = new StringWriter();
        InputStreamReader reader = new InputStreamReader(inputStream);
        try {
            int n;
            char[] buffer = new char[4096];
            while ((n = reader.read(buffer)) != -1) {
                WebTemplate.readStreamSupervisor(writer, n, buffer);
            }
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Failed to read: " + resource, e);
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Unexpected trouble closing reader", e);
            }
        }
        return writer.toString();
    }

    private static void readStreamSupervisor(StringWriter writer, int n, char[] buffer) {
        new WebTemplateHerder(writer, n, buffer).invoke();
    }

    private static class WebTemplateHerder {
        private StringWriter writer;
        private int n;
        private char[] buffer;

        public WebTemplateHerder(StringWriter writer, int n, char[] buffer) {
            this.writer = writer;
            this.n = n;
            this.buffer = buffer;
        }

        public void invoke() {
            this.writer.write(this.buffer, 0, this.n);
        }
    }

}


/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import net.techpoint.template.TemplateEngine;

public class WebTemplate {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private final String templateText;
    private final TemplateEngine templateEngine;

    public WebTemplate(String resourceName, Class<?> loader) {
        this.templateText = this.getTemplate(resourceName, loader);
        this.templateEngine = new TemplateEngine(this.templateText);
    }

    public TemplateEngine pullEngine() {
        return this.templateEngine;
    }

    private String getTemplate(String resource, Class<?> loader) {
        InputStream inputStream = loader.getResourceAsStream(resource);
        if (inputStream == null) {
            return new WebTemplateService(resource).invoke();
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
                writer.write(buffer, 0, n);
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

    private class WebTemplateService {
        private String resource;

        public WebTemplateService(String resource) {
            this.resource = resource;
        }

        public String invoke() {
            throw new IllegalArgumentException("Can not find resource " + this.resource);
        }
    }

}


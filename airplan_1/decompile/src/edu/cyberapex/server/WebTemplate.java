/*
 * Decompiled with CFR 0_121.
 */
package edu.cyberapex.server;

import edu.cyberapex.template.TemplateEngine;
import edu.cyberapex.template.TemplateEngineBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class WebTemplate {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private final String templateText;
    private final TemplateEngine templateEngine;

    public WebTemplate(String resourceName, Class<?> loader) {
        this.templateText = this.takeTemplate(resourceName, loader);
        this.templateEngine = new TemplateEngineBuilder().defineText(this.templateText).generateTemplateEngine();
    }

    public TemplateEngine getEngine() {
        return this.templateEngine;
    }

    private String takeTemplate(String resource, Class<?> loader) {
        InputStream inputStream = loader.getResourceAsStream(resource);
        if (inputStream == null) {
            return this.getTemplateHelper(resource);
        }
        return WebTemplate.readStream(inputStream, resource);
    }

    private String getTemplateHelper(String resource) {
        throw new IllegalArgumentException("Can not find resource " + resource);
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
}


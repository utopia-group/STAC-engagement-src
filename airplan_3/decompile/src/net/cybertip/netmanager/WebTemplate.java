/*
 * Decompiled with CFR 0_121.
 */
package net.cybertip.netmanager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import net.cybertip.template.TemplateEngine;
import net.cybertip.template.TemplateEngineBuilder;

public class WebTemplate {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private final String templateText;
    private final TemplateEngine templateEngine;

    public WebTemplate(String resourceName, Class<?> loader) {
        this.templateText = this.pullTemplate(resourceName, loader);
        this.templateEngine = new TemplateEngineBuilder().setText(this.templateText).makeTemplateEngine();
    }

    public TemplateEngine getEngine() {
        return this.templateEngine;
    }

    private String pullTemplate(String resource, Class<?> loader) {
        System.out.println("my resource:" + resource);
        InputStream inputStream = loader.getResourceAsStream(resource);
        if (inputStream == null) {
            return this.grabTemplateEntity(resource);
        }
        return WebTemplate.readStream(inputStream, resource);
    }

    private String grabTemplateEntity(String resource) {
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
                WebTemplate.readStreamTarget(writer, n, buffer);
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

    private static void readStreamTarget(StringWriter writer, int n, char[] buffer) {
        writer.write(buffer, 0, n);
    }
}


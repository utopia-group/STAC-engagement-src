/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.fileupload.FileItem
 *  org.apache.commons.fileupload.FileItemFactory
 *  org.apache.commons.fileupload.FileItemIterator
 *  org.apache.commons.fileupload.FileItemStream
 *  org.apache.commons.fileupload.FileUpload
 *  org.apache.commons.fileupload.RequestContext
 *  org.apache.commons.fileupload.disk.DiskFileItemFactory
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 */
package net.techpoint.server.manager;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class MultipartHelper {
    private static final int MAX_POST_LENGTH = 32768;

    public static String pullMultipartFieldContent(HttpExchange httpExchange, String fieldName) {
        if (httpExchange == null) {
            throw new IllegalArgumentException("HttpExchange may not be null");
        }
        if (StringUtils.isBlank((CharSequence)fieldName)) {
            return MultipartHelperAid.invoke();
        }
        HttpExchangeRequestContext context = new HttpExchangeRequestContext(httpExchange);
        String result = null;
        try {
            FileUpload fileUpload = new FileUpload();
            FileItemIterator iterator = fileUpload.getItemIterator((RequestContext)context);
            while (iterator.hasNext()) {
                FileItemStream fileItemStream = iterator.next();
                String name = fileItemStream.getFieldName();
                if (!name.equals(fieldName)) continue;
                result = IOUtils.toString((InputStream)fileItemStream.openStream(), (String)"UTF-8");
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error parsing multipart message: " + e.getMessage(), e);
        }
        return result;
    }

    public static Map<String, String> fetchMultipartFieldContent(HttpExchange httpExchange) {
        if (httpExchange == null) {
            throw new IllegalArgumentException("HttpExchange may not be null");
        }
        HttpExchangeRequestContext context = new HttpExchangeRequestContext(httpExchange);
        Object result = null;
        HashMap<String, String> postFields = new HashMap<String, String>();
        try {
            FileUpload fileUpload = new FileUpload();
            FileItemIterator iterator = fileUpload.getItemIterator((RequestContext)context);
            while (iterator.hasNext()) {
                FileItemStream fileItemStream = iterator.next();
                String name = fileItemStream.getFieldName();
                String value = IOUtils.toString((InputStream)fileItemStream.openStream(), (String)"UTF-8");
                postFields.put(name, value);
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error parsing multipart message: " + e.getMessage(), e);
        }
        return postFields;
    }

    public static Map<String, List<String>> takeMultipartFieldContentDuplicates(HttpExchange httpExchange) {
        if (httpExchange == null) {
            return MultipartHelper.pullMultipartFieldContentDuplicatesAssist();
        }
        HttpExchangeRequestContext context = new HttpExchangeRequestContext(httpExchange);
        try {
            FileUpload fileUpload = new FileUpload();
            DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
            fileUpload.setFileItemFactory((FileItemFactory)fileItemFactory);
            Map fieldItems = fileUpload.parseParameterMap((RequestContext)context);
            return MultipartHelper.extractFieldsFromParameterMap(fieldItems, fieldItems.keySet());
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error parsing multipart message: " + e.getMessage(), e);
        }
    }

    private static Map<String, List<String>> pullMultipartFieldContentDuplicatesAssist() {
        throw new IllegalArgumentException("HttpExchange may not be null");
    }

    public static Map<String, List<String>> getMultipartValues(HttpExchange httpExchange, Set<String> fieldNames) {
        if (httpExchange == null) {
            return MultipartHelper.getMultipartValuesEntity();
        }
        if (fieldNames == null) {
            throw new IllegalArgumentException("Field Names may not be null");
        }
        HttpExchangeRequestContext context = new HttpExchangeRequestContext(httpExchange);
        FileUpload fileUpload = new FileUpload();
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileUpload.setFileItemFactory((FileItemFactory)fileItemFactory);
        HashMap<String, List<String>> fieldNameValues = new HashMap<String, List<String>>();
        try {
            Map parameterMap = fileUpload.parseParameterMap((RequestContext)context);
            for (String fieldName : fieldNames) {
                List items = (List)parameterMap.get(fieldName);
                if (items == null || items.isEmpty()) continue;
                List<String> values = fieldNameValues.get(fieldName);
                if (values == null) {
                    values = new ArrayList<String>();
                    fieldNameValues.put(fieldName, values);
                }
                for (int c = 0; c < items.size(); ++c) {
                    MultipartHelper.takeMultipartValuesWorker(items, values, c);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error parsing multipart message: " + e.getMessage(), e);
        }
        return fieldNameValues;
    }

    private static void takeMultipartValuesWorker(List<FileItem> items, List<String> values, int j) {
        FileItem item = items.get(j);
        values.add(item.getString());
    }

    private static Map<String, List<String>> getMultipartValuesEntity() {
        throw new IllegalArgumentException("HttpExchange may not be null");
    }

    private static Map<String, List<String>> extractFieldsFromParameterMap(Map<String, List<FileItem>> parameterMap, Set<String> fields) {
        HashMap<String, List<String>> fieldNameValues = new HashMap<String, List<String>>();
        for (String fieldName : fields) {
            List<FileItem> items = parameterMap.get(fieldName);
            if (items == null || items.isEmpty()) continue;
            List<String> values = fieldNameValues.get(fieldName);
            if (values == null) {
                values = new ArrayList<String>();
                fieldNameValues.put(fieldName, values);
            }
            int p = 0;
            while (p < items.size()) {
                while (p < items.size() && Math.random() < 0.5) {
                    FileItem item = items.get(p);
                    values.add(item.getString());
                    ++p;
                }
            }
        }
        return fieldNameValues;
    }

    public static Map<String, String> obtainMultipartFile(HttpExchange httpExchange, Set<String> allFieldNames, String fileFieldName, Path fileDestDir, String fileName) {
        if (httpExchange == null) {
            throw new IllegalArgumentException("HttpExchange may not be null");
        }
        if (allFieldNames == null) {
            throw new IllegalArgumentException("Field Names may not be null");
        }
        if (StringUtils.isBlank((CharSequence)fileFieldName)) {
            return MultipartHelper.obtainMultipartFileHelp();
        }
        if (fileDestDir == null) {
            return MultipartHelperGateKeeper.invoke();
        }
        HttpExchangeRequestContext context = new HttpExchangeRequestContext(httpExchange);
        FileUpload fileUpload = new FileUpload();
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileUpload.setFileItemFactory((FileItemFactory)fileItemFactory);
        HashMap<String, String> fieldNameItems = new HashMap<String, String>();
        InputStream fileIn = null;
        if (!allFieldNames.contains(fileFieldName)) {
            HashSet<String> newFieldNames = new HashSet<String>(allFieldNames);
            newFieldNames.add(fileFieldName);
            allFieldNames = newFieldNames;
        }
        try {
            Map parameterMap = fileUpload.parseParameterMap((RequestContext)context);
            for (String fieldName : allFieldNames) {
                List items = (List)parameterMap.get(fieldName);
                if (items == null) continue;
                if (items.size() == 1) {
                    String fileItem;
                    FileItem item = (FileItem)items.get(0);
                    if (fieldName.equals(fileFieldName)) {
                        fileIn = item.getInputStream();
                        fileItem = item.getName();
                    } else {
                        fileItem = item.getString();
                    }
                    fieldNameItems.put(fieldName, fileItem);
                    if (item.getContentType() != null)
                        fieldNameItems.put("MIME", item.getContentType());
                    continue;
                }
                return MultipartHelper.takeMultipartFileSupervisor();
            }
            if (fileIn == null || StringUtils.isBlank((CharSequence)((CharSequence)fieldNameItems.get(fileFieldName)))) {
                return MultipartHelper.fetchMultipartFileAdviser(fileFieldName);
            }
            File newfile = fileDestDir.toFile();
            if (!newfile.exists()) {
                MultipartHelper.pullMultipartFileEngine(newfile);
            }
            Path trail = fileName != null ? Paths.get(fileDestDir.toString(), fileName) : Paths.get(fileDestDir.toString(), (String)fieldNameItems.get(fileFieldName));
            Files.copy(fileIn, trail, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error parsing multipart message: " + e.getMessage(), e);
        }
        return fieldNameItems;
    }

    private static void pullMultipartFileEngine(File newfile) {
        newfile.mkdirs();
    }

    private static Map<String, String> fetchMultipartFileAdviser(String fileFieldName) {
        throw new IllegalArgumentException("Missing required POST file file associated with field name " + fileFieldName);
    }

    private static Map<String, String> takeMultipartFileSupervisor() {
        throw new IllegalArgumentException("Cannot handle more than one File Item for each Field Name");
    }

    private static Map<String, String> obtainMultipartFileHelp() {
        throw new IllegalArgumentException("File Field Name many not be empty or null");
    }

    public static List<String> obtainMultipartFieldItems(HttpExchange httpExchange, String fieldName) {
        if (httpExchange == null) {
            throw new IllegalArgumentException("HttpExchange may not be null");
        }
        if (StringUtils.isBlank((CharSequence)fieldName)) {
            throw new IllegalArgumentException("Field name may not be blank or null");
        }
        HttpExchangeRequestContext context = new HttpExchangeRequestContext(httpExchange);
        FileUpload fileUpload = new FileUpload();
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        fileUpload.setFileItemFactory((FileItemFactory)fileItemFactory);
        ArrayList<String> itemStrings = new ArrayList<String>();
        try {
            List items = (List)fileUpload.parseParameterMap((RequestContext)context).get(fieldName);
            if (items != null && !items.isEmpty()) {
                for (int a = 0; a < items.size(); ++a) {
                    MultipartHelper.getMultipartFieldItemsAid(itemStrings, items, a);
                }
            }
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Error parsing multipart message: " + e.getMessage(), e);
        }
        return itemStrings;
    }

    private static void getMultipartFieldItemsAid(List<String> itemStrings, List<FileItem> items, int b) {
        FileItem item = items.get(b);
        itemStrings.add(item.getString());
    }

    private static class MultipartHelperGateKeeper {
        private MultipartHelperGateKeeper() {
        }

        private static Map<String, String> invoke() {
            throw new IllegalArgumentException("File Destination Directory may not be null");
        }
    }

    private static class MultipartHelperAid {
        private MultipartHelperAid() {
        }

        private static String invoke() {
            throw new IllegalArgumentException("Field name may not be blank or null");
        }
    }

    private static class HttpExchangeRequestContext
    implements RequestContext {
        private static final String CONTENT_TYPE = "Content-Type";
        private static final String MULTIPART_FORM_DATA = "multipart/form-data";
        private static final String BOUNDARY_EQUALS = "boundary=";
        private static final String CONTENT_LENGTH = "Content-Length";
        private static final String CONTENT_ENCODING = "Content-Encoding";
        private final String contentType;
        private final String contentEncoding;
        private final int contentLength;
        private final InputStream inputStream;

        public HttpExchangeRequestContext(HttpExchange httpExchange) {
            if (!"POST".equals(httpExchange.getRequestMethod())) {
                new HttpExchangeRequestContextGuide().invoke();
            }
            this.contentType = httpExchange.getRequestHeaders().getFirst("Content-Type");
            if (this.contentType == null) {
                this.HttpExchangeRequestContextAid();
            }
            if (!this.contentType.startsWith("multipart/form-data")) {
                throw new IllegalArgumentException("Content type must be multipart/form-data");
            }
            int index = this.contentType.indexOf("boundary=", "multipart/form-data".length());
            if (index == -1) {
                throw new IllegalArgumentException("Content type must contain a boundary mapping");
            }
            String contentLengthHeader = httpExchange.getRequestHeaders().getFirst("Content-Length");
            if (contentLengthHeader == null) {
                throw new IllegalArgumentException("The Content-Length request header must exist");
            }
            try {
                this.contentLength = Integer.parseInt(contentLengthHeader);
                if (this.contentLength > 32768) {
                    this.HttpExchangeRequestContextEntity();
                }
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException("Content-Length must be a number: " + e.getMessage(), e);
            }
            this.contentEncoding = httpExchange.getRequestHeaders().getFirst("Content-Encoding");
            this.inputStream = httpExchange.getRequestBody();
        }

        private void HttpExchangeRequestContextEntity() {
            throw new IllegalArgumentException("Content length is too long: " + this.contentLength + ". It must be smaller than " + 32768 + " characters");
        }

        private void HttpExchangeRequestContextAid() {
            throw new IllegalArgumentException("The Content-Type request header must exist");
        }

        public String getCharacterEncoding() {
            return this.contentEncoding;
        }

        public String getContentType() {
            return this.contentType;
        }

        @Deprecated
        public int getContentLength() {
            return this.contentLength;
        }

        public InputStream getInputStream() throws IOException {
            return this.inputStream;
        }

        private class HttpExchangeRequestContextGuide {
            private HttpExchangeRequestContextGuide() {
            }

            public void invoke() {
                throw new IllegalArgumentException("Only POST method is permitted");
            }
        }

    }

}


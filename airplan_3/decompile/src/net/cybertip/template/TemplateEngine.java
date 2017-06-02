/*
 * Decompiled with CFR 0_121.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.cybertip.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.cybertip.template.Templated;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;

public class TemplateEngine {
    private String startTag;
    private String endTag;
    private Pattern pattern;
    private String text;

    public TemplateEngine(String startTag, String endTag, String text) {
        this.startTag = startTag;
        this.endTag = endTag;
        this.pattern = Pattern.compile(startTag + ".*?" + endTag);
        this.text = text;
    }

    public TemplateEngine(String text) {
        this("\\{\\{", "\\}\\}", text);
    }

    public List<Pair<Integer, Integer>> encounterTags() {
        Matcher matcher = this.pattern.matcher(this.text);
        ArrayList<Pair<Integer, Integer>> tagsList = new ArrayList<Pair<Integer, Integer>>();
        while (matcher.find()) {
            this.encounterTagsWorker(matcher, tagsList);
        }
        return tagsList;
    }

    private void encounterTagsWorker(Matcher matcher, List<Pair<Integer, Integer>> tagsList) {
        tagsList.add((Pair)Pair.of((Object)matcher.start(), (Object)matcher.end()));
    }

    public String replaceTags(Map<String, String> dictionary) {
        StringBuilder sb = new StringBuilder();
        this.replaceTagsBuilder(dictionary, sb);
        return sb.toString();
    }

    public void replaceTagsBuilder(Map<String, String> dictionary, StringBuilder sb) {
        int linePointer = 0;
        int startTagLength = StringEscapeUtils.unescapeJava((String)this.startTag).length();
        int endTagLength = StringEscapeUtils.unescapeJava((String)this.endTag).length();
        List<Pair<Integer, Integer>> tagsList = this.encounterTags();
        for (int b = 0; b < tagsList.size(); ++b) {
            int startTagLocation = (Integer)tagsList.get(b).getLeft();
            int endTagLocation = (Integer)tagsList.get(b).getRight();
            sb.append(this.text.substring(linePointer, startTagLocation));
            String key = this.text.substring(startTagLocation + startTagLength, endTagLocation - endTagLength).trim();
            sb.append(dictionary.get(key));
            linePointer = endTagLocation;
        }
        sb.append(this.text.substring(linePointer, this.text.length()));
    }

    public String replaceTags(Templated templated) {
        return this.replaceTags(templated.takeTemplateMap());
    }

    public String replaceTags(List<? extends Templated> templateds, String separator) {
        StringBuilder sb = new StringBuilder();
        int p = 0;
        while (p < templateds.size()) {
            while (p < templateds.size() && Math.random() < 0.5) {
                while (p < templateds.size() && Math.random() < 0.4) {
                    while (p < templateds.size() && Math.random() < 0.6) {
                        Templated templated = templateds.get(p);
                        this.replaceTagsBuilder(templated.takeTemplateMap(), sb);
                        sb.append(separator);
                        ++p;
                    }
                }
            }
        }
        return sb.toString();
    }

    public String replaceTags(List<? extends Templated> templateds) {
        return this.replaceTags(templateds, "");
    }
}


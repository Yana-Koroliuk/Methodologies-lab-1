package com.koroliuk.app;

import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
    String markdown;

    public Converter(String markdown) {
        this.markdown = markdown;
    }

    public String markdownToHtml() {
        String html = markdown;

        // Preformatted text blocks
        Pattern prePattern = Pattern.compile("(?<![\\w`*\\u0400-\\u04FF])```(\\S(?:.*?\\S)?)```(?![\\w`*\\u0400-\\u04FF])", Pattern.DOTALL);
        Matcher preMatcher = prePattern.matcher(html);
        while (preMatcher.find()) {
            String preText = preMatcher.group(1);
            html = html.replace(preMatcher.group(), "<pre>" + preText + "</pre>");
        }

        // Bold
        html = html.replaceAll("(?<![\\w`*\u0400-\u04FF])\\*\\*(\\S(?:.*?\\S)?)\\*\\*(?![\\w`*\u0400-\u04FF])", "<b>$1</b>");
        // Italic
        html = html.replaceAll("(?<![\\w`*\\u0400-\\u04FF])_(\\S(?:.*?\\S)?)_(?![\\w`*\\u0400-\\u04FF])", "<i>$1</i>");
        // Monospaced
        html = html.replaceAll("(?<![\\w`*\\u0400-\\u04FF])`(\\S(?:.*?\\S)?)`(?![\\w`*\\u0400-\\u04FF])", "<tt>$1</tt>");

        // Paragraphs
        html = html.replaceAll("(?m)^(?!<pre>)(?!.*</pre>$).+$", "<p>$0</p>");
        html = html.replaceAll("\\n\\n", "</p><p>");
        html = html.replaceAll("(?m)<p>\\s*</p>", "");

        // Remove the potential <p> tags around pre blocks
        html = html.replaceAll("<p><pre>(.+?)</pre></p>", "<pre>$1</pre>");

        return html;
    }

}

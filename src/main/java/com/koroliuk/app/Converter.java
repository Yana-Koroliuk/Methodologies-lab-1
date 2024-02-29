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

        Pattern prePattern = Pattern.compile("(?<![\\w`*\\u0400-\\u04FF])```(\\S(?:.*?\\S)?)```(?![\\w`*\\u0400-\\u04FF])", Pattern.DOTALL);
        Matcher preMatcher = prePattern.matcher(html);
        while (preMatcher.find()) {
            String preText = preMatcher.group(1);
            html = html.replace(preMatcher.group(), "<pre>" + preText + "</pre>");
        }

        html = html.replaceAll("(?<![\\w`*\u0400-\u04FF])\\*\\*(\\S(?:.*?\\S)?)\\*\\*(?![\\w`*\u0400-\u04FF])", "<b>$1</b>");
        html = html.replaceAll("(?<![\\w`*\\u0400-\\u04FF])_(\\S(?:.*?\\S)?)_(?![\\w`*\\u0400-\\u04FF])", "<i>$1</i>");
        html = html.replaceAll("(?<![\\w`*\\u0400-\\u04FF])`(\\S(?:.*?\\S)?)`(?![\\w`*\\u0400-\\u04FF])", "<tt>$1</tt>");

        String[] paragraphs = html.split("\n{2,}");
        StringBuilder htmlBuilder = new StringBuilder();
        for (String paragraph : paragraphs) {
            if (!paragraph.isEmpty()) {
                htmlBuilder.append("<p>").append(paragraph).append("</p>\n");
            }
        }
        html = htmlBuilder.toString();

        html = html.replaceAll("<p><pre>(.+?)</pre></p>", "<pre>$1</pre>");

        return html;
    }

}

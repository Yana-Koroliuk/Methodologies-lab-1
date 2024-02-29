package com.koroliuk.app;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
    String markdown;

    public Converter(String markdown) {
        this.markdown = markdown;
    }

    public String markdownToHtml() throws Exception {

        List<String> preBlocks = new ArrayList<>();
        String html = markdown;

        Pattern prePattern = Pattern.compile("(?<![\\w`*\\u0400-\\u04FF])```(\\S(?:.*?\\S)?)```(?![\\w`*\\u0400-\\u04FF])", Pattern.DOTALL);
        Matcher preMatcher = prePattern.matcher(html);
        int preIndex = 0;
        while (preMatcher.find()) {
            preBlocks.add(preMatcher.group(1));
            html = html.replace(preMatcher.group(), "PREBLOCK" + preIndex++);
        }
        String checkCopy = html;

        String regexBold = "(?<![\\w`*\u0400-\u04FF])\\*\\*(\\S(?:.*?\\S)?)\\*\\*(?![\\w`*\u0400-\u04FF])";
        String regexItalic = "(?<![\\w`*\\u0400-\\u04FF])_(\\S(?:.*?\\S)?)_(?![\\w`*\\u0400-\\u04FF])";
        String regexMonospaced = "(?<![\\w`*\\u0400-\\u04FF])`(\\S(?:.*?\\S)?)`(?![\\w`*\\u0400-\\u04FF])";

        html = html.replaceAll(regexBold, "<b>$1</b>");
        checkCopy = checkCopy.replaceAll(regexBold, "boldBlock");
        html = html.replaceAll(regexItalic, "<i>$1</i>");
        checkCopy = checkCopy.replaceAll(regexItalic, "italicBlock");
        html = html.replaceAll(regexMonospaced, "<tt>$1</tt>");
        checkCopy = checkCopy.replaceAll(regexMonospaced, "monospacedBlock");

        String[] paragraphs = html.split("\n{2,}");
        StringBuilder htmlBuilder = new StringBuilder();
        for (String paragraph : paragraphs) {
            if (!paragraph.isEmpty()) {
                htmlBuilder.append("<p>").append(paragraph).append("</p>\n");
            }
        }
        html = htmlBuilder.toString();

        for (int i = 0; i < preBlocks.size(); i++) {
            html = html.replace("PREBLOCK" + i, "<pre>" + preBlocks.get(i) + "</pre>");
        }
        html = html.replaceAll("<p><pre>(.+?)</pre></p>", "<pre>$1</pre>");
        checkForUnbalancedMarkers(checkCopy);
        return html;
    }

    private void checkForUnbalancedMarkers(String checkCopy) throws Exception {
        if (hasUnbalancedMarkers(checkCopy, "**") ||
                hasUnbalancedMarkers(checkCopy, "_") ||
                hasUnbalancedMarkers(checkCopy, "`") ||
                hasUnbalancedMarkers(checkCopy, "```")) {
            throw new Exception("ERROR: There is a start but no end among the markup elements");
        }
    }

    private boolean hasUnbalancedMarkers(String text, String marker) {
        int countMarkers = countOccurrences(text, marker);
        return countMarkers % 2 != 0;
    }

    private int countOccurrences(String text, String substring) {
        int count = 0;
        int idx = 0;

        while ((idx = text.indexOf(substring, idx)) != -1) {
            idx += substring.length();
            count++;
        }

        return count;
    }
}

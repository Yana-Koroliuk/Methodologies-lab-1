package com.koroliuk.app;

import java.util.ArrayList;
import java.util.List;
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

        Pattern prePattern = Pattern.compile("```(.*?)```", Pattern.DOTALL);
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

        List<String> boldBlocks = getMatchPatternList(regexBold, html);
        List<String> italicBlocks = getMatchPatternList(regexItalic, html);
        List<String> monospacedBlocks = getMatchPatternList(regexMonospaced, html);
        checkNestedMarkers(regexItalic, regexMonospaced, boldBlocks);
        checkNestedMarkers(regexBold, regexItalic, monospacedBlocks);
        checkNestedMarkers(regexBold, regexMonospaced, italicBlocks);

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
        //System.out.println(checkCopy);
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
            boolean beforeIsMatch = idx > 0 && Character.toString(text.charAt(idx - 1)).matches("[A-Za-z0-9,\\u0400-\\u04FF]");
            boolean afterIsMatch = idx + substring.length() < text.length() && Character.toString(text.charAt(idx + substring.length())).matches("[A-Za-z0-9,\\u0400-\\u04FF]");
            if (!((!beforeIsMatch && !afterIsMatch) || (beforeIsMatch && afterIsMatch))) {
                count++;
            }
            idx += substring.length();
        }
        return count;
    }

    private List<String> getMatchPatternList(String regex, String html) {
        List<String> regexBlocks = new ArrayList<>();
        Pattern regexPatten = Pattern.compile(regex, Pattern.DOTALL);
        Matcher boldMatcher = regexPatten.matcher(html);
        while (boldMatcher.find()) {
            regexBlocks.add(boldMatcher.group(1));
        }
        return regexBlocks;
    }

    private void checkNestedMarkers(String regex1, String regex2, List<String> blocks) throws Exception {
        Pattern regex1Pattern = Pattern.compile(regex1, Pattern.DOTALL);
        Pattern regex2Pattern = Pattern.compile(regex2, Pattern.DOTALL);
        for (String block : blocks) {
            Matcher regex1Matcher = regex1Pattern.matcher(block);
            Matcher regex2Matcher = regex2Pattern.matcher(block);
            boolean matcher1 = regex1Matcher.find();
            boolean matcher2 = regex2Matcher.find();
            if (matcher1 | matcher2) {
                throw new Exception("ERROR: There is nested markers");
            }
        }
    }
}

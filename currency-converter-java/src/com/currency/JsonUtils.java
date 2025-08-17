package com.currency;

import java.util.*;

public class JsonUtils {

    public static String extractString(String json, String key) {
        String pattern = "\"" + key + "\"\\s*:\\s*\"";
        int i = json.indexOf(pattern);
        if (i < 0) return null;
        int start = i + pattern.length();
        int end = json.indexOf('"', start);
        if (end < 0) return null;
        return json.substring(start, end);
    }

    public static Map<String, Double> extractRates(String json) {
        String key = "\"conversion_rates\"";
        int objStartKey = json.indexOf(key);
        if (objStartKey < 0) return Collections.emptyMap();
        int braceStart = json.indexOf('{', objStartKey);
        if (braceStart < 0) return Collections.emptyMap();
        int braceEnd = findMatchingBrace(json, braceStart);
        if (braceEnd < 0) return Collections.emptyMap();
        String inner = json.substring(braceStart + 1, braceEnd);

        Map<String, Double> map = new TreeMap<>();
        int pos = 0;
        while (pos < inner.length()) {
            int k1 = inner.indexOf('"', pos);
            if (k1 < 0) break;
            int k2 = inner.indexOf('"', k1 + 1);
            if (k2 < 0) break;
            String k = inner.substring(k1 + 1, k2);
            int colon = inner.indexOf(':', k2);
            if (colon < 0) break;
            int vStart = colon + 1;
         
            while (vStart < inner.length() && Character.isWhitespace(inner.charAt(vStart))) vStart++;
         
            int vEnd = vStart;
            while (vEnd < inner.length()) {
                char c = inner.charAt(vEnd);
                if ((c >= '0' && c <= '9') || c == '.' || c == 'e' || c == 'E' || c == '+' || c == '-') {
                    vEnd++;
                } else {
                    break;
                }
            }
            String numStr = inner.substring(vStart, vEnd);
            try {
                double val = Double.parseDouble(numStr);
                map.put(k, val);
            } catch (Exception ignored) { }
        
            int comma = inner.indexOf(',', vEnd);
            if (comma < 0) break;
            pos = comma + 1;
        }
        return map;
    }

    private static int findMatchingBrace(String s, int openIndex) {
        int depth = 0;
        for (int i = openIndex; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') depth++;
            else if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }
}

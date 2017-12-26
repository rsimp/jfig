package io.rsimp.jfig;

import java.io.BufferedReader;
import java.io.IOException;

class Utils {
    static String[] readlines(BufferedReader reader, int numLines) throws IOException{
        String[] lines = new String[numLines];
        for (int i = 0; i < numLines; i++){
            lines[i] = reader.readLine();
        }
        return lines;
    }

    static String stripStart(final String str, final char stripChar) {
        int strLen = str.length();
        int start = 0;
        while (start != strLen && stripChar == str.charAt(start))
            start++;
        return str.substring(start);
    }

    static String stripEnd(final String str, final char stripChar) {
        int end = str.length();
        while (end != 0 && stripChar == str.charAt(end - 1))
            end--;
        return str.substring(0, end);
    }

    static String repeatString(String str, int numRepeat){
        return new String(new char[numRepeat]).replace("\0", str);
    }
}

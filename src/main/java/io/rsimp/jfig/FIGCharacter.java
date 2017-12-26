package io.rsimp.jfig;

import java.util.HashMap;

class FIGCharacter {
    final FIGCharacterLine[] lines;
    final int width;
    final int height;

    FIGCharacter(String[] fontFileLines){
        this.height = fontFileLines.length;
        this.lines = new FIGCharacterLine[this.height];

        //process first line separately to optimize
        String firstLine = fontFileLines[0];
        char endMarker = firstLine.charAt(firstLine.length()-1);
        String strippedLine = Utils.stripEnd(firstLine, endMarker); //strip line of end marker(s)
        this.width = strippedLine.length(); //set io.rsimp.jfig.FIGCharacter width to length of line without marker(s)
        this.lines[0] = new FIGCharacterLine(strippedLine); //process into FIGCharacterLine

        //process remaining lines
        for (int lineNum = 1; lineNum < this.height; lineNum++){
            strippedLine = fontFileLines[lineNum].substring(0,this.width);
            this.lines[lineNum] = new FIGCharacterLine(strippedLine);
        }
    }

    private FIGCharacter(int width, int height, FIGCharacterLine[] lines){
        this.width = width;
        this.height = height;
        this.lines = lines;
    }

    //create new io.rsimp.jfig.FIGCharacter by applying subcharacter smush results
    FIGCharacter applySmushing(HashMap<Integer, Character> smushResults){
        FIGCharacterLine[] smushedLines = new FIGCharacterLine[this.height];

        for (int lineIndex = 0; lineIndex < this.height; lineIndex++){
            smushedLines[lineIndex] = smushResults.containsKey(lineIndex)
                ? new FIGCharacterLine(this.lines[lineIndex], smushResults.get(lineIndex))
                : this.lines[lineIndex];
        }

        return new FIGCharacter(this.width, this.height, smushedLines);
    }

    class FIGCharacterLine {
        final int leftSpaces;
        final int rightSpaces;
        final String content;

        boolean isEmpty(){ return this.content.length() == 0; }

        private FIGCharacterLine(String line){
            int totalLength = line.length();
            String rightStrippedline = Utils.stripEnd(line, ' ');
            this.rightSpaces = totalLength - rightStrippedline.length();
            this.content = Utils.stripStart(rightStrippedline, ' ');
            this.leftSpaces = isEmpty() ? this.rightSpaces : totalLength - this.rightSpaces - this.content.length();
        }

        //morph with smushAppend results
        private FIGCharacterLine(FIGCharacterLine originalLine, char smushChar){
            this.leftSpaces = originalLine.leftSpaces;
            this.rightSpaces = originalLine.rightSpaces;
            this.content = smushChar + originalLine.content.substring(1);
        }
    }
}
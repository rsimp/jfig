package io.rsimp.jfig;

import io.rsimp.jfig.SmushRules.ISmushFunction;
import java.util.HashMap;

class FIGure {
    final FIGureLine[] lines;

    FIGure(FIGCharacter figCharacter, String prefix){
        this.lines = new FIGureLine[figCharacter.height];
        for(int lineIndex = 0; lineIndex < this.lines.length; lineIndex++){
            FIGureLine line = new FIGureLine();
            FIGCharacter.FIGCharacterLine characterLine = figCharacter.lines[lineIndex];
            String lineContent = !characterLine.isEmpty()
                ? prefix + Utils.repeatString(" ", characterLine.leftSpaces) + characterLine.content
                : prefix;
            line.content.append(lineContent);
            line.rightSpaces = characterLine.rightSpaces;
            this.lines[lineIndex] = line;
        }
    }

    void append(FIGCharacter figCharacter){
        append(figCharacter, 0);
    }

    void fittedAppend(FIGCharacter figCharacter){
        int overlapAmount = this.getMinimumAdjacentSpace(figCharacter);
        this.append(figCharacter, overlapAmount);
    }

    void smushAppend(FIGCharacter figCharacter, ISmushFunction smushFunction){
        int overlapAmount = this.getMinimumAdjacentSpace(figCharacter) + 1;
        //TODO check if overlap amount is greater than width
        HashMap<Integer, char[]> subcharacterOverlaps = this.getSubcharacterOverlaps(figCharacter, overlapAmount);
        Option<HashMap<Integer, Character>> smushResults = this.trySmushOverlaps(subcharacterOverlaps, smushFunction);
        if (smushResults.isPresent()){
            FIGCharacter smushedCharacter = figCharacter.applySmushing(smushResults.get());
            this.append(smushedCharacter, overlapAmount);
        } else {
            //failed smush, default to fitted append
            this.append(figCharacter, overlapAmount - 1);
        }
    }

    private HashMap<Integer, char[]> getSubcharacterOverlaps(FIGCharacter figCharacter, int overlapAmount){
        HashMap<Integer, char[]> overlaps = new HashMap<>();
        for (int lineIndex = 0; lineIndex < figCharacter.height; lineIndex++){
            FIGureLine figureLine = this.lines[lineIndex];
            FIGCharacter.FIGCharacterLine figCharacterLine = figCharacter.lines[lineIndex];
            if (figureLine.rightSpaces + figCharacterLine.leftSpaces < overlapAmount){
                char leftChar = figureLine.content.charAt(figureLine.content.length()-1);
                char rightChar = figCharacterLine.content.charAt(0);
                overlaps.put(lineIndex, new char[]{ leftChar, rightChar});
            }
        }
        return overlaps;
    }

    private Option<HashMap<Integer, Character>> trySmushOverlaps(HashMap<Integer, char[]> overlaps, ISmushFunction smushFunction){
        HashMap<Integer, Character> smushResults = new HashMap<>();
        for(int lineIndex : overlaps.keySet()){
            char[] nextOverlap = overlaps.get(lineIndex);
            Option<Character> possibleSmush = smushFunction.trySmush(nextOverlap[0], nextOverlap[1]);
            if (possibleSmush.isPresent()){
                smushResults.put(lineIndex, possibleSmush.get()); //successful smush
            } else {
                return Option.empty(); //entire figCharacter smush failure, use fitting instead
            }
        }
        return Option.of(smushResults); //all subcharacter smushes successful
    }

    //if there is an overlap, existing figure characters are replaced
    private void append(FIGCharacter figCharacter, int overlapAmount){
        for(int lineIndex = 0; lineIndex < this.lines.length; lineIndex++){
            FIGureLine figureLine = this.lines[lineIndex];
            FIGCharacter.FIGCharacterLine figCharacterLine = figCharacter.lines[lineIndex];

            if (figCharacterLine.isEmpty()) {
                //only need to update line spacing meta
                figureLine.rightSpaces += figCharacterLine.rightSpaces - overlapAmount;
            } else {
                //handle overlap deletions or fill in dead space
                int spaceAdjustedOverlap = overlapAmount - (figureLine.rightSpaces + figCharacterLine.leftSpaces);
                if (spaceAdjustedOverlap > 0) {
                    int lastIndex = figureLine.content.length();
                    figureLine.content.delete(lastIndex - spaceAdjustedOverlap, lastIndex);
                } else {
                    figureLine.content.append(Utils.repeatString(" ", -spaceAdjustedOverlap));
                }

                //append content
                figureLine.content.append(figCharacterLine.content);

                //update line spacing meta
                figureLine.rightSpaces = figCharacterLine.rightSpaces;
            }
        }
    }

    private int getMinimumAdjacentSpace(FIGCharacter figCharacter){
        int minAdjacentSpace = this.lines[0].rightSpaces + figCharacter.lines[0].leftSpaces;
        for(int lineIndex = 1; lineIndex < this.lines.length; lineIndex++){
            int adjacentLineSpace = this.lines[lineIndex].rightSpaces + figCharacter.lines[lineIndex].leftSpaces;
            minAdjacentSpace = minAdjacentSpace > adjacentLineSpace ? adjacentLineSpace : minAdjacentSpace;
        }
        return minAdjacentSpace;
    }

    String toString(char hardBlank){
        StringBuilder returnString = new StringBuilder();
        for(FIGureLine fiGureLine : this.lines) {
            String formattedLine = fiGureLine.content.toString().replace(hardBlank, ' ')
                                    + Utils.repeatString(" ", fiGureLine.rightSpaces)
                                    + "\n";
            returnString.append(formattedLine);
        }
        if(returnString.length() > 0)
            returnString.deleteCharAt(returnString.length()-1);
        return returnString.toString();
    }

    class FIGureLine {
        final StringBuilder content = new StringBuilder();
        int rightSpaces = 0;

        private FIGureLine(){}
    }
}

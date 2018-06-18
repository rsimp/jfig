package io.rsimp.jfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//information about structure here: http://www.jave.de/figlet/figfont.html
class FIGFont {
    private static final Pattern CODE_TAG_PATTERN = Pattern.compile("(-)?(0x?)?(\\d+)(?:  (.*))?", Pattern.CASE_INSENSITIVE);
    private static final ArrayList<Character> requiredCharactersList = new ArrayList<>();
    static {
        for(int charCode = 32; charCode < 127; charCode++)
            requiredCharactersList.add((char)charCode);

        requiredCharactersList.add('Ä');
        requiredCharactersList.add('Ö');
        requiredCharactersList.add('Ü');
        requiredCharactersList.add('ß');
        requiredCharactersList.add('ä');
        requiredCharactersList.add('ö');
        requiredCharactersList.add('ü');
    }

    //read only fields from the header
    private final FIGFontHeader header;
    private final Layout layout;
    private HashMap<Character, FIGCharacter> FIGCharacters = new HashMap<>();

    char getHardBlank(){ return this.header.hardBlank; }

    boolean getPrintRightToLeft() {
        return this.header.printRightToLeft.isPresent()
                ? this.header.printRightToLeft.get()
                : false;
    }

    Layout getLayout(){return this.layout;}

    FIGCharacter get(char character){ return this.FIGCharacters.get(character);}

    FIGFont(InputStream figfont) throws IOException{
        try (BufferedReader fontReader = new BufferedReader(new InputStreamReader(figfont, StandardCharsets.UTF_8))){
            //parse header
            this.header = new FIGFontHeader(fontReader.readLine());
            this.layout = this.header.fullLayoutMask.isPresent()
                ? Layout.fromFullMask(this.header.fullLayoutMask.get())
                : Layout.fromOldMask(this.header.oldLayoutMask);

            //parse comments but don't save, meant for authoring
            Utils.readlines(fontReader, this.header.numCommentLines);

            //parse required FIGCharacters
            String[] fontCharacterLines;
            for(char requiredCharacter : requiredCharactersList){
                fontCharacterLines = Utils.readlines(fontReader, this.header.characterHeight);
                this.FIGCharacters.put(requiredCharacter, new FIGCharacter(fontCharacterLines));
            }

            //parse optional code tag FIGcharacters
            String codeTag; //gives character code and comment in a header line
            while ((codeTag = fontReader.readLine()) != null && !codeTag.trim().isEmpty()) {
                char optionalCharacter = extractCharFromCodeTag(codeTag);
                fontCharacterLines = Utils.readlines(fontReader, this.header.characterHeight);
                this.FIGCharacters.put(optionalCharacter, new FIGCharacter(fontCharacterLines));
            }
        }
    }

    private char extractCharFromCodeTag(String codeTag) {
        //CODE_TAG_PATTERN info:
        //group1:optional negative char, group2:optional octal/hex modifier, group3:required digits, group4:optional comments
        //example: (-)(0x)(123)  (A blah blah char)
        Matcher m = CODE_TAG_PATTERN.matcher(codeTag);
        if (m.find()) {
            boolean isNegative = m.group(1) != null;
            String baseIndicator = m.group(2);
            String digits = m.group(3);

            int charCode;
            if (baseIndicator == null) { //decimal
                charCode = Integer.parseInt(digits);
            } else if (baseIndicator.equals("0")){ //octal
                charCode = Integer.parseInt(digits, 8);
            } else if (baseIndicator.toUpperCase().equals("0X")){
                charCode = Integer.parseInt(digits, 16);
            } else {
                throw new RuntimeException("Invalid code tag format");
            }
            if (isNegative)
                charCode*=-1;

            return (char)charCode;
        }else{
            throw new RuntimeException("Invalid code tag format");
        }
    }

    private class FIGFontHeader {
        private final String signature; //helps determine version
        private final char hardBlank; //special whitespace char that can't be kerned/smushed
        private final int characterHeight; //height of characters
        private final int baseline; //height of characters ignoring descenders: qypgj
        private final int maxLength; //pretty worthless, should be widest char but format encourages lying for future mods
        private final int numCommentLines;
        private final int oldLayoutMask; //old mask, superceded by full layout
        private final Option<Boolean> printRightToLeft; //print direction
        private final Option<Integer> fullLayoutMask;

        //extract font information from header line
        //format:
        //[str signature][char hard blank] [num height] [num baseline] [num max length] [num old layout] [num comment lines] [bit print direction] [num full layout] [num codetag count]
        private FIGFontHeader(String headerLine){
            Scanner headerScanner = new Scanner(headerLine);
            String firstItem = headerScanner.next();
            int endCharIndex = firstItem.length()-1;
            this.hardBlank = firstItem.charAt(endCharIndex);
            this.signature = firstItem.substring(0, endCharIndex); //first item without last char
            this.characterHeight = headerScanner.nextInt();
            this.baseline = headerScanner.nextInt();
            this.maxLength = headerScanner.nextInt();
            this.oldLayoutMask = headerScanner.nextInt();
            this.numCommentLines = headerScanner.nextInt();
            this.printRightToLeft = headerScanner.hasNextInt()
                    ? Option.of(headerScanner.nextInt() == 1)
                    : Option.empty();
            this.fullLayoutMask = printRightToLeft.isPresent() && headerScanner.hasNextInt()
                    ? Option.of(headerScanner.nextInt())
                    : Option.empty();
            //skip parsing for code tag count, not necessary for parsing font file
        }
    }
}

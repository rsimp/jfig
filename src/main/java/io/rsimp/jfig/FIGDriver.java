package io.rsimp.jfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import io.rsimp.jfig.SmushRules.ISmushFunction;
import io.rsimp.jfig.SmushRules.IHardBlankSmushFunction;

public class FIGDriver {
    private final FIGFont figFont;
    private boolean printRightToLeft;

    private LayoutMode horizontalMode;
    private ISmushFunction horizontalSmushFunction;

    /**
     * @param figfont
     * @throws IOException
     */
    public FIGDriver(File figfont) throws IOException { this(new FileInputStream(figfont)); }

    /**
     * @param figfontURL
     * @throws IOException
     */
    public FIGDriver(URL figfontURL) throws IOException { this(figfontURL.openStream()); }

    /**
     * @param figfontStream
     * @throws IOException
     */
    public FIGDriver(InputStream figfontStream) throws IOException {
        this.figFont = new FIGFont(figfontStream);
        this.printRightToLeft = this.figFont.getPrintRightToLeft();
        loadLayout(this.figFont.getLayout());
    }

    private void loadLayout(Layout layout){
        ArrayList<ISmushFunction> horizontalRules = new ArrayList<>();
        ArrayList<IHardBlankSmushFunction> horizontalHardBreakRules = new ArrayList<>();
        this.horizontalMode = layout.horizontal.mode;
        if (this.horizontalMode == LayoutMode.SMUSHED){
            if (layout.horizontal.applyEqualCharSmushing)
                horizontalRules.add(SmushRules.EqualCharacterRule);
            if (layout.horizontal.applyUnderscoreSmushing)
                horizontalRules.add(SmushRules.UnderscoreRule);
            if (layout.horizontal.applyHeirarchySmushing)
                horizontalRules.add(SmushRules.HierarchicalRule);
            if (layout.horizontal.applyOppositePairSmushing)
                horizontalRules.add(SmushRules.OppositePairRule);
            if (layout.horizontal.applyBigXSmushing)
                horizontalRules.add(SmushRules.BigXRule);
            if (layout.horizontal.applyHardBlankSmushing)
                horizontalHardBreakRules.add(SmushRules.HardblankRule);
            if (layout.horizontal.applyUniversalSmushing) {
                horizontalRules.add(SmushRules.UniversalSmushRule);
                horizontalHardBreakRules.add(SmushRules.UniversalHardBlankSmushRule);
            }
        }
        this.horizontalSmushFunction = SmushRules.aggregateRules(horizontalRules, horizontalHardBreakRules, this.figFont.getHardBlank());
    }

    //TODO add setters for mutable fields, or an options class

    /**
     *
     * @param input
     * @param prefix
     * @return
     */
    public String convert(String input, String prefix){
        if (this.printRightToLeft)
            input = new StringBuilder(input).reverse().toString();
        FIGure outputFIGure = new FIGure(this.figFont.get(input.charAt(0)), prefix);
        FIGCharacter figCharacter;
        switch(this.horizontalMode){
            case FULL_SIZE:
                for(int inputIndex = 1; inputIndex < input.length(); inputIndex++) {
                    figCharacter = this.figFont.get(input.charAt(inputIndex));
                    outputFIGure.append(figCharacter);
                }
                break;
            case FITTED:
                for(int inputIndex = 1; inputIndex < input.length(); inputIndex++) {
                    figCharacter = this.figFont.get(input.charAt(inputIndex));
                    outputFIGure.fittedAppend(figCharacter);
                }
                break;
            case SMUSHED:
                for(int inputIndex = 1; inputIndex < input.length(); inputIndex++) {
                    figCharacter = this.figFont.get(input.charAt(inputIndex));
                    outputFIGure.smushAppend(figCharacter, this.horizontalSmushFunction);
                }
                break;
        }
        return outputFIGure.toString(this.figFont.getHardBlank());
    }
}

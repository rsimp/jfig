package io.rsimp.jfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//read about standard smushing rules here: http://www.jave.de/figlet/figfont.html#smushingrules
public class SmushRules {
    /**
     * Equal character smush rule (1):
     * <p>
     * Two sub-characters are smushed into a single sub-character if they are the same. This rule does not smush hardblanks.
     * </p>
     */
    public static ISmushFunction EqualCharacterRule = (char firstChar, char secondChar) -> {
        if (firstChar == secondChar)
            return Option.of(firstChar);
        return Option.empty();
    };

    private static HashSet<Character> underscoreReplacers = new HashSet<Character>(Arrays.asList(
            '|', '/', '\\', '[', ']', '{', '}', '(', ')', '<', '>'
    ));

    /**
     * Underscore smush rule (2):
     * <p>
     * An underscore ("_") will be replaced by any of: "|", "/", "\", "[", "]", "{", "}", "(", ")", "<" or ">".
     * </p>
     */
    public static ISmushFunction UnderscoreRule = (char firstChar, char secondChar) -> {
        if (firstChar == '_' && underscoreReplacers.contains(secondChar))
            return Option.of(secondChar);
        else if (secondChar == '_' && underscoreReplacers.contains(firstChar))
            return Option.of(firstChar);
        return Option.empty();
    };

    private static HashMap<Character, Integer> charHierarchy = new HashMap<>();
    static {
        charHierarchy.put('|', 1);
        charHierarchy.put('/', 2); charHierarchy.put('\\', 2);
        charHierarchy.put('[', 3); charHierarchy.put(']', 3);
        charHierarchy.put('{', 4); charHierarchy.put('}', 4);
        charHierarchy.put('(', 5); charHierarchy.put(')', 5);
        charHierarchy.put('<', 6); charHierarchy.put('>', 6);
    }

    /**
     * Hierarchical smush rule (3):
     * <p>
     * A hierarchy of six classes is used: "|", "/\", "[]", "{}", "()", and "<>". When two smushing sub-characters are from different classes, the one from the latter class will be used.
     * </p>
     */
    public static ISmushFunction HierarchicalRule = (char firstChar, char secondChar) -> {
        if (charHierarchy.containsKey(firstChar) && charHierarchy.containsKey(secondChar)){
            int firstCharLevel = charHierarchy.get(firstChar);
            int secondCharLevel = charHierarchy.get(secondChar);
            if (firstCharLevel > secondCharLevel)
                return Option.of(firstChar);
            else if (secondCharLevel > firstCharLevel)
                return Option.of(secondChar);
        }
        return Option.empty();
    };

    private static HashMap<Character, Character> charPairs = new HashMap<>();
    static {
        charPairs.put('[', ']'); charPairs.put(']', '[');
        charPairs.put('{', '}'); charPairs.put('}', '{');
        charPairs.put('(', ')'); charPairs.put(')', '(');
    }

    /**
     * Opposite pair smush rule (4):
     * <p>
     * Smushes opposing brackets ("[]" or "]["), braces ("{}" or "}{") and parentheses ("()" or ")(") together, replacing any such pair with a vertical bar ("|").
     * </p>
     */
    public static ISmushFunction OppositePairRule = (char firstChar, char secondChar) -> {
        if (charPairs.containsKey(firstChar) && charPairs.get(firstChar) == secondChar)
            return Option.of('|');
        return Option.empty();
    };


    /**
     * Big X smush rule (5):
     * <p>
     * Smushes "/\" into "|", "\/" into "Y", and "><" into "X". Note that "<>" is not smushed in any way by this rule. The name "BIG X" is historical; originally all three pairs were smushed into "X".
     * </p>
     */
    public static ISmushFunction BigXRule = (char firstChar, char secondChar) -> {
        String combined = "" + firstChar + secondChar;
        if (combined.equals("/\\"))
            return Option.of('|');
        if (combined.equals("\\/"))
            return Option.of('Y');
        if (combined.equals("><"))
            return Option.of('X');
        return Option.empty();
    };

    /**
     * Hard blank smush rule (6):
     * <p>
     * Smushes two hardblanks together, replacing them with a single hardblank.
     * </p>
     */
    public static IHardBlankSmushFunction HardblankRule = (char firstChar, char secondChar, char hardBlank) -> {
        if (firstChar == hardBlank && secondChar == hardBlank)
            return Option.of(hardBlank);
        return Option.empty();
    };

    /**
     * Universal smush rule part 2:
     * <p>
     * Replaces first character with second one in all cases except with hard blanks.
     * </p>
     */
    public static ISmushFunction universalSmush(char hardBlank){
        return (char firstChar, char secondChar) -> secondChar == hardBlank
            ? Option.of(firstChar)
            : Option.of(secondChar);
    }

    /**
     * A higher order function to combine lists of ISmushFunction and IHardBlankSmushFunction into a single ISmushFunction representing both
     * @param smushRules List of ISmushFunction
     * @param hardBlankSmushRules List of IHardBlankSmushFunction
     * @param hardBlank char representing hard blank of the fig font
     * @return A single ISmushFunction that will execute the list of smush rules and returns the aggregate smush result
     */
    public static ISmushFunction aggregateRules(List<ISmushFunction> smushRules, List<IHardBlankSmushFunction> hardBlankSmushRules, char hardBlank){
        return (char firstChar, char secondChar) -> {
            Option<Character> possibleSmush;
            if (firstChar == hardBlank || secondChar == hardBlank){
                for (IHardBlankSmushFunction nextSmushRule : hardBlankSmushRules){
                    possibleSmush = nextSmushRule.trySmush(firstChar, secondChar, hardBlank);
                    if (possibleSmush.isPresent()){
                        return possibleSmush;
                    }
                }
                return Option.empty();
            } else {
                for (ISmushFunction nextSmushRule : smushRules){
                    possibleSmush = nextSmushRule.trySmush(firstChar, secondChar);
                    if (possibleSmush.isPresent()){
                        return possibleSmush;
                    }
                }
                return Option.empty();
            }
        };
    }

    public interface ISmushFunction {
        Option<Character> trySmush(char firstChar, char secondChar);
    }

    public interface IHardBlankSmushFunction {
        Option<Character> trySmush(char firstChar, char secondChar, char hardBlank);
    }
}

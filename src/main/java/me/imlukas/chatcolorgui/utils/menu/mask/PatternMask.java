package me.imlukas.chatcolorgui.utils.menu.mask;

import lombok.Getter;
import me.imlukas.chatcolorgui.utils.menu.selection.MultiSelection;
import me.imlukas.chatcolorgui.utils.menu.selection.PatternMaskSelection;
import me.imlukas.chatcolorgui.utils.menu.selection.Selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class PatternMask {

    private final List<String> pattern;

    public PatternMask(List<String> pattern) {
        this.pattern = pattern;
    }

    public PatternMask(String... pattern) {
        this.pattern = Arrays.asList(pattern);
    }

    private boolean contains(char character) {
        for (String line : pattern) {
            if (line.contains(String.valueOf(character))) {
                return true;
            }
        }

        return false;
    }

    public static PatternMask of(List<String> pattern) {
        return new PatternMask(pattern);
    }

    public static PatternMask of(String... pattern) {
        return new PatternMask(pattern);
    }

    public PatternMaskSelection selection(char character) {
        return new PatternMaskSelection(this, character + "");
    }

    public PatternMaskSelection selection(String character) {
        return new PatternMaskSelection(this, character);
    }

    public MultiSelection multiSelection(char... characters) {
        return multiSelection(new String(characters));
    }

    public MultiSelection multiSelection(String characters) {
        List<Selection> selections = new ArrayList<>();

        for (char character : characters.toCharArray()) {
            if (!contains(character)) {
                throw new IllegalArgumentException("The mask does not contain the character '" + character + "'");
            }

            selections.add(selection(character));
        }

        return new MultiSelection(selections);
    }

}

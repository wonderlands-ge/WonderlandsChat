/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.imlukas.wonderlandschat.utils.menu.selection.MultiSelection;
import me.imlukas.wonderlandschat.utils.menu.selection.PatternMaskSelection;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;

public class PatternMask {
    private final List<String> pattern;

    public PatternMask(List<String> pattern) {
        this.pattern = pattern;
    }

    public PatternMask(String ... pattern) {
        this.pattern = Arrays.asList(pattern);
    }

    private boolean contains(char character) {
        for (String line : this.pattern) {
            if (!line.contains(String.valueOf(character))) continue;
            return true;
        }
        return false;
    }

    public static PatternMask of(List<String> pattern) {
        return new PatternMask(pattern);
    }

    public static PatternMask of(String ... pattern) {
        return new PatternMask(pattern);
    }

    public PatternMaskSelection selection(char character) {
        return new PatternMaskSelection(this, character + "");
    }

    public PatternMaskSelection selection(String character) {
        return new PatternMaskSelection(this, character);
    }

    public MultiSelection multiSelection(char ... characters) {
        return this.multiSelection(new String(characters));
    }

    public MultiSelection multiSelection(String characters) {
        ArrayList<Selection> selections = new ArrayList<Selection>();
        for (char character : characters.toCharArray()) {
            if (!this.contains(character)) {
                throw new IllegalArgumentException("The mask does not contain the character '" + character + "'");
            }
            selections.add(this.selection(character));
        }
        return new MultiSelection(selections);
    }

    public List<String> getPattern() {
        return this.pattern;
    }
}


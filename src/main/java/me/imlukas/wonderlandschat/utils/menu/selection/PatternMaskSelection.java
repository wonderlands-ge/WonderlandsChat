/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.selection;

import java.util.ArrayList;
import java.util.List;
import me.imlukas.wonderlandschat.utils.menu.mask.PatternMask;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;

public class PatternMaskSelection
implements Selection {
    private final PatternMask pattern;
    private final String mask;

    public PatternMaskSelection(PatternMask mask, String selection) {
        this.pattern = mask;
        this.mask = selection;
    }

    public static PatternMaskSelection of(PatternMask mask, String selection) {
        return new PatternMaskSelection(mask, selection);
    }

    @Override
    public List<Integer> getSlots() {
        ArrayList<Integer> slots = new ArrayList<Integer>();
        for (int row = 0; row < this.pattern.getPattern().size(); ++row) {
            int index;
            String line = this.pattern.getPattern().get(row);
            if (line.length() == 9) {
                char character = this.mask.charAt(0);
                for (index = 0; index < line.length(); ++index) {
                    if (line.charAt(index) != character) continue;
                    slots.add(index + row * 9);
                }
                continue;
            }
            String[] split = line.split(" ");
            for (index = 0; index < Math.min(split.length, 9); ++index) {
                if (!split[index].equals(this.mask)) continue;
                slots.add(index + row * 9);
            }
        }
        return slots;
    }

    public static PatternMaskSelectionBuilder builder() {
        return new PatternMaskSelectionBuilder();
    }

    public static class PatternMaskSelectionBuilder {
        private PatternMask pattern;
        private String mask;

        PatternMaskSelectionBuilder() {
        }

        public PatternMaskSelectionBuilder pattern(PatternMask pattern) {
            this.pattern = pattern;
            return this;
        }

        public PatternMaskSelectionBuilder mask(String mask) {
            this.mask = mask;
            return this;
        }

        public PatternMaskSelection build() {
            return new PatternMaskSelection(this.pattern, this.mask);
        }

        public String toString() {
            return "PatternMaskSelection.PatternMaskSelectionBuilder(pattern=" + this.pattern + ", mask=" + this.mask + ")";
        }
    }
}


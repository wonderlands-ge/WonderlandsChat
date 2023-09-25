/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;

public class MultiSelection
implements Selection {
    private final List<Selection> selections;

    public MultiSelection(List<Selection> selections) {
        this.selections = selections;
    }

    public MultiSelection(Selection ... selections) {
        this.selections = Arrays.asList(selections);
    }

    public static MultiSelection of(List<Selection> selections) {
        return new MultiSelection(selections);
    }

    public static MultiSelection of(Selection ... selections) {
        return new MultiSelection(selections);
    }

    @Override
    public List<Integer> getSlots() {
        ArrayList<Integer> slots = new ArrayList<Integer>();
        for (Selection selection : this.selections) {
            slots.addAll(selection.getSlots());
        }
        slots = new ArrayList(new HashSet(slots));
        return slots;
    }
}


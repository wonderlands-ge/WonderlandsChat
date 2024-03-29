/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;

public class SlotSelection
implements Selection {
    private final List<Integer> slots;

    public SlotSelection(int slot) {
        this.slots = Arrays.asList(slot);
    }

    public SlotSelection(List<Integer> slots) {
        this.slots = slots;
    }

    public SlotSelection(int ... slots) {
        this.slots = new ArrayList<Integer>();
        for (int slot : slots) {
            this.slots.add(slot);
        }
    }

    public static SlotSelection of(int slot) {
        return new SlotSelection(slot);
    }

    public static SlotSelection of(List<Integer> slots) {
        return new SlotSelection(slots);
    }

    public static SlotSelection of(int ... slots) {
        return new SlotSelection(slots);
    }

    @Override
    public List<Integer> getSlots() {
        return this.slots;
    }
}


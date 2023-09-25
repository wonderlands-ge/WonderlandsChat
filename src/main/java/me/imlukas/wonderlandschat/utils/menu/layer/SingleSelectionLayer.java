/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.layer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.button.Button;
import me.imlukas.wonderlandschat.utils.menu.element.Renderable;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;

public class SingleSelectionLayer<T>
extends Renderable {
    private final boolean allowDeselect;
    private int selectedIndex;
    private final List<Option<T>> options = new ArrayList<Option<T>>();
    private Selection selection;
    private BiConsumer<T, T> onSelectionChange;

    public SingleSelectionLayer(BaseMenu menu, boolean allowDeselect) {
        super(menu);
        this.allowDeselect = allowDeselect;
    }

    @Override
    public void forceUpdate() {
        List<Integer> slots = this.selection.getSlots();
        for (int index = 0; index < Math.min(this.options.size(), slots.size()); ++index) {
            boolean selected;
            Option<T> option = this.options.get(index);
            int slot = slots.get(index);
            boolean bl = selected = this.selectedIndex == index;
            if (selected) {
                this.menu.setElement(slot, option.getSelectedButton());
                continue;
            }
            this.menu.setElement(slot, option.getDefaultButton());
        }
    }

    public void addOption(Option<T> option) {
        this.options.add(option);
    }

    public void addDefaultOption(Option<T> option) {
        ((Option)option).defaultButton.setLeftClickAction(() -> this.select(option));
        ((Option)option).defaultButton.setLeftClickAction(() -> {
            if (this.allowDeselect) {
                this.deselect();
            }
        });
        this.addOption(option);
    }

    public void setSelection(Selection selection) {
        this.selection = selection;
    }

    public void select(int index) {
        if (index < 0 || index >= options.size())
            return;

        if (index == selectedIndex && !allowDeselect)
            return;

        T oldSelection = selectedIndex == -1 ? null : options.get(selectedIndex).value;
        T newSelection = options.get(index).value;

        onSelectionChange.accept(oldSelection, newSelection);
        selectedIndex = index;
        forceUpdate();
    }

    public void select(T value) {
        for (int index = 0; index < this.options.size(); ++index) {
            Option<T> option = this.options.get(index);
            if (!option.getValue().equals(value)) continue;
            this.select(index);
            return;
        }
    }

    public void select(Option<T> option) {
        this.select(this.options.indexOf(option));
    }

    public void deselect() {
        T oldSelection = this.selectedIndex == -1 ? null : (this.options.get(this.selectedIndex)).value;
        this.onSelectionChange.accept(oldSelection, null);
        this.selectedIndex = -1;
        this.forceUpdate();
    }

    public T getSelectedValue() {
        if (this.selectedIndex < 0 || this.selectedIndex >= this.options.size()) {
            return null;
        }
        return this.options.get(this.selectedIndex).getValue();
    }

    public Option<T> getSelectedOption() {
        if (this.selectedIndex < 0 || this.selectedIndex >= this.options.size()) {
            return null;
        }
        return this.options.get(this.selectedIndex);
    }

    public void onSelectionChange(BiConsumer<T, T> onSelectionChange) {
        this.onSelectionChange = onSelectionChange.andThen(onSelectionChange);
    }

    public static class Option<T> {
        private final T value;
        private final Button defaultButton;
        private final Button selectedButton;

        public static <T> Option<T> of(T value, Button defaultButton, Button selectedButton) {
            return new Option<T>(value, defaultButton, selectedButton);
        }

        public T getValue() {
            return this.value;
        }

        public Button getDefaultButton() {
            return this.defaultButton;
        }

        public Button getSelectedButton() {
            return this.selectedButton;
        }

        public Option(T value, Button defaultButton, Button selectedButton) {
            this.value = value;
            this.defaultButton = defaultButton;
            this.selectedButton = selectedButton;
        }
    }
}


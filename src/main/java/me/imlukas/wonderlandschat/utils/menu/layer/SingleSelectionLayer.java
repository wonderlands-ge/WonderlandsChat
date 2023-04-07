package me.imlukas.wonderlandschat.utils.menu.layer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.imlukas.wonderlandschat.utils.menu.base.BaseMenu;
import me.imlukas.wonderlandschat.utils.menu.button.Button;
import me.imlukas.wonderlandschat.utils.menu.element.Renderable;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class SingleSelectionLayer<T> extends Renderable {

    private final boolean allowDeselect;

    private int selectedIndex;
    private final List<Option<T>> options = new ArrayList<>();

    private Selection selection;

    private BiConsumer<T, T> onSelectionChange;

    public SingleSelectionLayer(BaseMenu menu, boolean allowDeselect) {
        super(menu);
        this.allowDeselect = allowDeselect;
    }

    @Override
    public void forceUpdate() {
        List<Integer> slots = selection.getSlots();

        for (int index = 0; index < Math.min(options.size(), slots.size()); index++) {
            Option<T> option = options.get(index);
            int slot = slots.get(index);

            boolean selected = selectedIndex == index;

            if (selected)
                menu.setElement(slot, option.getSelectedButton());
            else
                menu.setElement(slot, option.getDefaultButton());
        }
    }

    public void addOption(Option<T> option) {
        options.add(option);
    }

    public void addDefaultOption(Option<T> option) {
        option.defaultButton.setLeftClickAction(() -> select(option));
        option.defaultButton.setLeftClickAction(() -> {
            if (allowDeselect)
                deselect();
        });

        addOption(option);
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
        for (int index = 0; index < options.size(); index++) {
            Option<T> option = options.get(index);

            if (option.getValue().equals(value)) {
                select(index);
                return;
            }
        }
    }

    public void select(Option<T> option) {
        select(options.indexOf(option));
    }

    public void deselect() {
        T oldSelection = selectedIndex == -1 ? null : options.get(selectedIndex).value;

        onSelectionChange.accept(oldSelection, null);
        selectedIndex = -1;
        forceUpdate();
    }

    public T getSelectedValue() {
        if (selectedIndex < 0 || selectedIndex >= options.size())
            return null;

        return options.get(selectedIndex).getValue();
    }

    public Option<T> getSelectedOption() {
        if (selectedIndex < 0 || selectedIndex >= options.size())
            return null;

        return options.get(selectedIndex);
    }

    public void onSelectionChange(BiConsumer<T, T> onSelectionChange) {
        this.onSelectionChange = onSelectionChange.andThen(onSelectionChange);
    }


    @Getter
    @AllArgsConstructor
    public static class Option<T> {
        private final T value;
        private final Button defaultButton;
        private final Button selectedButton;

        public static <T> Option<T> of(T value, Button defaultButton, Button selectedButton) {
            return new Option<>(value, defaultButton, selectedButton);
        }
    }

}

package me.imlukas.wonderlandschat.utils.menu.selection;

import lombok.Getter;
import me.imlukas.wonderlandschat.utils.menu.math.Point;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RectangularSelection implements Selection {

    private final Point firstPoint;
    private final Point secondPoint;

    public RectangularSelection(Point firstPoint, Point secondPoint) {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    @Override
    public List<Integer> getSlots() {
        List<Integer> slots = new ArrayList<>();

        for (int x = firstPoint.getX(); x <= secondPoint.getX(); x++) {
            for (int y = firstPoint.getY(); y <= secondPoint.getY(); y++) {
                slots.add(getSlot(x, y));
            }
        }

        return slots;
    }

    private int getSlot(int x, int y) {
        return y * 9 + x;
    }


}

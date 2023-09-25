/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.selection;

import java.util.ArrayList;
import java.util.List;
import me.imlukas.wonderlandschat.utils.menu.math.Point;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;

public class RectangularSelection
implements Selection {
    private final Point firstPoint;
    private final Point secondPoint;

    public RectangularSelection(Point firstPoint, Point secondPoint) {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    @Override
    public List<Integer> getSlots() {
        ArrayList<Integer> slots = new ArrayList<Integer>();
        for (int x = this.firstPoint.getX(); x <= this.secondPoint.getX(); ++x) {
            for (int y = this.firstPoint.getY(); y <= this.secondPoint.getY(); ++y) {
                slots.add(this.getSlot(x, y));
            }
        }
        return slots;
    }

    private int getSlot(int x, int y) {
        return y * 9 + x;
    }

    public Point getFirstPoint() {
        return this.firstPoint;
    }

    public Point getSecondPoint() {
        return this.secondPoint;
    }
}


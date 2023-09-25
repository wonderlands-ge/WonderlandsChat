/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.selection;

import java.util.ArrayList;
import java.util.List;
import me.imlukas.wonderlandschat.utils.menu.math.Point;
import me.imlukas.wonderlandschat.utils.menu.selection.Selection;

public class HollowRectangularSelection
implements Selection {
    private int thickness = 1;
    private final Point firstPoint;
    private final Point secondPoint;

    public HollowRectangularSelection(Point firstPoint, Point secondPoint) {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
    }

    public HollowRectangularSelection(Point firstPoint, Point secondPoint, int thickness) {
        this.firstPoint = firstPoint;
        this.secondPoint = secondPoint;
        this.thickness = thickness;
    }

    public int getThickness() {
        return this.thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    @Override
    public List<Integer> getSlots() {
        ArrayList<Integer> slots = new ArrayList<Integer>();
        for (int thickness = 0; thickness < this.thickness; ++thickness) {
            Point firstPoint = new Point(this.firstPoint.getX() + thickness, this.firstPoint.getY() + thickness);
            Point secondPoint = new Point(this.secondPoint.getX() - thickness, this.secondPoint.getY() - thickness);
            slots.addAll(this.getSlots(firstPoint, secondPoint));
        }
        return slots;
    }

    private int getSlot(int x, int y) {
        return y * 9 + x;
    }

    private List<Integer> getSlots(Point firstPoint, Point secondPoint) {
        ArrayList<Integer> slots = new ArrayList<Integer>();
        for (int x = firstPoint.getX(); x <= secondPoint.getX(); ++x) {
            slots.add(this.getSlot(x, firstPoint.getY()));
            slots.add(this.getSlot(x, secondPoint.getY()));
        }
        for (int y = firstPoint.getY(); y <= secondPoint.getY(); ++y) {
            slots.add(this.getSlot(firstPoint.getX(), y));
            slots.add(this.getSlot(secondPoint.getX(), y));
        }
        return slots;
    }
}


/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.menu.math;

public class Point {
    private final int x;
    private final int y;

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Point)) {
            return false;
        }
        Point other = (Point)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getX() != other.getX()) {
            return false;
        }
        return this.getY() == other.getY();
    }

    protected boolean canEqual(Object other) {
        return other instanceof Point;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getX();
        result = result * 59 + this.getY();
        return result;
    }

    public String toString() {
        return "Point(x=" + this.getX() + ", y=" + this.getY() + ")";
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}


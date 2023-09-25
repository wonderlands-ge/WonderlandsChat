/*
 * Decompiled with CFR 0.150.
 */
package me.imlukas.wonderlandschat.utils.concurrent;

public class Reference<T> {
    private T value;

    public Reference(T value) {
        this.value = value;
    }

    public Reference() {
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.value = value;
    }

    public String toString() {
        return this.value.toString();
    }
}


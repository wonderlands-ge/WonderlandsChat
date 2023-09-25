/*
 * Decompiled with CFR 0.150.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 */
package me.imlukas.wonderlandschat.utils.text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import me.imlukas.wonderlandschat.utils.concurrent.Reference;
import org.bukkit.entity.Player;

public class Placeholder<T> {
    private final Function<T, String> replacement;
    private String placeholder;
    private boolean expensiveLookup = false;
    private boolean cacheValue = false;
    private String cachedValue;

    public Placeholder(String placeholder, Function<T, String> replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }

    public Placeholder(String placeholder, String replacement) {
        this(placeholder, (T object) -> replacement);
        this.cachedValue = replacement;
    }

    public Placeholder(String placeholder, CompletableFuture<String> replacement) {
        Reference<String> ref = new Reference<String>("Loading...");
        replacement.thenAccept(ref::set);
        this.placeholder = placeholder;
        this.replacement = object -> (String)ref.get();
    }

    public static List<Placeholder<Player>> asPlaceholderList(Map<String, Object> map) {
        ArrayList<Placeholder<Player>> list = new ArrayList<Placeholder<Player>>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof CompletableFuture) {
                CompletableFuture future = (CompletableFuture)value;
                list.add(new Placeholder(key, (CompletableFuture<String>)future.thenApply(Object::toString)));
                continue;
            }
            if (value instanceof Supplier) {
                Supplier supplier = (Supplier)value;
                list.add(new Placeholder(key, supplier.get().toString()));
                continue;
            }
            list.add(new Placeholder<Player>(key, __ -> map.get(key).toString()));
        }
        return list;
    }

    public String replace(String text, T object) {
        if (text == null) {
            return null;
        }
        if (!this.placeholder.startsWith("%")) {
            this.placeholder = "%" + this.placeholder;
        }
        if (!this.placeholder.endsWith("%")) {
            this.placeholder = this.placeholder + "%";
        }
        if (this.cacheValue && this.cachedValue != null) {
            return text.replace(this.placeholder, this.cachedValue);
        }
        if (this.expensiveLookup) {
            int substringIndex = text.indexOf(this.placeholder);
            while (substringIndex != -1) {
                String before = text.substring(0, substringIndex);
                String after = text.substring(substringIndex + this.placeholder.length());
                text = before + this.replace(object) + after;
                substringIndex = text.indexOf(this.placeholder);
            }
            this.tryCache(text);
            return text;
        }
        String value = this.replace(object);
        this.tryCache(value);
        return text.replace(this.placeholder, value);
    }

    private String replace(T object) {
        return this.replacement.apply(object);
    }

    private void tryCache(String value) {
        if (this.cacheValue) {
            this.cachedValue = value;
        }
    }

    public int hashCode() {
        return this.placeholder.hashCode();
    }

    public Function<T, String> getReplacement() {
        return this.replacement;
    }

    public String getPlaceholder() {
        return this.placeholder;
    }

    public boolean isExpensiveLookup() {
        return this.expensiveLookup;
    }

    public boolean isCacheValue() {
        return this.cacheValue;
    }

    public String getCachedValue() {
        return this.cachedValue;
    }

    public Placeholder(Function<T, String> replacement) {
        this.replacement = replacement;
    }

    public Placeholder(Function<T, String> replacement, String placeholder, boolean expensiveLookup, boolean cacheValue, String cachedValue) {
        this.replacement = replacement;
        this.placeholder = placeholder;
        this.expensiveLookup = expensiveLookup;
        this.cacheValue = cacheValue;
        this.cachedValue = cachedValue;
    }

    public void setExpensiveLookup(boolean expensiveLookup) {
        this.expensiveLookup = expensiveLookup;
    }

    public void setCacheValue(boolean cacheValue) {
        this.cacheValue = cacheValue;
    }
}


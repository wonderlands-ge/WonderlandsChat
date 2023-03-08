package me.imlukas.chatcolorgui.utils.text;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.imlukas.chatcolorgui.utils.concurrent.Reference;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
/*
 * placeholder -> namespace (%player_name% -> player)
 * replacement -> function (player -> "illusion")
 * expensiveLookup: recursive-like, it allows you to have placeholders returning other placeholders
 * cacheValue: if true, the value will be cached once and never updated
 * cachedValue: the cached value
 */
public class Placeholder<T> {

    private final Function<T, String> replacement;
    private String placeholder;
    @Setter
    private boolean expensiveLookup = false;
    @Setter
    private boolean cacheValue = false;

    private String cachedValue;

    public Placeholder(String placeholder, Function<T, String> replacement) {
        this.placeholder = placeholder;
        this.replacement = replacement;
    }

    public Placeholder(String placeholder, String replacement) {
        this(placeholder, (object) -> replacement);
        this.cachedValue = replacement;
    }

    public Placeholder(String placeholder, CompletableFuture<String> replacement) {
        Reference<String> ref = new Reference<>("Loading...");

        replacement.thenAccept(ref::set);

        this.placeholder = placeholder;
        this.replacement = (object) -> ref.get();
    }

    public static List<Placeholder<Player>> asPlaceholderList(Map<String, Object> map) {
        List<Placeholder<Player>> list = new ArrayList<>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();

            if (value instanceof CompletableFuture<?>) {
                CompletableFuture<?> future = (CompletableFuture<?>) value;
                list.add(new Placeholder<>(key, future.thenApply(Object::toString)));
                continue;
            }

            if (value instanceof Supplier<?> ) {
                Supplier<?> supplier = (Supplier<?>) value;
                list.add(new Placeholder<>(key, supplier.get().toString()));
                continue;
            }

            list.add(new Placeholder<>(key, (__) -> map.get(key).toString()));
        }

        return list;
    }


    public String replace(String text, T object) {
        if (text == null)
            return null;

        if (!placeholder.startsWith("%")) {
            placeholder = "%" + placeholder;
        }

        if (!placeholder.endsWith("%")) {
            placeholder = placeholder + "%";
        }

        if (cacheValue && cachedValue != null) {
            return text.replace(placeholder, cachedValue);
        }

        if (expensiveLookup) {
            int substringIndex = text.indexOf(placeholder);

            while (substringIndex != -1) {
                String before = text.substring(0, substringIndex);
                String after = text.substring(substringIndex + placeholder.length());

                text = before + replace(object) + after;

                substringIndex = text.indexOf(placeholder);
            }

            tryCache(text);

            return text;
        }

        String value = replace(object);

        tryCache(value);

        return text.replace(placeholder, value);
    }

    private String replace(T object) {
        return replacement.apply(object);
    }

    private void tryCache(String value) {
        if (cacheValue) {
            cachedValue = value;
        }
    }

    @Override
    public int hashCode() {
        return placeholder.hashCode();
    }
}

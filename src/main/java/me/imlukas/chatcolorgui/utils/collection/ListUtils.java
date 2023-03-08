package me.imlukas.chatcolorgui.utils.collection;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class ListUtils {

    private ListUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static <T> T getRandom(Collection<T> set) {
        int size = set.size();
        int index = ThreadLocalRandom.current().nextInt(size);
        int i = 0;

        for (T t : set) {
            if (i == index) {
                return t;
            }

            i++;
        }

        return null;
    }

    public static <T> T getRandom(List<T> list) {
        int size = list.size();

        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return list.get(0);
        }

        Random random = ThreadLocalRandom.current();

        return list.get(random.nextInt(size));
    }
}

package com.github.nbuesing.kafka.connect.opensky.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public final class ListUtils {

    private ListUtils() {
    }

    /**
     * Both Guava and Apache Commons Collections 4 have similar methods, but those are based on the size
     * of the partition, not on the number of partitions. This allows for breaking of tasks to allow
     * bounding boxes to be split apart to separate tasks, but when there is less workers than bounding boxes,
     * then bounding boxes are shared.
     */
    public static <T> List<List<T>> partition(final List<T> list, final int parts) {

        ArrayList<List<T>> lists = new ArrayList<>(parts);

        int index = 0;
        int partsLeft = Math.min(parts, list.size());

        while (index < list.size()) {

            int remaining = list.size() - index;

            int size = remaining / partsLeft;

            lists.add(list.subList(index, index + size));

            index = index + size;
            partsLeft--;
        }

        return lists;
    }

}

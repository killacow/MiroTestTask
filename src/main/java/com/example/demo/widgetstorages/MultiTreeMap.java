package com.example.demo.widgetstorages;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class MultiTreeMap {
    private final TreeMap<Integer, HashMap<UUID, StoredWidget>> treeMap;
    private final Function<StoredWidget, Integer> keyExtractor;

    public MultiTreeMap(Function<StoredWidget, Integer> keyExtractor) {
        treeMap = new TreeMap<>();
        this.keyExtractor = keyExtractor;
    }

    public void put(StoredWidget widget) {
        Integer key = keyExtractor.apply(widget);
        HashMap<UUID, StoredWidget> values = treeMap.get(key);
        if (values == null)
            treeMap.put(key, new HashMap<>() {{
                put(widget.getId(), widget);
            }});
        else
            values.put(widget.getId(), widget);
    }

    public StoredWidget remove(Integer key, StoredWidget widget) {
        HashMap<UUID, StoredWidget> values = treeMap.get(key);
        if (values != null) {
            StoredWidget removedWidget = values.remove(widget.getId());
            if (values.size() == 0)
                treeMap.remove(key);
            return removedWidget;
        }
        return null;
    }

    public Stream<StoredWidget> filterHeadFor(Stream<StoredWidget> widgets, Integer bound) {
        NavigableMap<Integer, HashMap<UUID, StoredWidget>> map = head(bound);
        return widgets.filter(widget -> contains(map, widget));
    }

    public Stream<StoredWidget> filterTailFor(Stream<StoredWidget> widgets, Integer bound) {
        NavigableMap<Integer, HashMap<UUID, StoredWidget>> map = tail(bound);
        return widgets.filter(widget -> contains(map, widget));
    }

    @SuppressWarnings("unused")
    public Collection<StoredWidget> unwrappedHead(Integer bound) {
        Collection<HashMap<UUID, StoredWidget>> wrappedValues = head(bound).values();
        return unwrap(wrappedValues);
    }

    public Collection<StoredWidget> unwrappedTail(Integer bound) {
        Collection<HashMap<UUID, StoredWidget>> wrappedValues = tail(bound).values();
        return unwrap(wrappedValues);
    }

    private static Collection<StoredWidget> unwrap(Collection<HashMap<UUID, StoredWidget>> wrappedValues) {
        ArrayList<StoredWidget> result = new ArrayList<>(wrappedValues.size());
        for (HashMap<UUID, StoredWidget> wrappedValue : wrappedValues) {
            result.addAll(wrappedValue.values());
        }
        return result;
    }

    private boolean contains(NavigableMap<Integer, HashMap<UUID, StoredWidget>> map, StoredWidget widget) {
        HashMap<UUID, StoredWidget> uuidStoredWidgetHashMap = map.get(keyExtractor.apply(widget));
        if (uuidStoredWidgetHashMap == null)
            return false;
        return uuidStoredWidgetHashMap.containsKey(widget.getId());
    }

    private NavigableMap<Integer, HashMap<UUID, StoredWidget>> head(Integer bound) {
        return treeMap.headMap(bound, true);
    }

    private NavigableMap<Integer, HashMap<UUID, StoredWidget>> tail(Integer bound) {
        return treeMap.tailMap(bound, true);
    }
}

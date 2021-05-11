package com.example.demo.widgetstorages;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.models.WidgetUpdateRequest;

import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;

public class WidgetStorage implements IWidgetStorage {
    private final HashMap<UUID, StoredWidget> widgetsById;
    private final TreeMap<Integer, StoredWidget> widgetsSortedByZ;
    private final StampedLock lock;

    public WidgetStorage() {
        this.widgetsById = new HashMap<>();
        this.widgetsSortedByZ = new TreeMap<>();
        this.lock = new StampedLock();
    }

    public Widget create(WidgetCreateRequest widgetCreateRequest) {
        UUID id = UUID.randomUUID();
        boolean putOnTop = widgetCreateRequest.getZ() == null;
        StoredWidget newWidget = new StoredWidget(
                id,
                widgetCreateRequest.getX(),
                widgetCreateRequest.getY(),
                0, // this way we don't need to readLock at this point, so we can fail cheap (without locks) if we try to create widget with illegal args
                widgetCreateRequest.getWidth(),
                widgetCreateRequest.getHeight());
        long stamp = lock.writeLock();
        try {
            newWidget.setZ(putOnTop ? getTopZ() : widgetCreateRequest.getZ()); // actually, we don't need write-lock here, but we'll need it in any case later, so using tryConvertToWriteLock is considered as overhead
            widgetsById.put(id, newWidget);
            if (putOnTop) {
                widgetsSortedByZ.put(newWidget.getZ(), newWidget);
            } else {
                insert(newWidget);
            }
            return toWidget(newWidget);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public Widget read(UUID id) {
        long stamp = lock.tryOptimisticRead();
        try {
            for (; ; stamp = lock.readLock()) {
                if (stamp == 0L)
                    continue;
                Widget widget = toWidget(widgetsById.get(id));
                if (!lock.validate(stamp))
                    continue;
                return widget;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp))
                lock.unlockRead(stamp);
        }
    }

    public List<Widget> read() {
        long stamp = lock.tryOptimisticRead();
        try {
            for (; ; stamp = lock.readLock()) {
                if (stamp == 0L)
                    continue;

                // in the worst case this code will be called twice because of the optimistic reading;
                // so it depends on the ratio of read/write operations whether to use tryOptimisticRead or just readLock
                List<Widget> widgets = widgetsSortedByZ.values().stream().map(WidgetStorage::toWidget).collect(Collectors.toUnmodifiableList());

                if (!lock.validate(stamp))
                    continue;
                return widgets;
            }
        } finally {
            if (StampedLock.isReadLockStamp(stamp))
                lock.unlockRead(stamp);
        }
    }

    public Widget update(UUID id, WidgetUpdateRequest widgetUpdateRequest) {
        long stamp = lock.tryOptimisticRead();
        try {
            for (; ; stamp = lock.writeLock()) {
                if (stamp == 0L)
                    continue;
                StoredWidget storedWidget = widgetsById.get(id);
                Widget widget = toWidget(storedWidget);
                if (!lock.validate(stamp))
                    continue;
                if (widget == null)
                    return null;
                stamp = lock.tryConvertToWriteLock(stamp);
                if (stamp == 0L)
                    continue;
                if (widgetUpdateRequest.getX() != null)
                    storedWidget.setX(widgetUpdateRequest.getX());
                if (widgetUpdateRequest.getY() != null)
                    storedWidget.setY(widgetUpdateRequest.getY());
                if (widgetUpdateRequest.getWidth() != null)
                    storedWidget.setWidth(widgetUpdateRequest.getWidth());
                if (widgetUpdateRequest.getHeight() != null)
                    storedWidget.setHeight(widgetUpdateRequest.getHeight());
                if (widgetUpdateRequest.getZ() != null) {
                    int oldZ = storedWidget.getZ();
                    storedWidget.setZ(widgetUpdateRequest.getZ());
                    if (widgetUpdateRequest.getZ() != oldZ) {
                        widgetsSortedByZ.remove(oldZ);
                        storedWidget.setZ(widgetUpdateRequest.getZ());
                        insert(storedWidget);
                    }
                }
                return toWidget(storedWidget);
            }
        } finally {
            if (StampedLock.isWriteLockStamp(stamp))
                lock.unlockWrite(stamp);
        }
    }

    public Widget delete(UUID id) {
        long stamp = lock.writeLock();
        try {
            StoredWidget widget = widgetsById.remove(id);
            if (widget != null)
                widgetsSortedByZ.remove(widget.getZ());
            return toWidget(widget);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    private int getTopZ() {
        if (widgetsSortedByZ.isEmpty())
            return 0;
        return widgetsSortedByZ.lastKey() + 1; // TODO: can cause type overflow
    }

    private void insert(StoredWidget widget) {
        StoredWidget existingWidgetWithSameZ = widgetsSortedByZ.get(widget.getZ());
        if (existingWidgetWithSameZ == null) {
            widgetsSortedByZ.put(widget.getZ(), widget);
        } else {
            StoredWidget widgetToInsert = widget;
            for (Map.Entry<Integer, StoredWidget> entry : widgetsSortedByZ.tailMap(widget.getZ()).entrySet()) {
                if (widgetToInsert.getZ() != entry.getKey())
                    break;
                StoredWidget widgetToShift = entry.getValue();
                entry.setValue(widgetToInsert);
                widgetToShift.setZ(widgetToShift.getZ() + 1); // TODO: can cause type overflow
                widgetToInsert = widgetToShift;
            }
            widgetsSortedByZ.put(widgetToInsert.getZ(), widgetToInsert);
        }
    }

    private static Widget toWidget(StoredWidget widget) {
        if (widget == null)
            return null;
        return new Widget(widget.getId(), widget.getX(), widget.getY(), widget.getZ(), widget.getWidth(), widget.getHeight(), widget.getLastModifiedDate());
    }
}
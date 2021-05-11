package com.example.demo.widgetstorages;

import com.example.demo.models.Widget;
import com.example.demo.models.WidgetCreateRequest;
import com.example.demo.models.WidgetUpdateRequest;

import java.util.*;
import java.util.concurrent.locks.StampedLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WidgetStorageWithPagingAndFiltering implements IWidgetStorage {
    private final HashMap<UUID, StoredWidget> widgetsById;
    private final List<StoredWidget> widgetsSortedByZ;
    private final MultiTreeMap widgetsSortedByLeftBound;
    private final MultiTreeMap widgetsSortedByRightBound;
    private final MultiTreeMap widgetsSortedByUpperBound;
    private final MultiTreeMap widgetsSortedByLowerBound;
    private final StampedLock lock;

    public WidgetStorageWithPagingAndFiltering() {
        this.widgetsById = new HashMap<>();
        this.widgetsSortedByZ = new ArrayList<>();
        this.widgetsSortedByLeftBound = new MultiTreeMap(StoredWidget::getLeftBound);
        this.widgetsSortedByRightBound = new MultiTreeMap(StoredWidget::getRightBound);
        this.widgetsSortedByUpperBound = new MultiTreeMap(StoredWidget::getUpperBound);
        this.widgetsSortedByLowerBound = new MultiTreeMap(StoredWidget::getLowerBound);
        this.lock = new StampedLock();
    }

    private static Widget toWidget(StoredWidget widget) {
        if (widget == null)
            return null;
        return new Widget(widget.getId(), widget.getX(), widget.getY(), widget.getZ(), widget.getWidth(), widget.getHeight(), widget.getLastModifiedDate());
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
            widgetsSortedByLeftBound.put(newWidget);
            widgetsSortedByRightBound.put(newWidget);
            widgetsSortedByUpperBound.put(newWidget);
            widgetsSortedByLowerBound.put(newWidget);
            if (putOnTop) {
                widgetsSortedByZ.add(newWidget);
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
        return read(null, null, null, null, null, null);
    }

    public List<Widget> read(Integer skip, Integer take) {
        return read(skip, take, null, null, null, null);
    }

    public List<Widget> read(Integer leftBound, Integer rightBound, Integer upperBound, Integer lowerBound) {
        return read(null, null, leftBound, rightBound, upperBound, lowerBound);
    }

    public List<Widget> read(Integer skip, Integer take, Integer leftBound, Integer rightBound, Integer upperBound, Integer lowerBound) {
        if ((skip != null || take != null) && (leftBound != null || rightBound != null || upperBound != null || lowerBound != null))
            throw new IllegalArgumentException();

        if (skip == null) skip = 0;
        if (take == null) take = 10;
        if (skip < 0 || take < 0) throw new IllegalArgumentException();
        if (take > 500) throw new IllegalArgumentException();

        boolean useBounds = (leftBound != null && rightBound != null && upperBound != null && lowerBound != null);
        if (useBounds) {
            if (leftBound > rightBound || upperBound < lowerBound)
                throw new IllegalArgumentException();
        } else {
            if (leftBound != null || rightBound != null || upperBound != null || lowerBound != null)
                throw new IllegalArgumentException();
        }

        long stamp = lock.readLock(); // quite an expensive operation under lock, optimistic reading seems risky because it might double the price; so, just readLock
        try {

            Stream<StoredWidget> widgetsStream;
            if (useBounds) {
                Stream<StoredWidget> widgetsFilteredByLeftBound =
                        widgetsSortedByLeftBound.unwrappedTail(leftBound).stream();
                Stream<StoredWidget> widgetsFilteredByLeftAndRightBound =
                        widgetsSortedByRightBound.filterHeadFor(widgetsFilteredByLeftBound, rightBound);
                Stream<StoredWidget> widgetsFilteredByLeftAndRightAndUpperBound =
                        widgetsSortedByUpperBound.filterHeadFor(widgetsFilteredByLeftAndRightBound, upperBound);
                Stream<StoredWidget> widgetsFilteredByLeftAndRightAndUpperAndLowerBound =
                        widgetsSortedByLowerBound.filterTailFor(widgetsFilteredByLeftAndRightAndUpperBound, lowerBound);
                widgetsStream = widgetsFilteredByLeftAndRightAndUpperAndLowerBound.sorted();
            } else {
                widgetsStream = widgetsSortedByZ.stream().skip(skip).limit(take);
            }
            return widgetsStream.map(WidgetStorageWithPagingAndFiltering::toWidget).collect(Collectors.toUnmodifiableList());
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
                int oldLeftBound = storedWidget.getLeftBound();
                int oldRightBound = storedWidget.getRightBound();
                int oldUpperBound = storedWidget.getUpperBound();
                int oldLowerBound = storedWidget.getLowerBound();
                if (widgetUpdateRequest.getX() != null)
                    storedWidget.setX(widgetUpdateRequest.getX());
                if (widgetUpdateRequest.getY() != null)
                    storedWidget.setY(widgetUpdateRequest.getY());
                if (widgetUpdateRequest.getWidth() != null)
                    storedWidget.setWidth(widgetUpdateRequest.getWidth());
                if (widgetUpdateRequest.getHeight() != null)
                    storedWidget.setHeight(widgetUpdateRequest.getHeight());
                if (widgetUpdateRequest.getZ() != null) {
                    if (widgetUpdateRequest.getZ() != storedWidget.getZ()) {
                        widgetsSortedByZ.remove(Collections.binarySearch(widgetsSortedByZ, storedWidget));
                        storedWidget.setZ(widgetUpdateRequest.getZ());
                        insert(storedWidget);
                    } else {
                        storedWidget.setZ(widgetUpdateRequest.getZ());
                    }
                }
                int newLeftBound = storedWidget.getLeftBound();
                if (newLeftBound != oldLeftBound)
                    widgetsSortedByLeftBound.put(widgetsSortedByLeftBound.remove(oldLeftBound, storedWidget));
                int newRightBound = storedWidget.getRightBound();
                if (newRightBound != oldRightBound)
                    widgetsSortedByRightBound.put(widgetsSortedByRightBound.remove(oldRightBound, storedWidget));
                int newUpperBound = storedWidget.getUpperBound();
                if (newUpperBound != oldUpperBound)
                    widgetsSortedByUpperBound.put(widgetsSortedByUpperBound.remove(oldUpperBound, storedWidget));
                int newLowerBound = storedWidget.getLowerBound();
                if (newLowerBound != oldLowerBound)
                    widgetsSortedByLowerBound.put(widgetsSortedByLowerBound.remove(oldLowerBound, storedWidget));
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
            if (widget != null) {
                widgetsSortedByZ.remove(Collections.binarySearch(widgetsSortedByZ, widget));
                widgetsSortedByLeftBound.remove(widget.getLeftBound(), widget);
                widgetsSortedByRightBound.remove(widget.getRightBound(), widget);
                widgetsSortedByUpperBound.remove(widget.getUpperBound(), widget);
                widgetsSortedByLowerBound.remove(widget.getLowerBound(), widget);
            }
            return toWidget(widget);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    private int getTopZ() {
        if (widgetsSortedByZ.isEmpty())
            return 0;
        return widgetsSortedByZ.get(widgetsSortedByZ.size() - 1).getZ() + 1; // TODO: can cause type overflow
    }

    private void insert(StoredWidget widget) {
        int index = Collections.binarySearch(widgetsSortedByZ, widget);
        if (index < 0) {
            widgetsSortedByZ.add(-index - 1, widget);
        } else {
            StoredWidget widgetToInsert = widget;
            int i;
            for (i = index; i < widgetsSortedByZ.size(); i++) {
                StoredWidget storedWidget = widgetsSortedByZ.get(i);
                if (widgetToInsert.getZ() != storedWidget.getZ())
                    break;
                @SuppressWarnings("UnnecessaryLocalVariable")
                StoredWidget widgetToShift = storedWidget;
                widgetsSortedByZ.set(i, widgetToInsert);
                widgetToShift.setZ(widgetToShift.getZ() + 1); // TODO: can cause type overflow
                widgetToInsert = widgetToShift;
            }
            widgetsSortedByZ.add(i, widgetToInsert);
        }
    }
}

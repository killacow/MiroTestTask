package com.example.demo.widgetstorages;

import java.time.ZonedDateTime;
import java.util.UUID;

public class StoredWidget implements Comparable<StoredWidget> {
    private final UUID id;
    private int x;
    private int y;
    private int z;
    private int width;
    private int height;
    private ZonedDateTime lastModifiedDate;

    public StoredWidget(UUID id, int x, int y, int z, int width, int height) {
        this.id = id;
        setX(x);
        setY(y);
        setZ(z);
        setWidth(width);
        setHeight(height);
    }

    public UUID getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        updateLastModifiedDate();
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        updateLastModifiedDate();
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
        updateLastModifiedDate();
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) throws IllegalArgumentException {
        if (width <= 0)
            throw new IllegalArgumentException("width can not be less or equal to zero");
        this.width = width;
        updateLastModifiedDate();
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) throws IllegalArgumentException {
        if (height <= 0)
            throw new IllegalArgumentException("height can not be less or equal to zero");
        this.height = height;
        updateLastModifiedDate();
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    private void updateLastModifiedDate() {
        this.lastModifiedDate = ZonedDateTime.now();
    }

    @Override
    public int compareTo(StoredWidget o) {
        return Integer.compare(getZ(), o.getZ());
    }

    public int getLeftBound() {
        return x - width / 2;
    }

    public int getRightBound() {
        return x + width / 2;
    }

    public int getUpperBound() {
        return y + height / 2;
    }

    public int getLowerBound() {
        return y - height / 2;
    }
}
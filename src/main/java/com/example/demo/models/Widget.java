package com.example.demo.models;

import java.time.ZonedDateTime;
import java.util.UUID;

public class Widget {
    private final UUID id;
    private final int x;
    private final int y;
    private final int z;
    private final int width;
    private final int height;
    private final ZonedDateTime lastModifiedDate;

    public Widget(UUID id, int x, int y, int z, int width, int height, ZonedDateTime lastModifiedDate) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.lastModifiedDate = lastModifiedDate;
    }

    public UUID getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ZonedDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }
}

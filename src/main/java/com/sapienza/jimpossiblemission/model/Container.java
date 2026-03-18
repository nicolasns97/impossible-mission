package com.sapienza.jimpossiblemission.model;

import java.awt.*;
import java.util.Optional;

public class Container extends Entity implements Interactable {
    private final ContainerType type;
    private int searchCompletion;
    private Collectable collectable;

    public Container(int x, int y, int width, int height, ContainerType type) {
        this(x, y, width, height, type, null);
    }

    public Container(int x, int y, int width, int height, ContainerType type, Collectable collectable) {
        super(x, y, width, height);
        this.type = type;
        searchCompletion = 0;
        this.collectable = collectable;
    }

    public Optional<Collectable> pick() {
        if (searchCompletion >= 100 && collectable != null) {
            return Optional.of(collectable);
        }
        return Optional.empty();
    }

    public int getSearchCompletion() {
        return searchCompletion;
    }

    @Override
    public void interact() {
        searchCompletion += 2;
    }

    @Override
    public Rectangle getBoundingBox() {
        return null;
    }

    public enum ContainerType {
        DRAWER,
        SAFE,
        LOCKER,
        VENDING_MACHINE,
        SHELF,
        DESK
    }
}

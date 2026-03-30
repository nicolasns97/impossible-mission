package com.sapienza.jimpossiblemission.model;

import java.awt.*;
import java.util.Optional;

public class Container extends Entity implements Interactable {
    private final ContainerType type;
    private final Collectable collectable;
    private int searchCompletion;
    private boolean interacting;

    public Container(int x, int y, ContainerType type) {
        this(x, y, type, null);
    }

    public Container(int x, int y, ContainerType type, Collectable collectable) {
        super(x, y - type.getHeight(), type.getWidth(), type.getHeight());
        this.type = type;
        this.collectable = collectable;
        searchCompletion = 0;
    }

    public Optional<Collectable> collect() {
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
        interacting = true;
    }

    @Override
    public void stopInteracting() {
        interacting = false;
    }

    @Override
    public boolean isInteracting() {
        return interacting;
    }

    @Override
    public Rectangle getBoundingBox() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    public ContainerType getType() {
        return type;
    }

    public enum ContainerType {
        DRAWER(50, 60),
        VENDING_MACHINE(100, 100),
        BOOKSHELF(80, 120),
        DESK(200, 100),
        PRINTER(80, 80);

        private final int width;
        private final int height;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        ContainerType(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}

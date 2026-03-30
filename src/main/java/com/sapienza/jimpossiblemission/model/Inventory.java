package com.sapienza.jimpossiblemission.model;

import com.sapienza.jimpossiblemission.model.Code.CodeType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Inventory {
    private final List<Card> cards;
    private final List<Code> codes;

    public Inventory() {
        cards = new ArrayList<>();
        codes = new ArrayList<>();
    }

    public List<Code> getCodes() {
        return codes;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addCode(Code code) {
        codes.add(code);
    }

    public boolean useCode(CodeType type) {
        return codes.stream()
                .filter(c -> c.getType() == type)
                .findFirst()
                .map(codes::remove)
                .orElse(false);
    }

    public void add(Collectable collectable) {
        if (collectable instanceof Card) {
            cards.add((Card) collectable);
        } else if (collectable instanceof Code) {
            codes.add((Code) collectable);
        }
    }
}

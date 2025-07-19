package com.blackjack.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoveTest {
    @Test
    void testFromStringValidInputs() {
        assertEquals(Move.HIT, Move.fromString("hit"));
        assertEquals(Move.HIT, Move.fromString("h"));
        assertEquals(Move.STAY, Move.fromString("stay"));
        assertEquals(Move.STAY, Move.fromString("st"));
        assertEquals(Move.DOUBLE, Move.fromString("double"));
        assertEquals(Move.DOUBLE, Move.fromString("d"));
        assertEquals(Move.SPLIT, Move.fromString("split"));
        assertEquals(Move.SPLIT, Move.fromString("sp"));
        assertEquals(Move.SURRENDER, Move.fromString("surrender"));
        assertEquals(Move.SURRENDER, Move.fromString("sr"));
        assertEquals(Move.EXIT, Move.fromString("exit"));
        assertEquals(Move.EXIT, Move.fromString("q"));
        assertEquals(Move.EXIT, Move.fromString("e"));
        assertEquals(Move.EXIT, Move.fromString("quit"));
    }

    @Test
    void testFromStringInvalidInputThrows() {
        assertThrows(IllegalArgumentException.class, () -> Move.fromString("invalid"));
    }
}

package com.cmdjojo.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KbdxTest {

    @Test
    @DisplayName("MsToString test")
    void testMsToString() {
        assertEquals("2s 543ms", Kbdx.msToString(2543), "msToString with 1 param");
        assertEquals("2s", Kbdx.msToString(2543, 1), "msToString with 2 params#1");
        assertEquals("1m", Kbdx.msToString(62543, 1), "msToString with 2 params#2");
        assertEquals("1m 2s", Kbdx.msToString(62543, 2), "msToString with 2 params#3");

        assertEquals("543ms", Kbdx.msToString(543), "msToString, 1arg, no max");
        assertEquals("543ms", Kbdx.msToString(543, 1), "msToString, 1arg, 1 max");

        assertEquals("2s 543ms", Kbdx.msToString(2543), "msToString, 2args, no max");
        assertEquals("2s", Kbdx.msToString(2543, 1), "msToString, 2args, 1 max");
        assertEquals("2s 543ms", Kbdx.msToString(2543, 2), "msToString, 2args, 2 max");

        assertEquals("1m 8s 603ms", Kbdx.msToString(68603), "msToString, 3args, no max");
        assertEquals("1m", Kbdx.msToString(68603, 1), "msToString, 3args, 1 max");
        assertEquals("1m 8s", Kbdx.msToString(68603, 2), "msToString, 3args, 2 max");
        assertEquals("1m 8s 603ms", Kbdx.msToString(68603, 3), "msToString, 3args, 3 max");

        // 3 600 000 (1h) + 120 000 (2m) + 55 000 + 450
        assertEquals("1h 2m 55s 450ms", Kbdx.msToString(3775450), "msToString, 4args, no max");
        assertEquals("1h 2m", Kbdx.msToString(3775450, 2), "msToString, 4args, 2 max");

        // 518 400 000 (6d) + 7 200 000 (2h) + 180 000 (3m) + 4 000 + 125
        assertEquals("6d 2h 3m 4s 125ms", Kbdx.msToString(525784125), "msToString, 4args, no max");
        assertEquals("6d 2h 3m", Kbdx.msToString(525784125, 3), "msToString, 4args, 3 max");

        // 31 536 000 000 (1y) + 518 400 000 (6d) + 7 200 000 (2h) + 180 000 (3m) + 4
        // 000 + 125
        assertEquals("1y 6d 2h 3m 4s 125ms", Kbdx.msToString(32061784125L), "msToString, 4args, no max");
        assertEquals("1y 6d", Kbdx.msToString(32061784125L, 2), "msToString, 4args, 2 max");

        assertEquals("-1y 6d", Kbdx.msToString(-32061784125L, 2), "msToString, negative input, 4args, 2 max");

        assertThrows(IndexOutOfBoundsException.class, () -> Kbdx.msToString(1000, -2), "msToString out of bounds (-2)");

        assertThrows(IndexOutOfBoundsException.class, () -> Kbdx.msToString(1000, 0), "msToString out of bounds (0)");

        assertThrows(IndexOutOfBoundsException.class, () -> Kbdx.msToString(1000, 100), "msToString out of bounds (100)");

    }

    @Test
    @DisplayName("Random() test")
    void testRandom() {
        for (int i = 0; i < 1000; i++) {
            for (int r = 1; r < 100; r++) {
                var random1 = Kbdx.random(r);
                var random2 = Kbdx.random(r, r + 2);
                var constr = Kbdx.random(r, r + 1);
                assertTrue(random1 > -1 && random1 < r, "Random with 1 param");
                assertTrue(random2 > (r - 1) && random2 < (r + 2), "Random with 2 params");
                assertEquals(constr, r, "Random with 1 size (static)");

                var random3 = Kbdx.random(r * -1);
                var random4 = Kbdx.random(r * -1, -1);
                var random5 = Kbdx.random(r * -1, r);
                assertTrue(random3 > r * -1 && random3 <= 0, "Random with 1 param, negative");
                if (r > 1) {
                    assertTrue(random4 > r * -1 && random4 <= -1, "Random with 2 params, negative");
                } else {
                    assertEquals(random4, r * -1, "Random with 2 params, negative, absolute");
                }
                assertTrue(random5 > r * -1 && random5 < r, "Random with 2 params, mixed signs");


            }
        }
    }

    @Test
    @DisplayName("Math (floor, ceil, round) test")
    void testMath() {
        assertEquals(Kbdx.floor(7.4d), 7L, "Floor double -> long");
        assertEquals(Kbdx.ceil(7.4d), 8L, "Ceil double -> long");

        assertEquals(Kbdx.round(5.3453453, 5), 5.34535, "Rounding up, 5 digits");
        assertEquals(Kbdx.round(5.3453449, 5), 5.34534, "Rounding down, 5 digits");

        assertEquals(Kbdx.round(-5.3453453, 5), -5.34535, "Rounding up, 5 digits");
        assertEquals(Kbdx.round(-5.3453449, 5), -5.34534, "Rounding down, 5 digits");
    }

    @Test
    @DisplayName("Average() and Contains() test")
    void testArrayUtill() {
        int[] arr1 = {7, 4, 6, 2, 3, 4};
        //sum 26, avg = 26/6 = 13/3
        assertEquals(13d / 3, Kbdx.average(arr1), "Average, int array");
        long[] arr2 = {1, 5, 3, 6, 3, 3};
        //sum 21, avg = 21/6 = 7/2
        assertEquals(7d / 2, Kbdx.average(arr2), "Average, long array");

        assertFalse(Kbdx.contains(arr1, 5), "Contains, int array");
        assertTrue(Kbdx.contains(arr1, 4), "Contains, int array");
        assertFalse(Kbdx.contains(arr2, 4), "Contains, long array");
        assertTrue(Kbdx.contains(arr2, 5), "Contains, long array");

        String[] arr3 = {"Hello", "RandomStuff", "idkwat"};
        assertFalse(Kbdx.contains(arr3, "hello"), "Contains, string array");
        assertTrue(Kbdx.contains(arr3, "Hello"), "Contains, string array");

        Object[] arr4 = new Object[2];
        var sb = new StringBuilder();
        arr4[0] = sb;
        arr4[1] = "Hello";
        assertFalse(Kbdx.contains(arr4, new StringBuilder()), "Contains, object array");
        assertTrue(Kbdx.contains(arr4, sb), "Contains, object array");
        assertTrue(Kbdx.contains(arr4, "Hello"), "Contains, object array");

    }

}

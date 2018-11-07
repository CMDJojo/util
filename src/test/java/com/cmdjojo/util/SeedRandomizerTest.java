package com.cmdjojo.util;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class SeedRandomizerTest {
    @Test
    @DisplayName("Same seed gets same next values")
    void testNextVals() {
        for (int i = 0; i < 10000; i++) {
            var random1 = new SeedRandomizer();
            var random2 = new SeedRandomizer(random1.getSeed());
            assertEquals(random1.nextDouble(), random2.nextDouble(), "Same first double with same seed");
            assertEquals(random1.nextLong(), random2.nextLong(), "Same first long with same seed");
            assertEquals(random1.nextInt(), random2.nextInt(), "Same first int with same seed");
            assertEquals(random1.nextBetween(3, 6), random2.nextBetween(3, 6), "Same first between with same seed");
            assertNotEquals(random1.nextInt(), random2.nextLong(), "Different int/long with same seed");
            assertNotEquals(random1.nextDouble(), random2.nextDouble(), "Different nextDouble if updated with other methods inbetween");
        }
    }

    @Test
    @DisplayName("Same seed gets same coord value")
    void testCoords() {
        for (int i = 0; i < 10000; i++) {
            var random1 = new SeedRandomizer();
            var random2 = new SeedRandomizer(random1.getSeed());
            var x = Kbdx.random(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);
            var y = Kbdx.random(Integer.MIN_VALUE + 1, Integer.MAX_VALUE - 1);
            var startLong = random1.coordLong(x, y);
            assertEquals(random1.coordLong(x, y), random2.coordLong(x, y), "Same long with same seed and coords");
            assertEquals(startLong, random2.coordLong(x, y), "Same long with same seed and coords");
            assertNotEquals(startLong, random2.coordLong(x + 1, y), "Different long with same seed but diffrent coords");

            assertEquals(random1.coordBetween(x, y, 234, 232423423), random2.coordBetween(x, y, 234, 232423423), "Same between with same seed and coords");
            assertNotEquals(random1.coordBetween(x, y, 1, 10000000), random2.coordBetween(x + 1, y, 1, 10000000), "Diffrent between with same seed but diffrent coords");

            assertEquals(random1.coordDouble(x, y), random2.coordDouble(x, y), "Same double with same seed and coords");
            assertNotEquals(random1.coordDouble(x, y), random2.coordDouble(x, y + 1), "Diffrent double with same seed but diffrent coords");

            assertTrue(random1.coordBetween(x, y, 1, 10) < 10, "Between always lower than upper bound");
            assertTrue(random1.coordBetween(x, y, 1, 10) > 0, "Between always higher than lower bound");

        }
    }
}

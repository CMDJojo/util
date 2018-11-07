package com.cmdjojo.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;

class StopwatchTest {
    private Stopwatch s;

    @BeforeEach
    void prepare() {
        s = new Stopwatch();
    }

    @Test
    @DisplayName("Entry operations")
    void operations() {
        s.set("TestTimer1");
        s.set("TestTimer2", 1000);
        s.set("TestTimer3", new Date(1000));
        //Creating timers
        assertNotEquals(0, s.get("TestTimer1"), "Setting timer now");
        assertEquals(1000, s.get("TestTimer2"), "Setting timer by long");
        assertEquals(1000, s.get("TestTimer3"), "Setting timer by Date");

        assertNotEquals(-1, s.index("TestTimer1"), "Setting timer now");
        assertNotEquals(-1, s.index("TestTimer2"), "Setting timer by long");
        assertNotEquals(-1, s.index("TestTimer3"), "Setting timer by Date");

        assertFalse(s.matches("TestTimer1", "TestTimer2"), "Timers not matching");
        assertTrue(s.matches("TestTimer2", "TestTimer3"), "Timers matching");
        assertTrue(s.matches("TestTimer1", "TestTimer1"), "Timer matching itself");

        s.remove("TestTimer1");
        //Non-defined and removed timers
        assertEquals(0, s.get("TestTimer4"), "Reading non-set timer");
        assertEquals(0, s.get("TestTimer1"), "Deleting a timer");

        assertEquals(-1, s.index("TestTimer4"), "Reading non-set timer");
        assertEquals(-1, s.index("TestTimer1"), "Deleting a timer");

        s.set("TestTimer1");
        //Reordering if re-created (TestTimer1)
        assertEquals("TestTimer1", s.getNext("TestTimer3"), "Reordering if re-created");
        assertEquals("TestTimer1", s.getNext(s.index("TestTimer3")), "Reordering if re-created");

        assertNull(s.getNext("TestTimer1"), "Reordering if re-created");
        assertNull(s.getNext(s.index("TestTimer1")), "Reordering if re-created");

        //Actions with null timer
        assertNull(s.getNext("TestTimer4"), "Next on undefined");
        assertNull(s.getNext(s.index("TestTimer4")), "Next on undefined");

        //Make sure all items are looped by iteratable
        var l1 = new ArrayList<String>();
        l1.add("TestTimer2");
        l1.add("TestTimer3");
        l1.add("TestTimer1");

        var l2 = new ArrayList<String>();
        for (String name : s) {
            l2.add(name);
        }
        assertEquals(l1, l2, "Iterates over all values");
    }

    @Test
    @DisplayName("Compare and CompareMs")
    void compare() {
        s.set("TestTimer1");
        s.set("TestTimer2", s.get("TestTimer1"));
        s.set("TestTimer3", s.get("TestTimer1") + 220300);
        //TestTimer1 equals TestTimer2
        //TestTimer2 diff is 3min 40s 300ms

        assertEquals("0ms", s.compare("TestTimer1", "TestTimer2"), "Comparing same timer");
        assertEquals(0, s.compareMs("TestTimer1", "TestTimer2"), "Comapring same timer");

        assertEquals("3m 40s 300ms", s.compare("TestTimer1", "TestTimer3"), "Comparing other timer");
        assertEquals(220300, s.compareMs("TestTimer1", "TestTimer3"), "Comapring other timer");

        for (int i = 0; i < 100; i++) {
            var randomdiff = Kbdx.random(2000, 345384583);
            s.set("1timer" + i);
            s.set("2timer" + i, s.get("1timer" + i) + randomdiff);
            assertEquals(randomdiff, s.compareMs("1timer" + i, "2timer" + i), "Comparing with random diff");
            assertEquals(randomdiff, s.compareMs("2timer" + i, "1timer" + i), "Comparing with random diff");
            assertEquals(s.compare("1timer" + i, "2timer" + i), s.compare("2timer" + i, "1timer" + i), "Comparing with random diff");
        }
    }
}

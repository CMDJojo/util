package com.cmdjojo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextTableTest {

    @Test
    void testDynamic() {
        var table = new TextTable(1);
        table.set(0, 0, "1")
                .setDynamic(true)
                .set(4, 0, "SE")
                .removeColumn(4)
                .set(2, 1, "2")
                .set(3, 3, "3")
                .set(3, 4, "AB");
        assertEquals("1       \n" +
                "   2    \n" +
                "        \n" +
                "     3  \n" +
                "     AB ", table.toString(), "Small text table without with custom splitters and fillers");
    }

    @Test
    void testDynamicWithSettings() {
        var table = new TextTable();
        table.setColumnsplitter(":")
                .setFiller('_')
                .set(0, 0, "1")
                .set(2, 1, "2")
                .set(2, 43, "test")
                .set(3, 3, "3")
                .removeRow(43)
                .set(3, 4, "AB");
        assertEquals("1::_:__:\n" +
                "_::2:__:\n" +
                "_::_:__:\n" +
                "_::_:3_:\n" +
                "_::_:AB:", table.toString(), "Small text table with custom splitters and fillers");
    }

    @Test
    void testStatic() {
        var table = new TextTable(5);
        table.set(0, 0, "1")
                .set(4, 0, "SE")
                .removeColumn(4)
                .set(2, 1, "2")
                .set(3, 3, "3")
                .set(3, 4, "AB");
        assertEquals("1        \n" +
                "   2     \n" +
                "         \n" +
                "     3   \n" +
                "     AB  ", table.toString(), "Small static text table without with custom splitters and fillers");
    }

    @Test
    void testStaticWithSettings() {
        var table = new TextTable(2);
        table.setWidth(4).setColumnsplitter(":")
                .setFiller('_')
                .set(0, 0, "1")
                .set(2, 1, "2")
                .set(2, 43, "test")
                .set(3, 3, "3")
                .removeRow(43)
                .set(3, 4, "AB");
        assertEquals("1::_:__:\n" +
                "_::2:__:\n" +
                "_::_:__:\n" +
                "_::_:3_:\n" +
                "_::_:AB:", table.toString(), "Small static text table with custom splitters and fillers");
    }

    @Test
    void testDynamicErrors() {
        var table = new TextTable();
        assertThrows(IllegalArgumentException.class, () -> {
            table.set(-1, 2, "Yo");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            table.set(2, -1, "Yo");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            table.setWidth(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            table.pruneRows(-1);
        });
    }

    @Test
    void testStaticErrors() {
        var table = new TextTable(2);
        assertThrows(IllegalArgumentException.class, () -> {
            table.set(3, 2, "Yo");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            table.set(-1, 2, "Yo");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            table.set(2, -1, "Yo");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            table.setWidth(-1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            table.pruneRows(-1);
        });
    }

    @Test
    void setterAndGetterDynamic() {
        var table = new TextTable();
        assertEquals(' ', table.getFiller(), "Get default filler");
        assertEquals('a', table.setFiller('a').getFiller(), "Set/get custom filler");
        assertEquals(" ", table.getColumnsplitter(), "Get default splitter");
        assertEquals("ABC", table.setColumnsplitter("ABC").getColumnsplitter(), "Set/get custom splitter");
        assertTrue(table.getDynamic(), "Default dynamid");
        assertFalse(table.setDynamic(false).getDynamic(), "Setting dynamic");
    }

    @Test
    void setterAndGetterStatic() {
        var table = new TextTable(3);
        assertEquals(' ', table.getFiller(), "Get default filler");
        assertEquals('a', table.setFiller('a').getFiller(), "Set/get custom filler");
        assertEquals(" ", table.getColumnsplitter(), "Get default splitter");
        assertEquals("ABC", table.setColumnsplitter("ABC").getColumnsplitter(), "Set/get custom splitter");
        assertFalse(table.getDynamic(), "Default dynamid");
        assertTrue(table.setDynamic(true).getDynamic(), "Setting dynamic");
    }
}

package com.cmdjojo.util;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * TextTable class can make a table that can be converted to string. When used in monospace (as in console), cells will fit their width to the widest cell in the column.
 *
 * @author CMDJojo
 * @version 1.1-SNAPSHOT
 */
public class TextTable {
    private boolean dynamic = true;
    private char filler = ' ';
    private String columnsplitter = " ";
    private ArrayList<HashMap<Integer, String>> data = new ArrayList<>();

    /**
     * Creates a TextTable with dynamic mode enabled
     */
    public TextTable() {

    }

    /**
     * Creates a TextTable with set width (non-dynamic)
     * <p>
     * You can make this dynamic by using the setDynamic method
     *
     * @param width Amount of 'columns' in the table
     */

    public TextTable(int width) {
        if (width < 0) throw new IllegalArgumentException("Width must be non-negative! Was " + width);
        dynamic = false;
        setWidth(width);
    }

    private static int widest(HashMap<Integer, String> d) {
        int l = 0;
        for (String s : d.values()) {
            if (s.length() > l) l = s.length();
        }
        return l;
    }

    private static int highestIndex(ArrayList<HashMap<Integer, String>> d) {
        int l = 0;
        for (HashMap<Integer, String> a : d) {
            for (Integer i : a.keySet()) {
                if (i > l) l = i;
            }
        }
        return l;
    }

    /**
     * Gets dynamic mode
     *
     * @return true if dynamic, false if not
     * @see TextTable#setDynamic(boolean)
     */
    public boolean getDynamic() {
        return this.dynamic;
    }

    /**
     * Sets the 'dynamic' state of this table. Dynamic tables will automaticly create new columns if needed
     *
     * @param value true if dynamic, false if not
     * @return this
     */
    public TextTable setDynamic(boolean value) {
        this.dynamic = value;
        return this;
    }

    /**
     * Gets the current filler
     *
     * @return the current filler
     * @see TextTable#setFiller(char)
     */
    public char getFiller() {
        return filler;
    }

    /**
     * Sets the filler to the provided char, default <code>' '</code>
     * <p>
     * The filler is used to lengthen empty boxes
     *
     * @param c The char to use
     * @return this
     */
    public TextTable setFiller(char c) {
        filler = c;
        return this;
    }

    /**
     * Gets the current column splitter
     *
     * @return the current column splitter
     * @see TextTable#setColumnsplitter(String)
     */
    public String getColumnsplitter() {
        return columnsplitter;
    }

    /**
     * Sets the column splitter to the provided string, default <code>" "</code>
     * <p>
     * The column splitter is used to seperate rows, and can be multiple characters
     *
     * @param s The string to use
     * @return this
     */
    public TextTable setColumnsplitter(String s) {
        columnsplitter = s;
        return this;
    }

    /**
     * Sets a box in the table
     *
     * @param col   Column index (starts at 0)
     * @param row   Row index (starts at 0)
     * @param value The value to set the box to
     * @return this
     */
    public TextTable set(int col, int row, String value) {
        if (col < 0) throw new IllegalArgumentException("Col must be non-negative! Was " + col);
        if (row < 0) throw new IllegalArgumentException("Row must be non-negative! Was " + row);
        if (col >= data.size()) {
            if (dynamic)
                setWidth(col + 1);
            else
                throw new IllegalArgumentException("You cannot write to an non-existing column with dynamic off");
        }
        data.get(col).put(row, value);
        return this;
    }

    /**
     * Removes an column by its index
     *
     * @param col Index of the target column
     * @return this
     */
    public TextTable removeColumn(int col) {
        if (col < 0 || col >= data.size())
            throw new IllegalArgumentException("Col must be in bounds! Was " + col + " - bounds 0 to " + (data.size() - 1));
        if (!dynamic) setWidth(data.size() + 1);
        data.remove(col);
        return this;
    }

    /**
     * Removes an row by its index
     *
     * @param row Index of the target row
     * @return this
     */
    public TextTable removeRow(int row) {
        if (row < 0 || row > highestIndex(data))
            throw new IllegalArgumentException("Row must be in bounds! Was " + row + " - bounds 0 to " + highestIndex(data));
        for (HashMap<Integer, String> t : data) {
            for (Integer i : t.keySet()) {
                if (i == row) t.remove(i);
            }
        }
        return this;
    }

    /**
     * Sets the width of the TextTable. If it is too long, it will remove the highest indexed columns (the rightmost columns)
     *
     * @param width Amount of columns for the table to have
     * @return this
     */
    public TextTable setWidth(int width) {
        if (width < 0) throw new IllegalArgumentException("Width must be non-negative! Was " + width);
        if (data.size() < width) {
            // If data size is 5 and should be pruned to 8, indexes size (5) to width-1 (7) should be created
            for (int i = data.size(); i < width; i++) {
                data.add(new HashMap<>());
            }
        } else if (data.size() > width) {
            // If data is size 8 and should be pruned to 5, indexes 8-1 until 8-i < width, 5, should be removed
            for (int i = data.size() - 1; i >= width; i++) {
                data.remove(i);
            }
        }
        return this;
    }

    /**
     * Prunes the amount of rows to the provided value. It will not lengthen any lists
     *
     * @param rows Max amount of rows to be accepted
     * @return this
     */
    public TextTable pruneRows(int rows) {
        if (rows < 0) throw new IllegalArgumentException("Rows must be non-negative! Was " + rows);
        for (HashMap<Integer, String> t : data) {
            for (Integer i : t.keySet()) {
                if (i >= rows) t.remove(i);
            }
        }
        return this;
    }

    /**
     * Returns the amount of columns in the TextTable
     *
     * @return the amount of columns
     */
    public int columns() {
        return data.size();
    }

    /**
     * Returns the amount of rows in the TextTable
     *
     * @return the amount of rows
     */
    public int rows() {
        return highestIndex(data);
    }

    /**
     * Generates the text representing the table, according to set rules
     *
     * @return the TextTable as a (probably) multiline string
     */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(); //main string builder
        int l = highestIndex(data);
        for (int c = 0; c <= l; c++) {
            StringBuilder rb = new StringBuilder(); //row builder
            for (HashMap<Integer, String> col : data) {
                String item = col.getOrDefault(c, "");
                StringBuilder bb = new StringBuilder(item);
                int colwidth = widest(col);
                while (bb.length() < colwidth) bb.append(filler); //fill until its wide enough
                rb.append(bb);
                rb.append(columnsplitter);
            }
            sb.append(rb);
            if (c != l) sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Checks if two TextTables' toString() matches, and if they have the same dynamic mode, column splitter and filler
     *
     * @param t Target table
     * @return true if their toString() results matches and their properties are equal
     */

    public boolean equals(TextTable t) {
        return t.toString().equals(this.toString())
                && t.getDynamic() == dynamic
                && t.getColumnsplitter().equals(columnsplitter)
                && t.getFiller() == filler;
    }
}

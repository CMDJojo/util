package com.cmdjojo.util;

import static com.cmdjojo.util.Kbdx.msToString;

import java.util.*;

/**
 * Can compare time, set timers etc. Depends on Kbdx class (also by CMDJojo)
 *
 * @author CMDJojo (Jonathan Widen)
 * @version 1.2-SNAPSHOT
 */

public final class Stopwatch implements Iterable<String> {

    /**
     * Default pattern, if no pattern supplied
     */

    private static final String defaultPattern = "%A% => %B% took %C%";
    private HashMap<String, Long> timers = new HashMap<>();
    private ArrayList<String> keys = new ArrayList<>();

    @Override
    public Iterator<String> iterator() {
        return keys.iterator();
    }

    /**
     * Removes a timer by the name, if it exists
     *
     * @param name Name of the timer
     */

    public void remove(String name) {
        this.timers.remove(name);
        this.keys.remove(name);
    }

    /**
     * Sets a timer with name and current time as value
     *
     * @param name Name of the timer
     */
    public void set(String name) {
        this.set(name, new Date().getTime());
    }

    /**
     * Sets a timer with name and supplied date as value
     *
     * @param name Name of the timer
     * @param date Date to set as value
     */

    public void set(String name, Date date) {
        this.set(name, date.getTime());
    }

    /**
     * Sets a timer with name and supplied time as value
     *
     * @param name Name of the timer
     * @param time Time to set as value (ms since Jan 1, 1970)
     */

    public void set(String name, long time) {
        if (time < 0)
            throw new IndexOutOfBoundsException("long value must be parsable to Date() object, time > 0");
        this.timers.put(name, time);
        this.keys.add(name);
    }

    /**
     * Gets a timer with name
     *
     * @param name Name of the timer
     * @return long with the timer value
     */

    public long get(String name) {
        if (!this.timers.containsKey(name))
            return 0;
        return this.timers.get(name);
    }

    /**
     * Checks if a timer has the specific value
     *
     * @param timer The timer to check
     * @param value The value to compare the timer to
     * @return <code>true</code> if timer exists and has the same value as <code>value</code>
     */

    public boolean matches(String timer, long value) {
        return get(timer) == value && timers.containsKey(timer);
    }

    /**
     * Checks if two timers have the same value (matches)
     *
     * @param timer1 The timer to check
     * @param timer2 The timer to compare to
     * @return <code>true</code> if both timers exists and have the same value
     */

    public boolean matches(String timer1, String timer2) {
        return get(timer1) == get(timer2) && timers.containsKey(timer1) && timers.containsKey(timer2);
    }


    /**
     * Compares between a timer and current time
     *
     * @param name Name of the timer
     * @return long with the time difference (in ms)
     */

    public long compareMs(String name) {
        return this.compareMs(this.get(name), new Date().getTime());
    }

    /**
     * Compares between two timers
     *
     * @param name1 Name of the first timer
     * @param name2 Name of the second timer
     * @return long with the time difference (in ms)
     */

    public long compareMs(String name1, String name2) {
        return this.compareMs(this.get(name1), this.get(name2));
    }

    /**
     * Compares between a timer and a date
     *
     * @param name Name of the timer
     * @param date Date to compare with
     * @return long with the time difference (in ms)
     */

    public long compareMs(String name, Date date) {
        return this.compareMs(this.get(name), date.getTime());
    }

    /**
     * Compares between a timer and a value
     *
     * @param name Name of the timer
     * @param time long with value (ms since Jan 1, 1970)
     * @return long with the time difference (in ms)
     */

    public long compareMs(String name, long time) {
        return this.compareMs(this.get(name), time);
    }

    /**
     * Compares between two values (ms since Jan 1, 1970)
     *
     * @param time1 The first value
     * @param time2 The second value
     * @return long with the time difference (in ms)
     */

    public long compareMs(long time1, long time2) {
        if (time1 > time2)
            return time1 - time2;
        return time2 - time1;
    }

    /**
     * Compares between a timer and current time
     *
     * @param name Name of the timer
     * @return String with the time difference
     */

    public String compare(String name) {
        return this.compare(this.get(name), new Date().getTime());
    }

    /**
     * Compares between a timer and current time
     *
     * @param name Name of the timer
     * @param args Amount of "arguments" in answer
     * @return String with the time difference
     */

    public String compare(String name, int args) {
        return this.compare(this.get(name), new Date().getTime(), args);
    }

    /**
     * Compares between two timers
     *
     * @param name1 Name of the first timer
     * @param name2 Name of the second timer
     * @return String with the time difference
     */

    public String compare(String name1, String name2) {
        return this.compare(this.get(name1), this.get(name2));
    }

    /**
     * Compares between two timers
     *
     * @param name1 Name of the first timer
     * @param name2 Name of the second timer
     * @param args  Amount of "arguments" in answer
     * @return String with the time difference
     */

    public String compare(String name1, String name2, int args) {
        return this.compare(this.get(name1), this.get(name2), args);
    }

    /**
     * Compares between a timer and a date
     *
     * @param name Name of the timer
     * @param date Date to compare with
     * @return String with the time difference
     */

    public String compare(String name, Date date) {
        return this.compare(this.get(name), date.getTime());
    }

    /**
     * Compares between a timer and a date
     *
     * @param name Name of the timer
     * @param date Date to compare with
     * @param args Amount of "arguments" in answer
     * @return String with the time difference
     */

    public String compare(String name, Date date, int args) {
        return this.compare(this.get(name), date.getTime(), args);
    }

    /**
     * Compares between a timer and a value
     *
     * @param name Name of the timer
     * @param time long with value (ms since Jan 1, 1970)
     * @return String with the time difference
     */

    public String compare(String name, long time) {
        return this.compare(this.get(name), time);
    }

    /**
     * Compares between a timer and a value
     *
     * @param name Name of the timer
     * @param time long with value (ms since Jan 1, 1970)
     * @param args Amount of "arguments" in answer
     * @return String with the time difference
     */

    public String compare(String name, long time, int args) {
        return this.compare(this.get(name), time, args);
    }

    /**
     * Compares between two values (ms since Jan 1, 1970)
     *
     * @param time1 The first value
     * @param time2 The second value
     * @return String with the time difference
     */

    public String compare(long time1, long time2) {
        return this.compare(time1, time2, -1);
    }

    /**
     * Compares between two values (ms since Jan 1, 1970)
     *
     * @param time1 The first value
     * @param time2 The second value
     * @param args  Amount of "arguments" in string
     * @return String with the time difference
     */

    public String compare(long time1, long time2, int args) {
        return msToString(this.compareMs(time1, time2), args);
    }

    /**
     * Returns the index of a key (or -1 if not defined)
     *
     * @param name Key
     * @return The index of the key
     */
    public int index(String name) {
        return keys.indexOf(name);
    }

    /**
     * Returns the next key (or null if undefined)
     *
     * @param name The name of the key
     * @return Key name
     */
    public String getNext(String name) {
        return getNext(index(name));
    }

    /**
     * Returns the next key (or null if undefined)
     *
     * @param index The index of the key
     * @return Key name
     */
    public String getNext(int index) {
        if ((index + 1) < keys.size() && index != -1)
            return keys.get(index + 1);
        return null;
    }

    /**
     * Compares a key with the next key, and their values
     *
     * @param name Key
     * @return Comparasion between the key value and next value
     */
    public String compareNext(String name) {
        return compareNext(name, 0);
    }

    /**
     * Compares a key with the next key, and their values
     *
     * @param name Key
     * @param args The amount of "arguments" in the answer
     * @return Comparasion between the key value and next value
     */
    public String compareNext(String name, int args) {
        return compareNext(name, args, defaultPattern);
    }

    /**
     * Compares a key with the next key, and their values
     *
     * @param name    Key
     * @param args    The amount of "arguments" in the answer
     * @param pattern A pattern for the result display, %A% for the older time, %B%
     *                for the newer time, %C% for the result (all variables are
     *                optional)
     * @return Comparasion between the key value and next value
     */
    public String compareNext(String name, int args, String pattern) {
        String next = getNext(name);
        if (next == null)
            return "";
        return pattern.replace("%A%", name).replace("%B%", next).replace("%C%", compare(name, next, args));
    }

    /**
     * Compares each timer with the next one
     *
     * @return A string containing comparasions for all keys
     */
    public String compareNextAll() {
        return this.compareNextAll(0);
    }

    /**
     * Compares each timer with the next one
     *
     * @param args Amount of "arguments" in return text
     * @return A string containing comparasions for all keys
     */

    public String compareNextAll(int args) {
        return compareNextAll(args, defaultPattern);
    }

    /**
     * Compares each timer with the next one
     *
     * @param args    Amount of "arguments" in return text
     * @param pattern A pattern for the result display, %A% for the older time, %B%
     *                for the newer time, %C% for the result
     * @return A string containing comparasions for all keys
     */

    public String compareNextAll(int args, String pattern) {
        StringBuilder ret = new StringBuilder();
        for (String key : keys) {
            ret.append(compareNext(key, args, pattern)).append("\n");
        }
        return ret.toString().substring(0, ret.length() - 2);
    }

}

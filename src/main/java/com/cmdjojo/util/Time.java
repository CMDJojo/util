package com.cmdjojo.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Time {
    private long millis;
    private long offset;

    /**
     * Tries to create a new {@code Time} object by parsing the supplied string
     * <p>For examples on valid and invalid strings, see {@linkplain #parse(String)}
     *
     * @param time The time string to parse
     * @see #parse(String)
     */
    public Time(@NotNull String time) {
        millis = parse(time);
    }

    /**
     * Creates a new {@code Time} object, without any time set
     */
    public Time() {
        this(0);
    }

    /**
     * Creates a new {@code Time} object, with the time set to the argument provided
     *
     * @param millis The amount of milliseconds to set the time to
     */
    public Time(long millis) {
        this.millis = millis;
    }

    /**
     * Creates a new {@code Time} object, with the time set to the argument provided
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public Time(long amount, Unit unit) {
        this(amount * unit.scale);
    }

    /**
     * Creates a new {@code Time} object, with the time set to the argument provided
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public Time(double amount, Unit unit) {
        this((long) (amount * unit.scale));
    }

    /**
     * Creates a new {@code Time} object, setting the time to the sum of all {@code Time} objects supplied
     *
     * @param parts The {@code Time} objects used to calculate the current time
     */
    public Time(@Nullable Time... parts) {
        this(0);
        ArrayUtil.consume(parts, this::add, false);
    }

    /**
     * Parses the inputed time string and calculates the amount of milliseconds that time period will take.
     * <p>A time string is built up by number-unit pairs.
     * <p>A number can be a long, like {@code 563}, or a double, like {@code 4.5}. Commas will count as dots when parsing doubles.
     * <p>An unit can be any {@link Unit#values() Unit} name, non-case sensitive.
     * <p>The matcher tries to find a number, and thereafter it treats the next word like the unit. This is done repeatedly as long as there is matches.
     * <p>If the unit doesn't match any {@linkplain Unit} name, an {@linkplain IllegalArgumentException} is thrown.
     * <p>Text between the end of one pair and the start of another is ignored. Thus, strings like {@code 4 seconds and 5 days} will successfully parse.
     *
     * @param s The string
     * @return The amount of milliseconds for the time period the string describes
     * @throws IllegalArgumentException if the string couldn't be parsed
     */
    @Contract(pure = true)
    public static long parse(@NotNull String s) {
        long result = 0;
        Pattern pattern = Pattern.compile("([0-9\\s]*[0-9](?:[,.][0-9\\s]*[0-9])?)\\s*([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            Unit unit = parseUnit(matcher.group(2));
            String numstr = matcher.group(1)
                    .replaceAll("\\s", "")
                    .replace(",", ".");
            try {
                long num = Long.parseLong(numstr);
                result += num * unit.scale;
            } catch (NumberFormatException e) {
                try {
                    double num = Double.parseDouble(numstr);
                    result += (int) (num * unit.scale);
                } catch (NumberFormatException f) {
                    throw new IllegalArgumentException("Unknown number " + matcher.group(1));
                }
            }
        }
        return result;
    }

    /**
     * Parses a string into an unit, throwing an {@linkplain IllegalArgumentException} if no unit found
     *
     * @param s The string
     * @return An unit corresponding to the string
     * @throws IllegalArgumentException if no unit matches
     */
    @NotNull
    @Contract(pure = true)
    private static Unit parseUnit(@NotNull String s) {
        for (Unit u : Unit.values()) {
            if (s.equalsIgnoreCase(u.name())) return u;
        }
        throw new IllegalArgumentException("Unknown unit " + s);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} units
     *
     * @param n The amount of units
     * @param u The unit
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static Time from(int n, @NotNull Unit u) {
        return new Time(n, u);
    }

    /**
     * Creates a new {@code Time} object with the current time set to the amount of milliseconds since 1st of January 1970
     *
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    public static Time now() {
        return new Time(new Date().getTime());
    }

    /**
     * Creates a new {@code Time} object with the current time AND the offset set to the amount of milliseconds since 1st of January 1970
     * <p>Effectivly, that object will have its time set to 0, but if you use {@link #set(Time) #set(Time.now())} in, for example a stopwatch,
     * it will have its time equal to the time passed from object creation to that method call.
     *
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "-> new", pure = true)
    public static Time nowOffset() {
        Time t = now();
        t.offset(t);
        return t;
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} milliseconds
     *
     * @param n The amount of milliseconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time milliseconds(long n) {
        return new Time(n, Unit.MILLISECONDS);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} milliseconds
     *
     * @param n The amount of milliseconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time millis(long n) {
        return milliseconds(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} milliseconds
     *
     * @param n The amount of milliseconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time ms(long n) {
        return milliseconds(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} seconds
     *
     * @param n The amount of seconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time seconds(long n) {
        return new Time(n, Unit.SECONDS);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} seconds
     *
     * @param n The amount of seconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time s(long n) {
        return seconds(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} minutes
     *
     * @param n The amount of minutes
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time minutes(long n) {
        return new Time(n, Unit.MINUTES);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} minutes
     *
     * @param n The amount of minutes
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time m(long n) {
        return minutes(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} hours
     *
     * @param n The amount of hours
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time hours(long n) {
        return new Time(n, Unit.HOURS);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} hours
     *
     * @param n The amount of hours
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time h(long n) {
        return hours(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} days
     *
     * @param n The amount of days
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time days(long n) {
        return new Time(n, Unit.DAYS);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} days
     *
     * @param n The amount of days
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time d(long n) {
        return days(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} milliseconds
     *
     * @param n The amount of milliseconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time milliseconds(double n) {
        return new Time(n, Unit.MILLISECONDS);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} milliseconds
     *
     * @param n The amount of milliseconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time millis(double n) {
        return milliseconds(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} milliseconds
     *
     * @param n The amount of milliseconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time ms(double n) {
        return milliseconds(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} seconds
     *
     * @param n The amount of seconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time seconds(double n) {
        return new Time(n, Unit.SECONDS);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} seconds
     *
     * @param n The amount of seconds
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time s(double n) {
        return seconds(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} minutes
     *
     * @param n The amount of minutes
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time minutes(double n) {
        return new Time(n, Unit.MINUTES);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} minutes
     *
     * @param n The amount of minutes
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time m(double n) {
        return minutes(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} hours
     *
     * @param n The amount of hours
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time hours(double n) {
        return new Time(n, Unit.HOURS);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} hours
     *
     * @param n The amount of hours
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time h(double n) {
        return hours(n);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} days
     *
     * @param n The amount of days
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time days(double n) {
        return new Time(n, Unit.DAYS);
    }

    /**
     * Creates a new {@code Time} object with the current time set to {@code n} days
     *
     * @param n The amount of days
     * @return the newly created {@code Time} object
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static Time d(double n) {
        return days(n);
    }

    public static void main(String[] args) {
        System.out.println(hours(1.372).toString(Unit.MS.scale_id, Unit.MIN.scale_id, -1, true, false, null, null));
    }

    /**
     * Parses the string using {@linkplain #parse(String)} and adds that time to this object
     *
     * @param s The string
     * @throws IllegalArgumentException if the string doesn't parse
     * @see #parse(String)
     */
    public void add(@NotNull String s) {
        add(parse(s));
    }

    /**
     * Adds the milliseconds from the supplied {@code Time} object to this object
     *
     * @param t The Time object
     */
    public void add(@NotNull Time t) {
        add(t.calculateMillis());
    }

    /**
     * Adds the time from the argument provided to this object
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public void add(long amount, Unit unit) {
        add(amount * unit.scale);
    }

    /**
     * Adds the time from the arguments provided to this object
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public void add(double amount, Unit unit) {
        add((long) (amount * unit.scale));
    }

    /**
     * Adds the time supplied to this object
     *
     * @param millis The amount of milliseconds to add
     */
    public void add(long millis) {
        this.millis += millis;
    }

    /**
     * Parses the string using {@linkplain #parse(String)} and subtracts that time from this object
     *
     * @param s The string
     * @throws IllegalArgumentException if the string doesn't parse
     * @see #parse(String)
     */
    public void subtract(@NotNull String s) {
        subtract(parse(s));
    }

    /**
     * Subtracts the milliseconds from the supplied {@code Time} object from this object
     *
     * @param t The Time object
     */
    public void subtract(@NotNull Time t) {
        subtract(t.calculateMillis());
    }

    /**
     * Subtracts the time from the arguments provided from this object
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public void subtract(long amount, Unit unit) {
        subtract(amount * unit.scale);
    }

    /**
     * Subtracts the time from the argument provided from this object
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public void subtract(double amount, Unit unit) {
        subtract((long) (amount * unit.scale));
    }

    /**
     * Subtracts the time supplied from this object
     *
     * @param millis The amount of milliseconds to add
     */
    public void subtract(long millis) {
        this.millis -= millis;
    }

    /**
     * Parses the time using {@linkplain #parse(String)} and sets the time of this object to that
     *
     * @param s The string
     * @throws IllegalArgumentException If the string doesn't parse
     * @see #parse(String)
     */
    public void set(@NotNull String s) {
        set(parse(s));
    }

    /**
     * Sets this object's time to the milliseconds of the supplied {@code Time} object
     *
     * @param t The Time object
     */
    public void set(@NotNull Time t) {
        set(t.calculateMillis());
    }

    /**
     * Sets this object's time to the arguments provided
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public void set(long amount, Unit unit) {
        set(amount * unit.scale);
    }

    /**
     * Sets this object's time to the arguments provided
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public void set(double amount, Unit unit) {
        set((long) (amount * unit.scale));
    }

    /**
     * Sets this object's time to the time supplied
     *
     * @param millis The amount of milliseconds to add
     */
    public void set(long millis) {
        this.millis = millis;
    }

    /**
     * Sets the time offset of this object to this object's current value
     */
    public void offset() {
        offset(millis);
    }

    /**
     * Parses the time using {@linkplain #parse(String)} and sets the offset of this object to that
     *
     * @param s The string
     * @throws IllegalArgumentException If the string doesn't parse
     * @see #parse(String)
     */
    public void offset(String s) {
        offset(parse(s));
    }

    /**
     * Sets this object's offset to the milliseconds of the supplied {@code Time} object
     *
     * @param t The Time object
     */
    public void offset(@NotNull Time t) {
        offset(t.calculateMillis());
    }

    /**
     * Sets this object's offset to the arguments provided
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public void offset(long amount, Unit unit) {
        offset(amount * unit.scale);
    }

    /**
     * Sets this object's offset to the arguments provided
     *
     * @param amount The amount of {@code unit}'s elapsed
     * @param unit   The unit {@code amount} is in
     */
    public void offset(double amount, Unit unit) {
        offset((long) (amount * unit.scale));
    }

    /**
     * Sets the offset to the supplied argument
     *
     * @param millis The amount of milliseconds to add
     */
    public void offset(long millis) {
        this.offset = millis;
    }

    /**
     * Calculates the amount of milliseconds the time period of this object is taken.
     * This takes {@code offset} into account
     *
     * @return the amound of milliseconds
     */
    public long calculateMillis() {
        return millis - offset;
    }

    /**
     * Returns this object's time in the unit supplied
     * <p>The value is rounded down if not a multiple of the unit supplied,
     * for example {@code Time.ms(1900).to(Unit.S)} will return {@code 1}
     *
     * @param unit The unit to present this object's time in
     * @return This object's time represented in the unit provided
     */
    @Contract(pure = true)
    public long to(@NotNull Unit unit) {
        return calculateMillis() / unit.scale;
    }

    /**
     * Returns this object's time in milliseconds
     *
     * @return This object's time represented in milliseconds
     */
    @Contract(pure = true)
    public long milliseconds() {
        return to(Unit.MILLISECONDS);
    }

    /**
     * Returns this object's time in milliseconds
     *
     * @return This object's time represented in milliseconds
     */
    @Contract(pure = true)
    public long millis() {
        return milliseconds();
    }

    /**
     * Returns this object's time in milliseconds
     *
     * @return This object's time represented in milliseconds
     */
    @Contract(pure = true)
    public long ms() {
        return milliseconds();
    }

    /**
     * Returns this object's time in seconds
     * <p>The value is rounded down
     *
     * @return This object's time represented in seconds
     */
    @Contract(pure = true)
    public long seconds() {
        return to(Unit.SECONDS);
    }

    /**
     * Returns this object's time in seconds
     * <p>The value is rounded down
     *
     * @return This object's time represented in seconds
     */
    @Contract(pure = true)
    public long s() {
        return seconds();
    }

    /**
     * Returns this object's time in minutes
     * <p>The value is rounded down
     *
     * @return This object's time represented in minutes
     */
    @Contract(pure = true)
    public long minutes() {
        return to(Unit.MINUTES);
    }

    /**
     * Returns this object's time in minutes
     * <p>The value is rounded down
     *
     * @return This object's time represented in minutes
     */
    @Contract(pure = true)
    public long m() {
        return minutes();
    }

    /**
     * Returns this object's time in hours
     * <p>The value is rounded down
     *
     * @return This object's time represented in hours
     */
    @Contract(pure = true)
    public long hours() {
        return to(Unit.HOURS);
    }

    /**
     * Returns this object's time in hours
     * <p>The value is rounded down
     *
     * @return This object's time represented in hours
     */
    @Contract(pure = true)
    public long h() {
        return hours();
    }

    /**
     * Returns this object's time in days
     * <p>The value is rounded down
     *
     * @return This object's time represented in days
     */
    @Contract(pure = true)
    public long days() {
        return to(Unit.DAYS);
    }

    /**
     * Returns this object's time in days
     * <p>The value is rounded down
     *
     * @return This object's time represented in days
     */
    @Contract(pure = true)
    public long d() {
        return days();
    }

    /**
     * Returns {@code true} if the supplied object's time matches this object's time.
     * <p>The {@code offset} values doesn't have to match
     *
     * @param o The object
     * @return {@code true} if the object is equal to {@code this}
     */
    @Contract(value = "null -> false", pure = true)
    public boolean equals(Object o) {
        if ((null == o) || (o.getClass() != Time.class))
            return false;
        Time obj = (Time) o;
        return obj.calculateMillis() == calculateMillis();
    }

    /**
     * Returns {@code true} if the supplied object's time matches this object's time.
     * <p>In contrast to {@linkplain #equals(Object)}, the {@code offset} values must match
     *
     * @param o The object
     * @return {@code true} if the object is equal to {@code this}
     */
    @Contract(value = "null -> false", pure = true)
    public boolean strictEquals(Object o) {
        if ((null == o) || (o.getClass() != Time.class))
            return false;
        Time obj = (Time) o;
        return obj.millis == millis &&
                obj.offset == offset;
    }

    /**
     * Creates a string representation of the time value stored in this objecct
     *
     * @return The created String
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    public String toString() {
        return toString(Unit.min_id(), Unit.max_id());
    }

    /**
     * Creates a string representation of the time value stored in this objecct
     *
     * @param min The scale_id of the smallest Unit to show
     * @param max The scale_id of the largest Unit to show
     * @return The created String
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public String toString(Unit min, Unit max) {
        return toString(min, max, -1, true, true);
    }

    /**
     * Creates a string representation of the time value stored in this objecct
     *
     * @param min            The scale_id of the smallest Unit to show
     * @param max            The scale_id of the largest Unit to show
     * @param elems          The max amount of elements to show. A negative value will show all elements
     * @param showEmpty      Show empty elements, like {@code 0ms}
     * @param useShortenings Use shortenings, like {@code 100ms}, instead of full unit names, like {@code 100 milliseconds}
     * @return The created String
     */
    @NotNull
    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    public String toString(Unit min, Unit max, int elems, boolean showEmpty, boolean useShortenings) {
        return toString(min, max, elems, showEmpty, useShortenings, null, null);
    }

    /**
     * Creates a string representation of the time value stored in this objecct
     *
     * @param min            The scale_id of the smallest Unit to show
     * @param max            The scale_id of the largest Unit to show
     * @param elems          The max amount of elements to show. A negative value will show all elements
     * @param showEmpty      Show empty elements, like {@code 0ms}
     * @param useShortenings Use shortenings, like {@code 100ms}, instead of full unit names, like {@code 100 milliseconds}
     * @param predelim       A string segment to insert between the number and the unit. Default: {@code ""} if using shortened units, {@code " "} otherwise. Use {@code null} for default
     * @param postdelim      A string segment to insert between all value-unit pairs. Default: {@code " "} if using shortened units, {@code ", "} otherwise. Use {@code null} for default
     * @return The created String
     */
    @NotNull
    @Contract(value = "_, _, _, _, _, _, _-> new", pure = true)
    public String toString(Unit min, Unit max, int elems, boolean showEmpty, boolean useShortenings, @Nullable String predelim, @Nullable String postdelim) {
        return toString(min.scale_id, max.scale_id, elems, showEmpty, useShortenings, predelim, postdelim);
    }

    /**
     * Creates a string representation of the time value stored in this objecct
     *
     * @param min The scale_id of the smallest Unit to show
     * @param max The scale_id of the largest Unit to show
     * @return The created String
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public String toString(int min, int max) {
        return toString(min, max, -1, true, true);
    }

    /**
     * Creates a string representation of the time value stored in this objecct
     *
     * @param min            The scale_id of the smallest Unit to show
     * @param max            The scale_id of the largest Unit to show
     * @param elems          The max amount of elements to show. A negative value will show all elements
     * @param showEmpty      Show empty elements, like {@code 0ms}
     * @param useShortenings Use shortenings, like {@code 100ms}, instead of full unit names, like {@code 100 milliseconds}
     * @return The created String
     */
    @NotNull
    @Contract(value = "_, _, _, _, _ -> new", pure = true)
    public String toString(int min, int max, int elems, boolean showEmpty, boolean useShortenings) {
        return toString(min, max, elems, showEmpty, useShortenings, null, null);
    }

    /**
     * Creates a string representation of the time value stored in this objecct
     *
     * @param min            The scale_id of the smallest Unit to show
     * @param max            The scale_id of the largest Unit to show
     * @param elems          The max amount of elements to show. A negative value will show all elements
     * @param showEmpty      Show empty elements, like {@code 0ms}
     * @param useShortenings Use shortenings, like {@code 100ms}, instead of full unit names, like {@code 100 milliseconds}
     * @param predelim       A string segment to insert between the number and the unit. Default: {@code ""} if using shortened units, {@code " "} otherwise. Use {@code null} for default
     * @param postdelim      A string segment to insert between all value-unit pairs. Default: {@code " "} if using shortened units, {@code ", "} otherwise. Use {@code null} for default
     * @return The created String
     */
    @NotNull
    @Contract(value = "_, _, _, _, _, _, _-> new", pure = true)
    public String toString(int min, int max, int elems, boolean showEmpty, boolean useShortenings, @Nullable String predelim, @Nullable String postdelim) {
        if (min > max) throw new IllegalArgumentException("min > max");
        if (elems == 0) throw new IllegalArgumentException("param 'elems' can't be 0");
        int elems_added = 0;
        long ms = calculateMillis();
        if (predelim == null) predelim = useShortenings ? "" : " ";
        if (postdelim == null) postdelim = useShortenings ? " " : ", ";
        StringBuilder sb = new StringBuilder();
        for (int i = max; i >= min && elems_added != elems; i--) {
            Unit u = Unit.get(i);
            if (u == null) continue;
            long num = ms / u.scale;
            ms %= u.scale;
            if (num != 0 || showEmpty || (u.scale_id == min && sb.length() == 0)) {
                if (sb.length() != 0) sb.append(postdelim);
                sb.append(num);
                sb.append(predelim);
                if (useShortenings) sb.append(u.shortening);
                else {
                    if (num == 1 || num == -1) sb.append(u.singular);
                    else sb.append(u.plural);
                }
                elems_added++;
            }
        }
        return sb.toString();
    }

    /**
     * All available units for conversions
     * <p>This can be used for adding, subtracting, setting
     * and getting values from a {@linkplain Time} object
     * <p>The names on these enums are also the exact names
     * accepted as units in strings when parsing
     */
    public enum Unit {
        /**
         * Ground unit with scale 1 and scale_id 0
         */
        MILLISECONDS(1, 0,
                "ms",
                "millisecond",
                "milliseconds"),
        /**
         * Alias for {@linkplain #MILLISECONDS}
         */
        MILLISECOND(MILLISECONDS),
        /**
         * Alias for {@linkplain #MILLISECONDS}
         */
        MILLIS(MILLISECONDS),
        /**
         * Alias for {@linkplain #MILLISECONDS}
         */
        MS(MILLISECONDS),
        /**
         * Scaled as 1000 {@linkplain #MILLISECONDS}
         */
        SECONDS(MILLISECONDS, 1000,
                "s",
                "second",
                "seconds"),
        /**
         * Alias for {@linkplain #SECONDS}
         */
        SECOND(SECONDS),
        /**
         * Alias for {@linkplain #SECONDS}
         */
        SEC(SECONDS),
        /**
         * Alias for {@linkplain #SECONDS}
         */
        S(SECONDS),
        /**
         * Scaled as 60 {@linkplain #SECONDS}
         */
        MINUTES(SECONDS, 60,
                "m",
                "minute",
                "minutes"),
        /**
         * Alias for {@linkplain #MINUTES}
         */
        MINUTE(MINUTES),
        /**
         * Alias for {@linkplain #MINUTES}
         */
        MIN(MINUTES),
        /**
         * Alias for {@linkplain #MINUTES}
         */
        M(MINUTES),
        /**
         * Scaled as 60 {@linkplain #MINUTES}
         */
        HOURS(MINUTES, 60,
                "h",
                "hour",
                "hours"),
        /**
         * Alias for {@linkplain #HOURS}
         */
        HOUR(HOURS),
        /**
         * Alias for {@linkplain #HOURS}
         */
        H(HOURS),
        /**
         * Scaled as 24 {@linkplain #HOURS}
         */
        DAYS(HOURS, 24,
                "d",
                "day",
                "days"),
        /**
         * Alias for {@linkplain #DAYS}
         */
        DAY(DAYS),
        /**
         * Alias for {@linkplain #DAYS}
         */
        D(DAYS);

        /**
         * Amount of millseconds for one of this unit to pass
         */
        private final long scale;
        /**
         * The ID of this Unit. Two Units with the same scale should have the same scale_id
         */
        private final int scale_id;
        /**
         * The Unit strings of this Unit, in short form (ms), singular (millisecond) and plural (milliseconds)
         */
        private final String shortening;
        private final String singular;
        private final String plural;

        /**
         * Makes this unit a copy of the provided one, but with the scale amplified
         * and the scale_id increased by 1
         *
         * @param base      The original Unit
         * @param amplifier The amplification scale
         */
        Unit(Unit base, long amplifier, String shortening, String singular, String plural) {
            this(base.scale * amplifier,
                    base.scale_id + 1,
                    shortening,
                    singular,
                    plural);
        }

        /**
         * Makes this unit a copy of the provided one, keeping the scale,
         * scale_id, and strings, without changing the provided one
         *
         * @param copy The original Unit
         */
        Unit(Unit copy) {
            this(copy.scale,
                    copy.scale_id,
                    copy.shortening,
                    copy.singular,
                    copy.plural);
        }

        /**
         * Makes this unit with the provided scale and scale_id
         *
         * @param scale The scale
         */
        Unit(long scale, int scale_id, @NotNull String shortening, @NotNull String singular, @NotNull String plural) {
            this.scale = scale;
            this.scale_id = scale_id;
            this.shortening = shortening;
            this.singular = singular;
            this.plural = plural;
        }

        /**
         * Returns the highest scale_id
         *
         * @return the highest scale_id
         */
        @Contract(pure = true)
        public static int max_id() {
            int max = 0;
            for (Unit u : values()) if (u.scale_id > max) max = u.scale_id;
            return max;
        }

        /**
         * Returns the lowest scale_id
         *
         * @return the lowest scale_id
         */
        @Contract(pure = true)
        public static int min_id() {
            int min = max_id();
            for (Unit u : values()) if (u.scale_id < min) min = u.scale_id;
            return min;
        }

        /**
         * Returns the Unit with the provided id
         *
         * @param scale_id The id of the Unit
         * @return the Unit with the provided id
         */
        @Nullable
        public static Unit get(int scale_id) {
            for (Unit u : values()) if (u.scale_id == scale_id) return u;
            return null;
        }

        /**
         * Returns the scale of this Unit
         *
         * @return the scale of this Unit
         */
        @Contract(pure = true)
        public long getScale() {
            return scale;
        }

        /**
         * Returns the scale_id of this Unit
         *
         * @return the scale_id of this Unit
         */
        @Contract(pure = true)
        public int getScale_id() {
            return scale_id;
        }

        /**
         * Returns the shortening string of this Unit
         *
         * @return the shortening string of this Unit
         */
        @NotNull
        @Contract(pure = true)
        public String getShortening() {
            return shortening;
        }

        /**
         * Returns the singular string of this Unit
         *
         * @return the singular string of this Unit
         */
        @NotNull
        @Contract(pure = true)
        public String getSingular() {
            return singular;
        }

        /**
         * Returns the plural string of this Unit
         *
         * @return the plural string of this Unit
         */
        @NotNull
        @Contract(pure = true)
        public String getPlural() {
            return plural;
        }
    }
}

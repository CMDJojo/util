package com.cmdjojo.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Objects;
import java.util.function.*;


/**
 * This class contains some useful utilities for arrays, like filtering, transformation, denullification, and enboxing/unboxing of primitive type arrays
 * <p>It does only contain static methods, and cannot be instantiated
 *
 * @author CMDJojo
 * @version 1.0
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public final class ArrayUtil {
    /**
     * This is a static-only class, and cannot be instantiated
     */
    private ArrayUtil() {
    }

    /**
     * Finds the class of the data type of the array supplied
     *
     * @param c The array
     * @return The class of the data type of the array supplied, or {@code null} if the array only contains {@code null} values
     */
    @Nullable
    private static Class<?> findClass(Object[] c) {
        for (Object o : c)
            if (o != null) return o.getClass();
        return null;
    }

    /**
     * Finds the class of the data type of the array supplied, and throws an error if none is found
     * <p>Depends on, an is almost the same as, {@link #findClass(Object[]) findClass}
     *
     * @param c The array
     * @return The class of the data type of the array supplied
     * @throws IllegalArgumentException if the array only contains {@code null} elements
     */
    @NotNull
    private static Class<?> assureClass(@NotNull Object[] c) {
        Class<?> r = findClass(c);
        if (r == null)
            throw new IllegalArgumentException("Could not find class; make sure array contains at least one non-null element");
        return r;
    }

    /**
     * Returns a random element from the array
     * <p>If the array is empty, {@code null} will be returned
     *
     * @param data The array
     * @param <T>  The data type of the array
     * @return A random element from the array
     */
    @Nullable
    @Contract(value = "null -> fail", pure = true)
    public static <T> T random(T[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        if (data.length == 0) return null;
        return data[(int) (Math.random() * data.length)];
    }

    /**
     * Returns a random element from the array
     * <p>If the array is empty, {@code null} will be returned
     *
     * @param data     The array
     * @param skipNull Wether to skip {@code null} values
     * @param <T>      The data type of the array
     * @return A random element from the array
     */
    @Nullable
    @Contract(value = "null, _ -> fail; !null, true -> !null", pure = true)
    public static <T> T random(T[] data, boolean skipNull) {
        return skipNull ? random(denullify(data)) :
                random(data);
    }

    /**
     * Returns the first non-{@code null} element of the array
     *
     * @param data the array
     * @param <T>  the data type of the array
     * @return the first non-{@code null} element of the array
     */
    @Nullable
    @Contract(value = "null -> fail; _ -> _", pure = true)
    public static <T> T first(T[] data) {
        return first(data, Objects::nonNull);
    }

    /**
     * Returns the first element of the array matching the filter
     *
     * @param data   the array
     * @param filter the filter
     * @param <T>    the data type of the array and filter
     * @return the first element of the array matching the filter
     */
    @Nullable
    @Contract(value = "null, _-> fail; _, _ -> _", pure = true)
    public static <T> T first(T[] data, @NotNull Predicate<T> filter) {
        if (data == null) throw new IllegalArgumentException("null");
        for (T o : data)
            if (filter.test(o)) return o;
        return null;
    }

    /**
     * Returns the last non-{@code null} element of the array
     *
     * @param data the array
     * @param <T>  the data type of the array
     * @return the last non-{@code null} element of the array
     */
    @Nullable
    @Contract(value = "null -> fail; _ -> _", pure = true)
    public static <T> T last(T[] data) {
        return last(data, Objects::nonNull);
    }

    /**
     * Returns the last element of the array matching the filter
     *
     * @param data   the array
     * @param filter the filter
     * @param <T>    the data type of the array and filter
     * @return the last element of the array matching the filter
     */
    @Nullable
    @Contract(value = "null, _-> fail; _, _ -> _", pure = true)
    public static <T> T last(T[] data, Predicate<T> filter) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = data.length - 1; i >= 0; i++)
            if (filter.test(data[i])) return data[i];
        return null;
    }

    /**
     * Filters the array, returning a new array holding the same data type,
     * but containing only the elements allowed by the filter
     *
     * <p>This method will run all elements of the array through the filter, including {@code null} if present
     * <p>Each element will run through the filter in order one time, and one time only. The filter doesn't need to have the same result when run multiple times
     * <p>If the filter denies all elements, an empty array is returned rather than {@code null}
     *
     * @param c      The class of the elements
     * @param data   The array to be processed
     * @param filter The filter to filter the array elements through
     * @param <T>    The data type of the class, array and filter
     * @return An array containing all elements accepted by the filter
     * @see Predicate
     * @deprecated Use {@link #filter(Object[], Predicate) the overloaded method} instead
     * since this method depended on a workaround for class finding, which the overloaded method doesn't
     */
    @NotNull
    @Contract(value = "_, null, _ -> fail; _, _, _ -> new", pure = true)
    @Deprecated
    public static <T> T[] filter(@NotNull Class<T> c, T[] data, @NotNull Predicate<T> filter) {
        if (data == null) throw new IllegalArgumentException("null");
        return uc_filter(c, data, filter);
    }

    /**
     * Filters the array, returning a new array holding the same data type,
     * but containing only the elements allowed by the filter
     *
     * <p>This method will run all elements of the array through the filter, including {@code null} if present
     * <p>Each element will run through the filter in order one time, and one time only. The filter doesn't need to have the same result when run multiple times
     * <p>If the filter denies all elements, an empty array is returned rather than {@code null}
     * <p>This method uses class-finding, meaning that there must be at least one non-{@code null} element in the array
     * If you, for some reason, don't want to use class-finding, see {@link #filter(Class, Object[], Predicate)} this method} where the {@code class} is a parameter
     *
     * @param data   The array to be processed
     * @param filter The filter to filter the array elements through
     * @param <T>    The data type of the class, array and filter
     * @return An array containing all elements accepted by the filter
     * @throws IllegalArgumentException if {@code data} contains only null or has the length of 0
     * @see Predicate
     */
    @NotNull
    @Contract(value = "_, null -> fail; _, _ -> new", pure = true)
    public static <T> T[] filter(T[] data, @NotNull Predicate<T> filter) {
        if (data == null) throw new IllegalArgumentException("null");
        return uc_filter(data.getClass().getComponentType(), data, filter);
    }

    // performs the filtering
    @NotNull
    @Contract(value = "_, null, _ -> fail; _, _, _ -> new", pure = true)
    @SuppressWarnings("unchecked")
    private static <T> T[] uc_filter(@NotNull Class<?> c, T[] data, @NotNull Predicate<T> filter) {
        boolean[] fr = new boolean[data.length]; // filter results
        int i1 = 0; // index of boolean array when filtering
        int n = 0; // number of elements accepted
        for (T o : data)
            if (filter.test(o)) {
                n++;
                fr[i1++] = true;
            } else fr[i1++] = false;

        T[] res = (T[]) Array.newInstance(c, n);

        int i2 = 0; // index of new array
        for (int i = 0; i < fr.length && i2 < n; i++)
            if (fr[i])
                res[i2++] = data[i];
        return res;
    }

    /**
     * Filters the array, returning a new array holding the same data type,
     * but containing only the elements allowed by the filter
     *
     * <p>This method will run all elements of the array through the filter, including {@code null} if present
     * <p>Each element will run through the filter in order one time, and one time only. The filter doesn't need to have the same result when run multiple times
     * <p>If the filter denies all elements, an empty array is returned rather than {@code null}
     *
     * @param c      The class of the elements
     * @param data   The array to be processed
     * @param filter The filter to filter the array elements through
     * @param <T>    The data type of the class, array and filter
     * @return An array containing all elements accepted by the filter
     * @see BiPredicate
     * @deprecated Use {@link #arrayFilter(Object[], BiPredicate) the overloaded method} instead
     * since this method depended on a workaround for class finding, which the overloaded method doesn't
     */
    @NotNull
    @Contract(value = "_, null, _ -> fail; _, _, _ -> new", pure = true)
    @Deprecated
    public static <T> T[] arrayFilter(@NotNull Class<T> c, T[] data, @NotNull BiPredicate<Integer, T> filter) {
        if (data == null) throw new IllegalArgumentException("null");
        return uc_arrayFilter(c, data, filter);
    }

    /**
     * Filters the array, returning a new array holding the same data type,
     * but containing only the elements allowed by the filter
     *
     * <p>This method will run all elements of the array through the filter, including {@code null} if present
     * <p>Each element will run through the filter in order one time, and one time only. The filter doesn't need to have the same result when run multiple times
     * <p>If the filter denies all elements, an empty array is returned rather than {@code null}
     * <p>This method uses class-finding, meaning that there must be at least one non-{@code null} element in the array
     * If you, for some reason, don't want to use class-finding, see {@link #arrayFilter(Class, Object[], BiPredicate)} this method} where the {@code class} is a parameter
     *
     * @param data   The array to be processed
     * @param filter The filter to filter the array elements through
     * @param <T>    The data type of the class, array and filter
     * @return An array containing all elements accepted by the filter
     * @throws IllegalArgumentException if {@code data} contains only null or has the length of 0
     * @see BiPredicate
     */
    @NotNull
    @Contract(value = "_, null -> fail; _, _ -> new", pure = true)
    public static <T> T[] arrayFilter(T[] data, @NotNull BiPredicate<Integer, T> filter) {
        if (data == null) throw new IllegalArgumentException("null");
        return uc_arrayFilter(data.getClass().getComponentType(), data, filter);
        //return uc_arrayFilter(assureClass(data), data, filter);
    }

    // performs the filtering
    @NotNull
    @Contract(value = "_, null, _ -> fail; _, _, _ -> new", pure = true)
    @SuppressWarnings("unchecked")
    private static <T> T[] uc_arrayFilter(@NotNull Class<?> c, T[] data, @NotNull BiPredicate<Integer, T> filter) {
        boolean[] fr = new boolean[data.length]; // filter results
        int i1 = -1; // index of boolean array when filtering
        int n = 0; // number of elements accepted
        for (T o : data)
            if (filter.test(++i1, o)) {
                n++;
                fr[i1] = true;
            } else fr[i1] = false;

        T[] res = (T[]) Array.newInstance(c, n);

        int i2 = 0; // index of new array
        for (int i = 0; i < fr.length && i2 < n; i++)
            if (fr[i])
                res[i2++] = data[i];
        return res;
    }

    /**
     * Transforms the array from one data type to another
     * <p>This can be useful if you have one array of objects, and you want one array of the result of the objects' {@code toString()} method, for example
     * <p>All elements will be passed through the transformer once and only once, in order. {@code Null} elements will also be passed
     * <p>The transformer may return {@code null}, but it has to return a non-{@code null} value at least once
     * <p>If you don't want {@code null}'s in the array see {@link #denullify(Object[]) the denullify method}
     *
     * @param data        The array to be processed
     * @param transformer The transformer to be used
     * @param <T>         The data type of the input array elements
     * @param <R>         The data type of the transformed array elements
     * @return An array of the data type R, containing the results gotten when running the input array elements through the transformer
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static <T, R> R[] transform(T[] data, @NotNull Function<T, R> transformer) {
        if (data == null) throw new IllegalArgumentException("null");
        R o = null;
        int i;
        for (i = 0; i < data.length && o == null; i++)
            o = transformer.apply(data[i]);
        if (o == null)
            throw new IllegalArgumentException("Could not find class; make sure transformer returns at least one non-null element");
        return uc_transform(o.getClass(), data, transformer, i - 1, o);
    }

    /**
     * Transforms the array from one data type to another
     * <p>This can be useful if you have one array of objects, and you want one array of the result of the objects' {@code toString()} method, for example
     * <p>All elements will be passed through the transformer once and only once, in order. {@code Null} elements will also be passed
     * <p>The transformer may return {@code null}
     * <p>If you don't want {@code null}'s in the array see {@link #denullify(Object[]) the denullify method}
     *
     * @param c           The class of the elements
     * @param data        The array to be processed
     * @param transformer The transformer to be used
     * @param <T>         The data type of the input array elements
     * @param <R>         The data type of the transformed array elements
     * @return An array of the data type R, containing the results gotten when running the input array elements through the transformer
     */
    @NotNull
    @Contract(value = "_, null, _ -> fail; _, _, _ -> new", pure = true)
    public static <T, R> R[] transform(@NotNull Class<R> c, T[] data, @NotNull Function<T, R> transformer) {
        if (data == null) throw new IllegalArgumentException("null");
        return uc_transform(c, data, transformer, -1, null);
    }

    /**
     * Performs the transform
     * <p>An array is created with the data type {@code c}, which should be the same as {@code <R>}
     * <p>Then, it is casted from {@code c[]} to {@code R[]}, which should be fine
     * <p>This cast is classed as unchecked, but since this is a strictly controlled private method,
     * it should be fine, thus unchecked warnings are ignored
     *
     * @param c           The class of the elements
     * @param data        The array to be processed
     * @param transformer The transformer to be used
     * @param index       The index of the already transformed {@code element} in the array (or -1 if class was present)
     * @param element     An transformed element
     * @param <T>         The data type of the input array elements
     * @param <R>         The data type of the transformed array elements
     * @return An array of the data type R, containing the results gotten when running the input array elements through the transformer
     */
    // performs the transform
    @NotNull
    @Contract(value = "null, _, _, _, _ -> fail; _, _, _, _, _ -> new", pure = true)
    @SuppressWarnings("unchecked")
    private static <T, R> R[] uc_transform(@NotNull Class<?> c, T[] data, @NotNull Function<T, R> transformer, int index, @Nullable R element) {
        if (data == null) throw new IllegalArgumentException("null");
        R[] res = (R[]) Array.newInstance(c, data.length);
        if (index != -1) {
            for (int i = 0; i < index; i++)
                res[i] = null;
            res[index] = element;
        }
        for (int i = index + 1; i < data.length; i++) {
            res[i] = transformer.apply(data[i]);
        }
        return res;
    }

    /**
     * Transforms the array from one data type to another
     * <p>This can be useful if you have one array of objects, and you want one array of the result of the objects' {@code toString()} method, for example
     * <p>All elements will be passed through the transformer once and only once, in order. {@code Null} elements will also be passed
     * <p>The transformer may return {@code null}, but it has to return a non-{@code null} value at least once
     * <p>If you don't want {@code null}'s in the array see {@link #denullify(Object[]) the denullify method}
     *
     * @param data        The array to be processed
     * @param transformer The transformer to be used
     * @param <T>         The data type of the input array elements
     * @param <R>         The data type of the transformed array elements
     * @return An array of the data type R, containing the results gotten when running the input array elements through the transformer
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static <T, R> R[] arrayTransform(T[] data, @NotNull BiFunction<Integer, T, R> transformer) {
        if (data == null) throw new IllegalArgumentException("null");
        R o = null;
        int i;
        for (i = 0; i < data.length && o == null; i++)
            o = transformer.apply(i, data[i]);
        if (o == null)
            throw new IllegalArgumentException("Could not find class; make sure transformer returns at least one non-null element");
        return uc_arrayTransform(o.getClass(), data, transformer, i - 1, o);
    }

    /**
     * Transforms the array from one data type to another
     * <p>This can be useful if you have one array of objects, and you want one array of the result of the objects' {@code toString()} method, for example
     * <p>All elements will be passed through the transformer once and only once, in order. {@code Null} elements will also be passed
     * <p>The transformer may return {@code null}
     * <p>If you don't want {@code null}'s in the array see {@link #denullify(Class, Object[]) the denullify method}
     *
     * @param c           The class of the elements
     * @param data        The array to be processed
     * @param transformer The transformer to be used
     * @param <T>         The data type of the input array elements
     * @param <R>         The data type of the transformed array elements
     * @return An array of the data type R, containing the results gotten when running the input array elements through the transformer
     */
    @NotNull
    @Contract(value = "_, null, _ -> fail; _, _, _ -> new", pure = true)
    public static <T, R> R[] arrayTransform(@NotNull Class<R> c, T[] data, @NotNull BiFunction<Integer, T, R> transformer) {
        if (data == null) throw new IllegalArgumentException("null");
        return uc_arrayTransform(c, data, transformer, -1, null);
    }

    /**
     * Performs the transform
     * <p>An array is created with the data type {@code c}, which should be the same as {@code <R>}
     * <p>Then, it is casted from {@code c[]} to {@code R[]}, which should be fine
     * <p>This cast is classed as unchecked, but since this is a strictly controlled private method,
     * it should be fine, thus unchecked warnings are ignored
     *
     * @param c           The class of the elements
     * @param data        The array to be processed
     * @param transformer The transformer to be used
     * @param index       The index of the already transformed {@code element} in the array (or -1 if class was present)
     * @param element     An transformed element
     * @param <T>         The data type of the input array elements
     * @param <R>         The data type of the transformed array elements
     * @return An array of the data type R, containing the results gotten when running the input array elements through the transformer
     */
    // performs the transform
    @NotNull
    @Contract(value = "null, _, _, _, _ -> fail; _, _, _, _, _ -> new", pure = true)
    @SuppressWarnings("unchecked")
    private static <T, R> R[] uc_arrayTransform(@NotNull Class<?> c, T[] data, @NotNull BiFunction<Integer, T, R> transformer, int index, @Nullable R element) {
        if (data == null) throw new IllegalArgumentException("null");
        R[] res = (R[]) Array.newInstance(c, data.length);
        if (index != -1) {
            for (int i = 0; i < index; i++)
                res[i] = null;
            res[index] = element;
        }
        for (int i = index + 1; i < data.length; i++) {
            res[i] = transformer.apply(i, data[i]);
        }
        return res;
    }

    /**
     * Returns the same array but with all {@code null} values removed
     * <p>The resulting elements will be in the same order as in the original array
     * <p>If all elements are {@code null}, an empty array will be returned
     *
     * @param c    The class of the elements in the array
     * @param data The array
     * @param <T>  The data type of the array
     * @return An array with all non-{@code null} values from the original array
     * @deprecated Use {@link #denullify(Object[]) the overloaded method} instead
     * since this method depended on a workaround for class finding, which the overloaded method doesn't
     */
    @NotNull
    @Contract(value = "_, null -> fail; _, _ -> new", pure = true)
    @Deprecated
    public static <T> T[] denullify(@NotNull Class<T> c, T[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        return uc_denullify(c, data);
    }

    /**
     * Returns the same array but with all {@code null} values removed
     * <p>The resulting elements will be in the same order as in the original array
     * <p>This method uses class-finding, meaning that there must be at least one non-{@code null} element in the array
     * If you, for some reason, don't want to use class-finding, see {@link #denullify(Class, Object[])}  this method} where the {@code class} is a parameter
     *
     * @param data The array
     * @param <T>  The data type of the array
     * @return An array with all non-{@code null} values from the original array
     * @throws IllegalArgumentException if {@code data} contains only null or has the length of 0
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static <T> T[] denullify(T[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        return uc_denullify(data.getClass().getComponentType(), data);
        //return uc_denullify(assureClass(data), data);
    }

    // performs the denullification
    @NotNull
    @Contract(value = "_, null -> fail; _, _ -> new", pure = true)
    @SuppressWarnings("unchecked")
    private static <T> T[] uc_denullify(@NotNull Class<?> c, T[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = 0;
        for (T s : data) if (s != null) n++;
        T[] res = (T[]) Array.newInstance(c, n);
        int i2 = 0;
        for (T e : data)
            if (e != null) res[i2++] = e;
        return res;
    }

    /**
     * Runs all elements through the consumer's void method, which should do something with the values
     * <p>The array elements are passed in the order they appear in the array
     * <p>{@code null} elements aren't passed by default, use {@link #consume(Object[], Consumer, boolean) #consume(Object[], Consumer, true)}
     * to also pass {@code null} elements to the consumer
     *
     * @param data     The array
     * @param consumer The consumer
     * @param <T>      The data type of the array
     * @see Consumer
     */
    @Contract(value = "null, _ -> fail; _, _ -> _", pure = true)
    public static <T> void consume(T[] data, @NotNull Consumer<T> consumer) {
        consume(data, consumer, false);
    }

    /**
     * Runs all elements through the consumer's void method, which should do something with the values
     * <p>The array elements are passed in the order they appear in the array
     *
     * @param data     The array
     * @param consumer The consumer
     * @param passNull Whether to pass {@code null} elements or not
     * @param <T>      The data type of the array
     * @see Consumer
     */
    @Contract("null, _, _ -> fail; _, _, _ -> _")
    public static <T> void consume(T[] data, @NotNull Consumer<T> consumer, boolean passNull) {
        if (data == null) throw new IllegalArgumentException("null");
        for (T e : data) if (passNull || e != null) consumer.accept(e);
    }

    /**
     * Runs all elements through the consumer's void method, which should do something with the values
     * <p>The array elements are passed in the order they appear in the array
     * <p>{@code null} elements aren't passed by default, use {@link #arrayConsume(Object[], BiConsumer, boolean) #consume(Object[], Consumer, true)}
     * to also pass {@code null} elements to the consumer
     *
     * @param data     The array
     * @param consumer The consumer
     * @param <T>      The data type of the array
     * @see BiConsumer (consumer)
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static <T> void arrayConsume(T[] data, @NotNull BiConsumer<Integer, T> consumer) {
        arrayConsume(data, consumer, false);
    }

    /**
     * Runs all elements through the consumer's void method, which should do something with the values
     * <p>The array elements are passed in the order they appear in the array
     *
     * @param data     The array
     * @param consumer The consumer
     * @param passNull Whether to pass {@code null} elements or not
     * @param <T>      The data type of the array
     * @see BiConsumer
     */
    @Contract("null, _, _ -> fail")
    public static <T> void arrayConsume(T[] data, @NotNull BiConsumer<Integer, T> consumer, boolean passNull) {
        if (data == null) throw new IllegalArgumentException("null");
        int i = 0;
        for (T e : data) if (passNull || e != null) consumer.accept(i++, e);
    }

    /**
     * Joins the array elements to one single string, seperated by the delimeter.
     * <p>This uses the {@linkplain Object#toString()} method to turn the objects to strings.
     * <p>If an object is {@code null}, it is replaced with the string {@code "null"}.
     * <p>The returned string will be in format {@code [elem1, elem2, elem3]}.
     *
     * @param data The array
     * @return All elements joined by the delimeter
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static String join(Object[] data) {
        return join(data, ", ", "[", "]");
    }

    /**
     * Joins the array elements to one single string, seperated by the delimeter.
     * <p>This uses the {@linkplain Object#toString()} method to turn the objects to strings.
     * <p>If an object is {@code null}, it is replaced with the string {@code "null"}.
     *
     * @param data      The array
     * @param delimeter The delimeter to use between elements
     * @return All elements joined by the delimeter
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static String join(Object[] data, @Nullable String delimeter) {
        return join(data, delimeter, null, null);
    }

    /**
     * Joins the array elements to one single string, seperated by the delimeter.
     * <p>This uses the {@linkplain Object#toString()} method to turn the objects to strings.
     * <p>If an object is {@code null}, it is replaced with the string {@code "null"}.
     *
     * @param data      The array
     * @param delimeter The delimeter to use between elements
     * @param before    The string to put in the start of the returned string
     * @param after     The string to put at the end of the returned string
     * @return All elements joined by the delimeter
     */
    @NotNull
    @Contract(value = "null, _, _, _ -> fail; _, _, _, _ -> new", pure = true)
    public static String join(Object[] data, @Nullable String delimeter, @Nullable String before, @Nullable String after) {
        StringBuilder sb = new StringBuilder();
        if (before != null) sb.append(before);
        for (int i = 0; i < data.length; i++) {
            if (i != 0 && delimeter != null) sb.append(delimeter);
            sb.append(data[i] == null ? "null" : data[i].toString());
        }
        if (after != null) sb.append(after);
        return sb.toString();
    }


    // UNBOXGROUP

    /**
     * Turns an {@code Integer[]} into an {@code int[]}. {@code null} values will be turned into 0.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Integer[] array
     * @return an int[] array with the same values as the input array (null values will become 0)
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static int[] unbox(Integer[] data) {
        return unbox(data, 0);
    }

    /**
     * Turns an {@code Integer[]} into an {@code int[]}. {@code null} values will be replaced with the {@code int} provided as second argument.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Integer[] array
     * @param def  the value to replace {@code null} values with
     * @return an int[] array with the same values as the input array (null values will be replaced with {@code def})
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static int[] unbox(Integer[] data, int def) {
        if (data == null) throw new IllegalArgumentException("null");
        int[] res = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i] == null ? def : data[i];
        }
        return res;
    }

    /**
     * Turns an {@code Long[]} into an {@code long[]}. {@code null} values will be turned into 0.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Long[] array
     * @return an long[] array with the same values as the input array (null values will become 0)
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static long[] unbox(Long[] data) {
        return unbox(data, 0);
    }

    /**
     * Turns an {@code Long[]} into an {@code long[]}. {@code null} values will be replaced with the {@code long} provided as second argument.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Long[] array
     * @param def  the value to replace {@code null} values with
     * @return an long[] array with the same values as the input array (null values will be replaced with {@code def})
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static long[] unbox(Long[] data, long def) {
        if (data == null) throw new IllegalArgumentException("null");
        long[] res = new long[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i] == null ? def : data[i];
        }
        return res;
    }

    /**
     * Turns an {@code Float[]} into an {@code float[]}. {@code null} values will be turned into 0.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Float[] array
     * @return an float[] array with the same values as the input array (null values will become 0)
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static float[] unbox(Float[] data) {
        return unbox(data, 0f);
    }

    /**
     * Turns an {@code Float[]} into an {@code float[]}. {@code null} values will be replaced with the {@code float} provided as second argument.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Float[] array
     * @param def  the value to replace {@code null} values with
     * @return an float[] array with the same values as the input array (null values will be replaced with {@code def})
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static float[] unbox(Float[] data, float def) {
        if (data == null) throw new IllegalArgumentException("null");
        float[] res = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i] == null ? def : data[i];
        }
        return res;
    }


    /**
     * Turns an {@code Double[]} into an {@code double[]}. {@code null} values will be turned into 0.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Double[] array
     * @return an double[] array with the same values as the input array (null values will become 0)
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static double[] unbox(Double[] data) {
        return unbox(data, 0d);
    }

    /**
     * Turns an {@code Double[]} into an {@code double[]}. {@code null} values will be replaced with the {@code double} provided as second argument.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Double[] array
     * @param def  the value to replace {@code null} values with
     * @return an double[] array with the same values as the input array (null values will be replaced with {@code def})
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static double[] unbox(Double[] data, double def) {
        if (data == null) throw new IllegalArgumentException("null");
        double[] res = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i] == null ? def : data[i];
        }
        return res;
    }

    /**
     * Turns an {@code Byte[]} into an {@code byte[]}. {@code null} values will be turned into 0.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Byte[] array
     * @return an byte[] array with the same values as the input array (null values will become 0)
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static byte[] unbox(Byte[] data) {
        return unbox(data, (byte) 0);
    }

    /**
     * Turns an {@code Byte[]} into an {@code byte[]}. {@code null} values will be replaced with the {@code byte} provided as second argument.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Byte[] array
     * @param def  the value to replace {@code null} values with
     * @return an byte[] array with the same values as the input array (null values will be replaced with {@code def})
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static byte[] unbox(Byte[] data, byte def) {
        if (data == null) throw new IllegalArgumentException("null");
        byte[] res = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i] == null ? def : data[i];
        }
        return res;
    }

    /**
     * Turns an {@code Short[]} into an {@code short[]}. {@code null} values will be turned into 0.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Short[] array
     * @return an short[] array with the same values as the input array (null values will become 0)
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static short[] unbox(Short[] data) {
        return unbox(data, (short) 0);
    }

    /**
     * Turns an {@code Short[]} into an {@code short[]}. {@code null} values will be replaced with the {@code short} provided as second argument.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Short[] array
     * @param def  the value to replace {@code null} values with
     * @return an short[] array with the same values as the input array (null values will be replaced with {@code def})
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static short[] unbox(Short[] data, short def) {
        if (data == null) throw new IllegalArgumentException("null");
        short[] res = new short[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i] == null ? def : data[i];
        }
        return res;
    }

    /**
     * Turns an {@code Character[]} into an {@code char[]}. {@code null} values will be turned into ' '.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Character[] array
     * @return an char[] array with the same values as the input array (null values will become ' ')
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static char[] unbox(Character[] data) {
        return unbox(data, ' ');
    }

    /**
     * Turns an {@code Character[]} into an {@code char[]}. {@code null} values will be replaced with the {@code char} provided as second argument.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Character[] array
     * @param def  the value to replace {@code null} values with
     * @return an char[] array with the same values as the input array (null values will be replaced with {@code def})
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static char[] unbox(Character[] data, char def) {
        if (data == null) throw new IllegalArgumentException("null");
        char[] res = new char[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i] == null ? def : data[i];
        }
        return res;
    }

    /**
     * Turns an {@code Boolean[]} into an {@code boolean[]}. {@code null} values will be turned into {@code false}.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Boolean[] array
     * @return an boolean[] array with the same values as the input array (null values will become {@code false})
     */
    @NotNull
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static boolean[] unbox(Boolean[] data) {
        return unbox(data, false);
    }

    /**
     * Turns an {@code Boolean[]} into an {@code boolean[]}. {@code null} values will be replaced with the {@code boolean} provided as second argument.
     * <p>If you want to skip {@code null} values, see the {@link #denullify(Object[]) denullify} method
     *
     * @param data the Boolean[] array
     * @param def  the value to replace {@code null} values with
     * @return an boolean[] array with the same values as the input array (null values will be replaced with {@code def})
     */
    @NotNull
    @Contract(value = "null, _ -> fail; _, _ -> new", pure = true)
    public static boolean[] unbox(Boolean[] data, boolean def) {
        if (data == null) throw new IllegalArgumentException("null");
        boolean[] res = new boolean[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i] == null ? def : data[i];
        }
        return res;
    }


    // ENBOXGROUP

    /**
     * Turns an {@code int[]} into an {@code Integer[]}
     * <p>If you want to do it in the other way ({@code Integer[]} to {@code int[]}), use {@link #unbox(Integer[]) unbox}
     *
     * @param data the int[] array
     * @return an Integer[] array with the same values as the input array
     */
    @NotNull
    public static Integer[] enbox(@NotNull int[] data) {
        Integer[] res = new Integer[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i];
        }
        return res;
    }

    /**
     * Turns an {@code long[]} into an {@code Long[]}
     * <p>If you want to do it in the other way ({@code Long[]} to {@code long[]}), use {@link #unbox(Long[]) unbox}
     *
     * @param data the long[] array
     * @return an Long[] array with the same values as the input array
     */
    @NotNull
    public static Long[] enbox(@NotNull long[] data) {
        Long[] res = new Long[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i];
        }
        return res;
    }

    /**
     * Turns an {@code float[]} into an {@code Float[]}
     * <p>If you want to do it in the other way ({@code Float[]} to {@code float[]}), use {@link #unbox(Float[]) unbox}
     *
     * @param data the float[] array
     * @return an Float[] array with the same values as the input array
     */
    @NotNull
    public static Float[] enbox(@NotNull float[] data) {
        Float[] res = new Float[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i];
        }
        return res;
    }

    /**
     * Turns an {@code double[]} into an {@code Double[]}
     * <p>If you want to do it in the other way ({@code Double[]} to {@code double[]}), use {@link #unbox(Double[]) unbox}
     *
     * @param data the double[] array
     * @return an Double[] array with the same values as the input array
     */
    @NotNull
    public static Double[] enbox(@NotNull double[] data) {
        Double[] res = new Double[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i];
        }
        return res;
    }

    /**
     * Turns an {@code byte[]} into an {@code Byte[]}
     * <p>If you want to do it in the other way ({@code Byte[]} to {@code byte[]}), use {@link #unbox(Byte[]) unbox}
     *
     * @param data the byte[] array
     * @return an Byte[] array with the same values as the input array
     */
    @NotNull
    public static Byte[] enbox(@NotNull byte[] data) {
        Byte[] res = new Byte[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i];
        }
        return res;
    }

    /**
     * Turns an {@code short[]} into an {@code Short[]}
     * <p>If you want to do it in the other way ({@code Short[]} to {@code short[]}), use {@link #unbox(Short[]) unbox}
     *
     * @param data the short[] array
     * @return an Short[] array with the same values as the input array
     */
    @NotNull
    public static Short[] enbox(@NotNull short[] data) {
        Short[] res = new Short[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i];
        }
        return res;
    }

    /**
     * Turns an {@code char[]} into an {@code Character[]}
     * <p>If you want to do it in the other way ({@code Character[]} to {@code char[]}), use {@link #unbox(Character[]) unbox}
     *
     * @param data the char[] array
     * @return an Character[] array with the same values as the input array
     */
    @NotNull
    public static Character[] enbox(@NotNull char[] data) {
        Character[] res = new Character[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i];
        }
        return res;
    }

    /**
     * Turns an {@code boolean[]} into an {@code Boolean[]}
     * <p>If you want to do it in the other way ({@code Boolean[]} to {@code boolean[]}), use {@link #unbox(Boolean[]) unbox}
     *
     * @param data the boolean[] array
     * @return an Boolean[] array with the same values as the input array
     */
    @NotNull
    public static Boolean[] enbox(@NotNull boolean[] data) {
        Boolean[] res = new Boolean[data.length];
        for (int i = 0; i < data.length; i++) {
            res[i] = data[i];
        }
        return res;
    }

    // COUNTGROUP

    /**
     * Counts how many times {@code target} appears in the array
     *
     * @param data   the array
     * @param target the target to compare to
     * @return the amount of occurances of {@code target} in {@code data}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int count(int[] data, int target) {
        if (data == null) throw new IllegalArgumentException("null");
        int res = 0;
        for (int i : data) if (i == target) res++;
        return res;
    }

    /**
     * Counts how many times {@code target} appears in the array
     *
     * @param data   the array
     * @param target the target to compare to
     * @return the amount of occurances of {@code target} in {@code data}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int count(long[] data, long target) {
        if (data == null) throw new IllegalArgumentException("null");
        int res = 0;
        for (long i : data) if (i == target) res++;
        return res;
    }

    /**
     * Counts how many times {@code target} appears in the array
     *
     * @param data   the array
     * @param target the target to compare to
     * @return the amount of occurances of {@code target} in {@code data}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int count(float[] data, float target) {
        if (data == null) throw new IllegalArgumentException("null");
        int res = 0;
        for (float i : data) if (i == target) res++;
        return res;
    }

    /**
     * Counts how many times {@code target} appears in the array
     *
     * @param data   the array
     * @param target the target to compare to
     * @return the amount of occurances of {@code target} in {@code data}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int count(double[] data, double target) {
        if (data == null) throw new IllegalArgumentException("null");
        int res = 0;
        for (double i : data) if (i == target) res++;
        return res;
    }

    /**
     * Counts how many times {@code target} appears in the array
     *
     * @param data   the array
     * @param target the target to compare to
     * @return the amount of occurances of {@code target} in {@code data}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int count(byte[] data, byte target) {
        if (data == null) throw new IllegalArgumentException("null");
        int res = 0;
        for (byte i : data) if (i == target) res++;
        return res;
    }

    /**
     * Counts how many times {@code target} appears in the array
     *
     * @param data   the array
     * @param target the target to compare to
     * @return the amount of occurances of {@code target} in {@code data}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int count(short[] data, short target) {
        if (data == null) throw new IllegalArgumentException("null");
        int res = 0;
        for (short i : data) if (i == target) res++;
        return res;
    }

    /**
     * Counts how many times {@code target} appears in the array
     *
     * @param data   the array
     * @param target the target to compare to
     * @return the amount of occurances of {@code target} in {@code data}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int count(char[] data, char target) {
        if (data == null) throw new IllegalArgumentException("null");
        int res = 0;
        for (char i : data) if (i == target) res++;
        return res;
    }

    /**
     * Counts how many times {@code target} appears in the array
     *
     * @param data   the array
     * @param target the target to compare to
     * @return the amount of occurances of {@code target} in {@code data}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int count(boolean[] data, boolean target) {
        if (data == null) throw new IllegalArgumentException("null");
        int res = 0;
        for (boolean i : data) if (i == target) res++;
        return res;
    }

    /**
     * Counts how many times {@code target} appears in the array
     * <p>If the object refrences point to the same object,
     * or if {@linkplain Object#equals(Object)} returns true, it counts as an occurance; otherwise not.
     * <p>The array data type and the object provided as {@code target} can be of different types
     *
     * @param data   the array
     * @param target the target to compare to
     * @return the amount of occurances of {@code target} in {@code data}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int count(Object[] data, Object target) {
        if (data == null) throw new IllegalArgumentException("null");
        int res = 0;
        for (Object i : data) if (Objects.equals(i, target)) res++;
        return res;
    }


    // INDEXGROUP

    /**
     * Returns the index of the first occurance of {@code target} in {@code data}
     *
     * @param data   The array to search
     * @param target The value to search for
     * @return the index of the first occurance, or -1 if none is found
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int index(int[] data, int target) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < data.length; i++)
            if (data[i] == target) return i;
        return -1;
    }

    /**
     * Returns the index of the first occurance of {@code target} in {@code data}
     *
     * @param data   The array to search
     * @param target The value to search for
     * @return the index of the first occurance, or -1 if none is found
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int index(long[] data, long target) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < data.length; i++)
            if (data[i] == target) return i;
        return -1;
    }

    /**
     * Returns the index of the first occurance of {@code target} in {@code data}
     *
     * @param data   The array to search
     * @param target The value to search for
     * @return the index of the first occurance, or -1 if none is found
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int index(float[] data, float target) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < data.length; i++)
            if (data[i] == target) return i;
        return -1;
    }

    /**
     * Returns the index of the first occurance of {@code target} in {@code data}
     *
     * @param data   The array to search
     * @param target The value to search for
     * @return the index of the first occurance, or -1 if none is found
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int index(double[] data, double target) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < data.length; i++)
            if (data[i] == target) return i;
        return -1;
    }

    /**
     * Returns the index of the first occurance of {@code target} in {@code data}
     *
     * @param data   The array to search
     * @param target The value to search for
     * @return the index of the first occurance, or -1 if none is found
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int index(byte[] data, byte target) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < data.length; i++)
            if (data[i] == target) return i;
        return -1;
    }

    /**
     * Returns the index of the first occurance of {@code target} in {@code data}
     *
     * @param data   The array to search
     * @param target The value to search for
     * @return the index of the first occurance, or -1 if none is found
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int index(char[] data, char target) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < data.length; i++)
            if (data[i] == target) return i;
        return -1;
    }

    /**
     * Returns the index of the first occurance of {@code target} in {@code data}
     *
     * @param data   The array to search
     * @param target The value to search for
     * @return the index of the first occurance, or -1 if none is found
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int index(short[] data, short target) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < data.length; i++)
            if (data[i] == target) return i;
        return -1;
    }

    /**
     * Returns the index of the first occurance of {@code target} in {@code data}
     *
     * @param data   The array to search
     * @param target The value to search for
     * @return the index of the first occurance, or -1 if none is found
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static int index(boolean[] data, boolean target) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < data.length; i++)
            if (data[i] == target) return i;
        return -1;
    }

    /**
     * Returns the index of the first occurance of {@code target} in {@code data}
     * <p>Two elements are counted as the same if they either are references to the same object,
     * or returns {@code true} when running {@linkplain Object#equals(Object)}
     *
     * @param data   The array to search
     * @param target The value to search for
     * @param <T>    The data type of the array
     * @return the index of the first occurance, or -1 if none is found
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static <T> int index(T[] data, @Nullable T target) {
        if (data == null) throw new IllegalArgumentException("null");
        for (int i = 0; i < data.length; i++)
            if (Objects.equals(data[i], target)) return i;
        return -1;
    }


    // HASGROUP

    /**
     * Checks if the provided {@code int[]} contains the target {@code int}
     *
     * @param data   the array
     * @param target the target to find
     * @return {@code true} if the array contains at least one element equal to {@code target}, otherwise {@code false}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static boolean has(int[] data, int target) {
        return index(data, target) != -1;
    }

    /**
     * Checks if the provided {@code long[]} contains the target {@code long}
     *
     * @param data   the array
     * @param target the target to find
     * @return {@code true} if the array contains at least one element equal to {@code target}, otherwise {@code false}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static boolean has(long[] data, long target) {
        return index(data, target) != -1;
    }

    /**
     * Checks if the provided {@code float[]} contains the target {@code float}
     *
     * @param data   the array
     * @param target the target to find
     * @return {@code true} if the array contains at least one element equal to {@code target}, otherwise {@code false}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static boolean has(float[] data, float target) {
        return index(data, target) != -1;
    }

    /**
     * Checks if the provided {@code double[]} contains the target {@code double}
     *
     * @param data   the array
     * @param target the target to find
     * @return {@code true} if the array contains at least one element equal to {@code target}, otherwise {@code false}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static boolean has(double[] data, double target) {
        return index(data, target) != -1;
    }

    /**
     * Checks if the provided {@code byte[]} contains the target {@code byte}
     *
     * @param data   the array
     * @param target the target to find
     * @return {@code true} if the array contains at least one element equal to {@code target}, otherwise {@code false}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static boolean has(byte[] data, byte target) {
        return index(data, target) != -1;
    }

    /**
     * Checks if the provided {@code char[]} contains the target {@code char}
     *
     * @param data   the array
     * @param target the target to find
     * @return {@code true} if the array contains at least one element equal to {@code target}, otherwise {@code false}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static boolean has(char[] data, char target) {
        return index(data, target) != -1;
    }

    /**
     * Checks if the provided {@code short[]} contains the target {@code short}
     *
     * @param data   the array
     * @param target the target to find
     * @return {@code true} if the array contains at least one element equal to {@code target}, otherwise {@code false}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static boolean has(short[] data, short target) {
        return index(data, target) != -1;
    }

    /**
     * Checks if the provided {@code boolean[]} contains the target {@code boolean}
     *
     * @param data   the array
     * @param target the target to find
     * @return {@code true} if the array contains at least one element equal to {@code target}, otherwise {@code false}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static boolean has(boolean[] data, boolean target) {
        return index(data, target) != -1;
    }

    /**
     * Checks if the provided {@code T[]} contains the target {@code T}
     * <p>T can be any data type
     * <p>Elements are compared with both {@code ==} and {@link Object#equals(Object) #equals()},
     * meaning that if two object references points to the same object, it will return true.
     *
     * @param data   the array
     * @param target the target to find
     * @param <T>    The data type of the array
     * @return {@code true} if the array contains at least one element equal to {@code target}, otherwise {@code false}
     */
    @Contract(value = "null, _ -> fail", pure = true)
    public static <T> boolean has(T[] data, T target) {
        return index(data, target) != -1;
    }


    // SHUFFLEGROUP

    /**
     * Shuffles the array
     * <p>This method makes a copy of the array, shuffles the elements, and returns the new array.
     * <p>This method is unbiased - one element has the same probability to be in each spot.
     * <p>One element can happen to be in the same index in the input array as in the shuffled array.
     * <p>That means that the elements in the returned array can have the same order as the elements in the inputed array.
     *
     * @param data The array
     * @param <T>  The data type of the array
     * @return An shuffled array
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static <T> T[] shuffle(T[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        T[] res = data.clone();
        for (int i = res.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            if (index == i) continue;
            T t = res[i];
            res[i] = res[index];
            res[index] = t;
        }
        return res;
    }

    /**
     * Shuffles the array
     * <p>This method makes a copy of the array, shuffles the elements, and returns the new array.
     * <p>This method is unbiased - one element has the same probability to be in each spot.
     * <p>One element can happen to be in the same index in the input array as in the shuffled array.
     * <p>That means that the elements in the returned array can have the same order as the elements in the inputed array.
     *
     * @param data The array
     * @return An shuffled array
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static int[] shuffle(int[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int[] res = data.clone();
        for (int i = res.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            if (index == i) continue;
            int t = res[i];
            res[i] = res[index];
            res[index] = t;
        }
        return res;
    }

    /**
     * Shuffles the array
     * <p>This method makes a copy of the array, shuffles the elements, and returns the new array.
     * <p>This method is unbiased - one element has the same probability to be in each spot.
     * <p>One element can happen to be in the same index in the input array as in the shuffled array.
     * <p>That means that the elements in the returned array can have the same order as the elements in the inputed array.
     *
     * @param data The array
     * @return An shuffled array
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static long[] shuffle(long[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        long[] res = data.clone();
        for (int i = res.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            if (index == i) continue;
            long t = res[i];
            res[i] = res[index];
            res[index] = t;
        }
        return res;
    }

    /**
     * Shuffles the array
     * <p>This method makes a copy of the array, shuffles the elements, and returns the new array.
     * <p>This method is unbiased - one element has the same probability to be in each spot.
     * <p>One element can happen to be in the same index in the input array as in the shuffled array.
     * <p>That means that the elements in the returned array can have the same order as the elements in the inputed array.
     *
     * @param data The array
     * @return An shuffled array
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static float[] shuffle(float[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        float[] res = data.clone();
        for (int i = res.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            if (index == i) continue;
            float t = res[i];
            res[i] = res[index];
            res[index] = t;
        }
        return res;
    }

    /**
     * Shuffles the array
     * <p>This method makes a copy of the array, shuffles the elements, and returns the new array.
     * <p>This method is unbiased - one element has the same probability to be in each spot.
     * <p>One element can happen to be in the same index in the input array as in the shuffled array.
     * <p>That means that the elements in the returned array can have the same order as the elements in the inputed array.
     *
     * @param data The array
     * @return An shuffled array
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static double[] shuffle(double[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        double[] res = data.clone();
        for (int i = res.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            if (index == i) continue;
            double t = res[i];
            res[i] = res[index];
            res[index] = t;
        }
        return res;
    }

    /**
     * Shuffles the array
     * <p>This method makes a copy of the array, shuffles the elements, and returns the new array.
     * <p>This method is unbiased - one element has the same probability to be in each spot.
     * <p>One element can happen to be in the same index in the input array as in the shuffled array.
     * <p>That means that the elements in the returned array can have the same order as the elements in the inputed array.
     *
     * @param data The array
     * @return An shuffled array
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static byte[] shuffle(byte[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        byte[] res = data.clone();
        for (int i = res.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            if (index == i) continue;
            byte t = res[i];
            res[i] = res[index];
            res[index] = t;
        }
        return res;
    }

    /**
     * Shuffles the array
     * <p>This method makes a copy of the array, shuffles the elements, and returns the new array.
     * <p>This method is unbiased - one element has the same probability to be in each spot.
     * <p>One element can happen to be in the same index in the input array as in the shuffled array.
     * <p>That means that the elements in the returned array can have the same order as the elements in the inputed array.
     *
     * @param data The array
     * @return An shuffled array
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static char[] shuffle(char[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        char[] res = data.clone();
        for (int i = res.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            if (index == i) continue;
            char t = res[i];
            res[i] = res[index];
            res[index] = t;
        }
        return res;
    }

    /**
     * Shuffles the array
     * <p>This method makes a copy of the array, shuffles the elements, and returns the new array.
     * <p>This method is unbiased - one element has the same probability to be in each spot.
     * <p>One element can happen to be in the same index in the input array as in the shuffled array.
     * <p>That means that the elements in the returned array can have the same order as the elements in the inputed array.
     *
     * @param data The array
     * @return An shuffled array
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static short[] shuffle(short[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        short[] res = data.clone();
        for (int i = res.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            if (index == i) continue;
            short t = res[i];
            res[i] = res[index];
            res[index] = t;
        }
        return res;
    }

    /**
     * Shuffles the array
     * <p>This method makes a copy of the array, shuffles the elements, and returns the new array.
     * <p>This method is unbiased - one element has the same probability to be in each spot.
     * <p>One element can happen to be in the same index in the input array as in the shuffled array.
     * <p>That means that the elements in the returned array can have the same order as the elements in the inputed array.
     *
     * @param data The array
     * @return An shuffled array
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static boolean[] shuffle(boolean[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        boolean[] res = data.clone();
        for (int i = res.length - 1; i > 0; i--) {
            int index = (int) (Math.random() * (i + 1));
            if (index == i) continue;
            boolean t = res[i];
            res[i] = res[index];
            res[index] = t;
        }
        return res;
    }

    /**
     * Reverses the order of an array
     * <p>In an array of length {@code n}, element {@code 0} will have the index {@code n-1} in the new array,
     * element {@code 1} will have the index {@code n-2}, element {@code m} will have the index {@code n-m-1},
     * and element {@code n-1} will have the index {@code 0}
     *
     * @param data The array
     * @return A new array containing the same values, but in reverse order
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static int[] reverse(int[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = data.length;
        int[] ret = new int[n];
        for (int i = 0; i < data.length; i++)
            ret[n - i - 1] = data[i];

        return ret;
    }

    /**
     * Reverses the order of an array
     * <p>In an array of length {@code n}, element {@code 0} will have the index {@code n-1} in the new array,
     * element {@code 1} will have the index {@code n-2}, element {@code m} will have the index {@code n-m-1},
     * and element {@code n-1} will have the index {@code 0}
     *
     * @param data The array
     * @return A new array containing the same values, but in reverse order
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static long[] reverse(long[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = data.length;
        long[] ret = new long[n];
        for (int i = 0; i < data.length; i++)
            ret[n - i - 1] = data[i];

        return ret;
    }

    /**
     * Reverses the order of an array
     * <p>In an array of length {@code n}, element {@code 0} will have the index {@code n-1} in the new array,
     * element {@code 1} will have the index {@code n-2}, element {@code m} will have the index {@code n-m-1},
     * and element {@code n-1} will have the index {@code 0}
     *
     * @param data The array
     * @return A new array containing the same values, but in reverse order
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static float[] reverse(float[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = data.length;
        float[] ret = new float[n];
        for (int i = 0; i < data.length; i++)
            ret[n - i - 1] = data[i];

        return ret;
    }

    /**
     * Reverses the order of an array
     * <p>In an array of length {@code n}, element {@code 0} will have the index {@code n-1} in the new array,
     * element {@code 1} will have the index {@code n-2}, element {@code m} will have the index {@code n-m-1},
     * and element {@code n-1} will have the index {@code 0}
     *
     * @param data The array
     * @return A new array containing the same values, but in reverse order
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static double[] reverse(double[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = data.length;
        double[] ret = new double[n];
        for (int i = 0; i < data.length; i++)
            ret[n - i - 1] = data[i];

        return ret;
    }

    /**
     * Reverses the order of an array
     * <p>In an array of length {@code n}, element {@code 0} will have the index {@code n-1} in the new array,
     * element {@code 1} will have the index {@code n-2}, element {@code m} will have the index {@code n-m-1},
     * and element {@code n-1} will have the index {@code 0}
     *
     * @param data The array
     * @return A new array containing the same values, but in reverse order
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static byte[] reverse(byte[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = data.length;
        byte[] ret = new byte[n];
        for (int i = 0; i < data.length; i++)
            ret[n - i - 1] = data[i];

        return ret;
    }

    /**
     * Reverses the order of an array
     * <p>In an array of length {@code n}, element {@code 0} will have the index {@code n-1} in the new array,
     * element {@code 1} will have the index {@code n-2}, element {@code m} will have the index {@code n-m-1},
     * and element {@code n-1} will have the index {@code 0}
     *
     * @param data The array
     * @return A new array containing the same values, but in reverse order
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static char[] reverse(char[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = data.length;
        char[] ret = new char[n];
        for (int i = 0; i < data.length; i++)
            ret[n - i - 1] = data[i];

        return ret;
    }

    /**
     * Reverses the order of an array
     * <p>In an array of length {@code n}, element {@code 0} will have the index {@code n-1} in the new array,
     * element {@code 1} will have the index {@code n-2}, element {@code m} will have the index {@code n-m-1},
     * and element {@code n-1} will have the index {@code 0}
     *
     * @param data The array
     * @return A new array containing the same values, but in reverse order
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static short[] reverse(short[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = data.length;
        short[] ret = new short[n];
        for (int i = 0; i < data.length; i++)
            ret[n - i - 1] = data[i];

        return ret;
    }

    /**
     * Reverses the order of an array
     * <p>In an array of length {@code n}, element {@code 0} will have the index {@code n-1} in the new array,
     * element {@code 1} will have the index {@code n-2}, element {@code m} will have the index {@code n-m-1},
     * and element {@code n-1} will have the index {@code 0}
     *
     * @param data The array
     * @return A new array containing the same values, but in reverse order
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    public static boolean[] reverse(boolean[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = data.length;
        boolean[] ret = new boolean[n];
        for (int i = 0; i < data.length; i++)
            ret[n - i - 1] = data[i];

        return ret;
    }

    /**
     * Reverses the order of an array
     * <p>In an array of length {@code n}, element {@code 0} will have the index {@code n-1} in the new array,
     * element {@code 1} will have the index {@code n-2}, element {@code m} will have the index {@code n-m-1},
     * and element {@code n-1} will have the index {@code 0}
     *
     * @param data The array
     * @param <T>  The data type of the array
     * @return A new array containing the same objects, but in reverse order
     */
    @NotNull
    @Contract(value = "null -> fail", pure = true)
    @SuppressWarnings("unchecked")
    public static <T> T[] reverse(T[] data) {
        if (data == null) throw new IllegalArgumentException("null");
        int n = data.length;
        T[] ret = (T[]) Array.newInstance(data.getClass().getComponentType(), n);
        for (int i = 0; i < data.length; i++)
            ret[n - i - 1] = data[i];

        return ret;
    }

    /**
     * Turns a byte[] array to a String[] array where element {@code n} of the String[] array is the unsigned
     * hex representation of element {@code n} of the byte array
     *
     * @param data the input byte[] array
     * @return a transformed String[] array
     */
    public static String[] toHexArray(byte[] data) {
        return toHexArray(data, false);
    }

    /**
     * Turns a byte[] array to a String[] array where element {@code n} of the String[] array is the unsigned
     * hex representation of element {@code n} of the byte array
     *
     * @param data      the input byte[] array
     * @param uppercase whether or not the result should have {@code a b c d e f} in uppercase
     * @return a transformed String[] array
     */
    public static String[] toHexArray(byte[] data, boolean uppercase) {
        int n = data.length;
        String[] res = new String[n];
        for (int i = 0; i < n; i++)
            res[i] = String.format("%02" + (uppercase ? "X" : "x"), data[i]);
        return res;
    }

    /**
     * Turns a byte[] array to a String containing the hex representation of each element of the byte[] array
     * concatenated
     *
     * @param data the input byte[] array
     * @return the string
     */
    public static String toHexString(byte[] data) {
        return toHexString(data, false);
    }

    /**
     * Turns a byte[] array to a String containing the hex representation of each element of the byte[] array
     * concatenated
     *
     * @param data      the input byte[] array
     * @param uppercase whether or not the result should have {@code a b c d e f} in uppercase
     * @return the string
     */
    public static String toHexString(byte[] data, boolean uppercase) {
        return join(
                toHexArray(
                        data,
                        uppercase
                ),
                null
        );
    }
}

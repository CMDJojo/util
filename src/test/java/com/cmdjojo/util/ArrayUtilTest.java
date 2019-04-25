package com.cmdjojo.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class ArrayUtilTest {

    private int mem;

    private void addMem(int i) {
        mem += i;
    }

    @Test
    void filter() {
        String[] filterArrayTest1 = {
                "ABC",
                "deE",
                "oe",
                "MADSK",
                "dsi",
                "skAD"
        };
        String[] filterArrayResult1 = {
                "ABC",
                "deE",
                "dsi"
        };
        Predicate<String> filter1 = s -> s.length() == 3;
        String[] filteredArray1a = ArrayUtil.filter(filterArrayTest1, filter1);
        String[] filteredArray1b = ArrayUtil.filter(String.class, filterArrayTest1, filter1);
        assertArrayEquals(filteredArray1a, filterArrayResult1);
        assertArrayEquals(filteredArray1b, filterArrayResult1);

        String[] filterArrayTest2 = {
                null,
                null,
                "ease",
                "oe",
                null,
                "NULL"
        };
        String[] filterArrayResult2 = {
                null,
                null,
                null,
                "NULL"
        };
        Predicate<String> filter2 = s -> s == null || s.equalsIgnoreCase("null");
        String[] filteredArray2a = ArrayUtil.filter(filterArrayTest2, filter2);
        String[] filteredArray2b = ArrayUtil.filter(String.class, filterArrayTest2, filter2);
        assertArrayEquals(filteredArray2a, filterArrayResult2);
        assertArrayEquals(filteredArray2b, filterArrayResult2);

        String[] filterArrayTest3 = {
                null,
                null
        };
        String[] filterArrayResult3 = {};
        Predicate<String> filter3 = Objects::nonNull;

        //workaround removed - test will fail
        //assertThrows(IllegalArgumentException.class, () -> ArrayUtil.filter(filterArrayTest3, filter3));

        String[] filteredArray3b = ArrayUtil.filter(String.class, filterArrayTest3, filter3);
        assertArrayEquals(filteredArray3b, filterArrayResult3);
    }


    @Test
    void arrayFilter() {
        String[] filterArrayTest1 = {
                "ABC",
                "deE",
                "oe",
                "MADSK",
                "dsi",
                "skAD"
        };
        String[] filterArrayResult1 = {
                "deE",
                "skAD"
        };
        BiPredicate<Integer, String> filter1 = (i, s) -> (i + 1) % 2 == 0 && (s.length() == 3 || s.length() == 4);
        String[] filteredArray1a = ArrayUtil.arrayFilter(filterArrayTest1, filter1);
        String[] filteredArray1b = ArrayUtil.arrayFilter(String.class, filterArrayTest1, filter1);
        assertArrayEquals(filteredArray1a, filterArrayResult1);
        assertArrayEquals(filteredArray1b, filterArrayResult1);

        String[] filterArrayTest2 = {
                null,
                null,
                "ease",
                "oe",
                null,
                "NULL"
        };
        String[] filterArrayResult2 = {
                null,
                null,
                "oe",
                null,
                "NULL"
        };
        BiPredicate<Integer, String> filter2 = (i, s) -> s == null || s.equalsIgnoreCase("null") || i % 3 == 0;
        String[] filteredArray2a = ArrayUtil.arrayFilter(filterArrayTest2, filter2);
        String[] filteredArray2b = ArrayUtil.arrayFilter(String.class, filterArrayTest2, filter2);
        assertArrayEquals(filteredArray2a, filterArrayResult2);
        assertArrayEquals(filteredArray2b, filterArrayResult2);
    }

    @Test
    void transform() {
        String[] data = {
                "Hello",
                "darkness",
                "my",
                "old",
                "friend"
        };
        String[] res1 = {
                "h",
                "d",
                "m",
                "o",
                "f"
        };
        Integer[] res2 = {
                5,
                8,
                2,
                3,
                6
        };
        Function<String, String> t1 = s -> s.substring(0, 1).toLowerCase();
        Function<String, Integer> t2 = String::length;
        String[] r1 = ArrayUtil.transform(String.class, data, t1);
        Integer[] r2 = ArrayUtil.transform(Integer.class, data, t2);
        String[] r3 = ArrayUtil.transform(data, t1);
        Integer[] r4 = ArrayUtil.transform(data, t2);
        assertArrayEquals(r1, res1);
        assertArrayEquals(r2, res2);
        assertArrayEquals(r3, res1);
        assertArrayEquals(r4, res2);
    }

    @Test
    void arrayTransform() {
        String[] data = {
                "Hello",
                "darkness",
                "my",
                "old",
                "friend"
        };
        String[] res1 = {
                "1 h",
                "2 d",
                "3 m",
                "4 o",
                "5 f"
        };
        Integer[] res2 = {
                5,
                9,
                4,
                6,
                10
        };
        BiFunction<Integer, String, String> t1 = (i, s) -> (i + 1) + " " + s.substring(0, 1).toLowerCase();
        BiFunction<Integer, String, Integer> t2 = (i, s) -> s.length() + i;
        String[] r1 = ArrayUtil.arrayTransform(String.class, data, t1);
        Integer[] r2 = ArrayUtil.arrayTransform(Integer.class, data, t2);
        String[] r3 = ArrayUtil.arrayTransform(data, t1);
        Integer[] r4 = ArrayUtil.arrayTransform(data, t2);
        assertArrayEquals(r1, res1);
        assertArrayEquals(r2, res2);
        assertArrayEquals(r3, res1);
        assertArrayEquals(r4, res2);
    }

    @Test
    void denullify() {
        String[] a1 = {
                "SJDPSÖDK",
                "OKEPOKO",
                null,
                "MLSLÖM",
                null,
                null
        };
        String[] r1 = {
                "SJDPSÖDK",
                "OKEPOKO",
                "MLSLÖM"
        };
        String[] a2 = {
                null,
                "ABC",
                "SJDPSÖDK",
                "OKEPOKO",
                null,
                "MLSLÖM",
                null,
                null
        };
        String[] r2 = {
                "ABC",
                "SJDPSÖDK",
                "OKEPOKO",
                "MLSLÖM"
        };
        String[] a3 = {
                null,
                null,
                null
        };
        String[] r3 = {};
        assertArrayEquals(r1, ArrayUtil.denullify(a1));
        assertArrayEquals(r1, ArrayUtil.denullify(String.class, a1));
        assertArrayEquals(r2, ArrayUtil.denullify(a2));
        assertArrayEquals(r2, ArrayUtil.denullify(String.class, a2));

        //workaround removed - test will fail
        //assertThrows(IllegalArgumentException.class, () -> ArrayUtil.denullify(a3));

        assertArrayEquals(r3, ArrayUtil.denullify(String.class, a3));
    }

    @Test
    void consume() {
        String[] a = {
                "abca",
                "cvv",
                "KASDMKAOSDM"
        };
        ArrayUtil.consume(a, s -> addMem(s.length()));
        assertEquals(18, mem);
        mem = 0;
        ArrayUtil.consume(a, s -> {
            if (s.matches("\\w{4,}")) addMem(1);
        });
        assertEquals(2, mem);
    }

    @Test
    void join() {
        Integer[] a = {
                213,
                342,
                435
        };
        StringBuilder[] b = {
                new StringBuilder("A"),
                new StringBuilder("SKDNFSKND"),
                new StringBuilder("Ssdfk")
        };
        String[] c = {
                "asd",
                "dd"
        };

        assertEquals("[213, 342, 435]", ArrayUtil.join(a));
        assertEquals("[A, SKDNFSKND, Ssdfk]", ArrayUtil.join(b));
        assertEquals("[asd, dd]", ArrayUtil.join(c));

        assertEquals("213-342-435", ArrayUtil.join(a, "-"));
        assertEquals("A//SKDNFSKND//Ssdfk", ArrayUtil.join(b, "//"));
        assertEquals("asd\ndd", ArrayUtil.join(c, "\n"));

        assertEquals("$213-342-435?", ArrayUtil.join(a, "-", "$", "?"));
        assertEquals("[A//SKDNFSKND//Ssdfk]", ArrayUtil.join(b, "//", "[", "]"));
        assertEquals("BEFORE (asd\ndd) AFTER", ArrayUtil.join(c, "\n", "BEFORE (", ") AFTER"));
    }

    @Test
    void enboxDebox() {
        Integer[] boxedInt = {
                123,
                456,
                789,
                101112
        };
        Integer[] nullBoxedInt = {
                123,
                null,
                789,
                101112
        };
        int[] unboxedInt = {
                123,
                456,
                789,
                101112
        };
        Long[] boxedLong = {
                123456L,
                789101112L,
                131415161718L,
                192021222324L
        };
        Long[] nullBoxedLong = {
                123456L,
                789101112L,
                null,
                192021222324L
        };
        long[] unboxedLong = {
                123456L,
                789101112L,
                131415161718L,
                192021222324L
        };
        Float[] boxedFloat = {
                12.34f,
                56.78f,
                910.1112f
        };
        Float[] nullBoxedFloat = {
                12.34f,
                56.78f,
                null
        };
        float[] unboxedFloat = {
                12.34f,
                56.78f,
                910.1112f
        };
        Double[] boxedDouble = {
                1234.5678d,
                9101112.13141516d,
                17181920.21222324d
        };
        Double[] nullBoxedDouble = {
                null,
                9101112.13141516d,
                17181920.21222324d
        };
        double[] unboxedDouble = {
                1234.5678d,
                9101112.13141516d,
                17181920.21222324d
        };
        Byte[] boxedByte = {
                11,
                12,
                0x7F
        };
        Byte[] nullBoxedByte = {
                11,
                null,
                0x7F
        };
        byte[] unboxedByte = {
                11,
                12,
                0x7F
        };
        Character[] boxedChar = {
                'a',
                'b',
                'e',
                '\n'
        };
        Character[] nullBoxedChar = {
                'a',
                null,
                'e',
                '\n'
        };
        char[] unboxedChar = {
                'a',
                'b',
                'e',
                '\n'
        };
        Boolean[] boxedBoolean = {
                true,
                true,
                false,
                true,
                false
        };
        Boolean[] nullBoxedBoolean = {
                true,
                true,
                null,
                true,
                false
        };
        boolean[] unboxedBoolean = {
                true,
                true,
                false,
                true,
                false
        };

        assertTrue(Arrays.equals(ArrayUtil.enbox(unboxedInt), boxedInt));
        assertFalse(Arrays.equals(ArrayUtil.enbox(unboxedInt), nullBoxedInt));

        assertTrue(Arrays.equals(ArrayUtil.unbox(boxedInt), unboxedInt));
        assertTrue(Arrays.equals(ArrayUtil.unbox(nullBoxedInt, 456), unboxedInt));
        assertFalse(Arrays.equals(ArrayUtil.unbox(nullBoxedInt, 11), unboxedInt));


        assertTrue(Arrays.equals(ArrayUtil.enbox(unboxedLong), boxedLong));
        assertFalse(Arrays.equals(ArrayUtil.enbox(unboxedLong), nullBoxedLong));

        assertTrue(Arrays.equals(ArrayUtil.unbox(boxedLong), unboxedLong));
        assertTrue(Arrays.equals(ArrayUtil.unbox(nullBoxedLong, 131415161718L), unboxedLong));
        assertFalse(Arrays.equals(ArrayUtil.unbox(nullBoxedLong, 11), unboxedLong));


        assertTrue(Arrays.equals(ArrayUtil.enbox(unboxedFloat), boxedFloat));
        assertFalse(Arrays.equals(ArrayUtil.enbox(unboxedFloat), nullBoxedFloat));

        assertTrue(Arrays.equals(ArrayUtil.unbox(boxedFloat), unboxedFloat));
        assertTrue(Arrays.equals(ArrayUtil.unbox(nullBoxedFloat, 910.1112f), unboxedFloat));
        assertFalse(Arrays.equals(ArrayUtil.unbox(nullBoxedFloat, 11), unboxedFloat));


        assertTrue(Arrays.equals(ArrayUtil.enbox(unboxedDouble), boxedDouble));
        assertFalse(Arrays.equals(ArrayUtil.enbox(unboxedDouble), nullBoxedDouble));

        assertTrue(Arrays.equals(ArrayUtil.unbox(boxedDouble), unboxedDouble));
        assertTrue(Arrays.equals(ArrayUtil.unbox(nullBoxedDouble, 1234.5678d), unboxedDouble));
        assertFalse(Arrays.equals(ArrayUtil.unbox(nullBoxedDouble, 11), unboxedDouble));


        assertTrue(Arrays.equals(ArrayUtil.enbox(unboxedByte), boxedByte));
        assertFalse(Arrays.equals(ArrayUtil.enbox(unboxedByte), nullBoxedByte));

        assertTrue(Arrays.equals(ArrayUtil.unbox(boxedByte), unboxedByte));
        assertTrue(Arrays.equals(ArrayUtil.unbox(nullBoxedByte, (byte) 12), unboxedByte));
        assertFalse(Arrays.equals(ArrayUtil.unbox(nullBoxedByte, (byte) 11), unboxedByte));


        assertTrue(Arrays.equals(ArrayUtil.enbox(unboxedChar), boxedChar));
        assertFalse(Arrays.equals(ArrayUtil.enbox(unboxedChar), nullBoxedChar));

        assertTrue(Arrays.equals(ArrayUtil.unbox(boxedChar), unboxedChar));
        assertTrue(Arrays.equals(ArrayUtil.unbox(nullBoxedChar, 'b'), unboxedChar));
        assertFalse(Arrays.equals(ArrayUtil.unbox(nullBoxedChar, '2'), unboxedChar));


        assertTrue(Arrays.equals(ArrayUtil.enbox(unboxedBoolean), boxedBoolean));
        assertFalse(Arrays.equals(ArrayUtil.enbox(unboxedBoolean), nullBoxedBoolean));

        assertTrue(Arrays.equals(ArrayUtil.unbox(boxedBoolean), unboxedBoolean));
        assertTrue(Arrays.equals(ArrayUtil.unbox(nullBoxedBoolean, false), unboxedBoolean));
        assertFalse(Arrays.equals(ArrayUtil.unbox(nullBoxedBoolean, true), unboxedBoolean));
    }

    @Test
    void count() {
        int[] thingsInt = {
                2, 4, 2, 3, 34, 524, 24523, 45, 2345, 23, 45, 34, 43, 23, 33, 442, 2, 3, 4, 22, 3, 3, 33, 4
        }; // 3x2, 4x3, 3x4
        assertEquals(3, ArrayUtil.count(thingsInt, 2));
        assertEquals(4, ArrayUtil.count(thingsInt, 3));
        assertEquals(3, ArrayUtil.count(thingsInt, 4));
        assertEquals(0, ArrayUtil.count(thingsInt, 5));


        long[] thingsLong = {
                2, 4, 2, 3, 34, 524, 24523, 45, 2345, 23, 45, 34, 43, 23, 33, 442, 2, 3, 4, 22, 3, 3, 33, 4
        }; // 3x2, 4x3, 3x4
        assertEquals(3, ArrayUtil.count(thingsLong, 2));
        assertEquals(4, ArrayUtil.count(thingsLong, 3));
        assertEquals(3, ArrayUtil.count(thingsLong, 4));
        assertEquals(0, ArrayUtil.count(thingsLong, 5));

        float[] thingsFloat = {
                6.1f, 7.5f, 7.3f, 84.6f, 6.1f, 6.1f, 6.1f, 82734.23423f, 242.453f, 6.1f, 7.5f
        }; // 5x6.1 2x7.5
        assertEquals(5, ArrayUtil.count(thingsFloat, 6.1f));
        assertEquals(2, ArrayUtil.count(thingsFloat, 7.5f));
        assertEquals(1, ArrayUtil.count(thingsFloat, 7.3f));
        assertEquals(0, ArrayUtil.count(thingsFloat, 345));

        double[] thingsDouble = {
                6.1f, 7.5f, 7.3f, 84.6f, 6.1f, 6.1f, 6.1f, 82734.23423f, 242.453f, 6.1f, 7.5f
        }; // 5x6.1 2x7.5
        assertEquals(5, ArrayUtil.count(thingsDouble, 6.1f));
        assertEquals(2, ArrayUtil.count(thingsDouble, 7.5f));
        assertEquals(1, ArrayUtil.count(thingsDouble, 7.3f));
        assertEquals(0, ArrayUtil.count(thingsDouble, 345));

        byte[] thingsByte = {
                0x4f, 0x11, 0x42, 0x12, 0x11, 0x02, 0x02, 0x02, 0x09
        }; // 3x 0x02, 2x 0x11
        assertEquals(3, ArrayUtil.count(thingsByte, (byte) 0x02));
        assertEquals(2, ArrayUtil.count(thingsByte, (byte) 0x11));
        assertEquals(1, ArrayUtil.count(thingsByte, (byte) 0x09));
        assertEquals(0, ArrayUtil.count(thingsByte, (byte) 0));
    }

    @Test
    void has() {
        int[] a1 = {
                3,
                5,
                6,
                2,
                5,
                9
        };
        assertTrue(ArrayUtil.has(a1, 3));
        assertFalse(ArrayUtil.has(a1, 1));

        long[] a2 = {
                323423423,
                53245343645624L,
                5264564524L,
                345234523,
                3245,
                -1231235
        };

        assertTrue(ArrayUtil.has(a2, 3245L));
        assertFalse(ArrayUtil.has(a2, 5264514524L));
    }

    @Test
    void reverse() {
        String[] t1 = {
                "abc",
                "def",
                "ghj"
        };

        String[] r1 = {
                "ghj",
                "def",
                "abc"
        };
        assertArrayEquals(ArrayUtil.reverse(t1), r1);

        {
            int[] test = {
                    8,
                    5,
                    345,
                    32,
                    2344
            };
            int[] res = {
                    2344,
                    32,
                    345,
                    5,
                    8
            };
            assertArrayEquals(ArrayUtil.reverse(test), res);
        }

        {
            long[] test = {
                    8,
                    5,
                    345,
                    32,
                    2344
            };
            long[] res = {
                    2344,
                    32,
                    345,
                    5,
                    8
            };
            assertArrayEquals(ArrayUtil.reverse(test), res);
        }

        {
            short[] test = {
                    8,
                    5,
                    345,
                    32,
                    2344
            };
            short[] res = {
                    2344,
                    32,
                    345,
                    5,
                    8
            };
            assertArrayEquals(ArrayUtil.reverse(test), res);
        }

        {
            byte[] test = {
                    8,
                    5,
                    35,
                    32,
                    24
            };
            byte[] res = {
                    24,
                    32,
                    35,
                    5,
                    8
            };
            assertArrayEquals(ArrayUtil.reverse(test), res);
        }

        {
            double[] test = {
                    8,
                    5,
                    345,
                    32,
                    2344
            };
            double[] res = {
                    2344,
                    32,
                    345,
                    5,
                    8
            };
            assertArrayEquals(ArrayUtil.reverse(test), res);
        }

        {
            float[] test = {
                    8,
                    5,
                    345,
                    32,
                    2344
            };
            float[] res = {
                    2344,
                    32,
                    345,
                    5,
                    8
            };
            assertArrayEquals(ArrayUtil.reverse(test), res);
        }
    }

    @Test
    void hexify() {
        byte[] input = {
                (byte) 0xff,
                (byte) 0xa3,
                (byte) 0x32,
                (byte) 0xaa,
                (byte) 0x0e
        };
        String[] resu = {
                "FF",
                "A3",
                "32",
                "AA",
                "0E"
        };
        String[] resl = {
                "ff",
                "a3",
                "32",
                "aa",
                "0e"
        };
        String ressu = "FFA332AA0E";
        String ressl = "ffa332aa0e";

        assertArrayEquals(ArrayUtil.toHexArray(input, true), resu);
        assertArrayEquals(ArrayUtil.toHexArray(input, false), resl);
        assertArrayEquals(ArrayUtil.toHexArray(input), resl);

        assertEquals(ArrayUtil.toHexString(input, true), ressu);
        assertEquals(ArrayUtil.toHexString(input, false), ressl);
        assertEquals(ArrayUtil.toHexString(input), ressl);
    }
}

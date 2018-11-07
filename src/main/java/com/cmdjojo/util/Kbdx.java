package com.cmdjojo.util;

import javax.swing.JOptionPane;

/**
 * Some useful utils
 *
 * @author CMDJojo (Jonathan Widen)
 * @version 1.1-SNAPSHOT
 */

public final class Kbdx {
    /**
     * Prints a newline
     */
    public static void newline() {
        newline(1);
    }

    /**
     * Prints out specified amount of newlines
     *
     * @param amount Amount of new lines
     */
    public static void newline(int amount) {
        StringBuilder print = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            print.append("\n");
        }
        System.out.print(print.toString());
    }

    /**
     * Takes ms and returns a string containing d,h,m,s,ms
     *
     * @param input The value to convert to string
     * @return String, ex "1h 32m 2s 313ms"
     */

    public static String msToString(long input) {
        return msToString(input, -1);
    }

    /**
     * Takes ms and returns a string containing d,h,m,s,ms
     *
     * @param input The value to convert to string
     * @param args  Max amount of args to be displayed, or -1 to display all
     * @return String, ex "1h 32m 2s 313ms"
     */
    public static String msToString(long input, int args) {
        int maxArgs = 6;
        if (args == -1)
            args = maxArgs;
        if (args < 1 || args > maxArgs)
            throw new IndexOutOfBoundsException("Arg amount " + args + " out of range (1-" + maxArgs + ")");
        long ms = Math.abs(input);
        long s = ms / 1000;
        ms %= 1000;
        long m = s / 60;
        s %= 60;
        long h = m / 60;
        m %= 60;
        long d = h / 24;
        h %= 24;
        long y = d / 365;
        d %= 365;
        String returnStr = "";
        int level = 0; // 0-ms, 1-s, 2-m, 3-h, 4-d, 5-y
        if (s > 0)
            level = 1;
        if (m > 0)
            level = 2;
        if (h > 0)
            level = 3;
        if (d > 0)
            level = 4;
        if (y > 0)
            level = 5;

        if (level >= 5 && (level - args) < 5)
            returnStr += y + "y ";

        if (level >= 4 && (level - args) < 4)
            returnStr += d + "d ";

        if (level >= 3 && (level - args) < 3)
            returnStr += h + "h ";

        if (level >= 2 && (level - args) < 2)
            returnStr += m + "m ";

        if (level >= 1 && (level - args) < 1)
            returnStr += s + "s ";

        if ((level - args) < 0)
            returnStr += ms + "ms ";

        if (input < 0) returnStr = "-" + returnStr;

        return returnStr.substring(0, returnStr.length() - 1);
    }

    /**
     * Floor a double into an long
     *
     * @param num The double to be floored
     * @return The floored double
     */

    public static long floor(double num) {
        return Math.round(Math.floor(num));
    }

    /**
     * Cieled a double into an long
     *
     * @param num The double to be cieled
     * @return The cieled double
     */

    public static long ceil(double num) {
        return Math.round(Math.ceil(num));
    }

    /**
     * Rounds a number to correct amount of decimals
     *
     * @param num The number to round
     * @param dec The amount of decimals
     * @return Double of the number correctly rounded
     */

    public static double round(double num, int dec) {
        return Math.round(Math.pow(10.0, dec) * num) / Math.pow(10.0, dec);
    }

    /**
     * Shows a message dialog with desired text and default title "Message"
     *
     * @param text The text to display
     */
    public static void message(String text) {
        message(text, "Message");
    }

    /**
     * Shows a message dialog with desired text and title
     *
     * @param text  The text to display
     * @param title The title of the dialog box
     */
    public static void message(String text, String title) {
        JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a message dialog with a dropdown menu with the desired options
     *
     * @param force   Re-prompt if cancelled?
     * @param options String array with options avalible for the dropdown menu
     * @return The option selected when continuing, or null if none and force is
     * false
     */

    public static String dropdown(boolean force, String[] options) {
        return dropdown(force, options, "Select an option from the menu", "Select an option");
    }

    /**
     * Shows a message dialog with a dropdown menu with the desired options
     *
     * @param force   Re-prompt if cancelled?
     * @param options String array with options avalible for the dropdown menu
     * @param message The message in the dialog box
     * @return The option selected when continuing, or null if none and force is
     * false
     */

    public static String dropdown(boolean force, String[] options, String message) {
        return dropdown(force, options, message, "Select an option");
    }

    /**
     * Shows a message dialog with a dropdown menu with the desired options
     *
     * @param force   Re-prompt if cancelled?
     * @param options String array with options avalible for the dropdown menu
     * @param message The message in the dialog box
     * @param title   The title of the dialog box
     * @return The option selected when continuing, or null if none and force is
     * false
     */

    public static String dropdown(boolean force, String[] options, String message, String title) {
        String answer = (String) JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        if (answer == null && force)
            return dropdown(true, options, message, title);
        return answer;
    }

    /**
     * Prompts for an integer
     *
     * @return The integer inputed
     */

    public static int readInt() {
        return readInt(true, "Input a number", "Input");
    }

    /**
     * Prompts for an integer
     *
     * @param force Prompt again if no integer is present?
     * @return The integer inputed, or 0 if no integer is present and force is false
     */

    public static int readInt(boolean force) {
        return readInt(force, "Input a number", "Input");
    }

    /**
     * Prompts for an integer
     *
     * @param force   Prompt again if no integer is present?
     * @param message The message of the prompt
     * @return The integer inputed, or 0 if no integer is present and force is false
     */

    public static int readInt(boolean force, String message) {
        return readInt(force, message, "Input");
    }

    /**
     * Prompts for an integer
     *
     * @param force   Prompt again if no integer is present?
     * @param message The message of the prompt
     * @param title   The title of the dialog box
     * @return The integer inputed, or 0 if no integer is present and force is false
     */

    public static int readInt(boolean force, String message, String title) {
        String result = promptDialog(message, title);
        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException error) {
            if (force)
                return readInt(true, message, title);
            return 0;
        }
    }

    /**
     * Prompts for an integer within a range (min and max incl)
     *
     * @param force   Prompt again if no integer is present?
     * @param message The message of the prompt
     * @param title   The title of the dialog box
     * @param min     The smallest integer allowed
     * @param max     The largest integer allowed
     * @return The integer inputed, or 0 if no integer is present and force is false
     */

    public static int readInt(boolean force, String message, String title, int min, int max) {
        while (true) {
            int result = readInt(force, message, title);
            if (result >= min && result <= max)
                return result;
            else if (!force)
                return 0;
        }
    }

    /**
     * Prompts for an long
     *
     * @return The long inputed
     */

    public static long readLong() {
        return readLong(true, "Input a number", "Input");
    }

    /**
     * Prompts for an long
     *
     * @param force Prompt again if no long is present?
     * @return The long inputed, or 0 if no long is present and force is false
     */

    public static long readLong(boolean force) {
        return readLong(force, "Input a number", "Input");
    }

    /**
     * Prompts for an long
     *
     * @param force   Prompt again if no long is present?
     * @param message The message of the prompt
     * @return The long inputed, or 0 if no long is present and force is false
     */

    public static long readLong(boolean force, String message) {
        return readLong(force, message, "Input");
    }

    /**
     * Prompts for an long
     *
     * @param force   Prompt again if no long is present?
     * @param message The message of the prompt
     * @param title   The title of the dialog box
     * @return The long inputed, or 0 if no long is present and force is false
     */

    public static long readLong(boolean force, String message, String title) {
        String result = promptDialog(message, title);
        try {
            return Long.parseLong(result);
        } catch (NumberFormatException error) {
            if (force)
                return readLong(true, message, title);
            return 0;
        }
    }

    /**
     * Prompts for an long within a range (min and max incl)
     *
     * @param force   Prompt again if no long is present?
     * @param message The message of the prompt
     * @param title   The title of the dialog box
     * @param min     The smallest long allowed
     * @param max     The largest long allowed
     * @return The long inputed, or 0 if no long is present and force is false
     */

    public static long readLong(boolean force, String message, String title, long min, long max) {
        while (true) {
            long result = readLong(force, message, title);
            if (result >= min && result <= max)
                return result;
            else if (!force)
                return 0;
        }
    }

    /**
     * Prompts for a string
     *
     * @param force Prompt again if no string is present?
     * @return The integer inputed, or null if no string is present and force is
     * false
     */

    public static String readString(boolean force) {
        return readString(force, "Input a string", "Input");
    }

    /**
     * Prompts for an string
     *
     * @param force   Prompt again if no string is present?
     * @param message The message of the prompt
     * @return The string inputed, or null if no string is present and force is
     * false
     */

    public static String readString(boolean force, String message) {
        return readString(force, message, "Input");
    }

    /**
     * Prompts for an string
     *
     * @param force   Prompt again if no string is present?
     * @param message The message of the prompt
     * @param title   The title of the dialog box
     * @return The string inputed, or null if no string is present and force is
     * false
     */

    public static String readString(boolean force, String message, String title) {
        String result = promptDialog(message, title);
        if (force && (result == null || result.equals("")))
            return readString(true, message, title);
        return result;
    }

    /**
     * Prompts user with a box with buttons 'Yes' and 'No'
     *
     * @return String matching the name of the button pressed, or null if "esc" was
     * pressed
     */

    public static String readOption() {
        String[] options = {"Yes", "No"};
        return readOption(false, "Continue?", "Continue?", options);
    }

    /**
     * Prompts user with a box with buttons 'Yes' and 'No'
     *
     * @param force Re-prompt if "esc" key was pressed?
     * @return String matching the name of the button pressed, or null if "esc" was
     * pressed and force == false
     */

    public static String readOption(boolean force) {
        String[] options = {"Yes", "No"};
        return readOption(force, "Continue?", "Continue?", options);
    }

    /**
     * Prompts user with a box with buttons 'Yes' and 'No'
     *
     * @param force   Re-prompt if "esc" key was pressed?
     * @param message The message in the box
     * @return String matching the name of the button pressed, or null if "esc" was
     * pressed and force == false
     */

    public static String readOption(boolean force, String message) {
        String[] options = {"Yes", "No"};
        return readOption(force, message, "Continue?", options);
    }

    /**
     * Prompts user with a box with buttons 'Yes' and 'No'
     *
     * @param force   Re-prompt if "esc" key was pressed?
     * @param message The message in the box
     * @param title   The title of the prompt
     * @return String matching the name of the button pressed, or null if "esc" was
     * pressed and force == false
     */

    public static String readOption(boolean force, String message, String title) {
        String[] options = {"Yes", "No"};
        return readOption(force, message, title, options);
    }

    /**
     * Prompts user with a box with customizable buttons, such as 'yes' or 'no'
     *
     * @param force   Re-prompt if "esc" key was pressed?
     * @param message The message in the box
     * @param title   The title of the prompt
     * @param options String[] with the buttons
     * @return String matching the name of the button pressed, or null if "esc" was
     * pressed and force == false
     */

    public static String readOption(boolean force, String message, String title, String[] options) {
        int answer = JOptionPane.showOptionDialog(null, message, title, JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (answer == -1) {
            if (force)
                return readOption(true, message, title, options);
            return null;
        }
        return options[answer];
    }

    private static String promptDialog(String message, String title) {
        return JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Returns a random integer between 0 (incl) and max (excl)
     * <p>
     * If max is negative, the same rule applies, including 0 and excluding max, which is negative
     *
     * @param max The integer above the largest possible integer to return
     * @return int, the random number
     */

    public static int random(int max) {
        return (int) (Math.random() * max);
    }

    /**
     * Returns a random integer between min and max, following these rules:
     * <ul>
     * <li> Min and max can be swapped around however you want, min doesnt have to be smaller than max
     * <li> If min == max, that value will be returned
     * <li> In other cases, a random value from the 'range' is returned:
     * <ul>
     * <li> If min and max are positive (the normal case), the range includes the lowest value but not the highest
     * <li> If min and max are negative, the range includes the highest value (the value closest to 0), but excludes the lowest value (the most negative value)
     * <li> If one value of min and max is negative, and the other one is positive, both is excluded
     * </ul>
     * </ul>
     * You may switch arguments, so min &gt; max, then the highest one will be excl and the lowest will be incl
     * If negative, the lowest (most negative) argument is excl, and the highest is incl
     *
     * @param min The smallest possible integer to return
     * @param max The integer above the largest possible integer to return
     * @return int, the random number
     */

    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    /**
     * Returns the average number in an array of integers
     *
     * @param array The array to get the average double
     * @return The average in the array, as an double
     */

    public static double average(int[] array) {
        double sum = 0.0d;
        for (int x : array) {
            sum += x;
        }
        return sum / array.length;
    }

    /**
     * Returns the average number in an array of longs
     *
     * @param array The array to get the average double from
     * @return The average in the array, as an double
     */

    public static double average(long[] array) {
        double sum = 0.0d;
        for (long x : array) {
            sum += x;
        }
        return sum / array.length;
    }

    /**
     * Returns whenever an int is present in an array of ints
     *
     * @param array  The array to search
     * @param target The target to search for
     * @return True if there is at least one element matching target, otherwise
     * false
     */

    public static boolean contains(int[] array, int target) {
        for (int x : array) {
            if (x == target)
                return true;
        }
        return false;
    }

    /**
     * Returns whenever an long is present in an array of longs
     *
     * @param array  The array to search
     * @param target The target to search for
     * @return True if there is at least one element matching target, otherwise
     * false
     */

    public static boolean contains(long[] array, long target) {
        for (long x : array) {
            if (x == target)
                return true;
        }
        return false;
    }

    /**
     * Returns whenever an object is present in an array of objects. Using
     * object.equals(target object) method
     *
     * @param array  The array to search
     * @param target The target to search for
     * @return True if there is at least one element matching target, otherwise
     * false
     */

    public static boolean contains(Object[] array, Object target) {
        for (Object x : array) {
            if (x.equals(target))
                return true;
        }
        return false;
    }
}

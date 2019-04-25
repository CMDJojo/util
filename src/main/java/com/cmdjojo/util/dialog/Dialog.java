package com.cmdjojo.util.dialog;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.function.Predicate;

/**
 * Used to customize and create re-useable dialogs.
 * <p>The easiest way to create a dialog is to use any of the constructing methods:
 * <ul>
 * <li>{@linkplain #message(String, String)}</li>
 * <li>{@linkplain #getByte(String, String, String)}</li>
 * <li>{@linkplain #getShort(String, String, String)}</li>
 * <li>{@linkplain #getInt(String, String, String)}</li>
 * <li>{@linkplain #getLong(String, String, String)}</li>
 * <li>{@linkplain #getFloat(String, String, String)}</li>
 * <li>{@linkplain #getDouble(String, String, String)}</li>
 * <li>{@linkplain #dropdown(String, String, Object[], int)}</li>
 * <li>{@linkplain #buttons(String, String, Object[], int)}</li>
 * </ul>
 * <p>The constructing methods provide some basic parameters for some basic customization.
 * For even more customization, see setter methods.
 * <p>Use {@linkplain #show()} to display the dialog. By deafult, it is showed in a seperate thread,
 * thus not blocking the main thread. You should prefferably use {@linkplain #await()} if you want to
 * wait for it, or use {@linkplain Object#wait()}. You can also make it run on the caller thread, by
 * using {@link #setShowAsync(boolean) setShowAsync(false)}.
 * <p>While the dialog is displayed, all the setter methods are locked, meaning that you can only change
 * any properties of the dialog when the dialog did finish.
 * <p>Important to notice is that if the dialog is run in a seperate thread, the lock may not be aquired instantly,
 * meaning that calling setter methods immedialtely after {@link #show() show} might actually run.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class Dialog {
    /*
      Warning:
      Everything that is changing the dialog properties
      should lock on this object.
      When the dialog is displayed, this object is locked
      so no properties can be changed.
      'This' object is only locked on in two synchronized
      methods, #await() and #finish(), which is used
      for #wait() and #notifyAll() respectivly.
     */
    private final Object lock;

    // Dialog properties
    private String text, title, defaultValue;
    private Type type;
    private Importance importance;
    private Buttons buttons;
    private Input input;
    private ReturnType returnType;
    private Icon icon;
    private Object[] options;
    private @Nullable Predicate<Object> filter;
    private int selectedOptionIndex;
    private Component parent;
    private boolean acceptNull;
    private boolean showAsync;

    // Running status values
    private boolean finished;
    private boolean running;
    private Object result;

    /**
     * Creates a new {@code Dialog} object with standard options.
     * <p>The options used are:
     * <ul>
     * <li>No text (use {@linkplain #setText(String)})</li>
     * <li>No title (use {@linkplain #setTitle(String)})</li>
     * <li>No default (prefilled) value (use {@linkplain #setDefaultValue(String)})</li>
     * <li>Message type (use {@linkplain #setType(Type)})</li>
     * <li>Plain importance type (use {@linkplain #setImportance(Importance)})</li>
     * <li>Default button layout (use {@linkplain #setButtons(Buttons)})</li>
     * <li>String input type (not used since type is set to {@link Type#MESSAGE message}) (use {@linkplain #setInput(Input)})</li>
     * <li>Object return type of buttons/dropdown dialogs (not used since type is set to {@link Type#MESSAGE message}) (use {@linkplain #setReturnType(ReturnType)})</li>
     * <li>No icon (use {@linkplain #setIcon(Icon)})</li>
     * <li>No options for buttons/dropdown type (use {@linkplain #setOptions(Object[])})</li>
     * <li>No filter (use {@linkplain #setFilter(Predicate)})</li>
     * <li>No parent (use {@linkplain #setParent(Component)})</li>
     * <li>Not accepting {@code null} (use {@linkplain #setAcceptNull(boolean)})</li>
     * <li>Asynchronus display of type (in seperate thread) (use {@linkplain #setShowAsync(boolean)})</li>
     * </ul>
     */
    public Dialog() {
        this.text = "";
        this.title = "";
        this.defaultValue = "";
        this.type = Type.MESSAGE;
        this.importance = Importance.PLAIN;
        this.buttons = Buttons.DEFAULT;
        this.input = Input.STRING;
        this.returnType = ReturnType.OBJECT;
        this.icon = null;
        this.options = new Object[]{};
        this.filter = null;
        this.parent = null;
        this.acceptNull = false;
        this.showAsync = true;

        lock = new Object();
    }

    /**
     * Creates an empty message {@code Type} object
     *
     * @return the newly created {@code Type} object
     */
    @Contract(value = " -> new", pure = true)
    public static Dialog message() {
        return message("");
    }

    /**
     * Creates a message {@code Type} object with the provided text
     *
     * @param text the importance to display
     * @return the newly created {@code Type} object
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog message(String text) {
        return message(text, "Message");
    }

    /**
     * Creates a message {@code Type} object with the provided text and title
     *
     * @param text  the importance to display
     * @param title the title of the type box
     * @return the newly created {@code Type} object
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog message(String text, String title) {
        return new Dialog()
                .setText(text)
                .setTitle(title);
    }

    /**
     * Creates a button {@code Type} object with each element represented as a button
     * <p>Use {@linkplain #setButtons(Object[])} to set what elements to display
     *
     * @return the newly created {@code Type} object
     * @see #setButtons(Object[])
     */
    @Contract(value = " -> new", pure = true)
    public static Dialog buttons() {
        return buttons("Select a button");
    }

    /**
     * Creates a button {@code Type} object with each element represented as a button
     * <p>Use {@linkplain #setButtons(Object[])} to set what elements to display
     *
     * @param text The text in the type box
     * @return the newly created {@code Type} object
     * @see #setButtons(Object[])
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog buttons(String text) {
        return buttons(text, "Select");
    }

    /**
     * Creates a button {@code Type} object with each element represented as a button
     * <p>Use {@linkplain #setButtons(Object[])} to set what elements to display
     *
     * @param text  The text in the type box
     * @param title The title of the type box
     * @return the newly created {@code Type} object
     * @see #setButtons(Object[])
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog buttons(String text, String title) {
        return buttons(text, title, new String[0]);
    }

    /**
     * Creates a button {@code Type} object with each element represented as a button
     *
     * @param text    The text in the type box
     * @param title   The title of the type box
     * @param options The options (the buttons)
     * @return the newly created {@code Type} object
     * @see #setButtons(Object[])
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Dialog buttons(String text, String title, Object[] options) {
        return buttons(text, title, options, 0);
    }

    /**
     * Creates a button {@code Type} object with each element represented as a button
     *
     * @param text        The text in the type box
     * @param title       The title of the type box
     * @param options     The options (the buttons)
     * @param preselected The index of the element which should be preselected
     * @return the newly created {@code Type} object
     * @see #setButtons(Object[])
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static Dialog buttons(String text, String title, Object[] options, int preselected) {
        return new Dialog()
                .setText(text)
                .setTitle(title)
                .setType(Type.BUTTONS)
                .setButtons(options)
                .setSelectedOptionIndex(preselected);
    }

    /**
     * Creates a dropdown {@code Type} object with all options in a list
     * <p>Use {@linkplain #setDropdowns(Object[])} to set what options to display
     *
     * @return the newly created {@code Type} object
     * @see #setDropdowns(Object[])
     */
    @Contract(value = " -> new", pure = true)
    public static Dialog dropdown() {
        return dropdown("Select an option");
    }

    /**
     * Creates a dropdown {@code Type} object with all options in a list
     * <p>Use {@linkplain #setDropdowns(Object[])} to set what options to display
     *
     * @param text the text of the type box
     * @return the newly created {@code Type} object
     * @see #setDropdowns(Object[])
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog dropdown(String text) {
        return dropdown(text, "Select");
    }

    /**
     * Creates a dropdown {@code Type} object with all options in a list
     * <p>Use {@linkplain #setDropdowns(Object[])} to set what options to display
     *
     * @param text  the text of the type box
     * @param title the title of the type box
     * @return the newly created {@code Type} object
     * @see #setDropdowns(Object[])
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog dropdown(String text, String title) {
        return dropdown(text, title, new String[0]);
    }

    /**
     * Creates a dropdown {@code Type} object with all options in a list
     * <p>Use {@linkplain #setDropdowns(Object[])} to set what options to display
     *
     * @param text    the text of the type box
     * @param title   the title of the type box
     * @param options the options to have in the list
     * @return the newly created {@code Type} object
     * @see #setDropdowns(Object[])
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Dialog dropdown(String text, String title, Object[] options) {
        return dropdown(text, title, options, 0);
    }

    /**
     * Creates a dropdown {@code Type} object with all options in a list
     * <p>Use {@linkplain #setDropdowns(Object[])} to set what options to display
     *
     * @param text        the text of the type box
     * @param title       the title of the type box
     * @param options     the options to have in the list
     * @param preselected The index of the element which should be preselected
     * @return the newly created {@code Type} object
     * @see #setDropdowns(Object[])
     */
    @Contract(value = "_, _, _, _ -> new", pure = true)
    public static Dialog dropdown(String text, String title, Object[] options, int preselected) {
        return new Dialog()
                .setText(text)
                .setTitle(title)
                .setType(Type.DROPDOWN)
                .setDropdowns(options)
                .setSelectedOptionIndex(preselected);
    }

    /**
     * Creates a prompt {@code Type} object which accepts any string
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     * <p>By default, the text box is empty. You can set its value with {@linkplain #setDefaultValue(String)}
     *
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     * @see #setDefaultValue(String)
     */
    @Contract(value = " -> new", pure = true)
    public static Dialog string() {
        return string("Input a string");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any string
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     * <p>By default, the text box is empty. You can set its value with {@linkplain #setDefaultValue(String)}
     *
     * @param text the text of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     * @see #setDefaultValue(String)
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog string(String text) {
        return string(text, "Input");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any string
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     * <p>By default, the text box is empty. You can set its value with {@linkplain #setDefaultValue(String)}
     *
     * @param text  the text of the type box
     * @param title the title of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     * @see #setDefaultValue(String)
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog string(String text, String title) {
        return string(text, title, "");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any string
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text         the text of the type box
     * @param title        the title of the type box
     * @param defaultValue the prefilled value of the box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Dialog string(String text, String title, String defaultValue) {
        return new Dialog()
                .setText(text)
                .setTitle(title)
                .setType(Type.PROMPT)
                .setInput(Input.STRING)
                .setDefaultValue(defaultValue);
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code int}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     * <p>By default, the text box is empty. You can set its value with {@linkplain #setDefaultValue(String)}
     *
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     * @see #setDefaultValue(String)
     */
    @Contract(value = " -> new", pure = true)
    public static Dialog getInt() {
        return getInt("Input a number");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code int}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     * <p>By default, the text box is empty. You can set its value with {@linkplain #setDefaultValue(String)}
     *
     * @param text the text of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     * @see #setDefaultValue(String)
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog getInt(String text) {
        return getInt(text, "Input");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code int}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     * <p>By default, the text box is empty. You can set its value with {@linkplain #setDefaultValue(String)}
     *
     * @param text  the text of the type box
     * @param title the title of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     * @see #setDefaultValue(String)
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog getInt(String text, String title) {
        return getInt(text, title, "");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code int}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text         the text of the type box
     * @param title        the title of the type box
     * @param defaultValue the prefilled value of the box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Dialog getInt(String text, String title, String defaultValue) {
        return new Dialog()
                .setText(text)
                .setTitle(title)
                .setType(Type.PROMPT)
                .setInput(Input.INT)
                .setDefaultValue(defaultValue);
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code long}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = " -> new", pure = true)
    public static Dialog getLong() {
        return getLong("Input a number");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code long}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text the text of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog getLong(String text) {
        return getLong(text, "Input");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code long}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text  the text of the type box
     * @param title the title of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog getLong(String text, String title) {
        return getLong(text, title, "");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code long}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text         the text of the type box
     * @param title        the title of the type box
     * @param defaultValue the prefilled value of the box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Dialog getLong(String text, String title, String defaultValue) {
        return new Dialog()
                .setText(text)
                .setTitle(title)
                .setType(Type.PROMPT)
                .setInput(Input.LONG)
                .setDefaultValue(defaultValue);
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code float}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = " -> new", pure = true)
    public static Dialog getFloat() {
        return getFloat("Input a number");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code float}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text the text of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog getFloat(String text) {
        return getFloat(text, "Input");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code float}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text  the text of the type box
     * @param title the title of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog getFloat(String text, String title) {
        return getFloat(text, title, "");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code float}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text         the text of the type box
     * @param title        the title of the type box
     * @param defaultValue the prefilled value of the box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Dialog getFloat(String text, String title, String defaultValue) {
        return new Dialog()
                .setText(text)
                .setTitle(title)
                .setType(Type.PROMPT)
                .setInput(Input.FLOAT)
                .setDefaultValue(defaultValue);
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code double}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog getDouble() {
        return getDouble("Input a number");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code double}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text the text of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog getDouble(String text) {
        return getDouble(text, "Input");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code double}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text  the text of the type box
     * @param title the title of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog getDouble(String text, String title) {
        return getDouble(text, title, "");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code double}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text         the text of the type box
     * @param title        the title of the type box
     * @param defaultValue the prefilled value of the box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Dialog getDouble(String text, String title, String defaultValue) {
        return new Dialog()
                .setText(text)
                .setTitle(title)
                .setType(Type.PROMPT)
                .setInput(Input.DOUBLE)
                .setDefaultValue(defaultValue);
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code short}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = " -> new", pure = true)
    public static Dialog getShort() {
        return getShort("Input a number");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code short}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text the text of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog getShort(String text) {
        return getShort(text, "Input");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code short}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text  the text of the type box
     * @param title the title of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog getShort(String text, String title) {
        return getShort(text, title, "");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code short}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text         the text of the type box
     * @param title        the title of the type box
     * @param defaultValue the prefilled value of the box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Dialog getShort(String text, String title, String defaultValue) {
        return new Dialog()
                .setText(text)
                .setTitle(title)
                .setType(Type.PROMPT)
                .setInput(Input.SHORT)
                .setDefaultValue(defaultValue);
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code byte}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = " -> new", pure = true)
    public static Dialog getByte() {
        return getByte("Input a number");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code byte}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text the text of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_ -> new", pure = true)
    public static Dialog getByte(String text) {
        return getByte(text, "Input");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code byte}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text  the text of the type box
     * @param title the title of the type box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static Dialog getByte(String text, String title) {
        return getByte(text, title, "");
    }

    /**
     * Creates a prompt {@code Type} object which accepts any {@code byte}
     * <p>By default, {@code null} isn't allowed, but you can allow it with {@linkplain #setAcceptNull(boolean)}. Then, {@code null} will be returned if the box is closed
     * <p>By default, the input isn't filtered, and all values are allowed. You can add a filter using {@linkplain #setFilter(Predicate)}
     *
     * @param text         the text of the type box
     * @param title        the title of the type box
     * @param defaultValue the prefilled value of the box
     * @return the newly created {@code Type} object
     * @see #setAcceptNull(boolean)
     * @see #setFilter(Predicate)
     */
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Dialog getByte(String text, String title, String defaultValue) {
        return new Dialog()
                .setText(text)
                .setTitle(title)
                .setType(Type.PROMPT)
                .setInput(Input.BYTE)
                .setDefaultValue(defaultValue);
    }

    /**
     * Shows this dialog, waits for it to finish, and thereafter returns the result
     * <p>This pauses the thread until the dialog is finished
     * <p>This works for both synchronus and asynchronus display types
     *
     * @return the result of the dialog
     * @see #show()
     */
    public Object showAndGet() {
        show();
        if (showAsync) await();
        return result;
    }

    /**
     * Shows the dialog
     * <p>If {@linkplain #setShowAsync(boolean)} is set to {@code false}, the dialog will be run on this thread. This thread will thereby be paused.
     * You do not need to {@linkplain #await()} this object, and {@linkplain #getResult()} will return the result immediately after this method returns
     * <p>If {@linkplain #setShowAsync(boolean)} is set to {@code true}, a new thread will be created and the dialog will be run on that thread.
     * This thread will thereby not be paused. Use {@linkplain #await()} to wait for the dialog to finish,
     * and thereafter {@linkplain #getResult()} will return the result immediately
     * <p>That means that you can pipe this method like {@code <dialog>.show().await().getResult()}
     * <p>If you want a quick way to display the dialog and thereafter return the result, use {@linkplain #showAndGet()}
     *
     * @return this dialog
     * @see #showAndGet()
     */
    public Dialog show() {
        synchronized (lock) {
            finished = false;
            running = true;
            if (showAsync) {
                Thread t = new Thread(this::displayDialog);
                t.start();
            } else {
                displayDialog();
            }
            return this;
        }
    }

    /**
     * Displays this dialog and pauses the thread meanwhile.
     * Before displaying it, it aquires lock on {@linkplain #lock}, so no fields can be changed before it is finished
     * When a result is gathered and accepted, {@linkplain #finish(Object)} will be run, and thereafter this method will release its lock
     */
    @SuppressWarnings("MagicConstant")
    private void displayDialog() {
        synchronized (lock) {
            running = true;
            finished = false;
            if (type == Type.MESSAGE) {
                int res;
                do {
                    res = JOptionPane.showOptionDialog(parent, text, title, buttons.getConstant(), importance.getConstant(), icon, null, null);
                } while (res == -1);
                finish(null);


            } else if (type == Type.BUTTONS) {
                // Multiple buttons
                int index = selectedOptionIndex;
                if (index < 0 || index >= options.length) index = 0;
                int res;
                do {
                    res = JOptionPane.showOptionDialog(parent, text, title, buttons.getConstant(), importance.getConstant(), icon, options, options.length > 0 ? options[index] : null);
                } while ((res == -1 && !acceptNull) || !filterVerify(res));
                if (res == -1) finish(null);
                else if (returnType == ReturnType.OBJECT) finish(options[res]);
                else finish(res);


            } else if (type == Type.DROPDOWN) {
                // Dropdown list
                int index = selectedOptionIndex;
                if (index < 0 || index >= options.length) index = 0;
                Object res;
                do {
                    res = JOptionPane.showInputDialog(parent, text, title, importance.getConstant(), icon, options, options.length > 0 ? options[index] : null);
                } while ((res == null && !acceptNull) || !filterVerify(res));
                finish(res);


            } else {
                // Prompt window
                String res;
                boolean shouldContinue;
                do {
                    res = (String) JOptionPane.showInputDialog(parent, text, title, importance.getConstant(), icon, null, defaultValue);

                    shouldContinue = true;
                    if (input.verify(res) && filterVerify(res)) shouldContinue = false;
                    if (res == null && acceptNull) shouldContinue = false;
                } while (shouldContinue);
                finish(input.convert(res));
            }
        }
    }

    /**
     * Checks if the object is accepted by the filter (taken from the {@code filter} field)
     *
     * @param o the object
     * @return {@code true} if the object is {@code null}, the filter is {@code null}, or if the filter accepts the object, otherwise {@code false}
     */
    private boolean filterVerify(Object o) {
        if (o == null) return true;
        if (filter == null) return true;
        try {
            if (type == Type.PROMPT) return filter.test(input.convert((String) o));
            else return filter.test(o);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Callback method for show(), which locks on this object,
     * sets the fields that should be set, and notifies all waiters
     *
     * @param result The result to update the result field with
     */
    private synchronized void finish(@Nullable Object result) {
        finished = true;
        running = false;
        this.result = result;
        notifyAll();
    }

    /**
     * Pauses the current thread until the dialog is finished
     * <p>If the dialog did finish before this method was run, the thread will not be paused
     * <p>Any {@linkplain InterruptedException} will be thrown as a runtime error, since it shouldn't occur
     * <p>This can be run immediately after {@linkplain #show()} even if the dialog is set to run in another thread
     * <p>When the dialog is finished, the object will be returned. It will have the result object updated, so you can run {@linkplain #getResult()} immedtiately on the resulting object
     *
     * @return this dialog
     */
    public synchronized Dialog await() {
        if (finished) return this;
        try {
            wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Returns whether the dialog did finish its last run or not
     * <p>If the dialog was never run, this will return {@code false}
     * <p>This status is set synchronusly when running {@linkplain #show()} or anything that depends on it, even if the dialog is set to run in a different thread
     * <p>You should wait for this method to return {@code true} before using the results gotten from {@linkplain #getResult()}
     * <p>If this method returns {@code true}, the {@linkplain #getResult()} will always return the result from the last run
     *
     * @return {@code true} if the latest run did finish, otherwise {@code false}
     */
    @Contract(pure = true)
    public boolean isFinished() {
        return finished;
    }

    /**
     * Gets the result of the lastest run of the dialog
     * <p>If {@code null}, either the dialog didn't run, or the dialog was exited out and {@linkplain #setAcceptNull(boolean)} was set to accept {@code null}
     * <p>To find out if the dialog is finished or not, see {@linkplain #isFinished()}
     * <p>When {@linkplain #show()} is run, the result isn't cleared, but the {@link #isFinished() finished} status is reset. Thus, this method will return the result of the previous run
     *
     * @return the result of the last dialog run
     */
    @Nullable
    @Contract(pure = true)
    public Object getResult() {
        return result;
    }

    /**
     * Sets what options a {@link Type#DROPDOWN Dropdown} or {@link Type#BUTTONS Buttons} type dialog should display
     * <p>Use {@linkplain #setSelectedOptionIndex(int)} to set what option should be selected at start
     * <p>You need to pass the index of the element in question in this array, to that method, to set that element as preselected
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param options The options
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setOptions(@NotNull Object[] options) {
        synchronized (lock) {
            this.options = options;
            return this;
        }
    }

    /**
     * An alias for {@linkplain #setOptions(Object[])}
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param options the options
     * @return this dialog
     * @see #setOptions(Object[])
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setButtons(@NotNull Object[] options) {
        return setOptions(options);
    }

    /**
     * An alias for {@linkplain #setOptions(Object[])}
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param dropdowns the options
     * @return this dialog
     * @see #setOptions(Object[])
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setDropdowns(@NotNull Object[] dropdowns) {
        return setOptions(dropdowns);
    }

    /**
     * Sets what option should be pre-selected when using a {@link Type#DROPDOWN Dropdown} or {@link Type#BUTTONS Buttons} type object
     * <p>The index should be the index of the element in the Object array set by {@linkplain #setButtons(Object[])} or {@linkplain #setDropdowns(Object[])} that should be preselected
     * <p>If no index is supplied, or if the index is out of bounds, the first element will be selected
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param selectedOptionIndex The index of the selected option
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setSelectedOptionIndex(int selectedOptionIndex) {
        synchronized (lock) {
            this.selectedOptionIndex = selectedOptionIndex;
            return this;
        }
    }

    /**
     * Sets the type of the dialog
     * <p>It can be one of:
     * <ul>
     * <li>{@linkplain Type#MESSAGE} a plain message</li>
     * <li>{@linkplain Type#PROMPT} a prompt where the user inputs a value</li>
     * <li>{@linkplain Type#DROPDOWN} a dropdown list with all the {@link #setDropdowns(Object[]) elements}</li>
     * <li>{@linkplain Type#BUTTONS} multiple options (one for each {@link #setButtons(Object[]) elements})</li>
     * </ul>
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param type The type of the dialog
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setType(@NotNull Type type) {
        synchronized (lock) {
            this.type = type;
            return this;
        }
    }

    /**
     * Sets the text in the dialog
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param text the text in the dialog
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setText(@Nullable String text) {
        synchronized (lock) {
            this.text = text;
            return this;
        }
    }

    /**
     * Sets the title of the dialog
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param title the title of the dialog
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setTitle(@Nullable String title) {
        synchronized (lock) {
            this.title = title;
            return this;
        }
    }

    /**
     * Sets what icon should be displayed in the dialog
     * <p>Use {@code setIcon(null)} to clear the icon
     * <p>If no icon is provided, a default icon will be provided
     * <p>The default icon is dependent on the importance of the importance, see {@linkplain Importance}
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param icon The icon to use
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setIcon(@Nullable Icon icon) {
        synchronized (lock) {
            this.icon = icon;
            return this;
        }
    }

    /**
     * Sets what component should be the parent to the dialog
     * <p>Use {@code setParent(null)} to clear the parent
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param parent The component
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setParent(@Nullable Component parent) {
        synchronized (lock) {
            this.parent = parent;
            return this;
        }
    }

    /**
     * Sets the buttons type, or the 'buttons' if you will
     * <p>The types allowed are:
     * <ul>
     * <li>{@linkplain Buttons#DEFAULT}</li>
     * <li>{@linkplain Buttons#YES_NO}</li>
     * <li>{@linkplain Buttons#YES_NO_CANCEL}</li>
     * <li>{@linkplain Buttons#OK_CANCEL}</li>
     * </ul>
     * <p>You can only use setter methods when the dialog isn't shown
     * <p>This is not applicable to dialogs with {@link Type#BUTTONS button} type,
     * if you have a dialog of type {@link Type#BUTTONS button}, see {@linkplain #setButtons(Object[])}
     *
     * @param buttons What buttons the type should have
     * @return this dialog
     * @see #setButtons(Object[])
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setButtons(@NotNull Buttons buttons) {
        synchronized (lock) {
            this.buttons = buttons;
            return this;
        }
    }

    /**
     * Sets the importance type, or the 'serverity' if you will. This will change the default icon
     * <p>The types allowed are:
     * <ul>
     * <li>{@linkplain Importance#PLAIN} (no icon)</li>
     * <li>{@linkplain Importance#INFO}</li>
     * <li>{@linkplain Importance#WARNING}</li>
     * <li>{@linkplain Importance#ERROR}</li>
     * <li>{@linkplain Importance#QUESTION}</li>
     * </ul>
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param importance What importance type the type should be of
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setImportance(@NotNull Importance importance) {
        synchronized (lock) {
            this.importance = importance;
            return this;
        }
    }

    /**
     * Sets the prefilled value of the box in a prompt type box. Set the box mode to prompt using {@linkplain #setType(Type)}.
     * <p>The value is provided as a string, and will display regardless of the input type of the box.
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param defaultValue The prefilled value of the prompt box
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setDefaultValue(@Nullable String defaultValue) {
        synchronized (lock) {
            this.defaultValue = defaultValue;
            return this;
        }
    }

    /**
     * Sets the input type of a prompt type box. Set the box mode to prompt using {@linkplain #setType(Type)}.
     * <p>You can set it to {@code String}, or any primitive data type except {@code boolean} and {@code char}.
     * <p>When applying filters and when getting the result, you should cast the object to the data type you specify here
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param input The input type to allow
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setInput(@NotNull Input input) {
        synchronized (lock) {
            this.input = input;
            return this;
        }
    }

    /**
     * Sets if the {@linkplain #getResult()} should return the {@code Object} selected,
     * or the index in the array in which the object appears.
     * <p>This method only work on {@code BUTTONS} and {@code DROPDOWN} type dialogs
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param returnType {@code OBJECT} if you want the object to be returned, or {@code INDEX} if you want the index of the object to be returned
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setReturnType(@NotNull ReturnType returnType) {
        synchronized (lock) {
            this.returnType = returnType;
            return this;
        }
    }

    /**
     * Sets if you should be able to close this dialog using the X button, the esc button on the keyboard, or the cancel button in the type.
     * <p>If set to true, {@linkplain #getResult()} will yeild {@code null} in that case, even if it is an prompt type box with a primitive data type as input type.
     * <p>This method partially overrides the filter, since closing the type will finish with result {@code null} regargless of if the filter allows it or not.
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param acceptNull if this dialog should accept {@code null} (closing the window or simular)
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setAcceptNull(boolean acceptNull) {
        synchronized (lock) {
            this.acceptNull = acceptNull;
            return this;
        }
    }

    /**
     * Sets if this dialog should be created in a differnet thread or not.
     * <p>If {@code false}, it will block the current thread from the moment you run {@linkplain #show()} to when the type is finished.
     * <p>If {@code true} (default), it will be created in a seperate thread. You can use {@linkplain #await()} to pause the current thread until the type is dismissed.
     * <p>You can use {@linkplain Object#wait()}, if you prefer that. Remember to check wether the dialog is finished or not, before waiting.
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param showAsync If the type should be created in a seperate thread
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setShowAsync(boolean showAsync) {
        synchronized (lock) {
            this.showAsync = showAsync;
            return this;
        }
    }

    /**
     * Sets the filter for input
     * <p>If the type type is DROPDOWN or BUTTONS, the selected {@code Object} will be passed to the filter. You may cast this to the object type in question
     * <p>If the type type is PROMPT, you should cast the object to the input type ({@code String}, {@code int}, {@code double} etc.)
     * You should NOT cast the object to anything else than the {@code Input}.
     * <p>If the type is set to allow {@code null} values, {@code null}'s will be allowed regardless of the filter accepting it or not
     * <p>If the filtering causes an exception, it will count as failed but won't throw the exception. That means, if you want to check if the inputed int is positive, you can do {@code o -> (int) o > 0} without checking for {@code ClassCastException}, {@code NullPointerException} or simular
     * <p>You can only use setter methods when the dialog isn't shown
     *
     * @param filter The filter, or {@code null} if you don't want a filter
     * @return this dialog
     */
    @NotNull
    @Contract(value = "_ -> this", pure = true)
    public Dialog setFilter(@Nullable Predicate<Object> filter) {
        synchronized (lock) {
            this.filter = filter;
            return this;
        }
    }
}

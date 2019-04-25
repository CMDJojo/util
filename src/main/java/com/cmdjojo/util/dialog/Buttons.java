package com.cmdjojo.util.dialog;

import javax.swing.*;

public enum Buttons {
    DEFAULT(JOptionPane.DEFAULT_OPTION),
    YES_NO(JOptionPane.YES_NO_OPTION),
    YES_NO_CANCEL(JOptionPane.YES_NO_CANCEL_OPTION),
    OK_CANCEL(JOptionPane.OK_CANCEL_OPTION);

    private final int constant;

    Buttons(int constant) {
        this.constant = constant;
    }

    public int getConstant() {
        return constant;
    }
}

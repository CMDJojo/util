package com.cmdjojo.util.dialog;

import javax.swing.*;

public enum Importance {
    PLAIN(JOptionPane.PLAIN_MESSAGE),
    INFO(JOptionPane.INFORMATION_MESSAGE),
    WARNING(JOptionPane.WARNING_MESSAGE),
    ERROR(JOptionPane.ERROR_MESSAGE),
    QUESTION(JOptionPane.QUESTION_MESSAGE);

    private final int constant;

    Importance(int constant) {

        this.constant = constant;
    }

    public int getConstant() {
        return constant;
    }
}

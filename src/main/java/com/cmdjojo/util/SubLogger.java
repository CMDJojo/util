package com.cmdjojo.util;

public class SubLogger {

    // root > rewriter#abc.pdf > page#1: Started
    // name1delimetername2delimetername3seperatortext
    final String name;
    private final SubLogger parent;
    private final RootLogger root;

    SubLogger(String name, SubLogger parent, RootLogger root) {
        this.name = name;
        this.parent = parent;
        this.root = root;
    }

    public SubLogger createChild(String name) {
        return new SubLogger(
                name,
                this,
                root
        );
    }

    synchronized void printreq(String line) {
        parent.printreq(appendFirst(line));
    }

    public synchronized void flush() {
        root.flush();
    }

    String appendFirst(String line) {
        return name + getOptions().getDelimeter() + line;
    }

    public synchronized void println(String text) {
        parent.printreq(name + getOptions().getSeperator() + text);
    }

    public RootLogger.LoggerOptions getOptions() {
        return root.getOptions();
    }

    public void setOptions(RootLogger.LoggerOptions lo) {
        root.setOptions(lo);
    }
}

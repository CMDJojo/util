package com.cmdjojo.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class RootLogger extends SubLogger {
    private final LoggerOptions options;
    private final PrintWriter pw;
    private final BufferedWriter bw;

    public RootLogger(String name) {
        this(name, System.out);
    }

    public RootLogger(String name, PrintStream printStream) {
        super(name, null, null);
        this.options = LoggerOptions.normal();
        pw = new PrintWriter(printStream);
        bw = new BufferedWriter(pw);
    }

    @Override
    public SubLogger createChild(String name) {
        return new SubLogger(
                name,
                this,
                this
        );
    }

    private synchronized void printres(String line) {
        try {
            bw.write(line);
        } catch (IOException e) {
            pw.write("Error buffering to this PrintWriter:\n");
            e.printStackTrace(pw);
        }
    }

    @Override
    synchronized void printreq(String line) {
        printres(appendFirst(line) + "\n");
    }

    @Override
    public synchronized void println(String text) {
        printres(name + getOptions().getSeperator() + text + "\n");
    }

    @Override
    public synchronized void flush() {
        try {
            bw.flush();
        } catch (IOException e) {
            pw.write("Error flushing to this PrintWriter:\n");
            e.printStackTrace(pw);
        }
    }

    @Override
    public LoggerOptions getOptions() {
        return options;
    }

    @Override
    public void setOptions(LoggerOptions lo) {
        options.set(lo);
    }

    public static class LoggerOptions {
        private String delimeter;
        private String seperator;

        LoggerOptions(String delimeter, String seperator) {
            this.delimeter = delimeter;
            this.seperator = seperator;
        }

        static LoggerOptions normal() {
            return new LoggerOptions(
                    " > ",
                    ": "
            );
        }

        private void set(LoggerOptions so) {
            setDelimeter(so.getDelimeter());
            setSeperator(so.getSeperator());
        }

        public String getDelimeter() {
            return delimeter;
        }

        public void setDelimeter(String delimeter) {
            this.delimeter = delimeter;
        }

        public String getSeperator() {
            return seperator;
        }

        public void setSeperator(String seperator) {
            this.seperator = seperator;
        }
    }
}

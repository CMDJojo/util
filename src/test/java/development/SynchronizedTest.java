package development;

import com.cmdjojo.util.Stopwatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class SynchronizedTest {
    public static void main(String[] args) throws Exception {
        Stopwatch sw = new Stopwatch();
        sw.set("Sysout");
        for (int i = 0; i < 10000; i++) {
            System.out.println("abc" + i);
        }
        sw.set("BufferedWriter");
        BufferedWriter bw = new BufferedWriter(new PrintWriter(System.out));
        for (int i = 0; i < 10000; i++) {
            bw.write("abc" + i + "\n");
        }
        bw.flush();
        sw.set("BufferedSysout");
        BufferedSysout bs = new BufferedSysout();
        for (int i = 0; i < 10000; i++) {
            bs.write("abc" + i + "\n");
        }
        bs.flush();
        sw.set("WrappedBuffer");
        WrappedBuffer wb = new WrappedBuffer("[CODE] ");
        for (int i = 0; i < 10000; i++) {
            wb.println("abc" + i);
        }
        wb.flush();
        sw.set("end");
        System.out.println(sw.compareNextAll());
    }
}

class WrappedBuffer {
    private final BufferedWriter bw;
    private final PrintWriter pw;
    private Thread t;
    private String pref;
    private int interval;
    private int prints;

    WrappedBuffer(String pref) {
        this(pref, 1000);
    }

    WrappedBuffer(String pref, int interval) {
        this(pref, interval, System.out);
    }

    WrappedBuffer(String pref, int interval, PrintStream out) {
        this.pref = pref;
        this.interval = interval;
        pw = new PrintWriter(out);
        bw = new BufferedWriter(pw);
        prints = 0;
    }

    void start() {
        t = new Thread(this::tick);
        t.start();
    }

    void stop() {
        t.interrupt();
    }

    private void tick() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(interval);
                if (prints != 0) {
                    synchronized (this) {
                        bw.flush();
                        prints = 0;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                e.printStackTrace(pw);
            }
        }
    }

    synchronized void print(String str) {
        try {
            bw.append(pref).append(str);
            prints++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void println(String str) {
        try {
            print(str);
            bw.append('\n');
            prints++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized void flush() {
        try {
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class BufferedSysout {

    private final int bufferSize;
    private final Object wip;
    private char[] buffer;
    private int lastWrite;

    BufferedSysout() {
        this(512);
    }

    BufferedSysout(int bufferSize) {
        this.bufferSize = bufferSize;
        wip = new Object();
    }

    synchronized void write(String str) {
        write(str.toCharArray());
    }

    synchronized void write(char[] charArr) {
        for (char c : charArr) {
            write(c);
        }
    }

    synchronized void write(char c) {
        if (buffer == null) {
            buffer = new char[bufferSize];
            lastWrite = -1;
        }
        lastWrite++;
        if (lastWrite >= bufferSize) {
            flush();
            write(c);
        } else {
            buffer[lastWrite] = c;
        }
    }

    synchronized void flush() {
        final char[] tb = buffer.clone();
        buffer = null;
        Runnable r = () -> {
            synchronized (wip) {
                StringBuilder sb = new StringBuilder();
                for (char c : tb) if (c != '\u0000') sb.append(c);
                System.out.print(sb.toString());
            }
        };
        Thread t = new Thread(r);
        t.run();
    }
}

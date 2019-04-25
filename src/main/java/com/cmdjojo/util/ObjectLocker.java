package com.cmdjojo.util;


/**
 * ObjectLocker enables you to gain or release the lock on an object with method calls
 */
public class ObjectLocker {
    private final Object target;
    private boolean locked;
    private Thread thread;

    /**
     * The constructor
     *
     * @param target the object to lock on to
     */
    public ObjectLocker(Object target) {
        if (target == null)
            throw new IllegalArgumentException("null");
        this.target = target;
    }

    /**
     * Locks on to the object
     *
     * @return {@code true} if a new lock is scheduled, {@code false} if this
     * object already is attempting a lock on the target
     */
    public synchronized boolean lock() {
        if (thread != null) return false;
        thread = new Thread(this::sync);
        thread.start();
        return true;
    }

    /**
     * Releases the lock on the object
     *
     * @return {@code true} if the lock is released, {@code false} if this object
     * didn't try to lock on an object at the moment and therefor isn't released
     */
    public boolean unlock() {
        if (thread != null) {
            notify();
            return true;
        } else return false;
    }

    /**
     * Locks and {@link #wait() waits}
     */
    private void sync() {
        locked = false;
        synchronized (target) {
            locked = true;
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        locked = false;
        thread = null;
    }

    /**
     * Checks whether or not this object has the ownership of the targets monitor
     *
     * @return {@code true} if this object has ownership of targets monitor
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Checks whether or not this objects {@linkplain #lock()} method was called without
     * {@linkplain #unlock()} being called after, meaning that this object either has
     * the targets lock, or is waiting to get it
     *
     * @return {@code true} if this object has the targets lock OR is attempting to get it
     */
    public boolean isActive() {
        return thread != null;
    }
}

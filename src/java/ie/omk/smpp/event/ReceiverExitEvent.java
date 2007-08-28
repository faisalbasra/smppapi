package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.ConnectionState;

/**
 * Event generated by the receiver thread exiting. This event will be generated
 * when the receiver thread terminates either normally or abnormally due to an
 * exception. In the former case, isException will return false. In the latter,
 * isException will return true and the Throwable object that was the cause of
 * the thread's termination can be accessed using {@link #getException}. If an
 * application receives this event, it can be assumed that the connection to the
 * SMSC is invalid. That is, the network-specific connection will have to be
 * reestablished before binding to the SMSC is again possible. It is up to the
 * application to do any necessary clean up to the old network connection.
 * 
 * @version $Id$
 */
public class ReceiverExitEvent extends SMPPEvent {
    /**
     * Recevier exit reason of "unknown".
     */
    public static final int UNKNOWN = 0;

    /**
     * Receiver exited because bind timed out.
     */
    public static final int BIND_TIMEOUT = 1;

    /**
     * Receiver exited due to an exception.
     */
    public static final int EXCEPTION = 2;

    /** The exception that caused thread termination. */
    private Throwable exception;

    /** The state the Connection was in when the thread exited. */
    private ConnectionState connectionState;

    /**
     * The reason for the exit.
     */
    private int reason = UNKNOWN;

    /**
     * Create a new ReceiverExitEvent. Events created with this constructor will
     * signify a normal receiver thread termination with no errors.
     * 
     * @param source
     *            the source Connection of this event.
     */
    public ReceiverExitEvent(Connection source) {
        super(RECEIVER_EXIT, source);
    }

    /**
     * Create a new ReceiverExitEvent. If <code>t</code> is not null, the
     * newly created event will represent an abnormal termination of the
     * receiver thread. If <code>t</code> is null, this constructor has the
     * same effect as {@link #ReceiverExitEvent(Connection)}.
     * 
     * @param source
     *            the source Connection of this event.
     * @param t
     *            the exception which caused termination (may be null).
     */
    public ReceiverExitEvent(Connection source, Throwable t) {
        super(RECEIVER_EXIT, source);
        setException(t);
    }

    /**
     * Create a new ReceiverExitEvent. If <code>t</code> is not null, the
     * newly created event will represent an abnormal termination of the
     * receiver thread. If <code>t</code> is null, this constructor has the
     * same effect as {@link #ReceiverExitEvent(Connection)}.
     * 
     * @param source
     *            the source Connection of this event.
     * @param t
     *            the exception which caused termination (may be null).
     * @param state
     *            the state the Connection was in when termination occurred.
     * @see ie.omk.smpp.ConnectionState
     */
    public ReceiverExitEvent(Connection source, Throwable t, ConnectionState state) {
        super(RECEIVER_EXIT, source);
        setException(t);
        this.connectionState = state;
    }

    /**
     * Test if this event represents an abnormal termination.
     * 
     * @return true if this event represents abnormal termination due to an
     *         exception, false if it represents normal termination.
     * @deprecated use {#link #getReason}
     */
    public boolean isException() {
        return exception != null;
    }

    /**
     * Get the exception that caused termination.
     * 
     * @return the exception, or null if this event represents normal
     *         termination.
     */
    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable t) {
        this.exception = t;
        if (t != null) {
            this.reason = EXCEPTION;
        }
    }

    /**
     * Get the state the Connection was in when termination occurred.
     * @return the state of the connection.
     */
    public ConnectionState getState() {
        return connectionState;
    }

    /**
     * Get the reason for the exit event.
     * 
     * @return Returns the reason.
     */
    public int getReason() {
        return reason;
    }

    /**
     * Set the reason for the exit event. Should be one of the enumeration
     * values defined in this class.
     * 
     * @param reason
     *            The reason to set.
     */
    public void setReason(int reason) {
        this.reason = reason;
    }
}


/*
 * Java SMPP API
 * Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 * $Id$
 */

package ie.omk.smpp.event;

import ie.omk.smpp.Connection;
import ie.omk.smpp.message.SMPPPacket;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple implementation of the event dispatcher interface. This
 * implementation simply iterates over the set of registered observers and
 * notifies each of the event in turn. This means that whatever thread the
 * <code>Connection</code> object uses to call into this object will be
 * blocked, from the <code>Connection</code>'s point of view, until every
 * observer has successfully processed the event.
 * 
 * @author Oran Kelly
 * @see ie.omk.smpp.event.EventDispatcher
 */
public class SimpleEventDispatcher implements EventDispatcher {

    protected Log logger = LogFactory.getLog(SimpleEventDispatcher.class);

    /**
     * List of observers registered on this event dispatcher.
     */
    protected ArrayList observers = new ArrayList();

    /**
     * Create a new SimpleEventDispatcher.
     */
    public SimpleEventDispatcher() {
    }

    public void init() {
        // nothing to do.
    }

    public void destroy() {
        // nothing to do.
    }

    /**
     * Create a new AbstractEventDispatcher and register one observer on it.
     */
    public SimpleEventDispatcher(ConnectionObserver ob) {
        addObserver(ob);
    }

    /**
     * Add a connection observer to receive SMPP events. An observer cannot be
     * added twice. Attempting to do so has no effect.
     * 
     * @param ob
     *            the ConnectionObserver object to add.
     */
    public synchronized void addObserver(ConnectionObserver ob) {
        if (!observers.contains(ob))
            observers.add(ob);
        else
            logger.info("Not adding observer because it's already registered");
    }

    /**
     * Remove a connection observer. If the observer was not previously added,
     * the method has no effect.
     * 
     * @param ob
     *            The ConnectionObserver object to remove.
     */
    public synchronized void removeObserver(ConnectionObserver ob) {
        if (observers.contains(ob))
            observers.remove(observers.indexOf(ob));
        else
            logger.info("Cannot remove an observer that was not added");
    }

    /**
     * Get an iterator to iterate over the set of observers registered with this
     * event dispatcher.
     * 
     * @return an iterator which iterates the observers registered with this
     *         event dispatcher.
     */
    public synchronized Iterator observerIterator() {
        // get a shallow clone of the array list and return an iterator to that.
        return (((ArrayList) observers.clone()).iterator());
    }

    /**
     * Determine if this dispatcher has a particular observer registered for
     * events.
     * 
     * @param ob
     *            The ConnectionObserver to check the presence of.
     * @return true if the observer is registered with this dispatcher, false
     *         otherwise.
     */
    public synchronized boolean contains(ConnectionObserver ob) {
        return (observers.contains(ob));
    }

    /**
     * Notify registered observers of an SMPP event.
     * 
     * @param event
     *            the SMPP event to notify observers of.
     */
    public void notifyObservers(Connection conn, SMPPEvent event) {
        Iterator i = observerIterator();
        while (i.hasNext()) {
            try {
                ((ConnectionObserver) i.next()).update(conn, event);
            } catch (Exception x) {
                logger.warn("An observer exceptioned during update", x);
            }
        }
    }

    /**
     * Notify registered observers of an incoming SMPP packet.
     * 
     * @param packet
     *            the received packet to notify observers of.
     */
    public void notifyObservers(Connection conn, SMPPPacket packet) {
        Iterator i = observerIterator();
        while (i.hasNext()) {
            try {
                ((ConnectionObserver) i.next()).packetReceived(conn, packet);
            } catch (Exception x) {
                logger.warn("An observer exceptioned during update", x);
            }
        }
    }
}
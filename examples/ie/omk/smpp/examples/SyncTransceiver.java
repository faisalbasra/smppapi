/*
 * Java implementation of the SMPP v3.3 API
 * Copyright (C) 1998 - 2000 by Oran Kelly
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
 * $Id$
 */
package ie.omk.smpp.examples;

import ie.omk.smpp.Address;
import ie.omk.smpp.Connection;
import ie.omk.smpp.message.BindResp;
import ie.omk.smpp.message.SMPPPacket;
import ie.omk.smpp.message.SubmitSM;
import ie.omk.smpp.message.SubmitSMResp;
import ie.omk.smpp.message.UnbindResp;
import ie.omk.smpp.version.SMPPVersion;

import java.util.HashMap;

import org.apache.log4j.Logger;

/** A synchronous transceiver example. Using sync mode for either a transceiver
 * or receiver connection is less useful than using async mode as your
 * application must now poll the connection continuously for incoming delivery
 * messages from the SMSC.
 * 
 * @see ie.omk.smpp.examples.ParseArgs ParseArgs for details on running
 * this class.
 */
public class SyncTransceiver {

    private HashMap myArgs = new HashMap();

    private Connection myConnection = null;

    private Logger logger = Logger.getLogger("ie.omk.smpp.examples");

    public SyncTransceiver() {
    }

    private void init(String[] args) {
	try {
	    myArgs = ParseArgs.parse(args);

	    int port = Integer.parseInt((String)myArgs.get(ParseArgs.PORT));

	    myConnection = new Connection((String)myArgs.get(ParseArgs.HOSTNAME), port);
	    myConnection.setVersion(SMPPVersion.V34);
	    myConnection.autoAckLink(true);
	    myConnection.autoAckMessages(true);

	} catch (Exception x) {
	    logger.info("Bad command line arguments.");
	}
    }

    private void run() {
	try {
	    logger.info("Binding to the SMSC");

	    // Bind the short way:
	    BindResp resp = myConnection.bind(Connection.TRANSCEIVER,
		    (String)myArgs.get(ParseArgs.SYSTEM_ID),
		    (String)myArgs.get(ParseArgs.PASSWORD),
		    (String)myArgs.get(ParseArgs.SYSTEM_TYPE),
		    Integer.parseInt((String)myArgs.get(ParseArgs.ADDRESS_TON)),
		    Integer.parseInt((String)myArgs.get(ParseArgs.ADDRESS_NPI)),
		    (String)myArgs.get(ParseArgs.ADDRESS_RANGE));


	    if (resp.getCommandStatus() != 0) {
		logger.info("SMSC bind failed.");
		System.exit(1);
	    }

	    logger.info("Bind successful...submitting a message.");

	    // Submit a simple message
	    SubmitSM sm = (SubmitSM)myConnection.newInstance(SMPPPacket.SUBMIT_SM);
	    sm.setDestination(new Address(0, 0, "3188332314"));
	    sm.setMessageText("This is an example short message.");
	    SubmitSMResp smr = (SubmitSMResp)myConnection.sendRequest(sm);

	    logger.info("Submitted message ID: " + smr.getMessageId());

	    try {
		// Wait a while, see if the SMSC delivers anything to us...
		SMPPPacket p = myConnection.readNextPacket();
		logger.info("Received a packet!");
		logger.info(p.toString());

		// API should be automatically acking deliver_sm and
		// enquire_link packets...
	    } catch (java.net.SocketTimeoutException x) {
		// ah well...
	    }

	    // Unbind.
	    UnbindResp ubr = myConnection.unbind();
	    
	    if (ubr.getCommandStatus() == 0) {
		logger.info("Successfully unbound from the SMSC");
	    } else {
		logger.info("There was an error unbinding.");
	    }
	} catch (Exception x) {
	    logger.info("An exception occurred.");
	    x.printStackTrace(System.err);
	}
    }

    public static final void main(String[] args) {
	SyncTransceiver t = new SyncTransceiver();
	t.init(args);
	t.run();
    }
}

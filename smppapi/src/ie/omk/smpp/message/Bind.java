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
package ie.omk.smpp.message;

import java.io.IOException;
import java.io.OutputStream;

import ie.omk.smpp.SMPPException;
import ie.omk.debug.Debug;
import ie.omk.smpp.BadCommandIDException;
import ie.omk.smpp.BadInterfaceVersionException;
import ie.omk.smpp.InvalidNPIException;
import ie.omk.smpp.InvalidTONException;
import ie.omk.smpp.StringTooLongException;
import ie.omk.smpp.util.SMPPIO;

/** Abstract parent of BindTransmitter and BindReceiver.
  * @author Oran Kelly
  * @version 1.0
  */
public abstract class Bind
    extends ie.omk.smpp.message.SMPPRequest
{
    /** System Id */
    private String sysId = null;

    /** Authentication password */
    private String password = null;

    /** System type */
    private String sysType = null;

    /** Address range for message routing */
    private String addressRange = null;

    /** Interface version */
    private int interfaceVer = 0x33; // XXX use SMPPVersion class.

    /** Address Type Of Number for message routing */
    private int addrTon = 0;

    /** Address Numbering Plan Indicator for message routing */
    private int addrNpi = 0;


    public Bind(int id)
    {
	super(id);
    }

    /** Read in a BindTransmitter from an InputStream.  A full packet,
      * including the header fields must exist in the stream.
      * @param in The InputStream to read from
      * @throws java.io.IOException if there's a problem reading from the
      * input stream.
      */
    /*public Bind(InputStream in)
	throws java.io.IOException, ie.omk.smpp.SMPPException
    {
	super(in);

	if (getCommandStatus() != 0)
	    return;

	sysId = SMPPIO.readCString(in);
	password = SMPPIO.readCString(in);
	sysType = SMPPIO.readCString(in);
	interfaceVer =  SMPPIO.readInt(in, 1);
	addrTon =  SMPPIO.readInt(in, 1);
	addrNpi =  SMPPIO.readInt(in, 1);
	addressRange = SMPPIO.readCString(in);
    }*/

    /** Set the system Id
      * @param sysId The System Id to use (Up to 15 characters)
      * @throws ie.omk.smpp.StringTooLongException if the system id is too
      * long.
      */
    public void setSystemId(String sysId)
	throws StringTooLongException
    {
	if(sysId == null) {
	    this.sysId = null;
	    return;
	}

	if(sysId.length() < 16)
	    this.sysId = sysId;
	else
	    throw new StringTooLongException(15);
    }

    /** Set the password for this transmitter
      * @param password The new password to use (Up to 8 characters in length)
      * @throws ie.omk.smpp.StringTooLongException if the password is too
      * long.
      */
    public void setPassword(String password)
	throws StringTooLongException
    {
	if(password == null) {
	    this.password = null;
	    return;
	}

	if(password.length() < 9)
	    this.password = password;
	else
	    throw new StringTooLongException(8);
    }

    /** Set the system type for this transmitter
      * @param sysType The new system type (Up to 12 characters in length)
      * @throws ie.omk.smpp.StringTooLongException if the system type is too
      * long.
      */
    public void setSystemType(String sysType)
	throws StringTooLongException
    {
	if(sysType == null) {
	    this.sysType = null;
	    return;
	}

	if(sysType.length() < 13)
	    this.sysType = sysType;
	else
	    throw new StringTooLongException(12);
    }

    /** Set the interface version being used by this transmitter
      * @param interfaceVer The interface version to report to the SMSC.
      * @throws ie.omk.smpp.BadInterfaceVersionException if the interface
      * version is invalid.
      */ 
    public void setInterfaceVersion(int interfaceVer)
	throws BadInterfaceVersionException
    {
	if (interfaceVer != 0x33 && interfaceVer != 0x34)
	    throw new BadInterfaceVersionException(interfaceVer);

	this.interfaceVer = interfaceVer;
    }

    /** Set the message routing Ton for this transmitter
      * @param addrTon The new Type Of Number to use
      * @throws ie.omk.smpp.InvalidTONException if the TON is invalid.
      */
    public void setAddressTon(int addrTon)
	throws InvalidTONException
    {
	// XXX Check TON?
	this.addrTon = addrTon;
    }

    /** Set the message routing Npi for this transmitter
      * @param addrNpi The new Numbering plan indicator to use
      * @throws ie.omk.smpp.InvalidNPIException if the NPI is invalid
      */
    public void setAddressNpi(int addrNpi)
	throws InvalidNPIException
    {
	// XXX check the NPI?
	this.addrNpi = addrNpi;
    }

    /** Set the message routing address range for this transmitter
      * @param addressRange The new address range to use (Up to 40 characters)
      * @throws ie.omk.smpp.StringTooLongException if the address range is
      * too long.
      */
    public void setAddressRange(String addressRange)
	throws StringTooLongException
    {
	if(addressRange == null) {
	    this.addressRange = null;
	    return;
	}

	if(addressRange.length() < 41)
	    this.addressRange = addressRange;
	else
	    throw new StringTooLongException(40);
    }

    /** Get the system Id */
    public String getSystemId()
    {
	return (sysId);
    }

    /** Get the authentication password */
    public String getPassword()
    {
	return (password);
    }

    /** Get the current system type */
    public String getSystemType()
    {
	return (sysType);
    }

    /** Get the routing address regular expression */
    public String getAddressRange()
    {
	return (addressRange);
    }

    /** Get the Type of number */
    public int getAddressTon()
    {
	return (addrTon);
    }

    /** Get the Numbering plan indicator */
    public int getAddressNpi()
    {
	return (addrNpi);
    }

    /** Get the interface version */
    public int getInterfaceVersion()
    {
	return (interfaceVer);
    }


    /** Return the number of bytes this packet would be encoded as to an
      * OutputStream.
      * @return the number of bytes this packet would encode as.
      */
    public int getBodyLength()
    {
	// Calculated as the size of the header plus 3 1-byte ints and
	// 4 null-terminators for the strings plus the length of the strings
	int len = (((sysId != null) ? sysId.length() : 0)
		+ ((password != null) ? password.length() : 0)
		+ ((sysType != null) ? sysType.length() : 0)
		+ ((addressRange != null) ? addressRange.length() : 0));

	// 3 1-byte integers, 4 c-strings
	return (len + 3 + 4);
    }

    /** Write the byte representation of this packet to an OutputStream.
      * @param out The output stream to write to
      * @throws java.io.IOException If there is an error writing to the
      * stream.
      */
    protected void encodeBody(OutputStream out)
	throws java.io.IOException
    {
	SMPPIO.writeCString(sysId, out);
	SMPPIO.writeCString(password, out);
	SMPPIO.writeCString(sysType, out);
	SMPPIO.writeInt(interfaceVer, 1, out);
	SMPPIO.writeInt(addrTon, 1, out);
	SMPPIO.writeInt(addrNpi, 1, out);
	SMPPIO.writeCString(addressRange, out);
    }

    public void readBodyFrom(byte[] body, int offset)
    {
	sysId = SMPPIO.readCString(body, offset);
	offset += sysId.length() + 1;

	password = SMPPIO.readCString(body, offset);
	offset += password.length() + 1;

	sysType = SMPPIO.readCString(body, offset);
	offset += sysType.length() + 1;

	interfaceVer = SMPPIO.bytesToInt(body, offset++, 1);
	addrTon = SMPPIO.bytesToInt(body, offset++, 1);
	addrNpi = SMPPIO.bytesToInt(body, offset++, 1);
	addressRange = SMPPIO.readCString(body, offset);
    }

    /** Convert this packet to a String. Not to be interpreted programmatically,
      * it's just dead handy for debugging!
      */
    public String toString()
    {
	return new String("bind");
    }
}
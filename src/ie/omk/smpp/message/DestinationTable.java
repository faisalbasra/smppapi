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
 */
package ie.omk.smpp.message;

import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

import ie.omk.smpp.Address;
import ie.omk.smpp.StringTooLongException;

import ie.omk.smpp.util.SMPPIO;


public class DestinationTable
{
    private ArrayList dests = null;

    private int length = 0;

    public DestinationTable()
    {
	dests = new ArrayList();
    }

    public synchronized void add(Address addr)
    {
	dests.add(addr);
	length += addr.getLength() + 1; // don't forget the dest type flag!
    }

    public synchronized void add(String distList)
	throws ie.omk.smpp.StringTooLongException
    {
	if (distList.length() > 20) {
	    throw new StringTooLongException("DLName must be less 20 "
		    + "characters or less");
	}

	dests.add(distList);
	length += distList.length() + 2; // nul byte plus dest type flag
    }

    public synchronized void remove(Address addr)
    {
	int i = dests.indexOf(addr);
	if (i > -1)
	    length -= ((Address)dests.remove(i)).getLength() + 1;
    }

    public synchronized void remove(String distList)
    {
	int i = dests.indexOf(distList);
	if (i > -1)
	    length -= ((String)dests.remove(i)).length() + 2;
    }

    public Iterator iterator()
    {
	return (Collections.unmodifiableList(dests).iterator());
    }

    public ListIterator listIterator()
    {
	return (Collections.unmodifiableList(dests).listIterator());
    }

    public synchronized Object clone()
    {
	DestinationTable dt = new DestinationTable();
	dt.dests = (ArrayList)this.dests.clone();
	return (dt);
    }

    public synchronized int getLength()
    {
	return (length);
    }

    public int size()
    {
	return (dests.size());
    }

    public synchronized void writeTo(OutputStream out)
	throws java.io.IOException
    {
	Iterator i = dests.iterator();
	while (i.hasNext()) {
	    Object o = i.next();
	    if (o instanceof Address) {
		SMPPIO.writeInt(1, 1, out);
		((Address)o).writeTo(out);
	    } else {
		SMPPIO.writeInt(2, 1, out);
		SMPPIO.writeCString((String)o, out);
	    }
	}
    }

    public synchronized void readFrom(byte[] table, int offset, int count)
    {
	for (int i = 0; i < count; i++) {
	    int type = SMPPIO.bytesToInt(table, offset++, 1);
	    if (type == 1) {
		// SME address..
		Address a = new Address();
		a.readFrom(table, offset);
		offset += a.getLength();
		dests.add(a);
	    } else if (type == 2) {
		// Distribution list name
		String d = SMPPIO.readCString(table, offset);
		offset += d.length() + 1;
		dests.add(d);
	    } else {
		// XXX proper exception?
		throw new RuntimeException("Unidentified destination type.");
	    }
	}

	calculateLength();
    }

    private void calculateLength()
    {
	length = 0;

	Iterator i = dests.iterator();
	while (i.hasNext()) {
	    Object o = i.next();
	    length++;	    // For the destination type flag
	    if (o instanceof Address)
		length += ((Address)o).getLength();
	    else
		length += ((String)o).length() + 1;
	}
    }
}
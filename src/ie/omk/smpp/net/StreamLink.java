/*
 * Java SMPP API
 * Copyright (C) 1998 - 2001 by Oran Kelly
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
 * Java SMPP API author: oran.kelly@ireland.com
 * Java SMPP API Homepage: http://smppapi.sourceforge.net/
 */
package ie.omk.smpp.net;

import java.io.InputStream;
import java.io.OutputStream;

/** Implementation of an Smsc link using user supplied input and output streams.
  * @author Oran Kelly
  * @version 1.0
  */
public class StreamLink
    extends ie.omk.smpp.net.SmscLink
{
    /** The input side of the link. */
    private InputStream inStream = null;

    /** The output side of the link. */
    private OutputStream outStream = null;


    public StreamLink(InputStream inStream, OutputStream outStream)
    {
	if (inStream == null || outStream == null)
	    throw new NullPointerException("Neither stream can be null!");

	this.inStream = inStream;
	this.outStream = outStream;
    }

    /** Does nothing (the streams should already be open).
      */
    public synchronized void open()
    {
    }

    /** Does nothing. This object is not responsible for opening or closing the
      * streams.
      */
    public synchronized void close()
    {
    }

    /** Get the output stream of the output socket of the virtual connection.
      */
    public OutputStream getOutputStream()
    {
	return (this.outStream);
    }

    /** Get the input stream of the input socket of the virtual connection.
      */
    public InputStream getInputStream()
    {
	return (this.inStream);
    }

    /** Check connection status.
      * @return true always.
      */
    public boolean isConnected()
    {
	return (true);
    }
}

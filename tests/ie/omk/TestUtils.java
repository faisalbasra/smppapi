/*
 * Java SMPP API Copyright (C) 1998 - 2002 by Oran Kelly
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * A copy of the LGPL can be viewed at http://www.gnu.org/copyleft/lesser.html
 * Java SMPP API author: orank@users.sf.net Java SMPP API Homepage:
 * http://smppapi.sourceforge.net/ $Id: TestUtils.java,v 1.1 2002/07/27 14:59:08
 * orank Exp $
 */
package ie.omk;

public class TestUtils {
    private TestUtils() {
    }

    public static void displayArray(byte[] ar) {
        System.out.print("{");
        for (int i = 0; i < ar.length; i++) {
            System.out.print(" 0x" + Integer.toHexString((int) ar[i] & 0xff)
                    + ",");
        }
        System.out.print(" }\n");
    }
}
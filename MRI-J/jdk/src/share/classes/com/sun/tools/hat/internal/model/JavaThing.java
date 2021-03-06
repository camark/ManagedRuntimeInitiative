/*
 * Copyright 1997-2005 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */


/*
 * The Original Code is HAT. The Initial Developer of the
 * Original Code is Bill Foote, with contributions from others
 * at JavaSoft/Sun.
 */

package com.sun.tools.hat.internal.model;

import java.util.Enumeration;
import java.util.Hashtable;


/**
 *
 * @author      Bill Foote
 */


/**
 * Represents a java "Thing".  A thing is anything that can be the value of
 * a field.  This includes JavaHeapObject, JavaObjectRef, and JavaValue.
 */

public abstract class JavaThing {

    protected JavaThing() {
    }

    /**
     * If this is a forward reference, figure out what it really
     * refers to.
     *
     * @param snapshot  The snapshot this is for
     * @param field     The field this thing represents.  If null, it is
     *                  assumed this thing is an object (and never a value).
     */
    public JavaThing dereference(Snapshot shapshot, JavaField field) {
        return this;
    }


    /**
     * Are we the same type as other?
     *
     * @see JavaObject.isSameTypeAs()
     */
    public boolean isSameTypeAs(JavaThing other) {
        return getClass() == other.getClass();
    }
    /**
     * @return true iff this represents a heap-allocated object
     */
    abstract public boolean isHeapAllocated();

    /**
     * @return the size of this object, in bytes, including VM overhead
     */
    abstract public int getSize();

    /**
     * @return a human-readable string representation of this thing
     */
    abstract public String toString();

    /**
     * Compare our string representation to other's
     * @see java.lang.String.compareTo()
     */
    public int compareTo(JavaThing other) {
        return toString().compareTo(other.toString());
    }

}

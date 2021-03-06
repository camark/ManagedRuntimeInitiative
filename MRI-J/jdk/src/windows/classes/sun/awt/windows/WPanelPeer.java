/*
 * Copyright 1996-2007 Sun Microsystems, Inc.  All Rights Reserved.
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
package sun.awt.windows;

import java.awt.*;
import java.awt.peer.*;

import java.util.Vector;

import sun.awt.SunGraphicsCallback;

class WPanelPeer extends WCanvasPeer implements PanelPeer {

    // ComponentPeer overrides

    public void paint(Graphics g) {
        super.paint(g);
        SunGraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().
            runComponents(((Container)target).getComponents(), g,
                          SunGraphicsCallback.LIGHTWEIGHTS |
                          SunGraphicsCallback.HEAVYWEIGHTS);
    }
    public void print(Graphics g) {
        super.print(g);
        SunGraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().
            runComponents(((Container)target).getComponents(), g,
                          SunGraphicsCallback.LIGHTWEIGHTS |
                          SunGraphicsCallback.HEAVYWEIGHTS);
    }

    // ContainerPeer (via PanelPeer) implementation

    public Insets getInsets() {
        return insets_;
    }

    // Toolkit & peer internals

    Insets insets_;

    static {
        initIDs();
    }

    /**
     * Initialize JNI field IDs
     */
    private static native void initIDs();

    WPanelPeer(Component target) {
        super(target);
    }

    void initialize() {
        super.initialize();
        insets_ = new Insets(0,0,0,0);

        Color c = ((Component)target).getBackground();
        if (c == null) {
            c = WColor.getDefaultColor(WColor.WINDOW_BKGND);
            ((Component)target).setBackground(c);
            setBackground(c);
        }
        c = ((Component)target).getForeground();
        if (c == null) {
            c = WColor.getDefaultColor(WColor.WINDOW_TEXT);
            ((Component)target).setForeground(c);
            setForeground(c);
        }
    }

    /**
     * DEPRECATED:  Replaced by getInsets().
     */
    public Insets insets() {
        return getInsets();
    }

    /*
     * From the DisplayChangedListener interface. Often is
     * up-called from a WWindowPeer instance.
     */
    public void displayChanged() {
        super.displayChanged();
        displayChanged((Container)target);
    }

    /*
     * Recursively iterates through all the HW and LW children
     * of the container and calls displayChanged() for HW peers.
     * Iteration through children peers only is not enough as the
     * displayChanged notification may not be propagated to HW
     * components inside LW containers, see 4452373 for details.
     */
    private static void displayChanged(Container target) {
        Component children[] = ((Container)target).getComponents();
        for (Component child : children) {
            ComponentPeer cpeer = child.getPeer();
            if (cpeer instanceof WComponentPeer) {
                ((WComponentPeer)cpeer).displayChanged();
            } else if (child instanceof Container) {
                displayChanged((Container)child);
            }
        }
    }

    private native void pRestack(Object[] peers);
    private void restack(Container cont, Vector peers) {
        for (int i = 0; i < cont.getComponentCount(); i++) {
            Component comp = cont.getComponent(i);
            if (!comp.isLightweight()) {
                if (comp.getPeer() != null) {
                    peers.add(comp.getPeer());
                }
            }
            if (comp.isLightweight() && comp instanceof Container) {
                restack((Container)comp, peers);
            }
        }
    }

    /**
     * @see java.awt.peer.ContainerPeer#restack
     */
    public void restack() {
        Vector peers = new Vector();
        peers.add(this);
        Container cont = (Container)target;
        restack(cont, peers);
        pRestack(peers.toArray());
    }

    /**
     * @see java.awt.peer.ContainerPeer#isRestackSupported
     */
    public boolean isRestackSupported() {
        return true;
    }
}

/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema;

import org.xml.sax.Locator;

/**
 * Details of a name collision.
 *
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class CollisionInfo {
    private final String name;
    private final Locator source1;
    private final Locator source2;

    public CollisionInfo(String name, Locator source1, Locator source2) {
        this.name = name;
        this.source1 = source1;
        this.source2 = source2;
    }

    /**
     * Returns a localized message that describes the collision.
     */
    public String toString() {
        return Messages.format( Messages.MSG_COLLISION_INFO,
                name, printLocator(source1), printLocator(source2) );
    }

    private String printLocator(Locator loc) {
        if( loc==null )     return "";

        int line = loc.getLineNumber();
        String sysId = loc.getSystemId();
        if(sysId==null)     sysId = Messages.format(Messages.MSG_UNKNOWN_FILE);

        if( line!=-1 )
            return Messages.format( Messages.MSG_LINE_X_OF_Y,
                    Integer.toString(line), sysId );
        else
            return sysId;
    }
}

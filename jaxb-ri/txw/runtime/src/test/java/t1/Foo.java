/*
 * Copyright (c) 2005, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package t1;

import com.sun.xml.txw2.TypedXmlWriter;
import com.sun.xml.txw2.annotation.XmlElement;
import com.sun.xml.txw2.annotation.XmlAttribute;

/**
 * @author Kohsuke Kawaguchi
 */
@XmlElement(value="foo")
public interface Foo extends TypedXmlWriter {
    Foo foo();
    @XmlAttribute
    void hello(int min);
}

/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

/*
 *  $Id: Foo.java,v 1.1 2007-12-05 00:49:36 kohsuke Exp $
 *  Author: Sekhar Vajjhala  
 */  

/**
 * Map a class with a single property that has been marked with
 * @XmlValue to simple schema type.
 */
import jakarta.xml.bind.annotation.XmlValue;
 
public class Foo {
    /**
     * @XmlValue can only be used on a property whose type (int in
     * this case) is mapped to a simple schema type.
     */
    @XmlValue 
    public int bar;
}

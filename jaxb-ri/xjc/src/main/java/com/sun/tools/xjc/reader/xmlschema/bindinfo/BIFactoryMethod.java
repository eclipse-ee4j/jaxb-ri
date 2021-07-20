/*
 * Copyright (c) 1997, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.tools.xjc.reader.xmlschema.bindinfo;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import javax.xml.namespace.QName;

import com.sun.xml.xsom.XSComponent;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.reader.Ring;
import com.sun.tools.xjc.reader.Const;
import com.sun.tools.xjc.reader.xmlschema.BGMBuilder;

/**
 * Controls the {@code ObjectFactory} method name.
 * 
 * @author Kohsuke Kawaguchi
 */
@XmlRootElement(name="factoryMethod")
public class BIFactoryMethod extends AbstractDeclarationImpl {
    @XmlAttribute
    public String name;
    
    /**
     * If the given component has {@link BIInlineBinaryData} customization,
     * reflect that to the specified property.
     */
    public static void handle(XSComponent source, CPropertyInfo prop) {
        BIInlineBinaryData inline = Ring.get(BGMBuilder.class).getBindInfo(source).get(BIInlineBinaryData.class);
        if(inline!=null) {
            prop.inlineBinaryData = true;
            inline.markAsAcknowledged();
        }
    }


    public final QName getName() { return NAME; }

    /** Name of the declaration. */
    public static final QName NAME = new QName(Const.JAXB_NSURI,"factoryMethod");
}

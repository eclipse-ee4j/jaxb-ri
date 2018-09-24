/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.sun.codemodel;

import java.util.ArrayList;
import java.util.List;


/**
 * Try statement with Catch and/or Finally clause
 */

public class JTryBlock implements JStatement {

    private JBlock body = new JBlock();
    private List<JCatchBlock> catches = new ArrayList<JCatchBlock>();
    private JBlock _finally = null;

    JTryBlock() {
    }

    public JBlock body() {
        return body;
    }

    public JCatchBlock _catch(JClass exception) {
        JCatchBlock cb = new JCatchBlock(exception);
        catches.add(cb);
        return cb;
    }

    public JBlock _finally() {
        if (_finally == null) _finally = new JBlock();
        return _finally;
    }

    public void state(JFormatter f) {
        f.p("try").g(body);
        for (JCatchBlock cb : catches)
            f.g(cb);
        if (_finally != null)
            f.p("finally").g(_finally);
        f.nl();
    }

}

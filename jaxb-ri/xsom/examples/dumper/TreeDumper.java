/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.impl.util.SchemaTreeTraverser;
import com.sun.xml.xsom.impl.util.SchemaWriter;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

/**
 * Parses all the schemas specified as the command line arguments, then shows
 * it as JTree (to see if the parsing was done correctly.)
 *
 * @author Kirill Grouchnikov (kirillcool@yahoo.com)
 */
public class TreeDumper extends JFrame {
    public TreeDumper(String mainSchemName, JTree tree) {
        super("Tree for schema '" + mainSchemName + "'");
        this.getRootPane().setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(tree);
        this.getRootPane().add(scrollPane, BorderLayout.CENTER);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println(
                    "Please provide a single (root) schema location");
            System.exit(0);
        }

        XSOMParser reader = new XSOMParser();
        // set an error handler so that you can receive error messages
        reader.setErrorHandler(new ErrorReporter(System.out));
        // DomAnnotationParserFactory is a convenient default to use
        reader.setAnnotationParser(new DomAnnotationParserFactory());

        try {
            reader.parse(new File(args[0]));

            XSSchemaSet xss = reader.getResult();
            if (xss == null) {
                System.out.println("error");
            }
            else {
                SchemaTreeTraverser stt = new SchemaTreeTraverser();
                stt.visit(xss);
                TreeModel model = stt.getModel();
                JTree tree = new JTree(model);
                tree.setCellRenderer(new SchemaTreeTraverser.SchemaTreeCellRenderer());
                TreeDumper dumper = new TreeDumper(args[0], tree);
                Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
//                dumper.setPreferredSize(screenDim);
                dumper.setSize(screenDim);
                dumper.setVisible(true);
            }
        }
        catch (SAXException e) {
            if (e.getException() != null) {
                e.getException().printStackTrace();
            }
            else {
                e.printStackTrace();
            }
            throw e;
        }
    }
}

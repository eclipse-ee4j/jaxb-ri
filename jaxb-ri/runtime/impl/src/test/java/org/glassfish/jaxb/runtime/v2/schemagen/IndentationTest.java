/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime.v2.schemagen;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Test case for issue #1645: Indentation of JAXB_FORMATTED_OUTPUT was limited to eight levels.
 * 
 * This test verifies that the indentation fix correctly handles deep nesting beyond 8 levels.
 * 
 * @see <a href="https://github.com/eclipse-ee4j/jaxb-ri/issues/1645">Issue #1645</a>
 */
public class IndentationTest extends TestCase {

    /**
     * Nested structure for testing deep indentation
     */
    @XmlRootElement(name = "level0")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level0 {
        @XmlElement
        private Level1 level1;

        public Level0() {}
        
        public Level0(Level1 level1) {
            this.level1 = level1;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level1 {
        @XmlElement
        private Level2 level2;

        public Level1() {}
        
        public Level1(Level2 level2) {
            this.level2 = level2;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level2 {
        @XmlElement
        private Level3 level3;

        public Level2() {}
        
        public Level2(Level3 level3) {
            this.level3 = level3;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level3 {
        @XmlElement
        private Level4 level4;

        public Level3() {}
        
        public Level3(Level4 level4) {
            this.level4 = level4;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level4 {
        @XmlElement
        private Level5 level5;

        public Level4() {}
        
        public Level4(Level5 level5) {
            this.level5 = level5;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level5 {
        @XmlElement
        private Level6 level6;

        public Level5() {}
        
        public Level5(Level6 level6) {
            this.level6 = level6;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level6 {
        @XmlElement
        private Level7 level7;

        public Level6() {}
        
        public Level6(Level7 level7) {
            this.level7 = level7;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level7 {
        @XmlElement
        private Level8 level8;

        public Level7() {}
        
        public Level7(Level8 level8) {
            this.level8 = level8;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level8 {
        @XmlElement
        private Level9 level9;

        public Level8() {}
        
        public Level8(Level9 level9) {
            this.level9 = level9;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level9 {
        @XmlElement
        private Level10 level10;

        public Level9() {}
        
        public Level9(Level10 level10) {
            this.level10 = level10;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Level10 {
        @XmlElement
        private String content;

        public Level10() {}
        
        public Level10(String content) {
            this.content = content;
        }
    }

    /**
     * Recursive node for flexible depth testing
     */
    @XmlRootElement(name = "node")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Node {
        @XmlElement
        private Node child;
        
        @XmlElement
        private String value;

        public Node() {}
        
        public Node(Node child, String value) {
            this.child = child;
            this.value = value;
        }
    }

    /**
     * Test that verifies indentation works correctly for deep nesting (>8 levels).
     * This is the primary test for issue #1645.
     * 
     * This test uses ByteArrayOutputStream to ensure the bug manifests when using streams,
     * as the IndentingUTF8XmlOutput is specifically designed for OutputStream.
     */
    @Test
    public void testDeepIndentation() throws Exception {
        // Create a deeply nested structure (11 levels total, including root)
        Level10 level10 = new Level10("Deep Content");
        Level9 level9 = new Level9(level10);
        Level8 level8 = new Level8(level9);
        Level7 level7 = new Level7(level8);
        Level6 level6 = new Level6(level7);
        Level5 level5 = new Level5(level6);
        Level4 level4 = new Level4(level5);
        Level3 level3 = new Level3(level4);
        Level2 level2 = new Level2(level3);
        Level1 level1 = new Level1(level2);
        Level0 level0 = new Level0(level1);

        // Marshal with formatted output to ByteArrayOutputStream (uses UTF8XmlOutput path)
        JAXBContext jc = JAXBContext.newInstance(Level0.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        marshaller.marshal(level0, byteStream);
        
        // Convert bytes to String using UTF-8
        String result = byteStream.toString(StandardCharsets.UTF_8.name());
        System.out.println("Test output (ByteArrayOutputStream):");
        System.out.println(result);

        // Verify the structure has proper indentation
        // Each level should be indented with 4 spaces more than the previous
        assertTrue("Result should contain level0", result.contains("<level0>"));
        assertTrue("Result should contain level1", result.contains("<level1>"));
        assertTrue("Result should contain level2", result.contains("<level2>"));
        assertTrue("Result should contain level3", result.contains("<level3>"));
        assertTrue("Result should contain level4", result.contains("<level4>"));
        assertTrue("Result should contain level5", result.contains("<level5>"));
        assertTrue("Result should contain level6", result.contains("<level6>"));
        assertTrue("Result should contain level7", result.contains("<level7>"));
        assertTrue("Result should contain level8", result.contains("<level8>"));
        assertTrue("Result should contain level9", result.contains("<level9>"));
        assertTrue("Result should contain level10", result.contains("<level10>"));
        assertTrue("Result should contain content", result.contains("<content>Deep Content</content>"));

        // Verify proper indentation at different levels
        // Level 1 should have 4 spaces
        assertTrue("Level 1 should be indented with 4 spaces", 
                   result.contains("\n    <level1>"));
        
        // Level 8 should have 32 spaces (8 * 4)
        assertTrue("Level 8 should be indented with 32 spaces", 
                   result.contains("\n                                <level8>"));
        
        // Level 9 should have 36 spaces (9 * 4)
        assertTrue("Level 9 should be indented with 36 spaces", 
                   result.contains("\n                                    <level9>"));
        
        // Level 10 should have 40 spaces (10 * 4)
        assertTrue("Level 10 should be indented with 40 spaces", 
                   result.contains("\n                                        <level10>"));
        
        // Content should have 44 spaces (11 * 4)
        assertTrue("Content should be indented with 44 spaces", 
                   result.contains("\n                                            <content>"));
        
        // Compare with StringWriter result to ensure consistency
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(level0, stringWriter);
        String writerResult = stringWriter.toString();
        
        System.out.println("\nTest output (StringWriter):");
        System.out.println(writerResult);
        
        // Both outputs should be identical
        assertEquals("ByteArrayOutputStream and StringWriter should produce identical output", 
                     writerResult, result);
    }

    /**
     * Test indentation at exactly 8 levels (boundary condition).
     * Uses ByteArrayOutputStream to test the UTF8 output path.
     */
    @Test
    public void testEightLevelIndentation() throws Exception {
        // Create structure with exactly 8 nested levels
        Level8 level8 = new Level8(null);
        Level7 level7 = new Level7(level8);
        Level6 level6 = new Level6(level7);
        Level5 level5 = new Level5(level6);
        Level4 level4 = new Level4(level5);
        Level3 level3 = new Level3(level4);
        Level2 level2 = new Level2(level3);
        Level1 level1 = new Level1(level2);
        Level0 level0 = new Level0(level1);

        JAXBContext jc = JAXBContext.newInstance(Level0.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        // Test with ByteArrayOutputStream
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        marshaller.marshal(level0, byteStream);
        String result = byteStream.toString(StandardCharsets.UTF_8.name());
        
        System.out.println("8-level test output (ByteArrayOutputStream):");
        System.out.println(result);

        // Level 8 should have 32 spaces (8 * 4)
        assertTrue("Level 8 should be indented with 32 spaces", 
                   result.contains("\n                                <level8"));
        
        // Compare with StringWriter result
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(level0, stringWriter);
        String writerResult = stringWriter.toString();
        
        System.out.println("\n8-level test output (StringWriter):");
        System.out.println(writerResult);
        
        assertEquals("ByteArrayOutputStream and StringWriter should produce identical output", 
                     writerResult, result);
    }

    /**
     * Test indentation with 16 levels (double the buffer size).
     * Uses ByteArrayOutputStream to test the UTF8 output path.
     */
    @Test
    public void testSixteenLevelIndentation() throws Exception {
        // Create 16 nested levels
        Node node = new Node(null, "level16");
        for (int i = 15; i >= 1; i--) {
            node = new Node(node, "level" + i);
        }

        JAXBContext jc = JAXBContext.newInstance(Node.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        // Test with ByteArrayOutputStream
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        marshaller.marshal(node, byteStream);
        String result = byteStream.toString(StandardCharsets.UTF_8.name());
        
        System.out.println("16-level test output (ByteArrayOutputStream, first 2000 chars):");
        System.out.println(result.substring(0, Math.min(2000, result.length())));

        // Verify deep indentation at level 16
        // Level 16 should have proper indentation (not collapsed)
        // We check that there are multiple levels of indentation present
        assertTrue("Result should contain node elements", result.contains("<node>"));
        assertTrue("Result should contain child elements", result.contains("<child>"));
        
        // Count the number of nested child elements to verify structure
        int childCount = 0;
        int index = 0;
        while ((index = result.indexOf("<child>", index)) != -1) {
            childCount++;
            index++;
        }
        
        assertTrue("Should have at least 15 child elements", childCount >= 15);
        
        // Compare with StringWriter result
        StringWriter stringWriter = new StringWriter();
        marshaller.marshal(node, stringWriter);
        String writerResult = stringWriter.toString();
        
        System.out.println("\n16-level test output (StringWriter, first 2000 chars):");
        System.out.println(writerResult.substring(0, Math.min(2000, writerResult.length())));
        
        assertEquals("ByteArrayOutputStream and StringWriter should produce identical output", 
                     writerResult, result);
    }
}

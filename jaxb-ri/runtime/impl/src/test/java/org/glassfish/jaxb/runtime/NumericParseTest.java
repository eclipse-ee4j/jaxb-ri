/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.jaxb.runtime;

import org.glassfish.jaxb.core.WhiteSpaceProcessor;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The fast paths of {@link DatatypeConverterImpl#_parseDouble} and
 * {@link DatatypeConverterImpl#_parseLong} must return exactly what the general path returns,
 * bit for bit, and fail exactly when it fails.
 */
public class NumericParseTest {

    /** The general path of _parseDouble, unchanged. */
    private static double referenceDouble(CharSequence text) {
        String val = WhiteSpaceProcessor.trim(text).toString();
        switch (val) {
            case "NaN": return Double.NaN;
            case "INF": return Double.POSITIVE_INFINITY;
            case "-INF": return Double.NEGATIVE_INFINITY;
        }
        if (val.isEmpty() || !digitOrPeriodOrSign(val.charAt(0)) || !digitOrPeriodOrSign(val.charAt(val.length() - 1)))
            throw new NumberFormatException(val);
        return Double.parseDouble(val);
    }

    private static boolean digitOrPeriodOrSign(char ch) {
        return ('0' <= ch && ch <= '9') || ch == '+' || ch == '-' || ch == '.';
    }

    /** The general path of _parseLong, unchanged. */
    private static long referenceLong(CharSequence text) {
        CharSequence s = WhiteSpaceProcessor.trim(text);
        // removeOptionalPlus
        if (s.length() > 1 && s.charAt(0) == '+') {
            s = s.subSequence(1, s.length());
            char ch = s.charAt(0);
            if (!('0' <= ch && ch <= '9') && ch != '.') throw new NumberFormatException();
        }
        return Long.parseLong(s.toString());
    }

    private static String outcome(java.util.concurrent.Callable<Object> c) {
        try {
            Object v = c.call();
            if (v instanceof Double d) return "double:" + Long.toHexString(Double.doubleToRawLongBits(d));
            return String.valueOf(v);
        } catch (NumberFormatException e) {
            return "NumberFormatException";
        } catch (Exception e) {
            return e.getClass().getName();
        }
    }

    private static void checkDouble(String text) {
        assertEquals(outcome(() -> referenceDouble(text)), outcome(() -> DatatypeConverterImpl._parseDouble(text)), text);
        assertEquals(outcome(() -> referenceDouble(text)),
                outcome(() -> DatatypeConverterImpl._parseDouble(new StringBuilder(text))), text);
    }

    private static void checkLong(String text) {
        assertEquals(outcome(() -> referenceLong(text)), outcome(() -> DatatypeConverterImpl._parseLong(text)), text);
        assertEquals(outcome(() -> referenceLong(text)),
                outcome(() -> DatatypeConverterImpl._parseLong(new StringBuilder(text))), text);
    }

    @Test
    public void doubleEdgeCases() {
        for (String s : new String[] {
                "0", "-0", "+0", "0.0", "-0.0", ".5", "-.5", "+.5", "5.", "-5.", ".", "-", "+", "", " ", "\t\n 1.5 \r",
                "1.5", "0.1", "0.3", "2.675", "1.7976931348623157", "9007199254740992", "9007199254740993",
                "9007199254740991.5", "123456789012345678", "1234567890123456789", "0.0000000000000000000001",
                "0.00000000000000000000001", "1.0000000000000000000000", "00000000000000000000000001.25",
                "1e3", "1E-3", "1.5e+2", "INF", "-INF", "NaN", "+INF", "inf", "1.2.3", "1,5", "--1", "+-1", "1-",
                "1 2", "0x10", "1d", "1f", "\u0661\u0662", "4.9E-324", "1.5 ", " -0.000", "99999999999999999.9"}) {
            checkDouble(s);
        }
    }

    @Test
    public void randomDecimals() {
        Random r = new Random(1234);
        for (int n = 0; n < 200_000; n++) {
            StringBuilder b = new StringBuilder();
            if (r.nextInt(4) == 0) b.append(r.nextBoolean() ? '-' : '+');
            int intDigits = r.nextInt(12);
            for (int i = 0; i < intDigits; i++) b.append((char) ('0' + r.nextInt(10)));
            if (r.nextInt(3) != 0) {
                b.append('.');
                int frac = r.nextInt(r.nextInt(5) == 0 ? 25 : 10);
                for (int i = 0; i < frac; i++) b.append((char) ('0' + r.nextInt(10)));
            }
            checkDouble(b.toString());
        }
        for (int n = 0; n < 100_000; n++) {
            double d = r.nextInt(3) == 0 ? r.nextGaussian() * Math.pow(10, r.nextInt(30) - 15) : r.nextInt(1_000_000) / 100.0;
            checkDouble(Double.toString(d));
            checkDouble(new java.math.BigDecimal(d).round(new java.math.MathContext(1 + r.nextInt(17))).toPlainString());
        }
    }

    @Test
    public void longCases() {
        for (String s : new String[] {
                "0", "-0", "+0", "1", "-1", "+1", " 42 ", "\t-42\n", "999999999999999999", "-999999999999999999",
                "1000000000000000000", "9223372036854775807", "-9223372036854775808", "9223372036854775808",
                "0000000000000000000000001", "", " ", "-", "+", "+-1", "-+1", "--1", "1-", "1 2", "1.0", "1e3", "0x1",
                "\u0661\u0662", "12a"}) {
            checkLong(s);
        }
        Random r = new Random(99);
        for (int n = 0; n < 100_000; n++) {
            long v = r.nextLong() >> r.nextInt(64);
            checkLong((r.nextInt(5) == 0 && v >= 0 ? "+" : "") + v);
        }
    }

    /**
     * A CharSequence over a String that counts toString() calls and optionally refuses them,
     * so a test can tell whether the allocation-free fast path or the general path ran.
     */
    private static final class Probe implements CharSequence {
        private final String text;
        private final boolean failOnToString;
        final AtomicInteger toStringCalls = new AtomicInteger();

        Probe(String text, boolean failOnToString) {
            this.text = text;
            this.failOnToString = failOnToString;
        }

        @Override public int length() { return text.length(); }
        @Override public char charAt(int index) { return text.charAt(index); }
        @Override public CharSequence subSequence(int start, int end) { return text.substring(start, end); }

        @Override
        public String toString() {
            toStringCalls.incrementAndGet();
            if (failOnToString) throw new IllegalStateException("toString() must not be called on the fast path");
            return text;
        }
    }

    private static void assertFastDouble(String text, double expected) {
        double actual = DatatypeConverterImpl._parseDouble(new Probe(text, true));
        assertEquals(Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(actual), text);
    }

    private static void assertFastLong(String text, long expected) {
        assertEquals(expected, DatatypeConverterImpl._parseLong(new Probe(text, true)), text);
    }

    /** Inputs without surrounding whitespace, so the general path calls toString() exactly once. */
    private static void assertGeneralDouble(String text, double expected) {
        Probe p = new Probe(text, false);
        double actual = DatatypeConverterImpl._parseDouble(p);
        assertEquals(Double.doubleToRawLongBits(expected), Double.doubleToRawLongBits(actual), text);
        assertEquals(1, p.toStringCalls.get(), text);
    }

    private static void assertGeneralLong(String text, long expected) {
        Probe p = new Probe(text, false);
        assertEquals(expected, DatatypeConverterImpl._parseLong(p), text);
        assertEquals(1, p.toStringCalls.get(), text);
    }

    @Test
    public void doubleFastPathDoesNotConvertToString() {
        assertFastDouble("0", 0.0);
        assertFastDouble("-0", -0.0);
        assertFastDouble("+0", 0.0);
        assertFastDouble("0.0", 0.0);
        assertFastDouble("-0.0", -0.0);
        assertFastDouble("42", 42.0);
        assertFastDouble("-42", -42.0);
        assertFastDouble("+42", 42.0);
        assertFastDouble("5.", 5.0);
        assertFastDouble(".5", 0.5);
        assertFastDouble("-.5", -0.5);
        assertFastDouble("1.5", 1.5);
        assertFastDouble("0.1", 0.1);
        assertFastDouble("2.675", 2.675);
        assertFastDouble("9007199254740992", 9007199254740992.0);
        assertFastDouble("0.0000000000000000000001", 1e-22);
        assertFastDouble(" \t\r\n1.5 \t\r\n", 1.5);
        assertFastDouble("  -0  ", -0.0);
        // just inside the limits: significand exactly 2^53, 22 fraction digits, leading zeros
        // not counted
        assertFastDouble("900719925474099.2", 900719925474099.2);
        assertFastDouble("0.0000000000000000000025", 2.5e-21);
        assertFastDouble("00000000000000000000000001.25", 1.25);
    }

    @Test
    public void longFastPathDoesNotConvertToString() {
        assertFastLong("0", 0L);
        assertFastLong("-0", 0L);
        assertFastLong("+0", 0L);
        assertFastLong("7", 7L);
        assertFastLong("-7", -7L);
        assertFastLong("+7", 7L);
        assertFastLong(" \t\r\n42 \t\r\n", 42L);
        assertFastLong("\t-42\n", -42L);
        assertFastLong("999999999999999999", 999_999_999_999_999_999L);
        assertFastLong("-999999999999999999", -999_999_999_999_999_999L);
        assertFastLong("+000000000000000001", 1L);
        assertFastLong("-000000000000000000", 0L);
    }

    /**
     * Guard against a fast path that is too permissive. It does not verify the change under
     * test: it also passes with the fast path removed.
     */
    @Test
    public void guardDoubleOutsideFastPathUsesGeneralPath() {
        assertGeneralDouble("1e3", 1000.0);
        assertGeneralDouble("1.5E-3", 0.0015);
        assertGeneralDouble("INF", Double.POSITIVE_INFINITY);
        assertGeneralDouble("-INF", Double.NEGATIVE_INFINITY);
        assertGeneralDouble("NaN", Double.NaN);
        // significand above 2^53 (the trailing zero counts)
        assertGeneralDouble("9007199254740992.0", 9007199254740992.0);
        // more than 18 significant digits (the trailing zeros count)
        assertGeneralDouble("1.0000000000000000000000", 1.0);
        // significand above 2^53
        assertGeneralDouble("9007199254740993", 9007199254740993.0);
        assertGeneralDouble("123456789012345678", 123456789012345678.0);
        // more than 22 fraction digits
        assertGeneralDouble("0.00000000000000000000001", 1e-23);
        // more than 18 significant digits
        assertGeneralDouble("1.2345678901234567890", 1.234567890123456789);
    }

    /**
     * Guard against a fast path that is too permissive. It does not verify the change under
     * test: it also passes with the fast path removed.
     */
    @Test
    public void guardLongOutsideFastPathUsesGeneralPath() {
        assertGeneralLong("1000000000000000000", 1_000_000_000_000_000_000L);
        assertGeneralLong("-1000000000000000000", -1_000_000_000_000_000_000L);
        assertGeneralLong("9223372036854775807", Long.MAX_VALUE);
        assertGeneralLong("-9223372036854775808", Long.MIN_VALUE);
    }

    /**
     * Guard against a fast path that accepts invalid input. It does not verify the change under
     * test: it also passes with the fast path removed.
     */
    @Test
    public void guardInvalidInputsAreRejectedByGeneralPath() {
        for (String s : new String[] {"", ".", "-", "+", "1e", "abc", "1.2.3", "--1"}) {
            Probe p = new Probe(s, false);
            assertThrows(NumberFormatException.class, () -> DatatypeConverterImpl._parseDouble(p), s);
            assertEquals(1, p.toStringCalls.get(), s);
        }
        for (String s : new String[] {"", "-", "+", "1.0", "1e3", "12a", "--1"}) {
            Probe p = new Probe(s, false);
            assertThrows(NumberFormatException.class, () -> DatatypeConverterImpl._parseLong(p), s);
            assertEquals(1, p.toStringCalls.get(), s);
        }
        assertThrows(NumberFormatException.class, () -> DatatypeConverterImpl._parseLong(new Probe("+-1", false)));
    }
}

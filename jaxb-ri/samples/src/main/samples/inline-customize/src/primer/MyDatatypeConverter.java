/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package primer;

import java.math.BigInteger;
import javax.xml.bind.DatatypeConverter;

public class MyDatatypeConverter {

  public static short parseIntegerToShort(String value) {
	BigInteger result = DatatypeConverter.parseInteger(value);
	return (short)(result.intValue());
  }

  public static String printShortToInteger(short value) {
        BigInteger result = BigInteger.valueOf(value);
        return DatatypeConverter.printInteger(result);
  }

  public static int parseIntegerToInt(String value) {
	BigInteger result = DatatypeConverter.parseInteger(value);
	return result.intValue();
  }

  public static String printIntToInteger(int value) {
       BigInteger result = BigInteger.valueOf(value);
       return DatatypeConverter.printInteger(result);
  }
};

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
 * Use is subject to the license terms.
 */
package com.sun.tools.xjc;

import com.sun.istack.SAXParseException2;
import com.sun.tools.xjc.api.ErrorListener;

import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * Implemented by the driver of the compiler engine to handle
 * errors found during the compiliation.
 * 
 * <p>
 * This class implements {@link ErrorHandler} so it can be
 * passed to anywhere where {@link ErrorHandler} is expected.
 * 
 * <p>
 * However, to make the error handling easy (and make it work
 * with visitor patterns nicely),
 * none of the methods on thi class throws {@link org.xml.sax.SAXException}.
 * Instead, when the compilation needs to be aborted,
 * it throws {@link AbortException}, which is unchecked.
 * 
 * <p>
 * This also implements the externally visible {@link ErrorListener}
 * so that we can reuse our internal implementation for testing and such.
 * 
 * @author Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class ErrorReceiver  implements ErrorHandler, ErrorListener {

//
//
// convenience methods for callers
//    
//
    /**
     * @param loc
     *      can be null if the location is unknown
     */
    public final void error( Locator loc, String msg ) {
        error( new SAXParseException2(msg,loc) );
    }

    public final void error( Locator loc, String msg, Exception e ) {
        error( new SAXParseException2(msg,loc,e) );
    }

    public final void error( String msg, Exception e ) {
        error( new SAXParseException2(msg,null,e) );
    }

    public void error(Exception e) {
        error(e.getMessage(),e);
    }

    /**
     * @param loc
     *      can be null if the location is unknown
     */
    public final void warning( Locator loc, String msg ) {
        warning( new SAXParseException(msg,loc) );
    }
    
//
//
// ErrorHandler implementation, but can't throw SAXException
//
//
    public abstract void error(SAXParseException exception) throws AbortException;
    public abstract void fatalError(SAXParseException exception) throws AbortException;
    public abstract void warning(SAXParseException exception) throws AbortException;

    /**
     * This method will be invoked periodically to allow {@link AbortException}
     * to be thrown, especially when this is driven by some kind of GUI.
     */
    public void pollAbort() throws AbortException {
    }

    /**
     * Reports verbose messages to users.
     * 
     * This method can be used to report additional non-essential
     * messages. The implementation usually discards them
     * unless some specific debug option is turned on.
     */
    public abstract void info(SAXParseException exception) /*REVISIT:throws AbortException*/;

    /**
     * Reports a debug message to users.
     * 
     * @see #info(SAXParseException)
     */
    public final void debug( String msg ) {
        info( new SAXParseException(msg,null) );
    }

//
//
// convenience methods for derived classes
//
//
    
  /**
   * Returns the human readable string representation of the 
   * {@link org.xml.sax.Locator} part of the specified
   * {@link SAXParseException}.
   * 
   * @return  non-null valid object.
   */
  protected final String getLocationString( SAXParseException e ) {
      if(e.getLineNumber()!=-1 || e.getSystemId()!=null) {
          int line = e.getLineNumber();
          return Messages.format( Messages.LINE_X_OF_Y,
              line==-1?"?":Integer.toString( line ),
              getShortName( e.getSystemId() ) );
      } else {        
          return Messages.format( Messages.UNKNOWN_LOCATION );
      }
  }
    
  /** Computes a short name of a given URL for display. */
  private String getShortName( String url ) {
      if(url==null)  
          return Messages.format( Messages.UNKNOWN_FILE );

// sometimes the user deals with a set of schems that reference each other
// in a complicated way, and end up importing two versions of the same schema.
// just printing the file name makes it very difficult to recognize of this problem.
// so I decided to change it back to print the full URL.

//      int idx;
//
//      // system Id can be URL, so we can't use File.separator
//      idx = url.lastIndexOf('/');
//      if(idx!=-1)     return url.substring(idx+1);
//      idx = url.lastIndexOf('\\');
//      if(idx!=-1)     return url.substring(idx+1);
        
      return url;
  }
}


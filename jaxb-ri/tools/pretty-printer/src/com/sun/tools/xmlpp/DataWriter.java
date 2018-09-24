
//@@3RD PARTY CODE@@
// DataWriter.java - XML writer for data-oriented files.

package com.sun.tools.xmlpp;

import java.io.Writer;
import java.util.Stack;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;


/**
 * Write data- or field-oriented XML.
 *
 * <p>This filter pretty-prints field-oriented XML without mixed content.
 * all added indentation and newlines will be passed on down
 * the filter chain (if any).</p>
 *
 * <p>In general, all whitespace in an XML document is potentially
 * significant, so a general-purpose XML writing tool like the
 * {@link com.megginson.sax.XMLWriter XMLWriter} class cannot
 * add newlines or indentation.</p>
 *
 * <p>There is, however, a large class of XML documents where information
 * is strictly fielded: each element contains either character data
 * or other elements, but not both.  For this special case, it is possible
 * for a writing tool to provide automatic indentation and newlines
 * without requiring extra work from the user.  Note that this class
 * will likely not yield appropriate results for document-oriented
 * XML like XHTML pages, which mix character data and elements together.</p>
 *
 * <p>This writer will automatically place each start tag on a new line,
 * optionally indented if an indent step is provided (by default, there
 * is no indentation).  If an element contains other elements, the end
 * tag will also appear on a new line with leading indentation.  Consider,
 * for example, the following code:</p>
 *
 * <pre>
 * DataWriter w = new DataWriter();
 *
 * w.setIndentStep(2);
 * w.startDocument();
 * w.startElement("Person");
 * w.dataElement("name", "Jane Smith");
 * w.dataElement("date-of-birth", "1965-05-23");
 * w.dataElement("citizenship", "US");
 * w.endElement("Person");
 * w.endDocument();
 * </pre>
 *
 * <p>This code will produce the following document:</p>
 *
 * <pre>
 * &lt;?xml version="1.0" standalone="yes"?>
 *
 * &lt;Person>
 *   &lt;name>Jane Smith&lt;/name>
 *   &lt;date-of-birth>1965-05-23&lt;/date-of-birth>
 *   &lt;citizenship>US&lt;/citizenship>
 * &lt;/Person>
 * </pre>
 *
 * <p>This class inherits from {@link com.megginson.sax.XMLWriter
 * XMLWriter}, and provides all of the same support for Namespaces.</p>
 *
 * @author David Megginson, david@megginson.com
 * @version 0.2
 * @see XMLWriter
 */
public class DataWriter extends XMLWriter implements LexicalHandler
{



    ////////////////////////////////////////////////////////////////////
    // Constructors.
    ////////////////////////////////////////////////////////////////////


    /**
     * Create a new data writer for standard output.
     */
    public DataWriter ()
    {
	super();
    }


    /**
     * Create a new data writer for standard output.
     *
     * <p>Use the XMLReader provided as the source of events.</p>
     *
     * @param xmlreader The parent in the filter chain.
     */
    public DataWriter (XMLReader xmlreader)
    {
	super(xmlreader);
    }


    /**
     * Create a new data writer for the specified output.
     *
     * @param writer The character stream where the XML document
     *        will be written.
     */
    public DataWriter (Writer writer)
    {
	super(writer);
    }


    /**
     * Create a new data writer for the specified output.
     * <p>Use the XMLReader provided as the source of events.</p>
     *
     * @param xmlreader The parent in the filter chain.
     * @param writer The character stream where the XML document
     *        will be written.
     */
    public DataWriter (XMLReader xmlreader, Writer writer)
    {
	super(xmlreader, writer);
    }



    ////////////////////////////////////////////////////////////////////
    // Accessors and setters.
    ////////////////////////////////////////////////////////////////////


    /**
     * Return the current indent step.
     *
     * <p>Return the current indent step: each start tag will be
     * indented by this number of spaces times the number of
     * ancestors that the element has.</p>
     *
     * @return The number of spaces in each indentation step,
     *         or 0 or less for no indentation.
     * @see #setIndentStep(int)
     */
    public int getIndentStep ()
    {
	return indentStep;
    }


    /**
     * Set the current indent step.
     *
     * @param indentStep The new indent step (0 or less for no
     *        indentation).
     * @see #getIndentStep()
     */
    public void setIndentStep (int indentStep)
    {
	this.indentStep = indentStep;
    }



    ////////////////////////////////////////////////////////////////////
    // Override methods from XMLWriter.
    ////////////////////////////////////////////////////////////////////


    /**
     * Reset the writer so that it can be reused.
     *
     * <p>This method is especially useful if the writer failed
     * with an exception the last time through.</p>
     *
     * @see XMLWriter#reset()
     */
    public void reset ()
    {
	depth = 0;
	state = SEEN_NOTHING;
	stateStack = new Stack();
	super.reset();
    }


    /**
     * Write a start tag.
     *
     * <p>Each tag will begin on a new line, and will be
     * indented by the current indent step times the number
     * of ancestors that the element has.</p>
     *
     * <p>The newline and indentation will be passed on down
     * the filter chain through regular characters events.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName The element's qualified (prefixed) name.
     * @param atts The element's attribute list.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the start tag, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#startElement(String, String, String, Attributes)
     */
    public void startElement (String uri, String localName,
			      String qName, Attributes atts)
	throws SAXException
    {
	stateStack.push(SEEN_ELEMENT);
	state = SEEN_NOTHING;
	if (depth > 0) {
	    super.characters("\n");
	}
	doIndent();
	super.startElement(uri, localName, qName, atts);
	depth++;
    }


    /**
     * Write an end tag.
     *
     * <p>If the element has contained other elements, the tag
     * will appear indented on a new line; otherwise, it will
     * appear immediately following whatever came before.</p>
     *
     * <p>The newline and indentation will be passed on down
     * the filter chain through regular characters events.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName The element's qualified (prefixed) name.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the end tag, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#endElement(String, String, String)
     */
    public void endElement (String uri, String localName, String qName)
	throws SAXException
    {
	depth--;
	if (state == SEEN_ELEMENT) {
	    super.characters("\n");
	    doIndent();
	}
	super.endElement(uri, localName, qName);
	state = stateStack.pop();
    }


    /**
     * Write a empty element tag.
     *
     * <p>Each tag will appear on a new line, and will be
     * indented by the current indent step times the number
     * of ancestors that the element has.</p>
     *
     * <p>The newline and indentation will be passed on down
     * the filter chain through regular characters events.</p>
     *
     * @param uri The element's Namespace URI.
     * @param localName The element's local name.
     * @param qName The element's qualified (prefixed) name.
     * @param atts The element's attribute list.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the empty tag, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#emptyElement(String, String, String, Attributes)
     */
    public void emptyElement (String uri, String localName,
			      String qName, Attributes atts)
	throws SAXException
    {
	state = SEEN_ELEMENT;
	if (depth > 0) {
	    super.characters("\n");
	}
	doIndent();
	super.emptyElement(uri, localName, qName, atts);
    }


    /**
     * Write a sequence of characters.
     *
     * @param ch The characters to write.
     * @param start The starting position in the array.
     * @param length The number of characters to use.
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the characters, or if a filter further
     *            down the chain raises an exception.
     * @see XMLWriter#characters(char[], int, int)
     */
    public void characters (char ch[], int start, int length)
	throws SAXException
    {
	state = SEEN_DATA;
	super.characters(ch, start, length);
    }
    
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (depth > 0) {
            super.characters("\n");
        }
        doIndent();
        String body = formatCommentBody(new String(ch,start,length));
        super.comment(body.toCharArray(),0,body.length());
        if(depth==0)
            super.characters("\n");
    }
    
    private String formatCommentBody( String str ) {
        if( str.indexOf('\n')==-1 ) return str; // leave it untouched
        
        int n = indentStep * depth;
        StringBuffer indent = new StringBuffer(n);
        for( int i=0; i<n; i++ )
            indent.append(' ');
        
        StringBuffer result = new StringBuffer(str.length());
        result.append('\n');
        
        StringTokenizer tokens = new StringTokenizer(str,"\n");
        while(tokens.hasMoreTokens()) {
            String line = tokens.nextToken().trim();
            if( line.length()!=0 || tokens.hasMoreTokens() ) {
                result.append(indent);
                result.append("  ");
                result.append(line);
                result.append('\n');
            }
        }
        result.append(indent);
        
        return result.toString();
    }
    
    



    ////////////////////////////////////////////////////////////////////
    // Internal methods.
    ////////////////////////////////////////////////////////////////////


    /**
     * Print indentation for the current level.
     *
     * @exception org.xml.sax.SAXException If there is an error
     *            writing the indentation characters, or if a filter
     *            further down the chain raises an exception.
     */
    private void doIndent ()
	throws SAXException
    {
	if (indentStep > 0 && depth > 0) {
	    int n = indentStep * depth;
	    char ch[] = new char[n];
	    for (int i = 0; i < n; i++) {
		ch[i] = ' ';
	    }
	    characters(ch, 0, n);
	}
    }



    ////////////////////////////////////////////////////////////////////
    // Constants.
    ////////////////////////////////////////////////////////////////////

    private final static Object SEEN_NOTHING = new Object();
    private final static Object SEEN_ELEMENT = new Object();
    private final static Object SEEN_DATA = new Object();



    ////////////////////////////////////////////////////////////////////
    // Internal state.
    ////////////////////////////////////////////////////////////////////

    private Object state = SEEN_NOTHING;
    private Stack stateStack = new Stack();

    private int indentStep = 0;
    private int depth = 0;

}

// end of DataWriter.java

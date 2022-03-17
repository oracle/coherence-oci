/*
 * Copyright (c) 2022, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package com.oracle.coherence.oci.testing;

import com.tangosol.run.xml.XmlDocument;
import com.tangosol.run.xml.XmlDocumentReference;

import java.util.Objects;

/**
 * A mock {@link XmlDocumentReference}.
 *
 * @author Jonathan Knight  2022.01.25
 * @since 22.06
 */
public class XmlDocumentReferenceStub
        extends XmlDocumentReference
    {
    // ----- constructors ---------------------------------------------------

    /**
     * Create a {@link XmlDocumentReferenceStub}.
     *
     * @param xml  the wrapped {@link XmlDocument}
     */
    public XmlDocumentReferenceStub(XmlDocument xml)
        {
        super("");
        f_xml = Objects.requireNonNull(xml);
        }

    // ----- XmlDocumentReference methods -----------------------------------

    @Override
    public XmlDocument getXmlDocument(ClassLoader classLoader)
        {
        return f_xml;
        }

    // ----- data members ---------------------------------------------------

    /**
     * The wrapped {@link XmlDocument}.
     */
    private final XmlDocument f_xml;
    }

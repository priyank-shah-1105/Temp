package com.dell.asm.ui.util;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class UploadFileUtil {

    private static final Logger LOG = Logger.getLogger(UploadFileUtil.class);

    // make private so that this class it is not instantiated
    private UploadFileUtil() {
    }

    public static boolean isUploadWellFormedXml(final InputStream in) {
        try {
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            docBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            docBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            // we dont want to validate against a dtd just check if the input is well-formed xml
            docBuilderFactory.setValidating(false);

            final DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            final Document doc = docBuilder.parse(in);
            return true;
        } catch (ParserConfigurationException | SAXException e) {
            LOG.info("A parse exception occurred while trying to parse input for well-formed xml",
                     e);
            return false;
        } catch (IOException e) {
            LOG.error("An error occurred while trying to parse input for well-formed xml",
                      e);
            return false;
        }
    }
}

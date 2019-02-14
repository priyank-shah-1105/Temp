package com.dell.asm.ui.exception;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.AuthenticationException;

import com.dell.asm.i18n2.AsmDetailedMessageList;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class ASMUIAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ASMUIAuthenticationException.class);
    private AsmDetailedMessageList errorMessageList;

    public ASMUIAuthenticationException(final String msg) {
        super(msg);
    }

    public ASMUIAuthenticationException(final Throwable t) {
        super(StringUtils.EMPTY, t);
        if (t instanceof WebApplicationException) {
            errorMessageList = extractErrorMessageListFromResponse(
                    ((WebApplicationException) t).getResponse());
        }
    }

    protected AsmDetailedMessageList extractErrorMessageListFromResponse(
            final Response errorResponse) {
        AsmDetailedMessageList messageList = null;
        InputStream in = null;
        try {
            if (errorResponse.getEntity() != null) {
                final JAXBContext jaxbContext = JAXBContext.newInstance(
                        AsmDetailedMessageList.class);
                final Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                in = IOUtils.toBufferedInputStream((InputStream) errorResponse.getEntity());
                if (in.available() > 0) {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document document = db.parse(in);
                    messageList = (AsmDetailedMessageList) jaxbUnmarshaller.unmarshal(document);
                }
            }
        } catch (JAXBException e) {
            LOG.error("Unable to parse WebApplicationException", e);
        } catch (IOException e) {
            LOG.error("Unable to create input stream for error response in WebApplicationException",
                      e);
        } catch (ParserConfigurationException e) {
            LOG.warn("SECURITY: Could not set 'http://apache.org/xml/features/disallow-doctype-decl' attribute to 'true' on DocumentBuilderFactory. Continuing anyway.");
        } catch (SAXException e) {
            LOG.error("Unable to parse WebApplicationException", e);
        } finally {
            IOUtils.closeQuietly(in);
        }

        return messageList;
    }

    public AsmDetailedMessageList getErrorMessageList() {
        return errorMessageList;
    }
}

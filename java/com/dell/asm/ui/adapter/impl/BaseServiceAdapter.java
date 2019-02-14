package com.dell.asm.ui.adapter.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.dell.asm.rest.common.client.AuthenticatedRestAPIWebClient;
import com.dell.asm.rest.common.model.AccessKeyPair;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.spring.ASMUserDetails;

/**
 * The Class BaseServiceAdapter.
 */
public abstract class BaseServiceAdapter {

    /** The Constant log. */
    private static final Logger log = Logger.getLogger(BaseServiceAdapter.class);

    /**
     * env: Context object which holds context environment entry values.
     */
    private final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
        }

        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    } };

    /**
     * Service path, from application context.
     */
    private String servicePath = null;
    /**
     * Service url, calculated from path / host / port.
     */
    private String serviceUrl = null;
    /**
     * Service port if different from default.
     */
    private String servicePort = null;

    private ServiceProperties serviceProperties;

    public BaseServiceAdapter(ServiceProperties serviceProperties) {
        this.serviceProperties = serviceProperties;
    }

    /**
     * Gets the default service path (no host or port).
     *
     * @return the default service path
     */
    public String getServicePath() {
        return servicePath;
    }

    /**
     * Sets the default service url.
     *
     * @param path
     *            the new default service url
     */
    public void setServicePath(String path) {
        String baseHost = serviceProperties.getHost();
        String basePort = serviceProperties.getPort();

        if (getServicePort() != null)
            basePort = getServicePort();

        boolean isSecure = serviceProperties.isSecureConnection();

        try {
            URL url;
            if (path.startsWith("http")) {
                url = new URL(path);
            } else {
                url = new URL(
                        (isSecure ? "https://" : "http://") + baseHost + ":" + basePort + path);
            }
            serviceUrl = url.toString();
        } catch (MalformedURLException e) {
            log.warn("Malformed URL, service URL is not set");
        }
        log.debug("Service URL: " + serviceUrl);
    }

    /**
     * Gets the default service url.
     *
     * @return the default service url
     */
    public String getServiceUrl() {
        return serviceUrl;
    }

    /**
     * Creates WebClient helper with x-dell auth headers processing.
     *
     * @return the web client
     */
    WebClient createWebClient() {
        AccessKeyPair akp = new AccessKeyPair();
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc != null && sc.getAuthentication() != null) {
            ASMUserDetails userDetails =
                    (ASMUserDetails) sc.getAuthentication().getPrincipal();

            if (userDetails != null && userDetails.getAuthToken() != null) {
                akp.setApiKey(userDetails.getAuthToken().getApiKey());
                akp.setApiSecret(userDetails.getAuthToken().getApiSecret());
            }
        }

        WebClient webClient = AuthenticatedRestAPIWebClient.create(getServiceUrl(), akp,
                                                                   new ArrayList<>());
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HTTPConduit conduit = WebClient.getConfig(webClient).getHttpConduit();
            TLSClientParameters tlsCP = new TLSClientParameters();
            tlsCP.setDisableCNCheck(true);
            tlsCP.setSSLSocketFactory(sslSocketFactory);
            conduit.setTlsClientParameters(tlsCP);
            conduit.getClient().setReceiveTimeout(180000);
            conduit.getClient().setConnectionTimeout(180000);
            //WebClient.getConfig(webClient).setSynchronousTimeout(180000);

        } catch (Exception e) {
            log.error("Error in SSL trust manager.", e);
        }

        webClient.type(MediaType.APPLICATION_XML);
        return webClient;
    }

    WebClient createStreamingWebClient() {
        AccessKeyPair akp = new AccessKeyPair();
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc != null && sc.getAuthentication() != null) {
            ASMUserDetails userDetails =
                    (ASMUserDetails) sc.getAuthentication().getPrincipal();

            if (userDetails != null && userDetails.getAuthToken() != null) {
                akp.setApiKey(userDetails.getAuthToken().getApiKey());
                akp.setApiSecret(userDetails.getAuthToken().getApiSecret());
            }
        }

        WebClient webClient = AuthenticatedRestAPIWebClient.createStreamingClient(getServiceUrl(),
                                                                                  akp,
                                                                                  new ArrayList<>());

        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HTTPConduit conduit = WebClient.getConfig(webClient).getHttpConduit();
            TLSClientParameters tlsCP = new TLSClientParameters();
            tlsCP.setDisableCNCheck(true);
            tlsCP.setSSLSocketFactory(sslSocketFactory);
            conduit.setTlsClientParameters(tlsCP);
            conduit.getClient().setReceiveTimeout(180000);
            conduit.getClient().setConnectionTimeout(180000);
            //WebClient.getConfig(webClient).setSynchronousTimeout(180000);

        } catch (Exception e) {
            log.error("Error in SSL trust manager.", e);
        }

        webClient.type(MediaType.APPLICATION_OCTET_STREAM);
        webClient.accept(MediaType.APPLICATION_OCTET_STREAM);

        return webClient;
    }

    /**
     * Gets the service port if different from default.
     *
     * @return the port
     */
    public String getServicePort() {
        return servicePort;
    }

    /**
     * Set service port.
     * @param port
     */
    public void setServicePort(String port) {
        servicePort = port;
    }

}

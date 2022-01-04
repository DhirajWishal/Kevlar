package com.kevlar.client;

import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class TrustManager implements X509TrustManager {
    private final ArrayList<X509Certificate> trustedCertificates = new ArrayList<>();

    /**
     * Default constructor.
     * This will read the trusted certificate from the provided directory and will use that certificate as the only
     * trusted certificate.
     *
     * @throws CertificateException This method can throw this error. Please refer CertificateFactory.getInstance().
     */
    public TrustManager() throws CertificateException {
        CertificateFactory fact = CertificateFactory.getInstance("X.509");
        String[] certificateFiles = new String[]{
                "../credentials/dhirajcert.pem",
                "../credentials/thulanacert.pem",
                "../credentials/faizancert.pem"};
        try {
            for (String filename : certificateFiles) {
                FileInputStream inputStream = new FileInputStream(filename);
                trustedCertificates.add((X509Certificate) fact.generateCertificate(inputStream));
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all the accepted issuers.
     *
     * @return The accepted issuers array.
     */
    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
        return trustedCertificates.toArray(new X509Certificate[]{});
    }

    /**
     * Check if the client certificates are trusted.
     *
     * @param certs    The certificates to check.
     * @param authType The authentication type.
     * @throws CertificateException This method can throw a certification exception if all the certificates are not trusted.
     */
    public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        for (X509Certificate certificate : certs)
            if (trustedCertificates.contains(certificate))
                return;

        throw new CertificateException("The certificates are not trusted!");
    }

    /**
     * Check if the server certificates are trusted.
     *
     * @param certs    The certificates to check.
     * @param authType The authentication type.
     * @throws CertificateException This method can throw a certification exception if all the certificates are not trusted.
     */
    public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {
        for (X509Certificate certificate : certs)
            if (trustedCertificates.contains(certificate))
                return;

        throw new CertificateException("The certificates are not trusted!");
    }
}

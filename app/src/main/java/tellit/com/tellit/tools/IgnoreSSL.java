package tellit.com.tellit.tools;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by probojnik on 6/17/15.
 */
public class IgnoreSSL {
    SSLSocketFactory sslSocketFactory;
    private static IgnoreSSL ourInstance = new IgnoreSSL();

    public static IgnoreSSL getInstance() {
        return ourInstance;
    }

    private IgnoreSSL() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            // Let us create the factory where we can set some parameters for the connection
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            sslSocketFactory = sc.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(sslSocketFactory);


        } catch (NoSuchAlgorithmException nsae) {
            System.err.println("soap::The context specified does not exist. Check for the existence of JSSE");
        } catch (KeyManagementException kme) {
            System.err.println(kme.getMessage());
        }
    }

    public SSLSocketFactory getSslSocketFactory() {
        return sslSocketFactory;
    }
}

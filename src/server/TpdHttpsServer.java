package server;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.CookieManager;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;


public class TpdHttpsServer {
    private int port;
    private HttpsServer server;
    private static String protocol = "TLS";

    public static Map<String, Long> sessionsUsers = new HashMap<>();
    public static int sessionCounter = 0;

    public static Map<String, AuthData> authTokens = new HashMap<>();

    public void start(int port) {
        try {

            CookieManager cm = new CookieManager();
            CookieManager.setDefault(cm);

            this.port = port;
            // load certificate
            String keystoreFilename = getPath() + "keys.jks";
            char[] storepass = "changeit".toCharArray();
            char[] keypass = "changeit".toCharArray();
            String alias = "alias";
            FileInputStream fIn = new FileInputStream(keystoreFilename);
            KeyStore keystore = KeyStore.getInstance("JKS");
            keystore.load(fIn, storepass);
            // display certificate
//			Certificate cert = keystore.getCertificate(alias);
//			System.out.println(cert);

            // setup the key manager factory
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keystore, keypass);

            // setup the trust manager factory
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(keystore);

            // create https server
            server = HttpsServer.create(new InetSocketAddress(port), 0);
            // create ssl context
            SSLContext sslContext = SSLContext.getInstance(protocol);
            // setup the HTTPS context and parameters
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
                public void configure(HttpsParameters params) {
                    try {
                        // initialise the SSL context
                        SSLContext c = SSLContext.getDefault();
                        SSLEngine engine = c.createSSLEngine();
                        params.setNeedClientAuth(false);
                        params.setCipherSuites(engine.getEnabledCipherSuites());
                        params.setProtocols(engine.getEnabledProtocols());

                        // get the default parameters
                        SSLParameters defaultSSLParameters = c.getDefaultSSLParameters();
                        params.setSSLParameters(defaultSSLParameters);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        System.out.println("Failed to create HTTPS server");
                    }
                }
            });


            System.out.println("server started at " + port);
            server.createContext("/", new Handlers.RootHandler());
            server.createContext("/users/getAll", new Handlers.GetAllUsersHandler()).setAuthenticator(new CustomAuthenticator());
            server.createContext("/files/get", new Handlers.GetFiles()).setAuthenticator(new CustomAuthenticator());
            server.createContext("/files/add", new Handlers.AddFile()).setAuthenticator(new CustomAuthenticator());
            server.createContext("/file/download", new Handlers.DownloadFile()).setAuthenticator(new CustomAuthenticator());
            server.createContext("/permissions", new Handlers.Permissions()).setAuthenticator(new CustomAuthenticator());
            server.createContext("/login", new Handlers.LoginHandler());
            server.createContext("/token", new Handlers.TokenHandler());
//            server.createContext("/echoHeader", new Handlers.EchoHeaderHandler());
//            server.createContext("/echoGet", new Handlers.EchoGetHandler());
//            server.createContext("/echoPost", new Handlers.EchoPostHandler());
//            server.createContext("/login", new Handlers.LoginHandler());
//            server.createContext("/token", new Handlers.TokenHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CertificateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getPath() {
        return this.getClass().getClassLoader().getResource("").getPath() + "server/";
    }

    public void Stop() {
        server.stop(0);
        System.out.println("server stopped");
    }
}

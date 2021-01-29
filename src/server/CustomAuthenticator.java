package server;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.net.HttpCookie;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.List;

public class CustomAuthenticator extends Authenticator {

    @Override
    public Result authenticate(HttpExchange httpExchange) {
        try {
            CookieManager cm = (CookieManager) CookieManager.getDefault();
            List<HttpCookie> cookies = cm.getCookieStore().getCookies();

            List<HttpCookie> requestCookies = HttpCookie.parse(httpExchange.getRequestHeaders().getFirst("Cookie"));
            HttpCookie sessionIdCookie = null;
            //se extrage cookie-ul cu session ID-ul
            for (HttpCookie c : requestCookies) {
                if (c.getName().equals("sessionId")) {
                    sessionIdCookie = c;
                    break;
                }
            }

            if (sessionIdCookie != null) {
                //se verifica daca cookie-ul cu session id-ul e creat de server
                for (HttpCookie c : cookies) {
                    if (c.getValue().equals(sessionIdCookie.getValue())) {
                        return new Success(new HttpPrincipal("USernamrew", "principal"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Failure(401);
    }
}

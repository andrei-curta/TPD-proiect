package server;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.BasicAuthenticator;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.net.CookieHandler;

public class CustomAuthenticator extends Authenticator {

    @Override
    public Result authenticate(HttpExchange httpExchange) {

        return new Failure(401);
//        CookieHandler cookieHandler = CookieHandler.getDefault();
//        cookieHandler.get();
    }
}

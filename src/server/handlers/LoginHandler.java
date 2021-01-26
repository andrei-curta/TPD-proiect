package server.handlers;

import DTO.LoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.FileVersionEntity;
import entities.UserEntity;
import javafx.util.Pair;
import server.repository.UserRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

public class LoginHandler implements HttpHandler {
    private Map<String, Long> sessionsUsers = new HashMap<>();
    private int sessionCounter = 0;
    UserRepository userRepository = new UserRepository();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        //get object sent by client
        ObjectMapper objectMapper = new ObjectMapper();
        LoginDto loginData = objectMapper.readValue(httpExchange.getRequestBody(), LoginDto.class);

        HttpCookie cookie;
        UserEntity user = userRepository.getUserByUsername(loginData.getUsername());

        if (user != null) {
            sessionCounter++;
            String sessionId = Integer.toString((user.getUsername() +
                    Integer.toString(sessionCounter)).hashCode()) ;
            cookie = new HttpCookie("sessionId", sessionId);
            httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
            sessionsUsers.put(sessionId, user.getId());
            System.out.println(sessionsUsers.toString());

            String response = "Login successful " + sessionId;
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } else {
            String response = "Login failed";
            httpExchange.sendResponseHeaders(401, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import server.repository.UserRepository;
import util.HibernateUtil;

import javax.xml.ws.handler.Handler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class Handlers {

    public static class RootHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            String response = "<h1>Server start success if you see this message</h1>" + "<h1>Port: " + "</h1>";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class GetAllUsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange he) throws IOException {
            UserRepository repo = new UserRepository();
            try {
                List<UserEntity> l = repo.getAll();

            } catch (Exception e) {
                e.printStackTrace();
            }

            List<UserEntity> list = repo.getAll();

            ObjectMapper objectMapper = new ObjectMapper();
            String response = objectMapper.writeValueAsString(list);
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

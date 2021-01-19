package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import util.HibernateUtil;

import java.io.IOException;
import java.io.OutputStream;

public class Handlers {

    public static class RootHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange he) throws IOException {
            System.out.println("intra");
            UserEntity user = new UserEntity();
//
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                user = session.get(UserEntity.class, 1);

            } catch (Exception e) {
                e.printStackTrace();
            }

            SessionFactory factory;

            try {
                factory = new Configuration().configure().buildSessionFactory();
                Session session = factory.openSession();
                user = session.get(UserEntity.class, new  Long(1));
            } catch (Throwable ex) {
                System.err.println("Failed to create sessionFactory object." + ex);
                throw new ExceptionInInitializerError(ex);
            }

            System.out.println(user.getUsername());

            String response = "<h1>Server start success if you see this message</h1>" + "<h1>Port: " + "</h1>";
            he.sendResponseHeaders(200, response.length());
            OutputStream os = he.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}

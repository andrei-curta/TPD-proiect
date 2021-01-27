package server;

import DTO.FileDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.FileEntity;
import entities.FileVersionEntity;
import entities.UserEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import server.repository.FileRepository;
import server.repository.FileVersionRepository;
import server.repository.UserRepository;
import util.HibernateUtil;

import javax.xml.ws.handler.Handler;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
//            he.getHttpContext().setAuthenticator()

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

    public static class GetFiles implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            //extracting query parameters
            String query = httpExchange.getRequestURI().getQuery();
            Map<String, String> params = queryToMap(query);

            long userId = -1;
            try {
                userId = Long.parseLong(params.get("userid"));
            } catch (NumberFormatException e) {
                httpExchange.sendResponseHeaders(400, e.getMessage().length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(e.getMessage().getBytes());
                os.close();
            }

            UserRepository userRepository = new UserRepository();
            UserEntity fileOwner = userRepository.get(userId);

            //todo: dehardcodat
            UserEntity requestUser = userRepository.get(1);

            try {
                List<FileDto> files = userRepository.getAllFilesCanAccessFrom(requestUser, fileOwner).stream().map(f -> new FileDto(f)).collect(Collectors.toList());
                ObjectMapper objectMapper = new ObjectMapper();
                String response = objectMapper.writeValueAsString(files);
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static class AddFile implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            //get object sent by client
            ObjectMapper objectMapper = new ObjectMapper();
            FileEntity file = objectMapper.readValue(httpExchange.getRequestBody(), FileEntity.class);

            file.setDateCreated(new Timestamp(System.currentTimeMillis()));
            //todo: set owner

            FileRepository fileRepository = new FileRepository();
            FileEntity file_db = fileRepository.create(file);

            //return created entity
            String response = objectMapper.writeValueAsString(file_db);
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class AddFileVersion implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            //get object sent by client
            ObjectMapper objectMapper = new ObjectMapper();
            FileVersionEntity file = objectMapper.readValue(httpExchange.getRequestBody(), FileVersionEntity.class);



            //Todo: check if the file belongs to the curent user

            FileVersionRepository fileVersionRepository = new FileVersionRepository();
            FileRepository fileRepository = new FileRepository();

//            int latestVersion = file.getFileByFileId()

                    FileVersionEntity file_db = fileVersionRepository.create(file);

            //return created entity
            String response = objectMapper.writeValueAsString(file_db);
            httpExchange.sendResponseHeaders(200, response.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
}

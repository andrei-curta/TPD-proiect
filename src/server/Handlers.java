package server;

import DTO.FileDto;
import DTO.UserDto;
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
import java.util.*;
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

                List<UserDto> list = repo.getAll().stream().map(f -> new UserDto(f)).collect(Collectors.toList());

                ObjectMapper objectMapper = new ObjectMapper();
                String response = objectMapper.writeValueAsString(list);
                he.sendResponseHeaders(200, response.length());
                OutputStream os = he.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class AddFile implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                FileDto fileDto = objectMapper.readValue(httpExchange.getRequestBody(), FileDto.class);
                FileRepository fileRepo = new FileRepository();
                UserRepository userRepository = new UserRepository();

                FileEntity fileEntity = new FileEntity();
                if (fileDto.getId() == null) {
                    fileEntity.setId(fileDto.getId());
                    fileEntity.setTitle(fileDto.getTitle());
                    fileEntity.setDateCreated(new Timestamp(new Date().getTime()));
                    //todo: set current user
                    fileEntity.setUserByOwnerId(userRepository.get(1));

                } else {
                    //todo: verificat daca are acces la ID

                    fileEntity = fileRepo.get(fileDto.getId());
                }

                FileVersionEntity fileVersionEntity = new FileVersionEntity();
                fileVersionEntity.setContents(fileDto.getLatestVersion().getContents());
                fileVersionEntity.setModifiedOn(new Timestamp(new Date().getTime()));
                //todo: set current user

                fileVersionEntity.setUserByModifiedBy(userRepository.get(1));

                fileVersionEntity.setFileByFileId(fileEntity);

                FileEntity fe = fileRepo.create(fileEntity);
                System.out.println(fe);

                String response = "Saved successfully";
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } catch (Exception e) {
                String response = e.getMessage();
                httpExchange.sendResponseHeaders(500, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
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

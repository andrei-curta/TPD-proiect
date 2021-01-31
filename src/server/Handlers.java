package server;

import DTO.FileDto;
import DTO.LoginDto;
import DTO.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import server.repository.*;
import sun.plugin.cache.FileVersion;
import util.HibernateUtil;

import javax.xml.ws.handler.Handler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
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
            FileRepository fileRepository = new FileRepository();

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

            UserEntity requestUser = userRepository.getUserByUsername(httpExchange.getPrincipal().getUsername());

            try {
                List<FileDto> files = fileRepository.getAllFilesCanAccessFrom(requestUser, fileOwner).stream().map(f -> new FileDto(f)).collect(Collectors.toList());
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
                FileVersionRepository fileVersionRepo = new FileVersionRepository();
                FilePermissionsRepository filePermissionsRepo = new FilePermissionsRepository();
                PermissionTypeRepository permissionTypeRepo = new PermissionTypeRepository();

                UserEntity currentUser = userRepository.getUserByUsername(httpExchange.getPrincipal().getUsername());

                boolean isNewFile = fileDto.getId() == null;

                FileEntity fileEntity = new FileEntity();
                if (isNewFile) {
                    fileEntity.setId(fileDto.getId());
                    fileEntity.setTitle(fileDto.getTitle());
                    fileEntity.setDateCreated(new Timestamp(new Date().getTime()));
                    fileEntity.setUserByOwnerId(currentUser);


                } else {
                    //todo: verificat daca are acces la ID

                    fileEntity = fileRepo.get(fileDto.getId());
                }

                FileVersionEntity fileVersionEntity = new FileVersionEntity();
                fileVersionEntity.setContents(fileDto.getLatestVersion().getContents());
                fileVersionEntity.setModifiedOn(new Timestamp(new Date().getTime()));

                fileVersionEntity.setUserByModifiedBy(currentUser);
                fileVersionEntity.setFileByFileId(fileEntity);

                FileEntity updatedFile = fileRepo.create(fileEntity);

                //se dau permisiuni pe fisier daca este creat un fisier nou
                if (isNewFile) {
                    List<PermissionTypeEntity> permissionTypeEntityList = permissionTypeRepo.getAll();
                    filePermissionsRepo.addFilePermissionsForUser(updatedFile, currentUser, permissionTypeEntityList);
                }

                fileVersionRepo.create(fileVersionEntity);

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

    public static class DownloadFile implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            try {

                UserRepository userRepository = new UserRepository();
                DownloadHistoryRepository downloadHistoryRepository = new DownloadHistoryRepository();
                FileVersionRepository fileVersionRepository = new FileVersionRepository();
                UserEntity currentUser = userRepository.getUserByUsername(httpExchange.getPrincipal().getUsername());

                String query = httpExchange.getRequestURI().getQuery();
                Map<String, String> params = queryToMap(query);

                long fileVersionId = -1;
                try {
                    fileVersionId = Long.parseLong(params.get("fileVersionId"));
                } catch (NumberFormatException e) {
                    httpExchange.sendResponseHeaders(400, e.getMessage().length());
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(e.getMessage().getBytes());
                    os.close();
                }

                FileVersionEntity fileVersionEntity = fileVersionRepository.get(fileVersionId);
                FileEntity file = fileVersionEntity.getFileByFileId();

                if (downloadHistoryRepository.canDownloadFile(currentUser, file)) {

                    byte[] response = fileVersionEntity.getContents().getBytes();
                    OutputStream outputStream = httpExchange.getResponseBody();
                    httpExchange.sendResponseHeaders(200, response.length);
                    outputStream.write(response);
                    outputStream.flush();
                    outputStream.close();
                    httpExchange.getRequestBody().close();

                    String filename = file.getTitle();
                    httpExchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=" + filename);

                    DownloadHistoryEntity downloadHistoryEntry = new DownloadHistoryEntity();
                    downloadHistoryEntry.setUserByUserId(currentUser);
                    downloadHistoryEntry.setFileVersionByFileVersionId(fileVersionEntity);
                    downloadHistoryEntry.setDate(new Timestamp(new Date().getTime()));

                    downloadHistoryRepository.create(downloadHistoryEntry);
                } else {
                    String response = "You can't download the file";
                    httpExchange.sendResponseHeaders(401, response.length());
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }


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

    public static class LoginHandler implements HttpHandler {

        UserRepository userRepository = new UserRepository();

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            //get object sent by client
            ObjectMapper objectMapper = new ObjectMapper();
            LoginDto loginData = objectMapper.readValue(httpExchange.getRequestBody(), LoginDto.class);

            HttpCookie cookie;
            UserEntity user = userRepository.getUserByUsername(loginData.getUsername());

            if (user != null) {
                TpdHttpsServer.sessionCounter++;
                String sessionId = Integer.toString((user.getId() +
                        Integer.toString(TpdHttpsServer.sessionCounter) + new Timestamp(System.currentTimeMillis()).toString()).hashCode());
                cookie = new HttpCookie("sessionId", sessionId);

                CookieManager cm = (CookieManager) CookieManager.getDefault();
                cm.getCookieStore().add(null, cookie);

                httpExchange.getResponseHeaders().add("Set-Cookie", cookie.toString());
                TpdHttpsServer.sessionsUsers.put(sessionId, user.getId());
                System.out.println(TpdHttpsServer.sessionsUsers.toString());

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
}

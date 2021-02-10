package server;

import DTO.FileDto;
import DTO.LoginDto;
import DTO.UserDto;
import DTO.UserFilePermissionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import entities.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import server.repository.*;
import sun.plugin.cache.FileVersion;
import util.EmailClient;
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
import java.util.UUID;

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
                List<FileEntity> filesCanAccess = fileRepository.getAllFilesCanAccessFrom(requestUser, fileOwner);
                List<FileDto> files = filesCanAccess.stream().map(f -> new FileDto(f)).collect(Collectors.toList());
                ObjectMapper objectMapper = new ObjectMapper();
                String response = objectMapper.writeValueAsString(files);
                System.out.println(response);
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

                //get version number
                if (isNewFile) {
                    fileVersionEntity.setVersionNumber(1);
                } else {
                    FileVersionEntity latestVersion = fileEntity.getLatestVersion();
                    Integer latestVersionNr = 0;

                    if (latestVersion != null) {
                        latestVersionNr = latestVersion.getVersionNumber();
                    }
                    fileVersionEntity.setVersionNumber(latestVersionNr + 1);
                }

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

    /**
     * sends an email with a token
     */
    public static class TokenHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {

            String query = httpExchange.getRequestURI().getQuery();
            Map<String, String> params = queryToMap(query);

            String username = params.get("username");
            if (username != null) {

                // Adding 10 mins using Date constructor.
                Calendar date = Calendar.getInstance();
                long timeInSecs = date.getTimeInMillis();
                int tokenValidityMinutes = 5;
                Date tokenValidUntil = new Date(timeInSecs + (tokenValidityMinutes * 60 * 1000));

                UUID uuid = UUID.randomUUID();
                String token = uuid.toString();

                TpdHttpsServer.authTokens.put(token, new AuthData(username, tokenValidUntil));

                String subject = "Your token";
                String text = "Your token valid for " + tokenValidityMinutes + "minutes is: " + token;

                EmailClient emailClient = new EmailClient();
                try {
                    emailClient.sendMail(username, subject, text);
                } catch (Exception e) {
                    String response = "Mail could not be sent";
                    httpExchange.sendResponseHeaders(500, response.length());
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
                String response = "Mail successfully sent";
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream os = httpExchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    public static class Permissions implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            //extracting query parameters
            String query = httpExchange.getRequestURI().getQuery();
            Map<String, String> params = queryToMap(query);
            FileRepository fileRepository = new FileRepository();


            if (httpExchange.getRequestMethod().equals("GET") && params.get("fileId") != null) {
                try {

                    FilePermissionsRepository filePermissionsRepository = new FilePermissionsRepository();
                    List<FilePermissionEntity> filePermissions = filePermissionsRepository.filePermissions(Long.parseLong(params.get("fileId")));
                    List<UserFilePermissionDto> files = filePermissions.stream().map(f -> new UserFilePermissionDto(f)).collect(Collectors.toList());

                    ObjectMapper objectMapper = new ObjectMapper();
                    String response = objectMapper.writeValueAsString(files);
                    System.out.println(response);
                    httpExchange.sendResponseHeaders(200, response.length());
                    OutputStream os = httpExchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


//            long userId = -1;
//            try {
//                userId = Long.parseLong(params.get("userid"));
//            } catch (NumberFormatException e) {
//                httpExchange.sendResponseHeaders(400, e.getMessage().length());
//                OutputStream os = httpExchange.getResponseBody();
//                os.write(e.getMessage().getBytes());
//                os.close();
//            }
//
//            UserRepository userRepository = new UserRepository();
//            UserEntity fileOwner = userRepository.get(userId);
//
//            UserEntity requestUser = userRepository.getUserByUsername(httpExchange.getPrincipal().getUsername());
//
//            try {
//                List<FileEntity> filesCanAccess = fileRepository.getAllFilesCanAccessFrom(requestUser, fileOwner);
//                List<FileDto> files = filesCanAccess.stream().map(f -> new FileDto(f)).collect(Collectors.toList());
//                ObjectMapper objectMapper = new ObjectMapper();
//                String response = objectMapper.writeValueAsString(files);
//                System.out.println(response);
//                httpExchange.sendResponseHeaders(200, response.length());
//                OutputStream os = httpExchange.getResponseBody();
//                os.write(response.getBytes());
//                os.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            }
        }
    }

    public static class LoginHandler implements HttpHandler {

        UserRepository userRepository = new UserRepository();

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            //get object sent by client
            ObjectMapper objectMapper = new ObjectMapper();
            LoginDto loginData = objectMapper.readValue(httpExchange.getRequestBody(), LoginDto.class);

            //check if the token is valid and not expired
            //  AuthData authData = TpdHttpsServer.authTokens.get(loginData.getToken());
//            if (authData == null || authData.isValid() == false) {
//                String response = "Login failed. Invalid token";
//                httpExchange.sendResponseHeaders(401, response.length());
//                OutputStream os = httpExchange.getResponseBody();
//                os.write(response.getBytes());
//                os.close();
//                return;
//            }

            HttpCookie cookie;
            //todo: reparet
            // UserEntity user = userRepository.getUserByUsername(authData.getUsername());
            UserEntity user = userRepository.get(1);

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

                //remove the token so it cannot be used
                TpdHttpsServer.authTokens.remove(loginData.getToken());

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

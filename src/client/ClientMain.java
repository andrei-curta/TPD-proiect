package client;

import client.models.UserUI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import entities.UserEntity;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import util.HttpRequestHelper;

import javax.net.ssl.HttpsURLConnection;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            System.out.println("initial cookies");
            cookieManager.getCookieStore().getCookies().forEach(cookie -> System.out.println(cookie.getName()));

//            String resp = HttpRequestHelper.post("https://localhost:9000/login", "{\"username\": \"test\"}");
//            System.out.println(resp);


            primaryStage.setTitle("Login");
            Parent root = FXMLLoader.load(getClass().getResource("./ui/LoginView.fxml"));
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

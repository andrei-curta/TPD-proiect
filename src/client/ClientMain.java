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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //Label for education
        Label label = new Label("Educational qualification:");
        Font font = Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 12);
        label.setFont(font);
        //list View for educational qualification

        String resp = null;

        try {
             resp = HttpRequestHelper.get("https://localhost:9000/files/get?userid=1");

        }catch (Exception e){
            e.printStackTrace();
        }


        List<UserUI> users = null;
        try {


// Manually converting the response body InputStream to APOD using Jackson
            ObjectMapper mapper = new ObjectMapper();
            CollectionType javaType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, UserUI.class);
            users = mapper.readValue(resp, javaType);
        }catch (Exception e){
            e.printStackTrace();
        }


        ObservableList<UserUI> names = FXCollections.observableArrayList(users);
        ListView<UserUI> listView = new ListView<UserUI>(names);
        listView.setMaxSize(250, 500);
        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked on " + listView.getSelectionModel().getSelectedItem().getId());
            }
        });


        //Creating the layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(5, 5, 5, 50));
        layout.getChildren().addAll(label, listView);
        layout.setStyle("-fx-background-color: BEIGE");
        //Setting the stage
        Scene scene = new Scene(layout, 595, 200);


        primaryStage.setTitle("List View Example");
//        primaryStage.setScene(scene);
        Parent root = FXMLLoader.load(getClass().getResource("./ui/UserFilesView.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

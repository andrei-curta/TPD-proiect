package client.ui;

import client.models.UserUI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import entities.FileEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import util.HttpRequestHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserFilesController implements Initializable {

    @FXML
    private TableView tblFiles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<FileEntity> files = null;
        try {
            String resp = HttpRequestHelper.get("https://localhost:9000/users/getAll");
            System.out.println(resp);
            ObjectMapper mapper = new ObjectMapper();
            CollectionType javaType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, FileEntity.class);
            files = mapper.readValue(resp, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onFileClicked(ActionEvent event) {
        System.out.println("file clicked");
    }
}

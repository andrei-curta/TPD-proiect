package client.ui;

import DTO.FileDto;

import DTO.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import util.HttpRequestHelper;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserFilesController {

    private UserDto currentPageUser;

    @FXML
    private TableView<FileDto> tblFiles;
    @FXML
    private TableColumn<FileDto, String> title;


    public void initData(UserDto user){
        System.out.println("in init data : " + user);
        currentPageUser = user;
        List<FileDto> files = null;
        try {
            String resp = HttpRequestHelper.get("https://localhost:9000/files/get?userid=1");
            System.out.println(resp);
            ObjectMapper mapper = new ObjectMapper();
            CollectionType javaType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, FileDto.class);
            files = mapper.readValue(resp, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<FileDto> fileDtoObservableList = FXCollections.observableArrayList(files);
        title.setCellValueFactory(new PropertyValueFactory<FileDto, String>("title"));
        tblFiles.getItems().setAll(fileDtoObservableList);
    }

    @FXML
    private void onFileClicked(ActionEvent event) {
        System.out.println("file clicked");
    }
}

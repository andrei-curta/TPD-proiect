package client.ui;

import DTO.FileDto;

import DTO.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import util.HttpRequestHelper;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserFilesController {

    private UserDto currentPageUser;

    @FXML
    private TableView<FileDto> tblFiles;
    @FXML
    private TableColumn<FileDto, String> title;
    @FXML
    private TableColumn<FileDto, String> owner;
    @FXML
    private TableColumn versionNo;
    @FXML
    public TableColumn lastModified;


    public void initData(UserDto user) {
        System.out.println("in init data : " + user);
        currentPageUser = user;
        List<FileDto> files = null;
        try {
            String resp = HttpRequestHelper.get("https://localhost:9000/files/get?userid=" + user.getId());
            System.out.println(resp);
            ObjectMapper mapper = new ObjectMapper();
            CollectionType javaType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, FileDto.class);
            files = mapper.readValue(resp, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<FileDto> fileDtoObservableList = FXCollections.observableArrayList(files);
        //set the contents of the columns
        title.setCellValueFactory(new PropertyValueFactory<FileDto, String>("title"));
        owner.setCellValueFactory(new PropertyValueFactory<FileDto, String>("createdBy"));

        //sets value in column based on class property
        versionNo.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FileDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<FileDto, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getLatestVersion().getVersionNumber().toString());
                } else {
                    return new SimpleStringProperty("<no name>");
                }
            }
        });

        lastModified.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<FileDto, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<FileDto, String> p) {
                if (p.getValue() != null) {
                    return new SimpleStringProperty(p.getValue().getLatestVersion().getModifiedOn().toString());
                } else {
                    return new SimpleStringProperty("<no name>");
                }
            }
        });



        tblFiles.getItems().setAll(fileDtoObservableList);
    }

    public void onFileClicked(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FileView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();

            stage.setTitle("File");
            stage.setScene(new Scene(root1));
            //pass data to the controller
            FileController controller = fxmlLoader.getController();
            FileDto clickedFile = (FileDto) tblFiles.getSelectionModel().getSelectedItem();
            controller.initData(clickedFile);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

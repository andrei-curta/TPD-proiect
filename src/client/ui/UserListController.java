package client.ui;

import DTO.FileDto;
import DTO.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.HttpRequestHelper;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UserListController implements Initializable {
    @FXML
    private ListView lstUsers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<UserDto> users = null;
        try {
            String resp = HttpRequestHelper.get("https://localhost:9000/users/getAll");
            System.out.println(resp);
            ObjectMapper mapper = new ObjectMapper();
            CollectionType javaType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, UserDto.class);
            users = mapper.readValue(resp, javaType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ObservableList<UserDto> userDtoObservableList = FXCollections.observableArrayList(users);
        lstUsers.setItems(userDtoObservableList);
    }

    public void userClicked(MouseEvent event) {
        System.out.println("clicked on " + lstUsers.getSelectionModel().getSelectedItem());

        if (lstUsers.getSelectionModel().getSelectedItem() == null){
            System.out.println("Null selection");
        }
        else {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserFilesView.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setTitle("User files");
                stage.setScene(new Scene(root1));
                //pass data to the controller
                UserFilesController controller = fxmlLoader.getController();
                UserDto clickedUser = (UserDto) lstUsers.getSelectionModel().getSelectedItem();
                controller.initData(clickedUser);

                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

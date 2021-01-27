package client.ui;

import DTO.FileDto;
import DTO.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import util.HttpRequestHelper;

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

    }
}

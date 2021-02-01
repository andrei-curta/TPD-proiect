package client.ui;

import DTO.LoginDto;
import DTO.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.CustomHttpException;
import util.HttpRequestHelper;

import java.io.IOException;

public class LoginController {
    public TextField txtUsername;
    public Button btnSendToken;
    public TextField txtToken;
    public Button btnLogin;


    public void sendToken(ActionEvent actionEvent) {
        if (txtUsername.getText() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not allowed");
            alert.setContentText("Username cannot be empty");
            alert.show();
        } else {
            try {
                String resp = HttpRequestHelper.get("https://localhost:9000/token?username=" + txtUsername.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setContentText(resp);
                alert.show();
            } catch (CustomHttpException customHttpException) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("");
                alert.setContentText(customHttpException.getMessage());
                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void login(ActionEvent actionEvent) {
        if (txtUsername.getText() == null || txtToken.getText() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Not allowed");
            alert.setContentText("Username or token cannot be empty");
            alert.show();
        } else {
            try {
                LoginDto loginDto = new LoginDto();
                loginDto.setUsername(txtUsername.getText());
                loginDto.setToken(txtToken.getText());
                ObjectMapper objectMapper = new ObjectMapper();


                String resp = HttpRequestHelper.post("https://localhost:9000/login", objectMapper.writeValueAsString(loginDto));
                System.out.println(resp);

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserListView.fxml"));
                    Parent root1 = (Parent) fxmlLoader.load();
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.initStyle(StageStyle.UNDECORATED);
                    stage.setTitle("Users");
                    stage.setScene(new Scene(root1));

                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (CustomHttpException customHttpException) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("NoError");
                alert.setContentText(customHttpException.getMessage());
                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

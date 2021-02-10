package client.ui;

import DTO.FileDto;
import DTO.FileVersionDto;
import DTO.UserFilePermissionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import util.Crypto;
import util.CustomHttpException;
import util.HttpRequestHelper;

import javax.crypto.AEADBadTagException;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileController {


    @FXML
    private Button btnDecrypt;
    @FXML
    private PasswordField pwdDecrypt;
    @FXML
    private Label lblTitle;
    @FXML
    private Label lblLastModified;
    @FXML
    private TextArea txtContents;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnDownload;

    @FXML
    private TableView tblPermissions;
    @FXML
    private TableColumn permissionUsername;
    @FXML
    private TableColumn permissionName;

    private FileDto file;
    private boolean successfullyDecrypted = false;
    private List<UserFilePermissionDto> userFilePermissions;

    public void initData(FileDto file) {
        this.file = file;
        if (file != null) {
            try {
                FileVersionDto latestVersion = file.getLatestVersion();

                if (latestVersion != null) {
                    txtContents.setText(latestVersion.getContents());
                    lblLastModified.setText("Version: " + latestVersion.getVersionNumber() + "  |   Last modified by: " + latestVersion.getModifiedBy() + " on: " + latestVersion.getModifiedOn().toString());
                }

                lblTitle.setText(file.getTitle());
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadUserFilePermissions(file.getId());
        }

        setupAccordingToPermissions();
    }

    private void setupAccordingToPermissions() {
        if (!file.getPermissions().contains("write")) {
            txtContents.setEditable(false);
            txtContents.setDisable(true);
            btnSave.setDisable(true);
            btnDownload.setDisable(true);
        }
    }

    private void loadUserFilePermissions(long fileId) {
        try {
            String resp = HttpRequestHelper.get("https://localhost:9000/permissions?fileId=" + fileId);
            System.out.println(resp);
            ObjectMapper mapper = new ObjectMapper();
            CollectionType javaType = mapper.getTypeFactory()
                    .constructCollectionType(List.class, UserFilePermissionDto.class);
            userFilePermissions = mapper.readValue(resp, javaType);
            System.out.println(userFilePermissions);

            ObservableList<UserFilePermissionDto> permissionDtoObservableList = FXCollections.observableArrayList(userFilePermissions);

            permissionUsername.setCellValueFactory(new PropertyValueFactory<UserFilePermissionDto, String>("username"));
            permissionName.setCellValueFactory(new PropertyValueFactory<UserFilePermissionDto, String>("permissionTypeName"));
            tblPermissions.getItems().setAll(permissionDtoObservableList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void save(ActionEvent event) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            if (pwdDecrypt.getText().length() == 0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not allowed");
                alert.setContentText("Password cannot be empty");
                alert.show();
                return;
            }

            if (file.getId() == null || !successfullyDecrypted) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Not allowed");
                alert.setContentText("You must first decrypt the message");
                alert.show();
                return;
            }

            //encrypt content before sending to the server
            System.out.println(txtContents.getText());
            String encryptedContent = Crypto.encrypt(txtContents.getText().getBytes(UTF_8), pwdDecrypt.getText());
            file.getLatestVersion().setContents(encryptedContent);

            String payload = objectMapper.writeValueAsString(file);

            String resp = HttpRequestHelper.post("https://localhost:9000/files/add", payload);
            System.out.println(resp);
        } catch (
                Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void download(ActionEvent event) {
        try {

//            InputStream in = new URL(FILE_URL).openStream();
//            Files.copy(in, Paths.get("E:\\Downloads\\"), StandardCopyOption.REPLACE_EXISTING);

            String resp = HttpRequestHelper.get("https://localhost:9000/file/download?fileVersionId=" + this.file.getLatestVersion().getId());

            FileChooser fileChooser = new FileChooser();

            //Set extension filter for text files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            //Show save file dialog
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                saveTextToFile(resp, file);
            }

        } catch (CustomHttpException ce) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Not allowed");
            alert.setContentText(ce.getMessage());
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void decrypt(ActionEvent event) {
        try {
            try {
                String decryptedContent = Crypto.decrypt(file.getLatestVersion().getContents(), pwdDecrypt.getText());

                file.getLatestVersion().setContents(decryptedContent);
                txtContents.setText(decryptedContent);
                successfullyDecrypted = true;
            } catch (AEADBadTagException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Not allowed");
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

package client.ui;

import DTO.FileDto;
import DTO.FileVersionDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class FileController {
    @FXML
    private Label lblLastModified;
    @FXML
    private TextArea txtContents;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnEdit;

    private FileDto file;

    public void initData(FileDto file) {
        this.file = file;
        if (file != null) {
            try {
                FileVersionDto latestVersion = file.getLatestVersion();

                if (latestVersion != null) {
                    txtContents.setText(latestVersion.getContents());
                    lblLastModified.setText("Last modified by: " + latestVersion.getModifiedBy() + " on: " + latestVersion.getModifiedOn().toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        txtContents.setEditable(false);
    }
}

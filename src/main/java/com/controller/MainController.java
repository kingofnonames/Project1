package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MainController {
    @FXML
    private ImageView imageView;
    @FXML
    public void open(ActionEvent actionEvent) throws IOException {
        Stage stage=(Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            openImageInNewWindow(file,stage);
        }

    }

    private void openImageInNewWindow(File file,Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/openphotos.fxml"));
        Scene sceneOpen = new Scene(fxmlLoader.load(),600,600   );
        OpenImageController imageController=fxmlLoader.getController();
        imageController.setImageView(file);
        stage.setScene(sceneOpen);
        stage.setTitle("Image Viewer");

        stage.show();
    }

    public void take(ActionEvent actionEvent) {
    }

    public void filter(ActionEvent actionEvent) {
    }
}

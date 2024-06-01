package com.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class CameraAndTakePhotos extends Application {
    private VideoCapture capture;
    private Mat image;
    private boolean clicked = false;
    private ImageView cameraScreen;

    @Override
    public void start(Stage primaryStage) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        cameraScreen = new ImageView();
        Button btnCapture = new Button("Capture");

        StackPane root = new StackPane();
        root.getChildren().addAll(cameraScreen, btnCapture);

        Scene scene = new Scene(root, 640, 520);

        primaryStage.setTitle("Camera");
        primaryStage.setScene(scene);
        primaryStage.show();

        capture = new VideoCapture(0);
        image = new Mat();
        btnCapture.setOnAction(e -> clicked = true);

        new Thread(() -> startCamera()).start();
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            System.exit(0);
        });
    }

    public void startCamera() {
        byte[] imageData;

        while (true) {
            capture.read(image);
            MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, buf);
            imageData = buf.toArray();
            Image fxImage = new Image(new ByteArrayInputStream(imageData));
            Platform.runLater(() -> cameraScreen.setImage(fxImage));

            if (clicked) {
                clicked = false; // Reset the flag before showing the dialog
                Platform.runLater(this::showDialog);
            }

            try {
                Thread.sleep(33); // Delay for approximately 30 frames per second
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Image Name");
        dialog.setHeaderText("Enter image name:");
        dialog.setContentText("Name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String name = result.get();
            if (name.isEmpty()) {
                name = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
            }
            Imgcodecs.imwrite("images/" + name + ".jpg", image);
        } else {
            System.out.println("Dialog was cancelled.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

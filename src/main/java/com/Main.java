package com;

import com.controller.OpenImageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class Main extends Application {
    private VideoCapture capture;
    private Mat image;
    private boolean clickedCapture = false;
    private boolean clickedOpen = false;
    private boolean clickedFilter = false;
    private ImageView cameraScreen;
    private Button btnCapture;
    private Button btnOpen;
    private Button btnFilter;
    private volatile boolean running = true; // Flag to control the loop

    @Override
    public void start(Stage primaryStage) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        cameraScreen = new ImageView();
        btnCapture = new Button("Capture");
        btnOpen = new Button("Open");
        btnFilter = new Button("Filter");

        AnchorPane root = new AnchorPane();
        AnchorPane.setTopAnchor(cameraScreen, 0.0);
        AnchorPane.setLeftAnchor(cameraScreen, 0.0);
        AnchorPane.setRightAnchor(cameraScreen, 0.0);
        AnchorPane.setBottomAnchor(btnCapture, 10.0);
        AnchorPane.setRightAnchor(btnCapture, 30.0);
        AnchorPane.setBottomAnchor(btnOpen, 10.0);
        AnchorPane.setLeftAnchor(btnOpen, 30.0);
        AnchorPane.setBottomAnchor(btnFilter, 10.0);
        AnchorPane.setLeftAnchor(btnFilter, 270.0);

        root.getChildren().addAll(cameraScreen, btnCapture, btnOpen, btnFilter);

        Scene scene = new Scene(root, 600, 600);

        primaryStage.setTitle("Camera");
        primaryStage.setScene(scene);
        primaryStage.show();

        capture = new VideoCapture(0);
        // Set the desired width and height of the camera capture
        capture.set(3, 640); // CV_CAP_PROP_FRAME_WIDTH
        capture.set(4, 480); // CV_CAP_PROP_FRAME_HEIGHT
        image = new Mat();
        btnCapture.setOnAction(e -> clickedCapture = true);
        btnOpen.setOnAction(e -> {
            try {
                open(primaryStage);
                capture.release();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        btnFilter.setOnAction(e -> clickedFilter = true);



        new Thread(this::startCamera).start();
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            running = false; // Stop the loop
            capture.release(); // Release the camera
        });
    }

    public void startCamera() {
        byte[] imageData;

        while (running) {
            if (!capture.read(image)) {
                continue; // Continue if the frame is not read successfully
            }
            MatOfByte buf = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, buf);
            imageData = buf.toArray();
            Image fxImage = new Image(new ByteArrayInputStream(imageData));
            Platform.runLater(() -> cameraScreen.setImage(fxImage));

            if (clickedCapture) {
                clickedCapture = false; // Reset the flag before showing the dialog
                Platform.runLater(this::showCaptureDialog);
            }
            if (clickedOpen) {
                clickedOpen = false; // Reset the flag before showing the dialog
                Platform.runLater(this::showOpenDialog);
            }
            if (clickedFilter) {
                clickedFilter = false; // Reset the flag before showing the dialog
                Platform.runLater(this::showFilterDialog);
            }

            try {
                Thread.sleep(33); // Delay for approximately 30 frames per second
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showCaptureDialog() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Capture Image");
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
            System.out.println("Capture dialog was cancelled.");
        }
    }

    private void showOpenDialog() {
        // Implement open dialog functionality here
        System.out.println("Open button clicked.");
    }

    private void showFilterDialog() {
        // Implement filter dialog functionality here
        System.out.println("Filter button clicked.");
    }

    @Override
    public void stop() throws Exception {
        running = false; // Stop the loop
        if (capture != null && capture.isOpened()) {
            capture.release(); // Release the camera
        }
        super.stop();
    }

    public void open(Stage stage) throws IOException {
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

    public static void main(String[] args) {
        launch(args);

    }
}

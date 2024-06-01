package com.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.File;
import java.io.IOException;

public class OpenImageController {
    @FXML
    private ImageView imageView;
    @FXML
    private AnchorPane anchorPane;

    private File file1;
    private File file2=new File("C:/Users/quoca/Desktop/PersonalProject1/images/output.png");
    private double initialScaleX;
    private double initialScaleY;
    private double scale = 1.0;
    CascadeClassifier faceCascade = new CascadeClassifier();
    public void setImageView(File file) {
        if (file != null) {
            file1=file;
            System.out.println(file1.toString());
            String imagePath = file1.toURI().toString();
            Image image = new Image(imagePath);


            imageView.setImage(image);

        }
        initialScaleX = imageView.getScaleX();
        initialScaleY = imageView.getScaleY();
        faceCascade.load("data/haarcascade_frontalface_alt2.xml");

    }

    @FXML
    private void handleZoom(ZoomEvent zoomEvent) {
        System.out.println("Zoom event: " + zoomEvent);
        double zoomFactor = zoomEvent.getTotalZoomFactor();
        imageView.setScaleX(imageView.getScaleX() * zoomFactor);
        imageView.setScaleY(imageView.getScaleY() * zoomFactor);
        zoomEvent.consume();

    }

    public void handleSaveButtonAction(ActionEvent actionEvent) {
    }

    public void handleDetectButtonAction(ActionEvent actionEvent) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image = Imgcodecs.imread(file1.toString());
        detectAndSave(image);
        imageView.setImage(new Image(file2.toURI().toString()));

    }

    public void handleFilterButtonAction(ActionEvent actionEvent) {
    }

    public void handleBackButtonAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage)((Node) actionEvent.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/takephotos.fxml"));
        Scene scene = new Scene(loader.load(),600,600);
        stage.setScene(scene);
    }

    public void handleZoomInButtonAction(ActionEvent actionEvent) {
        double scaleFactor = 1.1;

        if(imageView!=null){
            imageView.setScaleX(imageView.getScaleX() * scaleFactor);
            imageView.setScaleY(imageView.getScaleY() * scaleFactor);
        }
    }

    public void handleZoomOutButtonAction(ActionEvent actionEvent) {
        double scaleFactor = 1.1;

        if(imageView!=null){
            imageView.setScaleX(imageView.getScaleX() / scaleFactor);
            imageView.setScaleY(imageView.getScaleY() / scaleFactor);

        }
    }

    public void handleScroll(ScrollEvent scrollEvent) {
        double deltaY = scrollEvent.getDeltaY();
        // Thay đổi kích thước ảnh dựa trên deltaY
        double scaleFactor = 1.1;
        if (deltaY < 0) {
            // Zoom out
            imageView.setScaleX(imageView.getScaleX() / scaleFactor);
            imageView.setScaleY(imageView.getScaleY() / scaleFactor);
        } else {
            // Zoom in
            imageView.setScaleX(imageView.getScaleX() * scaleFactor);
            imageView.setScaleY(imageView.getScaleY() * scaleFactor);
        }
        scrollEvent.consume();
    }

    public void handleRotateButtonAction(ActionEvent actionEvent) {
        imageView.setRotate(imageView.getRotate() + 90);
    }

    public void handleZoomOneOne(ActionEvent actionEvent) {
        if(imageView!=null){
            imageView.setScaleX(1);
            imageView.setScaleY(1);

        }
    }
    public void detectAndSave(Mat image) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(image, grayFrame, Imgproc.COLOR_RGB2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);
        int height = grayFrame.height();
        int width = grayFrame.width();
        int absoluteFaceSize=0;
        if(Math.round(height*0.2f)>0){
            absoluteFaceSize=Math.round(height*0.2f);

        }

        faceCascade.detectMultiScale(grayFrame, faces,1.1,2,0| Objdetect.CASCADE_SCALE_IMAGE,
                new Size(absoluteFaceSize,absoluteFaceSize),new Size());
        Rect[] faceArray=faces.toArray();
        for(int i=0;i<faceArray.length;i++){
            Imgproc.rectangle(image,faceArray[i], new Scalar(0,0,255),3);
        }
        Imgcodecs.imwrite("images/output.png",image);
        //imageView.setImage(new Image("images/output.jpg"));
    }
}

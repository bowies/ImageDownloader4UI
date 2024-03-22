package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    private TextArea urlTextArea;
    private TextField folderPathTextField;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Image Downloader");

        // 폴더 선택 버튼
        Button selectFolderBtn = new Button("Select Folder");
        DirectoryChooser directoryChooser = new DirectoryChooser();

        // 폴더 선택 이벤트 처리
        selectFolderBtn.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                String selectedFolderPath = selectedDirectory.getAbsolutePath();
                // 선택한 폴더 경로를 화면에 표시
                folderPathTextField.setText(selectedFolderPath);
            }
        });

        // 입력 필드 및 라벨
        urlTextArea = new TextArea();
        folderPathTextField = new TextField();
        Label urlLabel = new Label("Enter URL:");
        Label folderPathLabel = new Label("Selected Folder Path:");

        // 이미지 다운로드 버튼
        Button downloadBtn = new Button("Download Images");

        // 이미지 다운로드 이벤트 처리
        downloadBtn.setOnAction(e -> {
            String url = urlTextArea.getText();
            String folderPath = folderPathTextField.getText();

            // 이미지 다운로드를 백그라운드 스레드로 실행
            Task<Void> downloadTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    // 다운로드 중임을 사용자에게 알림
                    Platform.runLater(() -> statusLabel.setText("Downloading..."));

                    // 이미지 다운로드
                    ImageDownloaderThread.insertUrls(url, folderPath);

                    return null;
                }
            };

            // 작업이 완료되면 상태 라벨을 업데이트
            downloadTask.setOnSucceeded(event -> {
                // 다운로드 완료임을 사용자에게 알림
                Platform.runLater(() -> statusLabel.setText("Download Complete!"));
            });

            // 작업 시작
            new Thread(downloadTask).start();
        });

        // 상태 라벨
        statusLabel = new Label("");

        // 레이아웃 설정
        VBox vbox = new VBox();
        vbox.getChildren().addAll(urlLabel, urlTextArea, selectFolderBtn, folderPathLabel, folderPathTextField, downloadBtn, statusLabel);
        vbox.setSpacing(10);

        // 씬 설정
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

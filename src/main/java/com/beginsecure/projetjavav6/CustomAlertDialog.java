package com.beginsecure.projetjavav6;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CustomAlertDialog {

    public static void show(String title, String message, String type) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle(title);

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true); // Permet le retour à la ligne pour les longs messages
        messageLabel.setMaxWidth(300); // Largeur maximale pour le message

        // Style du label en fonction du type de message
        if ("error".equalsIgnoreCase(type)) {
            messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if ("information".equalsIgnoreCase(type)) {
            messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        } else {
            messageLabel.setStyle("-fx-text-fill: black;");
        }


        Button okButton = new Button("OK");
        okButton.setOnAction(e -> dialogStage.close());

        VBox vbox = new VBox(15); // Espacement entre les éléments
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(messageLabel, okButton);
        vbox.setStyle("-fx-padding: 20; -fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 2; -fx-border-radius: 5;");

        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }

    public static void showError(String title, String message) {
        show(title, message, "error");
    }

    public static void showInformation(String title, String message) {
        show(title, message, "information");
    }
}

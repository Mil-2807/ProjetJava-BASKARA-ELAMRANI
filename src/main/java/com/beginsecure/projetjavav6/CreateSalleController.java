package com.beginsecure.projetjavav6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateSalleController {

    @FXML
    private TextField roomNumberField;
    @FXML
    private TextField capacityField;
    @FXML
    private TextField locationField;
    @FXML
    private Label errorMessageLabel;

    @FXML
    private void handleCreateSalle(ActionEvent event) {
        String roomNumber = roomNumberField.getText();
        String capacityStr = capacityField.getText();
        String location = locationField.getText();

        // Validation des champs
        if (roomNumber.isEmpty() || capacityStr.isEmpty() || location.isEmpty()) {
            errorMessageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityStr);
            if (capacity <= 0) {
                errorMessageLabel.setText("La capacité doit être un nombre positif.");
                return;
            }
        } catch (NumberFormatException e) {
            errorMessageLabel.setText("La capacité doit être un nombre entier valide.");
            return;
        }

        // Vérifier si la salle existe déjà
        boolean roomExists = Administrateur.getToutesLesSalles().stream()
                .anyMatch(s -> s.getNumSalle().equalsIgnoreCase(roomNumber));
        if (roomExists) {
            errorMessageLabel.setText("Une salle avec ce numéro existe déjà.");
            return;
        }

        // Créer la nouvelle salle
        Salle nouvelleSalle = new Salle(roomNumber, capacity, location);
        Administrateur.ajouterSalle(nouvelleSalle); // Ajouter la salle à la liste globale

        CustomAlertDialog.showInformation("Succès", "Salle '" + roomNumber + "' créée avec succès.");
        closeForm();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) roomNumberField.getScene().getWindow();
        stage.close();
    }
}

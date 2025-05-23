package com.beginsecure.projetjavav6;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddEquipementToSalleController {

    @FXML
    private ComboBox<Salle> roomComboBox;
    @FXML
    private TextField equipmentNameField;
    @FXML
    private TextField equipmentDescriptionField;
    @FXML
    private Label errorMessageLabel;

    @FXML
    public void initialize() {
        // Peupler la ComboBox des salles
        ObservableList<Salle> salles = FXCollections.observableArrayList(Administrateur.getToutesLesSalles());
        roomComboBox.setItems(salles);
        roomComboBox.setConverter(new javafx.util.StringConverter<Salle>() {
            @Override
            public String toString(Salle salle) {
                return salle != null ? salle.getNumSalle() + " (Capacité: " + salle.getCapacite() + ")" : "";
            }

            @Override
            public Salle fromString(String string) {
                return null;
            }
        });
    }

    @FXML
    private void handleAddEquipment(ActionEvent event) {
        Salle selectedRoom = roomComboBox.getSelectionModel().getSelectedItem();
        String equipmentName = equipmentNameField.getText();
        String equipmentDescription = equipmentDescriptionField.getText();

        // Validation des champs
        if (selectedRoom == null || equipmentName.isEmpty() || equipmentDescription.isEmpty()) {
            errorMessageLabel.setText("Veuillez sélectionner une salle et remplir tous les champs de l'équipement.");
            return;
        }

        // Créer le nouvel équipement
        Equipement nouvelEquipement = new Equipement(equipmentName, equipmentDescription);

        // Ajouter l'équipement à la salle sélectionnée
        selectedRoom.ajouterEquipement(nouvelEquipement);

        CustomAlertDialog.showInformation("Succès", "Équipement '" + equipmentName + "' ajouté à la salle " + selectedRoom.getNumSalle() + ".");
        closeForm();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) roomComboBox.getScene().getWindow();
        stage.close();
    }
}

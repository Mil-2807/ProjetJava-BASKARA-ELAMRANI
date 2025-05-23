package com.beginsecure.projetjavav6;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.time.LocalDate;

public class SendNotificationController {

    @FXML
    private TextField recipientIdField;
    @FXML
    private TextArea messageArea;
    @FXML
    private Label errorMessageLabel;

    private Utilisateur sender; // L'utilisateur qui envoie la notification

    /**
     * Définit l'utilisateur qui envoie la notification.
     * @param sender L'instance de l'utilisateur (Administrateur, Enseignant, ou Étudiant) qui envoie.
     */
    public void setSender(Utilisateur sender) {
        this.sender = sender;
    }

    @FXML
    private void handleSendMessage(ActionEvent event) {
        String recipientId = recipientIdField.getText().trim();
        String messageContent = messageArea.getText().trim();
        String dateEnvoi = LocalDate.now().toString(); // Date actuelle

        if (recipientId.isEmpty() || messageContent.isEmpty()) {
            errorMessageLabel.setText("Veuillez entrer l'ID du destinataire et le message.");
            return;
        }

        if (sender == null) {
            CustomAlertDialog.showError("Erreur d'envoi", "L'expéditeur n'est pas défini.");
            return;
        }

        // Trouver le destinataire
        Utilisateur destinataire = findUserById(recipientId);

        if (destinataire == null) {
            errorMessageLabel.setText("Destinataire avec l'ID '" + recipientId + "' introuvable.");
            return;
        }

        // Créer et envoyer la notification
        Notification notification = new Notification(messageContent, dateEnvoi, destinataire);
        notification.envoyer(); // Cette méthode affichera la boîte de dialogue

        CustomAlertDialog.showInformation("Notification Envoyée", "Message envoyé à " + destinataire.getPrenom() + " " + destinataire.getNom() + ".");
        closeForm();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeForm();
    }

    private void closeForm() {
        Stage stage = (Stage) recipientIdField.getScene().getWindow();
        stage.close();
    }

    /**
     * Recherche un utilisateur (Admin, Enseignant, Étudiant) par son ID unique.
     * @param id L'ID de l'utilisateur à rechercher.
     * @return L'objet Utilisateur trouvé, ou null si non trouvé.
     */
    private Utilisateur findUserById(String id) {
        // Rechercher parmi les administrateurs
        for (Administrateur admin : Administrateur.getAdministrateursGlobaux()) {
            if (admin.getNumAdmin().equalsIgnoreCase(id)) {
                return admin;
            }
        }
        // Rechercher parmi les enseignants
        for (Enseignant enseignant : Administrateur.getEnseignantsGlobaux()) {
            if (enseignant.getNumEnseignant().equalsIgnoreCase(id)) {
                return enseignant;
            }
        }
        // Rechercher parmi les étudiants
        for (Etudiant etudiant : Administrateur.getEtudiantsGlobaux()) {
            if (etudiant.getNumEtudiant().equalsIgnoreCase(id)) {
                return etudiant;
            }
        }
        return null;
    }
}


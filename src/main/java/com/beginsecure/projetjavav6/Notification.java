package com.beginsecure.projetjavav6;

public class Notification {
    private String message;
    private String dateEnvoi;
    private Utilisateur destinataire;

    public Notification(String message, String dateEnvoi, Utilisateur destinataire) {
        this.message = message;
        this.dateEnvoi = dateEnvoi;
        this.destinataire = destinataire;
    }

    public void envoyer() {
        // Construction du message complet pour le log et l'affichage UI
        String fullMessage = "--- NOTIFICATION envoyée le " + dateEnvoi + " --- " +
                "À: " + destinataire.getNom() + " " + destinataire.getPrenom() + " (" + destinataire.getRole() + ") " +
                "Message: " + message;

        System.out.println(fullMessage); // Garde l'affichage console pour le débogage

        CustomAlertDialog.showInformation("Nouvelle Notification pour " + destinataire.getRole(),
                "De: Système\nDate: " + dateEnvoi + "\nMessage: " + message);

        // NOUVEAU: Ajoute la notification au gestionnaire centralisé
        NotificationManager.addNotification(fullMessage);

        // NOUVEAU: Passe le message à la méthode recevoirNotification du destinataire
        destinataire.recevoirNotification(fullMessage);
    }

    // Getters
    public String getMessage() {
        return message;
    }

    public String getDateEnvoi() {
        return dateEnvoi;
    }

    public Utilisateur getDestinataire() {
        return destinataire;
    }

    // Setters (si nécessaire, non modifiés ici)
    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateEnvoi(String dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }

    public void setDestinataire(Utilisateur destinataire) {
        this.destinataire = destinataire;
    }

    @Override
    public String toString() {
        return "Notification [message=" + message + ", dateEnvoi=" + dateEnvoi + ", destinataire=" + destinataire.getNom() + "]";
    }
}

package com.beginsecure.projetjavav6;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityFinderController {

    @FXML
    private ComboBox<Salle> roomComboBox;
    @FXML
    private TextField durationField;
    @FXML
    private TextField startDateField;
    @FXML
    private TextField endDateField;
    @FXML
    private TextField startTimeRangeField;
    @FXML
    private TextField endTimeRangeField;
    @FXML
    private VBox resultsArea;
    @FXML
    private Label errorMessageLabel;

    /**
     * Initialise le contrôleur après que tous les éléments FXML ont été chargés.
     * Peuple la ComboBox des salles.
     */
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
            public Salle fromString(String string) { return null; }
        });
    }

    /**
     * Gère l'action de recherche de disponibilités.
     * Valide les entrées, itère sur les créneaux possibles et affiche les résultats.
     * @param event L'événement d'action.
     */
    @FXML
    private void handleSearchAvailability(ActionEvent event) {
        resultsArea.getChildren().clear(); // Effacer les résultats précédents
        errorMessageLabel.setText(""); // Effacer les messages d'erreur précédents

        Salle selectedRoom = roomComboBox.getSelectionModel().getSelectedItem();
        String durationStr = durationField.getText();
        String startDateStr = startDateField.getText();
        String endDateStr = endDateField.getText();
        String startTimeRangeStr = startTimeRangeField.getText();
        String endTimeRangeStr = endTimeRangeField.getText();

        // 1. Validation des champs obligatoires
        if (selectedRoom == null || durationStr.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty() || startTimeRangeStr.isEmpty() || endTimeRangeStr.isEmpty()) {
            errorMessageLabel.setText("Veuillez remplir tous les champs.");
            return;
        }

        // 2. Validation et conversion des formats
        LocalDate startDate, endDate;
        LocalTime startTimeRange, endTimeRange;
        long durationMinutes;

        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
            if (startDate.isAfter(endDate)) {
                errorMessageLabel.setText("La date de début ne peut pas être après la date de fin.");
                return;
            }
        } catch (DateTimeParseException e) {
            errorMessageLabel.setText("Format de date invalide. Utilisez AAAA-MM-JJ.");
            return;
        }

        try {
            startTimeRange = LocalTime.parse(startTimeRangeStr);
            endTimeRange = LocalTime.parse(endTimeRangeStr);
            if (startTimeRange.isAfter(endTimeRange) || startTimeRange.equals(endTimeRange)) {
                errorMessageLabel.setText("L'heure de début ne peut pas être après ou égale à l'heure de fin.");
                return;
            }
        } catch (DateTimeParseException e) {
            errorMessageLabel.setText("Format d'heure invalide. Utilisez HH:MM.");
            return;
        }

        try {
            durationMinutes = parseDurationToMinutes(durationStr);
            if (durationMinutes <= 0) {
                errorMessageLabel.setText("La durée doit être positive (ex: 2h, 1h30m).");
                return;
            }
        } catch (IllegalArgumentException e) {
            errorMessageLabel.setText(e.getMessage());
            return;
        }

        // 3. Recherche des créneaux disponibles
        List<String> availableSlots = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            LocalTime currentTime = startTimeRange;
            while (currentTime.plusMinutes(durationMinutes).isBefore(endTimeRange) || currentTime.plusMinutes(durationMinutes).equals(endTimeRange)) {
                String proposedStartTime = currentTime.toString();
                String proposedEndTime = currentTime.plusMinutes(durationMinutes).toString();

                if (selectedRoom.verifierDisponibilite(currentDate.toString(), proposedStartTime, proposedEndTime)) {
                    availableSlots.add(currentDate.toString() + " de " + proposedStartTime + " à " + proposedEndTime);
                }
                currentTime = currentTime.plusMinutes(30); // Vérifier toutes les 30 minutes
            }
            currentDate = currentDate.plusDays(1);
        }

        // 4. Affichage des résultats
        if (availableSlots.isEmpty()) {
            resultsArea.getChildren().add(new Label("Aucun créneau disponible trouvé pour les critères spécifiés."));
        } else {
            for (String slot : availableSlots) {
                resultsArea.getChildren().add(new Label("Disponible: " + slot));
            }
            CustomAlertDialog.showInformation("Recherche terminée", availableSlots.size() + " créneau(x) disponible(s) trouvé(s).");
        }
    }

    /**
     * Convertit une chaîne de durée (ex: "2h", "1h30m") en minutes.
     * @param durationString La chaîne de durée.
     * @return La durée totale en minutes.
     * @throws IllegalArgumentException Si le format de la durée est invalide.
     */
    private long parseDurationToMinutes(String durationString) {
        long totalMinutes = 0;
        String lowerCaseDuration = durationString.toLowerCase().trim();

        if (lowerCaseDuration.contains("h")) {
            String[] parts = lowerCaseDuration.split("h");
            try {
                totalMinutes += Integer.parseInt(parts[0].trim()) * 60;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Format de durée invalide (heures): " + durationString);
            }
            if (parts.length > 1 && !parts[1].isEmpty()) {
                if (parts[1].contains("m")) {
                    try {
                        totalMinutes += Integer.parseInt(parts[1].replace("m", "").trim());
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Format de durée invalide (minutes après heures): " + durationString);
                    }
                } else {
                    throw new IllegalArgumentException("Format de durée invalide (minutes sans 'm'): " + durationString);
                }
            }
        } else if (lowerCaseDuration.contains("m")) {
            try {
                totalMinutes += Integer.parseInt(lowerCaseDuration.replace("m", "").trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Format de durée invalide (minutes seulement): " + durationString);
            }
        } else {
            throw new IllegalArgumentException("Format de durée invalide. Utilisez 'XhYm', 'Xh' ou 'Ym' (ex: 2h, 1h30m, 90m).");
        }
        return totalMinutes;
    }

    /**
     * Gère l'action d'annulation et ferme le formulaire.
     * @param event L'événement d'action.
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        Stage stage = (Stage) roomComboBox.getScene().getWindow();
        stage.close();
    }
}

package com.beginsecure.projetjavav6;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML
    private TextField tfNomPrenom;
    @FXML
    private TextField tfTelephone;
    @FXML
    private TextField tfEmail;
    @FXML
    private PasswordField tfPassword;
    @FXML
    private PasswordField tfConfirmPassword;
    @FXML
    private Button btInscription;
    @FXML
    private Button btClear;
    @FXML
    private Label errorMessageLabel;

    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label formationLabel;
    @FXML
    private TextField formationField;
    @FXML
    private Label specialtyLabel;
    @FXML
    private TextField specialtyField;

    // Méthode d'initialisation appelée après le chargement du FXML
    @FXML
    public void initialize() {
        // Peupler le ComboBox des rôles, incluant "Administrateur"
        ObservableList<String> roles = FXCollections.observableArrayList("Étudiant", "Enseignant", "Administrateur");
        roleComboBox.setItems(roles);
        roleComboBox.getSelectionModel().selectFirst(); // Sélectionner "Étudiant" par défaut

        // Configurer les écouteurs pour changer la visibilité des champs spécifiques au rôle
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            toggleRoleSpecificFields(newVal);
        });

        // Initialiser la visibilité des champs au démarrage
        toggleRoleSpecificFields(roleComboBox.getSelectionModel().getSelectedItem());
    }

    /**
     * Gère la visibilité des champs spécifiques au rôle (formation ou spécialité).
     * @param selectedRole Le rôle sélectionné.
     */
    private void toggleRoleSpecificFields(String selectedRole) {
        boolean isEtudiant = "Étudiant".equals(selectedRole);
        boolean isEnseignant = "Enseignant".equals(selectedRole);

        // Les champs de formation sont visibles uniquement pour les étudiants
        formationLabel.setVisible(isEtudiant);
        formationField.setVisible(isEtudiant);
        formationField.setManaged(isEtudiant);

        // Les champs de spécialité sont visibles uniquement pour les enseignants
        specialtyLabel.setVisible(isEnseignant);
        specialtyField.setVisible(isEnseignant);
        specialtyField.setManaged(isEnseignant);

        // Effacer les messages d'erreur si l'utilisateur change de rôle
        errorMessageLabel.setText("");
    }

    @FXML
    private void handleInscrireButtonAction(ActionEvent event) {
        String nomPrenom = tfNomPrenom.getText().trim();
        String telephone = tfTelephone.getText().trim();
        String email = tfEmail.getText().trim();
        String password = tfPassword.getText();
        String confirmPassword = tfConfirmPassword.getText();
        String selectedRole = roleComboBox.getSelectionModel().getSelectedItem();

        // 1. Validation des champs communs
        if (nomPrenom.isEmpty() || telephone.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || selectedRole == null) {
            errorMessageLabel.setText("Veuillez remplir tous les champs et sélectionner un rôle.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessageLabel.setText("Les mots de passe ne correspondent pas.");
            return;
        }

        if (password.length() < 6) {
            errorMessageLabel.setText("Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }

        // Validation simple du format d'email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            errorMessageLabel.setText("Le format de l'adresse e-mail est invalide.");
            return;
        }

        // 2. Traitement du Nom et Prénom
        String nom;
        String prenom;
        int firstSpace = nomPrenom.indexOf(' ');
        if (firstSpace > 0) {
            prenom = nomPrenom.substring(0, firstSpace);
            nom = nomPrenom.substring(firstSpace + 1);
        } else {
            prenom = nomPrenom;
            nom = ""; // Gérer le cas où seul le prénom est entré
        }

        // 3. Logique spécifique au rôle
        if ("Étudiant".equals(selectedRole)) {
            String formation = formationField.getText().trim();
            if (formation.isEmpty()) {
                errorMessageLabel.setText("Veuillez entrer votre formation.");
                return;
            }

            // Vérifier si l'email existe déjà pour un étudiant
            boolean emailExists = Administrateur.getEtudiantsGlobaux().stream()
                    .anyMatch(etu -> etu.getEmail().equalsIgnoreCase(email));
            if (emailExists) {
                errorMessageLabel.setText("Cette adresse e-mail est déjà utilisée par un autre étudiant.");
                return;
            }

            String newNumEtudiant = generateUniqueEtudiantId();
            Etudiant nouvelEtudiant = new Etudiant(newNumEtudiant, formation, nom, prenom, email, telephone, password);
            Administrateur.ajouterEtudiantGlobal(nouvelEtudiant);
            CustomAlertDialog.showInformation("Inscription réussie", "Votre compte étudiant a été créé avec succès.\nVotre numéro d'étudiant est: " + newNumEtudiant);

        } else if ("Enseignant".equals(selectedRole)) {
            String specialty = specialtyField.getText().trim();
            if (specialty.isEmpty()) {
                errorMessageLabel.setText("Veuillez entrer votre spécialité.");
                return;
            }

            // Vérifier si l'email existe déjà pour un enseignant
            boolean emailExists = Administrateur.getEnseignantsGlobaux().stream()
                    .anyMatch(ens -> ens.getEmail().equalsIgnoreCase(email));
            if (emailExists) {
                errorMessageLabel.setText("Cette adresse e-mail est déjà utilisée par un autre enseignant.");
                return;
            }

            String newNumEnseignant = generateUniqueEnseignantId();
            Enseignant nouvelEnseignant = new Enseignant(newNumEnseignant, specialty, nom, prenom, email, telephone, password);
            Administrateur.ajouterEnseignantGlobal(nouvelEnseignant);
            CustomAlertDialog.showInformation("Inscription réussie", "Votre compte enseignant a été créé avec succès.\nVotre numéro d'enseignant est: " + newNumEnseignant);
        } else if ("Administrateur".equals(selectedRole)) {
            // Vérifier si l'email existe déjà pour un administrateur
            boolean emailExists = Administrateur.getAdministrateursGlobaux().stream()
                    .anyMatch(adm -> adm.getEmail().equalsIgnoreCase(email));
            if (emailExists) {
                errorMessageLabel.setText("Cette adresse e-mail est déjà utilisée par un autre administrateur.");
                return;
            }

            String newNumAdmin = generateUniqueAdminId();
            Administrateur nouvelAdmin = new Administrateur(newNumAdmin, nom, prenom, email, telephone, password);
            // L'ajout à la liste globale des administrateurs est géré par le constructeur de Administrateur
            // mais pour la cohérence avec les autres rôles, on peut appeler une méthode statique si elle existe.
            // Si Administrateur.addAdministrateurGlobal() n'existe pas, le constructeur suffit.
            // Administrateur.addAdministrateurGlobal(nouvelAdmin); // Décommenter si cette méthode est ajoutée
            CustomAlertDialog.showInformation("Inscription réussie", "Votre compte administrateur a été créé avec succès.\nVotre numéro d'administrateur est: " + newNumAdmin);
        }


        // 4. Sauvegarde de tous les utilisateurs (y compris le nouveau) dans le fichier CSV
        // Cette méthode doit être statique et accessible depuis LoginController
        LoginController.saveUsersToCsv();

        // 5. Fermeture du formulaire
        closeForm();
    }

    @FXML
    private void handleClearButtonAction(ActionEvent event) {
        tfNomPrenom.clear();
        tfTelephone.clear();
        tfEmail.clear();
        tfPassword.clear();
        tfConfirmPassword.clear();
        formationField.clear();
        specialtyField.clear();
        roleComboBox.getSelectionModel().selectFirst(); // Réinitialiser la sélection du rôle
        errorMessageLabel.setText("");
    }

    private void closeForm() {
        Stage stage = (Stage) btInscription.getScene().getWindow();
        stage.close();
    }

    /**
     * Génère un numéro d'étudiant unique en se basant sur le plus grand ID existant.
     * Format: ETUxxx
     * @return Le nouveau numéro d'étudiant unique.
     */
    private String generateUniqueEtudiantId() {
        Optional<Integer> maxId = Administrateur.getEtudiantsGlobaux().stream()
                .map(etu -> {
                    String num = etu.getNumEtudiant();
                    if (num != null && num.startsWith("ETU") && num.length() > 3) {
                        try {
                            return Integer.parseInt(num.substring(3));
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                    return 0;
                })
                .max(Integer::compare);

        int nextId = maxId.orElse(0) + 1;
        return String.format("ETU%03d", nextId);
    }

    /**
     * Génère un numéro d'enseignant unique en se basant sur le plus grand ID existant.
     * Format: ENSxxx
     * @return Le nouveau numéro d'enseignant unique.
     */
    private String generateUniqueEnseignantId() {
        Optional<Integer> maxId = Administrateur.getEnseignantsGlobaux().stream()
                .map(ens -> {
                    String num = ens.getNumEnseignant();
                    if (num != null && num.startsWith("ENS") && num.length() > 3) {
                        try {
                            return Integer.parseInt(num.substring(3));
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                    return 0;
                })
                .max(Integer::compare);

        int nextId = maxId.orElse(0) + 1;
        return String.format("ENS%03d", nextId);
    }

    /**
     * Génère un numéro d'administrateur unique en se basant sur le plus grand ID existant.
     * Format: ADMxxx
     * @return Le nouveau numéro d'administrateur unique.
     */
    private String generateUniqueAdminId() {
        Optional<Integer> maxId = Administrateur.getAdministrateursGlobaux().stream()
                .map(adm -> {
                    String num = adm.getNumAdmin();
                    if (num != null && num.startsWith("ADM") && num.length() > 3) {
                        try {
                            return Integer.parseInt(num.substring(3));
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                    return 0;
                })
                .max(Integer::compare);

        int nextId = maxId.orElse(0) + 1;
        return String.format("ADM%03d", nextId);
    }
}

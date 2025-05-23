package com.beginsecure.projetjavav6;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Classe utilitaire pour exporter des données au format CSV.
 */
public class CsvExporter {

    /**
     * Exporte une liste de cours vers un fichier CSV.
     * Le fichier CSV inclura un en-tête.
     *
     * @param courses La liste des objets Cours à exporter.
     * @param filePath Le chemin complet du fichier CSV à créer (ex: "emploidutemps.csv").
     * @return true si l'exportation a réussi, false sinon.
     */
    public static boolean exportCoursesToCsv(List<Cours> courses, String filePath) {
        // En-tête du fichier CSV
        String CSV_HEADER = "Nom du Cours,Durée,Heure de Début,Heure de Fin,Date,Enseignant,Salle,Nombre d'Étudiants Inscrits";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Écrire l'en-tête
            writer.write(CSV_HEADER);
            writer.newLine();

            // Écrire les données de chaque cours
            for (Cours cours : courses) {
                StringBuilder line = new StringBuilder();
                line.append("\"").append(escapeCsv(cours.getNom())).append("\"").append(",");
                line.append("\"").append(escapeCsv(cours.getDuree())).append("\"").append(",");
                line.append("\"").append(escapeCsv(cours.getHeureDebut())).append("\"").append(",");
                line.append("\"").append(escapeCsv(cours.getHeureFin())).append("\"").append(",");
                line.append("\"").append(escapeCsv(cours.getDateCours())).append("\"").append(",");
                line.append("\"").append(escapeCsv(cours.getEnseignant() != null ? cours.getEnseignant().getPrenom() + " " + cours.getEnseignant().getNom() : "N/A")).append("\"").append(",");
                line.append("\"").append(escapeCsv(cours.getSalle() != null ? cours.getSalle().getNumSalle() : "N/A")).append("\"").append(",");
                line.append(cours.getEtudiantsInscrits().size()); // Nombre d'étudiants

                writer.write(line.toString());
                writer.newLine();
            }
            System.out.println("Exportation CSV réussie vers : " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de l'exportation CSV : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Échappe une chaîne de caractères pour le format CSV.
     * Ajoute des guillemets si la chaîne contient une virgule ou des guillemets,
     * et double les guillemets existants.
     * @param value La chaîne à échapper.
     * @return La chaîne échappée.
     */
    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        // Si la valeur contient une virgule, des guillemets ou un saut de ligne, elle doit être entre guillemets
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            // Doubler les guillemets existants
            String escapedValue = value.replace("\"", "\"\"");
            return "\"" + escapedValue + "\"";
        }
        return value;
    }
}

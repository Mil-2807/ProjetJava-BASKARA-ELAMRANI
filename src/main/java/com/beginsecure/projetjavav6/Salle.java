package com.beginsecure.projetjavav6;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// 6. Classe Salle
public class Salle {
    private String numSalle;
    private int capacite;
    private String localisation;
    // Liste pour stocker tous les créneaux occupés, chaque élément est un tableau de String [date, heureDebut, heureFin]
    // Une classe CreneauHoraire dédiée serait plus robuste ici, mais on garde String[] pour l'instant.
    private List<String[]> creneauxOccupes;
    private List<Equipement> equipements; // Liste des équipements dans la salle

    // Constructeur
    public Salle(String numSalle, int capacite, String localisation) {
        this.numSalle = numSalle;
        this.capacite = capacite;
        this.localisation = localisation;
        this.creneauxOccupes = new ArrayList<>(); // Initialise la liste vide
        this.equipements = new ArrayList<>(); // Initialise la liste des équipements vide
    }

    public List<String[]> getCreneauxOccupes() {
        return creneauxOccupes;
    }

    public void setCreneauxOccupes(List<String[]> creneauxOccupes) {
        this.creneauxOccupes = creneauxOccupes;
    }

    // Vérifie si la salle est disponible pour un créneau horaire donné à une date spécifique
    public boolean verifierDisponibilite(String date, String heureDebut, String heureFin) {
        System.out.println("SALLE " + numSalle + ": Vérification dispo pour " + date + " de " + heureDebut + " à " + heureFin);

        try {
            LocalTime debutNouveau = LocalTime.parse(heureDebut);
            LocalTime finNouveau = LocalTime.parse(heureFin);

            // Vérifier si les heures sont valides (début avant fin)
            if (debutNouveau.isAfter(finNouveau) || debutNouveau.equals(finNouveau)) {
                System.out.println("SALLE " + numSalle + ": Erreur: Heures de début/fin invalides (début doit être avant fin).");
                return false;
            }

            // Parcourir tous les créneaux existants pour vérifier les chevauchements
            for (String[] creneauExistant : creneauxOccupes) {
                String dateExistante = creneauExistant[0];
                String debutExistantStr = creneauExistant[1];
                String finExistantStr = creneauExistant[2];

                // Ne vérifier que les créneaux pour la même date
                if (date.equals(dateExistante)) {
                    LocalTime debutExistant = LocalTime.parse(debutExistantStr);
                    LocalTime finExistant = LocalTime.parse(finExistantStr);

                    // Condition de chevauchement: (DébutNouveau < FinExistant) ET (DébutExistant < FinNouveau)
                    if (debutNouveau.isBefore(finExistant) && debutExistant.isBefore(finNouveau)) {
                        System.out.println("SALLE " + numSalle + ": Occupée de " + debutExistantStr + " à " + finExistantStr + " (conflit).");
                        return false; // Conflit trouvé, la salle n'est pas disponible
                    }
                }
            }
            System.out.println("SALLE " + numSalle + ": Disponible!");
            return true; // Aucun conflit, la salle est disponible
        } catch (DateTimeParseException e) {
            System.err.println("Erreur de format d'heure dans Salle.verifierDisponibilite: " + e.getMessage());
            return false;
        }
    }

    // Réserve la salle pour un créneau horaire donné à une date spécifique
    // Il est recommandé d'appeler verifierDisponibilite() avant d'appeler reserver().
    public void reserver(String date, String heureDebut, String heureFin) {
        // Ajoute le nouveau créneau à la liste
        creneauxOccupes.add(new String[]{date, heureDebut, heureFin});
        System.out.println("SALLE " + numSalle + ": Réservée le " + date + " de " + heureDebut + " à " + heureFin + ".");
    }

    // Libère un créneau horaire réservé
    public void liberer(String date, String heureDebut, String heureFin) {
        // Crée un créneau "cible" pour la comparaison
        String[] creneauCible = new String[]{date, heureDebut, heureFin};

        // Utilise la méthode removeIf avec un prédicat pour simplifier la suppression
        // Note: cela nécessite que String[] ait une méthode equals() adéquate ou une comparaison manuelle.
        // Ici, on fait une comparaison manuelle des éléments du tableau.
        boolean removed = creneauxOccupes.removeIf(creneau ->
                creneau[0].equals(creneauCible[0]) &&
                        creneau[1].equals(creneauCible[1]) &&
                        creneau[2].equals(creneauCible[2])
        );

        if (removed) {
            System.out.println("SALLE " + numSalle + ": Créneau " + date + " " + heureDebut + "-" + heureFin + " libéré.");
        } else {
            System.out.println("SALLE " + numSalle + ": Créneau " + date + " " + heureDebut + "-" + heureFin + " non trouvé pour libération.");
        }
    }

    // Ajoute un équipement à la salle
    public void ajouterEquipement(Equipement equipement) {
        if (equipement != null) {
            if (!equipements.contains(equipement)) { // Nécessite Equipement.equals()
                equipements.add(equipement);
                System.out.println("SALLE " + numSalle + ": Ajout de " + equipement.getNom() + ".");
            } else {
                System.out.println("SALLE " + numSalle + ": L'équipement " + equipement.getNom() + " est déjà présent.");
            }
        } else {
            System.out.println("SALLE " + numSalle + ": Impossible d'ajouter un équipement nul.");
        }
    }

    // Retire un équipement de la salle
    public void retirerEquipement(Equipement equipement) {
        if (equipement != null) {
            if (equipements.remove(equipement)) { // Nécessite Equipement.equals()
                System.out.println("SALLE " + numSalle + ": Retrait de " + equipement.getNom() + ".");
            } else {
                System.out.println("SALLE " + numSalle + ": Équipement " + equipement.getNom() + " non trouvé.");
            }
        } else {
            System.out.println("SALLE " + numSalle + ": Équipement à retirer est nul.");
        }
    }

    // Getters
    public String getNumSalle() { return numSalle; }
    public int getCapacite() { return capacite; }
    public String getLocalisation() { return localisation; }
    public List<Equipement> getEquipements() { return new ArrayList<>(equipements); } // Retourne une copie

    // Setters (setter pour numSalle n'est pas fourni car il est l'identifiant unique)
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }

    // Implémentation de la méthode equals pour définir l'égalité entre deux objets Salle.
    // Une salle est unique par son numéro.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Utilisation de getClass() pour s'assurer que les objets sont de la même classe exacte
        if (o == null || getClass() != o.getClass()) return false;
        Salle salle = (Salle) o;
        return Objects.equals(numSalle, salle.numSalle); // Comparaison sécurisée même si numSalle était nul
    }

    // La méthode hashCode() a été supprimée comme demandé.
    // Soyez conscient des implications mentionnées précédemment concernant les collections basées sur le hachage.

    // Représentation textuelle de l'objet Salle
    @Override
    public String toString() {
        // Construction de la liste des équipements pour l'affichage
        String listeEquipements = "";
        if (equipements.isEmpty()) {
            listeEquipements = "Aucun";
        } else {
            List<String> nomsEquipements = new ArrayList<>();
            for (Equipement eq : equipements) {
                nomsEquipements.add(eq.getNom());
            }
            listeEquipements = String.join(", ", nomsEquipements);
        }

        return "Salle: " + numSalle +
                ", Capacité: " + capacite +
                ", Localisation: " + localisation +
                ", Équipements: [" + listeEquipements + "]";
    }
}
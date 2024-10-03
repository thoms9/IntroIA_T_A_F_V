import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.*;
import lejos.robotics.chassis.*;


public class Actionneurs {
    
    // Déclaration d'un MovePilot pour gérer les déplacements du robot
    private MovePilot pilot;
    
    // Moteur pour contrôler les pinces du robot
    private RegulatedMotor moteurPinces;

    // Constructeur qui initialise les moteurs et le pilot
    public Actionneurs() {
        // Initialisation des moteurs des roues (moteurs moyens)
        EV3MediumRegulatedMotor moteurGauche = new EV3MediumRegulatedMotor(MotorPort.C);
        EV3MediumRegulatedMotor moteurDroit = new EV3MediumRegulatedMotor(MotorPort.B);
        
        // Création des roues avec les caractéristiques (diamètre 56 mm et offset pour la position sur le châssis)
        Wheel roueGauche = WheeledChassis.modelWheel(moteurGauche, 56).offset(-75);  // Roue gauche à 75 mm à gauche du centre
        Wheel roueDroite = WheeledChassis.modelWheel(moteurDroit, 56).offset(75);   // Roue droite à 75 mm à droite du centre
        
        // Création du châssis différentiel avec deux roues
        Chassis chassis = new WheeledChassis(new Wheel[] { roueGauche, roueDroite }, WheeledChassis.TYPE_DIFFERENTIAL);
        
        // Initialisation du MovePilot avec le châssis créé
        pilot = new MovePilot(chassis);

        // Initialisation du moteur des pinces (moteur moyen sur le port A)
        moteurPinces = new EV3MediumRegulatedMotor(MotorPort.A);
    }

    // Méthode pour faire avancer le robot sur une distance donnée (en cm)
    public void avancer(double distance) {
        pilot.travel(distance);  // Déplace le robot sur la distance spécifiée
    }

    // Méthode pour faire reculer le robot sur une distance donnée (en cm)
    public void reculer(double distance) {
        pilot.travel(-distance);  // Déplace le robot en arrière (distance négative)
    }

    // Méthode pour tourner le robot d'un angle donné (en degrés)
    public void tourner(double angle) {
        pilot.rotate(angle);  // Tourne le robot de l'angle spécifié (positif pour tourner à droite, négatif pour tourner à gauche)
    }

    // Méthode pour arrêter immédiatement les moteurs du robot
    public void arreter() {
        pilot.stop();  // Stoppe les moteurs et arrête tout mouvement du robot
    }

    // Méthode pour fermer les pinces avec un angle et une vitesse spécifiés
    public void fermerPinces(int angle, int vitesse) {
        moteurPinces.setSpeed(vitesse);  // Définit la vitesse du moteur des pinces
        moteurPinces.rotate(angle);  // Ferme les pinces en les faisant tourner d'un angle spécifié
    }

    // Méthode pour ouvrir les pinces avec un angle et une vitesse spécifiés
    public void ouvrirPinces(int angle, int vitesse) {
        moteurPinces.setSpeed(vitesse);  // Définit la vitesse du moteur des pinces
        moteurPinces.rotate(-angle);  // Ouvre les pinces en les faisant tourner dans le sens inverse de l'angle spécifié
    }

}



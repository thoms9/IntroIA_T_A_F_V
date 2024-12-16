package robot;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.navigation.*;
import lejos.utility.Delay;
import lejos.robotics.chassis.*;

	/**
	 * Classe qui gère l'initialisation et le contrôle des moteurs du robot LEGO EV3.
	 * 
	 * Cette classe permet de contrôler les moteurs pour effectuer des déplacements
	 * (avancer, reculer, tourner) ainsi que manipuler des objets via des pinces.
	 * 
	 * Relations avec d'autres modules :
	 * - Cette classe dépend des informations des capteurs du module Sensors pour
	 * ajuster les comportements du robot.
	 * 
	 * Modules utilisés par ce module :
	 * - LeJOS (pour les moteurs comme EV3MediumRegulatedMotor et MovePilot).
	 * 
	 * Modules utilisant ce module :
	 * - Main : Utilise les méthodes d'Actionneurs pour effectuer des actions 
	 * spécifiques sur le robot.
	 * 
	 * Définitions de types :
	 * - `EV3MediumRegulatedMotor` : Utilisé pour contrôler les moteurs de déplacement
	 * et des pinces.
	 * - `MovePilot` : Utilisé pour effectuer des déplacements globaux du robot
	 * (avancer, reculer, tourner).
	 * - `Wheel`, `Chassis` : Utilisés pour configurer les roues et le châssis du robot.
	 * 
	 * Procédures externes :
	 * - `setLinearSpeed(double)` : Définit la vitesse linéaire du robot en mm/s.
	 * - `setRotationSpeed(double)` : Définit la vitesse de rotation du robot en degrés/s.
	 * - `avancer(double, boolean)` : Fait avancer le robot sur une distance donnée.
	 * - `reculer(double, boolean)` : Fait reculer le robot sur une distance donnée.
	 * - `tourner(double, boolean)` : Fait tourner le robot d'un angle donné
	 * (positif pour sens horaire).
	 * - `arreter()` : Arrête immédiatement tous les moteurs du robot.
	 * - `ouvrirPinces(int, boolean)` : Ouvre les pinces avec une vitesse spécifiée 
	 * pendant 2.8 secondes.
	 * - `fermerPinces(int, boolean)` : Ferme les pinces avec une vitesse spécifiée 
	 * pendant 2.8 secondes.
	 * - `isMoving()` : Indique si le robot est en mouvement.
	 * 
	 * Variables externes :
	 * - Aucune variable publique, les moteurs sont gérés en interne.
	 * 
	 * @author Alexander OSTLE, Thomas BEGOTTI, Victor CHARREYRON
	 */
	public class Actionneurs {
    
    private MovePilot pilot;    
    private EV3MediumRegulatedMotor moteurPinces;

    /**
     *  Constructeur qui initialise les moteurs et le pilot
     */
    public Actionneurs() {
        EV3MediumRegulatedMotor moteurGauche = 
        		new EV3MediumRegulatedMotor(MotorPort.C);
        EV3MediumRegulatedMotor moteurDroit = 
        		new EV3MediumRegulatedMotor(MotorPort.B);
        
        Wheel roueGauche = 
        		WheeledChassis.modelWheel(moteurGauche, 56).offset(-53.5);
        Wheel roueDroite = 
        		WheeledChassis.modelWheel(moteurDroit, 56).offset(53.5);
        
        Chassis chassis = new 
        		WheeledChassis(new Wheel[] { roueGauche, roueDroite }, 
        				WheeledChassis.TYPE_DIFFERENTIAL);
        
        pilot = new MovePilot(chassis);

        moteurPinces = new EV3MediumRegulatedMotor(MotorPort.A);
    }

    /**
     * Méthode pour régler la vitesse linéaire du robot.
     * 
     * @param vitesse Vitesse en millimètres par seconde.
     */
    public void setLinearSpeed(double vitesse){
        pilot.setLinearSpeed(vitesse);
    }
    
    /**
     * Méthode pour régler la vitesse de rotation du pilot
     * 
     * @param vitesse Vitesse (en degrès par secondes)
     */
    public void setRotationSpeed(double vitesse) {
    	pilot.setAngularSpeed(vitesse);
    }

    /**
     * Méthode pour faire avancer le robot sur une distance donnée.
     * 
     * @param distance Distance en millimètres que le robot doit parcourir.
     * @param immediateReturn Si vrai, la méthode retourne immédiatement sans attendre
     *                        la fin de l'exécution.
     */
    public void avancer(double distance, boolean immediateReturn) {
        pilot.travel(distance, immediateReturn);
    }

    /**
     * Méthode pour faire reculer le robot sur une distance donnée.
     * 
     * @param distance Distance en millimètres que le robot doit parcourir.
     * @param immediateReturn Si vrai, la méthode retourne immédiatement sans attendre
     *                        la fin de l'exécution.
     */   
    public void reculer(double distance, boolean immediateReturn) {
        pilot.travel(-distance, immediateReturn);
    }   

    /**
     * Méthode pour faire tourner le robot d'un angle donné.
     * 
     * @param angle Angle en degrés. Positif pour le sens horaire, négatif sinon.
     * @param immediateReturn Si vrai, la méthode retourne immédiatement sans attendre
     *                        la fin de l'exécution.
     */ 
    public void tourner(double angle, boolean immediateReturn) {
        pilot.rotate(angle, immediateReturn);
    }

    /**
     * Méthode pour arrêter immédiatement les moteurs du robot
     */
    public void arreter() {
        pilot.stop();
    }

    /**
     * Méthode pour ouvrir les pinces avec une vitesse spécifiée pendant
     * 2.8 secondes
     * 
     * @param vitesse (en mm par secondes)
     * @param immediateReturn (si vrai retourne imédiatement sans 
     * 				  avoir fini d'executer la méthode)
     */
    public void fermerPinces( int vitesse, boolean immediateReturn) {
        moteurPinces.setSpeed(vitesse);
        moteurPinces.backward();
        Delay.msDelay(2800);
        moteurPinces.stop();
    }

    /**
     * Méthode pour ouvrir les pinces avec une vitesse spécifiée pendant
     * 2.8 secondes
     * 
     * @param vitesse (en mm par secondes)
     * @param immediateReturn (si vrai retourne imédiatement sans 
     * 				  avoir fini d'executer la méthode)
     */
    public void ouvrirPinces( int vitesse, boolean immediateReturn) {
        moteurPinces.setSpeed(vitesse);
        moteurPinces.forward();
        Delay.msDelay(2800);
        moteurPinces.stop();
    }
    
    /**
     * Méthode pour savoir si le robot est en train de bouger
     * 
     * @return boolean vrai si le robot est en mouvement
     */
    public boolean isMoving() {
    	return pilot.isMoving();
    }
}




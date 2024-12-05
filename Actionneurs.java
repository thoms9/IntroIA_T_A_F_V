import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.navigation.*;
import lejos.utility.Delay;
import lejos.robotics.chassis.*;

	/**
	* Classe Actionneur qui gère l'initialisation et l'accès aux moteurs connectés au robot LEGO EV3.
	*/
	public class Actionneurs {
    
    private MovePilot pilot;    
    private EV3MediumRegulatedMotor moteurPinces;

    // Constructeur qui initialise les moteurs et le pilot
    public Actionneurs() {
        EV3MediumRegulatedMotor moteurGauche = new EV3MediumRegulatedMotor(MotorPort.C);
        EV3MediumRegulatedMotor moteurDroit = new EV3MediumRegulatedMotor(MotorPort.B);
        
        Wheel roueGauche = WheeledChassis.modelWheel(moteurGauche, 56).offset(-54);
        Wheel roueDroite = WheeledChassis.modelWheel(moteurDroit, 56).offset(54);
        
        Chassis chassis = new WheeledChassis(new Wheel[] { roueGauche, roueDroite }, WheeledChassis.TYPE_DIFFERENTIAL);
        
        pilot = new MovePilot(chassis);

        moteurPinces = new EV3MediumRegulatedMotor(MotorPort.A);
    }

    //Méthode pour régler la vitesse de déplacement du pilot différentiel (en cm par seconde)
    public void setLinearSpeed(double vitesse){
        pilot.setLinearSpeed(vitesse);
    }
    
    //Méthode pour régler la vitesse de rotation du pilot différentiel (en cm par seconde)
    public void setRotationSpeed(double vitesse) {
    	pilot.setAngularSpeed(vitesse);
    }

    // Méthode pour faire avancer le robot sur une distance donnée (en cm)
    public void avancer(double distance, boolean immediateReturn) {
        pilot.travel(distance, immediateReturn);
    }

    // Méthode pour faire reculer le robot sur une distance donnée (en cm)
    public void reculer(double distance, boolean immediateReturn) {
        pilot.travel(-distance, immediateReturn);
    }   

    // Méthode pour tourner le robot d'un angle donné (en degrés)
    public void tourner(double angle, boolean immediateReturn) {
        pilot.rotate(angle, immediateReturn);  // Tourne le robot de l'angle spécifié (positif pour tourner à droite, négatif pour tourner à gauche)
    }

    // Méthode pour arrêter immédiatement les moteurs du robot
    public void arreter() {
        pilot.stop();
    }

    // Méthode pour fermer les pinces avec un angle et une vitesse spécifiés
    public void fermerPinces( int vitesse, boolean immediateReturn) {
        moteurPinces.setSpeed(vitesse);
        moteurPinces.backward();
        Delay.msDelay(2800);
        moteurPinces.stop();
    }

    // Méthode pour ouvrir les pinces avec un angle et une vitesse spécifiés
    public void ouvrirPinces( int vitesse, boolean immediateReturn) {
        moteurPinces.setSpeed(vitesse);
        moteurPinces.forward();
        Delay.msDelay(2800);
        moteurPinces.stop();
    }
    
    public void stopChassis(boolean b) {
    	pilot.stop();
    }
    
    public boolean isMoving() {
    	return pilot.isMoving();
    }
}

import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.*;
import lejos.robotics.chassis.*;


public class Actionneurs {
    
	//
    private MovePilot pilot;
    private RegulatedMotor moteurPinces;

   
    public Actionneurs() {
        EV3MediumRegulatedMotor moteurGauche = new EV3MediumRegulatedMotor(MotorPort.C);
        EV3MediumRegulatedMotor moteurDroit = new EV3MediumRegulatedMotor(MotorPort.B);
        
        
        Wheel roueGauche = WheeledChassis.modelWheel(moteurGauche, 56).offset(-75);
        Wheel roueDroite = WheeledChassis.modelWheel(moteurDroit, 56).offset(75);
        
        Chassis chassis = new WheeledChassis(new Wheel[] { roueGauche, roueDroite }, WheeledChassis.TYPE_DIFFERENTIAL);
        
      
        pilot = new MovePilot(chassis);

    
        moteurPinces = new EV3MediumRegulatedMotor(MotorPort.A);
    }

    
    public void avancer(double distance) {
        pilot.travel(distance); 
    }

 
    public void reculer(double distance) {
        pilot.travel(-distance); 
    }


    public void tourner(double angle) {
        pilot.rotate(angle); 
    }


    public void arreter() {
        pilot.stop();
    }

    
    public void fermerPinces(int angle, int vitesse) {
        moteurPinces.setSpeed(vitesse);
        moteurPinces.rotate(angle); 
    }


    public void ouvrirPinces(int angle, int vitesse) {
        moteurPinces.setSpeed(vitesse);
        moteurPinces.rotate(-angle); 
    }

   
}


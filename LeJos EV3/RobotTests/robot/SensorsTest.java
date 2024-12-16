package robot;

import org.junit.Test;

public class SensorsTest {
	
	@Test
	public void testCapteurTactile() {
        Sensors sensor = new Sensors();
        
        // Test du capteur couleur
        System.out.println("Capteur tactile activé : " + sensor.getTouch());
    }
	
	@Test
	public void testCapteurUltrasonique() {
		Sensors sensor = new Sensors();
		
		// Test du capteur ultrasonique
		System.out.println("Distance mesurée : " + sensor.getDistance());
	}
	
	@Test
	public void testCapteurCouleur() {
		Sensors sensor = new Sensors();
		
		// Test du capteur couleur
		System.out.println("Couleur détectée : " + sensor.getColor());
	}


}

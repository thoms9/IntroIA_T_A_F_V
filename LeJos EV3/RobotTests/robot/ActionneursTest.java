package robot;

import org.junit.Test;

public class ActionneursTest {

	@Test
	public void testDeplacement() {
		Actionneurs actionneurs = new Actionneurs();
		
		// Test de déplacement
        System.out.println("Test: avancer 500 ms");
        actionneurs.avancer(500, false);
        
        System.out.println("Test: reculer 500 ms");
        actionneurs.reculer(500, false);
        
        System.out.println("Test: tourner de 90 degrés");
        actionneurs.tourner(90, false);
	}
	
	@Test
	public void testPinces() {
		Actionneurs actionneurs = new Actionneurs();
		
		// Test des pinces
        System.out.println("Test: ouvrir les pinces");
        actionneurs.ouvrirPinces(700, false);
        
        System.out.println("Test: fermer les pinces");
        actionneurs.fermerPinces(700, false);
	}
}

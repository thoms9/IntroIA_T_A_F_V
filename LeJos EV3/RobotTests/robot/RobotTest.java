package robot;

import org.junit.Test;

public class RobotTest {

	@Test
	public void testInteg1() {
		Robot robot = new Robot();
		
		// Test pour arréter le robot si le capteur ultrasonique 
		// détecte un obstable a moin de 25 cm.
		robot.actionneurs.avancer(3000, false);
		while(robot.actionneurs.isMoving()) {
			if(robot.distanceDevant()< 0.25f) {
				robot.actionneurs.arreter();
			}
		}
		robot.stop();
		
	}
	
	@Test
	public void testPremierPalet() {
		Robot robot = new Robot();
		
		
		// Test pour vérifier le bon fonctionnement de premierPalet
		int orientation = 0;
		robot.premierPalet();
		
	}
	
	@Test
	public void testTrouverPalet() {
		Robot robot = new Robot();
		
		
		// Test pour vérifier le bon fonctionnement de trouverPalet
		int orientation = 0;
		robot.trouverPalet();
		
	}
	
	@Test
	public void testManipulerPalet() {
		Robot robot = new Robot();
		
		// Test pour vérifier le bon fonctionnement de manipulerPalet
		int orientation = 135;
		robot.manipulerPalet();
	}
	
}

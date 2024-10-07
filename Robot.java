
public class Robot {

	Sensors sensors = new Sensors();
	Actionneurs actionneurs = new Actionneurs();
	
	public void Ramasser() {
		actionneurs.ouvrirPinces(10, 30);
		while(!sensors.getTouch()) actionneurs.avancer(10);
		if(sensors.getTouch()) actionneurs.fermerPinces(10,30);
	}
	 
	 

}

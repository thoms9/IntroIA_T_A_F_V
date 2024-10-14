import java.util.ArrayList;
import lejos.utility.Delay;

import lejos.robotics.SampleProvider;

public class Robot {

	private Sensors sensors;
	private Actionneurs actionneurs;
	
    public Robot() {
        sensors = new Sensors();
        actionneurs = new Actionneurs();
    }
        
	public void ramasser() {
		actionneurs.ouvrirPinces(10, 30);
		while(!sensors.getTouch()) actionneurs.avancer(10);
		if(sensors.getTouch()) actionneurs.fermerPinces(10,30);
	}
	
	public void rechercher() {
		SampleProvider distanceProvider = sensors.getDistance();
		ArrayList distances = new ArrayList();
		
			float[] sample = new float[distanceProvider.sampleSize()];
	        distanceProvider.fetchSample(sample, 0);
	        distances.add(Float.valueOf(sample[0]));	
		
	}
	
	public void deplacerVersPalet() {
		tourner(angle.Rechercher());
		Delay.msDelay(200);
		avancer(distance.Rechercher()-10);
		
	}
	
	public void deposer() {
		actionneurs.ouvrirPinces(10, 30);
	}
	
	public void premierPalet() {
		actionneurs.arc(10, 10);
		ramasser();
		actionneurs.avancer(10);
		actionneurs.ouvrirPinces(10, 10);
	}
	
   
}


import java.util.ArrayList;

import lejos.robotics.SampleProvider;

public class Robot {

	private Sensors sensors;
	private Actionneurs actionneurs;
	
    public Robot() {
        sensors = new Sensors();
        actionneurs = new Actionneurs();
    }
        
	public void Ramasser() {
		actionneurs.ouvrirPinces(10, 30);
		while(!sensors.getTouch()) actionneurs.avancer(10);
		if(sensors.getTouch()) actionneurs.fermerPinces(10,30);
	}
	
	public void Rechercher() {
		SampleProvider distanceProvider = sensors.getDistance();
		ArrayList distances = new ArrayList();
		
			float[] sample = new float[distanceProvider.sampleSize()];
	        distanceProvider.fetchSample(sample, 0);
	        distances.add(Float.valueOf(sample[0]));	
		
	}
	
	public void DeplacerVersPalet() {
		public void Avancer(distance.Rechercher());
		
	}
	
	public void Deposer() {
		actionneurs.ouvrirPinces(10, 30);
	}
	
	public void PremierPalet() {
		actionneurs.arc(10, 10);
		Ramasser();
		actionneurs.avancer(10);
		actionneurs.ouvrirPinces(10, 10);
	}
	
   
}


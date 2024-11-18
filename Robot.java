import java.util.ArrayList;
import java.util.List;

import lejos.hardware.motor.*;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class Robot {

	private Sensors sensors;
	private Actionneurs actionneurs;
	
    public Robot() {
        sensors = new Sensors();
        actionneurs = new Actionneurs();
    }
        
	public void ramasser() {
		actionneurs.setSpeedChassis(120);
		actionneurs.ouvrirPinces(1000);
		while(!sensors.getTouch()) { 
			actionneurs.avancer(100, true);
			//Delay.msDelay(500);
		}
		if(sensors.getTouch()) actionneurs.fermerPinces(1000);
	}
	
	public void rechercher() {
		SampleProvider distanceProvider = sensors.getDistance();
		ArrayList distances = new ArrayList();
		
			float[] sample = new float[distanceProvider.sampleSize()];
	        distanceProvider.fetchSample(sample, 0);
	        distances.add(Float.valueOf(sample[0]));	
		
	}
	
	public void deplacerVersPalet() {
		
		Delay.msDelay(200);
		
		
	}
	
	public void deposer() {
		actionneurs.ouvrirPinces(1000);
	}
	
	public void premierPalet() {
		actionneurs.avancer(500, true);
		actionneurs.ouvrirPinces(1000);
		actionneurs.fermerPinces(1000);
		actionneurs.arc(1230,750);
		actionneurs.ouvrirPinces(100);

	}
	
	public static void main (String[] args) {
		
	

		
        Actionneurs actionneurs = new Actionneurs();
        Sensors sensors = new Sensors();
        
        
        final int numMeasures = 36;  
        float[] distances = new float[numMeasures];  
        
        
        SampleProvider distanceSensor = sensors.getDistance();
        float[] distanceSample = new float[distanceSensor.sampleSize()];
        
       
        actionneurs.setSpeedChassis(50);  
        
        List<Float> data  = new ArrayList<>(); 
        actionneurs.tourner(360.0, true);
        while (actionneurs.isMoving()) {
            distanceSensor.fetchSample(distanceSample, 0);
            data.add(distanceSample[0]);  
            System.out.println((distanceSample[0] * 100) + " cm");
            
        }
        

        System.out.println(data);
         
        

        
        sensors.closeSensors();
        
    }
	
}

   



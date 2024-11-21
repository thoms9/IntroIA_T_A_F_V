import java.util.ArrayList;
import java.util.List;

import lejos.hardware.motor.*;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class Robot {

	private Sensors sensors;
	private Actionneurs actionneurs;
	private List<Float> distances;
	private int orientation;
	
    public Robot() {
        sensors = new Sensors();
        actionneurs = new Actionneurs();
        distances = new ArrayList<>();
        orientation = 0;
    }
        
	public void ramasser() {
		actionneurs.setLinearSpeed(120);
		actionneurs.ouvrirPinces(1000);
		while(!sensors.getTouch()) { 
			actionneurs.avancer(100, true);
			//Delay.msDelay(500);
		}
		if(sensors.getTouch()) actionneurs.fermerPinces(1000);
	}
	
public void rechercher() {
		
		actionneurs.setRotationSpeed(50);
		
	    distances.clear();
	    
	    actionneurs.tourner(360.0, true);
	    
	    float[] sample = new float[sensors.getDistance().sampleSize()];
	    
	    while (actionneurs.isMoving()) {
	        sensors.getDistance().fetchSample(sample, 0);
	        
	        distances.add(sample[0]);
	        
	        Delay.msDelay(100);
	    }
	    
	    sensors.closeSensors();
	    
	    System.out.println("Distances : " + distances);
	    System.out.println(distances.size());
	    
	}

	
public void deplacerVersPalet() {
	
	List<Integer> indicesPalet = new ArrayList<>();
	
	final double tolerance = 0.25;
    
    for (int i = 0; i < distances.size() - 1; i++) {
    	if (distances.get(i)>1) i++;
        if (Math.abs(distances.get(i) - distances.get(i + 1)) <= tolerance) {
            indicesPalet.add(i);
        }
    }
    
    double d = 10;
    int c = 0;
    
    if (!indicesPalet.isEmpty()) {
    	for (int j = 0; j < indicesPalet.size()-2 - 1; j=j+2) {
			if(distances.get((indicesPalet.get(j)+indicesPalet.get(j+1))/2)<d) {
				d = (distances.get((indicesPalet.get(j)+indicesPalet.get(j+1))/2));
				c = (indicesPalet.get(j)+indicesPalet.get(j+1))/2;
			}
    	}
    }

    if (!indicesPalet.isEmpty()) System.out.println("Indices palets : " + indicesPalet);
    else System.out.println("Aucun palet détecté.");
    
    

    
    System.out.println(indicesPalet);
    System.out.println("Orientation palet : " + 360/distances.size()*c);

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
	
	public int getCouleurEnTempsReel() {
	   
	    SampleProvider colorProvider = sensors.getColor();
	    float[] sample = new float[colorProvider.sampleSize()];
	    
	    
	    colorProvider.fetchSample(sample, 0);
	    
	   
	    return (int) sample[0];
	}
	
	public void avancerJusquaBlanc() {
	    actionneurs.setLinearSpeed(200); 
	    actionneurs.avancer(Double.MAX_VALUE, true); 

	   
	    while (getCouleurEnTempsReel() != 6) { 
	        Delay.msDelay(100); 
	    }

	   
	    actionneurs.arreter();
	    System.out.println("Couleur blanche détectée, arrêt du robot.");
	    deposer();
	    actionneurs.reculer(150,true);
	    actionneurs.fermerPinces(1000);
	}
	
	public static void main (String[] args) {
		
		Robot R = new Robot();
		R.rechercher();
		R.deplacerVersPalet();
		//R.avancerJusquaBlanc();
		
        
    }
	
}

   



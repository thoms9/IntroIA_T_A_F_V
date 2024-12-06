import java.util.ArrayList;
import java.util.List;

import lejos.hardware.Button;
import lejos.utility.Delay;

public class Robot {

	private Sensors sensors;
	private Actionneurs actionneurs;
	private List<Float> distances;
	private double orientation;
	private int indicePalet;
	private Float distancePalet;
	private static int etat;
	private static int tentative;
	private static int nbRecherche;
	private float dMin = 3f;
	private int indiceMin = -1;
	
	
	public Robot() {
        sensors = new Sensors();
        actionneurs = new Actionneurs();
        distances = new ArrayList<>();
        orientation = 0;
        indicePalet = -1;
        distancePalet = 3f;
        etat = -1;
        tentative = 0;
        dMin = 3f;
		indiceMin = -1;
		nbRecherche = 0;
    }
    
    public void stop() {
    	actionneurs.arreter();
    	sensors.closeSensors();
    }
    
    public void rechercher() {
    			
		actionneurs.setRotationSpeed(150);
	    distances.clear();
	    actionneurs.tourner(360.0, true);
	    
	    float[] sample = new float[sensors.getDistance().sampleSize()];
	    
	    while (actionneurs.isMoving()) {
	        sensors.getDistance().fetchSample(sample, 0);   
	        distances.add(sample[0]);
	        Delay.msDelay(15);
	    }
	    if(distances.isEmpty() && nbRecherche<=3 ) {
	    	rechercher();
	    	nbRecherche += 1;
	    }
	    else if (nbRecherche>3)
	    	distances.add(3f);
	}

	public List<Integer> indicesDistances() {
		List<Integer> indices = new ArrayList<>();	
		double angle = (double)360/distances.size();
		for (int i = 0; i < distances.size(); i++) {	
	    	if(i<(90/angle))
	    			indices.add(-2);
	    	else if(i>(270/angle))
	    			indices.add(-2);
	    	else if(distances.get(i)>3) {
	    		indices.add(i);
	    		distances.set(i,3f);
	    	}
	    	else indices.add(i);
		 }
	    return indices;
	 }
    
    public List<Integer> indicesDebutFin(List<Integer> indices) {
    	List<Integer> indicesDebutFin = new ArrayList<>();
        final double tolerance = 0.15;
            for (int i = 1; i < distances.size(); i++) {
            	if(indices.get(i)==-2) {
                	continue;
                }
                double difference = Math.abs(distances.get(i) - distances.get(i - 1));
                if (difference < tolerance) {
                	indicesDebutFin.add(indices.get(i));
                }
            }

            return indicesDebutFin;
    }
    
    public List<Integer> indicesPalets(List<Integer> indicesDebutFin) {
	    List<Integer> indicesPalets = new ArrayList<>();
	    for (int i=0; i<indicesDebutFin.size()-1; i+=2) {
	    	int debut = indicesDebutFin.get(i);
	    	int fin = indicesDebutFin.get(i+1)-2;
	   		int moy = (debut+fin)/2;
	   		indicesPalets.add(moy);
	   	}
	   	return indicesPalets;
    }
    
    public void distancePalet(List<Integer> indicesPalets) {
    	for (int i=0; i<indicesPalets.size();i++) {
    		if(distances.get(indicesPalets.get(i))<distancePalet) {
	    		distancePalet = distances.get(indicesPalets.get(i));
	    		indicePalet = indicesPalets.get(i);
	   		}
	   	}
     }
    
    public void trouverPalet() {
    	
    	rechercher();
		nbRecherche = 0;
		List<Integer> indices = indicesDistances();
        List<Integer> indicesDebutFin = indicesDebutFin(indices);
        List<Integer> indicesPalets = indicesPalets(indicesDebutFin);
        distancePalet(indicesPalets);
        orientation = indicePalet*(double)360/distances.size();
        if(distancePalet==3f || indicePalet==-1) {
        	actionneurs.reculer(500, false);
        	tentative += 1;
        	trouverPalet();
        	return;
        }
        	
	}
	
	public void deplacerVersPalet() {
		
		actionneurs.setRotationSpeed(75);
		actionneurs.setLinearSpeed(100);
		actionneurs.tourner(orientation, false);
		actionneurs.avancer(3000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(250);
			float distance = distanceDevant();
			if(distance <= distancePalet && distance > 0.15) {
				distancePalet = distance;
			}
			else {
				actionneurs.arreter();
			}
		}       
	}
		
	public Float distanceDevant() {
		float[] sample = new float[sensors.getDistance().sampleSize()];
		sensors.getDistance().fetchSample(sample, 0);
		return sample[0];
	}
	
	public void ramasser() {
        actionneurs.setLinearSpeed(75);
        actionneurs.ouvrirPinces(700, true);
        actionneurs.avancer(600, true);
        boolean palet = false;
        
        while(actionneurs.isMoving()) {
        	 if (sensors.getTouch()) {
        		 actionneurs.fermerPinces(700,false);
             	 actionneurs.arreter();
             	 palet = true;
             }
        	
        }
        if(!palet) actionneurs.fermerPinces(700, false); 
    }

    public void allerZoneDepot() {
		
    	actionneurs.setLinearSpeed(150);
				
		if(orientation < 180) {
			actionneurs.tourner(90-orientation, false);
		}
		
		else {
			actionneurs.tourner(270-orientation, false);
		}
		
		actionneurs.avancer(2000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(50);
			float distance = distanceDevant();
			if(distance > 0.15) {
				continue;
			}
			else {
				actionneurs.arreter();
			}
		}
		if(orientation < 180) {
			actionneurs.tourner(-90, false);
		}
				
		else actionneurs.tourner(90, false);
	}

	public void deposer() {
		
		actionneurs.setLinearSpeed(200);
		actionneurs.avancer(3000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(150);
			float distance = distanceDevant();
			int color = sensors.getColor();
			if(color == lejos.robotics.Color.WHITE && distance < 0.3) {
				actionneurs.arreter();
				actionneurs.ouvrirPinces(700, true);
				actionneurs.setLinearSpeed(150);
				actionneurs.reculer(100, true);
				actionneurs.fermerPinces(700,false);
				actionneurs.arreter();
			}
			else {
				continue;
			}
		}	
	}
	
	public void resetOrientation() {
		
		if(orientation < 180) {
			actionneurs.tourner(90, false);
		}
			
		else {
			actionneurs.tourner(-90, false);
		}
		actionneurs.reculer(2000, true);
		while(actionneurs.isMoving()) {
			Delay.msDelay(50);
			float distance = distanceDevant();
			if(distance < 0.9f) {
				continue;
			}
			else {
				actionneurs.arreter();
			}
		}
		if(orientation < 180) {
			actionneurs.tourner(-90, false);
		}
			
		else {
			actionneurs.tourner(90, false);
		}
		
		rechercher();
		dMin = 3f;
		indiceMin = -1;
		distanceMin();
		if(indiceMin == -1) {
			deposer();
			rechercher();
			distanceMin();
		}
	}
	
	public void distanceMin() {
		for(int i=0;i<distances.size();i++) {
			if(distances.get(i)<dMin) {
				dMin = distances.get(i);
				indiceMin=i;
			}
			
		}
		double angle = indiceMin*(double)360/distances.size();
		actionneurs.setRotationSpeed(75);
		actionneurs.tourner(angle, false);
		orientation=0;
	}

 	public void manipulerPalet() {
		deplacerVersPalet();
		ramasser();
		allerZoneDepot();
		deposer();
		resetOrientation();
		distancePalet = 3f;
		indicePalet = -1;
	}
	
	public void premierPalet() {
		
		actionneurs.setLinearSpeed(200);
		actionneurs.setRotationSpeed(75);
		actionneurs.avancer(600,true);
		actionneurs.ouvrirPinces(700,false);
		actionneurs.fermerPinces(700,false);
		actionneurs.tourner(30,false);
		actionneurs.avancer(350,false);
		actionneurs.tourner(-30, false);
		deposer();
		resetOrientation();

	}
	
	public static void main (String[] args) {
		
		Robot R = new Robot();
		
		while(etat != 3) {
		
			switch(etat) {
			
			    case -1:
			    	if(Button.ENTER.isDown()) {
			    		etat = 0;
			    	}
					
			
				case 0:
					R.premierPalet();
					if(Button.ESCAPE.isDown()) {
						R.stop();
						etat = -1;
					}
					etat = 1;
					break;
				
				case 1:
					R.trouverPalet();
					if(tentative>3) etat = 3;
					if(Button.ESCAPE.isDown()) {
						R.stop();
						etat = -1;
					}
					else etat = 2;
					break;
				
				case 2:
					R.manipulerPalet();
					tentative = 0;
					if(Button.ESCAPE.isDown()) {
						R.stop();
						etat = -1;
					}
					etat = 1;
					break;
				
				case 3:
					R.stop();
					if(Button.ESCAPE.isDown()) {
						R.stop();
						etat = -1;
					}
					break;
	
			}
		}
   
    }
	
}

import java.util.ArrayList;
import java.util.List;
import lejos.utility.Delay;

public class Robot {

	private Sensors sensors;
	private Actionneurs actionneurs;
	private List<Float> distances;
	private double orientation;
	private int indicePalet;
	private Float distancePalet;
	private int premiereCouleur;
	
    public Robot() {
        sensors = new Sensors();
        actionneurs = new Actionneurs();
        distances = new ArrayList<>();
        orientation = 0;
        indicePalet = -1;
        distancePalet = 3f;
        premiereCouleur = lejos.robotics.Color.BLUE;
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
	}

	public List<Integer> indicesDistances() {
		List<Integer> indices = new ArrayList<>();	
		double angle = (double)360/distances.size();
		for (int i = 0; i < distances.size(); i++) {	
	    	if(i<(90/angle))
	    			indices.add(-2);
	    	else if(i>(270/angle))
	    			indices.add(-2);
	    	else if(distances.get(i)>2.5)
	    		indices.add(-1);
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
	    	int fin = indicesDebutFin.get(i+1)-1;
	   		int moy = (debut+fin)/2;
	   		indicesPalets.add(moy);
	   	}
	   	return indicesPalets;
    }
    
    public Float distancePalet(List<Integer> indicesPalets) {
    	for (int i=0; i<indicesPalets.size();i++) {
    		if(distances.get(indicesPalets.get(i))<distancePalet) {
	    		distancePalet = distances.get(indicesPalets.get(i));
	   		}
	   	}
	   	return distancePalet;
     }
    
    public int orientationMin(List<Integer> indicesPalets) {
    	float distanceMin = 3;
    	int indice = -1;
	   	for (int i=0; i<indicesPalets.size();i++) {
	   		if(distances.get(indicesPalets.get(i))<distanceMin) {
	   			distanceMin = distances.get(indicesPalets.get(i));
	   			indice = indicesPalets.get(i);
    		}
	   	}
	   	return indice;
	 }
    
    public void trouverPalet() {
    	
    	rechercher();
		
		List<Integer> indices = indicesDistances();
        List<Integer> indicesDebutFin = indicesDebutFin(indices);
        List<Integer> indicesPalets = indicesPalets(indicesDebutFin);
        distancePalet = distancePalet(indicesPalets);
        indicePalet = orientationMin(indicesPalets);
        orientation = indicePalet*(double)360/distances.size();
        System.out.println("indices Palets: " + indicesPalets);
        System.out.println("distance palet le plus proche: " +distancePalet + " m");
        System.out.println("orientation palet le plus proche: "
        		+ indicePalet*(double)360/distances.size() + " degrès");
        if(distances.isEmpty() || indicesDebutFin.isEmpty() || indicesPalets.isEmpty() ||
        		distancePalet==3 || indicePalet==-1) {
        	actionneurs.reculer(300, false);
        	trouverPalet();
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
			if(distance > 2f) distance = 2f;
			if(distance < distancePalet) {
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
        actionneurs.avancer(450, true);
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
		switch (premiereCouleur) {
			
		case lejos.robotics.Color.BLUE:
			allerZoneDepotVert();
			break;
		
		case lejos.robotics.Color.GREEN:
			allerZoneDepotBlue();
			break;
			
		default:
			break;
		}
	}
	
    public void allerZoneDepotVert() {
		
    	actionneurs.setLinearSpeed(75);
		actionneurs.avancer(100, false);
		actionneurs.setRotationSpeed(75);
				
		if(orientation < 180) {
			actionneurs.tourner(90-orientation, false);
		}
		
		else {
			actionneurs.tourner(270-orientation, false);
		}
		
		actionneurs.setLinearSpeed(150);
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
			actionneurs.tourner(-110, false);
		}
				
		else actionneurs.tourner(110, false);
	}

	public void allerZoneDepotBlue() {
		
		actionneurs.avancer(100, false);
				
		if(orientation < 180) {
			actionneurs.tourner(90-orientation, false);
		}
		
		else {
			actionneurs.tourner(270-orientation, false);
		}
		
		actionneurs.setLinearSpeed(75);
		actionneurs.avancer(2000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(50);
			float distance = distanceDevant();
			if(distance > 0.15) {
				continue;
			}
			else {
				actionneurs.arreter();
				if(orientation < 180) {
					actionneurs.tourner(90, false);
				}
				
				else actionneurs.tourner(-90, false);
				break;
			}
		}
	}
	
	public void deposer() {
		switch (premiereCouleur) {
		
		case lejos.robotics.Color.BLUE:
			deposerVert();
			break;
		
		case lejos.robotics.Color.GREEN:
			deposerBlue();
			break;
			
		default:
			break;
		}
	}
	
	public void deposerVert() {
		
		actionneurs.setLinearSpeed(100);
		actionneurs.avancer(3000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(75);
			float distance = distanceDevant();
			int color = sensors.getColor();
			if(color == lejos.robotics.Color.WHITE && distance < 0.3) {
				actionneurs.arreter();
				actionneurs.ouvrirPinces(700, true);
				actionneurs.reculer(350, true);
				actionneurs.fermerPinces(700,true);
				actionneurs.arreter();
			}
			else {
				continue;
			}
		}
		actionneurs.setLinearSpeed(50);
		if(orientation < 180) {
			actionneurs.tourner(90, false);
		}
			
		else {
			actionneurs.tourner(-90, false);
		}
		actionneurs.reculer(800, true);
		while(actionneurs.isMoving()) {
			if(sensors.getColor() == lejos.robotics.Color.BLACK) {
				actionneurs.arreter();
			}
			else continue;
		}
		if(orientation < 180) {
			actionneurs.tourner(-90, false);
		}
			
		else {
			actionneurs.tourner(90, false);
		}	
	}

	public void deposerBlue() {
		
		actionneurs.setLinearSpeed(100);
		actionneurs.avancer(3000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(75);
			float distance = distanceDevant();
			if(sensors.getColor() == lejos.robotics.Color.WHITE || distance > 0.3) {
				actionneurs.arreter();
				actionneurs.ouvrirPinces(700, true);
				actionneurs.reculer(350, true);
				actionneurs.fermerPinces(700,true);
				actionneurs.arreter();
			}
			else {
				continue;
			}
		}
		if(orientation < 180) {
			actionneurs.tourner(-90, false);
		}
			
		else {
			actionneurs.tourner(90, false);
		}
		actionneurs.reculer(800, true);
		while(actionneurs.isMoving()) {
			if(sensors.getColor() == lejos.robotics.Color.BLACK) {
				actionneurs.arreter();
			}
			else continue;
		}
		if(orientation < 180) {
			actionneurs.tourner(90, false);
		}
			
		else {
			actionneurs.tourner(-90, false);
		}	
	}
	
	public void resetOrientation() {
		float distanceZero = 0f;
		if(orientation>180) {
			actionneurs.setRotationSpeed(50);
			while(actionneurs.isMoving()) {
				Delay.msDelay(5);
				float distance = distanceDevant();
				if(distance == distanceZero) {
					actionneurs.arreter();
					orientation = 0;
				}
				else continue;
			}
		}
		else {
			
		}
	}

	public void manipulerPalet() {
		ramasser();
		allerZoneDepot();
		deposer();
	}
	
	public void premierPalet() {
		premiereCouleur = sensors.getColor();
	}
	
	public static void main (String[] args) {
		
		Robot R = new Robot();
		R.trouverPalet();
		R.manipulerPalet();
		R.stop();
		
        
    }
	
}

   



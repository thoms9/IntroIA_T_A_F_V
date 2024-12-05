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
        premiereCouleur = -1;
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
	    		indices.add(-2);
	    	else indices.add(i);
		 }
	    return indices;
	 }
    
    public List<Integer> indicesDebutFin(List<Integer> indices) {
    	List<Integer> indicesDebutFin = new ArrayList<>();
        final double tolerance = 0.3;
            for (int i = 1; i < distances.size(); i++) {
                double difference = Math.abs(distances.get(i) - distances.get(i - 1));
                if(indices.get(i)==-2) {
                	continue;
                }
                else if (difference > tolerance) {
                	indicesDebutFin.add(indices.get(i));
                }
            }

            return indicesDebutFin;
    }
    
    public List<Integer> indicesPalets(List<Integer> indicesDebutFin) {
	    List<Integer> indicesPalets = new ArrayList<>();
	    for (int i=0; i<indicesDebutFin.size()-1; i+=2) {
	    	int debut = indicesDebutFin.get(i);
	    	int fin = indicesDebutFin.get(i+1);
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
        System.out.println("indices Palets: " + indicesPalets);
        System.out.println("distance palet le plus proche: " +distancePalet + " m");
        System.out.println("orientation palet le plus proche: "
        		+ indicePalet*(double)360/distances.size() + " degrès");
        if(distances.isEmpty() || indicesDebutFin.isEmpty() || indicesPalets.isEmpty() ||
        		distancePalet==3 || indicePalet==-1) {
        	actionneurs.reculer(30, false);
        	trouverPalet();
        }
        	
	}
	
	public void deplacerVersPalet() {
		
		actionneurs.setRotationSpeed(75);
		actionneurs.setLinearSpeed(100);
		actionneurs.tourner(orientation, false);
		actionneurs.avancer(distancePalet*100+25, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(20);
			float distance = distanceDevant();
			if(distance < distancePalet) {
				distancePalet = distance;
			}
			else {
				actionneurs.arreter();
				ramasser();
			}
		}       
	}
	
	public Float distanceDevant() {
		float[] sample = new float[sensors.getDistance().sampleSize()];
		sensors.getDistance().fetchSample(sample, 0);
		return sample[0];
	}
	
	public void ramasser() {
        actionneurs.setLinearSpeed(50);
        actionneurs.ouvrirPinces(750, true);
        actionneurs.avancer(45, true);
        
        while(actionneurs.isMoving()) {
        	 if (sensors.getTouch()) {
        		 actionneurs.fermerPinces(750,false);
             	 actionneurs.arreter();
             }
        	
        }
    }

	public void allerZoneDepot() {
		switch (premiereCouleur) {
			
		case lejos.robotics.Color.BLUE:
			allerZoneDepotVert();
		
		case lejos.robotics.Color.GREEN:
			allerZoneDepotBlue();
			
		default:
			break;
		}
	}
	
    public void allerZoneDepotVert() {
		
		actionneurs.avancer(10, false);
				
		if(orientation < 180) {
			actionneurs.tourner(90-orientation, false);
		}
		
		else {
			actionneurs.tourner(90+orientation, false);
		}
		
		actionneurs.setLinearSpeed(75);
		actionneurs.avancer(Double.MAX_VALUE, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(20);
			float distance = distanceDevant();
			if(distance > 0.15) {
				continue;
			}
			else {
				actionneurs.arreter();
				if(orientation < 180) {
					actionneurs.tourner(-90, false);
				}
				
				else actionneurs.tourner(90, false);
				break;
			}
		}
	}

	public void allerZoneDepotBlue() {
		
		actionneurs.avancer(10, false);
				
		if(orientation < 180) {
			actionneurs.tourner(90+orientation, false);
		}
		
		else {
			actionneurs.tourner(90-orientation, false);
		}
		
		actionneurs.setLinearSpeed(75);
		actionneurs.avancer(Double.MAX_VALUE, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(20);
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
		
		case lejos.robotics.Color.GREEN:
			deposerBlue();
			
		default:
			break;
		}
	}
	
	public void deposerVert() {
		
		actionneurs.setLinearSpeed(100);
		actionneurs.avancer(Double.MAX_VALUE, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(20);
			float distance = distanceDevant();
			if(!(sensors.getColor() == lejos.robotics.Color.WHITE) || distance > 0.3) {
				continue;
			}
			else {
				actionneurs.arreter();
				actionneurs.ouvrirPinces(750, true);
				actionneurs.reculer(35, true);
				actionneurs.fermerPinces(1000,true);
				actionneurs.arreter();
			}
		}
		if(orientation < 180) {
			actionneurs.tourner(90, false);
		}
			
		else {
			actionneurs.tourner(-90, false);
		}
		actionneurs.reculer(80, true);
		while(actionneurs.isMoving()) {
			if(!(sensors.getColor() == lejos.robotics.Color.BLACK)) {
				continue;
			}
			else actionneurs.arreter();
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
		actionneurs.avancer(Double.MAX_VALUE, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(20);
			float distance = distanceDevant();
			if(!(sensors.getColor() == lejos.robotics.Color.WHITE) || distance > 0.3) {
				continue;
			}
			else {
				actionneurs.arreter();
				actionneurs.ouvrirPinces(750, true);
				actionneurs.reculer(35, true);
				actionneurs.fermerPinces(1000,true);
				actionneurs.arreter();
			}
		}
		if(orientation < 180) {
			actionneurs.tourner(-90, false);
		}
			
		else {
			actionneurs.tourner(90, false);
		}
		actionneurs.reculer(80, true);
		while(actionneurs.isMoving()) {
			if(!(sensors.getColor() == lejos.robotics.Color.BLACK)) {
				continue;
			}
			else actionneurs.arreter();
		}
		if(orientation < 180) {
			actionneurs.tourner(90, false);
		}
			
		else {
			actionneurs.tourner(-90, false);
		}	
	}

	public void manipulerPalet() {
		ramasser();
		allerZoneDepot();
		deposer();
	}
	
	public void orientation() {
    	
    }
	
	public void premierPalet() {
		premiereCouleur = sensors.getColor();
		System.out.println("Noir: " + lejos.robotics.Color.BLACK);
		System.out.println("Blue: " + lejos.robotics.Color.BLUE);
		System.out.println("Vert: " + lejos.robotics.Color.GREEN);
		System.out.println("Blanc: " + lejos.robotics.Color.WHITE);
	}
	
	public static void main (String[] args) {
		
		Robot R = new Robot();
		R.trouverPalet();
		R.manipulerPalet();
        
    }
	
}

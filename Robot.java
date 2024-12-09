import java.util.ArrayList;
import java.util.List;

import lejos.hardware.Button;
import lejos.utility.Delay;

public class RobotF {
	
	private Sensors sensors;
	private Actionneurs actionneurs;
	private List<Float> distances;
	private double orientation;
	private static Float distancePalet;
	private static int etat;
	private static int tentative;
	
	public RobotF() {
        sensors = new Sensors();
        actionneurs = new Actionneurs();
        distances = new ArrayList<>();
        etat = -1;
        tentative = 0;
        orientation = 0;
        distancePalet = 0.80f;
//		actionneurs.ouvrirPinces(700,true);

	}
	
	public void deposer() {
		actionneurs.ouvrirPinces(700, true);
		actionneurs.reculer(100, true);
		actionneurs.fermerPinces(700, false);
	}
	
	public void deplacerVersMur() {
		if(orientation < 180) {
			actionneurs.tourner(80-orientation, false);
		}
		
		else {
			actionneurs.tourner(260-orientation, false);
		}
		
		actionneurs.avancer(2000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(50);
			float distance = distanceDevant();
			if(distance > 0.20) {
				continue;
			}
			else {
				actionneurs.arreter();
			}
		}
		if(orientation < 180) {
			actionneurs.tourner(-100, false);
		}
				
		else actionneurs.tourner(100, false);
	}
	
	public void prendrePalet() {
		boolean palet = false;
		actionneurs.ouvrirPinces(700, true);
        actionneurs.avancer(500, true);
        
        while(actionneurs.isMoving()) {
        	 if (sensors.getTouch()) {
        		 actionneurs.fermerPinces(700,false);
             	 actionneurs.arreter();
             	 palet = true;
             }
        	
        }
        if(!palet) actionneurs.fermerPinces(700, false);
	}
	
	public void avancerVersPalet() {
		actionneurs.avancer(3000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(250);
			float distance = distanceDevant();
			if(distance <= distancePalet && distance > 0.30) {
				distancePalet = distance;
			}
			else {
				actionneurs.arreter();
			}
		}       
	}

	public void tournerVersPalet() {
		actionneurs.tourner(orientation, false);
	}
	
	public void manipulerPalet() {
		tournerVersPalet();
		avancerVersPalet();
		prendrePalet();
		deplacerVersMur();
		allerALigneBlanche();
		deposer();
		recentrer();
		resetOrientation();
		distancePalet = 0.80f;
		
	}
	
	public void trouverPalet() {
		distancePalet = 0.80f;
		rechercher();
		distancePalet();
		
	}
	
	public void distancePalet() {
		int indiceMin = -1;
		distancePalet = 0.80f;
		for(int i=0;i<distances.size();i++) {
			double ori = i*(double)360/distances.size();
			if(ori < 110 || ori > 240) {
				continue;
			}
			else if(distances.get(i)<distancePalet) {
				distancePalet = distances.get(i);
				indiceMin=i;
			}
		}
		orientation = indiceMin*(double)360/distances.size();
	}
	
	public void distanceMin() {
		int indiceMin = -1;
		distancePalet = 0.80f;
		for(int i=0;i<distances.size();i++) {
			double ori = i*(double)360/distances.size();
			if(ori>70 || ori<290) {
				continue;
			}
			else if(distances.get(i)<distancePalet) {
				distancePalet = distances.get(i);
				indiceMin=i;
			}
		}
		orientation = indiceMin*(double)360/distances.size();
	}
	
	public void rechercher() {
        distances.clear();
        float[] sample = new float[sensors.getDistance().sampleSize()];
        actionneurs.tourner(360.0, true);

        while (actionneurs.isMoving()) {
            sensors.getDistance().fetchSample(sample, 0);
            distances.add(sample[0]);
            Delay.msDelay(30);
        }
    }
	
	public void resetOrientation() {
		
		allerALigneBlanche();
		rechercher();
		distanceMin();
		actionneurs.tourner(orientation, false);
		orientation = 0;

	}
	
	public void recentrer() {
		if(orientation < 180) {
			actionneurs.tourner(90, false);
		}
			
		else {
			actionneurs.tourner(-90, false);
		}
		actionneurs.reculer(2000, true);
		while(actionneurs.isMoving()) {
			Delay.msDelay(25);
			float distance = distanceDevant();
			if(distance < 0.85f) {
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
	}
	
	public Float distanceDevant() {
		float[] sample = new float[sensors.getDistance().sampleSize()];
		sensors.getDistance().fetchSample(sample, 0);
		return sample[0];
	}
	
	public void allerALigneBlanche() {
		actionneurs.avancer(3000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(150);
			float distance = distanceDevant();
			int color = sensors.getColor();
			if(color == lejos.robotics.Color.WHITE && distance < 0.3) {
				actionneurs.arreter();
			}
			else {
				continue;
			}
		}
	}
	
	public void premierPalet() {
		actionneurs.avancer(600,true);
		Delay.msDelay(1000);
		actionneurs.fermerPinces(700,false);
		actionneurs.tourner(30,false);
		actionneurs.avancer(350,false);
		actionneurs.tourner(-30, false);
		allerALigneBlanche();
		deposer();
		recentrer();
		
	}
	
	public void reculer(int distance) {
		actionneurs.reculer(distance, false);
	}
	
	public void stop() {
    	actionneurs.arreter();
    	sensors.closeSensors();
    }
	
	public static void main(String[] args) {
		RobotF R = new RobotF();
		R.actionneurs.setRotationSpeed(75);
		R.actionneurs.setLinearSpeed(150);
		
		while(etat != 3) {
		
			switch(etat) {
			
			    case -1:
			    	if(Button.waitForAnyPress()==Button.ID_ENTER) {
			    		etat = 0;
			    	}
					
			
				case 0:
//					R.premierPalet();
					etat = 1;
					break;
				
				case 1:
					R.trouverPalet();
					if(distancePalet == 0.80f) {
						R.actionneurs.reculer(400, false);
						break;
					}
					etat = 2;
					break;
				
				case 2:
					R.manipulerPalet();
					etat = 1;
					break;
				
				case 3:
					R.stop();
					break;
	
			}
		}


	}

}

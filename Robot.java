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

	
        /*
	 * Retourne un boolean qui détermine si la liste des distances est vide,
	 * vrai si la liste est vide, faux si la liste n'est pas vide. 
	 */
	public boolean distanceVide() {
		
	    if (distances.isEmpty()) {
	        System.out.println("Aucune donnée de distance disponible.");
	        return true;
	    }
	    
	    return false;
	}
	
	
	
	/*
	 * Retourne une HashMap contenant les indices (entiers) et les distances (float) en mètres,
	 */
	public Map<Integer, Float> indicesPalets() {
		
		Map<Integer, Float> distanceMap = new HashMap<>();
		
	    for (int i = 0; i < distances.size(); i++) {				
	        distanceMap.put(i, distances.get(i));
	    }
	    
	    return distanceMap;
	}
	
	
	
	/*
	 * Retourne une liste d'entier des indices des palets filtrés
	 * Prend comme paramètre une HashMap contenant les indices et les distances.
	 */
	public List<Integer> indicesFiltres(Map<Integer, Float> map) {
		
		List<Integer> indicesFiltres = new ArrayList<>();

	    for (int i = 0; i < distances.size(); i++) {
	        Float value = map.get(i);
	        if (value != null && value > 0.3  && value <2) indicesFiltres.add(i);
	    }
	    
	    if (indicesFiltres.isEmpty()) {
	        System.out.println("Aucun indice filtre.");
	    }
	    
	    return indicesFiltres;
	}
	
	
	
	/*
	 * Retourne une liste d'entier des indices des palets potentiels visibles. 
	 * Prend en premier paramètres une HashMap contenant les indices et les distances,
	 * et en deuxième paramètre une liste des indices filtrés.
	 */
	public List<Integer> indicesPalets(Map<Integer, Float> map, List<Integer> indices) {
		
		float tolerance = 0.21f;
		List<Integer> indicesPalets = new ArrayList<>();
	    
	    for (int i = 0; i < indices.size(); i++) {
	    	Float courant = (i >= 0 && i < indices.size()) ? map.get(indices.get(i)) : null;
	        Float prochain = (i + 1 < indices.size()) ? map.get(indices.get(i+1)) : null;
	        Float precedent = (i - 1 >= 0) ? map.get(indices.get(i-1)) : null;
	        
	        if(courant==null) continue;	        	
	        
	        if ((precedent != null && Math.abs(courant - precedent) <= tolerance) ||
	                (prochain != null && Math.abs(courant - prochain) <= tolerance)) {
	                indicesPalets.add(indices.get(i));
	            }
	        
	        if ((precedent != null && Math.abs(courant - precedent) <= tolerance) &&
	                !(prochain != null && Math.abs(courant - prochain) <= tolerance)) {
	                indicesPalets.add(0);
	            }	        	
	    }
	    
	    if (indicesPalets.isEmpty()) {
	    	System.out.println("Aucun palets detecte.");
	    }
	    
	    return indicesPalets;
	}
	
	
	
	/*
	 * Retourne une liste composée de listes des indices (entiers) de chaque palet.
	 * Prend comme paramètre une liste des palets potentiels.
	 */
	public ArrayList<ArrayList<Integer>> palets(List<Integer> indices) {
		
	    ArrayList<ArrayList<Integer>> palets = new ArrayList<>();
	    ArrayList<Integer> palet = new ArrayList<>();

	    for (int i = 0; i < indices.size(); i++) {
	        int courant = indices.get(i);
	        int precedent = (i > 0) ? indices.get(i - 1) : -1;

	        if (courant == precedent + 1) {
	            palet.add(courant);
	        } 
	        
	        else {
	            if (palet.size() > 0) {
	                if (!(palet.size() == 1 && palet.get(0) == 0)) {
	                    if (palet.size() <= 3) {
	                        palets.add(new ArrayList<>(palet));
	                    }
	                }
	                palet.clear();
	            }
	            palet.add(courant);
	        }
	    }

	    if (palet.size() > 0 && palet.size() <= 3 && !(palet.size() == 1 && palet.get(0) == 0)) {
	        palets.add(new ArrayList<>(palet));
	        palet.clear();
	    }

	    return palets;
	}

	
	
	/*
	 * Retourne un float contenant la distance en mètre du palet le plus proche du robot.
	 * Prend comme premier paramètre une liste composée de listes des indices (entiers) de chaque palets,
	 * et comme deuxième paramètre une HashMap contenant les indices (entiers) et les distances (float) en mètres.
	 */
	public float paletLePlusProche(ArrayList<ArrayList<Integer>> palets, Map<Integer, Float> map) {
		
		ArrayList<Integer> palet = new ArrayList<>();
		float distanceMin = 3;
	    
	    for(int i=0; i<palets.size();i++) {
	    	palet = palets.get(i);
	    	
	    	if(palet.size()==0) {
	    		continue;
	    	}
	    	
	    	float sum = 0;
	    	int indiceSum = 0;
	        for (int j=0; j< palet.size(); j++) {
	              sum += map.get(palet.get(j));
	              indiceSum+=palet.get(j);
	        }
	        
	        if(sum/palet.size()<distanceMin) {
	        	distanceMin=sum/palet.size();
	        }
	    }
	    
	    return distanceMin;
	}

	
	
	/*
	 * Méthode qui détermine le palets le plus proche du robot et son orientation.
	 */
	public void trouverPalet() {
		
		if (distanceVide());
		
	    Map<Integer, Float> distanceMap = indicesPalets();
	    
	    List<Integer> indicesFiltres = indicesFiltres(distanceMap);
	    
	    List<Integer> indicesPalets = indicesPalets(distanceMap,indicesFiltres);
	    
	    ArrayList<ArrayList<Integer>> palets = palets(indicesPalets);

	    Float distanceMin = paletLePlusProche(palets,distanceMap);
	    
	    System.out.println("Nombre de mesure: " + distanceMap.size());
	    System.out.println("Nombre d'indices filtres: " + indicesFiltres.size());
	    System.out.println("Indices palets: " + indicesPalets);
	    System.out.println("Nombre de palets visibles: " + palets.size());
	    System.out.println("Liste de listes des indices des palets: " + palets);
	    System.out.println("Distance du palet le plus proche: " + distanceMin);
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

   



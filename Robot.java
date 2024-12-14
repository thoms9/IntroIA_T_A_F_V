import java.util.ArrayList;
import java.util.List;

import lejos.hardware.Button;
import lejos.utility.Delay;

	/**
	 * Classe Robot qui représente le point d'entrée du programme.
	 * 
	 * Cette classe initialise les différents composants du robot, tels que les
	 * actionneurs et les capteurs, puis exécute la logique principale.
	 * 
	 * Elle utilise :
	 * - La classe Actionneurs pour contrôler les moteurs du robot.
	 * - La classe Sensors pour interagir avec les capteurs.
	 * 
	 * @author Alexander OSTLE, Thomas BEGOTTI, Victor CHARREYRON
	 */
	public class Robot {
	
	private Sensors sensors;
	private Actionneurs actionneurs;
	private List<Float> distances;
	private static double orientation;
	private static Float distancePalet;
	private static int etat;
	
	
	
	/**
	 * Constructeur qui initialise les capteurs, les 
	 * actionneurs, la liste pour les distances, l'état
	 * initial et ouvre les pinces du robot pour démarer 
	 * plus rapidement.
	 * 
	 * @see Sensors#Sensors()
	 * @see Actionneurs#Actionneurs()
	 * @see Actionneurs#ouvrirPinces(int, boolean)
	 */
	public Robot() {
        sensors = new Sensors();
        actionneurs = new Actionneurs();
        distances = new ArrayList<>();
        actionneurs.ouvrirPinces(700,true);
        etat = -1;

	}
	
	
	
	/**
	 * Méthode spécifique pour gerer le premier palet;
	 * augmente la vitesse du robot au début puisque le premier
	 * palet vaut plus de points, à la fin réduit cet vitesse pour
	 * favoriser la précision à la vitesse dans le reste du programme.
	 * Le robot prend le premier palet et le dépose
	 * dans la zone d’en-but adverse, il fini par se recentrer.
	 * L'orientation dépend du choix fait au début du lancement du
	 * programme principal.
	 * 
	 * @see Actionneurs#setLinearSpeed(double)
	 * @see #prendrePremierPalet()
	 * @see Actionneurs#tourner(double, boolean)
	 * @see Actionneurs#avancer(double, boolean)
	 * @see #allerALigneBlanche()
	 * @see #deposer()
	 * @see #recentrer()
	 */
	public void premierPalet() {
		actionneurs.setLinearSpeed(250);
		prendrePremierPalet();
		if(orientation == 0) {
			actionneurs.tourner(30,false);
			actionneurs.avancer(350,false);
			actionneurs.tourner(-30, false);
		}
		if(orientation == 360) {
			actionneurs.tourner(-30,false);
			actionneurs.avancer(350,false);
			actionneurs.tourner(30, false);
		}
		allerALigneBlanche();
		deposer();
		recentrer();
		actionneurs.setLinearSpeed(150);
		
	}
	
	
	
	/**
	 * Méthode pour attraper le premier palet; le robot 
	 * avance de 80cm, si il detecte un contact avant il 
	 * ferme ses pinces et s'arrète, sinon il le fait à 80cm.
	 * 
	 * @see Actionneurs#avancer(double, boolean)
	 * @see Actionneurs#isMoving()
	 * @see Sensors#getTouch()
	 * @see Actionneurs#fermerPinces(int, boolean)
	 * @see Actionneurs#arreter()
	 */
	public void prendrePremierPalet() {
		boolean palet = false;
        actionneurs.avancer(800, true);
        
        while(actionneurs.isMoving()) {
        	 if (sensors.getTouch()) {
        		 actionneurs.fermerPinces(700,false);
             	 actionneurs.arreter();
             	 palet = true;
             }
        	
        }
        if(!palet) actionneurs.fermerPinces(700, false);
	}
	
	
	
	/**
	 * Méthode pour faire avancer le robot de la longeur de la table (3m), ou
	 * jusqu'à la zone d’en-but adverse, le robot s'arrète si il detecte la
	 * ligne blanche et si la distance devant lui est inférieur a 30cm.
	 * Le robot verifie la distance devant tous les 150ms.
	 * 
	 * @see Actionneurs#avancer(double, boolean)
	 * @see Actionneurs#isMoving()
	 * @see Delay#msDelay(long)
	 * @see #distanceDevant()
	 * @see Sensors#getColor()
	 * @see Actionneurs#arreter()
	 */
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
	
	
	
	/**
	 * Méthode qui retourne la distance devant le robot capté
	 * avec le capteur d'ultrason.
	 * 
	 * @return float la distance en mètres.
	 * 
	 * @see Sensors#getDistance()
	 */
	public Float distanceDevant() {
		float[] sample = new float[sensors.getDistance().sampleSize()];
		sensors.getDistance().fetchSample(sample, 0);
		return sample[0];
	}
	
	
	
	/**
	 * Méthode pour deposer un palet; le robot ouvre ses pinces
	 * recule de 10cm puis ferme ses pinces et attend
	 * que ses pinces soient fermées pour continuer.
	 * 
	 * @see Actionneurs#ouvrirPinces(int, boolean)
	 * @see Actionneurs#reculer(double, boolean)
	 * @see Actionneurs#fermerPinces(int, boolean)
	 */
	public void deposer() {
		actionneurs.ouvrirPinces(700, true);
		actionneurs.reculer(100, true);
		actionneurs.fermerPinces(700, false);
	}
	
	
	
	/**
	 * Méthode pour recentrer le robot sur la table;
	 * regarde l'orientation du robot et se tourne pour
	 * regarder le mur le plus proche, recule en verifiant
	 * la distance devant, s'arrète lorsque cette distance
	 * est supérieur à 85cm puis se tourne en direction de
	 * la zone d’en-but adverse.
	 * 
	 * @see Actionneurs#tourner(double, boolean)
	 * @see Actionneurs#reculer(double, boolean)
	 * @see Actionneurs#isMoving()
	 * @see Delay#msDelay(long)
	 * @see #distanceDevant()
	 * @see Actionneurs#arreter()
	 */
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
	
	
	
	/**
	 * Méthode pour la détéction d'un palet; avant de
	 * rechercher elle réinitialise la distancePalet
	 * à 80cm pour pouvoir ignorer tout palet plus
	 * éloigné que 80cm, favorisant la précision du calcul
	 * de l'angle correspondant au palet.
	 * 
	 * @see #rechercher()
	 * @see #indicesFiltre()
	 * @see #paletsFiltres(List)
	 * @see #paletLePlusProche(List)
	 */
	public void trouverPalet() {
		distancePalet = 0.80f;
		rechercher();
		paletLePlusProche(paletsFiltres(indicesFiltre()));
	}
	
	
	
	/**
	 * Méthode pour que le robot fasse un tour sur lui même;
	 * vide la liste des distaces en attribut puis 
	 * prend les distances devant le robot en mètres tous les 30ms
	 * et les ajoutent à la liste distances en attribut.
	 * 
	 * @see Sensors#getDistance()
	 * @see Actionneurs#tourner(double, boolean)
	 * @see Actionneurs#isMoving()
	 * @see Delay#msDelay(long)
	 */
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
	
	
	
	/**
	 * Méthode qui filtre les indices des distances; elle ne prend
	 * en compte que les indices des distances inférieurs à 2m et supérieurs
	 * à 30cm. Elle ne prend en compte aussi uniquement les indices des distances
	 * si l'angle est compris entre 90° et 270°, ignorant ainsi tout ce qui
	 * se trouve devant le robot.
	 * 
	 * @return List d'entier des indices valides de distances.
	 */
	public List<Integer> indicesFiltre() {
        List<Integer> indicesValides = new ArrayList<Integer>();
        double angle = (double)360/distances.size();
        for (int i = 0; i < distances.size(); i++) {
            if (distances.get(i) < 2.0f && distances.get(i) > 0.3f &&
            		i>(90/angle) && i<(270/angle)) {
                indicesValides.add(i);
            }
        }
        return indicesValides;
    }
	
	
	
	/**
	 * Méthode qui prend une liste valide d'indices, regarde si
	 * ces indices sont successifs, compare leurs distances,
	 * si la distance est inférieur a un seuil "-différence"
	 * qui correspond au début potentiel d'un palet ajoute cet indice 
	 * a une liste, puis regarde si les prochains indices ont une différence 
	 * inférieur au seuil de "tolerancePalet" et ajoute l'incice 
	 * courrent a la même liste, ensuite regarde si la distance est 
	 * supérieur a un seuil "différence" correspondant à la fin potentiel d'un 
	 * palet et l'ajoute a la liste. Si il detecte 2
	 * début de palet il vide la liste, ou si il trouve deux indices
	 * qui ne sont pas successif sans avoir detecter la fin d'un palet
	 * il vide la liste. Lorsqu'il detecte un debut et une fin il
	 * vérifie si la taille du palet potentiel correspond à la taille
	 * moyenne d'un palet et si oui l'ajoute a la liste de liste comme
	 * un palet, puis vide la liste courrante.
	 * 
	 * @param indices liste d'entier d'indices des distances valides
	 * 
	 * @return List de List d'entier des indices des palets
	 */
	public List<List<Integer>> paletsFiltres(List<Integer> indices) {
        List<List<Integer>> palets = new ArrayList<List<Integer>>();
        List<Integer> paletActuel = new ArrayList<Integer>();
        final double difference = 0.5;
        final double tolerancePalet = 0.05;
        final int tailleMinPalet = 3;
        final int tailleMaxPalet = 6;

        for (int i = 1; i < indices.size(); i++) {
            int indiceActuel = indices.get(i);
            int indicePrecedent = indices.get(i - 1);
            
            if(indiceActuel != indicePrecedent-1) {
            	paletActuel.clear();
            }

            double distanceActuel = distances.get(indiceActuel);
            double distancePrecedent = distances.get(indicePrecedent);

            if (distanceActuel - distancePrecedent < -difference) {
            	paletActuel.clear();
                paletActuel.add(indiceActuel);
                
            } else if(Math.abs(distanceActuel - distancePrecedent) < tolerancePalet){
                paletActuel.add(indiceActuel);
            } else if (distanceActuel - distancePrecedent > difference) {
            	if(!paletActuel.isEmpty()) {
            		if(paletActuel.size()>=tailleMinPalet && 
            				paletActuel.size()<=tailleMaxPalet) {
            			palets.add(paletActuel);
            		}
            		paletActuel.clear();
            	}
            }
        }
        if(paletActuel.size()>=tailleMinPalet && paletActuel.size()<=tailleMaxPalet) {
			palets.add(paletActuel);
		}
		paletActuel.clear();

        return palets;
    }
	
	
	
	/**
	 * Méthode qui à partir d'une liste de liste de palet trouve le palet le plus 
	 * proche du robot; trouve l'indice du milieu des palets, compare leurs distances 
	 * et met à jour la distance du palet et l'orientation en attribut que va devoir 
	 * prendrele robot.
	 * 
	 * @param palets liste de listes d'entier des indices de chaque palet
	 */
	public void paletLePlusProche(List<List<Integer>> palets) {
    	double angle = (double)360/distances.size();
        for (List<Integer> palet : palets) {
        	int sumIndice = 0;
            int indiceMilieu = 0;
            Float distanceMoyen = 0f;
            for(int i=0; i<palet.size(); i++) {
            	sumIndice+=palet.get(i); 
            }
            indiceMilieu = sumIndice/palet.size();
            distanceMoyen = distances.get(indiceMilieu);
            if(distanceMoyen<distancePalet) {
            	distancePalet = distanceMoyen;
            	orientation = indiceMilieu*angle;
            }
        }
    }
	
	
	
	
	/**
	 * Méthode qui gère la manipulation d'un palet; le robot
	 * se tourne vers le palet le prend, se déplace vers le mur,
	 * se tourne vers la zone d’en-but adverse, avance jusqu'à
	 * la zone d’en-but adverse dépose le paler, se recentre et il 
	 * finit par réinitialisé l'orientation pour retrouver 0°.
	 * 
	 * @see #tournerVersPalet()
	 * @see #avancerVersPalet()
	 * @see #prendrePalet()
	 * @see #deplacerVersMur()
	 * @see #allerALigneBlanche()
	 * @see #deposer()
	 * @see #recentrer()
	 * @see #resetOrientation()
	 */
	public void manipulerPalet() {
		tournerVersPalet();
		avancerVersPalet();
		prendrePalet();
		deplacerVersMur();
		allerALigneBlanche();
		deposer();
		recentrer();
		resetOrientation();
	}
	
	
	
	/**
	 * Méthode qui tourne le robot vers l'orientation
	 * qui est en atribut.
	 * 
	 * @see Actionneurs#tourner(double, boolean)
	 */
	public void tournerVersPalet() {
		actionneurs.tourner(orientation, false);
	}
	
	
	
	/**
	 * Méthode qui fait avancer le robot de la longeur
	 * de la table (3m) et s'arrète quand la distance de devant
	 * augmente (le robot ne detecte plus le palet car il est trop proche),
	 * ou lorsque la distance est inférieur à 25cm (le robot a detecté un mur).
	 * 
	 * @see Actionneurs#avancer(double, boolean)
	 * @see Actionneurs#isMoving()
	 * @see Delay#msDelay(long)
	 * @see #distanceDevant()
	 * @see Actionneurs#arreter()
	 */
	public void avancerVersPalet() {
		actionneurs.avancer(3000, true);
		
		while(actionneurs.isMoving()) {
			Delay.msDelay(250);
			float distance = distanceDevant();
			if(distance <= distancePalet || distance < 0.30) {
				distancePalet = distance;
			}
			else {
				actionneurs.arreter();
			}
		}       
	}
	
	
	
	/**
	 * Méthode pour attraper un palet; le robot ouvre ses
	 * pinces, avance de 60cm, s'il detecte un contact
	 * avant il ferme ses pinces et s'arrète sinon il le 
	 * fait à 60cm.
	 * 
	 * @see Actionneurs#ouvrirPinces(int, boolean)
	 * @see Actionneurs#avancer(double, boolean)
	 * @see Actionneurs#isMoving()
	 * @see Sensors#getTouch()
	 * @see Actionneurs#fermerPinces(int, boolean)
	 * @see Actionneurs#arreter()
	 */
	public void prendrePalet() {
		boolean palet = false;
		actionneurs.ouvrirPinces(700, true);
        actionneurs.avancer(600, true);
        
        while(actionneurs.isMoving()) {
        	 if (sensors.getTouch()) {
        		 actionneurs.fermerPinces(700,false);
             	 actionneurs.arreter();
             	 palet = true;
             }
        	
        }
        if(!palet) actionneurs.fermerPinces(700, false);
	}
	
	
	
	/**
	 * Méthode pour déplacer le robot vers le mur; le robot se
	 * tourne en fonction de l'orientation en attribut pour aller
	 * vers le mur le plus proche, il avance tant que la
	 * distance est supérieur à 20cm puis le robot se tourne
	 * pour regarder la zone d’en-but adverse. Il se tourne
	 * de plus 90° pour eviter les colisions avec les murs et
	 * prendre en compte le poids supplémentaire d'un palet.
	 * 
	 * @see Actionneurs#tourner(double, boolean)
	 * @see Actionneurs#avancer(double, boolean)
	 * @see Actionneurs#isMoving()
	 * @see Delay#msDelay(long)
	 * @see #distanceDevant()
	 * @see Actionneurs#arreter()
	 */
	public void deplacerVersMur() {
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
	
	
	
	/**
	 * Méthode pour réinitialisé l'orientation du robot à 0°;
	 * le robot vas jusqu'à la ligne blanche lance une recherche
	 * puis se tourne de l'angle équivalent a la distance la plus
	 * petite qui représente le mur, puis fini par réinitialisé
	 * l'orientation à 0.
	 * 
	 * @see #allerALigneBlanche()
	 * @see #rechercher()
	 * @see #distanceMin()
	 * @see Actionneurs#tourner(double, boolean)
	 */
	public void resetOrientation() {
		allerALigneBlanche();
		rechercher();
		distanceMin();
		actionneurs.tourner(orientation, false);
		orientation = 0;
	}
	
	
	
	/**
	 * Méthode qui recherche la distance minimum de l'attribut
	 * distances, il ignore toutes les distances qui ne sont pas 
	 * inférieur à 70° et supérieur à 290°, il ne prend donc en
	 * compte que les distances devant le robot.
	 */
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
	
	
	
	/**
	 * Méthode qui arrète les actionneurs et les senseurs du robot.
	 * 
	 * @see Actionneurs#arreter()
	 * @see Sensors#closeSensors()
	 */
	public void stop() {
    	actionneurs.arreter();
    	sensors.closeSensors();
    }
	
	
	
	/**
	 * Méthode principale qui initialise le robot et 
	 * gère le switch case des différent état du robot.
	 * 
	 * @param args
	 * 
	 * @see #RobotF()
	 * @see Actionneurs#setRotationSpeed(double)
	 * @see Button#waitForAnyPress()
	 * @see #premierPalet()
	 * @see #trouverPalet()
	 * @see Actionneurs#reculer(double, boolean)
	 * @see #manipulerPalet()
	 * @see #stop()
	 */
	public static void main(String[] args) {
		
		// Initialise le robot
		Robot R = new Robot();
		
		
		// Vitesse de rotation du robot durant tous le programme.
		R.actionneurs.setRotationSpeed(75);
		
		while(etat != 3) {
		
			switch(etat) {
			
				// Attent que l'utilisateur appuie sur un boutton
				// puis passe à l'état 0. Si il veut commencer à gauche
				// il appui sur gauche et droite si il veut commencer à droite
			    case -1:
			    	if(Button.waitForAnyPress()==Button.ID_RIGHT) {
			    		orientation = 0;
			    		etat = 0;
			    		break;
			    	}
			    	if(Button.waitForAnyPress()==Button.ID_LEFT) {
			    		orientation = 360;
			    		etat = 0;
			    		break;
			    	}
			    	
			
			    // Execute la méthode premierPalet puis passe
			    // à l'état 1.
				case 0:
					R.premierPalet();
					etat = 1;
					break;
					
				// Execute la méthode trouverPalet, s'il ne
				// trouve rien recule et recomence, puis passe
				// à l'état 2.
				case 1:
					R.trouverPalet();
					if(distancePalet == 0.80f) {
						R.actionneurs.reculer(400, false);
						break;
					}
					etat = 2;
					break;
				
				// Execute la méthode manipulerPalet puis passe
				// à l'état 1.
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

package robot;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

	/**
	 * Classe qui gère l'initialisation et l'accès aux capteurs du robot LEGO EV3.
	 * 
	 * Cette classe permet de récupérer les informations des capteurs de distance,
	 * de contact et de couleur. Elle est utilisée pour obtenir des données 
	 * environnementales, telles que la distance à un obstacle, le statut d'un 
	 * contact tactile et la couleur détectée.
	 * 
	 * Relations avec d'autres modules :
	 * - Cette classe est utilisée par le module Actionneurs pour ajuster 
	 * les mouvements du robot en fonction des capteurs.
	 * 
	 * Modules utilisés par ce module :
	 * - LeJOS (pour les capteurs spécifiques comme EV3UltrasonicSensor,
	 * EV3TouchSensor et EV3ColorSensor).
	 * 
	 * Modules utilisant ce module :
	 * - Main : Utilise les méthodes de Sensors pour effectuer des actions 
	 * spécifiques sur le robot.
	 * 
	 * Définitions de types :
	 * - `EV3UltrasonicSensor`, `EV3TouchSensor`, `EV3ColorSensor` : capteurs utilisés.
	 * - `SampleProvider` : Fournisseur d'échantillons utilisé pour obtenir 
	 * des mesures des capteurs.
	 * - `float[]` : Tableau pour stocker les échantillons de capteurs (distance,
	 * contact, couleur).
	 * 
	 * Procédures externes :
	 * - `getDistance()` : Retourne un fournisseur d'échantillons pour le capteur ultrasonique.
	 * - `getTouch()` : Retourne un booléen pour vérifier si le capteur tactile est activé.
	 * - `getColor()` : Retourne l'identifiant de la couleur détectée par le capteur de couleur.
	 * - `closeSensors()` : Ferme tous les capteurs et libère les ressources associées.
	 * 
	 * Variables externes :
	 * - Aucune variable publique, les capteurs sont gérés en interne.
	 * 
	 * @author Alexander OSTLE, Thomas BEGOTTI, Victor CHARREYRON
	 */
	public class Sensors {

    private EV3UltrasonicSensor usSensor; 
    private EV3TouchSensor touchSensor;
    private EV3ColorSensor colorSensor;

    private SampleProvider distanceMode; 
    private SampleProvider touchMode;
    private int colorMode;
    
    

    /**
     * Constructeur de la classe Sensors. Initialise chaque capteur 
     * avec les ports respectifs et obtient les fournisseurs d'échantillon.
     */
    public Sensors() {
        usSensor = new EV3UltrasonicSensor(SensorPort.S1);
        distanceMode = usSensor.getDistanceMode();

        touchSensor = new EV3TouchSensor(SensorPort.S3);
        touchMode = touchSensor.getTouchMode();

        colorSensor = new EV3ColorSensor(SensorPort.S2);
        colorMode = colorSensor.getColorID();
       
    }

    
    
    /**
     * Ferme tous les capteurs pour libérer les ressources matérielles associées.
     * Cette méthode doit être appelée lorsque les capteurs ne sont plus utilisés 
     * pour éviter les fuites de mémoire ou des conflits matériels.
     */
    public void closeSensors() {
        if (usSensor != null) {
            usSensor.close();
        }
        if (touchSensor != null) {
            touchSensor.close();
        }
        if (colorSensor != null) {
            colorSensor.close();
        }
    }
    
    
    
    /**
     * Retourne le fournisseur d'échantillons pour le capteur de distance ultrasonique.
     * 
     * @return SampleProvider pour la distance (en mètres).
     */
    public SampleProvider getDistance() {
        return distanceMode;
    }
    
    
    
    /**
     * Retourne vrai si le capteur est en contact avec un objet
     * 
     * @return boolean pour le contact.
     */
    public boolean getTouch() {
    	float[] sample = new float[touchMode.sampleSize()];
    	touchMode.fetchSample(sample, 0);
    	return (sample[0]==1);
    }
    
    
    
    /**
     * Retourne un entier représentatif de la couleur pour le capteur de couleur.
     * 
     * @return entier pour la couleur.
     */
    public int getColor() {
         return colorMode;
    }
    
    
    
    /**
     * Affiche un diagnostic des capteurs connectés. 
     * Vérifie si chaque capteur est correctement initialisé et prêt à être utilisé.
     * Le résultat est affiché sur la console sous la forme :
     * - "Ultrasonique : OK" ou "Ultrasonique : Erreur"
     * - "Contact : OK" ou "Contact : Erreur"
     * - "Couleur : OK" ou "Couleur : Erreur"
     */
    public void diagnostic() {
        System.out.println("Ultrasonique : " + (usSensor != null ? "OK" : "Non détecté"));
        System.out.println("Contact : " + (touchSensor != null ? "OK" : "Non détecté"));
        System.out.println("Couleur : " + (colorSensor != null ? "OK" : "Non détecté"));
    }
    
    
}

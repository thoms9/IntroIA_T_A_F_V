import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

/**
 * Classe Sensors qui gère l'initialisation et l'accès aux capteurs connectés au robot LEGO EV3.
 */
public class Sensors {

    // Déclaration des capteurs EV3 : capteur ultrasonique, capteur tactile et capteur de couleur.
    private EV3UltrasonicSensor usSensor; 
    private EV3TouchSensor touchSensor;
    private EV3ColorSensor colorSensor;

    // Fournisseurs d'échantillons pour chaque capteur. Ils sont utilisés pour lire les valeurs des capteurs.
    private SampleProvider distanceMode; 
    private SampleProvider touchMode;
    private SampleProvider colorMode;

    /**
     * Constructeur de la classe Sensors. Initialise chaque capteur avec les ports respectifs.
     */
    public Sensors() {
        // Initialisation du capteur ultrasonique sur le port S1
        usSensor = new EV3UltrasonicSensor(SensorPort.S1);
        // Obtention du fournisseur d'échantillons pour la mesure de la distance
        distanceMode = usSensor.getDistanceMode();

        // Initialisation du capteur tactile sur le port S3
        touchSensor = new EV3TouchSensor(SensorPort.S3);
        // Obtention du fournisseur d'échantillons pour la détection de touche
        touchMode = touchSensor.getTouchMode();

        // Initialisation du capteur de couleur sur le port S2
        colorSensor = new EV3ColorSensor(SensorPort.S2);
        // Obtention du fournisseur d'échantillons pour la détection de la couleur
        colorMode = colorSensor.getColorIDMode();
    }

    /**
     * Méthode pour fermer tous les capteurs.
     */
    public void closeSensors() {
        // Vérifie si le capteur ultrasonique est initialisé avant de le fermer
        if (usSensor != null) {
            usSensor.close();
        }
        // Vérifie si le capteur tactile est initialisé avant de le fermer
        if (touchSensor != null) {
            touchSensor.close();
        }
        // Vérifie si le capteur de couleur est initialisé avant de le fermer
        if (colorSensor != null) {
            colorSensor.close();
        }
    }
    
    /**
     * Retourne le fournisseur d'échantillons pour le capteur de distance ultrasonique.
     * @return SampleProvider pour la distance.
     */
    public SampleProvider getDistance() {
        return distanceMode;
    }
    
    /**
     * Retourne vrai si le capteur est en contact avec un objet
     * @return boolean pour le contact.
     */
    public boolean getTouch() {
    	// Create an array to store the sample value
    	float[] sample = new float[touchMode.sampleSize()];

    	// Fetch the sample from the touch sensor
    	touchMode.fetchSample(sample, 0);
    	
    	if(sample[0]==1) return true;
    	else return false;
    }
    
    /**
     * Retourne le fournisseur d'échantillons pour le capteur de couleur.
     * @return SampleProvider pour la couleur.
     */
    public SampleProvider getColor() {
        return colorMode;
    }
    
}

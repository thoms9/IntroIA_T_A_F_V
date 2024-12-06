import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

	/**
	 * Classe Sensors qui gère l'initialisation et l'accès aux capteurs connectés au robot LEGO EV3.
	 */
	public class Sensors {

    private EV3UltrasonicSensor usSensor; 
    private EV3TouchSensor touchSensor;
    private EV3ColorSensor colorSensor;

    private SampleProvider distanceMode; 
    private SampleProvider touchMode;
    private int colorMode;
    
    

    /**
     * Constructeur de la classe Sensors. Initialise chaque capteur avec les ports respectifs
     * et obtient les fournisseurs d'échantillon.
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
     * Méthode pour fermer tous les capteurs.
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
     * @return SampleProvider pour la distance (en mètres).
     */
    public SampleProvider getDistance() {
        return distanceMode;
    }
    
    
    
    /**
     * Retourne vrai si le capteur est en contact avec un objet
     * @return boolean pour le contact.
     */
    public boolean getTouch() {
    	float[] sample = new float[touchMode.sampleSize()];
    	touchMode.fetchSample(sample, 0);
    	return (sample[0]==1);
    }
    
    
    
    /**
     * Retourne un entier représentatif de la couleur pour le capteur de couleur.
     * @return entier pour la couleur.
     */
    public int getColor() {
         return colorMode;
    }
    
}

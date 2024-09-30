	import lejos.hardware.port.SensorPort;
	import lejos.hardware.sensor.EV3UltrasonicSensor;
	import lejos.hardware.sensor.EV3TouchSensor;
	import lejos.hardware.sensor.EV3ColorSensor;
	import lejos.robotics.SampleProvider;

public class Sensors {


	    private EV3UltrasonicSensor usSensor;
	    private EV3TouchSensor touchSensor;
	    private EV3ColorSensor colorSensor;

	    private SampleProvider distanceMode;
	    private SampleProvider touchMode;
	    private SampleProvider colorMode;

	    public Sensors() {
	        usSensor = new EV3UltrasonicSensor(SensorPort.S1);
	        distanceMode = usSensor.getDistanceMode();

	        touchSensor = new EV3TouchSensor(SensorPort.S3);
	        touchMode = touchSensor.getTouchMode();

	        colorSensor = new EV3ColorSensor(SensorPort.S2);
	        colorMode = colorSensor.getColorIDMode();
	    }

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
	    
	    public SampleProvider getDistance() {
	    	return distanceMode;
	    }
	    
	    public SampleProvider getTouch() {
	    	return touchMode;
	    }
	    
	    public SampleProvider getColor() {
	    	return colorMode;
	    }
	
}

package my.project.MySensors;

import java.util.List;

import android.content.Context;
import android.opengl.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorController implements SensorEventListener 
{
	private SensorManager mSensorManager = null;
	private Boolean sensorRegisteredFlag = false;
	private final double RADIANS_TO_DEGREES = 180/Math.PI;
	private final float[] sZVector = { 0, 0, 1, 1 };
	private float R[] = new float[16];
	private float remapR[] = new float[16];
	private float remapR_inv[] = new float[16];
    private float AccelerometerValues_last[] = new float[3];
    private float MagneticFieldValues_last[] = new float[3];
    private float orientationValues[] = new float[3];
	private float orientationVector[] = new float[4];
	private float azimuthVector[] = new float[4];
    boolean bHaveAccelerometer = false;
    boolean bHaveMagneticField = false;
	private float orientation;//up direction
	private float azimuth;//aim to north
	private float pitch;
	
	public SensorController(Context context)
	{
		mSensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        registerSensor();
	}

	public boolean getNowOrientation(float [] retValues) 
	{
		retValues[0] = pitch;
		retValues[1] = orientation;
		retValues[2] = azimuth;
		return true;
	}	
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
		{
			MagneticFieldValues_last[0] = event.values[0];
			MagneticFieldValues_last[1] = event.values[1];
			MagneticFieldValues_last[2] = event.values[2];
			
			bHaveMagneticField = true;
		}
		if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
		{
			AccelerometerValues_last[0] = event.values[0];
			AccelerometerValues_last[1] = event.values[1];
			AccelerometerValues_last[2] = event.values[2];
			
			bHaveAccelerometer = true;
		}
		if(bHaveMagneticField && bHaveAccelerometer)
		{
			if(SensorManager.getRotationMatrix(R, null, AccelerometerValues_last, MagneticFieldValues_last))
			{
				SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, remapR);
				SensorManager.getOrientation(remapR, orientationValues);
				
				Matrix.multiplyMV(orientationVector, 0, remapR, 0, sZVector, 0);
				pitch = (float) (-Math.atan2(orientationVector[1],
							orientationVector[2]) * RADIANS_TO_DEGREES);
				
				
				Matrix.multiplyMV(orientationVector, 0, remapR, 0, sZVector, 0);
				orientation = (float) (-Math.atan2(orientationVector[0],
							orientationVector[1]) * RADIANS_TO_DEGREES);
				
				
				Matrix.invertM(remapR_inv, 0, remapR, 0);
				Matrix.multiplyMV(azimuthVector, 0, remapR_inv, 0, sZVector, 0);
				azimuth = (float) (180 + Math.atan2(azimuthVector[0],
							azimuthVector[1]) * RADIANS_TO_DEGREES);
			}
		}
	}
	
	public void onResume()
    {
		registerSensor();
	}
	
	public void onPause()
    {
    	if(mSensorManager != null && sensorRegisteredFlag)
    	{
    		mSensorManager.unregisterListener(this);
    	}
    }

	private void registerSensor()
	{
	   	if(mSensorManager != null)
	   	{
	    	List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
	    	if(sensors.size() > 0)
	    	{
	    		Sensor sensor = sensors.get(0);
	    		//if(!mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL))
	    		if(!mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME))
	    		{
	    			return;
	    		}
	    	}
	    	sensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
	    	if(sensors.size() > 0)
	    	{
	    		Sensor sensor = sensors.get(0);
	    		//if(!mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL))
	    		if(!mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME))
	    		{
	    			return;
	    		}
	    	}
	    	sensorRegisteredFlag = true;
 		}
	
	}
	
}

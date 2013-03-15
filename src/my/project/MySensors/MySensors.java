package my.project.MySensors;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

public class MySensors extends Activity 
{
	private SensorController MySensors = null;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	private TextView NowText = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        MySensors = new SensorController(this);
        
        NowText = (TextView)findViewById(R.id.TextView01);
        
        mHandler.postDelayed(ReadSensorValues, 300);
    }
    
    private Runnable ReadSensorValues = new Runnable() 
    {
    	float orientationValues[] = new float[3];
        public void run() 
        {
        	MySensors.getNowOrientation(orientationValues);
        	
        	NowText.setText("pitch:" + orientationValues[0] + "\n" +
        					"orientation:" + orientationValues[1] + "\n" +
        					"azimuth:" + orientationValues[2]);
        	
        	mHandler.postDelayed(ReadSensorValues, 100);
        }        
    };
    protected void onResume()
    {
    	super.onResume();
    	MySensors.onResume();
    }   
   
    protected void onPause()
    {
		super.onPause();
    	MySensors.onPause();
    }    
}
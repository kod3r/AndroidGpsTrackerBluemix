package com.androidgpstrackerbluemix;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	final String RESTSERVICEURI = "http://nodejsgpstrackerrestservice.mybluemix.net"; // Bluemix Route
	Button btnLocation;
	TextView tvLink;
	TextView tvLatitude;
	TextView tvLongitude;
	TextView tvDeviceId;
	GPSTracker gps;
	Timer timer;
	TimerTask timerTask;
	boolean isOn = false;
	Context context;
	Http http;
	String httpResponse;
	String deviceId;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btnLocation = (Button) findViewById(R.id.btnLocation);
		tvLatitude = (TextView) findViewById(R.id.tVLatitude);
		tvLongitude = (TextView) findViewById(R.id.tVLongtitude);
		tvLink = (TextView) findViewById(R.id.tvLink);
		tvLink.setMovementMethod(LinkMovementMethod.getInstance());
		tvDeviceId = (TextView) findViewById(R.id.tvDeviceId);
		context = MainActivity.this;
		gps = new GPSTracker(context);
		http = new Http();
		TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		deviceId = tManager.getDeviceId();
		tvDeviceId.setText(deviceId);
		tvLink.setText(Html.fromHtml("<a href=\"" + RESTSERVICEURI + "/?deviceid=" + deviceId + "\">See your location on the web</a>"));
		
		// Button Location which acivates/deactivates the timer
		btnLocation.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try
				{
					if (!isOn)
					{
						if(gps.canGetLocation)
						{
							gps.getLocation();
							if (timerTask != null)
							{
								timerTask = null;
							}
							if (timer != null)
							{
								timer = null;
							}
							timer = new Timer();
							timerTask = createTimerTask();
							timer.schedule(timerTask, 0, 10000);
							isOn = true;
							btnLocation.setText("Track Location: ON");
						}
						else
						{
							gps.showSettingsAlert();
							gps.getLocation();
						}
					}
					else
					{
						timerTask.cancel();
						timer.purge();
						timer.cancel();
						isOn = false;
						btnLocation.setText("Track Location: OFF");
						tvLatitude.setText("");
						tvLongitude.setText("");
					}
				}
				catch(Exception e)
				{
					Toast.makeText(context, "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	// TimerTask, which gets the location and sends it to the node.js server
	private TimerTask createTimerTask()
	{
		TimerTask t = new TimerTask()
		{
			public void run()
			{
				// send latitude and longitude to REST service
				httpResponse = http.get(RESTSERVICEURI + "/?deviceid=" + deviceId + "&latitude=" + String.valueOf(gps.getLatitude()) + "&longitude=" + String.valueOf(gps.getLongitude()));
				
				// set latitude and longitude on the GUI
				MainActivity.this.runOnUiThread(new Runnable()
				{
					public void run()
					{
						tvLatitude.setText(String.valueOf(gps.getLatitude()));
						tvLongitude.setText(String.valueOf(gps.getLongitude()));
					}
				});
			}
		};
		
		return t;
	}
}

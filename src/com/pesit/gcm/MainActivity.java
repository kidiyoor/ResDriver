package com.pesit.gcm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;


import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends Activity {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final String TAG = null;
	 Context context;	
	  ProgressDialog progress;
	 public static final String EXTRA_MESSAGE = "message";
	    public static final String PROPERTY_REG_ID = "registration_id";
	    private static final String PROPERTY_APP_VERSION = "appVersion";
//project number i got from console
	    String SENDER_ID = "477538242754";
	    
	    
	    GoogleCloudMessaging gcm;
	    AtomicInteger msgId = new AtomicInteger();
	    SharedPreferences prefs;

	    String regid;
	    String msg;
	  TextView mDisplay;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		   System.out.println("Inside onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		int playFlag=0;
		mDisplay=(TextView)findViewById(R.id.mDisplay);
		 //context = getApplicationContext();
		 
		// Check device for Play Services APK.
	    if (checkPlayServices()) {
	        // If this check succeeds, proceed with normal processing.
	        System.out.println("Play store supported");
	       // mDisplay.setText("Play store supported");
	        setContentView(R.layout.activity_main);
	        playFlag=1;
	    }
	    else
	    {
	    	System.out.println("Play store Not supported");
	    }
	    if(playFlag==1)
	    {
	    	 gcm = GoogleCloudMessaging.getInstance(this);
	            regid = getRegistrationId(context);

	            if (regid.isEmpty()) {
	                registerInBackground();
	    
	            }
	    }
	    mDisplay.append(msg + "\n");
	}
	
	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}
	
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}
	
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
	
	private void registerInBackground() {
		progress = new ProgressDialog(this);
		progress.setMessage("Loading...");
		new MyTask(progress).execute();
	}
	
	private void sendRegistrationIdToBackend() {
		try {
			 HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://kidiyoor.site88.net/resfeber/sendRegistrationId.php");
		   System.out.println("connect to website first time");
		 
		     // Add your data
	       List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	            nameValuePairs.add(new BasicNameValuePair("driverId", "1"));
	            nameValuePairs.add(new BasicNameValuePair("regId", regid));
	          httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        // Execute HTTP Post Request
		   	        HttpResponse response = httpclient.execute(httppost);
	       System.out.println("HTTP post done - upload regid");

	       HttpEntity entity = response.getEntity();
    	     InputStream is = entity.getContent();
    	     System.out.println("regId sent to web");
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
	
	}
	
	private void storeRegistrationId(Context context, String regId) {
		/*System.out.println("regId saving into mob");
	    final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	    System.out.println("regId saved in mob");
	*/
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public class MyTask extends AsyncTask<Void, Void, Void> {
		  private ProgressDialog progress1;

		public MyTask(ProgressDialog progress2) {
		    this.progress1 = progress2;
		    progress=progress1;
		  }

		  public void onPreExecute() {
			  progress.show();
		    
		   
		  }

		

		  public void onPostExecute(Void unused) {
			 //mDisplay.append(msg + "\n");
			  			  progress.dismiss();
		  }

		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			msg = "";
            try {
                if (gcm == null) {
                	System.out.println("gcm was null so gcm=goo....get..()");
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                System.out.println("before gcm.SENDER_ID");
                regid = gcm.register(SENDER_ID);
                System.out.println("after gcm.SENDER_ID");
                
                msg = "Device registered, registration ID=" + regid;

                
                sendRegistrationIdToBackend();

                
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
                // If there is an error, don't just keep trying to register.
                // Require the user to click a button again, or perform
                // exponential back-off.
            }
            System.out.println(msg);
            return null;
		
		}
	}
}

package com.pesit.gcm;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Confirmation extends Activity {
	String msg1=null;
	TextView tv;
	Button accept;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmation);
		Bundle bundle = getIntent().getExtras();
		msg1 = bundle.getString("m");
	System.out.println(msg1);
	tv=(TextView)findViewById(R.id.tv);
	tv.setText(msg1);
	accept=(Button)findViewById(R.id.accept);
	
	accept.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			System.out.println("accept");
			try {
				 HttpClient httpclient = new DefaultHttpClient();
			    HttpPost httppost = new HttpPost("http://kidiyoor.site88.net/resfeber/accept.php");
			   System.out.println("connect to website to send notification");
			   
			     // Add your data
		       List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		            nameValuePairs.add(new BasicNameValuePair("sno", "1"));
		            nameValuePairs.add(new BasicNameValuePair("driver", "driver2"));
		            
		            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        // Execute HTTP Post Request
			   	        HttpResponse response = httpclient.execute(httppost);
		      
			   	     System.out.println("Done notifiying");
		       HttpEntity entity = response.getEntity();
	    	  InputStream is = entity.getContent();
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    	System.out.println("catch1");
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    	System.out.println("catch2");
		    }
			
			
		}
	});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.confirmation, menu);
		return true;
	}
	
}

package com.androidgpstrackerbluemix;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class Http 
{
	// Members
	HttpClient httpClient;
	
	//Constructor
	public Http()
	{
		httpClient = new DefaultHttpClient();
	}
	
	//Methods
	public String get(String uri)
	{
		String result = null;
		
		try 
		{
			HttpGet httpGet = new HttpGet(uri);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();

            if (httpEntity != null) 
            {
                InputStream inputstream = httpEntity.getContent();
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream));
                StringBuilder stringbuilder = new StringBuilder();

                String currentline = null;
                while ((currentline = bufferedreader.readLine()) != null) 
                {
                    stringbuilder.append(currentline + "\n");
                }
                
                result = stringbuilder.toString();
                inputstream.close();
            }
            
            return result;
        } 
		catch (Exception e) 
		{
            return "ERROR: " + e.getMessage();
        }
	}
}

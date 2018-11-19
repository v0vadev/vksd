package ru.vovadev.vkstories;
import android.os.*;
import java.net.*;
import java.io.*;
import android.widget.*;
import android.content.*;
import java.sql.*;
import org.json.*;
import android.app.*;
import android.net.*;

public class OfLogin extends AsyncTask<String, Void, String>
{
	private Context context;
	private Activity activity;

	public OfLogin(Context c,Activity a){
		this.context = c;
		this.activity = a;
	}

	protected String doInBackground(String[] url)
	{
		try{
			URL obj = new URL("https://oauth.vk.com/token?grant_type=password&v=5.68&scope=8&client_id=2274003&client_secret=hHbZxrka2uZ6jB1inYsH&username="+url[0]+"&password="+url[1]);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent","VKAndroidApp/4.13.1-1206 (Android 7.1.2; SDK 25; armeabi-v7a; Xiaomi Redmi 4X; ru)");

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return response.toString();
		} catch(Exception e){
			e.printStackTrace();
			return "{\"error\":\"Произошла ошибка\"}";
		}
		// TODO: Implement this method
	}

	@Override
	protected void onPostExecute(String result)
	{
		try{
			JSONObject obj = new JSONObject(result);
			if(obj.isNull("error")){
				String token = obj.getString("access_token");
				//Toast.makeText(context,token,Toast.LENGTH_SHORT).show();
				new RefreshToken(context,activity,obj.getString("user_id")).execute(token);
			} else{
				Toast.makeText(context,obj.getString("error"),Toast.LENGTH_SHORT).show();
			}
		} catch(JSONException e){
			e.printStackTrace();
		}
		//Toast.makeText(context,result,Toast.LENGTH_LONG).show();
		// TODO: Implement this method
		super.onPostExecute(result);
	}
}

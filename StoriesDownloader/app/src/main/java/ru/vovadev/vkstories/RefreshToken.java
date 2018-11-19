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

public class RefreshToken extends AsyncTask<String, Void, String>
{
	private Context context;
	private Activity activity;
	private String uid;

	public RefreshToken(Context c,Activity a,String uid){
		this.context = c;
		this.activity = a;
		this.uid = uid;
	}

	protected String doInBackground(String... url)
	{
		try{
			URL obj = new URL("https://api.vk.com/method/auth.refreshToken?access_token="+url[0]+"&receipt=z_6yWq0SEc:APA91bHFIzuJ_xfGdkg8PAxweoVii7X_vIk0x5Hxb2KFJvKTVLF5pgwTDoiuVnQlypxxY4eKfZVbBcuag1pGiAfnE1pOAePtl1jSkyDvuoCBTlurZF0gDMejFuu2jH75dJysZ-sFZTcK&v=5.68");
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
				String token = obj.getJSONObject("response").getString("token");
				SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.shared_prefs),Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("musicToken",token);
				editor.putString("auid",uid);
				editor.apply();
				new Stats(context).execute("kateLogin");
				activity.finish();
				activity.startActivity(activity.getIntent());
				//Toast.makeText(context,token,Toast.LENGTH_LONG).show();
			}
		} catch(JSONException e){
			e.printStackTrace();
		}
		// TODO: Implement this method
		super.onPostExecute(result);
	}
}

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

public class GetRequest extends AsyncTask<String, Void, String>
{
	private Context context;
	
	public GetRequest(Context c){
		this.context = c;
	}

	protected String doInBackground(String... url)
	{
		try{
			URL obj = new URL(url[0]);
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

			connection.setRequestMethod("GET");

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
			return "Error";
		}
		// TODO: Implement this method
	}

	@Override
	protected void onPostExecute(String result)
	{
		try{
			SharedPreferences sp = context.getSharedPreferences("main",Context.MODE_PRIVATE);
			JSONObject res = new JSONObject(result);
			Boolean enabled = res.getBoolean("enabled");
			if(enabled){
				SharedPreferences.Editor editor = sp.edit();
				JSONObject not = res.getJSONObject("notification");
				String id = not.getString("id");
				if(!sp.getString("lastNot","").equals(id)){
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle(not.getString("title"));
					builder.setMessage(not.getString("body"));
					builder.setPositiveButton(not.getString("okBtn"), new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface inter,int num){
								//do nothing
							}
						});
					builder.show();
					editor.putString("lastNot",id);
					editor.apply();
				}
			}
		} catch(JSONException je){
			je.printStackTrace();
		}
		// TODO: Implement this method
		super.onPostExecute(result);
	}
}

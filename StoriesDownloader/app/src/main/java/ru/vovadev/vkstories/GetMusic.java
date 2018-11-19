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
import android.view.*;
import android.support.v4.widget.*;
import android.media.*;

public class GetMusic extends AsyncTask<String, Void, String>
{
	private Context context;
	private Activity activity;
	private String oid;
	private String[] urls;
	private String[] audio;
	private String[] artist;

	public GetMusic(Context c,Activity a,String oid){
		this.context = c;
		this.activity = a;
		this.oid = oid;
	}

	protected String doInBackground(String[] url)
	{
		try{
			URL obj = new URL("https://api.vk.com/method/audio.get?owner_id="+oid+"&access_token="+url[0]+"&v=5.68");
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
		ProgressBar pb = activity.findViewById(R.id.musicLoad);
		android.support.v4.widget.SwipeRefreshLayout sw = activity.findViewById(R.id.swiperefresh);
		pb.setVisibility(View.GONE);
		sw.setVisibility(View.VISIBLE);
		//Toast.makeText(context,result,Toast.LENGTH_LONG).show();
		try{
			JSONObject obj = new JSONObject(result);
			if(obj.isNull("error")){
				JSONArray res = obj.getJSONObject("response").getJSONArray("items");
				audio = new String[res.length()];
				urls = new String[res.length()];
				artist = new String[res.length()];
				for(int i=0;i<res.length();i++){
					JSONObject t = (JSONObject) res.get(i);
					artist[i] = t.getString("artist");
					audio[i] = t.getString("title");
					urls[i] = t.getString("url");
				}
				AudioListAdapter ala = new AudioListAdapter(activity,audio,artist);
				ListView lv = activity.findViewById(R.id.musicList);
				lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
					public void onItemClick(AdapterView<?> av,View v,int pos,long id){
						Integer intid = Integer.parseInt(Long.toString(id));
						/*Intent intent = new Intent(context,PlayerActivity.class);
						intent.putExtra("url",urls[intid]);
						context.startActivity(intent);*/
						new Stats(context).execute("audioDownload");
						new DownloadFile(context,activity,audio[intid]).execute(urls[intid]);
						//Toast.makeText(context,urls[intid],Toast.LENGTH_SHORT).show();
						//JSONObject aobj = (JSONObject) 
					}
				});
				lv.setAdapter(ala);
				SwipeRefreshLayout swl = activity.findViewById(R.id.swiperefresh);
				swl.setRefreshing(false);
			} else{
				Toast.makeText(context,obj.getJSONObject("error").getString("error_msg"),Toast.LENGTH_SHORT).show();
			}
		} catch(JSONException e){
			e.printStackTrace();
		}
		//Toast.makeText(context,result,Toast.LENGTH_LONG).show();
		// TODO: Implement this method
		super.onPostExecute(result);
	}
}

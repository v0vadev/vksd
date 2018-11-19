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
import android.content.pm.*;

public class Stats extends AsyncTask<String, Void, String>
{
	private Context context;
	
	public Stats(Context c){
		this.context = c;
	}

	protected String doInBackground(String... type)
	{
		try{
			SharedPreferences sp = this.context.getSharedPreferences(this.context.getString(R.string.shared_prefs),Context.MODE_PRIVATE);
			PackageInfo pm = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
			String v = pm.versionName;
			URL urll = new URL("https://app.vovadev.ru/sd/stats.php?type="+type[0]+"&v="+v+"&uid="+sp.getString("uid",""));
			HttpURLConnection con = (HttpURLConnection) urll.openConnection();
			con.setRequestMethod("GET");
			return con.getResponseMessage();
		} catch(Exception e){
			e.printStackTrace();
			return "err";
		}
	}

	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
	}
}

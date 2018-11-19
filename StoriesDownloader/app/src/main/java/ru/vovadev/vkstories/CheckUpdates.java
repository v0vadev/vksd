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

public class CheckUpdates extends AsyncTask<String, Void, String>
{
	private Context context;
	private String v;
	private Activity activity;
	private JSONObject vr;

	public CheckUpdates(Context c,Activity a){
		this.context = c;
		this.activity = a;
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
			PackageInfo pm = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
			v = pm.versionName;
			try{
				vr = new JSONObject(result);
				if(!vr.getBoolean("deleted") && !vr.getString("version").equals(v)){
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Обновите приложение");
					builder.setMessage("Версия приложения на устройстве: "+v+". Имеется более новая версия: "+vr.getString("version")+". Обновите приложение");
					builder.setPositiveButton("Обновить", new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface inter,int num){
								Intent bi = new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id=ru.vovadev.vkstories"));
								context.startActivity(bi);
							}
						});
					builder.setNegativeButton("Позже", new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface inter,int num){
								//do nothing
							}
						});
					builder.show();
				} else if(vr.getBoolean("deleted") && !vr.getString("version").equals(v)){
					AlertDialog.Builder builder = new AlertDialog.Builder(context);
					builder.setTitle("Обновите приложение");
					builder.setMessage("Версия приложения на устройстве: "+v+". Имеется более новая версия: "+vr.getString("version")+". Обновите приложение. К сожалению, оно было удалено из Google Play, но Вы можете прямо сейчас вручную скачать и установить новую версию.");
					builder.setPositiveButton("Скачать", new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface inter,int num){
								/*Intent bi = new Intent(Intent.ACTION_VIEW,Uri.parse("https://app.vovadev.ru/sd/SD_v"+v+".apk"));
								context.startActivity(bi);*/
								try{
									new DownloadFile(context,activity,"update").execute("https://app.vovadev.ru/sd/SD_v"+vr.getString("version")+".apk");
								} catch(JSONException exc){
									exc.printStackTrace();
								}
							}
						});
					builder.setNegativeButton("Позже", new DialogInterface.OnClickListener(){
							public void onClick(DialogInterface inter,int num){
								//do nothing
							}
						});
					builder.show();
				}
			} catch(JSONException ex){
				ex.printStackTrace();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		// TODO: Implement this method
		super.onPostExecute(result);
	}
}

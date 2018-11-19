package ru.vovadev.vkstories;
import android.os.*;
import android.app.*;
import java.net.*;
import java.io.*;
import android.util.*;
import android.widget.*;
import android.content.*;
import android.support.v4.app.*;
import okhttp3.internal.*;
import android.net.*;
import android.support.v4.content.*;
import android.support.v4.*;
import android.support.v7.appcompat.*;

public class DownloadFile extends AsyncTask<String, String, String> {

	private NotificationCompat.Builder builder;
	private NotificationManager manager;
	private String fileName;
	private String folder;
	private boolean isDownloaded;
	private static final String TAG = StoryActivity.class.getSimpleName();
	private Context context;
	private Activity activity;
	private int notId;
	private String name;
	
	public DownloadFile(Context c,Activity a,String n){
		this.context = c;
		this.activity = a;
		name = n;
	}

	/**
	 * Before starting background thread
	 * Show Progress Bar Dialog
	 */
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		/*this.progressDialog = new ProgressDialog(activity);
		this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.progressDialog.setCancelable(false);
		this.progressDialog.show();*/
		builder = new NotificationCompat.Builder(context);
		builder.setContentTitle("Загрузка: "+name);
		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setOngoing(true);
		builder.setShowWhen(false);
		manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notId = 0;
	}

	/**
	 * Downloading file in background thread
	 */
	@Override
	protected String doInBackground(String[] f_url) {
		int count;
		try {
			URL url = new URL(f_url[0]);
			URLConnection connection = url.openConnection();
			connection.connect();
			// getting file length
			int lengthOfFile = connection.getContentLength();


			// input stream to read file - with 8k buffer
			InputStream input = new BufferedInputStream(url.openStream(), 8192);

			//Extract file name from URL
			if(name.equals(context.getString(R.string.story))){
				fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());
				String[] fnamear = fileName.split("\\?");
				fileName = fnamear[0];
			} else if(name.equals("update")){
				fileName = "update.apk";
			} else {
				fileName = CheckFile(folder,name,1);
			}

			//External directory path to save file
			folder = Environment.getExternalStorageDirectory() + File.separator + "StoriesDownloader/";

			//Create androiddeft folder if it does not exist
			File directory = new File(folder);

			if (!directory.exists()) {
				directory.mkdirs();
			}

			// Output stream to write file
			OutputStream output = new FileOutputStream(folder + fileName);

			byte data[] = new byte[1024];

			long total = 0;

			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress....
				// After this onProgressUpdate will be called
				publishProgress("" + (int) ((total * 100) / lengthOfFile));
				//Log.d(TAG, "Прогресс: " + (int) ((total * 100) / lengthOfFile));

				// writing data to file
				output.write(data, 0, count);
			}

			// flushing output
			output.flush();

			// closing streams
			output.close();
			input.close();
			if(name.equals("update")){
				return "Скачано";
			}
			return "Скачано в: " + folder + fileName;

		} catch (Exception e) {
			Log.e("Ошибка: ", e.getMessage());
		}

		return "Что-то пошло не так. Приложению предоставлен доступ к памяти?";
	}

	/**
	 * Updating progress bar
	 */
	protected void onProgressUpdate(String... progress) {
		// setting progress percentage
		//progressDialog.setProgress(Integer.parseInt(progress[0]));
		builder.setProgress(100,Integer.parseInt(progress[0]),false);
		manager.notify(notId,builder.build());
	}


	@Override
	protected void onPostExecute(String message) {
		// dismiss the dialog after the file was downloaded
		manager.cancel(notId);
		notId = notId+1;

		// Display File path after downloading
		Toast.makeText(context,
					   message, Toast.LENGTH_LONG).show();
		if(name.equals("update")){
			File file = new File(context.getString(R.string.appdirns)+"/update.apk");
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
				Uri uri = FileProvider.getUriForFile(context, "ru.vovadev.vkstories.provider",file);
				Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
				install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
				install.setDataAndType(uri,"application/vnd.android.package-archive");
				context.startActivity(install);
			} else{
				Uri uri = Uri.fromFile(file);
				Intent install = new Intent(Intent.ACTION_VIEW);
				install.setDataAndType(uri, "application/vnd.android.package-archive");
				install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.startActivity(install);
			}
		}
	}
	
	public String CheckFile(String folder,String name,Integer i){
		File fileCheck =  new File(folder+name);
		if(fileCheck.exists()){
			name = name+Integer.toString(i)+".mp3";
			return CheckFile(folder,name,i+1);
		}
		return name+".mp3";
	}
}

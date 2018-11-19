package ru.vovadev.vkstories;

import android.app.*;
import android.os.*;
import com.vk.sdk.*;
import com.vk.sdk.util.*;
import android.widget.*;
import java.util.*;
import android.content.*;
import android.view.*;
import com.vk.sdk.api.*;
import org.json.*;
import java.time.*;
import android.widget.AbsListView.*;
import android.graphics.*;
import com.squareup.picasso.*;
import android.support.v4.view.*;
import java.io.*;
import android.net.*;
import android.support.v4.content.*;
import android.*;
import android.content.pm.*;
import android.support.v4.app.*;
import java.net.*;
import android.opengl.*;

public class MainActivity extends Activity 
{
	
	private SharedPreferences sp;

	@Override
	public SharedPreferences getSharedPreferences(String name, int mode)
	{
	// TODO: Implement this method
	return super.getSharedPreferences(name, mode);
		}
		
	static String scope = "stories";
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
		File dir = new File(getString(R.string.appdir));
		File updateApk = new File(getString(R.string.appdir)+"/update.apk");
		if(updateApk.exists()){
			updateApk.delete();
			showMessage(getString(R.string.thankstext));
		}
		dir.mkdirs();
		sp = getSharedPreferences(getString(R.string.shared_prefs),Context.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        if(sp.contains("token") && sp.contains("uid") && sp.contains("lastNot")){
			setContentView(R.layout.main);
			getStories();
			checkNotifications();
			checkUpdates();
		} else{
			setContentView(R.layout.login);
		}
		if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
			ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bar, menu);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.about:
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				LayoutInflater inf = LayoutInflater.from(MainActivity.this);
				final View v = inf.inflate(R.layout.about,null);
				builder.setTitle(getString(R.string.about));
				builder.setView(v);
				builder.setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
												  public void onClick(DialogInterface dlg, int sumthin) {

												  }
											  });
				builder.show();
				return true;
			case R.id.friends:
				Intent inte = new Intent(MainActivity.this,FriendsActivity.class);
				inte.putExtra("oid",sp.getString("uid",""));
				startActivity(inte);
				return true;
		}
		return true;
	}
	
	public void login(View v){
		VKSdk.login(this,scope);
	}
	
	public void forum(View v){
		Intent bi = new Intent(Intent.ACTION_VIEW,Uri.parse("http://4pda.ru/forum/index.php?showtopic=919837"));
		startActivity(bi);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if(!VKSdk.onActivityResult(requestCode,resultCode,data,new VKCallback<VKAccessToken>(){
			@Override
			public void onResult(VKAccessToken token){
				SharedPreferences sp = getSharedPreferences(getString(R.string.shared_prefs),Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("token",token.accessToken);
				editor.putString("uid",token.userId);
				editor.putString("lastNot","0");
				editor.apply();
				setContentView(R.layout.main);
				getStories();
				checkNotifications();
				checkUpdates();
			}
			@Override
			public void onError(VKError err){
				showMessage(getString(R.string.api_err)+": "+err.errorMessage);
			}
		})){
		 super.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	public void showMessage(String text){
		Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
	}
	
	public void getStories(){
		VKRequest req = new VKRequest("stories.get",VKParameters.from("extended","1"));
		req.executeWithListener(new VKRequest.VKRequestListener(){
			public void onComplete(VKResponse res){
				try{
					JSONObject resp = res.json.getJSONObject("response");
					JSONArray ar = resp.getJSONArray("items");
					JSONArray profiles = res.json.getJSONObject("response").getJSONArray("profiles");
					JSONArray groups = res.json.getJSONObject("response").getJSONArray("groups");
					for(int i=0;i<ar.length();i++){
						JSONArray t = (JSONArray) ar.get(i);
						try{
							for(int o=0;o<t.length();o++){
								JSONObject obj = (JSONObject) t.get(o);
								String name = "";
								if(Integer.parseInt(obj.getString("owner_id")) > 0){
									//user
									int index = findIn(profiles,"id",obj.getString("owner_id"));
									try{
										JSONObject user = (JSONObject) profiles.get(index);
									    name = user.getString("first_name");
									} catch(JSONException e){
										e.printStackTrace();
									}
								} else{
									//group
									int intOid = Integer.parseInt(obj.getString("owner_id"))*-1;
									String strOid = Integer.toString(intOid);
									int index = findIn(groups,"id",strOid);
									try{
										JSONObject user = (JSONObject) groups.get(index);
										name = user.getString("name");
									} catch(JSONException e){
										e.printStackTrace();
									}
								}
								//showMessage(name);
								LinearLayout ll = new LinearLayout(getApplicationContext());
								de.hdodenhof.circleimageview.CircleImageView civ = new de.hdodenhof.circleimageview.CircleImageView(getApplicationContext());
								TextView tv = new TextView(getApplicationContext());
								ll.setGravity(Gravity.CENTER);
								ll.setOrientation(LinearLayout.VERTICAL);
								ll.setTag(obj.getString("owner_id")+"_"+obj.getString("id"));
								ll.setOnClickListener(new LinearLayout.OnClickListener(){
									@Override
									public void onClick(View v){
										Intent intent = new Intent(MainActivity.this,StoryActivity.class);
										intent.putExtra("story_id",v.getTag().toString());
										startActivity(intent);
									}
								});
								LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
								lp.setMargins(5,5,5,5);
								ll.setLayoutParams(lp);
								LinearLayout mainll = findViewById(R.id.storiesList);
								
								mainll.addView(ll);
								ll.addView(civ);
								ll.addView(tv);
								SharedPreferences sp = getSharedPreferences(getString(R.string.shared_prefs),Context.MODE_PRIVATE);
								Picasso.get().load("https://app.vovadev.ru/sd/getPhoto.php?oid="+obj.getString("owner_id")+"&token="+sp.getString("token","")).placeholder(R.drawable.placeholder).into(civ);
								civ.getLayoutParams().width = 100;
								civ.getLayoutParams().height = 100;
								tv.setText(name);
								tv.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
								if(obj.getString("owner_id").equals(sp.getString("uid",""))){
									tv.setTextColor(Color.rgb(81,129,184));
									tv.setTypeface(null,Typeface.BOLD);
									civ.setBackgroundResource(R.drawable.border);
								} else{
									tv.setTextColor(Color.BLACK);
								}
							}
						} catch(JSONException e){
							e.printStackTrace();
						}
					}
				} catch(JSONException e){
					e.printStackTrace();
				}
			}
		});
	}
	
	public void donate(View v){
		Intent bi = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.donationalerts.ru/r/v0va_development"));
		startActivity(bi);
	}
	
	public void github(View v){
		Intent bi = new Intent(MainActivity.this,MusicActivity.class);
		startActivity(bi);
	}
	
	public void vkgroup(View v){
		Intent bi = new Intent(Intent.ACTION_VIEW,Uri.parse("https://vk.com/vkstoriesapp"));
		startActivity(bi);
	}
	
	public void openSearch(View v){
		Intent bi = new Intent(MainActivity.this,SearchActivity.class);
		startActivity(bi);
	}
	
	public int findIn(JSONArray ar,String key,String value){
		int resp = 0;
		for(int y=0;y<ar.length();y++){
			try{
				JSONObject t = (JSONObject) ar.get(y);
				if(!t.getString(key).equals(value)){
					continue;
				}
				resp = y;
				break;
			} catch(JSONException e){
				e.printStackTrace();
			}
		}
		return resp;
	}
	
	public void checkNotifications(){
		new GetRequest(this).execute("https://app.vovadev.ru/sd/notification.php");
	}
	
	public void checkUpdates(){
		new CheckUpdates(this,this).execute("https://app.vovadev.ru/sd/newVersion.php");
	}
	
	public void logout(View v){
		SharedPreferences sp = getSharedPreferences(getString(R.string.shared_prefs),Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove("token");
		editor.remove("uid");
		editor.apply();
		setContentView(R.layout.login);
	}
}

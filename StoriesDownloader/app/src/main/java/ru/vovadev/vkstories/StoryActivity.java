package ru.vovadev.vkstories;
import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.net.*;
import java.io.*;
import android.view.*;
import com.vk.sdk.api.*;
import org.json.*;
import com.squareup.picasso.*;
import android.opengl.*;
import java.io.*;
import java.util.*;
import android.icu.text.*;
import android.util.*;
import android.*;
import java.util.jar.*;
import android.net.*;
import android.webkit.*;

public class StoryActivity extends Activity
{
	
	private String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Intent intent = getIntent();
		String sid = intent.getStringExtra("story_id");
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.story);
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		VKRequest req = new VKRequest("stories.getById",VKParameters.from("stories",sid,"extended","1","fields","photo_100"));
		req.executeWithListener(new VKRequest.VKRequestListener(){
			public void onComplete(VKResponse res){
				try{
					JSONArray ar = res.json.getJSONObject("response").getJSONArray("items");
					JSONArray users = res.json.getJSONObject("response").getJSONArray("profiles");
					JSONArray groups = res.json.getJSONObject("response").getJSONArray("groups");
					for(int i=0;i<ar.length();i++){
						try{
							JSONObject obj = (JSONObject) ar.get(i);
							JSONObject replies = obj.getJSONObject("replies");
							ImageView img = findViewById(R.id.story);
							TextView datetv = findViewById(R.id.storyDate);
							Long sd = (long) Integer.parseInt(obj.getString("date"));
							SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
							Date date = new Date(sd*1000);
							datetv.setText(sdf.format(date));
							String photoo = "";
							String name = "";
							uid = obj.getString("owner_id");
							if(Integer.parseInt(obj.getString("owner_id")) > 0){
								int index = findIn(users,"id",obj.getString("owner_id"));
								JSONObject userObj = (JSONObject) users.get(index);
								photoo = userObj.getString("photo_100");
								name = userObj.getString("first_name")+" "+userObj.getString("last_name");
							} else{
								int oid = Integer.parseInt(obj.getString("owner_id"))*-1;
								int index = findIn(users,"id",Integer.toString(oid));
								JSONObject userObj = (JSONObject) groups.get(index);
								photoo = userObj.getString("photo_100");
								name = userObj.getString("name");
							}
							if(!replies.getString("count").equals("0")){
								Button btn = findViewById(R.id.answersBtn);
								btn.setVisibility(View.VISIBLE);
								btn.setText(getString(R.string.answer)+" ("+replies.getString("count")+")");
								btn.setTag(obj.getString("owner_id")+"_"+obj.getString("id"));
								btn.setOnClickListener(new View.OnClickListener(){
										@Override
										public void onClick(View v){
											Intent intent = new Intent(StoryActivity.this,RepliesActivity.class);
											intent.putExtra("story_id",v.getTag().toString());
											startActivity(intent);
										}
									});
							}
							if(!obj.isNull("parent_story_owner_id") && !obj.isNull("parent_story_id")){
								Button btn = findViewById(R.id.parentBtn);
								btn.setVisibility(View.VISIBLE);
								btn.setTag(obj.getString("parent_story_owner_id")+"_"+obj.getString("parent_story_id"));
								btn.setOnClickListener(new View.OnClickListener(){
										@Override
										public void onClick(View v){
											Intent intent = new Intent(StoryActivity.this,StoryActivity.class);
											intent.putExtra("story_id",v.getTag().toString());
											startActivity(intent);
										}
								});
							}
							if(!obj.isNull("link")){
								JSONObject link = obj.getJSONObject("link");
								Button btn = findViewById(R.id.linkBtn);
								btn.setText(link.getString("text"));
								btn.setTag(link.getString("url"));
								btn.setOnClickListener(new View.OnClickListener(){
									public void onClick(View v){
										Intent bi = new Intent(Intent.ACTION_VIEW,Uri.parse(v.getTag().toString()));
										startActivity(bi);
									}
								});
								btn.setVisibility(View.VISIBLE);
							}
							ImageView iv = findViewById(R.id.authorpic);
							Picasso.get().load(photoo).placeholder(R.drawable.placeholder).into(iv);
							TextView tvn = findViewById(R.id.authorname);
							tvn.setText(name);
							String type = "";
							if(obj.getString("type").equals("photo")){
								JSONArray photo = obj.getJSONObject("photo").getJSONArray("sizes");
								JSONObject size = (JSONObject) photo.get(photo.length()-1);
								if(size.getString("url").equals("")){
									Toast.makeText(getApplicationContext(),getString(R.string.load_err),Toast.LENGTH_LONG).show();
									finish();
								}
								img.setVisibility(View.VISIBLE);
								Picasso.get().load(size.getString("url")).placeholder(R.drawable.screen).into(img);
								type = getString(R.string.photo);
								findViewById(R.id.downloadBtn).setTag(size.getString("url"));
							} else if(obj.getString("type").equals("video")){
								WebView wv =  findViewById(R.id.webView);
								//String url = obj.getJSONObject("video").getString("photo_800");
								//Picasso.get().load(url).placeholder(R.drawable.screen).into(img);
								//findViewById(R.id.wvBtn).setVisibility(View.VISIBLE);
								type = getString(R.string.video);
								JSONObject files = obj.getJSONObject("video").getJSONObject("files");
								Iterator x = files.keys();
								JSONArray filesAr = new JSONArray();
								while(x.hasNext()){
									String key = (String) x.next();
									filesAr.put(files.get(key));
								}
								wv.loadUrl(filesAr.get(0).toString());
								wv.setVisibility(View.VISIBLE);
								findViewById(R.id.downloadBtn).setTag(filesAr.get(0).toString());
								findViewById(R.id.wvBtn).setTag(filesAr.get(0).toString());
							}
							TextView tv = findViewById(R.id.storyType);
							tv.setText(getString(R.string.typeOf)+": "+type);
						} catch(JSONException e){
							e.printStackTrace();
						}
					}
				} catch(JSONException e){
					e.printStackTrace();
				}
			}
			
			public void onError(VKError err){
				Toast.makeText(getApplicationContext(),getString(R.string.api_err)+": "+err.errorMessage,Toast.LENGTH_LONG).show();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.story, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case R.id.copy:
				Intent intent = getIntent();
				String sid = intent.getStringExtra("story_id");
				String url = "https://vk.com/story"+sid;
				ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData cd = ClipData.newPlainText(url,url);
				cm.setPrimaryClip(cd);
				showMessage(getString(R.string.copied));
				return true;
			case R.id.share:
				Intent intenttt = getIntent();
				String siddd = intenttt.getStringExtra("story_id");
				String urlll = "https://vk.com/story"+siddd;
				//creating share intent
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT,urlll);
				startActivity(Intent.createChooser(i,getString(R.string.share)));
				return true;
			case R.id.openIn:
				Intent intentt = getIntent();
				String sidd = intentt.getStringExtra("story_id");
				String urll = "https://vk.com/story"+sidd;
				Intent bi = new Intent(Intent.ACTION_VIEW,Uri.parse(urll));
				startActivity(bi);
				return true;
			case android.R.id.home:
				// app icon in action bar clicked; goto parent activity.
				this.finish();
				return true;
		}
		return true;
	}
	
	public void download(View v){
		new Stats(getApplicationContext()).execute("download");
		new DownloadFile(StoryActivity.this,this,getString(R.string.story)).execute(v.getTag().toString());
		//Toast.makeText(getApplicationContext(),v.getTag().toString(),Toast.LENGTH_SHORT).show();
	}
	
	public void showMessage(String text){
		Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT).show();
	}
	
	public void back(View v){
		finish();
	}
	
	public void openVideo(View v){
		Intent bi = new Intent(Intent.ACTION_VIEW,Uri.parse(v.getTag().toString()));
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
	
	public void openProfile(View v){
		if(Integer.parseInt(uid) > 0){
			Intent inte = new Intent(StoryActivity.this,UserActivity.class);
			inte.putExtra("uid",uid);
			startActivity(inte);
		}
	}
}

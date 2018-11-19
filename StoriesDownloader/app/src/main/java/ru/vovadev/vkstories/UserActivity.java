package ru.vovadev.vkstories;
import android.app.*;
import android.os.*;
import android.content.*;
import com.vk.sdk.api.*;
import org.json.*;
import android.widget.*;
import android.view.*;
import com.squareup.picasso.*;
import android.net.*;

public class UserActivity extends Activity
{
	
	private String uid;
	private String sn;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);
		uid = getIntent().getStringExtra("uid");
		sn = "id"+uid;
		new Stats(getApplicationContext()).execute("userProfile");
		//Toast.makeText(getApplicationContext(),uid,Toast.LENGTH_SHORT).show();
		VKRequest req = new VKRequest("execute.getUser",VKParameters.from("uid",uid));
		req.executeWithListener(new VKRequest.VKRequestListener(){
			public void onComplete(VKResponse res){
				try{
					JSONObject obj = res.json.getJSONObject("response");
					sn = obj.getString("screen_name");
					ProgressBar pb = findViewById(R.id.userLoader);
					de.hdodenhof.circleimageview.CircleImageView civ = findViewById(R.id.useravaCiv);
					TextView tv = findViewById(R.id.usernameTv);
					LinearLayout btnsl = findViewById(R.id.btnsLay);
					LinearLayout bll = findViewById(R.id.lockedLay);
					Button fbtn = findViewById(R.id.fbtn);
					Button sbtn = findViewById(R.id.sbtn);
					Button mbtn = findViewById(R.id.mbtn);
					pb.setVisibility(View.GONE);
					civ.setVisibility(View.VISIBLE);
					tv.setVisibility(View.VISIBLE);
					Picasso.get().load(obj.getString("photo")).placeholder(getDrawable(R.drawable.placeholder)).into(civ);
					tv.setText(obj.getString("name"));
					UserActivity.this.setTitle(obj.getString("name"));
					if(!Boolean.parseBoolean(obj.getString("hasStories"))){
						sbtn.setVisibility(View.GONE);
					}
					if(!Boolean.parseBoolean(obj.getString("hasFriends"))){
						fbtn.setVisibility(View.GONE);
					}
					if(!Boolean.parseBoolean(obj.getString("audio"))){
						mbtn.setVisibility(View.GONE);
					}
					if(Boolean.parseBoolean(obj.getString("blocked")) || Boolean.parseBoolean(obj.getString("bl"))){
						bll.setVisibility(View.VISIBLE);
					} else{
						btnsl.setVisibility(View.VISIBLE);
					}
				} catch(JSONException e){
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),R.string.api_err,Toast.LENGTH_SHORT).show();
				}
			}
			
			public void onError(VKError err){
				Toast.makeText(getApplicationContext(),getString(R.string.api_err)+": "+err.errorMessage,Toast.LENGTH_LONG).show();
			}
		});
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
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
				//Intent intent = getIntent();
				//String sid = intent.getStringExtra("story_id");
				String url = "https://vk.com/"+sn;
				ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData cd = ClipData.newPlainText(url,url);
				cm.setPrimaryClip(cd);
				Toast.makeText(getApplicationContext(),getString(R.string.copied),Toast.LENGTH_SHORT).show();
				return true;
			case R.id.share:
				/*Intent intenttt = getIntent();
				 String siddd = intenttt.getStringExtra("story_id");*/
				String urlll = "https://vk.com/"+sn;
				//creating share intent
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("text/plain");
				i.putExtra(Intent.EXTRA_TEXT,urlll);
				startActivity(Intent.createChooser(i,getString(R.string.share)));
				return true;
			case R.id.openIn:
				/*Intent intentt = getIntent();
				 String sidd = intentt.getStringExtra("story_id");*/
				String urll = "https://vk.com/"+sn;
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
	
	public void openFriends(View v){
		Intent inte = new Intent(UserActivity.this,FriendsActivity.class);
		inte.putExtra("oid",uid);
		startActivity(inte);
	}
	
	public void openStories(View v){
		Intent inte = new Intent(UserActivity.this,UserStoriesActivity.class);
		inte.putExtra("oid",uid);
		startActivity(inte);
	}
	
	public void openMusic(View v){
		Intent inte = new Intent(UserActivity.this,MusicActivity.class);
		inte.putExtra("oid",uid);
		startActivity(inte);
	}
	
}

package ru.vovadev.vkstories;
import android.app.*;
import android.os.*;
import com.vk.sdk.api.*;
import org.json.*;
import android.widget.*;
import android.view.*;
import android.support.v4.widget.*;
import android.content.*;
import android.net.*;

public class UserStoriesActivity extends Activity
{
	
	private String oid;
	private String[] titles;
	private String[] imgs;
	private String[] sids;
	private ProgressBar pb;
	private android.support.v4.widget.SwipeRefreshLayout swl;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		oid = getIntent().getStringExtra("oid");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_stories);
		new Stats(getApplicationContext()).execute("storiesList");
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
		pb = findViewById(R.id.storyLoad);
		swl = findViewById(R.id.sswiperefresh);
		pb.setVisibility(View.VISIBLE);
		VKRequest req = new VKRequest("execute.getStoriesByUserId",VKParameters.from("link","https://vk.com/id"+oid,"name_case","gen"));
		req.executeWithListener(new VKRequest.VKRequestListener(){
			public void onComplete(VKResponse res){
				try{
					pb.setVisibility(View.GONE);
					swl.setVisibility(View.VISIBLE);
					JSONObject user = res.json.getJSONObject("response").getJSONObject("user");
					JSONArray stories = res.json.getJSONObject("response").getJSONArray("stories");
					titles = new String[stories.length()];
					imgs = new String[stories.length()];
					sids = new String[stories.length()];
					for(int i=0;i<stories.length();i++){
						titles[i] = getString(R.string.story)+" "+user.getString("first_name")+" "+user.getString("last_name");
						JSONObject t = (JSONObject) stories.get(i);
						sids[i] = t.getString("owner_id")+"_"+t.getString("id");
						if(t.getString("type").equals("photo")){
							t = (JSONObject) t.getJSONObject("photo").getJSONArray("sizes").get(1);
							imgs[i] = t.getString("url");
						} else{
							t = t.getJSONObject("video");
							imgs[i] = t.getString("photo_130");
						}
					}
					StoryListAdapter adapter = new StoryListAdapter(UserStoriesActivity.this,titles,imgs);
					//ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,titles);
					//UserListAdapter adapter = new UserListAdapter(UserStoriesActivity.this,titles,imgs);
					ListView lv = findViewById(R.id.storyyList);
					lv.setVisibility(View.VISIBLE);
					lv.setOnItemClickListener(new ListView.OnItemClickListener(){
						public void onItemClick(AdapterView<?> p1,View v,int p3,long p4){
							String sid = sids[Integer.parseInt(Long.toString(p4))];
							Intent inte = new Intent(UserStoriesActivity.this,StoryActivity.class);
							inte.putExtra("story_id",sid);
							startActivity(inte);
						}
					});
					lv.setAdapter(adapter);
				} catch(JSONException e){
					e.printStackTrace();
				}
			}
			
			public void onError(VKError err){
				Toast.makeText(getApplicationContext(),getString(R.string.api_err),Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()){
			case android.R.id.home:
				// app icon in action bar clicked; goto parent activity.
				this.finish();
				return true;
		}
		return true;
	}
	
}

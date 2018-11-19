package ru.vovadev.vkstories;
import android.app.*;
import android.os.*;
import android.content.*;
import com.vk.sdk.api.*;
import org.json.*;
import android.widget.*;
import android.view.View.*;
import android.view.*;

public class RepliesActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.replies);
		getReplies();
	}
	
	public void getReplies(){
		Intent intent = getIntent();
		String sid = intent.getStringExtra("story_id");
		String[] sidsplit = sid.split("_");
		VKRequest req = new VKRequest("stories.getReplies",VKParameters.from("owner_id",sidsplit[0],"story_id",sidsplit[1],"extended","1"));
		req.executeWithListener(new VKRequest.VKRequestListener(){
			public void onComplete(VKResponse res){
				try{
					JSONArray ar = res.json.getJSONObject("response").getJSONArray("items");
					JSONArray users = res.json.getJSONObject("response").getJSONArray("profiles");
					JSONArray groups = res.json.getJSONObject("response").getJSONArray("groups");
					for(int i=0;i<ar.length();i++){
						JSONArray t = (JSONArray) ar.get(i);
						for(int o=0;o<t.length();o++){
							JSONObject obj = (JSONObject) t.get(o);
							String name = "";
							if(Integer.parseInt(obj.getString("owner_id")) > 0){
								int ind = findIn(users,"id",obj.getString("owner_id"));
								JSONObject user = (JSONObject) users.get(ind);
								name = user.getString("first_name")+" "+user.getString("last_name");
							} else{
								int indint = Integer.parseInt(obj.getString("owner_id"))*-1;
								int ind = findIn(groups,"id",Integer.toString(indint));
								JSONObject user = (JSONObject) groups.get(ind);
								name = user.getString("name");
							}
							Button btn = new Button(getApplicationContext());
							btn.setText(name);
							btn.setTag(obj.getString("owner_id")+"_"+obj.getString("id"));
							btn.setOnClickListener(new View.OnClickListener(){
									public void onClick(View v){
										Intent intent = new Intent(RepliesActivity.this,StoryActivity.class);
										intent.putExtra("story_id",v.getTag().toString());
										startActivity(intent);
									}
							});
							LinearLayout ll = findViewById(R.id.repliesList);
							ll.addView(btn);
						}
					}
				} catch(JSONException e){
					e.printStackTrace();
				}
			}
		});
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
	
	public void goBack(View v){
		finish();
	}
}

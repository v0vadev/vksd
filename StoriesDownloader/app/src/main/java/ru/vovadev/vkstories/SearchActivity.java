package ru.vovadev.vkstories;
import android.app.*;
import android.graphics.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.vk.sdk.api.*;
import org.json.*;
import com.squareup.picasso.*;
import android.content.*;

public class SearchActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
	}
	
	public void search(View v){
		EditText linket = findViewById(R.id.linket);
		String link = linket.getText().toString();
		VKRequest req = new VKRequest("execute.getStoriesByUserId",VKParameters.from("link",link));
		req.executeWithListener(new VKRequest.VKRequestListener(){
			public void onComplete(VKResponse res){
				//Toast.makeText(getApplicationContext(),res.json.toString(),Toast.LENGTH_LONG).show();
				try{
					JSONObject user = res.json.getJSONObject("response").getJSONObject("user");
					JSONArray stories = res.json.getJSONObject("response").getJSONArray("stories");
					LinearLayout ll = findViewById(R.id.resultList);
					ll.removeAllViewsInLayout();
					ImageView iv = findViewById(R.id.userphoto);
					TextView tv = findViewById(R.id.username);
					Picasso.get().load(user.getString("photo_100")).placeholder(R.drawable.placeholder).into(iv);
					tv.setText(user.getString("first_name")+" "+user.getString("last_name"));
					if(Integer.toString(stories.length()).equals("0")) return;
					for(int i=0;i<stories.length();i++){
						Button btn = new Button(getApplicationContext());
						JSONObject t = (JSONObject) stories.get(i);
						btn.setText(getString(R.string.story)+" "+Integer.toString(i+1));
						btn.setTag(t.getString("owner_id")+"_"+t.getString("id"));
						btn.setOnClickListener(new View.OnClickListener(){
							public void onClick(View v){
								Intent intent = new Intent(SearchActivity.this,StoryActivity.class);
								intent.putExtra("story_id",v.getTag().toString());
								startActivity(intent);
							}
						});
						ll.addView(btn);
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
}

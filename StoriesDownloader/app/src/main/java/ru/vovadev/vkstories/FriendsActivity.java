package ru.vovadev.vkstories;
import android.app.*;
import android.os.*;
import android.view.*;
import com.vk.sdk.api.*;
import android.content.*;
import android.widget.*;
import org.json.*;

public class FriendsActivity extends Activity
{

	@Override
	public Intent getIntent()
	{
		// TODO: Implement this method
		return super.getIntent();
	}
	
	private String oid;
	private String[] ids;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friends);
		new Stats(getApplicationContext()).execute("friendsList");
		oid = getIntent().getStringExtra("oid");
		//Toast.makeText(getApplicationContext(),oid,Toast.LENGTH_SHORT).show();
		ProgressBar pb = findViewById(R.id.friendsLoad);
		pb.setVisibility(View.VISIBLE);
		VKRequest req = new VKRequest("friends.get",VKParameters.from("user_id",oid,"order","hints","fields","photo_100"));
		req.executeWithListener(new VKRequest.VKRequestListener(){
			public void onComplete(VKResponse res){
				//Toast.makeText(getApplicationContext(),res.json.toString(),Toast.LENGTH_LONG).show();
				ProgressBar pb = findViewById(R.id.friendsLoad);
				android.support.v4.widget.SwipeRefreshLayout sw = findViewById(R.id.fswiperefresh);
				pb.setVisibility(View.GONE);
				sw.setVisibility(View.VISIBLE);
				try{
					JSONArray obj = res.json.getJSONObject("response").getJSONArray("items");
					if(obj.length() == 0){
						TextView tv = findViewById(R.id.noFriendsText);
						tv.setVisibility(View.VISIBLE);
					} else{
						String[] names = new String[obj.length()];
						String[] avas = new String[obj.length()];
						ids = new String[obj.length()];
						for(int i=0;i<obj.length();i++){
							JSONObject t = (JSONObject) obj.get(i);
							ids[i] = t.getString("id");
							names[i] = t.getString("first_name")+" "+t.getString("last_name");
							avas[i] = t.getString("photo_100");
						}
						UserListAdapter adapter = new UserListAdapter(FriendsActivity.this,names,avas);
						//ArrayAdapter<String> adapter = new ArrayAdapter<String>(FriendsActivity.this,android.R.layout.simple_list_item_1,names);
						ListView lv = findViewById(R.id.friendsList);
						lv.setOnItemClickListener(new ListView.OnItemClickListener(){
							public void onItemClick(AdapterView<?> p1,View p2,int p3,long id){
								String uid = ids[Integer.parseInt(Long.toString(id))];
								Intent inte = new Intent(FriendsActivity.this,UserActivity.class);
								inte.putExtra("uid",uid);
								startActivity(inte);
							}
						});
						lv.setAdapter(adapter);
					}
				} catch(JSONException e){
					e.printStackTrace();
				}
			}
			
			public void onError(VKError err){
				Toast.makeText(getApplicationContext(),getString(R.string.api_err)+": "+err.errorReason,Toast.LENGTH_LONG).show();
			}
		});
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; goto parent activity.
				this.finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}

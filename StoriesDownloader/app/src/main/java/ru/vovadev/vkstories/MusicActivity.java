package ru.vovadev.vkstories;
import android.app.*;
import android.os.*;
import android.view.*;
import android.content.*;
import android.widget.*;
import java.lang.reflect.*;
import android.support.v4.widget.*;

public class MusicActivity extends Activity
{
	
	private SharedPreferences sp;
	private ProgressBar pb;
	private TextView tv;
	private SwipeRefreshLayout swl;
	private String oid;

	@Override
	public SharedPreferences getSharedPreferences(String name, int mode)
	{
		// TODO: Implement this method
		return super.getSharedPreferences(name, mode);
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		sp = getSharedPreferences(getString(R.string.shared_prefs),Context.MODE_PRIVATE);
		setContentView(R.layout.music);
		pb = findViewById(R.id.musicLoad);
		tv = findViewById(R.id.noMusicText);
		swl = findViewById(R.id.swiperefresh);
		oid = getIntent().getStringExtra("oid");
		if(oid == null){
			oid = sp.getString("auid","");
		}
		//Toast.makeText(getApplicationContext(),oid,Toast.LENGTH_SHORT).show();
		if(sp.contains("musicToken") && sp.contains("auid")){
			pb.setVisibility(View.VISIBLE);
			tv.setVisibility(View.GONE);
			new GetMusic(MusicActivity.this,this,oid).execute(sp.getString("musicToken",""));
			swl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
				public void onRefresh(){
					new GetMusic(MusicActivity.this,MusicActivity.this,sp.getString("auid","")).execute(sp.getString("musicToken",""));
				}
			});
			swl.setColorScheme(R.color.refreshColor,R.color.refreshColor,R.color.refreshColor);
		} else{
			AlertDialog.Builder builder = new AlertDialog.Builder(MusicActivity.this);
			LayoutInflater inf = LayoutInflater.from(MusicActivity.this);
			View v = inf.inflate(R.layout.oflogin,null);
			builder.setTitle(getString(R.string.music));
			builder.setView(v);
			builder.setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface inter,int num){
					AlertDialog dialog = (AlertDialog) inter;
					EditText login = (EditText) dialog.findViewById(R.id.oflogin);
					EditText pass = (EditText) dialog.findViewById(R.id.ofpass);
					String[] lpass = new String[2];
					lpass[0] = login.getText().toString();
					lpass[1] = pass.getText().toString();
					new OfLogin(MusicActivity.this,MusicActivity.this).execute(lpass);
				}
			});
			builder.show();
			if(!sp.contains("readKate")){
				AlertDialog.Builder ab = new AlertDialog.Builder(MusicActivity.this);
				ab.setTitle(getString(R.string.music));
				ab.setMessage(getString(R.string.aboutKate));
				ab.setPositiveButton(getString(R.string.ok),new AlertDialog.OnClickListener(){
						public void onClick(DialogInterface p1,int p2){
							SharedPreferences.Editor editor = sp.edit();
							editor.putString("readKate","1");
							editor.apply();
						}
					});
				ab.show();
			}
		}
		super.onCreate(savedInstanceState);
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setHomeButtonEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.music, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				// app icon in action bar clicked; goto parent activity.
				this.finish();
				return true;
			case R.id.logoutMenu:
				SharedPreferences.Editor editor = sp.edit();
				editor.remove("musicToken");
				editor.apply();
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void showMessage(String text){
		Toast.makeText(MusicActivity.this,text,Toast.LENGTH_SHORT).show();
	}
}

package ru.vovadev.vkstories;
import android.widget.*;
import android.app.*;
import android.view.*;

public class AudioListAdapter extends ArrayAdapter<String>
{
	private Activity activity;
	private String[] title;
	private String[] artist;
	
	public AudioListAdapter(Activity a,String[] t,String[] ar){
		super(a,R.layout.audio_list,t);
		this.activity = a;
		this.title = t;
		this.artist = ar;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater inf = activity.getLayoutInflater();
		View v = inf.inflate(R.layout.audio_list,null,true);
		TextView tit = v.findViewById(R.id.audioTitle);
		TextView art = v.findViewById(R.id.audioArtist);
		tit.setText(title[position]);
		art.setText(artist[position]);
		return v;
	}
	
}

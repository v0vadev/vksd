package ru.vovadev.vkstories;
import android.widget.*;
import android.app.*;
import android.view.*;
import com.squareup.picasso.*;

public class StoryListAdapter extends ArrayAdapter<String>
{
	private Activity activity;
	private String[] imgUrl;
	private String[] title;
	
	public StoryListAdapter(Activity a,String[] tit,String[] iu){
		super(a,R.layout.story_list,tit);
		this.activity = a;
		this.imgUrl = iu;
		this.title = tit;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater li = activity.getLayoutInflater();
		View v = li.inflate(R.layout.story_list,null,true);
		ImageView iv = v.findViewById(R.id.storyImage);
		TextView tv = v.findViewById(R.id.storyTitle);
		tv.setText(title[position]);
		Picasso.get().load(imgUrl[position]).placeholder(R.drawable.screen).into(iv);
		return v;
	}
	
}

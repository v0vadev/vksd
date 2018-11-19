package ru.vovadev.vkstories;
import android.widget.*;
import android.content.*;
import android.app.*;
import android.view.*;
import android.support.v4.widget.*;
import com.squareup.picasso.*;

public class UserListAdapter extends ArrayAdapter<String> 
{
	private Activity context;
	private String[] name;
	private String[] imgUrl;
	
	public UserListAdapter(Activity c,String[] n,String[] u){
		super(c,R.layout.user_list,n);
		this.context = c;
		this.name = n;
		this.imgUrl = u;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		LayoutInflater li = context.getLayoutInflater();
		View v = li.inflate(R.layout.user_list,null,true);
		TextView tvn = v.findViewById(R.id.listun);
		de.hdodenhof.circleimageview.CircleImageView civu = v.findViewById(R.id.listava);
		tvn.setText(name[position]);
		//civu.setImageDrawable(context.getDrawable(R.drawable.placeholder));
		Picasso.get().load(imgUrl[position]).placeholder(R.drawable.placeholder).into(civu);
		return v;
	}
}

package chatapp.overload.com.sparkchatapp.tools;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import chatapp.overload.com.sparkchatapp.R;

public class FriendAdapter extends BaseAdapter{

    Context mContext;
    LayoutInflater mInflater;
    List<String> mFriend_name_list;
    int count_row;

    public FriendAdapter(Context context, Map<String,String> friend_list){
        count_row = 0;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mFriend_name_list = new ArrayList<String>(friend_list.values());
        count_row = friend_list.size();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.friend_listview,null);
            holder = new ViewHolder();
            holder.friend_name = (TextView) convertView.findViewById(R.id.tvFriend_name);
            holder.is_online = (TextView) convertView.findViewById(R.id.tvFriend_is_online);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        String[] friend_data = mFriend_name_list.get(i).split("&");
        holder.friend_name.setText(friend_data[0]);
        holder.friend_name.setTypeface(Typeface.create("serif-monospace", Typeface.BOLD));
        holder.friend_name.setTextColor(Color.parseColor("#000000"));
        holder.friend_name.setGravity(Gravity.LEFT);

        holder.is_online.setText(Integer.parseInt(friend_data[1]) > 0 ? "Online" : "Offline");
        holder.is_online.setTypeface(Typeface.create("serif-monospace", Typeface.BOLD));
        holder.is_online.setTextColor(Color.parseColor(Integer.parseInt(friend_data[1]) > 0 ? "#00CC00" : "#CC0000"));
        holder.is_online.setGravity(Gravity.RIGHT);
        return convertView;
    }

    @Override
    public int getCount() {
        return count_row;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    public class ViewHolder
    {
        TextView friend_name;
        TextView is_online;
    }
}



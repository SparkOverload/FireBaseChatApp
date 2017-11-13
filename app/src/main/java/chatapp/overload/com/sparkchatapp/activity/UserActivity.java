package chatapp.overload.com.sparkchatapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;
import chatapp.overload.com.sparkchatapp.R;
import chatapp.overload.com.sparkchatapp.tools.FriendAdapter;

public class UserActivity extends Activity {

    DatabaseReference database;
    AlertDialog.Builder builder;
    AlertDialog alert;
    ProgressDialog dialog_loding;
    String user_key;

    TextView tvUser_username;
    TextView tvUser_name;
    ListView lvUser_friends;

    Map<String,String> friend_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);
        database = FirebaseDatabase.getInstance().getReference();
        builder = new AlertDialog.Builder(UserActivity.this);

        dialog_loding = new ProgressDialog(UserActivity.this);
        dialog_loding.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog_loding.setMessage("Loading. Please wait...");
        dialog_loding.setIndeterminate(true);
        dialog_loding.setCanceledOnTouchOutside(false);

        tvUser_username = (TextView) findViewById(R.id.tvUser_username);
        tvUser_name = (TextView) findViewById(R.id.tvUser_name);
        lvUser_friends = (ListView)findViewById(R.id.lvUser_friends);

        Intent receive_user = getIntent();
        if(receive_user.getExtras() != null){
            user_key = receive_user.getStringExtra("user_key");
            getUserData(user_key);
        }else{
            user_key = null;
            tvUser_username.setText("Error!");
            tvUser_name.setText("Seem to hack.");
        }
    }

    public void getUserData(final String user_key){
        dialog_loding.show();
        // ----------------------- get user data --------------------------//
        Query get_user = database.child("USER").child(user_key);
        get_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Map user_data = (Map) dataSnapshot.getValue();
                    tvUser_username.setText("Username : "+user_data.get("USERNAME").toString());
                    tvUser_name.setText("Name : "+user_data.get("NAME".toString()));
                }else{
                    tvUser_username.setText("Error!");
                    tvUser_name.setText("User not found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // ------------------------ get friend name and key ------------------------//
        friend_list = new HashMap<String, String>();
        Query get_friend_list = database.child("USER");
        get_friend_list.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot getSnapshot : dataSnapshot.getChildren()) {
                        if(!getSnapshot.getKey().equals(user_key)) {
                            Map friend_data = (Map) getSnapshot.getValue();
                            friend_list.put(getSnapshot.getKey(), friend_data.get("NAME").toString()+"&"+friend_data.get("IS_ONLINE").toString());
                        }
                    }
                }

                // ------------------------ create custom list view -----------------------------//
                if(friend_list != null) {
                    lvUser_friends.setAdapter(new FriendAdapter(getApplicationContext(), friend_list));
                    lvUser_friends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                            Intent send_data = new Intent(UserActivity.this, ChatActivity.class);
                            send_data.putExtra("user_key", user_key);
                            send_data.putExtra("friend_key",friend_list.keySet().toArray()[arg2].toString());
                            send_data.putExtra("friend_name",friend_list.values().toArray()[arg2].toString().split("&")[0]);
                            startActivity(send_data);
                        }
                    });
                }
                dialog_loding.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClickBtnLogout(View v) {
        if (user_key != null) {
            Map<String, Object> update_is_online = new HashMap<String, Object>();
            update_is_online.put("IS_ONLINE", 0);
            database.child("USER").child(user_key).updateChildren(update_is_online, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        dialog_loding.dismiss();
                        builder.setMessage("Error user can't logout.");
                        alert = builder.create();
                        alert.show();
                    } else {
                        finish();
                    }
                }
            });
        }else{
            dialog_loding.dismiss();
            builder.setMessage("Error seem to hack.");
            alert = builder.create();
            alert.show();
        }
    }
}



package chatapp.overload.com.sparkchatapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import chatapp.overload.com.sparkchatapp.R;

public class ChatActivity extends Activity {

    DatabaseReference database;
    ProgressDialog dialog_loding;
    LinearLayout layoutChat_board;
    ScrollView svChat_board;
    EditText etChat_message_area;
    Button btnChat_friend_back;

    String user_key;
    String friend_key;
    String friend_name;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        database = FirebaseDatabase.getInstance().getReference();

        dialog_loding = new ProgressDialog(ChatActivity.this);
        dialog_loding.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog_loding.setMessage("Loading. Please wait...");
        dialog_loding.setIndeterminate(true);
        dialog_loding.setCanceledOnTouchOutside(false);

        svChat_board = (ScrollView) findViewById(R.id.svChat_board);
        svChat_board.setVerticalScrollBarEnabled(false);
        btnChat_friend_back = (Button) findViewById(R.id.btnChat_friend_back);
        etChat_message_area = (EditText) findViewById(R.id.etChat_message_area);

        Intent receive_data = getIntent();
        if(receive_data != null) {
            user_key = receive_data.getStringExtra("user_key");
            friend_key = receive_data.getStringExtra("friend_key");
            friend_name = receive_data.getStringExtra("friend_name");
            getMessage(user_key,friend_key,friend_name);
        }
    }

    public void getMessage(final String user_key,String friend_key,String friend_name){
        btnChat_friend_back.setText("< "+friend_name);
        layoutChat_board = (LinearLayout) findViewById(R.id.layoutChat_board);
        Query get_message_list = database.child("MESSAGE").child(user_key+"_"+friend_key);
        get_message_list.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getValue() != null) {
                    Map message_data = (Map) dataSnapshot.getValue();
                    createTextViewMessageBox((user_key.equals(message_data.get("SENDER").toString()) ? 1 : 0),message_data.get("TEXT").toString());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onClickBtnChatSend(View v){
        if(!etChat_message_area.getText().toString().trim().equals("")) {
            Map<String, Object> message_data = new HashMap<String, Object>();
            message_data.put("TEXT", etChat_message_area.getText().toString());
            message_data.put("SENDER", user_key);
            database.child("MESSAGE").child(user_key + "_" + friend_key).push().setValue(message_data);
            database.child("MESSAGE").child(friend_key + "_" + user_key).push().setValue(message_data);
        }
        etChat_message_area.setText(null);
    }

    public void createTextViewMessageBox(int sender,String text){
        TextView message_box = new TextView(ChatActivity.this);
        message_box.setText(text);
        message_box.setTextSize(16);
        message_box.setTypeface(Typeface.create("serif-monospace", Typeface.BOLD));
        message_box.setTextColor(Color.parseColor("#000000"));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1.0f;
        layoutParams.gravity = (sender == 1 ? Gravity.RIGHT : Gravity.LEFT);
        message_box.setBackgroundResource((sender == 1 ? R.drawable.bubble_in : R.drawable.bubble_out));
        message_box.setLayoutParams(layoutParams);
        layoutChat_board.addView(message_box);

        // ------------------ jump to bottom in scrollview ------------------------//
        svChat_board.post(new Runnable() {
            @Override
            public void run() {
                svChat_board.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void onClickBtnBackUser(View v){
        finish();
    }
}

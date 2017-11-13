package chatapp.overload.com.sparkchatapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import chatapp.overload.com.sparkchatapp.R;

public class LoginActivity extends Activity {

    DatabaseReference database;
    AlertDialog.Builder builder;
    AlertDialog alert;
    ProgressDialog dialog_loding;
    EditText etLogin_username;
    EditText etLogin_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        database = FirebaseDatabase.getInstance().getReference();
        builder = new AlertDialog.Builder(LoginActivity.this);

        dialog_loding = new ProgressDialog(LoginActivity.this);
        dialog_loding.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog_loding.setMessage("Loading. Please wait...");
        dialog_loding.setIndeterminate(true);
        dialog_loding.setCanceledOnTouchOutside(false);

        etLogin_username = (EditText) findViewById(R.id.etLogin_username);
        etLogin_password = (EditText) findViewById(R.id.etLogin_password);

        Intent receive_data = getIntent();
        if(receive_data.getExtras() != null){
            etLogin_username.setText(receive_data.getStringExtra("username"));
            etLogin_password.setText(receive_data.getStringExtra("password"));
        }
    }

    public void onClickBtnRegister(View v){
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }

    public void onClickBtnLogin(View v){
        if(validateLoginFrom()) {
            dialog_loding.show();
            Query get_user = database.child("USER").orderByChild("USERNAME_PASSWORD").equalTo(etLogin_username.getText().toString() + "_" + etLogin_password.getText().toString());
            get_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.getChildrenCount() == 1) {
                            Map<String, Object> update_is_online = new HashMap<String, Object>();
                            update_is_online.put("IS_ONLINE", 1);
                            database.child("USER").child(((Map) dataSnapshot.getValue()).keySet().toArray()[0].toString()).updateChildren(update_is_online, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError != null) {
                                        dialog_loding.dismiss();
                                        builder.setMessage("Error user can't online.");
                                        alert = builder.create();
                                        alert.show();
                                    } else {
                                        dialog_loding.dismiss();
                                        Intent send_data = new Intent(LoginActivity.this, UserActivity.class);
                                        send_data.putExtra("user_key", databaseReference.getKey());
                                        startActivity(send_data);
                                    }
                                }
                            });
                        } else {
                            dialog_loding.dismiss();
                            builder.setMessage("Error user is duplicate.");
                            alert = builder.create();
                            alert.show();
                        }
                    } else {
                        etLogin_username.setError("Please check your username .");
                        etLogin_password.setError("Please check your password .");
                        dialog_loding.dismiss();
                        builder.setMessage("Username or Password incorrect.");
                        alert = builder.create();
                        alert.show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public boolean validateLoginFrom(){
        int pass_check = 0;

        if(etLogin_username.getText().toString().trim().equals("")){
            etLogin_username.setError("Please fill username .");
        }else{
            pass_check++;
        }

        if(etLogin_password.getText().toString().trim().equals("")){
            etLogin_password.setError("Please fill password .");
        }else{
            pass_check++;
        }

        return (pass_check == 2 ? true : false);
    }


}

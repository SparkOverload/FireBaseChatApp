package chatapp.overload.com.sparkchatapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

public class RegisterActivity extends Activity {

    DatabaseReference database;
    AlertDialog.Builder builder;
    ProgressDialog dialog_saving;
    EditText etRegister_name;
    EditText etRegister_username;
    EditText etRegister_password;
    EditText etRegister_password_c;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        database = FirebaseDatabase.getInstance().getReference();
        builder = new AlertDialog.Builder(RegisterActivity.this);

        dialog_saving = new ProgressDialog(RegisterActivity.this);
        dialog_saving.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog_saving.setMessage("Saving. Please wait...");
        dialog_saving.setIndeterminate(true);
        dialog_saving.setCanceledOnTouchOutside(false);

        etRegister_name = (EditText) findViewById(R.id.etRegiter_name);
        etRegister_username = (EditText) findViewById(R.id.etRegiter_username);
        etRegister_password = (EditText) findViewById(R.id.etRegiter_password);
        etRegister_password_c = (EditText) findViewById(R.id.etRegiter_password_c);
    }

    public void onClickBtnBackLogin(View v){
        finish();
    }

    public void onClickBtnSubmit(View v){
        if(validateRegisterFrom())
        {
            dialog_saving.show();
            Query check_user_exist = database.child("USER").orderByChild("USERNAME").equalTo(etRegister_username.getText().toString());
            check_user_exist.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        dialog_saving.dismiss();
                        etRegister_username.setError("Username have already !");
                    }else{
                        Map<String, Object> value = new HashMap<String, Object>();
                        value.put("NAME", etRegister_name.getText().toString());
                        value.put("USERNAME", etRegister_username.getText().toString());
                        value.put("PASSWORD", etRegister_password.getText().toString());
                        value.put("USERNAME_PASSWORD", etRegister_username.getText().toString() + "_" + etRegister_password.getText().toString());
                        value.put("IS_ONLINE", 0);
                        database.child("USER").push().setValue(value, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    builder.setMessage("Data could not be saved " + databaseError.getMessage());
                                } else {
                                    builder.setMessage("Hello " + etRegister_name.getText() + " let's talk with us go go.").setOnCancelListener(new DialogInterface.OnCancelListener() {
                                        @Override
                                        public void onCancel(DialogInterface dialogInterface) {
                                            Intent send_data = new Intent(RegisterActivity.this,LoginActivity.class);
                                            send_data.putExtra("username",etRegister_username.getText().toString());
                                            send_data.putExtra("password",etRegister_password.getText().toString());
                                            startActivity(send_data);
                                        }
                                    });
                                }
                                dialog_saving.dismiss();
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public boolean validateRegisterFrom(){
        int pass_check = 0;

        if(etRegister_name.getText().toString().trim().equals("")){
            etRegister_name.setError("Name must not has space !");
        }else{
            pass_check++;
        }

        if(etRegister_username.getText().toString().trim().equals("")){
            etRegister_username.setError("Username must not has space !");
        }else{
            pass_check++;
        }

        if(etRegister_password.getText().toString().trim().equals("")){
            etRegister_password.setError("Password must not has space !");
        }else{
            pass_check++;
        }

        if(!(etRegister_password_c.getText().toString().trim().equals(etRegister_password.getText().toString().trim()))){
            etRegister_password_c.setError("Password confirm not equal Password !");
        }else{
            pass_check++;
        }

        return (pass_check == 4 ? true : false);
    }
}

package aplikasiku.navwithdirectionlib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import aplikasiku.navwithdirectionlib.FDBMobel.User;

public class HelloFirebaseDB extends AppCompatActivity {

    FirebaseDatabase mFirebaseInstance;
    DatabaseReference tUser,tLokasi;
    ValueEventListener tUserListener;

    Button butInsert, butUpdate;
    EditText edName,edStatus,edNameToUpdate,edNameValueUpdate;
    ProgressBar prgInsert;
    TextView tvReadResult;

    String readresult;
    String keyUpdate;
    User userDataUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_firebase_db);

        init();
        initListener();
    }

    private void initListener() {
        butInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });

        //selectData();
        tUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                readresult="";
                for(DataSnapshot user : dataSnapshot.getChildren()){
                    User usr = user.getValue(User.class);
                    readresult += user.getKey();
                    //readresult += " "+ usr.name +", "+ usr.status +".";
                    tvReadResult.setText(readresult);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        tUser.addValueEventListener(tUserListener);

        butUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edNameToUpdate.getText().toString().equals("")){
                    updateData(edNameToUpdate.getText().toString(), edNameValueUpdate.getText().toString());
                }
            }
        });
    }

    private void updateData(String valueToUpdate, String valueUpdate) {
        prgInsert.setVisibility(View.VISIBLE);
        mFirebaseInstance.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    User usr = data.getValue(User.class);
                    if(usr.name.equals(edNameToUpdate.getText().toString())){
                        keyUpdate = data.getKey();
                        userDataUpdate = usr;

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/" + keyUpdate, new User(edNameValueUpdate.getText().toString(), userDataUpdate.status).toMap());

                        tUser.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                prgInsert.setVisibility(View.INVISIBLE);
                                if(databaseError==null){
                                    Toast.makeText(getApplicationContext(), "update ok", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "update fail", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void insertData() {
        String name = edName.getText().toString();
        String status = edStatus.getText().toString();
        if(!name.equals("") && !status.equals("")){
            prgInsert.setVisibility(View.VISIBLE);
            String uid = tUser.push().getKey();
            tUser.child(uid).setValue(new User(name, status), new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    prgInsert.setVisibility(View.INVISIBLE);
                    if(databaseError==null){
                        Toast.makeText(getApplicationContext(), "insert ok", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "insert fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void init() {
        butInsert = (Button) findViewById(R.id.buttonInsert);
        edName = (EditText) findViewById(R.id.editTextDBname);
        edStatus = (EditText) findViewById(R.id.editTextDBstts);
        prgInsert = (ProgressBar) findViewById(R.id.create_progress);
        tvReadResult = (TextView) findViewById(R.id.textView2);
        butUpdate = (Button) findViewById(R.id.buttonUpdate);
        edNameToUpdate = (EditText) findViewById(R.id.editTextNameToUpdate);
        edNameValueUpdate = (EditText) findViewById(R.id.editTextValueUpdate);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        tUser = mFirebaseInstance.getReference("users");
        tLokasi = mFirebaseInstance.getReference("locations");
    }
}

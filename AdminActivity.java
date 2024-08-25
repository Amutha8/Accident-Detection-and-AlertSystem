package com.application.accidentdetection;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ListView userList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        userList = findViewById(R.id.userList);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        // Query to retrieve users where value.value == "true"
        Query query = mDatabase.orderByChild("value").equalTo("true");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get user details
                    String userId = snapshot.getKey();
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String emergencyNumber = snapshot.child("emergencyNumber").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String value = snapshot.child("value").getValue(String.class);

                    // Add user to list
                    User user = new User( name, phone, address, emergencyNumber, email,value);
                    users.add(user);
                }

                // Display users in ListView using custom adapter
                UserListAdapter adapter = new UserListAdapter(AdminActivity.this, R.layout.list_item_user, users);
                userList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}

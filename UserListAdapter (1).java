package com.application.accidentdetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UserListAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private int mResource;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
        }

        // Get user details for the current position
        final User user = getItem(position);

        // Set user details to TextViews
        TextView nameTextView = convertView.findViewById(R.id.textViewName);
        TextView emailTextView = convertView.findViewById(R.id.textViewEmail);
        TextView phoneTextView = convertView.findViewById(R.id.textViewPhone);
        TextView addressTextView = convertView.findViewById(R.id.textViewAddress);
        TextView emergencyNumberTextView = convertView.findViewById(R.id.textViewEmergencyNumber);
        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhone());
        addressTextView.setText(user.getAddress());
        emergencyNumberTextView.setText(user.getEmergencyNumber());

        // Set onClickListener for the deactivate button


        return convertView;
    }
}

package com.example.tcs_health_system;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.tcs_health_system.Adapter.ChatAdapter;
import com.example.tcs_health_system.Modal.ChatMesssage;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.tcs_health_system.Login.PREFS_NAME;

public class chatbot extends AppCompatActivity {

    private String Uame;
    private DatabaseReference dbr;
    RecyclerView recyclerView;
    EditText txt;
    ImageButton send;
    ChatAdapter myadapter;
    List<ChatMesssage> msgs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatbot);

        getSupportActionBar().setTitle("ChatBot");

        SharedPreferences pref=getSharedPreferences(PREFS_NAME,0);
        Uame =pref.getString("user",null);

        dbr= FirebaseDatabase.getInstance().getReferenceFromUrl("https://tcs-health-system-default-rtdb.firebaseio.com/users").
                child(Uame).child("files");
        msgs=new ArrayList<>();

        Bundle bundle = getIntent().getExtras();
        String message = bundle.getString("message");

        txt=findViewById(R.id.edmsg);
        send=findViewById(R.id.send);
        recyclerView=findViewById(R.id.recycleChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ChatMesssage Rdata = new ChatMesssage("",message);
        msgs.add(Rdata);
        myadapter=new ChatAdapter(chatbot.this,msgs);
        myadapter.notifyDataSetChanged();
        recyclerView.setAdapter(myadapter);


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMesssage Sdata=new ChatMesssage(txt.getText().toString(),"");
                msgs.add(Sdata);

                DatabaseReference sub=dbr.child(txt.getText().toString());
                sub.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.child("File_Source").getValue()==null) {
                            ChatMesssage Rdata = new ChatMesssage("","no such file exist");
                            msgs.add(Rdata);
                        }
                        else{
                            ChatMesssage Rdata = new ChatMesssage("", snapshot.child("File_Source").getValue().toString());
                            msgs.add(Rdata);
                        }

                        myadapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(msgs.size()-1);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


                txt.setText("");
            }
        });

    }
}
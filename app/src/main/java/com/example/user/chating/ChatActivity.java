package com.example.user.chating;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    private String mChatUser;

    private DatabaseReference mRootRef;

    private FirebaseAuth mAuth;

    private String cur_id;

    private Button sendbtn;

    private EditText sendtext;

    private RecyclerView mMessagesList;

    private final List<Messages> messagesList=new ArrayList<>();

    private LinearLayoutManager mLinearLayout;

    private MessageAdapter mAdapter;

    private DatabaseReference mMessagesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mRootRef= FirebaseDatabase.getInstance().getReference();

        mAuth=FirebaseAuth.getInstance();

        cur_id=mAuth.getCurrentUser().getUid();

        sendbtn=(Button)findViewById(R.id.send_btn);
        sendtext=(EditText)findViewById(R.id.send_text);

        mAdapter=new MessageAdapter(messagesList);

        mMessagesList=(RecyclerView)findViewById(R.id.recyc_mess);

        mLinearLayout=new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        mMessagesList.setAdapter(mAdapter);



        loadmessages();

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                sendmessage();

            }
        });


    }

    private void sendmessage(){

        String message=sendtext.getText().toString();

        if(!TextUtils.isEmpty(message)){


            DatabaseReference user_message_push = mRootRef.child("messages").push();

            String push_id=user_message_push.getKey();

            Map messagemap=new HashMap();
            messagemap.put("message",message);
            messagemap.put("from",cur_id);

            Map messageusermap=new HashMap();
            messageusermap.put("messages/" + push_id,messagemap);

            mRootRef.updateChildren(messageusermap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                }
            });


        }

    }
    private void loadmessages(){


        Query messageQuery=mRootRef.child("messages").limitToLast(5);

        mRootRef.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                Messages message=dataSnapshot.getValue(Messages.class);

                messagesList.add(message);
                mAdapter.notifyDataSetChanged();

                mMessagesList.scrollToPosition(messagesList.size()-1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);


        if(item.getItemId()==R.id.main_logout_btn){


            FirebaseAuth.getInstance().signOut();
            Intent intent=new Intent(ChatActivity.this,MainActivity.class);
            startActivity(intent);

        }

        return true;
    }
}

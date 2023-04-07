package com.example.loginpage;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.loginpage.utility.Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.getstream.chat.android.client.ChatClient;
import io.getstream.chat.android.client.api.models.FilterObject;
import io.getstream.chat.android.client.api.models.QueryChannelRequest;
import io.getstream.chat.android.client.api.models.querysort.QuerySortByField;
import io.getstream.chat.android.client.api.models.querysort.QuerySorter;
import io.getstream.chat.android.client.channel.ChannelClient;
import io.getstream.chat.android.client.models.Channel;
import io.getstream.chat.android.client.models.CustomObject;
import io.getstream.chat.android.client.models.Filters;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.client.models.User;
import io.getstream.chat.android.offline.plugin.configuration.Config;
import io.getstream.chat.android.offline.plugin.factory.StreamOfflinePluginFactory;

/**
 * @author saran
 * @date 10/2/2023
 */

public class HomePage extends AppCompatActivity {
   private final Database mDatabase = Database.getInstance();
   private final ChatClient client = ChatClient.instance();
   private EditText RoomCode;
   private Bundle b;
   private String LIVESTREAM;
   private String api_key;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      b = getIntent().getExtras();
      super.onCreate(savedInstanceState);
      setContentView(R.layout.homepage);

      Button createRoomButton = findViewById(R.id.createRoom);
      Button submit = findViewById(R.id.roomSubmit);
      Button viewMembers = findViewById(R.id.viewMembers);

      
      String userToken = b.getString("userToken");
      String uid = b.getString("uid");
      //String role = b.getString("role");
      api_key = b.getString("api_key");
      LIVESTREAM = getString(R.string.livestreamChannelType);
      TextView txtView = findViewById(R.id.usernameField);
      //String welcomeMsg = "Welcome!" + role;
      //txtView.setText(welcomeMsg);
      createRoomButton.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            registerUser(uid,userToken);
         }
      });

      submit.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            registerUser_another(uid,userToken);
         }
      });
//      viewMembers.setOnClickListener(new View.OnClickListener() {
//         @Override
//         public void onClick(View view) {
//            view_members(roomCode);
//         }
//      });
   }

   private void registerUser(String uid, String userToken){
      User streamUser = new User();
      streamUser.setId(uid);
      client.connectUser(
              streamUser,userToken
      ).enqueue(connectionResult->{
         if(connectionResult.isError()) {
            System.out.println("Error connecting to client!" + connectionResult.error());
         } else {
            String createRoomCode=String.valueOf(randomInteger());
            startChannel(createRoomCode);
            System.out.println("successfully created a room with code:"+createRoomCode);
         }
              }
      );
   }
   private void registerUser_another(String uid, String userToken){
      User streamUser = new User();
      streamUser.setId(uid);
      client.connectUser(
              streamUser,userToken
      ).enqueue(connectionResult->{
                 if(connectionResult.isError()) {
                    System.out.println("Error connecting to client!" + connectionResult.error());
                 } else {
                    RoomCode = (EditText) findViewById(R.id.roomCode);
                    String roomCode = RoomCode.getText().toString();
                    String channelId = "messageRoom"+roomCode;
                    Task<Boolean> checkChannelTask = mDatabase.checkChannel(channelId);

                    checkChannelTask.addOnSuccessListener(new OnSuccessListener<Boolean>() {
                       @Override
                       public void onSuccess(Boolean channelExists) {
                          if (channelExists) {
                             System.out.println("Channel exists.");
                             startChannel(roomCode);
                          } else {
                             System.out.println("Channel does not exist.");
                          }
                       }
                    });

                    checkChannelTask.addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                          System.out.println("Error checking channel: " + e.getMessage());
                       }
                    });

                 }
              }
      );
   }

   private void startChannel(String createRoomCode){
      try{
         String channelId = "messageRoom"+createRoomCode;
         ChannelClient channelClient = client.channel(LIVESTREAM, channelId);
         startActivity(ChannelActivity.newIntent(HomePage.this,channelClient,mDatabase));
         System.out.println(" Channel started successfully ");

      } catch (Exception e){
         System.out.println("Unable to start channel on HomePage: " + e);
      }

   }
   //method to create a random 4 digit number for room creating purposes
   private int randomInteger(){
      Random rand = new Random();
      int randomNumber = rand.nextInt(9000) + 1000;
      return randomNumber;
   }


   private void join_channel(){
      try{

         ChannelClient channelClient = client.channel("livestream", "messageRoom");

         channelClient.watch().enqueue(result -> {
            if (result.isSuccess()){
               Channel channel2 = result.data();
               System.out.println(channel2.getMembers());
               startActivity(ChannelActivity.newIntent(this,channelClient,mDatabase));
            }
            else{
               System.out.println(result);
            }
         });

//         User user = new User();
//         user.setName("sarangnirwan");
//         // TODO make algorithm to generate JWT Token
//         String tkn = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMDEifQ.T8dm9FWij7dW4i0baXWFa7mb9Aixm2erfZNkij-WpWk";
//         user.setId("6969");
//         String adminToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiNjk2OSJ9.OZgYJ-SH7XiqRx77xrRw7uZKwWeOoqgtfHxgDSdScwk";
////         user.setId("02");
////         user.setName("Varma");
////         user.setId("admin");
////         String tkn = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoiMDIifQ.NAgqyl_yFdiymLKfHNchsgtH0a_lrz1mTSqtFLVU7UA";
//         client.connectUser(user,adminToken).enqueue(connectionResult->{
//                    if(connectionResult.isError()) System.out.println("Error connecting to client!" + connectionResult.error());
//                    else{
//                       System.out.println("success");
//                       ChannelClient channelClient = client.channel("livestream", "message_room");
//
//                       client.channel("livestream","message_room").watch().enqueue(result -> {
//                          if (result.isSuccess()){
//                             Channel channel2 = result.data();
//                             System.out.println(channel2.getMembers());
//                             startActivity(ChannelActivity.newIntent(this,channelClient,mDatabase));
//                          }
//                          else{
//                             System.out.println(result);
//                          }
//                       });
//                    }
//                 }
//         );

      }
      catch(Exception e){
         System.out.println("cannot add users" + e);
      }



   }

   public void view_members(String roomCode){
      ChannelClient channelClient2 = client.channel("livestream", roomCode);
      channelClient2.watch().enqueue(result -> {
         if (result.isSuccess()) {
            Channel channel = result.data();
         } else {
         }
      });


   }

}
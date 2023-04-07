package com.example.loginpage.utility;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;


import com.example.loginpage.databinding.AttachedButtonBinding;
import com.getstream.sdk.chat.adapter.MessageListItem;

import java.util.List;

import androidx.annotation.NonNull;
import io.getstream.chat.android.client.models.Message;
import io.getstream.chat.android.ui.message.list.MessageListView;
import io.getstream.chat.android.ui.message.list.adapter.BaseMessageItemViewHolder;
import io.getstream.chat.android.ui.message.list.adapter.MessageListItemViewHolderFactory;
import io.getstream.chat.android.ui.message.list.adapter.MessageListListenerContainer;

/**
 * @author saran
 * @date 25/2/2023
 */

public class CustomMessageViewHolderFactory extends MessageListItemViewHolderFactory {
   private final int BUTTON_VIEW_HOLDER_TYPE = 1;
   private final Database mDatabase;
   
   public int getItemViewType(@NonNull MessageListItem item){
      if(item instanceof MessageListItem.MessageItem){
         return BUTTON_VIEW_HOLDER_TYPE;
      }
      return super.getItemViewType(item);
   }
   @Override
   public BaseMessageItemViewHolder<? extends MessageListItem> createViewHolder(@NonNull ViewGroup parentView, int viewType){
      if (viewType == BUTTON_VIEW_HOLDER_TYPE) {
         return new ButtonViewHolder(parentView, AttachedButtonBinding.inflate(LayoutInflater.from(parentView.getContext()), parentView, false),mDatabase);
      }
      return super.createViewHolder(parentView, viewType);
   }
   public CustomMessageViewHolderFactory(Database database){
      this.mDatabase = database;
   }

}

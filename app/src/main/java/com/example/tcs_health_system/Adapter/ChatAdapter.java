package com.example.tcs_health_system.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tcs_health_system.Modal.ChatMesssage;
import com.example.tcs_health_system.R;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    Context context;
    List<ChatMesssage> Cmsg;

    public ChatAdapter(Context context, List<ChatMesssage> Cmsg) {
        this.context = context;
        this.Cmsg = Cmsg;
        setHasStableIds(true);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatrecycle,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder( ViewHolder holder, int position) {

        ChatMesssage item=Cmsg.get(position);
       //holder.artist.setText(item.getArtist());

        holder.setIsRecyclable(false);
      //  Log.v("loadedimgag",item.getSimg()+"==="+item.getRimg());
       if(item.getRmsg().equals("")){
           holder.other.setVisibility(View.GONE);
            holder.user.setText(item.getSmsg());
        }
        else if(item.getSmsg().equals("")){
            holder.user.setVisibility(View.GONE);
            holder.other.setText(item.getRmsg());
        }

    }


    @Override
    public int getItemCount() {
        Log.v("sizeis", ""+Cmsg.size());
        return Cmsg.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView user,other;
        public ViewHolder( View itemView) {
            super(itemView);
            user=itemView.findViewById(R.id.user);
            other=itemView.findViewById(R.id.other);
           // artist=itemView.findViewById(R.id.artist);
           // name.setOnClickListener(this);
            itemView.setOnClickListener(this);
            other.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos=getAdapterPosition();
                    ChatMesssage data=Cmsg.get(pos);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(data.getRmsg()), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent newIntent = Intent.createChooser(intent, "Open File");
                    try {
                        context.startActivity(newIntent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                        Toast.makeText(context,"no app can perform this ",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


        @Override
        public void onClick(View v) {

            if(v.getId()==R.id.other){

            }

        }

    }
}

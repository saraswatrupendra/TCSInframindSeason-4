package com.example.tcs_health_system.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tcs_health_system.Modal.HealthParam;
import com.example.tcs_health_system.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HealthAdapter extends RecyclerView.Adapter<HealthAdapter.ViewHolder> {
    Context context;
    List<HealthParam> ParaList;

    public HealthAdapter(Context context, List<HealthParam> ParaList) {
        this.context = context;
        this.ParaList = ParaList;
    }

    @NonNull
    @Override
    public HealthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.list_row,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HealthAdapter.ViewHolder holder, int position) {

        HealthParam data=ParaList.get(position);

        holder.Hparam.setText(data.getHParaName());
        holder.value.setText(""+data.getValue());

       /* java.text.DateFormat d=java.text.DateFormat.getDateInstance();
        String at=d.format(new Date(System.currentTimeMillis()).getTime());
        holder.date.setText(at);

        Glide.with(context).load(data.getImage()).centerCrop().into(holder.image);
*/


    }

    @Override
    public int getItemCount() {
        return ParaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView Hparam,value;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Hparam=itemView.findViewById(R.id.HealthParam);
            value=itemView.findViewById(R.id.value);
        }
    }



}

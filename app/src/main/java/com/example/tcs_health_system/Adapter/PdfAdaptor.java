package com.example.tcs_health_system.Adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tcs_health_system.Modal.HealthParam;
import com.example.tcs_health_system.Modal.pdfView;
import com.example.tcs_health_system.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.tcs_health_system.Login.PREFS_NAME;

public class PdfAdaptor extends RecyclerView.Adapter<PdfAdaptor.ViewHolder> {

    Context context;
    List<pdfView> pdfList;

    public PdfAdaptor(Context context, List<pdfView> pdfList) {
        this.context = context;
        this.pdfList = pdfList;
    }


    @NonNull
    @Override
    public PdfAdaptor.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.pdf_row,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfAdaptor.ViewHolder holder, int position) {
        pdfView p=pdfList.get(position);
        holder.filename.setText(p.getFileName());

    }

    @Override
    public int getItemCount() {
        return pdfList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView filename;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            filename=itemView.findViewById(R.id.filename);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences pref=context.getSharedPreferences(PREFS_NAME,0);
                   String Uame =pref.getString("user",null);

                    DatabaseReference dbr= FirebaseDatabase.getInstance().getReferenceFromUrl("https://tcs-health-system-default-rtdb.firebaseio.com/users").
                            child(Uame).child("files");
                    DatabaseReference sub=dbr.child(filename.getText().toString());
                    sub.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(snapshot.child("File_Source").getValue().toString()), "application/pdf");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Intent newIntent = Intent.createChooser(intent, "Open File");
                            try {
                                context.startActivity(newIntent);
                            } catch (ActivityNotFoundException e) {
                                // Instruct the user to install a PDF reader here, or something
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });


        }
    }
}

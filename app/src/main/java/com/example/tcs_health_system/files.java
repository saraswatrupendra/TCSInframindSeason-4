package com.example.tcs_health_system;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.example.tcs_health_system.Adapter.PdfAdaptor;
import com.example.tcs_health_system.Modal.pdfView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.tcs_health_system.Login.PREFS_NAME;

public class files extends AppCompatActivity {
    private Uri imgUri;
    private static final int Gall_Code = 1;
    DatabaseReference dbr;
    StorageReference storageReference;
    ProgressDialog progressDialog;
     EditText item;
    private String Uame;
    RecyclerView recyclerView;
    private PdfAdaptor pdfAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        getSupportActionBar().setTitle("Upload/Display Files");

        SharedPreferences pref=getSharedPreferences(PREFS_NAME,0);
        Uame =pref.getString("user",null);


        storageReference= FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://tcs-health-system.appspot.com");

        progressDialog=new ProgressDialog(this);


        dbr= FirebaseDatabase.getInstance().getReferenceFromUrl("https://tcs-health-system-default-rtdb.firebaseio.com/users").
                child(Uame).child("files");


        findViewById(R.id.pdfrecy);

        recyclerView=findViewById(R.id.pdfrecy);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        retriveFiles();


        ImageButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPdf();  
           }
        });
    }


    public void addPdf() {
        AlertDialog.Builder alertbuilder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.alertpopup, null);
        item= v.findViewById(R.id.name);
        final Button select = v.findViewById(R.id.select);
        Button save = v.findViewById(R.id.save);
        alertbuilder.setView(v);
        final AlertDialog dialog = alertbuilder.create();
        dialog.show();

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                //intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, Gall_Code);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imgUri!=null) {
                    StorageReference UploadFile = storageReference.child("UploadFiles").child(imgUri.getLastPathSegment());
                    UploadFile.putFile(imgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (!TextUtils.isEmpty(item.getText().toString()) ) {
                                        DatabaseReference subdata = dbr.child(item.getText().toString());
                                        subdata.child("File_Name").setValue(item.getText().toString());
                                        subdata.child("File_Source").setValue(uri.toString());
                                        Toast.makeText(files.this, "File Uploaded Successfully", Toast.LENGTH_LONG).show();
                                    } else
                                        Toast.makeText(files.this, "Give a specific name to Your file", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    dialog.dismiss();
                }
                else{
                    dialog.dismiss();
                    Toast.makeText(files.this, "First select your file", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gall_Code && resultCode == RESULT_OK) {
            imgUri = data.getData();
        }
    }




    public void retriveFiles(){
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<pdfView> pdfList=new ArrayList<pdfView>();

                for(DataSnapshot ds:snapshot.getChildren()){
                    try {
                        pdfView p=new pdfView(ds.child("File_Name").getValue().toString(),ds.child("File_Source").getValue().toString());
                        pdfList.add(p);
                    }catch (Exception e){}
                  }
                pdfAdapter=new PdfAdaptor(files.this,pdfList);
                recyclerView.setAdapter(pdfAdapter);
                pdfAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}

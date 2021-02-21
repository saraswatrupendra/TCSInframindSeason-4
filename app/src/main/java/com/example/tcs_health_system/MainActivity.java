package com.example.tcs_health_system;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.tcs_health_system.Adapter.HealthAdapter;
import com.example.tcs_health_system.Modal.HealthParam;
import com.example.tcs_health_system.service.SendMailTask;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.example.tcs_health_system.Login.PREFS_NAME;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    HealthAdapter blogAdapter;


    RequestQueue queue;
    private static String url="https://httpbin.org/post";
    private String Uame;
    private Timer timer = new Timer();
    HashMap<String, Integer> paramsnew = new HashMap<String, Integer>();
    StringBuilder eod=new StringBuilder("");

    //  HashMap<String, Integer> a= jsonexcute();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Calendar calendar=Calendar.getInstance();

        SharedPreferences pref=getSharedPreferences(PREFS_NAME,0);
        Uame =pref.getString("user",null);

        int curr=calendar.get(Calendar.DAY_OF_MONTH);
        int prev=pref.getInt("day",0);
        int occur=pref.getInt("count",0);
        if(prev!=curr || occur!=curr){
            SharedPreferences.Editor e=pref.edit();
            e.putInt("day",curr);
            e.commit();

            TimerTask task=new TimerTask() {
            @Override
            public void run() {
                endOfDayReport(new EODCallBack() {
                    @Override
                    public void onCallback(String value) {
                        sendEmail("Health Report End Of Day ",value);
                        Log.v("eodReport",value);
                        e.putInt("count",curr);
                        e.commit();
                    }
                });

            }
        };
        long delay = ChronoUnit.MILLIS.between(LocalTime.now(), LocalTime.of(23, 50, 00));
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);

        }

        queue= Volley.newRequestQueue(this);

        recyclerView=findViewById(R.id.recy);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                HashMap<String, Integer> a= jsonexcute();

                if(a.size()!=0) {
                    SimpleDateFormat formattertime = new SimpleDateFormat("HH:mm:ss");
                    SimpleDateFormat formatterday = new SimpleDateFormat("dd:MM:YYYY");
                    Date date = new Date();

                    DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://tcs-health-system-default-rtdb.firebaseio.com/users").
                            child(Uame).child("HealthParameter").child(formatterday.format(date)).child(formattertime.format(date));

                    dbr.child("Cholesterol (mg per dl)").setValue(a.get("Cholesterol (mg/dl)"));
                    dbr.child("Blood Pressure (mm-Hg)").setValue(a.get("Blood Pressure (mm-Hg)"));
                    dbr.child("Blood Sugar (mg per dl)").setValue(a.get("Blood Sugar (mg/dl)"));
                    dbr.child("FEV (%)").setValue(a.get("FEV (%)"));
                    dbr.child("Oxygen Saturation (%)").setValue(a.get("Oxygen Saturation (%)"));
                    dbr.child("Pulse rate (beats per min)").setValue(a.get("Pulse rate (beats/min)"));
                    dbr.child("Respiratory Rate (breath per min)").setValue(a.get("Respiratory Rate (breath/min)"));

                    Log.v("thiddate", formatterday.format(date));
                    String st=HealthStatus(a.get("Blood Pressure (mm-Hg)"), a.get("Blood Sugar (mg/dl)"), a.get("Cholesterol (mg/dl)")
                            , a.get("FEV (%)"), a.get("Oxygen Saturation (%)"), a.get("Pulse rate (beats/min)"), a.get("Respiratory Rate (breath/min)"));
                }
            }
        }, 0,30000);

    }


    private HashMap<String, Integer> jsonexcute() {

        HashMap<String, Integer> params = new HashMap<String, Integer>();

        params.put("Oxygen Saturation (%)", (int)(new Random().nextInt(5)+95));
        params.put("Respiratory Rate (breath/min)", (int)new Random().nextInt(15)+10);
        params.put("Pulse rate (beats/min)", (int)new Random().nextInt(65)+60);
        params.put("Blood Pressure (mm-Hg)", (int)new Random().nextInt(70)+80);
        params.put("Blood Sugar (mg/dl)", (int)new Random().nextInt(135)+70);
        params.put("Cholesterol (mg/dl)", (int)new Random().nextInt(65)+180);
        params.put("FEV (%)", (int)new Random().nextInt(40)+37);


        JsonObjectRequest jos=new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                        JSONObject job = null;
                        try {
                            Log.e("pololo", String.valueOf(response.getJSONObject("json")));

                            List<HealthParam> ParaList = new ArrayList<>();

                            job = response.getJSONObject("json");
                            Iterator<String> iter = job.keys();
                            HealthParam item;

                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    int value = job.getInt(key);
                                    item = new HealthParam(key, job.getInt(key));
                                    ParaList.add(item);

                                    paramsnew.put(key,value);
                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }

                            blogAdapter = new HealthAdapter(MainActivity.this, ParaList);
                            recyclerView.setAdapter(blogAdapter);
                            blogAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Log.v("cvbnml",""+paramsnew);

        queue.add(jos);
        return paramsnew;
    }


    public String HealthStatus(int bp,int sugar,int choles,int fev,int OxySat,int pulse,int resp){
        StringBuilder sb=new StringBuilder("");
        if(sugar>140 && sugar<200){
            sb.append("pre-diabetes, ");
        }
        if(sugar>200){
            sb.append("diabetes ");
            sendEmail("Emergency Condition Alert","Your sugar level is = "+sugar+"mg/dl which is above 200 mg/dl, you have risk of diabetes,consult to doctors");
        }
        if(resp<12 && fev<40){
            sb.append("Bronchietises, ");
        }
        if(bp>135 && choles>240 && pulse>84){
            sb.append("CHD ");
        }
        if (OxySat<96){
            sb.append("hypoxemia, ");
        }
        if(OxySat<95 && bp>135 && resp>20 && pulse>100){
            sb.append("moderate asthma, ");
            sendEmail("Emergency Condition Alert","Your oxygen saturation is = "+OxySat+"% which is below 95% , Blood Pressure = " +bp+"mm-Hg which is above 135 mm-Hg , Respiratory Rate = "+resp+" breath/min which is above 20 breath/min , "+pulse+" beats/min which is above 100 beats/min you have risk of ASTHMA,consult to doctors");

        }
        if(sb==null || sb.toString().equals(""))
            return "Hi, I am TCS Health ChatBot based on health parameters "+"You are free from all disease.".toUpperCase()+" You can also find your earlier uploaded files by providing name of particular file";
        return "Hi, I am TCS Health ChatBot based on health parameters You have chances of "+sb.toString().toUpperCase()+" You can also find your earlier uploaded files by providing name of particular file";
    }

    public void sendEmail(String subject,String emailbody)
    {

        String toEmails=Uame.replace(',','.');
        List<String> toEmailList = Arrays.asList(toEmails.split("\\s*,\\s*"));

        new SendMailTask(MainActivity.this).execute("tcshealthsystem@gmail.com", "tcshealth", toEmailList, subject, emailbody);
    }

    public void endOfDayReport(EODCallBack eodCB){
        SimpleDateFormat formatterday = new SimpleDateFormat("dd:MM:YYYY");
        Date date = new Date();
        final int[] c = {0};

        DatabaseReference dbr = FirebaseDatabase.getInstance().getReferenceFromUrl("https://tcs-health-system-default-rtdb.firebaseio.com/users").
                child(Uame).child("HealthParameter").child(formatterday.format(date));

        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String str="";
                 double bp = 0;
                 double sugar = 0;
                 double chole = 0;
                 double fev = 0;
                 double oxysat = 0;
                 double pulse = 0;
                 double resp = 0;
                 double size = snapshot.getChildrenCount();
                for(DataSnapshot ds:snapshot.getChildren()){
                        for (DataSnapshot cds : ds.getChildren()) {
                            if (cds.getKey().equals("Blood Pressure (mm-Hg)")) {
                                bp += Integer.parseInt(cds.getValue().toString());
                            } else if (cds.getKey().equals("Blood Sugar (mg per dl)")) {
                                sugar += Integer.parseInt(cds.getValue().toString());
                            } else if (cds.getKey().equals("Cholesterol (mg per dl)")) {
                                chole += Integer.parseInt(cds.getValue().toString());
                            } else if (cds.getKey().equals("FEV (%)")) {
                                fev += Integer.parseInt(cds.getValue().toString());
                            } else if (cds.getKey().equals("Oxygen Saturation (%)")) {
                                oxysat += Integer.parseInt(cds.getValue().toString());
                            } else if (cds.getKey().equals("Pulse rate (beats per min)")) {
                                pulse += Integer.parseInt(cds.getValue().toString());
                            } else if (cds.getKey().equals("Respiratory Rate (breath per min)")) {
                                resp += Integer.parseInt(cds.getValue().toString());
                            }

                            //Log.v("eoddata", cds.getKey() + ":" + cds.getValue());
                            size = snapshot.getChildrenCount();
                            //eod.append(
                            str = "Blood Pressure (mm-Hg) : " + (int) (bp / size) + "\n Blood Sugar (mg per dl) : " + (int) (sugar / size) +
                                    "\n FEV (%) : " + (int) (fev / size) + "\n Cholesterol (mg per dl) : " + (int) (chole / size) +
                                    "\n Oxygen Saturation (%) : " + (int) (oxysat / size) + "\n Pulse rate (beats per min) : " + (int) (pulse / size) +
                                    "\n Respiratory Rate (breath per min) : " + (int) (resp / size);

                        }
                    }
                if(c[0] ==0){
                eodCB.onCallback(str);
                c[0]++;
                    Log.v("eodW",size+" "+bp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.Updf:{
                startActivity(new Intent(MainActivity.this,files.class));
                return true;
            }
            case R.id.ChatBot: {
               String message= HealthStatus(paramsnew.get("Blood Pressure (mm-Hg)"), paramsnew.get("Blood Sugar (mg/dl)"), paramsnew.get("Cholesterol (mg/dl)")
                        , paramsnew.get("FEV (%)"), paramsnew.get("Oxygen Saturation (%)"), paramsnew.get("Pulse rate (beats/min)"), paramsnew.get("Respiratory Rate (breath/min)"));

                Intent intent = new Intent(MainActivity.this, chatbot.class);
                intent.putExtra("message", message);
                startActivity(intent);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public interface EODCallBack {
        void onCallback(String value);
    }
    
    
     @Override
    public void onBackPressed() {
        finishAffinity();
        finish();
    }

}

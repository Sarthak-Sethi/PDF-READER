package com.example.pdfreader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;

import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.nio.file.Files;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
Adapter adapter;
ListView lis;
ArrayList<String> data = new ArrayList<>();
private static final int REQUEST_PERMISSION = 1234;
    private static final int PERMISSIONCOUNT = 2;
 private static final String[]  PERMISSIONS =
         {Manifest.permission.READ_EXTERNAL_STORAGE ,Manifest.permission.WRITE_EXTERNAL_STORAGE };
 private boolean readmode = false;
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lis = findViewById(R.id.listview);
        adapter = new Adapter();
        lis.setAdapter(adapter);
//        for(int i=0;i<100;i++){
//            data.add("hello"+i);
//        }
//        adapter.setData(data);
        lis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.filemode).setVisibility(View.GONE);
                PDFView pdfView = findViewById(R.id.pdfview);
                pdfView.setVisibility(View.VISIBLE);
                File pdfile  = new File(data.get(position));
                pdfView.fromFile(pdfile).load();
                readmode = true;
            }
        });
        lis.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                findViewById(R.id.topbar).setVisibility(View.VISIBLE);
                return true;
            }
        });
    }
    public void onBackPressed() {
        if(readmode){
            readmode= false;
            findViewById(R.id.pdfview).setVisibility(View.GONE);
            findViewById(R.id.filemode).setVisibility(View.VISIBLE);
        }else{
            super.onBackPressed();
        }
    }
   // @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        //if(notpermissin()){
            Log.e("sarthak","on resume");
            requestPermissions(PERMISSIONS,REQUEST_PERMISSION);
        //}

    }
    private boolean notpermissin(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int perptr = 0;
            while(perptr<PERMISSIONCOUNT){
                Log.e("sarthak","in while");
                if (checkSelfPermission(PERMISSIONS[perptr])!= PackageManager.PERMISSION_GRANTED)
                {
                    Log.e("sarthak","return true");
                    return true;
                }
                perptr++;
            }
            Log.e("sarthak","outside while");
        }
        return false;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.e("sarthak","onRequestPermissionsResult");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISSION && grantResults.length>0){
            if(notpermissin()){
                Log.e("sarthak","permission false");
                ((ActivityManager) this.getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                recreate();
                // clear data of user denies the permission
            }else{
                Log.e("sarthak","permission false  in load data");
                loaddata();
            }
        }
    }
    private void loaddata() {
        data.clear();
        File downloadsfolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] files = downloadsfolder.listFiles();
       // File[] files = getExternalFilesDirs(Environment.DIRECTORY_DOWNLOADS);
        Log.e("a","length is"+files.length);
        for(int i=0;i<files.length;i++){
            String filename = files[i].getAbsolutePath();
             if(filename.endsWith(".pdf")){
            data.add(filename);
            }
        }
        adapter.setData(data);

    }

    class  Adapter extends BaseAdapter {
        ArrayList<String> data = new ArrayList<>();

        void setData(List<String> mdata) {
            data.clear();
            data.addAll(mdata);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.items,parent,false);
            }
            TextView textView = convertView.findViewById(R.id.item);
            String text =data.get(position);
            textView.setText(text.substring(text.lastIndexOf('/'+1)));
            return convertView;
        }
    }
    }


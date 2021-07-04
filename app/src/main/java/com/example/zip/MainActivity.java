package com.example.zip;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.*;

public class MainActivity extends AppCompatActivity {

    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = SDPath + "/instinctcoder/zipunzip/data/" ;
    private String zipPath = SDPath + "/instinctcoder/zipunzip/zip/" ;
    private String unzipPath = SDPath + "/instinctcoder/zipunzip/unzip/" ;

    final static String TAG = MainActivity.class.getName();

    Button btnUnzip, btnZip;
    CheckBox chkParent;
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t = (TextView)findViewById(R.id.tv);
        btnUnzip = (Button) findViewById(R.id.btnUnzip);
        btnUnzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileHelper.unzip(zipPath + "dummy.zip",unzipPath)) {
                    Toast.makeText(MainActivity.this,"Unzip successfully.",Toast.LENGTH_LONG).show();
                }


            }
        });


        chkParent = (CheckBox) findViewById(R.id.chkParent);

        btnZip = (Button) findViewById(R.id.btnZip);
        btnZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FileHelper.zip(dataPath, zipPath, "dummy.zip", chkParent.isChecked())){
                    Toast.makeText(MainActivity.this,"Zip successfully.",Toast.LENGTH_LONG).show();
                }
            }
        });




        //Create dummy files
        FileHelper.saveToFile(dataPath,"This is dummy data 01", "Dummy1.txt");
        FileHelper.saveToFile(dataPath,"This is dummy data 02", "Dummy2.txt");
        FileHelper.saveToFile(dataPath,"This is dummy data 03", "Dummy3.txt");



    }

    int requestcode = 1;

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {

        super.onActivityResult(requestCode,resultCode,data);
        System.out.println(SDPath);
        if(requestCode == requestcode && resultCode == Activity.RESULT_OK)
        {
            if (data == null)
            {
                return;
            }
            if (null != data.getClipData()){

                String temp = "";
                for(int i=0;i<data.getClipData().getItemCount();i++)
                {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    System.out.print(uri.getPath());
                    String s1 = uri.getPath();
                    String s2 = s1.replaceFirst("/external_files","");
                    String s3 = s2.replaceFirst("primary:","");
                    temp +=  SDPath+s3 + "\n";
                    File f1 = new File(SDPath+s3);
                    File f2 = new File( SDPath + "/instinctcoder/zipunzip/data"+"/"+uri.getLastPathSegment());
                    try {
                        copyFile(f1,f2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                t.setText(temp);
            }
            else
            {
                Uri uri = data.getData();
                String s1 = uri.getPath();
                String s2 = s1.replaceFirst("/external_files","");
                String s3 = s2.replaceFirst("primary:","");
                File f1 = new File(SDPath+s3);
                File f2 = new File(SDPath + "/instinctcoder/zipunzip/data"+"/"+uri.getLastPathSegment());
                t.setText(SDPath+s3);
                try {
                    copyFile(f1,f2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void picker(View view) {

        Uri selectedUri = Uri.parse(SDPath);
        Intent filesIntent;
        filesIntent = new Intent(Intent.ACTION_GET_CONTENT);
        filesIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        filesIntent.addCategory(Intent.CATEGORY_OPENABLE);
        filesIntent.setDataAndType(selectedUri,"*/*");  //use image/* for photos, etc.
        startActivityForResult(filesIntent,requestcode);
    }

    public static void copyFile(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < sourceLocation.listFiles().length; i++) {

                copyFile(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);

            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }

    }
}
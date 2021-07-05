package com.example.zip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {


    private String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String dataPath = SDPath + "/instinctcoder/zipunzip/data/" ;
    private String zipPath = SDPath + "/instinctcoder/zipunzip/zip/" ;
    private String unzipPath = SDPath + "/instinctcoder/zipunzip/unzip/" ;
    private String filename = "";
    private String zipFileName = "";
    private String unzipFolderName = "";

    final static String TAG = MainActivity.class.getName();

    Button btnUnzip, btnZip;
    CheckBox chkParent;
    TextView tw1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tw1 = (TextView)findViewById(R.id.tv);
        btnUnzip = (Button) findViewById(R.id.btnUnzip);
        btnUnzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                takenameforUNzip(v);


            }
        });


        chkParent = (CheckBox) findViewById(R.id.chkParent);

        btnZip = (Button) findViewById(R.id.btnZip);
        btnZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Date date = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-ddhh:mm:ss");
                String strDate = dateFormat.format(date);*/
                takename(v);

            }
        });




        //Create dummy files
       /* FileHelper.saveToFile(dataPath,"This is dummy data 01", "Dummy1.txt");
        FileHelper.saveToFile(dataPath,"This is dummy data 02", "Dummy2.txt");
        FileHelper.saveToFile(dataPath,"This is dummy data 03", "Dummy3.txt");*/



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
                            filename = new File(uri.getPath()).getName();
                            System.out.println("last:"+filename);
                            String s2=s1.replace("/external_files","");
                            String s3=s2.replace("primary:","");
                            String s4=s3.replaceFirst("/document","");
                            temp +=  SDPath+s4 + "\n";
                            File f1 = new File(SDPath+s4);
                            File f2 = new File( SDPath + "/instinctcoder/zipunzip/data"+"/"+filename);
                            try {
                                copyFile(f1,f2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        tw1.setText(temp);
                    }
                    else
                    {
                        Uri uri = data.getData();
                        System.out.println(uri.getLastPathSegment());
                        String s1 = uri.getPath();
                        filename = new File(uri.getPath()).getName();
                        System.out.println("last:"+filename);
                        String s2=s1.replace("/external_files","");
                        String s3=s2.replace("primary:","");
                        String s4=s3.replaceFirst("/document","");
                        File f1 = new File(SDPath+s4);
                        File f2 = new File(SDPath + "/instinctcoder/zipunzip/data"+"/"+filename);
                        tw1.setText(SDPath+s4);
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


    public void takename(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                zipFileName = input.getText().toString();
                if (FileHelper.zip(dataPath, zipPath, zipFileName+".zip", chkParent.isChecked())){
                    Toast.makeText(MainActivity.this,"Zip successfully.",Toast.LENGTH_LONG).show();
                    File del = new File(dataPath+"/");
                    System.out.println("data"+del);
                    FileHelper.deleteRecursive(del);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void takenameforUNzip(View view){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unzipFolderName = input.getText().toString();
                if (FileHelper.unzip(dataPath+filename,unzipPath+"/"+unzipFolderName)) {
                    Toast.makeText(MainActivity.this,"Unzip successfully.",Toast.LENGTH_LONG).show();
                    File del = new File(dataPath+"/");
                    System.out.println("data"+del);
                    FileHelper.deleteRecursive(del);
                }


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }
}
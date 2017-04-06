package com.example.akash.videoencrptionexa;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.maxcom.http.LocalSingleHttpServer;
import fr.maxcom.libmedia.Licensing;

public class MainActivity extends AppCompatActivity implements VideoDialogFragment.DialogFragmentToActivity {
    private final static String ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG";
    private final static String ALGO_SECRET_KEY_GENERATOR = "AES";
    private final static int IV_LENGTH = 16;
    private Cursor mVideoCursor;
    private ArrayList<HashMap<String, String>> listOfVideo;
    @InjectView(R.id.btnDecrypt)
    Button btnDecrypt;
    @InjectView(R.id.btnEncrypt)
    Button btnEncrypt;
    @InjectView(R.id.tvText)
    TextView tvText;
    @InjectView(R.id.vdVideoView)
    VideoView vdVideoView;
    @InjectView(R.id.btnSelectVideo)
    Button btnSelectVideo;
    private File inFile;
    private File outFile;
    private File urlOutFile;
    private File outFile_dec;
    private File urlOutFile_dec;
    private SecretKey key;
    private byte[] keyData;
    private SecretKey keyFromKeydata;
    private AlgorithmParameterSpec paramSpec;
    private byte[] iv;
    private String path;
    private String encrypted_path;
    private String selectedVideoPath;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Toast.makeText(MainActivity.this,"result got activity"+requestCode+resultCode,Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        listOfVideo = new ArrayList();

        final String[] videoColumns = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DISPLAY_NAME};
        mVideoCursor = getApplicationContext().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoColumns, null, null, null);
        mVideoCursor.moveToFirst();
        for (int i = 0; i < mVideoCursor.getCount(); i++) {
            listOfVideo.add(new HashMap<String, String>() {
                {
                    put("data", String.valueOf(mVideoCursor.getString(mVideoCursor.getColumnIndex(MediaStore.Video.Media.DATA))));
                    put("duration", String.valueOf(mVideoCursor.getString(mVideoCursor.getColumnIndex(MediaStore.Video.Media.DURATION))));
                    put("displayName", String.valueOf(mVideoCursor.getString(mVideoCursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME))));
                    put("size", String.valueOf(mVideoCursor.getString(mVideoCursor.getColumnIndex(MediaStore.Video.Media.SIZE))));
                    mVideoCursor.moveToNext();

                }
            });
        }
        mVideoCursor.close();

        Licensing.allow(getApplicationContext());

        //first or any particular video from sdcard
        //  tvText.setText(""+listOfVideo.get(0).get("data"));
        //path of video saved in sdcard
        path = listOfVideo.get(0).get("data");


        //playing the video to be encrypted
//        vdVideoView.setVideoPath(path);
//        vdVideoView.start();


//        inFile = new File(listOfVideo.get(0).get("data"));
//        outFile = new File(path.substring(0, path.lastIndexOf("/"))+"/encrypted_video.swf");
//        encrypted_path=path.substring(0, path.lastIndexOf("/"))+"/encrypted_video.swf";
//        urlOutFile = new File(path.substring(0, path.lastIndexOf("/"))+"/url_encrypted_video.swf");
//       // outFile_dec = new File(path.substring(0, path.lastIndexOf("/"))+"/decrypted_video.mp4");
//        urlOutFile_dec = new File(path.substring(0, path.lastIndexOf("/"))+"/url_decrypted_video.mp4");

        try {
            key = KeyGenerator.getInstance(ALGO_SECRET_KEY_GENERATOR).generateKey();


            keyData = key.getEncoded();
            keyFromKeydata = new SecretKeySpec(keyData, 0, keyData.length, ALGO_SECRET_KEY_GENERATOR); //if you want to store key bytes to db so its just how to //recreate back key from bytes array
            iv = new byte[IV_LENGTH];

            SecureRandom.getInstance(ALGO_RANDOM_NUM_GENERATOR).nextBytes(iv);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        paramSpec = new IvParameterSpec(iv);


        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inFile = new File(selectedVideoPath);
                outFile = new File(selectedVideoPath.substring(0, selectedVideoPath.lastIndexOf("/")) + "/encrypted_video.swf");
                encrypted_path = selectedVideoPath.substring(0, selectedVideoPath.lastIndexOf("/")) + "/encrypted_video.swf";
                // urlOutFile = new File(selectedVideoPath.substring(0, selectedVideoPath.lastIndexOf("/"))+"/url_encrypted_video.swf");
                outFile_dec = new File(selectedVideoPath.substring(0, selectedVideoPath.lastIndexOf("/")) + "/decrypted_video.mp4");
                // urlOutFile_dec = new File(selectedVideoPath.substring(0, selectedVideoPath.lastIndexOf("/"))+"/url_decrypted_video.mp4");


                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        Toast.makeText(MainActivity.this, "" + selectedVideoPath + " Encrypted Successfully!!", Toast.LENGTH_SHORT).show();
                        boolean deleted = inFile.delete();
                        if (deleted) {
                            Toast.makeText(MainActivity.this, "" + selectedVideoPath + " deleted Successfully!!", Toast.LENGTH_SHORT).show();

                        }
                        refreshSDCard();
                        //playing the video we have encrypted
                        vdVideoView.stopPlayback();
                        vdVideoView.setVideoPath(selectedVideoPath.substring(0, selectedVideoPath.lastIndexOf("/")) + "/encrypted_video.swf");

                        vdVideoView.start();


                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        try {
                            //encrypting the video from sdcard
                            Encrypter.encrypt(key, paramSpec, new FileInputStream(inFile), new FileOutputStream(outFile));

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (NoSuchPaddingException e) {
                            e.printStackTrace();
                        } catch (InvalidAlgorithmParameterException e) {
                            e.printStackTrace();
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }
                }.execute();
            }
        });


        btnSelectVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoDialogFragment videoDialogFragment = VideoDialogFragment.newInstance(listOfVideo);
                videoDialogFragment.show(getFragmentManager(), "");
            }
        });


        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<HashMap<String,String>>  decryptedList= searchFile(Environment.getExternalStorageDirectory());

                VideoDialogFragment videoDialogFragment = VideoDialogFragment.newInstance(decryptedList);
                videoDialogFragment.show(getFragmentManager(), "");
//                new AsyncTask<Void, Void, String>()
//
//                {
//
//                    @Override
//                    protected String doInBackground(Void... voids) {
//                        String decryptpath=null;
//                        try {
//
//                            //decrypting the video saved in sdcard
//                             decryptpath = Encrypter.decrypt(keyFromKeydata, paramSpec, new FileInputStream(outFile), encrypted_path);
//
//
////                            //decrypting the streaming video file saved from url
////                            Encrypter.decrypt(keyFromKeydata, paramSpec, new FileInputStream(urlOutFile), new FileOutputStream(urlOutFile_dec));
//
//                        } catch (NoSuchAlgorithmException e) {
//                            e.printStackTrace();
//                        } catch (NoSuchPaddingException e) {
//                            e.printStackTrace();
//                        } catch (InvalidKeyException e) {
//                            e.printStackTrace();
//                        } catch (InvalidAlgorithmParameterException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        return decryptpath;
//                    }
//
//                    @Override
//                    protected void onPostExecute(String s) {
//                        Toast.makeText(MainActivity.this, "" + selectedVideoPath + " Decrypted Successfully!!", Toast.LENGTH_SHORT).show();
//                        vdVideoView.stopPlayback();
//                        vdVideoView.setVideoPath(s);
//                        vdVideoView.start();
//                        super.onPostExecute(s);
//                    }
//                }.execute();


            }
        });

    }

    ArrayList<HashMap<String,String>> arrayList;

    public ArrayList<HashMap<String,String>>   searchFile(File dir) {
        String pdfPattern = ".swf";
        final File[] listFile = dir.listFiles();

        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    searchFile(listFile[i]);
                }
                else {
                    if (listFile[i].getName().endsWith(pdfPattern)){
                        final int indx=i;
                        arrayList = new ArrayList();
                        arrayList.add(new HashMap<String, String>()
                        {
                            {
                                put("data",listFile[indx].getAbsolutePath());
                                put("displayName",listFile[indx].getName());
                            }
                        });
                    }
                }
            }
        }
        return arrayList;
    }
private void refreshSDCard()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(inFile);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
        } else {
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }




    @Override
    public void onReturningResult(Intent data) {
        Toast.makeText(MainActivity.this,"Result Received at activity",Toast.LENGTH_SHORT).show();
        selectedVideoPath=data.getExtras().get("data").toString();
        tvText.setText(selectedVideoPath);

        vdVideoView.setVideoPath(selectedVideoPath);
        vdVideoView.start();


    }
}


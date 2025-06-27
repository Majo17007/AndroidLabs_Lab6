package com.example.androidlabs_lab6;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;
    private Bitmap currentCatImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        new CatImages().execute();
    }

    private class CatImages extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            while (true) {
                try {
                    // Step 1: Get JSON
                    URL url = new URL("https://cataas.com/cat?json=true");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder json = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        json.append(line);
                    }

                    JSONObject jsonObject = new JSONObject(json.toString());
                    String catId = jsonObject.getString("id");
                    String catUrl = "https://cataas.com/cat/" + catId;

                    File file = new File(getFilesDir(), catId + ".jpg");
                    if (!file.exists()) {
                        // Download image
                        InputStream in = new URL(catUrl).openStream();
                        FileOutputStream out = new FileOutputStream(file);

                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                        out.close();
                        in.close();
                    }

                    currentCatImage = BitmapFactory.decodeFile(file.getAbsolutePath());

                    // Reset progress and display new image
                    publishProgress(0);

                    // Progress countdown
                    for (int i = 0; i <= 100; i++) {
                        publishProgress(i);
                        Thread.sleep(30); // Adjust speed as you like
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
            if (values[0] == 0 && currentCatImage != null) {
                imageView.setImageBitmap(currentCatImage);
            }
        }
    }
}

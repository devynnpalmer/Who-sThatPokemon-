package com.devynnpalmer.whosthatpokemon;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WTPMain extends AppCompatActivity {

    ArrayList<String> pokeURLs = new ArrayList<String>();
    ArrayList<String> pokeNames = new ArrayList<String>();
    int chosenPoke = 0;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void pokeChose(View view) {

        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))) {

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getApplicationContext(), "Wrong! It was " + pokeNames.get(chosenPoke), Toast.LENGTH_SHORT).show();

        }

        createNewQuestion();

    }


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;

            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                StringBuilder result = new StringBuilder();
                URL url = new URL(params[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int charRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charRead = inputStreamReader.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }
                    result.append(String.copyValueOf(inputBuffer, 0, charRead));
                }
                return result.toString();
            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wtpmain);

        imageView = (ImageView) findViewById(R.id.imageView);
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        DownloadTask task = new DownloadTask();

        String result = null;

        try {
            result = task.execute("http://pokemongo.gamepress.gg/pokemon-list").get();

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(result);

            while (m.find()) {
                pokeURLs.add(m.group(1));

            }
            p = Pattern.compile("<alt=\"(.*?)\"");
            m = p.matcher(result);

            while (m.find()) {

                pokeNames.add(m.group(1));

            }

        } catch (InterruptedException e) {

            e.printStackTrace();

        } catch (ExecutionException e) {

            e.printStackTrace();
        }

        createNewQuestion();
    }


    public void createNewQuestion() {

        Random random = new Random();
        chosenPoke = random.nextInt(pokeURLs.size());

        ImageDownloader imageTask = new ImageDownloader();

        Bitmap pokeImage;

        try {

            pokeImage = imageTask.execute(pokeURLs.get(chosenPoke)).get();

            imageView.setImageBitmap(pokeImage);

            locationOfCorrectAnswer = random.nextInt(4);

            int incorrectAnswerLocation;

            for(int i=0;i<4; i++) {

                if (i == locationOfCorrectAnswer) {

                    answers[i] = pokeNames.get(chosenPoke);

                } else {

                    incorrectAnswerLocation = random.nextInt(pokeURLs.size());

                    while (incorrectAnswerLocation == chosenPoke) {

                        incorrectAnswerLocation = random.nextInt(pokeURLs.size());
                    }

                    answers[i] = pokeNames.get(incorrectAnswerLocation);

                }
            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





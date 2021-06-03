package com.example.guessthecountry;
/*
At first I created DownloadTask class and with that class I downloaded the HTML codes of the related
web site as a  string value.

After that I find out the relevant parts of the HTML codes with using regex. And created two lists;
one for country names,and the other one for urls of the flags.

And than I have created the "game" method. At there firstly I created a random int value(in range
of the length of the country list) to select a country from the country list. Secondly I created a
random integer value(in range of 4) to select a random button to show the correct answer. Thirdly
I have created a new list for the answers(both right and wrong). After that I have assigned the
texts of the buttons. At last I have downloaded the image of the country's flag with the help of
"ImageDownloader" class. With this way I have created the unique question. To clear out the answers
every time I have added a piece of code to the very beginning of the game method

Finally I have created "select" method for button interaction. At first I get the tag of the pressed
button and tried to find out if it is the correct button or not with a simple if-else structure.
After that I called the game method to create a new question.

 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    ArrayList<String> countryNames = new ArrayList<String>();
    ArrayList<String> imageUrls = new ArrayList<String>();
    ArrayList<String> answers = new ArrayList<String>();
    Random r = new Random();
    ImageView imageView;
    int correctButtonIndex = 5;


    public void select(View view){

        int pressedButton = Integer.parseInt(view.getTag().toString());
        if (pressedButton == correctButtonIndex){

            Toast.makeText(this,"Correct :)",Toast.LENGTH_SHORT).show();

        }else{

            Toast.makeText(this,"Wrong :(",Toast.LENGTH_SHORT).show();

        }

        game();

    }

    public void game (){
        answers.clear();

        int correctAnsIndex = r.nextInt(countryNames.size());

        String correctAnswer = countryNames.get(correctAnsIndex);
        String imageUrl = "https://www.worldometers.info/img/flags/" + imageUrls.get(correctAnsIndex) + ".gif";

        correctButtonIndex = r.nextInt(4);
        //Log.i("Answer : ",correctAnswer + " : " + Integer.toString(correctButtonIndex));

        for (int i = 0; i < 4; i++){
            if (i == correctButtonIndex){
                answers.add(correctAnswer);
            }else{
                String country = countryNames.get(r.nextInt(countryNames.size()));
                if (country.equals(correctAnswer) || answers.contains(country)){
                    country = countryNames.get(r.nextInt(countryNames.size()));
                }
                answers.add(country);
            }
        }
        button0.setText(answers.get(0));
        button1.setText(answers.get(1));
        button2.setText(answers.get(2));
        button3.setText(answers.get(3));

        ImageDownloader task = new ImageDownloader();
        Bitmap myImage;

        try {
            myImage = task.execute(imageUrl).get();
            imageView.setImageBitmap(myImage);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        imageView = findViewById(R.id.imageView);

        DownloadTask task = new DownloadTask();
        String downloadedResult = null;

        try {

            downloadedResult = task.execute("https://www.worldometers.info/geography/flags-of-the-world/").get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Pattern p = Pattern.compile("<div style=\"font-weight:bold; padding-top:10px\">(.*?)</div>");
        Matcher m = p.matcher(downloadedResult);

        while(m.find()){

            countryNames.add(m.group(1));

        }

        Pattern imgP = Pattern.compile("<img src=\"/img/flags/small/tn_(.*?).gif");
        Matcher imgM = imgP.matcher(downloadedResult);

        while(imgM.find()){

            imageUrls.add(imgM.group(1));

        }
        game();


    }


public class DownloadTask extends AsyncTask<String,Void, String>{


    @Override
    protected String doInBackground(String... urls) {

        String result = "";

        try {

            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader reader = new InputStreamReader(inputStream);
            int data = reader.read();

            while (data != -1){
                char current = (char) data;

                result += current;

                data = reader.read();
            }
            return result;


        }catch (Exception e){
            e.printStackTrace();
            return "Failed";
        }

    }
}


public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

    @Override
    protected Bitmap doInBackground(String... urls) {

        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
}
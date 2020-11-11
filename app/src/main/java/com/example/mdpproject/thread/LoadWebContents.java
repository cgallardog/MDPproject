package com.example.mdpproject.thread;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.mdpproject.MenuActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadWebContents implements Runnable {
    // Class to download a text-based content (e.g. HTML, XML, JSON, ...) from a URL
    // and populate a String with it that will be sent in a Message

    Handler creator; // handler to the main activity, who creates this task
    private String expectedContent_type;
    private String string_URL;


    public LoadWebContents(Handler handler, String cnt_type, String strURL) {
        // The constructor accepts 3 arguments:
        // The handler to the creator of this object
        // The content type expected (e.g. "application/vnd.google-earth.kml+xml").
        // The URL to load.
        creator = handler;
        expectedContent_type = cnt_type;
        string_URL = strURL;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        // initial preparation of the message to communicate with the UI Thread:
        Message msg = new Message();
        msg.setTarget(creator);
        Bundle msg_data = new Bundle();

        // Build the logTag with the Thread and Class names (to identify logs):
        String logTag = MenuActivity.LOGSLOADWEBCONTENT + ", Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        Log.d(logTag, "run() called, starting load");

        String response = ""; // This string will contain the loaded contents of a text resource

        HttpURLConnection urlConnection;

        try {
            URL url = new URL(string_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            String actualContentType = urlConnection.getContentType(); // content type declared by the HTTP server
            InputStream is = urlConnection.getInputStream();

            Log.d("TAG", String.valueOf(expectedContent_type.equals(actualContentType)));
            if (expectedContent_type.equals(actualContentType)) {
                // We check that the actual content type got from the server is the expected one
                // and if it is, download text
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(reader);
                // We read the text contents line by line and add them to the response:
                String line = in.readLine();
                while (line != null) {
                    response += line + "\n";
                    line = in.readLine();
                }
            } else { // content type not supported
                response = "Actual content type different from expected ("+
                        actualContentType + " vs " + expectedContent_type;
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            response = e.toString();
        }

        //Log.d(logTag, "load complete, sending message to UI thread");
        if ("".equals(response) == false) {
            msg_data.putString("text", response);
        }
        msg.setData(msg_data);
        msg.sendToTarget();
    }
}

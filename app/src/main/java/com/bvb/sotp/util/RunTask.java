package com.bvb.sotp.util;


import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

class RunTask extends AsyncTask<String, String, String> {

    private String response;
    String string = "your string parameter";
    String SOAP_ACTION = "EncryptVerifyUser";
    String stringUrl = "";
    //if you experience a problem with url remove the '?wsdl' ending



    @Override
    protected String doInBackground(String... params) {

        try {

            //paste your request structure here as the String body(copy it exactly as it is in soap ui)
            //assuming that this is your request body

            String body = string;

            BufferedReader reader = null;
            try {
                URL url = new URL(stringUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDefaultUseCaches(false);
                conn.setRequestProperty("Accept", "text/xml");
                conn.setRequestProperty("SOAPAction", SOAP_ACTION);

                //push the request to the server address

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(body);
                wr.flush();

                //get the server response

                 reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {


                    builder.append(line);
                    response = builder.toString();//this is the response, parse it in onPostExecute

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {

                    reader.close();
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }


        } catch (Exception e) {

            e.printStackTrace();
        }

        return response;
    }

    /**
     * @see AsyncTask#onPostExecute(Object)
     */
    @Override
    protected void onPostExecute(String result) {



        try {

//            Toast.makeText(this,"Response "+ result,Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package br.com.rubythree.library.models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import android.app.IntentService;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Base64;

public class ExecuteRequest extends IntentService {
    
	int responseCode;
    int method;
    String message, response, entity, httpUsername, httpPassword;
    ArrayList<ParcelableNameValuePair> params;
    ArrayList<ParcelableNameValuePair> headers;
    HttpRequestBase request;
    ResultReceiver receiver;
    private static final String TAG = "[ExecuteRequest]";
    private String url;

    public ExecuteRequest() {
        super("ExecuteRequest");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        params = intent.getParcelableArrayListExtra("params");
        headers = intent.getParcelableArrayListExtra("headers");
        url = intent.getStringExtra("url");
        receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        method = (int) intent.getIntExtra("method", 1);
        entity = intent.getStringExtra("entity");

        httpUsername = intent.getStringExtra("httpUsername");
        httpPassword = intent.getStringExtra("httpPassword");

        try {
            execute(method);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeDelete(String deletePath) throws Exception {
        request = new HttpDelete(url + deletePath);

        // add headers
        for (NameValuePair h : headers) {
            request.addHeader(h.getName(), h.getValue());
        }

        request.addHeader(
                "Authorization",
                "Basic "
                        + Base64.encodeToString(
                                (httpUsername + ":" + httpPassword).getBytes(),
                                Base64.NO_WRAP));

        commit();
    }

    public void execute(int method) throws Exception {
        switch (method) {
        case RestService.GET: {
            String combinedParams = "";
            if (!params.isEmpty()) {
                combinedParams += "?";
                for (NameValuePair p : params) {
                    String paramString = p.getName() + "="
                            + URLEncoder.encode(p.getValue(), "UTF-8");
                    if (combinedParams.length() > 1) {
                        combinedParams += "&" + paramString;
                    } else {
                        combinedParams += paramString;
                    }
                }
            }

            request = new HttpGet(url + combinedParams);

            // add headers
            for (NameValuePair h : headers) {
                request.addHeader(h.getName(), h.getValue());
            }

            request.addHeader(
                    "Authorization",
                    "Basic "
                            + Base64.encodeToString(
                                    (httpUsername + ":" + httpPassword)
                                            .getBytes(), Base64.NO_WRAP));

            commit();
            break;
        }
        case RestService.POST: {
            request = new HttpPost(url);

            // add headers
            for (NameValuePair h : headers) {
                request.addHeader(h.getName(), h.getValue());
            }

            if (!params.isEmpty()) {
                ((HttpPost) request).setEntity(new UrlEncodedFormEntity(params,
                        HTTP.UTF_8));
            }
            if (entity != null && entity != "") {
                StringEntity se = new StringEntity(entity);
                ((HttpPost) request).setEntity(se);
            }

            request.addHeader(
                    "Authorization",
                    "Basic "
                            + Base64.encodeToString(
                                    (httpUsername + ":" + httpPassword)
                                            .getBytes(), Base64.NO_WRAP));

            commit();
            break;
        }
        case RestService.PUT: {
            request = new HttpPut(url);

            // add headers
            for (NameValuePair h : headers) {
                request.addHeader(h.getName(), h.getValue());
            }

            if (!params.isEmpty()) {
                ((HttpPut) request).setEntity(new UrlEncodedFormEntity(params,
                        HTTP.UTF_8));
            }

            request.addHeader(
                    "Authorization",
                    "Basic "
                            + Base64.encodeToString(
                                    (httpUsername + ":" + httpPassword)
                                            .getBytes(), Base64.NO_WRAP));

            commit();
            break;
        }
        case RestService.DELETE: {
            request = new HttpDelete(url);

            // add headers
            for (NameValuePair h : headers) {
                request.addHeader(h.getName(), h.getValue());
            }

            request.addHeader(
                    "Authorization",
                    "Basic "
                            + Base64.encodeToString(
                                    (httpUsername + ":" + httpPassword)
                                            .getBytes(), Base64.NO_WRAP));

            commit();
            break;
        }

        }
    }

    private void commit() {
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance("android");
        HttpResponse httpResponse;
        

        
        try {
            httpResponse = httpClient.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                String response = convertStreamToString(instream);
                Bundle responseBundle = new Bundle();
                responseBundle.putString("result", response);
                responseBundle.putInt("responseCode", responseCode);
                receiver.send(method, responseBundle);

                instream.close(); // Closing the input stream will trigger
                                    // connection release
                httpClient.close();
            }

        } catch (ClientProtocolException e) {
            httpClient.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            httpClient.getConnectionManager().shutdown();
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
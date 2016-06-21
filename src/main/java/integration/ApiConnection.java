/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package integration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import integration.domain.DeleteStolenRequest;
import integration.domain.StolenRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Marijn
 */
public class ApiConnection {

    private static final String BASE_URL_PRODUCTION = "http://api.seclab.marijn.ws";
    private static final String REGULAR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private String apiKey = "";

    public ApiConnection(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean addStolenCar(StolenRequest stolenRequest) {
        try {
            Gson gson = new GsonBuilder().setDateFormat(REGULAR_DATE_FORMAT).create();
            String jsonBody = gson.toJson(stolenRequest);
            StringEntity postingString = new StringEntity(jsonBody, StandardCharsets.UTF_8);

            HttpPost post = new HttpPost(BASE_URL_PRODUCTION + "/api/stolen?api_key=" + this.apiKey);
            post.setEntity(postingString);
            post.setHeader(HTTP.CONTENT_TYPE, "application/json");

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(post);

            Logger.getLogger(ApiConnection.class.getName()).log(Level.INFO, "POST: " + response.getStatusLine().getStatusCode());
            Logger.getLogger(ApiConnection.class.getName()).log(Level.INFO, EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));

            if (response.getStatusLine().getStatusCode() == 200) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(ApiConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean removeStolenCar(DeleteStolenRequest deleteStolenRequest) {
        try {
            Gson gson = new Gson();
            String jsonBody = gson.toJson(deleteStolenRequest);
            StringEntity postingString = new StringEntity(jsonBody, StandardCharsets.UTF_8);

            HttpPost post = new HttpPost(BASE_URL_PRODUCTION + "/api/stolen/_delete?api_key=" + this.apiKey);
            post.setEntity(postingString);
            post.setHeader(HTTP.CONTENT_TYPE, "application/json");

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(post);

            Logger.getLogger(ApiConnection.class.getName()).log(Level.INFO, "DELETE: " + response.getStatusLine().getStatusCode());
            Logger.getLogger(ApiConnection.class.getName()).log(Level.INFO, EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));

            if (response.getStatusLine().getStatusCode() == 200) {
                return true;
            }
        } catch (IOException ex) {
            Logger.getLogger(ApiConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

}

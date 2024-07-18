package com.enit.Erecruitement;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class Geocoder {

    private final String GEOCODING_API_ENDPOINT = "https://geocode.search.hereapi.com/v1/geocode";
    private final String API_KEY = "AIzaSyB5D8Li4SItwtyBP--MHCyUl7eJs8MuHrM" ;

    public Map<String, Double> geocodeSync(String address) {
        RestTemplate restTemplate = new RestTemplate();

        // Build the request URL with the address and API key
        String requestUrl = GEOCODING_API_ENDPOINT + "?q=" + address + "&apiKey=" + API_KEY;
        //String requestUrl = GEOCODING_API_ENDPOINT +"?apiKey=" + API_KEY + "&q=" + address ;

        // Send the request and get the response as a JSON string
        ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);

        // Check if the response is successful
        if (response.getStatusCode() == HttpStatus.OK) {
            // Parse the JSON response using the Jackson ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode responseNode = null;
            try {
                responseNode = objectMapper.readTree(response.getBody());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Get the latitude and longitude from the response
            if (responseNode != null) {
                ArrayNode itemsNode = (ArrayNode) responseNode.get("items");
                if (itemsNode != null && itemsNode.size() > 0) {
                    ObjectNode positionNode = (ObjectNode) itemsNode.get(0).get("position");
                    if (positionNode != null) {
                        double latitude = positionNode.get("lat").asDouble();
                        double longitude = positionNode.get("lng").asDouble();
                        Map<String, Double> location = new HashMap<>();
                        location.put("latitude", latitude);
                        location.put("longitude", longitude);
                        return location;
                    }
                }
            }
        }
        return null;
    }
}

//
//public class Geocoder {
//
//    private static final String GEOCODING_RESOURCE = "https://geocode.search.hereapi.com/v1/geocode";
//    private static final String API_KEY = "AIzaSyB5D8Li4SItwtyBP--MHCyUl7eJs8MuHrM";
//
//    public String GeocodeSync(String query) throws IOException, InterruptedException {
//
//        HttpClient httpClient = HttpClient.newHttpClient();
//
//        String encodedQuery = URLEncoder.encode(query,"UTF-8");
//        String requestUri = GEOCODING_RESOURCE + "?apiKey=" + API_KEY + "&q=" + encodedQuery;
//
//        HttpRequest geocodingRequest = HttpRequest.newBuilder().GET().uri(URI.create(requestUri))
//                .timeout(Duration.ofMillis(2000)).build();
//
//        HttpResponse geocodingResponse = httpClient.send(geocodingRequest,
//                HttpResponse.BodyHandlers.ofString());
//
//        return (String) geocodingResponse.body();
//    }
//
//}
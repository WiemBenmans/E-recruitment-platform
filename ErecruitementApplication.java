package com.enit.Erecruitement;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
@SpringBootApplication
public class ErecruitementApplication {

	public static void main(String[] args) {
	SpringApplication.run(ErecruitementApplication.class, args);
//		String skills = "java,python,sql";
//		List<String> skillsList = Arrays.asList(skills.split(",\\s*"));
//		System.out.println(skillsList);


//		Geocoder geocoder = new Geocoder();
//
//		String address = "1600 Amphitheatre Parkway, Mountain View, CA";
//		Map<String, Double> location = geocoder.geocodeSync(address);
//
//		if (location != null) {
//			System.out.println("Latitude: " + location.get("latitude"));
//			System.out.println("Longitude: " + location.get("longitude"));
//		} else {
//			System.out.println("Failed to geocode address: " + address);
//		}
	}

//	public static void main(String[] args) throws IOException, InterruptedException {
//
//
//		ObjectMapper mapper = new ObjectMapper();
//		Geocoder geocoder = new Geocoder();
//
//		String response = geocoder.GeocodeSync("11 Wall St, New York, NY 10005");
//		JsonNode responseJsonNode = mapper.readTree(response);
//
//		JsonNode items = responseJsonNode.get("items");
//
//		for (JsonNode item : items) {
//			JsonNode address = item.get("address");
//			String label = address.get("label").asText();
//			JsonNode position = item.get("position");
//
//			String lat = position.get("lat").asText();
//			String lng = position.get("lng").asText();
//			System.out.println(label + " is located at " + lat + "," + lng + ".");
//		}
//	}


}





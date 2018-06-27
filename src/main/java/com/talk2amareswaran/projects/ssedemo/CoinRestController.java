package com.talk2amareswaran.projects.ssedemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "*")
public class CoinRestController 
{
	
	private List<AllCoins> coinsList = null;
	
	@Autowired
	CoinEventService coinEventService;

	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {
			String country = "USD";
			initializeCoins(country);
		};
	}

	public void initializeCoins(String country) throws ParseException, IOException {
		coinsList = new ArrayList<>();
		String SearchUrl = null;
		AllCoins product = null;
		SearchUrl = "https://api.coinmarketcap.com/v1/ticker/?limit=10&convert=" + country;
		URL url = new URL(SearchUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		} else {
			System.out.println("URL:: " + SearchUrl);
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			String obj = "";
			String price_usd=null;
			while ((output = br.readLine()) != null) {
				obj = obj + output;
			}
			JSONParser jsonParser = new JSONParser();
			JSONArray jsonArr = (JSONArray) jsonParser.parse(obj);
			for (int i = 0; i < jsonArr.size(); i++) {
				product = new AllCoins();
				JSONObject jobj = (JSONObject) jsonArr.get(i);
				String name = (java.lang.String) jobj.get("name");
				price_usd = (java.lang.String) jobj.get("price_usd");
				product.setName(name);
				product.setPrice_usd(price_usd);
				coinsList.add(product);
			}
		}

	}

	public List<AllCoins> initializeCoinss(String country) throws ParseException, IOException {
		coinsList=new ArrayList<>();
		String SearchUrl = null;
		AllCoins product = null;
		SearchUrl = "https://api.coinmarketcap.com/v1/ticker/?limit=10&convert=" + country;
		URL url = new URL(SearchUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
		} else {
			System.out.println("URL:: " + SearchUrl);
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			String obj = "";
			String price_usd=null;
			while ((output = br.readLine()) != null) {
				obj = obj + output;
			}
			JSONParser jsonParser = new JSONParser();
			JSONArray jsonArr = (JSONArray) jsonParser.parse(obj);
			for (int i = 0; i < jsonArr.size(); i++) {
				product = new AllCoins();
				JSONObject jobj = (JSONObject) jsonArr.get(i);
				String name = (java.lang.String) jobj.get("name");
				if (!country.equalsIgnoreCase("USD")) {
					price_usd = (java.lang.String) jobj.get("price_aud");
				} else {
					price_usd = (java.lang.String) jobj.get("price_usd");
				}
				product.setName(name);
				product.setPrice_usd(price_usd);
				coinsList.add(product);
			}
		}
		return coinsList;

	}
	
	@RequestMapping(value = "/coin/{country}", produces = MediaType.TEXT_EVENT_STREAM_VALUE, method = RequestMethod.GET)
	public Flux<List<AllCoins>> coinEvent(@PathVariable String country) throws ParseException, IOException {
		coinsList = initializeCoinss(country);
		return coinEventService.getCoinsEvents(coinsList, country);
	}
	
}
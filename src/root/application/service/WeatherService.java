package root.application.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import root.application.model.Weather;
import root.application.model.WeatherRequest;
import root.util.Cache;
import root.util.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by Semyon Danilov on 01.10.2014.
 */
public class WeatherService {

    private Cache<WeatherRequest, Weather> cache = new Cache<>();

    private final String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=";

    public Weather getWeather(final WeatherRequest request, final WeatherCallback callback) {
        WeatherThread thread = new WeatherThread(new WeatherRequest("RU", "Saint-Petersburg", new Date()), callback);
        thread.start();
        return null;
    }

    private class WeatherThread extends Thread {

        private WeatherRequest request;
        private WeatherCallback callback;

        public WeatherThread(final WeatherRequest request, final WeatherCallback callback) {
            this.request = request;
            this.callback = callback;
        }

        @Override
        public void run() {
            String q = URLEncoder.encode(request.getCity() + "," + request.getCountry());
            URL url = null;
            try {
                url = new URL(apiUrl + q);
            } catch (MalformedURLException e) {
                System.out.println("Failed to resolve URI: " + e.getMessage());
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int status = connection.getResponseCode();
                if (status == 200) {
                    InputStream is = connection.getInputStream();
                    InputStreamReader reader = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(reader);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    String jsonString = sb.toString();
                    JSONObject jsonObject = (JSONObject) (new JSONParser()).parse(jsonString);
                    System.out.println(jsonObject.toJSONString());

                    String city = (String) jsonObject.get("name");
                    JSONObject main = (JSONObject) jsonObject.get("main");
                    JSONObject sys = (JSONObject) jsonObject.get("sys");
                    JSONObject wind = (JSONObject) jsonObject.get("wind");
                    double degree = NumberUtils.objectToDouble(wind.get("deg"));
                    double speed = NumberUtils.objectToDouble(wind.get("speed"));
                    String country = (String) sys.get("country");
                    Double tempS = NumberUtils.objectToDouble(main.get("temp"));
                    tempS -= 273;
                    Double tempMin = NumberUtils.objectToDouble(main.get("temp_min"));
                    tempMin -= 273;
                    Weather weather = new Weather(request, tempS, tempMin);
                    weather.setCityName(city);
                    weather.setCountryName(country);
                    weather.setWindAngle(degree);
                    JSONArray _weatherArray = (JSONArray) jsonObject.get("weather");
                    JSONObject _weather = (JSONObject) _weatherArray.get(0);
                    String effect = (String) _weather.get("main");
                    weather.setWindSpeed(speed);
                    weather.setEffect(effect);
                    callback.onLoad(weather);
                }

            } catch (IOException e) {
                System.out.println("Failed to load data: " + e.getMessage());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    public static abstract class WeatherCallback {

        public abstract void onLoad(final Weather weather);

    }

}

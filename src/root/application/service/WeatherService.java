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

/**
 * Created by Semyon Danilov on 01.10.2014.
 */
public class WeatherService {

    private Cache<WeatherRequest, Weather> cache = new Cache<>();

    private final String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=";

    public Weather getWeather(final WeatherRequest request, final WeatherCallback callback) {
        Weather weather = cache.get(request);
        if (weather != null) {
            return weather;
        }
        WeatherThread thread = new WeatherThread(request, callback);
        thread.start();
        return null;
    }

    public WeatherPromise getWeatherPromise(final WeatherRequest request) {
        Weather weather = cache.get(request);
        if (weather != null) {
            WeatherPromise promise = new WeatherPromise(null);
            promise.setResult(weather);
            return promise;
        }
        WeatherPromiseCallback callback = new WeatherPromiseCallback();
        WeatherThread thread = new WeatherThread(request, callback);
        final WeatherPromise promise = new WeatherPromise(thread);
        callback.setPromise(promise);
        thread.start();
        return promise;
    }

    private static class WeatherPromiseCallback extends WeatherCallback {

        private WeatherPromise promise;

        public void setPromise(final WeatherPromise promise) {
            this.promise = promise;
        }

        @Override
        public void onLoad(final Weather weather) {
            promise.setResult(weather);
        }

        @Override
        public void onLoad(final String error) {

        }
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
                    Integer code = NumberUtils.objectToInteger(jsonObject.get("cod"));
                    if (code != 200) {
                        callback.onLoad(jsonObject.get("message").toString());
                        return;
                    }
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
                    cache.put(request, weather);
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

        public abstract void onLoad(final String error);

    }

    public static class WeatherPromise {

        private boolean isReady = false;
        private Weather weather = null;
        private Thread thread = null;

        public WeatherPromise(final Thread thread) {
            this.thread = thread;
        }

        public synchronized boolean isReady() {
            return isReady;
        }

        public synchronized void setResult(final Weather weather) {
            this.weather = weather;
            this.isReady = true;
        }

        public Weather waitForResult() {
            try {
                while (!isReady()) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return weather;
        }

    }

}

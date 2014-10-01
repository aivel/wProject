package root.application.service;

import root.application.model.Weather;
import root.application.model.WeatherRequest;
import root.util.Cache;

/**
 * Created by Semyon Danilov on 01.10.2014.
 */
public class WeatherService {

    private Cache<WeatherRequest, Weather> cache = new Cache<>();

    public Weather getWeather(final WeatherRequest request, final WeatherCallback callback) {
        (new Thread() {

            @Override
            public void run() {
                Weather weather = new Weather(request, 24.6);
                callback.onLoad(weather);
            }

        }).start();
        return null;
    }

    public static abstract class WeatherCallback {

        public abstract void onLoad(final Weather weather);

    }

}

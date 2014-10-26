package root.application.service;

import org.omg.CORBA.ORB;
import root.application.model.Weather;
import root.application.model.WeatherRequest;
import root.application.service.WeatherApp.WeatherInterfacePOA;
import root.application.service.WeatherApp.WeatherInterfacePackage.WeatherIDL;

/**
 * Created by Semyon Danilov on 26.10.2014.
 */
public class WeatherImpl extends WeatherInterfacePOA {

    private ORB orb;
    private WeatherService weatherService;

    public void setORB(ORB orb_val) {
        orb = orb_val;
    }

    public void setWeatherService(final WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Override
    public WeatherIDL get(final String city, final String country) {
        System.out.println("Someone connected by IDL and requested weather");
        WeatherService.WeatherPromise weatherPromise = weatherService.getWeatherPromise(new WeatherRequest(country, city, null));
        Weather weather = weatherPromise.waitForResult();
        WeatherIDL weatherIDL = new WeatherIDL(weather.getTemperature(),
                weather.getMinTemperature(),
                weather.getCityName(),
                weather.getCountryName(),
                weather.getWindAngle(),
                weather.getWindSpeed(),
                weather.getEffect());
        return weatherIDL;
    }

}

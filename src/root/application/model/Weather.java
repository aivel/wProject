package root.application.model;

import org.json.simple.JSONObject;

/**
 * Created by Semyon Danilov on 01.10.2014.
 */
public class Weather {

    private WeatherRequest request;

    private double temperature;
    private double minTemperature;
    private String cityName;
    private String countryName;
    private double windAngle;
    private double windSpeed;
    private String effect;

    public Weather(final WeatherRequest request, final double temperature, final double minTemperature) {
        this.request = request;
        this.temperature = temperature;
        this.minTemperature = minTemperature;
    }

    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(final double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(final String cityName) {
        this.cityName = cityName;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    public double getWindAngle() {
        return windAngle;
    }

    public void setWindAngle(final double windAngle) {
        this.windAngle = windAngle;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(final double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(final String effect) {
        this.effect = effect;
    }

    public WeatherRequest getRequest() {
        return request;
    }

    public void setRequest(final WeatherRequest request) {
        this.request = request;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(final double temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("temperature", temperature);
        jsonObject.put("minTemperature", minTemperature);
        jsonObject.put("cityName", cityName);
        jsonObject.put("countryName", countryName);
        jsonObject.put("windAngle", windAngle);
        jsonObject.put("windSpeed", windSpeed);
        jsonObject.put("effect", effect);
        return jsonObject.toJSONString();
    }

}

package root.application.model;

/**
 * Created by Semyon Danilov on 01.10.2014.
 */
public class Weather {

    private WeatherRequest request;

    private double temperature;

    public Weather(final WeatherRequest request, final double temperature) {
        this.request = request;
        this.temperature = temperature;
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
        return "Weather{" +
                "temperature=" + temperature +
                '}';
    }

}

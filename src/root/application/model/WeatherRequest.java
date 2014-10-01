package root.application.model;

import java.util.Date;

/**
 * Created by Semyon Danilov on 01.10.2014.
 */
public class WeatherRequest {

    private String country;

    private String city;

    private Date date;

    public WeatherRequest(final String country, final String city, final Date date) {
        this.country = country;
        this.city = city;
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeatherRequest that = (WeatherRequest) o;

        if (!city.equals(that.city)) return false;
        if (!country.equals(that.country)) return false;
        if (!date.equals(that.date)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = country.hashCode();
        result = 31 * result + city.hashCode();
        result = 31 * result + date.hashCode();
        return result;
    }

}

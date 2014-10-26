package root.application.service;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import root.application.model.Weather;
import root.application.model.WeatherRequest;
import root.application.service.WeatherApp.WeatherInterface;
import root.application.service.WeatherApp.WeatherInterfaceHelper;
import root.application.service.WeatherApp.WeatherInterfacePackage.WeatherIDL;

import java.util.Date;

/**
 * Created by Semyon Danilov on 26.10.2014.
 */
public class CorbaWeatherClient {

    private WeatherInterface weatherInterface = null;

    public void run() {
        try{
            // create and initialize the ORB
            String[] args = new String[]{"-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"};
            ORB orb = ORB.init(args, null);

            // get the root naming context
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt instead of NamingContext. This is
            // part of the Interoperable naming Service.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // resolve the Object Reference in Naming
            String name = "WeatherInterface";
            weatherInterface = WeatherInterfaceHelper.narrow(ncRef.resolve_str(name));

            System.out.println("Obtained a handle on server object: " + weatherInterface);

        } catch (Exception e) {
            System.out.println("ERROR : " + e) ;
            e.printStackTrace(System.out);
        }

    }

    public Weather getWeather(final String city, final String countryName) {
        WeatherIDL weatherIDL = weatherInterface.get(city, countryName);
        Weather weather = new Weather(new WeatherRequest(countryName, city, new Date()), weatherIDL.temperature, weatherIDL.minTemperature);
        weather.setEffect(weatherIDL.effect);
        weather.setWindSpeed(weatherIDL.windSpeed);
        weather.setWindAngle(weatherIDL.windAngle);
        weather.setCountryName(weatherIDL.countryName);
        weather.setCityName(weatherIDL.cityName);
        return weather;
    }

}

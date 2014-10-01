package root.test;

import com.sun.istack.internal.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import root.application.model.Weather;
import root.application.model.WeatherRequest;
import root.application.service.WeatherService;
import root.server.AsyncUDPServer;
import root.server.MessageHandler;
import root.server.message.ByteMessage;
import root.server.message.model.Message;
import root.server.model.Server;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by Max on 9/29/2014.
 */
public class TestServer {
    static public void main(String[] args) {
        final int bufferSize = 256;
        final Server server = new AsyncUDPServer(InetAddress.getLoopbackAddress(), 12304, bufferSize);
        server.setHandler(new MessageHandler(server) {

            private WeatherService service = new WeatherService();

            @Override
            public void handleMessage(@NotNull final Message message) {
                System.out.println(new String(message.getData()));
                String str = new String(message.getData());
                JSONObject jsonObject = null;
                try {
                    JSONParser parser = new JSONParser();
                    jsonObject = (JSONObject) parser.parse(str);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String country = (String) jsonObject.get("country");
                String city = (String) jsonObject.get("city");
                WeatherRequest request = new WeatherRequest(country, city, new Date());
                service.getWeather(request, new WeatherService.WeatherCallback() {
                    @Override
                    public void onLoad(final Weather weather) {
                        final Message msg = new ByteMessage(message.getSenderAddress(), weather.toString());
                        sendResponse(msg);
                    }
                });
            }

        });
        new Thread(server).start();
        System.out.println("Test server successfully started!");
    }
}

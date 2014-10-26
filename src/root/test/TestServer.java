package root.test;

import com.sun.istack.internal.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import root.application.model.Weather;
import root.application.model.WeatherRequest;
import root.application.service.CorbaWeatherClient;
import root.application.service.CorbaWeatherServer;
import root.application.service.WeatherService;
import root.server.MessageHandler;
import root.server.SyncUDPServer;
import root.server.message.ByteMessage;
import root.server.message.model.Message;
import root.server.model.Server;

import java.net.InetAddress;

/**
 * Created by Max on 9/29/2014.
 */
public class TestServer {

    private static boolean isServerOffline;
    private static CorbaWeatherClient corbaWeatherClient;
    private static CorbaWeatherServer corbaWeatherServer;
    private static WeatherService service = new WeatherService();

    static public void main(String[] args) {
        final int bufferSize = 256;
        final Server server = new SyncUDPServer(InetAddress.getLoopbackAddress(), Integer.valueOf(args[1]), bufferSize);
        String line = args[0];
        if (line.contains("offline")) {
            isServerOffline = true;
            corbaWeatherClient = new CorbaWeatherClient();
            corbaWeatherClient.run();
        } else {
            isServerOffline = false;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    corbaWeatherServer = new CorbaWeatherServer(1050, service);
                    corbaWeatherServer.run();
                }
            };
            thread.start();
        }
        server.setMessageHandler(new MessageHandler(server) {

            @Override
            public void handleMessage(@NotNull final Message message) {
                String str = new String(message.getData()).trim();
                System.out.println(str);
                JSONObject jsonObject = null;
                try {
                    JSONParser parser = new JSONParser();
                    jsonObject = (JSONObject) parser.parse(str);
                } catch (ParseException e) {
                    System.out.println("Failed to parse JSON: " + e.getMessage());
                    final ByteMessage msg = new ByteMessage(message.getSenderAddress(), "Error while parsing JSON.");
                    sendResponse(msg);
                    return;
                }
                String country = (String) jsonObject.get("country");
                String city = (String) jsonObject.get("city");
                WeatherRequest request = new WeatherRequest(country, city, null);
                Weather weather = null;
                if (isServerOffline) {
                    weather = corbaWeatherClient.getWeather(city, country);
                } else {
                    weather = service.getWeather(request, new WeatherService.WeatherCallback() {

                        @Override
                        public void onLoad(final Weather weather) {
                            JSONObject json = new JSONObject();
                            try {
                                json.put("success", true);
                                json.put("weather", weather.toJSON());
                                final Message msg = new ByteMessage(message.getSenderAddress(), json.toJSONString());
                                sendResponse(msg);
                            } catch (Exception e) {
                                json.put("success", false);
                                json.put("error", "Failed to use pipe: " + e.getMessage());
                                final Message msg = new ByteMessage(message.getSenderAddress(), json.toJSONString());
                                sendResponse(msg);
                            }
                        }

                        @Override
                        public void onLoad(final String error) {
                            JSONObject json = new JSONObject();
                            json.put("success", false);
                            json.put("error", error);
                            final Message msg = new ByteMessage(message.getSenderAddress(), json.toJSONString());
                            sendResponse(msg);
                        }

                    });
                }
                if (weather != null) {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("success", true);
                        json.put("weather", weather.toJSON());
                        final Message msg = new ByteMessage(message.getSenderAddress(), json.toJSONString());
                        sendResponse(msg);
                    } catch (Exception e) {
                        json.put("success", false);
                        json.put("error", "Failed to use pipe: " + e.getMessage());
                        final Message msg = new ByteMessage(message.getSenderAddress(), json.toJSONString());
                        sendResponse(msg);
                    }
                }
            }

        });
        new Thread(server).start();
        System.out.println("Test server successfully started!");
    }

}

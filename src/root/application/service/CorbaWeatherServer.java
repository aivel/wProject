package root.application.service;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import root.application.service.WeatherApp.WeatherInterface;
import root.application.service.WeatherApp.WeatherInterfaceHelper;

/**
 * Created by Semyon Danilov on 26.10.2014.
 */
public class CorbaWeatherServer {

    private int port;
    private WeatherService weatherService;

    public CorbaWeatherServer(final int port, final WeatherService weatherService) {
        this.port = port;
        this.weatherService = weatherService;
    }

    public void run() {

        try{
            // create and initialize the ORB
            String[] args = new String[]{"-ORBInitialPort", port + "", "-ORBInitialHost", "localhost"};
            ORB orb = ORB.init(args, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            WeatherImpl weatherImpl = new WeatherImpl();
            weatherImpl.setORB(orb);
            weatherImpl.setWeatherService(weatherService);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(weatherImpl);
            WeatherInterface href = WeatherInterfaceHelper.narrow(ref);

            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            String name = "WeatherInterface";
            NameComponent path[] = ncRef.to_name( name );
            ncRef.rebind(path, href);

            System.out.println("WeatherInterfaceServer ready and waiting ...");

            // wait for invocations from clients
            orb.run();
        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("WeatherInterfaceServer Exiting ...");
    }

}

package ie.tcd.dsg.surf.weatherforecast.server;

//import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

//import com.sun.jersey.api.container.httpserver.HttpServerFactory;
//import com.sun.jersey.api.core.PackagesResourceConfig;
//import com.sun.jersey.api.core.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

import ie.tcd.dsg.surf.weatherforecast.gateway.WeatherForecast;

/**
 * 
 * @author Andrés Paz, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
@SuppressWarnings("restriction")
public class WeatherForecastServer {

	private static final int SERVER_PORT = 8085;
	private static final String DISCOVERY_SERVER_PROPERTY = "address";
	private static final String PROPERTIES_FILE_NAME = "gateway.properties";
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		InputStream inputStream = null;
		try {
			System.out.println("[INFO] Starting Weather Forecast Server...\n");
			HttpServer weatherForecastServer = createHttpServer();
//			weatherForecastServer.start();
			System.out.println(String.format("\n[INFO] Weather Forecast Server started with WADL available at " + "%sapplication.wadl",	getWeatherForecastURI()));
			System.out.println("\n[INFO] Started Weather Forecast Server successfully.");
			System.out.println("\n[INFO] Discovering Weather Forecast Resources...");
			WeatherForecast weatherForecast = WeatherForecast.getInstance();
			int resources = 0;
			String discoveryServerURI = null;
//			discoveryServerURI = System.getenv(DISCOVERY_SERVER_PROPERTY);
//			if (discoveryServerURI == null) {
//				System.out.println("\n[INFO] Environment variable not found. Looking for properties file...");
				Properties properties = new Properties();
				try {
					String propertiesFilePath = System.getProperty("user.home") + File.separator + PROPERTIES_FILE_NAME;
					inputStream = new FileInputStream(propertiesFilePath);
					properties.load(inputStream);
					discoveryServerURI = properties.getProperty(DISCOVERY_SERVER_PROPERTY);
					if (discoveryServerURI != null) {
						if (discoveryServerURI.contains(":")) {
							if (!discoveryServerURI.startsWith("[")) {
								discoveryServerURI = "[" + discoveryServerURI;
							}
							if (!discoveryServerURI.endsWith("]")) {
								discoveryServerURI += "]";
							}
							discoveryServerURI = "http://" + discoveryServerURI;
						}
						weatherForecast.discoverAllResources(discoveryServerURI);
					} else {
						System.out.println("\n[WARNING] Property not found. Using test URI...");
						resources = weatherForecast.discoverAllResources();
					}
				} catch (FileNotFoundException e) {
					System.out.println("\n[ERROR] Properties file not found. Using test URI...");
					resources = weatherForecast.discoverAllResources();
				}  finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
//			} else {
//				weatherForecast.discoverAllResources(discoveryServerURI);
//			}
			while (true) {
				resources = weatherForecast.getResourcesDiscovered();
				System.out.println("\n[INFO] Resources discovered: " + resources);
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
				}
			}
		} catch (IOException e) {
			System.err.println("\n[ERROR]" + e.getMessage());
			e.printStackTrace();
		}
	}
	
//	private static void stopServer (HttpServer weatherForecastServer) {
//		System.out.println("\n[INFO] Enter \"stop\" to stop the server.\n");
//		System.out.print("weather@raspberry_pi ~ $ ");
//		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
//			String command = in.readLine();
//			if (command.equals("stop")) {
//				weatherForecastServer.stop(0);
//				System.out.println("\n[INFO] Stopped Weather Forecast Server.");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private static HttpServer createHttpServer() throws IOException {
		ResourceConfig weatherForecastResourceConfig = new ResourceConfig();
		weatherForecastResourceConfig.packages("ie.tcd.dsg.surf.weatherforecast.service");
		return JdkHttpServerFactory.createHttpServer(getWeatherForecastURI(), weatherForecastResourceConfig);
	}

	/**
	 * 
	 * @return
	 */
	private static URI getWeatherForecastURI() {
		return UriBuilder.fromUri("http://" + getHostName() + "/").port(SERVER_PORT).build();
	}

	/**
	 * 
	 * @return
	 */
	private static String getHostName() {
		String hostName = "localhost";
		try {
			hostName = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return hostName;
	}

}

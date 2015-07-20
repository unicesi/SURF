/*
 * This file is part of WeatherForecastGateway.
 * 
 * WeatherForecastGateway is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * WeatherForecastGateway is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with WeatherForecastGateway. If not, see <http://www.gnu.org/licenses/>.
 */
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
 * HTTP Server publishing a REST API to query temperature and humidity information from MOTEs that 
 * provide it as a CoAP resource.
 * 
 * @author Andrés Paz <afpaz at icesi.edu.co>, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
@SuppressWarnings("restriction")
public class WeatherForecastServer {

	/**
	 * HTTP server port. Default selected port is 8085.
	 */
	private static final int SERVER_PORT = 8085;
	/**
	 * The name of the properties file where the address to the discovery server is stored.
	 */
	private static final String PROPERTIES_FILE_NAME = "gateway.properties";
	/**
	 * The discovery server property name in the properties file.
	 */
	private static final String DISCOVERY_SERVER_PROPERTY = "address";
	
	/**
	 * The main method. Looks for the properties file to retrieve the discovery server's address.
	 * Then starts the HTTP server and the discovery thread.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Input stream to read the properties file.
		InputStream inputStream = null;
		try {
			System.out.println("[INFO] Starting Weather Forecast Server...\n");
			// Create and start the HTTP server.
			HttpServer weatherForecastServer = createHttpServer();
			System.out.println(String.format("\n[INFO] Weather Forecast Server started with WADL available at " + "%sapplication.wadl",	getWeatherForecastURI()));
			System.out.println("\n[INFO] Started Weather Forecast Server successfully.");
			System.out.println("[INFO] Looking for discovery server address...");
			// Get the WeatherForecast instance.
			WeatherForecast weatherForecast = WeatherForecast.getInstance();
			int resources = 0;
			String discoveryServerURI = null;
			// Read the properties file. If the discovery server's address is IPv6, add square brackets if needed.
			Properties properties = new Properties();
			try {
				String propertiesFilePath = System.getProperty("user.home") + File.separator + PROPERTIES_FILE_NAME;
				inputStream = new FileInputStream(propertiesFilePath);
				properties.load(inputStream);
				discoveryServerURI = properties.getProperty(DISCOVERY_SERVER_PROPERTY);
				if (discoveryServerURI != null) {
					System.out.println("[INFO] Discovery server address found. Building URI...");
					if (discoveryServerURI.contains(":")) {
						if (!discoveryServerURI.startsWith("[")) {
							discoveryServerURI = "[" + discoveryServerURI;
						}
						if (!discoveryServerURI.endsWith("]")) {
							discoveryServerURI += "]";
						}
						discoveryServerURI = "http://" + discoveryServerURI;
					}
					System.out.println("[INFO] Starting discovery thread...");
					// Start the discovery thread.
					weatherForecast.discoverAllResources(discoveryServerURI);
					System.out.println("[INFO] Discovery thread started.");
				} else {
					// If the properties file was found but the property was not, use the Test URI.
					System.out.println("\n[WARNING] Property not found. Using test URI...");
					resources = weatherForecast.discoverAllResources();
				}
			} catch (FileNotFoundException e) {
				// If the properties file was not found, use the Test URI.
				System.out.println("\n[ERROR] Properties file not found. Using test URI...");
				resources = weatherForecast.discoverAllResources();
			}  finally {
				// Close the stream to the properties file.
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			// Show amount of discovered resources.
			while (true) {
				System.out.println("\n[INFO] Discovering Weather Forecast Routes...");
				resources = weatherForecast.getResourcesDiscovered();
				System.out.println("\n[INFO] Total resources discovered: " + resources);
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
	
//	/**
//	 * Stops the server.
//	 * 
//	 * @param weatherForecastServer The HTTP server to be stopped.
//	 */
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
	 * Creates and starts a lightweight HTTP server. Uses the HTTP server bundled with Java SE.
	 * Requires Java SE 6 or later.
	 * 
	 * @return The HTTP server.
	 * @throws IOException If any error occurs in the process.
	 */
	private static HttpServer createHttpServer() throws IOException {
		// Configure resources that will be published in the HTTP server.
		ResourceConfig weatherForecastResourceConfig = new ResourceConfig();
		// All resources are RESTful web services located in the package ie.tcd.dsg.surf.weatherforecast.service.
		weatherForecastResourceConfig.packages("ie.tcd.dsg.surf.weatherforecast.service");
		// Create the HTTP server with the previous configuration.
		// Note: The server is started automatically.
		return JdkHttpServerFactory.createHttpServer(getWeatherForecastURI(), weatherForecastResourceConfig);
	}

	/**
	 * Returns the server's URI.
	 * 
	 * @return The server's URI.
	 */
	private static URI getWeatherForecastURI() {
		return UriBuilder.fromUri("http://" + getHostName() + "/").port(SERVER_PORT).build();
	}

	/**
	 * Returns the server's host name.
	 * 
	 * @return The server's host name.
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

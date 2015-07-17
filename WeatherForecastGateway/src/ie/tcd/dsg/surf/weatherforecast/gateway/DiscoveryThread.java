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
package ie.tcd.dsg.surf.weatherforecast.gateway;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 
 * @author Andrés Paz <afpaz at icesi.edu.co>, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
public class DiscoveryThread extends Thread {

	/**
	 * 
	 */
	private WeatherForecast weatherForecast;
	/**
	 * 
	 */
	private String discoveryServerURI;
	/**
	 * 
	 */
	private boolean stopRequested;
	/**
	 * 
	 */
	private Set<String> discoveredRoutes;
	
	/**
	 * 
	 * @param weatherForecast
	 * @param discoveryServerURI
	 */
	public DiscoveryThread(WeatherForecast weatherForecast, String discoveryServerURI) {
		super();
		this.weatherForecast = weatherForecast;
		this.discoveryServerURI = discoveryServerURI;
		this.stopRequested = false;
		this.discoveredRoutes = new HashSet<>();
	}
	
	/**
	 * 
	 */
	public void stopDiscovery() {
		this.stopRequested = true;
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getDiscoveredRoutes() {
		return this.discoveredRoutes;
	}
	
	/**
	 * 
	 */
	@Override
	public void run() {
		super.run();
		Client client = ClientBuilder.newClient();
		WebTarget serviceWebTarget = client.target(this.discoveryServerURI);
		Invocation.Builder invocationBuilder = serviceWebTarget.request(MediaType.APPLICATION_XML_TYPE);
		while (!stopRequested) {
			System.out.println("[INFO{dt}] Requesting XML from " + this.discoveryServerURI);
			Response response = invocationBuilder.get();
			if (response.getStatus() == 200) {
				System.out.println("[INFO{dt}] Response received. Processing XML...");
				String xmlResponse = response.readEntity(String.class);
				boolean newRoutesFound = processResponse(xmlResponse);
				if (newRoutesFound) {
					System.out.println("[INFO{dt}] New routes found. Notifying...");
					this.weatherForecast.notifyNewRoutesFound();
				} else {
					System.out.println("[INFO{dt}] No new routes were found. Trying again in 10 seconds.");
				}
			} else {
				System.out.println("[ERROR{dt}] Failed with HTTP error code: " + response.getStatus());
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	/**
	 * 
	 * @param xml
	 * @return
	 */
	private boolean processResponse(String xml) {
		boolean newRoutesFound = false;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try {
		    documentBuilder = documentBuilderFactory.newDocumentBuilder();
		    InputSource inputSource = new InputSource();
		    inputSource.setCharacterStream(new StringReader(xml));
		    try {
		        Document document = documentBuilder.parse(inputSource);
		        document.getDocumentElement().normalize();
		        
		        NodeList routes = document.getElementsByTagName("route");
		        
		        for (int i = 0; i < routes.getLength(); i++) {
		        	Node nNode = routes.item(i);
		        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		    			Element route = (Element) nNode;
		    			boolean added = this.discoveredRoutes.add(route.getTextContent());
		    			if (added && !newRoutesFound) {
		    				newRoutesFound = true;
		    			}
		        	}
		        }
		    } catch (SAXException e) {
		    	e.printStackTrace();
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		} catch (ParserConfigurationException e) {
		    e.printStackTrace();
		}
		return newRoutesFound;
	}
	
}

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

public class DiscoveryThread extends Thread {

	private WeatherForecast weatherForecast;
	private String discoveryServerURI;
	private boolean stopRequested;
	private Set<String> discoveredRoutes;
	
	public DiscoveryThread(WeatherForecast weatherForecast, String discoveryServerURI) {
		super();
		this.weatherForecast = weatherForecast;
		this.discoveryServerURI = discoveryServerURI;
		this.stopRequested = false;
		this.discoveredRoutes = new HashSet<>();
	}
	
	public void stopDiscovery() {
		this.stopRequested = true;
	}
	
	public Set<String> getDiscoveredRoutes() {
		return this.discoveredRoutes;
	}
	
	@Override
	public void run() {
		super.run();
		Client client = ClientBuilder.newClient();
		WebTarget serviceWebTarget = client.target(this.discoveryServerURI);
		Invocation.Builder invocationBuilder = serviceWebTarget.request(MediaType.APPLICATION_XML_TYPE);
		while (!stopRequested) {
			Response response = invocationBuilder.get();
			if (response.getStatus() == 200) {
				String xmlResponse = response.readEntity(String.class);
				boolean newRoutesFound = processResponse(xmlResponse);
				if (newRoutesFound) {
					this.weatherForecast.notifyNewRoutesFound();
				}
			} else {
				System.out.println("[ERROR] Failed with HTTP error code: " + response.getStatus());
			}
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
			}
		}
	}
	
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
		    			if (!added && !newRoutesFound) {
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

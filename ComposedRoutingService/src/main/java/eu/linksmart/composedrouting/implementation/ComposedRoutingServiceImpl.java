/*
 * This file is part of ComposedRoutingService.
 * 
 * ComposedRoutingService is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ComposedRoutingService is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ComposedRoutingService. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.linksmart.composedrouting.implementation;

//import java.io.IOException;
//import java.io.InputStream;
//import java.net.MalformedURLException;
//import java.net.URL;
import java.rmi.RemoteException;

import org.apache.cxf.jaxrs.client.WebClient;

//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Invocation;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;

import org.apache.log4j.Logger;
import org.osgi.service.component.ComponentContext;

import eu.linksmart.network.ServiceAttribute;
import eu.linksmart.composedrouting.service.ComposedRoutingService;
import eu.linksmart.network.Registration;
import eu.linksmart.network.networkmanager.NetworkManager;
import eu.linksmart.utils.Part;

/**
 * 
 * @author Andrés Paz, I2T Research Group, Universidad Icesi, Cali - Colombia
 * 
 */
@Component(name = "ComposedRoutingService", immediate = true)
@Service
@Properties({
	@Property(name = "service.exported.interfaces", value = "*"),
	@Property(name = "service.exported.configs", value = "org.apache.cxf.ws"),
	@Property(name = "org.apache.cxf.ws.address", value = "http://localhost:9090/cxf/services/ComposedRoutingService") })
public class ComposedRoutingServiceImpl implements ComposedRoutingService {

	/* constants */
	private static final String CXF_SERVICES_PATH = "http://localhost:9090/cxf/services/";
	private static final String ENDPOINT = CXF_SERVICES_PATH + SID;

	/* fields */
	private Logger mLogger = Logger.getLogger(ComposedRoutingServiceImpl.class.getName());
	protected ComponentContext mContext;
	private Registration serviceVirtualAddress = null;
	private String backbone = null;

	@Reference(name = "NetworkManager", cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindNetworkManager", unbind = "unbindNetworkManager", policy = ReferencePolicy.DYNAMIC)
	private NetworkManager networkManager;

	protected void bindNetworkManager(NetworkManager nm) {
		mLogger.debug(SID + "::binding networkmanager");
		networkManager = nm;
	}

	protected void unbindNetworkManager(NetworkManager nm) {
		mLogger.debug(SID + "::un-binding networkmanager");
		networkManager = null;
	}

	@Activate
	protected void activate(ComponentContext context) {
		mLogger.info("Activating " + SID + "...");
		initBackbone();
		invokeRegisterService();
		mLogger.info("Started " + SID);
	}

	private void initBackbone() {
		try {
			String[] backbones = networkManager.getAvailableBackbones();
			for (String b : backbones) {
				if (b.contains("soap")) {
					this.backbone = b;
				}
			}
			if (backbone == null) {
				backbone = "eu.linksmart.network.backbone.impl.soap.BackboneSOAPImpl";
			}
			mLogger.info("Using backbone: " + backbone);
		} catch (RemoteException e) {
			mLogger.error("Unable to retrieve list of backbones from network-manager", e);
		}
	}

	private void invokeRegisterService() {
		try {
			serviceVirtualAddress = networkManager.registerService(
					new Part[] { new Part(ServiceAttribute.DESCRIPTION.name(), DESCRIPTION) }, ENDPOINT, backbone);
			mLogger.info(
					"Created Virtual Address: " + serviceVirtualAddress.getVirtualAddressAsString());
		} catch (RemoteException e) {
			mLogger.error(e.getMessage(), e.getCause());
		}
	}

	@Deactivate
	protected void deactivate(ComponentContext context) {
		mLogger.info("Stopping " + SID + "...");
		invokeRemoveService();
		mLogger.info("Stopped " + SID);
	}

	private void invokeRemoveService() {
		mLogger.info("Removed virtual address:" + serviceVirtualAddress.getVirtualAddressAsString());
	}

	private static final String BASE_URL = "http://localhost:8084/RoutingService/webresources";
	private static final String SERVICE = "route";
	private static final String OPERATION = "getRoute";
	private static final String QUERY = "?origin=ScienceGallery&destination=Spire&transport=Walking";

	@Override
	public String getRoute() {
		// The following code does not work deployed on LinkSmart.
		// However, it is the standard way of consuming a RESTful
		// web service in any Java application.
		// BEGIN
//		Client client = ClientBuilder.newClient();
//		WebTarget serviceWebTarget = client.target(BASE_URL).path(SERVICE).path(OPERATION);
//		serviceWebTarget = serviceWebTarget.queryParam("origin", "ScienceGallery");
//		serviceWebTarget = serviceWebTarget.queryParam("destination", "Spire");
//		serviceWebTarget = serviceWebTarget.queryParam("transport", "Walking");
//		Invocation.Builder invocationBuilder = serviceWebTarget.request(MediaType.APPLICATION_XML_TYPE);
//		Response response = invocationBuilder.get();
//		if (response.getStatus() != 200) {
//			return "Failed : HTTP error code : " + response.getStatus();
//		}
//		String xmlResponse = response.readEntity(String.class);
//		return xmlResponse;
		// END
		
		// Using CXF WebClient works under LinkSmart
		WebClient client = WebClient.create(BASE_URL + "/" + SERVICE + "/" + OPERATION + QUERY);
		client.accept("application/xml");
		String response = client.get(String.class);
		return response;
		
	}
	
}

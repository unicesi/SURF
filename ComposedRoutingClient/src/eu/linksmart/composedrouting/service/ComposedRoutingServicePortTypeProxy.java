/*
 * This file is part of ComposedRoutingClient.
 * 
 * ComposedRoutingClient is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ComposedRoutingClient is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ComposedRoutingClient. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * ComposedRoutingServicePortTypeProxy.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package eu.linksmart.composedrouting.service;

public class ComposedRoutingServicePortTypeProxy implements eu.linksmart.composedrouting.service.ComposedRoutingServicePortType {
  private String _endpoint = null;
  private eu.linksmart.composedrouting.service.ComposedRoutingServicePortType composedRoutingServicePortType = null;
  
  public ComposedRoutingServicePortTypeProxy() {
    _initComposedRoutingServicePortTypeProxy();
  }
  
  public ComposedRoutingServicePortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initComposedRoutingServicePortTypeProxy();
  }
  
  private void _initComposedRoutingServicePortTypeProxy() {
    try {
      composedRoutingServicePortType = (new eu.linksmart.composedrouting.service.ComposedRoutingServiceLocator()).getComposedRoutingServicePort();
      if (composedRoutingServicePortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)composedRoutingServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)composedRoutingServicePortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (composedRoutingServicePortType != null)
      ((javax.xml.rpc.Stub)composedRoutingServicePortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public eu.linksmart.composedrouting.service.ComposedRoutingServicePortType getComposedRoutingServicePortType() {
    if (composedRoutingServicePortType == null)
      _initComposedRoutingServicePortTypeProxy();
    return composedRoutingServicePortType;
  }
  
  public java.lang.String getRoute() throws java.rmi.RemoteException{
    if (composedRoutingServicePortType == null)
      _initComposedRoutingServicePortTypeProxy();
    return composedRoutingServicePortType.getRoute();
  }
  
  
}
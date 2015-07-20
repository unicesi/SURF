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
 * ComposedRoutingServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */
package eu.linksmart.composedrouting.service;

public class ComposedRoutingServiceLocator extends org.apache.axis.client.Service implements eu.linksmart.composedrouting.service.ComposedRoutingService {

    public ComposedRoutingServiceLocator() {
    }


    public ComposedRoutingServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ComposedRoutingServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ComposedRoutingServicePort
    private java.lang.String ComposedRoutingServicePort_address = "http://localhost:9090/cxf/services/ComposedRoutingService";

    public java.lang.String getComposedRoutingServicePortAddress() {
        return ComposedRoutingServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ComposedRoutingServicePortWSDDServiceName = "ComposedRoutingServicePort";

    public java.lang.String getComposedRoutingServicePortWSDDServiceName() {
        return ComposedRoutingServicePortWSDDServiceName;
    }

    public void setComposedRoutingServicePortWSDDServiceName(java.lang.String name) {
        ComposedRoutingServicePortWSDDServiceName = name;
    }

    public eu.linksmart.composedrouting.service.ComposedRoutingServicePortType getComposedRoutingServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ComposedRoutingServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getComposedRoutingServicePort(endpoint);
    }

    public eu.linksmart.composedrouting.service.ComposedRoutingServicePortType getComposedRoutingServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            eu.linksmart.composedrouting.service.ComposedRoutingServiceSoapBindingStub _stub = new eu.linksmart.composedrouting.service.ComposedRoutingServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getComposedRoutingServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setComposedRoutingServicePortEndpointAddress(java.lang.String address) {
        ComposedRoutingServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (eu.linksmart.composedrouting.service.ComposedRoutingServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                eu.linksmart.composedrouting.service.ComposedRoutingServiceSoapBindingStub _stub = new eu.linksmart.composedrouting.service.ComposedRoutingServiceSoapBindingStub(new java.net.URL(ComposedRoutingServicePort_address), this);
                _stub.setPortName(getComposedRoutingServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ComposedRoutingServicePort".equals(inputPortName)) {
            return getComposedRoutingServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.composedrouting.linksmart.eu/", "ComposedRoutingService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.composedrouting.linksmart.eu/", "ComposedRoutingServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ComposedRoutingServicePort".equals(portName)) {
            setComposedRoutingServicePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}

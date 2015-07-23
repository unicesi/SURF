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
package dyna.app.service.brs.erpi.t100IntegrateService;

public class TIPTOPServiceGateWayPortTypeProxy implements dyna.app.service.brs.erpi.t100IntegrateService.TIPTOPServiceGateWayPortType {
  private String _endpoint = null;
  private dyna.app.service.brs.erpi.t100IntegrateService.TIPTOPServiceGateWayPortType tIPTOPServiceGateWayPortType = null;
  
  public TIPTOPServiceGateWayPortTypeProxy() {
    _initTIPTOPServiceGateWayPortTypeProxy();
  }
  
  public TIPTOPServiceGateWayPortTypeProxy(String endpoint) {
    _endpoint = endpoint;
    _initTIPTOPServiceGateWayPortTypeProxy();
  }
  
  private void _initTIPTOPServiceGateWayPortTypeProxy() {
    try {
      tIPTOPServiceGateWayPortType = (new dyna.app.service.brs.erpi.t100IntegrateService.TIPTOPServiceGateWayLocator()).getTIPTOPServiceGateWayPortType();
      if (tIPTOPServiceGateWayPortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)tIPTOPServiceGateWayPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)tIPTOPServiceGateWayPortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (tIPTOPServiceGateWayPortType != null)
      ((javax.xml.rpc.Stub)tIPTOPServiceGateWayPortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public dyna.app.service.brs.erpi.t100IntegrateService.TIPTOPServiceGateWayPortType getTIPTOPServiceGateWayPortType() {
    if (tIPTOPServiceGateWayPortType == null)
      _initTIPTOPServiceGateWayPortTypeProxy();
    return tIPTOPServiceGateWayPortType;
  }
  
  public java.lang.String invokeSrv(java.lang.String request) throws java.rmi.RemoteException{
    if (tIPTOPServiceGateWayPortType == null)
      _initTIPTOPServiceGateWayPortTypeProxy();
    return tIPTOPServiceGateWayPortType.invokeSrv(request);
  }
  
  public java.lang.String invokeMdm(java.lang.String request) throws java.rmi.RemoteException{
    if (tIPTOPServiceGateWayPortType == null)
      _initTIPTOPServiceGateWayPortTypeProxy();
    return tIPTOPServiceGateWayPortType.invokeMdm(request);
  }
  
  public java.lang.String syncProd(java.lang.String request) throws java.rmi.RemoteException{
    if (tIPTOPServiceGateWayPortType == null)
      _initTIPTOPServiceGateWayPortTypeProxy();
    return tIPTOPServiceGateWayPortType.syncProd(request);
  }
  
  public java.lang.String callbackSrv(java.lang.String request) throws java.rmi.RemoteException{
    if (tIPTOPServiceGateWayPortType == null)
      _initTIPTOPServiceGateWayPortTypeProxy();
    return tIPTOPServiceGateWayPortType.callbackSrv(request);
  }
  
  
}
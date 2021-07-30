/**
 * PubKeyInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package dyna.net.portal.integrateService;

public class PubKeyInfo  implements java.io.Serializable {
    private java.math.BigInteger publicExponent;

    private java.math.BigInteger publicModulus;

    public PubKeyInfo() {
    }

    public PubKeyInfo(
           java.math.BigInteger publicExponent,
           java.math.BigInteger publicModulus) {
           this.publicExponent = publicExponent;
           this.publicModulus = publicModulus;
    }


    /**
     * Gets the publicExponent value for this PubKeyInfo.
     * 
     * @return publicExponent
     */
    public java.math.BigInteger getPublicExponent() {
        return publicExponent;
    }


    /**
     * Sets the publicExponent value for this PubKeyInfo.
     * 
     * @param publicExponent
     */
    public void setPublicExponent(java.math.BigInteger publicExponent) {
        this.publicExponent = publicExponent;
    }


    /**
     * Gets the publicModulus value for this PubKeyInfo.
     * 
     * @return publicModulus
     */
    public java.math.BigInteger getPublicModulus() {
        return publicModulus;
    }


    /**
     * Sets the publicModulus value for this PubKeyInfo.
     * 
     * @param publicModulus
     */
    public void setPublicModulus(java.math.BigInteger publicModulus) {
        this.publicModulus = publicModulus;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PubKeyInfo)) return false;
        PubKeyInfo other = (PubKeyInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.publicExponent==null && other.getPublicExponent()==null) || 
             (this.publicExponent!=null &&
              this.publicExponent.equals(other.getPublicExponent()))) &&
            ((this.publicModulus==null && other.getPublicModulus()==null) || 
             (this.publicModulus!=null &&
              this.publicModulus.equals(other.getPublicModulus())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getPublicExponent() != null) {
            _hashCode += getPublicExponent().hashCode();
        }
        if (getPublicModulus() != null) {
            _hashCode += getPublicModulus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PubKeyInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://webservice.dsc.com/", "pubKeyInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("publicExponent");
        elemField.setXmlName(new javax.xml.namespace.QName("", "publicExponent"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("publicModulus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "publicModulus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

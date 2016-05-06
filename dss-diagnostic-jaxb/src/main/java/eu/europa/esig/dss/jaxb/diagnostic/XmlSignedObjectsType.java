//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.05.06 at 10:52:35 AM CEST 
//


package eu.europa.esig.dss.jaxb.diagnostic;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SignedObjectsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SignedObjectsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SignedSignature" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="TimestampedTimestamp" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="DigestAlgAndValue" type="{http://dss.esig.europa.eu/validation/diagnostic}DigestAlgAndValueType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignedObjectsType", propOrder = {
    "signedSignature",
    "timestampedTimestamp",
    "digestAlgAndValue"
})
public class XmlSignedObjectsType {

    @XmlElement(name = "SignedSignature")
    protected List<XmlSignedSignature> signedSignature;
    @XmlElement(name = "TimestampedTimestamp")
    protected List<XmlTimestampedTimestamp> timestampedTimestamp;
    @XmlElement(name = "DigestAlgAndValue")
    protected List<XmlDigestAlgAndValueType> digestAlgAndValue;

    /**
     * Gets the value of the signedSignature property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signedSignature property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignedSignature().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlSignedSignature }
     * 
     * 
     */
    public List<XmlSignedSignature> getSignedSignature() {
        if (signedSignature == null) {
            signedSignature = new ArrayList<XmlSignedSignature>();
        }
        return this.signedSignature;
    }

    /**
     * Gets the value of the timestampedTimestamp property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the timestampedTimestamp property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTimestampedTimestamp().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlTimestampedTimestamp }
     * 
     * 
     */
    public List<XmlTimestampedTimestamp> getTimestampedTimestamp() {
        if (timestampedTimestamp == null) {
            timestampedTimestamp = new ArrayList<XmlTimestampedTimestamp>();
        }
        return this.timestampedTimestamp;
    }

    /**
     * Gets the value of the digestAlgAndValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the digestAlgAndValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDigestAlgAndValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlDigestAlgAndValueType }
     * 
     * 
     */
    public List<XmlDigestAlgAndValueType> getDigestAlgAndValue() {
        if (digestAlgAndValue == null) {
            digestAlgAndValue = new ArrayList<XmlDigestAlgAndValueType>();
        }
        return this.digestAlgAndValue;
    }

}

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.12.16 at 04:53:00 PM CET 
//


package es.ehubio.proteomics.waters.plgs;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}MODIFIES"/>
 *       &lt;/sequence>
 *       &lt;attribute name="MCAT_REAGENT" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *       &lt;attribute name="NAME" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "modifies"
})
@XmlRootElement(name = "MODIFIER")
public class MODIFIER {

    @XmlElement(name = "MODIFIES", required = true)
    protected MODIFIES modifies;
    @XmlAttribute(name = "MCAT_REAGENT", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String mcatreagent;
    @XmlAttribute(name = "NAME", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String name;

    /**
     * Gets the value of the modifies property.
     * 
     * @return
     *     possible object is
     *     {@link MODIFIES }
     *     
     */
    public MODIFIES getMODIFIES() {
        return modifies;
    }

    /**
     * Sets the value of the modifies property.
     * 
     * @param value
     *     allowed object is
     *     {@link MODIFIES }
     *     
     */
    public void setMODIFIES(MODIFIES value) {
        this.modifies = value;
    }

    /**
     * Gets the value of the mcatreagent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMCATREAGENT() {
        return mcatreagent;
    }

    /**
     * Sets the value of the mcatreagent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMCATREAGENT(String value) {
        this.mcatreagent = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNAME() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNAME(String value) {
        this.name = value;
    }

}

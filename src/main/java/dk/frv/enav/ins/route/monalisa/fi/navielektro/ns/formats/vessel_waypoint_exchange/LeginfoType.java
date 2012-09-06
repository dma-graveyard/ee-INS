//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.08 at 04:37:43 PM CEST 
//


package dk.frv.enav.ins.route.monalisa.fi.navielektro.ns.formats.vessel_waypoint_exchange;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * Compulsary element except for the last waypoint
 * 
 * <p>Java class for leginfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="leginfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="legtype" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;enumeration value="RHUMBLINE"/>
 *               &lt;enumeration value="GREAT-CIRCLE"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="turn-radius" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" minOccurs="0"/>
 *         &lt;element name="planned-speed" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="rhs-xte" type="{http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange}XTEType" minOccurs="0"/>
 *         &lt;element name="lhs-xte" type="{http://www.navielektro.fi/ns/formats/vessel-waypoint-exchange}XTEType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "leginfoType", propOrder = {
    "legtype",
    "turnRadius",
    "plannedSpeed",
    "rhsXte",
    "lhsXte"
})
public class LeginfoType {

    protected String legtype;
    @XmlElement(name = "turn-radius")
    @XmlSchemaType(name = "positiveInteger")
    protected Integer turnRadius;
    @XmlElement(name = "planned-speed")
    protected Float plannedSpeed;
    @XmlElement(name = "rhs-xte")
    protected Integer rhsXte;
    @XmlElement(name = "lhs-xte")
    protected Integer lhsXte;

    /**
     * Gets the value of the legtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLegtype() {
        return legtype;
    }

    /**
     * Sets the value of the legtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLegtype(String value) {
        this.legtype = value;
    }

    /**
     * Gets the value of the turnRadius property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public Integer getTurnRadius() {
        return turnRadius;
    }

    /**
     * Sets the value of the turnRadius property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTurnRadius(Integer value) {
        this.turnRadius = value;
    }

    /**
     * Gets the value of the plannedSpeed property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getPlannedSpeed() {
        return plannedSpeed;
    }

    /**
     * Sets the value of the plannedSpeed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setPlannedSpeed(Float value) {
        this.plannedSpeed = value;
    }

    /**
     * Gets the value of the rhsXte property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getRhsXte() {
        return rhsXte;
    }

    /**
     * Sets the value of the rhsXte property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setRhsXte(Integer value) {
        this.rhsXte = value;
    }

    /**
     * Gets the value of the lhsXte property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getLhsXte() {
        return lhsXte;
    }

    /**
     * Sets the value of the lhsXte property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setLhsXte(Integer value) {
        this.lhsXte = value;
    }

}
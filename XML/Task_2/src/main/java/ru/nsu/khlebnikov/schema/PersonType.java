//
// This file was generated by the Eclipse Implementation of JAXB, v4.0.5 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
//


package ru.nsu.khlebnikov.schema;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for person-type complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="person-type">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="firstname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="siblings" type="{}siblings-type" minOccurs="0"/>
 *         <element name="children" type="{}children-type" minOccurs="0"/>
 *         <element name="spouce" type="{}spouce-type" minOccurs="0"/>
 *         <element name="parents" type="{}parents-type" minOccurs="0"/>
 *       </sequence>
 *       <attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       <attribute name="gender" type="{}gender-type" />
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "person-type", propOrder = {
    "firstname",
    "surname",
    "siblings",
    "children",
    "spouce",
    "parents"
})
public class PersonType {

    protected String firstname;
    protected String surname;
    protected SiblingsType siblings;
    protected ChildrenType children;
    protected SpouceType spouce;
    protected ParentsType parents;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "gender")
    protected GenderType gender;

    /**
     * Gets the value of the firstname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * Sets the value of the firstname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstname(String value) {
        this.firstname = value;
    }

    /**
     * Gets the value of the surname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Sets the value of the surname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSurname(String value) {
        this.surname = value;
    }

    /**
     * Gets the value of the siblings property.
     * 
     * @return
     *     possible object is
     *     {@link SiblingsType }
     *     
     */
    public SiblingsType getSiblings() {
        return siblings;
    }

    /**
     * Sets the value of the siblings property.
     * 
     * @param value
     *     allowed object is
     *     {@link SiblingsType }
     *     
     */
    public void setSiblings(SiblingsType value) {
        this.siblings = value;
    }

    /**
     * Gets the value of the children property.
     * 
     * @return
     *     possible object is
     *     {@link ChildrenType }
     *     
     */
    public ChildrenType getChildren() {
        return children;
    }

    /**
     * Sets the value of the children property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChildrenType }
     *     
     */
    public void setChildren(ChildrenType value) {
        this.children = value;
    }

    /**
     * Gets the value of the spouce property.
     * 
     * @return
     *     possible object is
     *     {@link SpouceType }
     *     
     */
    public SpouceType getSpouce() {
        return spouce;
    }

    /**
     * Sets the value of the spouce property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpouceType }
     *     
     */
    public void setSpouce(SpouceType value) {
        this.spouce = value;
    }

    /**
     * Gets the value of the parents property.
     * 
     * @return
     *     possible object is
     *     {@link ParentsType }
     *     
     */
    public ParentsType getParents() {
        return parents;
    }

    /**
     * Sets the value of the parents property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParentsType }
     *     
     */
    public void setParents(ParentsType value) {
        this.parents = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the gender property.
     * 
     * @return
     *     possible object is
     *     {@link GenderType }
     *     
     */
    public GenderType getGender() {
        return gender;
    }

    /**
     * Sets the value of the gender property.
     * 
     * @param value
     *     allowed object is
     *     {@link GenderType }
     *     
     */
    public void setGender(GenderType value) {
        this.gender = value;
    }

}
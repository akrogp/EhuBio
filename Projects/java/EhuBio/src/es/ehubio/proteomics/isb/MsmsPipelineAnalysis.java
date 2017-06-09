//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2017.06.09 a las 07:10:04 PM CEST 
//


package es.ehubio.proteomics.isb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="msms_run_summary">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="sample_enzyme">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="specificity">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="cut" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="no_cut" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="sense" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="search_summary">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="search_database">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="local_path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="enzymatic_search_constraint">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="enzyme" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="max_num_internal_cleavages" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                     &lt;attribute name="min_number_termini" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="aminoacid_modification" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="aminoacid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="massdiff" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                                     &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                                     &lt;attribute name="variable" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="symbol" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="parameter" maxOccurs="unbounded">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                     &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="base_name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="search_engine" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="search_engine_version" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="precursor_mass_type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="fragment_mass_type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="search_id" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="spectrum_query" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="search_result">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="search_hit" maxOccurs="unbounded">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;sequence>
 *                                                 &lt;element name="modification_info">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;sequence>
 *                                                           &lt;element name="mod_aminoacid_mass" maxOccurs="unbounded">
 *                                                             &lt;complexType>
 *                                                               &lt;complexContent>
 *                                                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                                   &lt;attribute name="position" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                                                   &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                                                                 &lt;/restriction>
 *                                                               &lt;/complexContent>
 *                                                             &lt;/complexType>
 *                                                           &lt;/element>
 *                                                         &lt;/sequence>
 *                                                         &lt;attribute name="modified_peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                                 &lt;element name="search_score" maxOccurs="unbounded">
 *                                                   &lt;complexType>
 *                                                     &lt;complexContent>
 *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                                         &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                                         &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                                                       &lt;/restriction>
 *                                                     &lt;/complexContent>
 *                                                   &lt;/complexType>
 *                                                 &lt;/element>
 *                                               &lt;/sequence>
 *                                               &lt;attribute name="hit_rank" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="peptide_prev_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="peptide_next_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="protein" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                                               &lt;attribute name="num_tot_proteins" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="num_matched_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="tot_num_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="calc_neutral_pep_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                                               &lt;attribute name="massdiff" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                                               &lt;attribute name="num_tol_term" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="num_missed_cleavages" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                               &lt;attribute name="num_matched_peptides" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="spectrum" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="spectrumNativeID" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                           &lt;attribute name="start_scan" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="end_scan" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="precursor_neutral_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                           &lt;attribute name="assumed_charge" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                           &lt;attribute name="retention_time_sec" type="{http://www.w3.org/2001/XMLSchema}double" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="base_name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="msManufacturer" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="msModel" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="raw_data_type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="raw_data" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="summary_xml" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "msmsRunSummary"
})
@XmlRootElement(name = "msms_pipeline_analysis")
public class MsmsPipelineAnalysis {

    @XmlElement(name = "msms_run_summary", required = true)
    protected MsmsPipelineAnalysis.MsmsRunSummary msmsRunSummary;
    @XmlAttribute(name = "date")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar date;
    @XmlAttribute(name = "summary_xml")
    protected String summaryXml;

    /**
     * Obtiene el valor de la propiedad msmsRunSummary.
     * 
     * @return
     *     possible object is
     *     {@link MsmsPipelineAnalysis.MsmsRunSummary }
     *     
     */
    public MsmsPipelineAnalysis.MsmsRunSummary getMsmsRunSummary() {
        return msmsRunSummary;
    }

    /**
     * Define el valor de la propiedad msmsRunSummary.
     * 
     * @param value
     *     allowed object is
     *     {@link MsmsPipelineAnalysis.MsmsRunSummary }
     *     
     */
    public void setMsmsRunSummary(MsmsPipelineAnalysis.MsmsRunSummary value) {
        this.msmsRunSummary = value;
    }

    /**
     * Obtiene el valor de la propiedad date.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDate() {
        return date;
    }

    /**
     * Define el valor de la propiedad date.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDate(XMLGregorianCalendar value) {
        this.date = value;
    }

    /**
     * Obtiene el valor de la propiedad summaryXml.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSummaryXml() {
        return summaryXml;
    }

    /**
     * Define el valor de la propiedad summaryXml.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSummaryXml(String value) {
        this.summaryXml = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="sample_enzyme">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="specificity">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="cut" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="no_cut" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="sense" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="search_summary">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="search_database">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="local_path" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="enzymatic_search_constraint">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="enzyme" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="max_num_internal_cleavages" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                           &lt;attribute name="min_number_termini" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="aminoacid_modification" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="aminoacid" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="massdiff" type="{http://www.w3.org/2001/XMLSchema}double" />
     *                           &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
     *                           &lt;attribute name="variable" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="symbol" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="parameter" maxOccurs="unbounded">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                           &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="base_name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="search_engine" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="search_engine_version" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="precursor_mass_type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="fragment_mass_type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="search_id" type="{http://www.w3.org/2001/XMLSchema}int" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="spectrum_query" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="search_result">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="search_hit" maxOccurs="unbounded">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;sequence>
     *                                       &lt;element name="modification_info">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;sequence>
     *                                                 &lt;element name="mod_aminoacid_mass" maxOccurs="unbounded">
     *                                                   &lt;complexType>
     *                                                     &lt;complexContent>
     *                                                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                                         &lt;attribute name="position" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                                         &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
     *                                                       &lt;/restriction>
     *                                                     &lt;/complexContent>
     *                                                   &lt;/complexType>
     *                                                 &lt;/element>
     *                                               &lt;/sequence>
     *                                               &lt;attribute name="modified_peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                       &lt;element name="search_score" maxOccurs="unbounded">
     *                                         &lt;complexType>
     *                                           &lt;complexContent>
     *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                               &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                               &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}double" />
     *                                             &lt;/restriction>
     *                                           &lt;/complexContent>
     *                                         &lt;/complexType>
     *                                       &lt;/element>
     *                                     &lt;/sequence>
     *                                     &lt;attribute name="hit_rank" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                     &lt;attribute name="peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="peptide_prev_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="peptide_next_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="protein" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                                     &lt;attribute name="num_tot_proteins" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                     &lt;attribute name="num_matched_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                     &lt;attribute name="tot_num_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                     &lt;attribute name="calc_neutral_pep_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
     *                                     &lt;attribute name="massdiff" type="{http://www.w3.org/2001/XMLSchema}double" />
     *                                     &lt;attribute name="num_tol_term" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                     &lt;attribute name="num_missed_cleavages" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                     &lt;attribute name="num_matched_peptides" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="spectrum" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="spectrumNativeID" type="{http://www.w3.org/2001/XMLSchema}string" />
     *                 &lt;attribute name="start_scan" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                 &lt;attribute name="end_scan" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                 &lt;attribute name="precursor_neutral_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
     *                 &lt;attribute name="assumed_charge" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                 &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" />
     *                 &lt;attribute name="retention_time_sec" type="{http://www.w3.org/2001/XMLSchema}double" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="base_name" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="msManufacturer" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="msModel" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="raw_data_type" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="raw_data" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "sampleEnzyme",
        "searchSummary",
        "spectrumQuery"
    })
    public static class MsmsRunSummary {

        @XmlElement(name = "sample_enzyme", required = true)
        protected MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme sampleEnzyme;
        @XmlElement(name = "search_summary", required = true)
        protected MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary searchSummary;
        @XmlElement(name = "spectrum_query", required = true)
        protected List<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery> spectrumQuery;
        @XmlAttribute(name = "base_name")
        protected String baseName;
        @XmlAttribute(name = "msManufacturer")
        protected String msManufacturer;
        @XmlAttribute(name = "msModel")
        protected String msModel;
        @XmlAttribute(name = "raw_data_type")
        protected String rawDataType;
        @XmlAttribute(name = "raw_data")
        protected String rawData;

        /**
         * Obtiene el valor de la propiedad sampleEnzyme.
         * 
         * @return
         *     possible object is
         *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme }
         *     
         */
        public MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme getSampleEnzyme() {
            return sampleEnzyme;
        }

        /**
         * Define el valor de la propiedad sampleEnzyme.
         * 
         * @param value
         *     allowed object is
         *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme }
         *     
         */
        public void setSampleEnzyme(MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme value) {
            this.sampleEnzyme = value;
        }

        /**
         * Obtiene el valor de la propiedad searchSummary.
         * 
         * @return
         *     possible object is
         *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary }
         *     
         */
        public MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary getSearchSummary() {
            return searchSummary;
        }

        /**
         * Define el valor de la propiedad searchSummary.
         * 
         * @param value
         *     allowed object is
         *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary }
         *     
         */
        public void setSearchSummary(MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary value) {
            this.searchSummary = value;
        }

        /**
         * Gets the value of the spectrumQuery property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the spectrumQuery property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSpectrumQuery().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery }
         * 
         * 
         */
        public List<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery> getSpectrumQuery() {
            if (spectrumQuery == null) {
                spectrumQuery = new ArrayList<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery>();
            }
            return this.spectrumQuery;
        }

        /**
         * Obtiene el valor de la propiedad baseName.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getBaseName() {
            return baseName;
        }

        /**
         * Define el valor de la propiedad baseName.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setBaseName(String value) {
            this.baseName = value;
        }

        /**
         * Obtiene el valor de la propiedad msManufacturer.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMsManufacturer() {
            return msManufacturer;
        }

        /**
         * Define el valor de la propiedad msManufacturer.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMsManufacturer(String value) {
            this.msManufacturer = value;
        }

        /**
         * Obtiene el valor de la propiedad msModel.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMsModel() {
            return msModel;
        }

        /**
         * Define el valor de la propiedad msModel.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMsModel(String value) {
            this.msModel = value;
        }

        /**
         * Obtiene el valor de la propiedad rawDataType.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRawDataType() {
            return rawDataType;
        }

        /**
         * Define el valor de la propiedad rawDataType.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRawDataType(String value) {
            this.rawDataType = value;
        }

        /**
         * Obtiene el valor de la propiedad rawData.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getRawData() {
            return rawData;
        }

        /**
         * Define el valor de la propiedad rawData.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setRawData(String value) {
            this.rawData = value;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="specificity">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="cut" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="no_cut" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="sense" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "specificity"
        })
        public static class SampleEnzyme {

            @XmlElement(required = true)
            protected MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme.Specificity specificity;
            @XmlAttribute(name = "name")
            protected String name;

            /**
             * Obtiene el valor de la propiedad specificity.
             * 
             * @return
             *     possible object is
             *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme.Specificity }
             *     
             */
            public MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme.Specificity getSpecificity() {
                return specificity;
            }

            /**
             * Define el valor de la propiedad specificity.
             * 
             * @param value
             *     allowed object is
             *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme.Specificity }
             *     
             */
            public void setSpecificity(MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme.Specificity value) {
                this.specificity = value;
            }

            /**
             * Obtiene el valor de la propiedad name.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getName() {
                return name;
            }

            /**
             * Define el valor de la propiedad name.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setName(String value) {
                this.name = value;
            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="cut" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="no_cut" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="sense" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Specificity {

                @XmlAttribute(name = "cut")
                protected String cut;
                @XmlAttribute(name = "no_cut")
                protected String noCut;
                @XmlAttribute(name = "sense")
                protected String sense;

                /**
                 * Obtiene el valor de la propiedad cut.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getCut() {
                    return cut;
                }

                /**
                 * Define el valor de la propiedad cut.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setCut(String value) {
                    this.cut = value;
                }

                /**
                 * Obtiene el valor de la propiedad noCut.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getNoCut() {
                    return noCut;
                }

                /**
                 * Define el valor de la propiedad noCut.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setNoCut(String value) {
                    this.noCut = value;
                }

                /**
                 * Obtiene el valor de la propiedad sense.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSense() {
                    return sense;
                }

                /**
                 * Define el valor de la propiedad sense.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSense(String value) {
                    this.sense = value;
                }

            }

        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="search_database">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="local_path" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="enzymatic_search_constraint">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="enzyme" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="max_num_internal_cleavages" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                 &lt;attribute name="min_number_termini" type="{http://www.w3.org/2001/XMLSchema}int" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="aminoacid_modification" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="aminoacid" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="massdiff" type="{http://www.w3.org/2001/XMLSchema}double" />
         *                 &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
         *                 &lt;attribute name="variable" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="symbol" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="parameter" maxOccurs="unbounded">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="base_name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="search_engine" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="search_engine_version" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="precursor_mass_type" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="fragment_mass_type" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="search_id" type="{http://www.w3.org/2001/XMLSchema}int" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "searchDatabase",
            "enzymaticSearchConstraint",
            "aminoacidModification",
            "parameter"
        })
        public static class SearchSummary {

            @XmlElement(name = "search_database", required = true)
            protected MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.SearchDatabase searchDatabase;
            @XmlElement(name = "enzymatic_search_constraint", required = true)
            protected MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.EnzymaticSearchConstraint enzymaticSearchConstraint;
            @XmlElement(name = "aminoacid_modification", required = true)
            protected List<MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.AminoacidModification> aminoacidModification;
            @XmlElement(required = true)
            protected List<MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.Parameter> parameter;
            @XmlAttribute(name = "base_name")
            protected String baseName;
            @XmlAttribute(name = "search_engine")
            protected String searchEngine;
            @XmlAttribute(name = "search_engine_version")
            protected String searchEngineVersion;
            @XmlAttribute(name = "precursor_mass_type")
            protected String precursorMassType;
            @XmlAttribute(name = "fragment_mass_type")
            protected String fragmentMassType;
            @XmlAttribute(name = "search_id")
            protected Integer searchId;

            /**
             * Obtiene el valor de la propiedad searchDatabase.
             * 
             * @return
             *     possible object is
             *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.SearchDatabase }
             *     
             */
            public MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.SearchDatabase getSearchDatabase() {
                return searchDatabase;
            }

            /**
             * Define el valor de la propiedad searchDatabase.
             * 
             * @param value
             *     allowed object is
             *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.SearchDatabase }
             *     
             */
            public void setSearchDatabase(MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.SearchDatabase value) {
                this.searchDatabase = value;
            }

            /**
             * Obtiene el valor de la propiedad enzymaticSearchConstraint.
             * 
             * @return
             *     possible object is
             *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.EnzymaticSearchConstraint }
             *     
             */
            public MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.EnzymaticSearchConstraint getEnzymaticSearchConstraint() {
                return enzymaticSearchConstraint;
            }

            /**
             * Define el valor de la propiedad enzymaticSearchConstraint.
             * 
             * @param value
             *     allowed object is
             *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.EnzymaticSearchConstraint }
             *     
             */
            public void setEnzymaticSearchConstraint(MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.EnzymaticSearchConstraint value) {
                this.enzymaticSearchConstraint = value;
            }

            /**
             * Gets the value of the aminoacidModification property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the aminoacidModification property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getAminoacidModification().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.AminoacidModification }
             * 
             * 
             */
            public List<MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.AminoacidModification> getAminoacidModification() {
                if (aminoacidModification == null) {
                    aminoacidModification = new ArrayList<MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.AminoacidModification>();
                }
                return this.aminoacidModification;
            }

            /**
             * Gets the value of the parameter property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the parameter property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getParameter().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.Parameter }
             * 
             * 
             */
            public List<MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.Parameter> getParameter() {
                if (parameter == null) {
                    parameter = new ArrayList<MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary.Parameter>();
                }
                return this.parameter;
            }

            /**
             * Obtiene el valor de la propiedad baseName.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBaseName() {
                return baseName;
            }

            /**
             * Define el valor de la propiedad baseName.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBaseName(String value) {
                this.baseName = value;
            }

            /**
             * Obtiene el valor de la propiedad searchEngine.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSearchEngine() {
                return searchEngine;
            }

            /**
             * Define el valor de la propiedad searchEngine.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSearchEngine(String value) {
                this.searchEngine = value;
            }

            /**
             * Obtiene el valor de la propiedad searchEngineVersion.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSearchEngineVersion() {
                return searchEngineVersion;
            }

            /**
             * Define el valor de la propiedad searchEngineVersion.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSearchEngineVersion(String value) {
                this.searchEngineVersion = value;
            }

            /**
             * Obtiene el valor de la propiedad precursorMassType.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getPrecursorMassType() {
                return precursorMassType;
            }

            /**
             * Define el valor de la propiedad precursorMassType.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setPrecursorMassType(String value) {
                this.precursorMassType = value;
            }

            /**
             * Obtiene el valor de la propiedad fragmentMassType.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getFragmentMassType() {
                return fragmentMassType;
            }

            /**
             * Define el valor de la propiedad fragmentMassType.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setFragmentMassType(String value) {
                this.fragmentMassType = value;
            }

            /**
             * Obtiene el valor de la propiedad searchId.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getSearchId() {
                return searchId;
            }

            /**
             * Define el valor de la propiedad searchId.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setSearchId(Integer value) {
                this.searchId = value;
            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="aminoacid" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="massdiff" type="{http://www.w3.org/2001/XMLSchema}double" />
             *       &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
             *       &lt;attribute name="variable" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="symbol" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class AminoacidModification {

                @XmlAttribute(name = "aminoacid")
                protected String aminoacid;
                @XmlAttribute(name = "massdiff")
                protected Double massdiff;
                @XmlAttribute(name = "mass")
                protected Double mass;
                @XmlAttribute(name = "variable")
                protected String variable;
                @XmlAttribute(name = "symbol")
                protected String symbol;

                /**
                 * Obtiene el valor de la propiedad aminoacid.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getAminoacid() {
                    return aminoacid;
                }

                /**
                 * Define el valor de la propiedad aminoacid.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setAminoacid(String value) {
                    this.aminoacid = value;
                }

                /**
                 * Obtiene el valor de la propiedad massdiff.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Double }
                 *     
                 */
                public Double getMassdiff() {
                    return massdiff;
                }

                /**
                 * Define el valor de la propiedad massdiff.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Double }
                 *     
                 */
                public void setMassdiff(Double value) {
                    this.massdiff = value;
                }

                /**
                 * Obtiene el valor de la propiedad mass.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Double }
                 *     
                 */
                public Double getMass() {
                    return mass;
                }

                /**
                 * Define el valor de la propiedad mass.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Double }
                 *     
                 */
                public void setMass(Double value) {
                    this.mass = value;
                }

                /**
                 * Obtiene el valor de la propiedad variable.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getVariable() {
                    return variable;
                }

                /**
                 * Define el valor de la propiedad variable.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setVariable(String value) {
                    this.variable = value;
                }

                /**
                 * Obtiene el valor de la propiedad symbol.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getSymbol() {
                    return symbol;
                }

                /**
                 * Define el valor de la propiedad symbol.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setSymbol(String value) {
                    this.symbol = value;
                }

            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="enzyme" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="max_num_internal_cleavages" type="{http://www.w3.org/2001/XMLSchema}int" />
             *       &lt;attribute name="min_number_termini" type="{http://www.w3.org/2001/XMLSchema}int" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class EnzymaticSearchConstraint {

                @XmlAttribute(name = "enzyme")
                protected String enzyme;
                @XmlAttribute(name = "max_num_internal_cleavages")
                protected Integer maxNumInternalCleavages;
                @XmlAttribute(name = "min_number_termini")
                protected Integer minNumberTermini;

                /**
                 * Obtiene el valor de la propiedad enzyme.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getEnzyme() {
                    return enzyme;
                }

                /**
                 * Define el valor de la propiedad enzyme.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setEnzyme(String value) {
                    this.enzyme = value;
                }

                /**
                 * Obtiene el valor de la propiedad maxNumInternalCleavages.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Integer }
                 *     
                 */
                public Integer getMaxNumInternalCleavages() {
                    return maxNumInternalCleavages;
                }

                /**
                 * Define el valor de la propiedad maxNumInternalCleavages.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Integer }
                 *     
                 */
                public void setMaxNumInternalCleavages(Integer value) {
                    this.maxNumInternalCleavages = value;
                }

                /**
                 * Obtiene el valor de la propiedad minNumberTermini.
                 * 
                 * @return
                 *     possible object is
                 *     {@link Integer }
                 *     
                 */
                public Integer getMinNumberTermini() {
                    return minNumberTermini;
                }

                /**
                 * Define el valor de la propiedad minNumberTermini.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link Integer }
                 *     
                 */
                public void setMinNumberTermini(Integer value) {
                    this.minNumberTermini = value;
                }

            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Parameter {

                @XmlAttribute(name = "name")
                protected String name;
                @XmlAttribute(name = "value")
                protected String value;

                /**
                 * Obtiene el valor de la propiedad name.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getName() {
                    return name;
                }

                /**
                 * Define el valor de la propiedad name.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setName(String value) {
                    this.name = value;
                }

                /**
                 * Obtiene el valor de la propiedad value.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getValue() {
                    return value;
                }

                /**
                 * Define el valor de la propiedad value.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setValue(String value) {
                    this.value = value;
                }

            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;attribute name="local_path" type="{http://www.w3.org/2001/XMLSchema}string" />
             *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class SearchDatabase {

                @XmlAttribute(name = "local_path")
                protected String localPath;
                @XmlAttribute(name = "type")
                protected String type;

                /**
                 * Obtiene el valor de la propiedad localPath.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getLocalPath() {
                    return localPath;
                }

                /**
                 * Define el valor de la propiedad localPath.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setLocalPath(String value) {
                    this.localPath = value;
                }

                /**
                 * Obtiene el valor de la propiedad type.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String getType() {
                    return type;
                }

                /**
                 * Define el valor de la propiedad type.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void setType(String value) {
                    this.type = value;
                }

            }

        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="search_result">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="search_hit" maxOccurs="unbounded">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;sequence>
         *                             &lt;element name="modification_info">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;sequence>
         *                                       &lt;element name="mod_aminoacid_mass" maxOccurs="unbounded">
         *                                         &lt;complexType>
         *                                           &lt;complexContent>
         *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                               &lt;attribute name="position" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                                               &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
         *                                             &lt;/restriction>
         *                                           &lt;/complexContent>
         *                                         &lt;/complexType>
         *                                       &lt;/element>
         *                                     &lt;/sequence>
         *                                     &lt;attribute name="modified_peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                             &lt;element name="search_score" maxOccurs="unbounded">
         *                               &lt;complexType>
         *                                 &lt;complexContent>
         *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                                     &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                                     &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}double" />
         *                                   &lt;/restriction>
         *                                 &lt;/complexContent>
         *                               &lt;/complexType>
         *                             &lt;/element>
         *                           &lt;/sequence>
         *                           &lt;attribute name="hit_rank" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                           &lt;attribute name="peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="peptide_prev_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="peptide_next_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="protein" type="{http://www.w3.org/2001/XMLSchema}string" />
         *                           &lt;attribute name="num_tot_proteins" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                           &lt;attribute name="num_matched_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                           &lt;attribute name="tot_num_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                           &lt;attribute name="calc_neutral_pep_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
         *                           &lt;attribute name="massdiff" type="{http://www.w3.org/2001/XMLSchema}double" />
         *                           &lt;attribute name="num_tol_term" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                           &lt;attribute name="num_missed_cleavages" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                           &lt;attribute name="num_matched_peptides" type="{http://www.w3.org/2001/XMLSchema}int" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="spectrum" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="spectrumNativeID" type="{http://www.w3.org/2001/XMLSchema}string" />
         *       &lt;attribute name="start_scan" type="{http://www.w3.org/2001/XMLSchema}int" />
         *       &lt;attribute name="end_scan" type="{http://www.w3.org/2001/XMLSchema}int" />
         *       &lt;attribute name="precursor_neutral_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
         *       &lt;attribute name="assumed_charge" type="{http://www.w3.org/2001/XMLSchema}int" />
         *       &lt;attribute name="index" type="{http://www.w3.org/2001/XMLSchema}int" />
         *       &lt;attribute name="retention_time_sec" type="{http://www.w3.org/2001/XMLSchema}double" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "searchResult"
        })
        public static class SpectrumQuery {

            @XmlElement(name = "search_result", required = true)
            protected MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult searchResult;
            @XmlAttribute(name = "spectrum")
            protected String spectrum;
            @XmlAttribute(name = "spectrumNativeID")
            protected String spectrumNativeID;
            @XmlAttribute(name = "start_scan")
            protected Integer startScan;
            @XmlAttribute(name = "end_scan")
            protected Integer endScan;
            @XmlAttribute(name = "precursor_neutral_mass")
            protected Double precursorNeutralMass;
            @XmlAttribute(name = "assumed_charge")
            protected Integer assumedCharge;
            @XmlAttribute(name = "index")
            protected Integer index;
            @XmlAttribute(name = "retention_time_sec")
            protected Double retentionTimeSec;

            /**
             * Obtiene el valor de la propiedad searchResult.
             * 
             * @return
             *     possible object is
             *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult }
             *     
             */
            public MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult getSearchResult() {
                return searchResult;
            }

            /**
             * Define el valor de la propiedad searchResult.
             * 
             * @param value
             *     allowed object is
             *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult }
             *     
             */
            public void setSearchResult(MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult value) {
                this.searchResult = value;
            }

            /**
             * Obtiene el valor de la propiedad spectrum.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSpectrum() {
                return spectrum;
            }

            /**
             * Define el valor de la propiedad spectrum.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSpectrum(String value) {
                this.spectrum = value;
            }

            /**
             * Obtiene el valor de la propiedad spectrumNativeID.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getSpectrumNativeID() {
                return spectrumNativeID;
            }

            /**
             * Define el valor de la propiedad spectrumNativeID.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setSpectrumNativeID(String value) {
                this.spectrumNativeID = value;
            }

            /**
             * Obtiene el valor de la propiedad startScan.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getStartScan() {
                return startScan;
            }

            /**
             * Define el valor de la propiedad startScan.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setStartScan(Integer value) {
                this.startScan = value;
            }

            /**
             * Obtiene el valor de la propiedad endScan.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getEndScan() {
                return endScan;
            }

            /**
             * Define el valor de la propiedad endScan.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setEndScan(Integer value) {
                this.endScan = value;
            }

            /**
             * Obtiene el valor de la propiedad precursorNeutralMass.
             * 
             * @return
             *     possible object is
             *     {@link Double }
             *     
             */
            public Double getPrecursorNeutralMass() {
                return precursorNeutralMass;
            }

            /**
             * Define el valor de la propiedad precursorNeutralMass.
             * 
             * @param value
             *     allowed object is
             *     {@link Double }
             *     
             */
            public void setPrecursorNeutralMass(Double value) {
                this.precursorNeutralMass = value;
            }

            /**
             * Obtiene el valor de la propiedad assumedCharge.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getAssumedCharge() {
                return assumedCharge;
            }

            /**
             * Define el valor de la propiedad assumedCharge.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setAssumedCharge(Integer value) {
                this.assumedCharge = value;
            }

            /**
             * Obtiene el valor de la propiedad index.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getIndex() {
                return index;
            }

            /**
             * Define el valor de la propiedad index.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setIndex(Integer value) {
                this.index = value;
            }

            /**
             * Obtiene el valor de la propiedad retentionTimeSec.
             * 
             * @return
             *     possible object is
             *     {@link Double }
             *     
             */
            public Double getRetentionTimeSec() {
                return retentionTimeSec;
            }

            /**
             * Define el valor de la propiedad retentionTimeSec.
             * 
             * @param value
             *     allowed object is
             *     {@link Double }
             *     
             */
            public void setRetentionTimeSec(Double value) {
                this.retentionTimeSec = value;
            }


            /**
             * <p>Clase Java para anonymous complex type.
             * 
             * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
             * 
             * <pre>
             * &lt;complexType>
             *   &lt;complexContent>
             *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *       &lt;sequence>
             *         &lt;element name="search_hit" maxOccurs="unbounded">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;sequence>
             *                   &lt;element name="modification_info">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;sequence>
             *                             &lt;element name="mod_aminoacid_mass" maxOccurs="unbounded">
             *                               &lt;complexType>
             *                                 &lt;complexContent>
             *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                                     &lt;attribute name="position" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                                     &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
             *                                   &lt;/restriction>
             *                                 &lt;/complexContent>
             *                               &lt;/complexType>
             *                             &lt;/element>
             *                           &lt;/sequence>
             *                           &lt;attribute name="modified_peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                   &lt;element name="search_score" maxOccurs="unbounded">
             *                     &lt;complexType>
             *                       &lt;complexContent>
             *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                           &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                           &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}double" />
             *                         &lt;/restriction>
             *                       &lt;/complexContent>
             *                     &lt;/complexType>
             *                   &lt;/element>
             *                 &lt;/sequence>
             *                 &lt;attribute name="hit_rank" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                 &lt;attribute name="peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="peptide_prev_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="peptide_next_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="protein" type="{http://www.w3.org/2001/XMLSchema}string" />
             *                 &lt;attribute name="num_tot_proteins" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                 &lt;attribute name="num_matched_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                 &lt;attribute name="tot_num_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                 &lt;attribute name="calc_neutral_pep_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
             *                 &lt;attribute name="massdiff" type="{http://www.w3.org/2001/XMLSchema}double" />
             *                 &lt;attribute name="num_tol_term" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                 &lt;attribute name="num_missed_cleavages" type="{http://www.w3.org/2001/XMLSchema}int" />
             *                 &lt;attribute name="num_matched_peptides" type="{http://www.w3.org/2001/XMLSchema}int" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *       &lt;/sequence>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "", propOrder = {
                "searchHit"
            })
            public static class SearchResult {

                @XmlElement(name = "search_hit", required = true)
                protected List<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit> searchHit;

                /**
                 * Gets the value of the searchHit property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the searchHit property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getSearchHit().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit }
                 * 
                 * 
                 */
                public List<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit> getSearchHit() {
                    if (searchHit == null) {
                        searchHit = new ArrayList<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit>();
                    }
                    return this.searchHit;
                }


                /**
                 * <p>Clase Java para anonymous complex type.
                 * 
                 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                 * 
                 * <pre>
                 * &lt;complexType>
                 *   &lt;complexContent>
                 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *       &lt;sequence>
                 *         &lt;element name="modification_info">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;sequence>
                 *                   &lt;element name="mod_aminoacid_mass" maxOccurs="unbounded">
                 *                     &lt;complexType>
                 *                       &lt;complexContent>
                 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                           &lt;attribute name="position" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *                           &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
                 *                         &lt;/restriction>
                 *                       &lt;/complexContent>
                 *                     &lt;/complexType>
                 *                   &lt;/element>
                 *                 &lt;/sequence>
                 *                 &lt;attribute name="modified_peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *         &lt;element name="search_score" maxOccurs="unbounded">
                 *           &lt;complexType>
                 *             &lt;complexContent>
                 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *                 &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}double" />
                 *               &lt;/restriction>
                 *             &lt;/complexContent>
                 *           &lt;/complexType>
                 *         &lt;/element>
                 *       &lt;/sequence>
                 *       &lt;attribute name="hit_rank" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="peptide_prev_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="peptide_next_aa" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="protein" type="{http://www.w3.org/2001/XMLSchema}string" />
                 *       &lt;attribute name="num_tot_proteins" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="num_matched_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="tot_num_ions" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="calc_neutral_pep_mass" type="{http://www.w3.org/2001/XMLSchema}double" />
                 *       &lt;attribute name="massdiff" type="{http://www.w3.org/2001/XMLSchema}double" />
                 *       &lt;attribute name="num_tol_term" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="num_missed_cleavages" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *       &lt;attribute name="num_matched_peptides" type="{http://www.w3.org/2001/XMLSchema}int" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "", propOrder = {
                    "modificationInfo",
                    "searchScore"
                })
                public static class SearchHit {

                    @XmlElement(name = "modification_info", required = true)
                    protected MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo modificationInfo;
                    @XmlElement(name = "search_score", required = true)
                    protected List<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.SearchScore> searchScore;
                    @XmlAttribute(name = "hit_rank")
                    protected Integer hitRank;
                    @XmlAttribute(name = "peptide")
                    protected String peptide;
                    @XmlAttribute(name = "peptide_prev_aa")
                    protected String peptidePrevAa;
                    @XmlAttribute(name = "peptide_next_aa")
                    protected String peptideNextAa;
                    @XmlAttribute(name = "protein")
                    protected String protein;
                    @XmlAttribute(name = "num_tot_proteins")
                    protected Integer numTotProteins;
                    @XmlAttribute(name = "num_matched_ions")
                    protected Integer numMatchedIons;
                    @XmlAttribute(name = "tot_num_ions")
                    protected Integer totNumIons;
                    @XmlAttribute(name = "calc_neutral_pep_mass")
                    protected Double calcNeutralPepMass;
                    @XmlAttribute(name = "massdiff")
                    protected Double massdiff;
                    @XmlAttribute(name = "num_tol_term")
                    protected Integer numTolTerm;
                    @XmlAttribute(name = "num_missed_cleavages")
                    protected Integer numMissedCleavages;
                    @XmlAttribute(name = "num_matched_peptides")
                    protected Integer numMatchedPeptides;

                    /**
                     * Obtiene el valor de la propiedad modificationInfo.
                     * 
                     * @return
                     *     possible object is
                     *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo }
                     *     
                     */
                    public MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo getModificationInfo() {
                        return modificationInfo;
                    }

                    /**
                     * Define el valor de la propiedad modificationInfo.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo }
                     *     
                     */
                    public void setModificationInfo(MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo value) {
                        this.modificationInfo = value;
                    }

                    /**
                     * Gets the value of the searchScore property.
                     * 
                     * <p>
                     * This accessor method returns a reference to the live list,
                     * not a snapshot. Therefore any modification you make to the
                     * returned list will be present inside the JAXB object.
                     * This is why there is not a <CODE>set</CODE> method for the searchScore property.
                     * 
                     * <p>
                     * For example, to add a new item, do as follows:
                     * <pre>
                     *    getSearchScore().add(newItem);
                     * </pre>
                     * 
                     * 
                     * <p>
                     * Objects of the following type(s) are allowed in the list
                     * {@link MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.SearchScore }
                     * 
                     * 
                     */
                    public List<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.SearchScore> getSearchScore() {
                        if (searchScore == null) {
                            searchScore = new ArrayList<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.SearchScore>();
                        }
                        return this.searchScore;
                    }

                    /**
                     * Obtiene el valor de la propiedad hitRank.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *     
                     */
                    public Integer getHitRank() {
                        return hitRank;
                    }

                    /**
                     * Define el valor de la propiedad hitRank.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *     
                     */
                    public void setHitRank(Integer value) {
                        this.hitRank = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad peptide.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getPeptide() {
                        return peptide;
                    }

                    /**
                     * Define el valor de la propiedad peptide.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setPeptide(String value) {
                        this.peptide = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad peptidePrevAa.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getPeptidePrevAa() {
                        return peptidePrevAa;
                    }

                    /**
                     * Define el valor de la propiedad peptidePrevAa.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setPeptidePrevAa(String value) {
                        this.peptidePrevAa = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad peptideNextAa.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getPeptideNextAa() {
                        return peptideNextAa;
                    }

                    /**
                     * Define el valor de la propiedad peptideNextAa.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setPeptideNextAa(String value) {
                        this.peptideNextAa = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad protein.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getProtein() {
                        return protein;
                    }

                    /**
                     * Define el valor de la propiedad protein.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setProtein(String value) {
                        this.protein = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad numTotProteins.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *     
                     */
                    public Integer getNumTotProteins() {
                        return numTotProteins;
                    }

                    /**
                     * Define el valor de la propiedad numTotProteins.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *     
                     */
                    public void setNumTotProteins(Integer value) {
                        this.numTotProteins = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad numMatchedIons.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *     
                     */
                    public Integer getNumMatchedIons() {
                        return numMatchedIons;
                    }

                    /**
                     * Define el valor de la propiedad numMatchedIons.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *     
                     */
                    public void setNumMatchedIons(Integer value) {
                        this.numMatchedIons = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad totNumIons.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *     
                     */
                    public Integer getTotNumIons() {
                        return totNumIons;
                    }

                    /**
                     * Define el valor de la propiedad totNumIons.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *     
                     */
                    public void setTotNumIons(Integer value) {
                        this.totNumIons = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad calcNeutralPepMass.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Double }
                     *     
                     */
                    public Double getCalcNeutralPepMass() {
                        return calcNeutralPepMass;
                    }

                    /**
                     * Define el valor de la propiedad calcNeutralPepMass.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Double }
                     *     
                     */
                    public void setCalcNeutralPepMass(Double value) {
                        this.calcNeutralPepMass = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad massdiff.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Double }
                     *     
                     */
                    public Double getMassdiff() {
                        return massdiff;
                    }

                    /**
                     * Define el valor de la propiedad massdiff.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Double }
                     *     
                     */
                    public void setMassdiff(Double value) {
                        this.massdiff = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad numTolTerm.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *     
                     */
                    public Integer getNumTolTerm() {
                        return numTolTerm;
                    }

                    /**
                     * Define el valor de la propiedad numTolTerm.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *     
                     */
                    public void setNumTolTerm(Integer value) {
                        this.numTolTerm = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad numMissedCleavages.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *     
                     */
                    public Integer getNumMissedCleavages() {
                        return numMissedCleavages;
                    }

                    /**
                     * Define el valor de la propiedad numMissedCleavages.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *     
                     */
                    public void setNumMissedCleavages(Integer value) {
                        this.numMissedCleavages = value;
                    }

                    /**
                     * Obtiene el valor de la propiedad numMatchedPeptides.
                     * 
                     * @return
                     *     possible object is
                     *     {@link Integer }
                     *     
                     */
                    public Integer getNumMatchedPeptides() {
                        return numMatchedPeptides;
                    }

                    /**
                     * Define el valor de la propiedad numMatchedPeptides.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link Integer }
                     *     
                     */
                    public void setNumMatchedPeptides(Integer value) {
                        this.numMatchedPeptides = value;
                    }


                    /**
                     * <p>Clase Java para anonymous complex type.
                     * 
                     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;complexContent>
                     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *       &lt;sequence>
                     *         &lt;element name="mod_aminoacid_mass" maxOccurs="unbounded">
                     *           &lt;complexType>
                     *             &lt;complexContent>
                     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *                 &lt;attribute name="position" type="{http://www.w3.org/2001/XMLSchema}int" />
                     *                 &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
                     *               &lt;/restriction>
                     *             &lt;/complexContent>
                     *           &lt;/complexType>
                     *         &lt;/element>
                     *       &lt;/sequence>
                     *       &lt;attribute name="modified_peptide" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "", propOrder = {
                        "modAminoacidMass"
                    })
                    public static class ModificationInfo {

                        @XmlElement(name = "mod_aminoacid_mass", required = true)
                        protected List<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo.ModAminoacidMass> modAminoacidMass;
                        @XmlAttribute(name = "modified_peptide")
                        protected String modifiedPeptide;

                        /**
                         * Gets the value of the modAminoacidMass property.
                         * 
                         * <p>
                         * This accessor method returns a reference to the live list,
                         * not a snapshot. Therefore any modification you make to the
                         * returned list will be present inside the JAXB object.
                         * This is why there is not a <CODE>set</CODE> method for the modAminoacidMass property.
                         * 
                         * <p>
                         * For example, to add a new item, do as follows:
                         * <pre>
                         *    getModAminoacidMass().add(newItem);
                         * </pre>
                         * 
                         * 
                         * <p>
                         * Objects of the following type(s) are allowed in the list
                         * {@link MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo.ModAminoacidMass }
                         * 
                         * 
                         */
                        public List<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo.ModAminoacidMass> getModAminoacidMass() {
                            if (modAminoacidMass == null) {
                                modAminoacidMass = new ArrayList<MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit.ModificationInfo.ModAminoacidMass>();
                            }
                            return this.modAminoacidMass;
                        }

                        /**
                         * Obtiene el valor de la propiedad modifiedPeptide.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getModifiedPeptide() {
                            return modifiedPeptide;
                        }

                        /**
                         * Define el valor de la propiedad modifiedPeptide.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setModifiedPeptide(String value) {
                            this.modifiedPeptide = value;
                        }


                        /**
                         * <p>Clase Java para anonymous complex type.
                         * 
                         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                         * 
                         * <pre>
                         * &lt;complexType>
                         *   &lt;complexContent>
                         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                         *       &lt;attribute name="position" type="{http://www.w3.org/2001/XMLSchema}int" />
                         *       &lt;attribute name="mass" type="{http://www.w3.org/2001/XMLSchema}double" />
                         *     &lt;/restriction>
                         *   &lt;/complexContent>
                         * &lt;/complexType>
                         * </pre>
                         * 
                         * 
                         */
                        @XmlAccessorType(XmlAccessType.FIELD)
                        @XmlType(name = "")
                        public static class ModAminoacidMass {

                            @XmlAttribute(name = "position")
                            protected Integer position;
                            @XmlAttribute(name = "mass")
                            protected Double mass;

                            /**
                             * Obtiene el valor de la propiedad position.
                             * 
                             * @return
                             *     possible object is
                             *     {@link Integer }
                             *     
                             */
                            public Integer getPosition() {
                                return position;
                            }

                            /**
                             * Define el valor de la propiedad position.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link Integer }
                             *     
                             */
                            public void setPosition(Integer value) {
                                this.position = value;
                            }

                            /**
                             * Obtiene el valor de la propiedad mass.
                             * 
                             * @return
                             *     possible object is
                             *     {@link Double }
                             *     
                             */
                            public Double getMass() {
                                return mass;
                            }

                            /**
                             * Define el valor de la propiedad mass.
                             * 
                             * @param value
                             *     allowed object is
                             *     {@link Double }
                             *     
                             */
                            public void setMass(Double value) {
                                this.mass = value;
                            }

                        }

                    }


                    /**
                     * <p>Clase Java para anonymous complex type.
                     * 
                     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
                     * 
                     * <pre>
                     * &lt;complexType>
                     *   &lt;complexContent>
                     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
                     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
                     *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}double" />
                     *     &lt;/restriction>
                     *   &lt;/complexContent>
                     * &lt;/complexType>
                     * </pre>
                     * 
                     * 
                     */
                    @XmlAccessorType(XmlAccessType.FIELD)
                    @XmlType(name = "")
                    public static class SearchScore {

                        @XmlAttribute(name = "name")
                        protected String name;
                        @XmlAttribute(name = "value")
                        protected Double value;

                        /**
                         * Obtiene el valor de la propiedad name.
                         * 
                         * @return
                         *     possible object is
                         *     {@link String }
                         *     
                         */
                        public String getName() {
                            return name;
                        }

                        /**
                         * Define el valor de la propiedad name.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link String }
                         *     
                         */
                        public void setName(String value) {
                            this.name = value;
                        }

                        /**
                         * Obtiene el valor de la propiedad value.
                         * 
                         * @return
                         *     possible object is
                         *     {@link Double }
                         *     
                         */
                        public Double getValue() {
                            return value;
                        }

                        /**
                         * Define el valor de la propiedad value.
                         * 
                         * @param value
                         *     allowed object is
                         *     {@link Double }
                         *     
                         */
                        public void setValue(Double value) {
                            this.value = value;
                        }

                    }

                }

            }

        }

    }

}

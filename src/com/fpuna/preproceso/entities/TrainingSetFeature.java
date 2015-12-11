/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpuna.preproceso.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Santirrium
 */
@Entity
@Table(name = "\"TrainingSetFeature\"")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "TrainingSetFeature.findAll", query = "SELECT t FROM TrainingSetFeature t"),
    @NamedQuery(name = "TrainingSetFeature.findByIdTs", query = "SELECT t FROM TrainingSetFeature t WHERE t.idTs = :idTs"),
    @NamedQuery(name = "TrainingSetFeature.findByEtiqueta", query = "SELECT t FROM TrainingSetFeature t WHERE t.etiqueta = :etiqueta"),
    @NamedQuery(name = "TrainingSetFeature.findByMeanX", query = "SELECT t FROM TrainingSetFeature t WHERE t.meanX = :meanX"),
    @NamedQuery(name = "TrainingSetFeature.findByStdX", query = "SELECT t FROM TrainingSetFeature t WHERE t.stdX = :stdX"),
    @NamedQuery(name = "TrainingSetFeature.findByMaxX", query = "SELECT t FROM TrainingSetFeature t WHERE t.maxX = :maxX"),
    @NamedQuery(name = "TrainingSetFeature.findByMinX", query = "SELECT t FROM TrainingSetFeature t WHERE t.minX = :minX"),
    @NamedQuery(name = "TrainingSetFeature.findBySkewnessX", query = "SELECT t FROM TrainingSetFeature t WHERE t.skewnessX = :skewnessX"),
    @NamedQuery(name = "TrainingSetFeature.findByKurtosisX", query = "SELECT t FROM TrainingSetFeature t WHERE t.kurtosisX = :kurtosisX"),
    @NamedQuery(name = "TrainingSetFeature.findByEnergyX", query = "SELECT t FROM TrainingSetFeature t WHERE t.energyX = :energyX"),
    @NamedQuery(name = "TrainingSetFeature.findByEntropyX", query = "SELECT t FROM TrainingSetFeature t WHERE t.entropyX = :entropyX"),
    @NamedQuery(name = "TrainingSetFeature.findByIqrX", query = "SELECT t FROM TrainingSetFeature t WHERE t.iqrX = :iqrX"),
    @NamedQuery(name = "TrainingSetFeature.findByArX1", query = "SELECT t FROM TrainingSetFeature t WHERE t.arX1 = :arX1"),
    @NamedQuery(name = "TrainingSetFeature.findByArX2", query = "SELECT t FROM TrainingSetFeature t WHERE t.arX2 = :arX2"),
    @NamedQuery(name = "TrainingSetFeature.findByArX3", query = "SELECT t FROM TrainingSetFeature t WHERE t.arX3 = :arX3"),
    @NamedQuery(name = "TrainingSetFeature.findByArX4", query = "SELECT t FROM TrainingSetFeature t WHERE t.arX4 = :arX4"),
    @NamedQuery(name = "TrainingSetFeature.findByMeanFreqx", query = "SELECT t FROM TrainingSetFeature t WHERE t.meanFreqx = :meanFreqx")})
public class TrainingSetFeature implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "\"ID_TS\"")
    private Integer idTs;
    @Basic(optional = false)
    @Column(name = "\"Etiqueta\"")
    private String etiqueta;
    @Basic(optional = false)
    @Column(name = "mean_x")
    private float meanX;
    @Basic(optional = false)
    @Column(name = "std_x")
    private float stdX;
    @Basic(optional = false)
    @Column(name = "max_x")
    private float maxX;
    @Basic(optional = false)
    @Column(name = "min_x")
    private float minX;
    @Basic(optional = false)
    @Column(name = "skewness_x")
    private float skewnessX;
    @Basic(optional = false)
    @Column(name = "kurtosis_x")
    private float kurtosisX;
    @Basic(optional = false)
    @Column(name = "energy_x")
    private float energyX;
    @Basic(optional = false)
    @Column(name = "entropy_x")
    private float entropyX;
    @Basic(optional = false)
    @Column(name = "iqr_x")
    private float iqrX;
    @Basic(optional = false)
    @Column(name = "ar_x_1")
    private float arX1;
    @Basic(optional = false)
    @Column(name = "ar_x_2")
    private float arX2;
    @Basic(optional = false)
    @Column(name = "ar_x_3")
    private float arX3;
    @Basic(optional = false)
    @Column(name = "ar_x_4")
    private float arX4;
    @Basic(optional = false)
    @Column(name = "\"meanFreq_x\"")
    private float meanFreqx;

    public TrainingSetFeature() {
    }

    public TrainingSetFeature(Integer idTs) {
        this.idTs = idTs;
    }

    public TrainingSetFeature(Integer idTs, String etiqueta, float meanX, float stdX, float maxX, float minX, float skewnessX, float kurtosisX, float energyX, float entropyX, float iqrX, float arX1, float arX2, float arX3, float arX4, float meanFreqx) {
        this.idTs = idTs;
        this.etiqueta = etiqueta;
        this.meanX = meanX;
        this.stdX = stdX;
        this.maxX = maxX;
        this.minX = minX;
        this.skewnessX = skewnessX;
        this.kurtosisX = kurtosisX;
        this.energyX = energyX;
        this.entropyX = entropyX;
        this.iqrX = iqrX;
        this.arX1 = arX1;
        this.arX2 = arX2;
        this.arX3 = arX3;
        this.arX4 = arX4;
        this.meanFreqx = meanFreqx;
    }

    public Integer getIdTs() {
        return idTs;
    }

    public void setIdTs(Integer idTs) {
        this.idTs = idTs;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public float getMeanX() {
        return meanX;
    }

    public void setMeanX(float meanX) {
        this.meanX = meanX;
    }

    public float getStdX() {
        return stdX;
    }

    public void setStdX(float stdX) {
        this.stdX = stdX;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getSkewnessX() {
        return skewnessX;
    }

    public void setSkewnessX(float skewnessX) {
        this.skewnessX = skewnessX;
    }

    public float getKurtosisX() {
        return kurtosisX;
    }

    public void setKurtosisX(float kurtosisX) {
        this.kurtosisX = kurtosisX;
    }

    public float getEnergyX() {
        return energyX;
    }

    public void setEnergyX(float energyX) {
        this.energyX = energyX;
    }

    public float getEntropyX() {
        return entropyX;
    }

    public void setEntropyX(float entropyX) {
        this.entropyX = entropyX;
    }

    public float getIqrX() {
        return iqrX;
    }

    public void setIqrX(float iqrX) {
        this.iqrX = iqrX;
    }

    public float getArX1() {
        return arX1;
    }

    public void setArX1(float arX1) {
        this.arX1 = arX1;
    }

    public float getArX2() {
        return arX2;
    }

    public void setArX2(float arX2) {
        this.arX2 = arX2;
    }

    public float getArX3() {
        return arX3;
    }

    public void setArX3(float arX3) {
        this.arX3 = arX3;
    }

    public float getArX4() {
        return arX4;
    }

    public void setArX4(float arX4) {
        this.arX4 = arX4;
    }

    public float getMeanFreqx() {
        return meanFreqx;
    }

    public void setMeanFreqx(float meanFreqx) {
        this.meanFreqx = meanFreqx;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTs != null ? idTs.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TrainingSetFeature)) {
            return false;
        }
        TrainingSetFeature other = (TrainingSetFeature) object;
        if ((this.idTs == null && other.idTs != null) || (this.idTs != null && !this.idTs.equals(other.idTs))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.fpuna.preproceso.entities.TrainingSetFeature[ idTs=" + idTs + " ]";
    }
    
}

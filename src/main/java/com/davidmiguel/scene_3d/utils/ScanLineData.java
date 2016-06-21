package com.davidmiguel.scene_3d.utils;

/**
 * ScanLineData.
 *
 * @author davidmigloz
 * @since 21/06/2016
 */
class ScanLineData {
    private int currentY;
    private double ndotla;
    private double ndotlb;
    private double ndotlc;
    private double ndotld;

    private double ua;
    private double ub;
    private double uc;
    private double ud;

    private double va;
    private double vb;
    private double vc;
    private double vd;

    public int getCurrentY() {
        return currentY;
    }

    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    public double getNdotla() {
        return ndotla;
    }

    public void setNdotla(double ndotla) {
        this.ndotla = ndotla;
    }

    public double getNdotlb() {
        return ndotlb;
    }

    public void setNdotlb(double ndotlb) {
        this.ndotlb = ndotlb;
    }

    public double getNdotlc() {
        return ndotlc;
    }

    public void setNdotlc(double ndotlc) {
        this.ndotlc = ndotlc;
    }

    public double getNdotld() {
        return ndotld;
    }

    public void setNdotld(double ndotld) {
        this.ndotld = ndotld;
    }

    public double getUa() {
        return ua;
    }

    public void setUa(double ua) {
        this.ua = ua;
    }

    public double getUb() {
        return ub;
    }

    public void setUb(double ub) {
        this.ub = ub;
    }

    public double getUc() {
        return uc;
    }

    public void setUc(double uc) {
        this.uc = uc;
    }

    public double getUd() {
        return ud;
    }

    public void setUd(double ud) {
        this.ud = ud;
    }

    public double getVa() {
        return va;
    }

    public void setVa(double va) {
        this.va = va;
    }

    public double getVb() {
        return vb;
    }

    public void setVb(double vb) {
        this.vb = vb;
    }

    public double getVc() {
        return vc;
    }

    public void setVc(double vc) {
        this.vc = vc;
    }

    public double getVd() {
        return vd;
    }

    public void setVd(double vd) {
        this.vd = vd;
    }
}

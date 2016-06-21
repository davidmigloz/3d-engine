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

    int getCurrentY() {
        return currentY;
    }

    void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    double getNdotla() {
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
}

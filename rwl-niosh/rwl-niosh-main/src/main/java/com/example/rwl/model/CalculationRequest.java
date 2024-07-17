package com.example.rwl.model;

public class CalculationRequest {

    private final double loadConstant = 23;
    private double H;
    private double V;
    private double D;
    private double A;
    private double F;
    private double workDuration;
    private CouplingType couplingType;
    private double L; // Actual Weight

    public double getLoadConstant() {
        return loadConstant;
    }

    public double getH() {
        return H;
    }

    public void setH(double h) {
        H = h;
    }

    public double getV() {
        return V;
    }

    public void setV(double v) {
        V = v;
    }

    public double getD() {
        return D;
    }

    public void setD(double d) {
        D = d;
    }

    public double getA() {
        return A;
    }

    public void setA(double a) {
        A = a;
    }

    public double getF() {
        return F;
    }

    public void setF(double f) {
        F = f;
    }
    public double getWorkDuration() {
        return workDuration;
    }

    public void setWorkDuration(double workDuration) {
        this.workDuration = workDuration;
    }

    public CouplingType getCouplingType() {
        return couplingType;
    }

    public void setCouplingType(CouplingType couplingType) {
        this.couplingType = couplingType;
    }

    public double getL() {
        return L;
    }

    public void setL(double l) {
        L = l;
    }

}

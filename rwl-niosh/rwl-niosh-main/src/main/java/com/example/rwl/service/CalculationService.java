package com.example.rwl.service;

import com.example.rwl.model.CalculationRequest;
import com.example.rwl.model.CouplingType;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@Service
public class CalculationService {

    private final CalculationRequest calculationRequest;

    public CalculationService(CalculationRequest calculationRequest) {
        this.calculationRequest = calculationRequest;
    }

    public double horizontalMultiplier(double H){
        return roundNumber(25 / H);
    }

    public boolean recommendationHorizontal(double H){
        if(H > 30 ){
            return true;
        }
        return false;
    }

    public double verticalMultiplier(double V){
        return roundNumber(1 - (0.003 * Math.abs(V-75)));
    }

    public double distanceMultiplier(double D ){
        return roundNumber(0.82 + (4.5 / D));
    }

    public double asymmetryMultiplier(double A){
        return roundNumber(1 - (0.0032 * A));
    }


    // Function to calculate FM based on F, array, and index range
    private double calculateFM(double F, double[] array, int endIndex) {
        double FM;
        if (F <= 0.2) {
            FM = array[0];
        } else if (F == 0.5) {
            FM = array[1];
        } else if (F >= 1 && F <= endIndex) {
            FM = array[1 + (int) F];
        } else {
            FM = 0;
        }
        return roundNumber(FM);
    }

    public double frequencyMultiplier(double F, double V, double workDuration){
        double FM = 0;
        double[] smallerThanOne = {1, 0.97, 0.94, 0.91, 0.88, 0.84, 0.80, 0.75, 0.70, 0.60, 0.52, 0.45, 0.41, 0.37, 0.34, 0.31, 0.28, 0};
        double[] biggerThanOne_smallerThanTwo = {0.95, 0.92, 0.88, 0.84, 0.79, 0.72, 0.60, 0.50, 0.42, 0.35, 0.30, 0.26, 0.23, 0.21, 0};
        double[] biggerThanTwo_smallerThanEight = {0.85, 0.81, 0.75, 0.65, 0.55, 0.45, 0.35, 0.27, 0.22, 0.18, 0.15, 0.13, 0};

        if(workDuration <= 1){
            if (V < 75) {
                FM = calculateFM(F, smallerThanOne, 12);
            } else if (V >= 75) {
                FM = calculateFM(F, smallerThanOne, 15);
            }
        }
        else if(workDuration > 1 && workDuration <=2){
            if (V < 75) {
                FM = calculateFM(F, biggerThanOne_smallerThanTwo, 10);
            } else if (V >= 75) {
                FM = calculateFM(F, biggerThanOne_smallerThanTwo, 12);
            }
        }
        else if(workDuration > 2 && workDuration <= 8){
            if (V < 75) {
                FM = calculateFM(F, biggerThanTwo_smallerThanEight, 8);
            } else if (V >= 75) {
                FM = calculateFM(F, biggerThanTwo_smallerThanEight, 11);
            }
        }
        return roundNumber(FM);
    }

    public double couplingMultiplier(double V, CouplingType couplingType){
        double CM = 0;
        if(couplingType == CouplingType.GOOD){
            CM = 1;
        }
        else if(couplingType == CouplingType.FAIR){
            if(V<75){
                CM = 0.95;
            }
            else if(V>75){
                CM = 1;
            }
        }
        else if(couplingType == CouplingType.POOR){
            CM = 0.9;
        }
        return roundNumber(CM);
    }

    public double calculateRWL(double LC, double HM, double VM, double DM, double AM, double FM, double CM){
        double RWL;

        RWL = LC * HM * VM * DM * AM * FM * CM;
        return roundNumber(RWL);
    }

    public double loadIndex(double L, double RWL){
        double LI = (L / RWL);

        return roundNumber(LI);
    }

    private double roundNumber(double number){
        try {
            DecimalFormat df = new DecimalFormat("#.###");
            double roundedNumber = Double.parseDouble(df.format(number));
            return roundedNumber;
        }
        catch (NumberFormatException numberFormatException){
            return 0;
        }
    }

}

package com.example.template;
import com.google.common.truth.Truth.assertThat;
import org.junit.Test;
import android.util.Log;

/*
// taken from https://developer.android.com/training/testing/unit-testing/local-unit-tests#java
public class ExampleTest {
    double IRR_FACTOR = 0.85;
    // this is the weightedAverage method copied from the Individual_Pokemon_view file, the import statement wasn't working
    public int weightedAverage(int hp, int atk, int def, int spd, int satk, int sdef) {
        int avg = 0;
        int sum = 0;
        int count = 6;
        if (satk < Math.round(atk * IRR_FACTOR)) {
            count = 5;
            sum = hp + atk + def + spd + sdef;
        }
        else if (atk < Math.round(satk * IRR_FACTOR)) {
            count = 5;
            sum = hp + def + spd + satk + sdef;
        }
        else {
            sum = hp + atk + def + spd + satk + sdef;
        }
        avg = sum / count;

        return avg;
    }
    @Test
    // should test the weighted average
    public String testWeightedAverage() {

        // our method takes into account that often times pokemon only attack physically or specially,
        // we will be testing with a pokemon who uses special, physical, and one who uses both
        String returnVal = "";
        int blisseyExpected = 106;
        int blisseyCalc = weightedAverage(255, 10, 10, 55, 75, 135);
        if(blisseyCalc == blisseyExpected)
            returnVal += "Passed Blissey (low ATK, high SATK)";
        else
            returnVal += "Failed Blissey (low ATK, high SATK)";

        int relekExpected = 96;      // HP, ATK, DEF, SPD, SATK, SDEF
        int relekCalc = weightedAverage(80, 100, 50, 200, 100, 50);
        if(relekCalc == relekExpected)
            returnVal += "Passed Regieleki (balanced)";
        else
            returnVal += "Failed Regieleki (balanced)";

        int ramExpected = 86;      // HP, ATK, DEF, SPD, SATK, SDEF
        int ramCalc = weightedAverage(97, 165, 60, 58, 65, 50);
        if(ramCalc == ramExpected)
            returnVal += "Passed Rampardos (high ATK, low SATK)";
        else
            returnVal += "Failed Rampardos (high ATK, low SATK)";

        return returnVal;
    }*/
}

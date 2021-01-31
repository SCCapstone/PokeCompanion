package com.example.template;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
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
    public void testWeightedAverage() {

        // our method takes into account that often times pokemon only attack physically or specially,
        // we will be testing with a pokemon who uses special, physical, and one who uses both
        String returnVal = "";
        int blisseyExpected = 106;
        int blisseyCalc = weightedAverage(255, 10, 10, 55, 75, 135);

        int relekExpected = 96;      // HP, ATK, DEF, SPD, SATK, SDEF
        int relekCalc = weightedAverage(80, 100, 50, 200, 100, 50);

        int ramExpected = 86;      // HP, ATK, DEF, SPD, SATK, SDEF
        int ramCalc = weightedAverage(97, 165, 60, 58, 65, 50);

        assertEquals(blisseyExpected, blisseyCalc);
        assertEquals(relekExpected, relekCalc);
        assertEquals(ramExpected, ramCalc);
    }
}
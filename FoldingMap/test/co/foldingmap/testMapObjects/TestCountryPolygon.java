/* 
 * Copyright (C) 2014 Alec Dhuse
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package co.foldingmap.testMapObjects;

import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.vector.Polygon;
import org.junit.Ignore;

/**
 * Creates a Polygon for use in test cases.
 * 
 * @author Alec
 */
@Ignore
public class TestCountryPolygon {
    
    public static Polygon getPolygon() {
        CoordinateList<Coordinate> coordinates = getCoordinates();
        
        return new Polygon("Cameroon", "Country - Filled", coordinates);
    }
    
    /**
     * Creates the CoordinateList for the polygon.
     * 
     * @return 
     */
    public static CoordinateList<Coordinate> getCoordinates() {
        CoordinateList<Coordinate> cList = new CoordinateList<Coordinate>();
        
        cList.add(new Coordinate("14.07499,13.08159,0"));
        cList.add(new Coordinate("14.44305,13.08472,0"));
        cList.add(new Coordinate("14.52611,12.96999,0"));
        cList.add(new Coordinate("14.50805,12.8775,0"));
        cList.add(new Coordinate("14.52012,12.86465,0"));
        cList.add(new Coordinate("14.55458,12.82794,0"));
        cList.add(new Coordinate("14.55194,12.76361,0"));
        cList.add(new Coordinate("14.715,12.71028,0"));
        cList.add(new Coordinate("14.70722,12.65388,0"));
        cList.add(new Coordinate("14.74555,12.67333,0"));
        cList.add(new Coordinate("14.87555,12.44111,0"));
        cList.add(new Coordinate("14.90166,12.14277,0"));
        cList.add(new Coordinate("15.04507,12.08408,0"));
        cList.add(new Coordinate("15.05361,12.03305,0"));
        cList.add(new Coordinate("15.05166,11.945,0"));
        cList.add(new Coordinate("15.11111,11.78139,0")); 
        cList.add(new Coordinate("15.06277,11.68167,0"));
        cList.add(new Coordinate("15.14,11.525,0"));
        cList.add(new Coordinate("15.10555,11.48972,0"));
        cList.add(new Coordinate("15.0175,11.18611,0"));
        cList.add(new Coordinate("15.07861,10.88944,0"));
        cList.add(new Coordinate("15.05666,10.80555,0"));
        cList.add(new Coordinate("15.08722,10.74722,0"));
        cList.add(new Coordinate("15.13972,10.51916,0"));
        cList.add(new Coordinate("15.38778,10.22861,0"));
        cList.add(new Coordinate("15.67887,9.992426,0"));
        cList.add(new Coordinate("15.42222,9.926941,0"));
        cList.add(new Coordinate("15.23277,9.987774,0"));
        cList.add(new Coordinate("15.03139,9.946384,0"));
        cList.add(new Coordinate("14.92027,9.971663,0"));
        cList.add(new Coordinate("14.77639,9.921108,0"));
        cList.add(new Coordinate("14.45833,9.998884,0 "));
        cList.add(new Coordinate("14.19055,9.981663,0"));
        cList.add(new Coordinate("14.10861,9.811386,0"));
        cList.add(new Coordinate("14.01,9.729996,0"));
        cList.add(new Coordinate("13.96027,9.634441,0"));
        cList.add(new Coordinate("14.4175,9.134163,0"));
        cList.add(new Coordinate("14.82472,8.811108,0"));
        cList.add(new Coordinate("14.85277,8.817774,0"));
        cList.add(new Coordinate("15.19277,8.497217,0"));
        cList.add(new Coordinate("15.25805,8.351662,0"));
        cList.add(new Coordinate("15.44666,7.877498,0"));
        cList.add(new Coordinate("15.50361,7.774165,0"));
        cList.add(new Coordinate("15.58,7.758332,0"));
        cList.add(new Coordinate("15.58416,7.686665,0"));
        cList.add(new Coordinate("15.4991,7.526431,0"));      
        cList.add(new Coordinate("15.42916,7.428886,0"));
        cList.add(new Coordinate("15.46027,7.395832,0"));
        cList.add(new Coordinate("15.23972,7.246109,0"));
        cList.add(new Coordinate("15.05916,6.778609,0"));
        cList.add(new Coordinate("14.95861,6.733609,0"));
        cList.add(new Coordinate("14.805,6.346665,0"));
        cList.add(new Coordinate("14.74027,6.262499,0"));
        cList.add(new Coordinate("14.48805,6.12972,0"));
        cList.add(new Coordinate("14.41444,6.044165,0"));
        cList.add(new Coordinate("14.485,5.919998,0"));
        cList.add(new Coordinate("14.56333,5.910831,0"));
        cList.add(new Coordinate("14.59277,5.930553,0"));
        cList.add(new Coordinate("14.62166,5.839165,0"));
        cList.add(new Coordinate("14.62444,5.697776,0"));
        cList.add(new Coordinate("14.58916,5.604165,0"));
        cList.add(new Coordinate("14.60111,5.420553,0"));  
        cList.add(new Coordinate("14.53166,5.29361,0"));
        cList.add(new Coordinate("14.68759,5.114281,0"));
        cList.add(new Coordinate("14.70305,4.858609,0"));
        cList.add(new Coordinate("14.73277,4.623054,0"));
        cList.add(new Coordinate("14.89222,4.476665,0"));
        cList.add(new Coordinate("15.04389,4.36222,0"));
        cList.add(new Coordinate("15.10277,4.171943,0"));
        cList.add(new Coordinate("15.12289,4.114404,0"));
        cList.add(new Coordinate("15.19083,4.046943,0"));
        cList.add(new Coordinate("15.04389,4.029165,0"));
        cList.add(new Coordinate("15.07694,3.920555,0"));
        cList.add(new Coordinate("15.26027,3.673888,0"));
        cList.add(new Coordinate("15.86944,3.105555,0"));
        cList.add(new Coordinate("15.93889,3.10611,0"));
        cList.add(new Coordinate("16.11055,2.864166,0"));
        cList.add(new Coordinate("16.11277,2.823333,0"));  
        cList.add(new Coordinate("16.07166,2.796666,0"));
        cList.add(new Coordinate("16.07805,2.70611,0"));
        cList.add(new Coordinate("16.11516,2.705832,0"));
        cList.add(new Coordinate("16.09194,2.656388,0"));
        cList.add(new Coordinate("16.16444,2.300277,0"));
        cList.add(new Coordinate("16.20641,2.221102,0"));
        cList.add(new Coordinate("16.08999,2.166388,0"));
        cList.add(new Coordinate("16.06055,1.973333,0"));
        cList.add(new Coordinate("16.12416,1.873888,0"));
        cList.add(new Coordinate("16.16249,1.726666,0 "));
        cList.add(new Coordinate("16.07222,1.654166,0"));
        cList.add(new Coordinate("16.01777,1.761111,0"));
        cList.add(new Coordinate("15.93277,1.780555,0"));
        cList.add(new Coordinate("15.87194,1.825,0"));
        cList.add(new Coordinate("15.48638,1.976388,0"));
        cList.add(new Coordinate("15.33722,1.920555,0"));  
        cList.add(new Coordinate("15.27277,1.992222,0"));
        cList.add(new Coordinate("15.15139,2.039721,0"));
        cList.add(new Coordinate("15.14139,2.008333,0"));
        cList.add(new Coordinate("15.07222,1.98,0"));
        cList.add(new Coordinate("14.98,2.031943,0"));
        cList.add(new Coordinate("14.95388,2.003611,0"));
        cList.add(new Coordinate("14.88778,2.003888,0"));
        cList.add(new Coordinate("14.91083,2.053332,0"));
        cList.add(new Coordinate("14.86139,2.112499,0"));
        cList.add(new Coordinate("14.80527,2.060833,0"));
        cList.add(new Coordinate("14.56783,2.203994,0"));
        cList.add(new Coordinate("14.52389,2.160554,0"));
        cList.add(new Coordinate("14.37833,2.161388,0"));
        cList.add(new Coordinate("13.73,2.160277,0"));
        cList.add(new Coordinate("13.29422,2.164074,0"));
        cList.add(new Coordinate("13.255,2.266388,0"));       
        cList.add(new Coordinate("13.13194,2.280277,0"));
        cList.add(new Coordinate("13.08555,2.246388,0"));
        cList.add(new Coordinate("13,2.25611,0"));
        cList.add(new Coordinate("12.755,2.232777,0"));
        cList.add(new Coordinate("12.57568,2.274327,0"));
        cList.add(new Coordinate("12.33687,2.317276,0"));
        cList.add(new Coordinate("12.16222,2.280277,0"));
        cList.add(new Coordinate("11.62472,2.313055,0"));
        cList.add(new Coordinate("11.36837,2.303591,0"));
        cList.add(new Coordinate("11.34038,2.168611,0"));
        cList.add(new Coordinate("10.67361,2.167777,0"));
        cList.add(new Coordinate("10.02291,2.167367,0"));  
        cList.add(new Coordinate("9.850828,2.242499,0 "));
        cList.add(new Coordinate("9.808546,2.346065,0"));
        cList.add(new Coordinate("9.844997,2.668055,0"));
        cList.add(new Coordinate("9.966665,3.081666,0"));
        cList.add(new Coordinate("9.909443,3.240833,0"));
        cList.add(new Coordinate("9.795275,3.413888,0"));
        cList.add(new Coordinate("9.642776,3.533888,0"));
        cList.add(new Coordinate("9.810274,3.630555,0 "));
        cList.add(new Coordinate("9.681664,3.594444,0"));
        cList.add(new Coordinate("9.619997,3.603611,0"));
        cList.add(new Coordinate("9.543053,3.814166,0"));
        cList.add(new Coordinate("9.601109,3.774999,0"));  
        cList.add(new Coordinate("9.594721,3.837499,0"));
        cList.add(new Coordinate("9.617775,3.86861,0"));
        cList.add(new Coordinate("9.665276,3.833611,0"));
        cList.add(new Coordinate("9.692776,3.854722,0"));
        cList.add(new Coordinate("9.743608,3.82361,0"));
        cList.add(new Coordinate("9.702219,3.872499,0"));
        cList.add(new Coordinate("9.678608,3.907221,0"));
        cList.add(new Coordinate("9.767498,3.955832,0"));
        cList.add(new Coordinate("9.62583,3.941666,0"));
        cList.add(new Coordinate("9.614719,3.960277,0"));
        cList.add(new Coordinate("9.724163,4.099721,0"));
        cList.add(new Coordinate("9.629999,4.030276,0"));   
        cList.add(new Coordinate("9.557497,4.011665,0"));
        cList.add(new Coordinate("9.474442,4.109999,0"));
        cList.add(new Coordinate("9.48222,4.059165,0"));
        cList.add(new Coordinate("9.528887,3.978055,0"));
        cList.add(new Coordinate("9.492496,3.96611,0"));
        cList.add(new Coordinate("9.476664,4.004443,0"));
        cList.add(new Coordinate("9.421108,4.013887,0"));
        cList.add(new Coordinate("9.463886,3.905833,0"));
        cList.add(new Coordinate("9.351385,3.90861,0"));
        cList.add(new Coordinate("9.309998,3.942222,0"));  
        cList.add(new Coordinate("9.296663,3.982499,0"));
        cList.add(new Coordinate("9.318052,4.019165,0"));
        cList.add(new Coordinate("9.274998,3.978333,0"));
        cList.add(new Coordinate("9.232496,3.960555,0"));
        cList.add(new Coordinate("9.203331,4.013332,0"));
        cList.add(new Coordinate("9.116941,4.01222,0"));
        cList.add(new Coordinate("8.968052,4.108888,0"));
        cList.add(new Coordinate("8.986805,4.214077,0"));
        cList.add(new Coordinate("8.904997,4.381665,0"));
        cList.add(new Coordinate("8.933609,4.546943,0"));
        cList.add(new Coordinate("8.841665,4.640832,0"));
        cList.add(new Coordinate("8.866386,4.589443,0"));
        cList.add(new Coordinate("8.865833,4.537498,0"));
        cList.add(new Coordinate("8.816385,4.58361,0"));
        cList.add(new Coordinate("8.808052,4.555276,0"));
        cList.add(new Coordinate("8.784441,4.540554,0"));
        cList.add(new Coordinate("8.721386,4.579999,0"));
        cList.add(new Coordinate("8.685553,4.666388,0"));
        cList.add(new Coordinate("8.642498,4.686666,0"));
        cList.add(new Coordinate("8.724722,4.519443,0"));
        cList.add(new Coordinate("8.701942,4.494443,0"));
        cList.add(new Coordinate("8.516943,4.513332,0"));  
        cList.add(new Coordinate("8.502497,4.563888,0 "));
        cList.add(new Coordinate("8.533331,4.572498,0"));
        cList.add(new Coordinate("8.510555,4.62861,0"));
        cList.add(new Coordinate("8.590485,4.810515,0"));
        cList.add(new Coordinate("8.63611,4.826943,0"));
        cList.add(new Coordinate("8.621941,4.894721,0"));
        cList.add(new Coordinate("8.824007,5.188001,0"));
        cList.add(new Coordinate("8.828053,5.234164,0"));
        cList.add(new Coordinate("8.920277,5.607498,0"));
        cList.add(new Coordinate("8.833609,5.71361,0"));
        cList.add(new Coordinate("8.888885,5.786109,0"));
        cList.add(new Coordinate("8.860666,5.819821,0"));
        cList.add(new Coordinate("8.879997,5.85472,0"));
        cList.add(new Coordinate("9.006386,5.909998,0"));
        cList.add(new Coordinate("9.00333,5.94472,0"));
        cList.add(new Coordinate("9.294996,6.207776,0"));
        cList.add(new Coordinate("9.363331,6.325831,0"));
        cList.add(new Coordinate("9.434996,6.327776,0"));
        cList.add(new Coordinate("9.468607,6.404443,0"));
        cList.add(new Coordinate("9.576662,6.446942,0"));
        cList.add(new Coordinate("9.612219,6.521665,0"));
        cList.add(new Coordinate("9.711388,6.522776,0"));  
        cList.add(new Coordinate("9.795551,6.801664,0"));
        cList.add(new Coordinate("9.874718,6.775831,0"));
        cList.add(new Coordinate("10.16722,7.019165,0"));
        cList.add(new Coordinate("10.205,6.900554,0"));
        cList.add(new Coordinate("10.25639,6.875831,0"));
        cList.add(new Coordinate("10.51333,6.878054,0"));
        cList.add(new Coordinate("10.59611,7.134165,0"));
        cList.add(new Coordinate("10.62,7.053609,0"));
        cList.add(new Coordinate("10.85416,6.946942,0"));
        cList.add(new Coordinate("10.88277,6.827776,0"));
        cList.add(new Coordinate("10.94333,6.778054,0"));
        cList.add(new Coordinate("11.01916,6.778609,0"));   
        cList.add(new Coordinate("11.08222,6.697776,0"));
        cList.add(new Coordinate("11.07333,6.591109,0"));
        cList.add(new Coordinate("11.11694,6.444164,0"));
        cList.add(new Coordinate("11.15472,6.433331,0"));
        cList.add(new Coordinate("11.38889,6.460277,0"));
        cList.add(new Coordinate("11.445,6.596943,0"));
        cList.add(new Coordinate("11.51472,6.604165,0"));
        cList.add(new Coordinate("11.5625,6.667498,0"));
        cList.add(new Coordinate("11.58777,6.785275,0"));
        cList.add(new Coordinate("11.55472,6.82111,0"));
        cList.add(new Coordinate("11.5875,6.89222,0"));  
        cList.add(new Coordinate("11.79139,7.050831,0"));
        cList.add(new Coordinate("11.88666,7.078054,0"));
        cList.add(new Coordinate("11.89416,7.123054,0"));
        cList.add(new Coordinate("11.74972,7.270554,0"));
        cList.add(new Coordinate("11.86,7.402221,0"));
        cList.add(new Coordinate("12.04361,7.577776,0"));
        cList.add(new Coordinate("12.04361,7.73972,0"));
        cList.add(new Coordinate("12.22277,7.97472,0"));
        cList.add(new Coordinate("12.24722,8.393887,0"));
        cList.add(new Coordinate("12.27527,8.428329,0"));
        cList.add(new Coordinate("12.35333,8.430275,0"));
        cList.add(new Coordinate("12.42166,8.518608,0")); 
        cList.add(new Coordinate("12.38639,8.61305,0"));
        cList.add(new Coordinate("12.49249,8.628883,0"));
        cList.add(new Coordinate("12.56889,8.600552,0"));
        cList.add(new Coordinate("12.6875,8.66083,0"));
        cList.add(new Coordinate("12.72833,8.759441,0"));
        cList.add(new Coordinate("12.79416,8.765831,0"));
        cList.add(new Coordinate("12.82416,8.847496,0"));
        cList.add(new Coordinate("12.84138,9.085552,0"));
        cList.add(new Coordinate("12.90889,9.235273,0"));
        cList.add(new Coordinate("12.91361,9.343607,0"));
        cList.add(new Coordinate("12.84893,9.360079,0"));  
        cList.add(new Coordinate("13.05278,9.508331,0"));
        cList.add(new Coordinate("13.15639,9.515831,0"));
        cList.add(new Coordinate("13.22111,9.555275,0"));
        cList.add(new Coordinate("13.25139,9.676941,0"));
        cList.add(new Coordinate("13.22833,9.909719,0"));
        cList.add(new Coordinate("13.26555,9.984995,0"));
        cList.add(new Coordinate("13.24389,10.03166,0"));
        cList.add(new Coordinate("13.26639,10.08611,0"));
        cList.add(new Coordinate("13.39805,10.11111,0"));
        cList.add(new Coordinate("13.47055,10.19166,0"));
        cList.add(new Coordinate("13.45861,10.23888,0"));
        cList.add(new Coordinate("13.57833,10.6825,0"));         
        cList.add(new Coordinate("13.6375,10.75583,0"));
        cList.add(new Coordinate("13.88666,11.17055,0"));
        cList.add(new Coordinate("14.00916,11.28333,0"));
        cList.add(new Coordinate("14.15805,11.23361,0"));
        cList.add(new Coordinate("14.19583,11.25083,0"));
        cList.add(new Coordinate("14.61777,11.50555,0"));  
        cList.add(new Coordinate("14.64639,11.57583,0"));
        cList.add(new Coordinate("14.64111,11.64694,0"));
        cList.add(new Coordinate("14.55833,11.71527,0"));
        cList.add(new Coordinate("14.64944,11.915,0"));
        cList.add(new Coordinate("14.61916,12.03555,0"));
        cList.add(new Coordinate("14.67416,12.15083,0"));
        cList.add(new Coordinate("14.65722,12.18722,0"));
        cList.add(new Coordinate("14.55277,12.23222,0"));
        cList.add(new Coordinate("14.49083,12.33583,0"));
        cList.add(new Coordinate("14.17389,12.38416,0"));
        cList.add(new Coordinate("14.18805,12.44389,0"));
        cList.add(new Coordinate("14.18351,12.46948,0"));
        
        return cList;
    }
}

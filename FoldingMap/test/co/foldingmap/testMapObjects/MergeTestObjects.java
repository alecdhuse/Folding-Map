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

import co.foldingmap.map.vector.VectorObject;
import co.foldingmap.map.vector.VectorLayer;
import co.foldingmap.map.vector.Coordinate;
import co.foldingmap.map.vector.LineString;
import co.foldingmap.map.vector.CoordinateList;
import co.foldingmap.map.DigitalMap;
import org.junit.Ignore;

/**
 *
 * @author Alec
 */
@Ignore
public class MergeTestObjects {
    
    public static DigitalMap getMap() {
        DigitalMap  mapData = new DigitalMap();
        VectorLayer layer   = getLayer();
        
        mapData.addLayer(layer);
        mapData.setSelected(layer.getObjectList());
        
        return mapData;
    }
    
    public static VectorLayer getLayer() {
        VectorLayer l = new VectorLayer("Layer");
        
        l.addObject(getLine1());
        l.addObject(getLine2());

        return l;
    }
    
    public static VectorObject getLine1() {
        VectorObject object;
        CoordinateList<Coordinate> cList = getLine1Coordinates();
        
        object = new LineString("Untitled Path", "Untitled Path", cList);
        
        return object;
    }
    
    public static VectorObject getLine2() {
        VectorObject object;
        CoordinateList<Coordinate> cList = getLine2Coordinates();
        
        object = new LineString("Untitled Path", "Untitled Path", cList);
        
        return object;
    }    
    
    public static CoordinateList<Coordinate> getLine1Coordinates() {
        CoordinateList<Coordinate> cList = new CoordinateList<Coordinate>();
        
        cList.add(new Coordinate("-121.14198,44.372704,0.0"));
        cList.add(new Coordinate("-121.14217,44.372696,0.0"));
        cList.add(new Coordinate("-121.14237,44.37278,0.0"));
        cList.add(new Coordinate("-121.142555,44.372883,0.0"));
        cList.add(new Coordinate("-121.14273,44.372967,0.0"));
        cList.add(new Coordinate("-121.142815,44.373028,0.0"));
        cList.add(new Coordinate("-121.14283,44.37313,0.0"));
        cList.add(new Coordinate("-121.14294,44.373188,0.0"));
        cList.add(new Coordinate("-121.14319,44.37326,0.0"));
        cList.add(new Coordinate("-121.14346,44.373207,0.0"));
        
        return cList;
    }
    
    public static CoordinateList<Coordinate> getLine2Coordinates() {
        CoordinateList<Coordinate> cList = new CoordinateList<Coordinate>();
        
        cList.add(new Coordinate("-121.14138,44.37285,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1411,44.372932,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14095,44.372982,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.140854,44.373043,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14073,44.37305,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14069,44.373013,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1406,44.372955,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.140564,44.37285,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14055,44.37278,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.140495,44.372696,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14051,44.372646,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14057,44.37253,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1406,44.372482,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.140724,44.372448,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14083,44.37246,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.140945,44.37242,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14104,44.372437,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14118,44.372505,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14124,44.372547,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14128,44.372448,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.141365,44.37239,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14139,44.372364,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14144,44.372303,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1415,44.372227,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14156,44.372143,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14166,44.372025,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14176,44.37191,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1418,44.371845,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14183,44.371777,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1418,44.3717,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.141754,44.371655,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14169,44.37162,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14162,44.371593,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.141556,44.37159,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.141495,44.37156,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14149,44.37149,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14148,44.37145,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14148,44.371414,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14162,44.371284,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14174,44.37124,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14188,44.37119,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14203,44.37108,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14207,44.37107,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1421,44.37113,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1422,44.37112,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142265,44.37113,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14219,44.371197,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142105,44.371223,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142075,44.37128,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14194,44.371323,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14181,44.371365,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14177,44.371387,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.141815,44.37148,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14185,44.371563,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.141884,44.371593,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14195,44.371647,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142075,44.371796,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142166,44.371902,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14236,44.37184,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14247,44.371723,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14262,44.371727,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14267,44.3717,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14271,44.37168,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14286,44.37156,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142944,44.37147,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.143005,44.371414,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14309,44.371353,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14316,44.371326,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14325,44.371296,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1433,44.371254,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14336,44.371246,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14344,44.37129,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14349,44.371296,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14361,44.37131,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14362,44.37135,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14357,44.371445,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.143486,44.371548,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14346,44.371597,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14345,44.37163,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14338,44.371784,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14334,44.37182,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.143265,44.371883,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14313,44.37196,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14304,44.37204,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14298,44.3721,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142914,44.372185,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14288,44.37226,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142845,44.372326,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14269,44.37243,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14264,44.372482,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14266,44.37249,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142685,44.37249,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14275,44.37251,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142784,44.372517,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14277,44.37257,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14275,44.3726,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14279,44.372593,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14292,44.37261,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.142975,44.372593,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.143036,44.372585,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.143234,44.372536,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14339,44.372513,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14358,44.37251,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14365,44.37251,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14372,44.37252,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14366,44.372562,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.143654,44.37261,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14379,44.372803,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14366,44.372833,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14353,44.372864,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14343,44.37283,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14324,44.372757,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14318,44.372856,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14316,44.37292,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14329,44.373055,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14325,44.373096,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.1433,44.37313,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14336,44.373165,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14344,44.373196,0,2013-07-09T18:56:51Z"));
        cList.add(new Coordinate("-121.14346,44.373207,0,2013-07-09T18:56:51Z"));
        
        return cList;
    }    
}

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
package co.foldingmap.dataStructures;

import co.foldingmap.map.themes.OutlineStyle;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class ListOperations {
    
    
    public static ArrayList getCommonObjects(ArrayList list1, ArrayList list2) {
        
        ArrayList returnList = new ArrayList();
        Object    object1, object2;
        
        for (int i = 0; i < list1.size(); i++) {
            object1 = list1.get(i);
            
            for (int j = 0; j < list2.size(); j++) {
                object2 = list2.get(j);
                
                if (object1 == object2 && !returnList.contains(object1)) {
                    returnList.add(list1.get(i));
                    break;
                }
            }
        }
        
        return returnList;
    }
    
    /**
     * Finds the first OutlineStyle with the matching condition.
     * If no OutlineStyle is found then null is returned.
     * 
     * @param list
     * @param condition
     * @return 
     */
    public static OutlineStyle getOutlineStyleFromCondition(ArrayList<OutlineStyle> list, 
                                                            String                  condition) {
        OutlineStyle returnStyle = null;
        
        for (OutlineStyle currentStyle: list) {
            if (currentStyle.getBorderCondition().equals(condition)) {
                returnStyle = currentStyle;
                break;
            }
        }
        
        return returnStyle;
    }
    
    /**
     * Compares two lists to see if the contain the same objects regardless of 
     * order.
     * 
     * @param list1
     * @param list2
     * @return 
     */
    public static boolean listsContainSameObjects(ArrayList list1, ArrayList list2) {
        boolean result = false;
        
        if (list1.size() == list2.size()) {
            for (Object obj: list1) {
                if (list2.contains(obj)) {
                    result = true;
                } else {
                    result = false;
                    break;
                }
            }
        } else {
            //List don't conain the same number of elements, return false.
            result = false;
        }
        
        return result;
    }
}

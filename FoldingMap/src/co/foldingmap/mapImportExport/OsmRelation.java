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
package co.foldingmap.mapImportExport;

import co.foldingmap.dataStructures.PropertyValuePair;
import java.util.ArrayList;

/**
 *
 * @author Alec
 */
public class OsmRelation {
    private ArrayList<OsmMember>            members;
    private ArrayList<PropertyValuePair>    properties;
    private boolean                         visible;
    private long                            id;
    private String                          type;
    
    public OsmRelation(long id) {
        this.members    = new ArrayList<OsmMember>();
        this.properties = new ArrayList<PropertyValuePair>();
        this.visible    = true;
        this.id         = (id);
        this.type       = "";
    }
    
    public void addMember(OsmMember member) {
        this.members.add(member);
    }
    
    public void addProperty(PropertyValuePair property) {
        this.properties.add(property);
        
        if (property.getProperty().equalsIgnoreCase("type")) {
            type = property.getValue();
        }
    }
    
    /**
     * Returns the number of member tags that contain an inner role.
     * 
     * @return 
     */
    public int countInnerRoles() {
        int count = 0;

        for (OsmMember member: members) {
            if (member.role.equalsIgnoreCase("inner"))
                count++;
        }
        
        return count;
    }    
    
    /**
     * Returns the number of member tags that contain an outer role.
     * 
     * @return 
     */
    public int countOuterRoles() {
        int count = 0;

        try {
            for (OsmMember member: members) {
                if (member.role.equalsIgnoreCase("outer"))
                    count++;
            }
        } catch (Exception e) {
            System.err.println("Error in OsmRelation.countOuterRoles() - " + e);
        }
        
        return count;
    }
    
    public long[] getInnerRoles() {        
        int     index        = 0;
        long[]  innerRoleIDs = new long[countInnerRoles()];
             
        for (OsmMember member: members) {
            if (member.role.equalsIgnoreCase("inner")) {
                innerRoleIDs[index] = member.refID;
                index++;
            }                
        }        
        
        return innerRoleIDs;
    }
    
    /**
     * Returns the members of this relation.
     * 
     * @return 
     */
    public ArrayList<OsmMember> getMembers() {
        return this.members;
    }
    
    public long[] getOuterRoles() {
        int     index        = 0;
        long[]  outerRoleIDs = new long[countOuterRoles()];
        
        for (OsmMember member: members) {
            if (member.role.equalsIgnoreCase("outer")) {
                outerRoleIDs[index] = member.refID;
                index++;
            }                
        }        
        
        return outerRoleIDs;
    }
    
    /**
     * Returns all PropertyValuePairs for this Relation.
     * 
     * @return 
     */
    public ArrayList<PropertyValuePair> getProperties() {
        return this.properties;
    }
                
    /**
     * Returns the value for a given key or property name.
     * Returns n empty string if the property name does not exist.
     * 
     * @param property
     * @return 
     */
    public String getPropertyValue(String property) {
        String value = "";
        
        for (PropertyValuePair pvp: properties) {
            if (pvp.getProperty().equalsIgnoreCase(property)) {
                value = pvp.getValue();
                break;
            }
        }
        
        return value;
    }
    
    /**
     * Return the Relation type, will return an empty string in the is no type
     * property.
     * 
     * @return 
     */
    public String getType() {
        return type;
    }
    
    /**
     * Parses the XML for an OSM relation and returns an OsmRelation object.
     * 
     * @param xml
     * @return 
     */
    public static OsmRelation parseRelation(String xml) {
        int         start, end, offset;
        OsmRelation relation;
        
        try {
            //get relation id
            start    = xml.indexOf("id=") + 4;
            end      = xml.indexOf("\"", start);                
            relation = new OsmRelation(Long.parseLong(xml.substring(start, end)));

            //get members
            while (start >= 0) {
                start = xml.indexOf("<member", end) ;
                
                if (start >= 0) {
                    end = xml.indexOf("/>", start) + 2;                   
                    relation.addMember(new OsmMember(xml.substring(start, end)));
                }
            }

            //get tags
            start = 0;
            end   = 0;
            while (start >= 0) {
                start = xml.indexOf("<tag", end) ;
                
                if (start >= 0) {
                    end = xml.indexOf("/>", start) + 2;
                    relation.addProperty(OsmImporter.getOsmTag(xml.substring(start, end))); 
                }
            }        

            return relation;
        } catch (Exception e) {
            System.err.println("Error in OsmRelaiton.parseRelation(String) - " + e);
            return null;
        }
    }
            
}

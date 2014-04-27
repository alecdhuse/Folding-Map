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
import co.foldingmap.map.vector.Coordinate;
import java.util.ArrayList;

/**
 * This class is used with the main OsmImported class to import
 * OpenStreetMap.org XML files.  This class corresponds to the OSM node tag and
 * is used for convenience.
 * 
 * Stores information about an Open Street Map (osm) node.
 * This information consists of the node's ID, It's Lat/Lon/Alt Coordinate and
 * any tags associated with it.
 *
 * @author alecdhuse
 */
public class OsmNode {
    protected ArrayList<PropertyValuePair> nodeTags;
    protected Coordinate                   nodeCoordinate;
    protected String                       changeSet, nodeID;

    /**
     * Creates a node with a given name and Coordinate.
     * 
     * @param nodeID
     * @param nodeCoordinate 
     */
    public OsmNode(String nodeID, Coordinate nodeCoordinate) {
        this.nodeCoordinate = nodeCoordinate;
        this.nodeID         = nodeID;
        this.nodeTags       = new ArrayList<PropertyValuePair>();
    }

    /**
     * Creates a node with tags.
     * 
     * @param nodeID
     * @param nodeCoordinate
     * @param nodeTags 
     */
    public OsmNode(String nodeID, Coordinate nodeCoordinate, ArrayList<PropertyValuePair> nodeTags) {
        this.nodeTags = nodeTags;
        this.nodeCoordinate = nodeCoordinate;
        this.nodeID = nodeID;
    }

    /**
     * Adds a tag to this Node.
     * 
     * @param key
     * @param value 
     */
    public void addTag(String key, String value) {
        nodeTags.add(new PropertyValuePair(key, value));
    }

    /**
     * Returns the ChangeSet of this Node.
     * 
     * @return 
     */
    public String getChangeSet() {
        return this.changeSet;
    }

    /**
     * Returns the Coordinate of this node.
     * 
     * @return 
     */
    public Coordinate getNodeCoordinate() {
        return nodeCoordinate;
    }

    /**
     * Returns the ID of this node.
     * 
     * @return 
     */
    public String getNodeID() {
        return nodeID;
    }

    /**
     * Returns all the tags for this Node.
     * 
     * @return 
     */
    public ArrayList<PropertyValuePair> getNodeTags() {
        return nodeTags;
    }

    /**
     * Returns if this Node contains a tag with a Name property.
     * 
     * @return 
     */
    public boolean hasNameTag() {
        boolean nameTag = false;

        for (PropertyValuePair currentPair: nodeTags) {
            if (currentPair.getProperty().equalsIgnoreCase("Name")) {
                nameTag = true;
                break;
            }
        }

        return nameTag;
    }

    /**
     * Sets the Change Set for this node.  
     * See link for more details: http://wiki.openstreetmap.org/wiki/Changeset
     *
     * @param changeSet 
     */
    public void setChangeSet(String changeSet) {
        if (!changeSet.equals("")) {
            this.changeSet = changeSet;
        }
    }

    /**
     * Sets the Coordinate used by this node.
     * 
     * @param nodeCoordinate 
     */
    public void setNodeCoordinate(Coordinate nodeCoordinate) {
        this.nodeCoordinate = nodeCoordinate;
    }

    /**
     * Sets the ID for this node.  The ID is how Map Objects reference the 
     * coordinates they use.
     * 
     * @param nodeID 
     */
    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    /**
     * Sets the tags for this Node.
     * 
     * @param nodeTags 
     */
    public void setNodeTags(ArrayList<PropertyValuePair> nodeTags) {
        this.nodeTags = nodeTags;
    }

    /**
     * Returns XML of the form:
     * <node id='-1' timestamp='2010-12-02T13:21:04Z' visible='true' lat='5.621446346905824' lon='10.111021898545193' />
     *
     * @return
     */
    public String toXML() {
        StringBuilder xml = new StringBuilder();

        try {
            if (changeSet != null) {
                xml.append("\t<node changeset='");
                xml.append(changeSet);
                xml.append("' ");
            } else {
                xml.append("\t<node id='");
                xml.append(nodeID);
                xml.append("' ");
            }

            xml.append("timestamp='");
            xml.append(nodeCoordinate.getTimestamp());
            xml.append("' visible='true' lat='");
            xml.append(nodeCoordinate.getLatitude());
            xml.append("' lon='");
            xml.append(nodeCoordinate.getLongitude());
            xml.append("' ele='");
            xml.append(nodeCoordinate.getAltitude());
            xml.append("'>\n");

            if (nodeTags != null) {
                for (PropertyValuePair currentPVP: nodeTags) {
                    xml.append("\t\t<tag k='");
                    xml.append(currentPVP.getProperty());
                    xml.append("' v='");
                    xml.append(currentPVP.getValue());
                    xml.append("'/>");
                }
            } //end null check

            xml.append("\t</node>");
            
        } catch (Exception e) {
            System.out.println("Error in OsmNode.toXML() - " + e);
        }
        
        return xml.toString();
    }
    
}

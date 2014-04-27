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

/**
 *
 * @author Alec
 */
public class OsmMember {
    protected   long    refID;
    protected   String  role, type;
    
    public OsmMember(String xml) {
        int     start, end;
        
        //get type
        start = xml.indexOf("type=") + 6;
        end   = xml.indexOf("\"", start);
        type  = xml.substring(start, end);
        
        //get ref
        start = xml.indexOf("ref=", end) + 5;
        end   = xml.indexOf("\"", start);
        refID = Long.parseLong(xml.substring(start, end));        
        
        //get role
        start = xml.indexOf("role=", end) + 6;
        end   = xml.indexOf("\"", start);
        role  = xml.substring(start, end);           
    }
    
    public OsmMember(String type, String ref, String role) {
        try {
            this.type   = type;
            this.role   = role;
            this.refID  = Long.parseLong(ref);
        } catch (Exception e) {
            refID = 0;
            System.err.println("Error in OsmMember.constructor - " + e);
        }               
    }
}

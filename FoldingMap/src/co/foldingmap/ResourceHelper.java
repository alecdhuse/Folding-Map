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
package co.foldingmap;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Helper class for getting jar resources.
 * 
 * @author Alec
 */
public class ResourceHelper {
    private final ArrayList<String> filePaths;
    
    private static ResourceHelper    resourceHelper;
    private static String            webUserAgent;
    
    private ResourceHelper() {
        filePaths     = new ArrayList<String>();
        webUserAgent = "GoogleEarth/6.1.0.5001(Macintosh;Mac OS X (10.7.4);en;kml:2.2;client:Free;type:default)";
    }
    
    /**
     * Adds file paths to search for resource files.
     * 
     * @param newFilePath 
     */
    public static void addFilePath(String newFilePath) {   
        boolean pathFound = false;
        
        if (newFilePath.endsWith(File.separator)) {
            for (String path: ResourceHelper.getInstance().filePaths) {
                if (path.equals(newFilePath)) {
                    pathFound = true;
                    break;
                }
            }
            
            //If the path does not exist add it to the paths list.
            if (!pathFound)
                ResourceHelper.getInstance().filePaths.add(newFilePath);
        } else {
            for (String path: ResourceHelper.getInstance().filePaths) {
                if (path.equals(newFilePath + File.separator)) {
                    pathFound = true;
                    break;
                }
            }
            
            //If the path does not exist add it to the paths list.
            if (!pathFound)            
                ResourceHelper.getInstance().filePaths.add(newFilePath + File.separator);
        }
    }
    
    /**
     * Removes all stored resource paths in the ResourceHelper.
     */
    public static void clearResourcePaths() {        
        ResourceHelper.getInstance().filePaths.clear();
    }
    
    /**
     * Downloads a file from an external resource over HTTP.
     * 
     * @param fileAddress
     * @return 
     */
    public static File downloadFile(String fileAddress) {
        boolean         binaryFile;
        BufferedReader  br;
        BufferedWriter  bw;        
        File            file;
        String          currentLine, fileExtension, secondLine;
        URL             urlAddress;
        URLConnection   urlConnection;        
        
        file = null;
        
        try {
            binaryFile    = false;
            
            try {
                //Assume HTTP protocol            
                urlAddress    = new URL(fileAddress);
                urlConnection = urlAddress.openConnection();

                //set user agent
                System.setProperty("http.agent", ""); 
                urlConnection.setRequestProperty("User-Agent", webUserAgent);   

                br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            } catch (Exception e) {
                //HTTP failed try local file
                urlAddress = null;
                File sourceFile = new File(fileAddress);
                int  filePathIndex = 0;
                
                //If the file does not exist try to find it in the filePaths
                while (sourceFile.exists() == false) {
                    if (filePathIndex < ResourceHelper.getInstance().filePaths.size()) {
                        sourceFile = new File(ResourceHelper.getInstance().filePaths.get(filePathIndex) + fileAddress);
                        filePathIndex++;
                    } else {
                        sourceFile = null;
                        break;
                    }
                }
                
                if (sourceFile != null) {
                    br = new BufferedReader(new FileReader(sourceFile));
                } else {
                    Logger.log(Logger.ERR, "Could not find file: " + fileAddress);
                    return null;
                }
                
                //TODO: Make this work for Binary files.
            }
            
            //read first line to try and discover the file type
            currentLine = br.readLine();
            
            if (currentLine != null) {           
                if (currentLine.startsWith("GIF")) {
                    fileExtension = "gif";
                    binaryFile    = true;
                } else if (currentLine.startsWith("PK")) {
                    fileExtension = "kmz";  
                    binaryFile    = true;
                } else if (currentLine.startsWith("���� JFIF")) {
                    fileExtension = "jpg";  
                    binaryFile    = true;
                } else if (currentLine.startsWith("�PNG")) {
                    fileExtension = "png";
                    binaryFile    = true;
                } else if (currentLine.startsWith("<kml")) {
                    fileExtension = "kml";
                } else if (currentLine.startsWith("<?xml")) {
                    secondLine    = br.readLine();
                    
                    if (secondLine.startsWith("<rss")) {
                        fileExtension = "rss";
                    } else if (secondLine.startsWith("<feed")) {
                        fileExtension = "rss";
                    } else if (secondLine.startsWith("<fmxml")) {
                        fileExtension = "fmxml";
                    } else {
                        fileExtension = "kml";
                    }
                    
                    currentLine += ("\n" + secondLine);
                } else if (currentLine.startsWith("{")) {
                    fileExtension = "geojson";
                } else if (currentLine.startsWith("var")) {
                    fileExtension = "js";                    
                } else {
                    fileExtension = "tmp";
                }
                
                file = File.createTempFile("tempFile", fileExtension);
                file.deleteOnExit();
                
                if (binaryFile == false) {
                    bw   = new BufferedWriter(new FileWriter(file));
                    bw.write(currentLine + "\n");

                    while ((currentLine = br.readLine()) != null) {
                        bw.write(currentLine + "\n");      
                    }       

                    br.close();
                    bw.close();    
                } else {
                    //Binary File
                    br.close();

                    urlConnection  = urlAddress.openConnection();                    
                    InputStream is = urlConnection.getInputStream();                                        
                    byte[] buffer  = new byte[urlConnection.getContentLength()];
                    
                    OutputStream output     = new FileOutputStream( file );
                    BufferedInputStream bis = new BufferedInputStream(is);
                    int n;
                                        
                    while ((n = bis.read(buffer)) != -1) {
                        if (n > 0) output.write(buffer, 0, n);                    
                    }
                    
                    output.close();
                }
            }            
            
        } catch (Exception e) {
            System.err.println("Error in ResourceHelper.downloadFile(String) - " + e);
        }
        
        return file;
    }    
    
    /**
     * Downloads a text file over HTTP and returns its contents as a String.
     * 
     * @param fileAddress
     * @return 
     */
    public static String downloadString(String fileAddress) {
        BufferedReader  br;       
        StringBuilder   content;
        URL             urlAddress;
        URLConnection   urlConnection;        
        
        content = new StringBuilder();
        
        try {
            urlAddress    = new URL(fileAddress);
            urlConnection = urlAddress.openConnection();
            
//            //set user agent
//            System.setProperty("http.agent", ""); 
//            urlConnection.setRequestProperty("User-Agent", webUserAgent);   
                        
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            
            while (br.ready()) 
                content.append(br.readLine());            
            
            br.close();                            
        } catch (Exception e) {
            System.err.println("Error in ResourceHelper.downloadString(String) - " + e);
        }
        
        return content.toString();
    }      
    
    /**
     * Returns a buffered image version of a given image file;
     * 
     * @param fileName
     * @return 
     */
    public BufferedImage getBufferedImage(String fileName) {
        BufferedImage   bi = null;
        String          filePath, prefix;
        URL             url;
        
        try {
            if (fileName.length() > 7) {
                prefix = fileName.substring(0, 7);
            } else {
                prefix = "";
            }
            
            if (prefix.equalsIgnoreCase("http://")) {
                url = new URL(fileName);
                bi  = ImageIO.read(url);
            } else if (prefix.toLowerCase().startsWith("file:/")) {
                bi = ImageIO.read(new File(fileName.substring(5)));   
            } else if (fileName.contains(File.separator)) {
                bi = ImageIO.read(new File(fileName));               
            } else {
                filePath  = "resources/"  + fileName;
                url       = getClass().getResource(filePath);    
                
                if (url == null) 
                    url = getURL(fileName);
                
                if (url == null) {
                    //If no file is found check alternative resource paths
                    for (String path: ResourceHelper.getInstance().filePaths) {
                        filePath = path + fileName;
                        url = getURL(filePath);
                        
                        if (url != null) {
                            bi = ImageIO.read(url);
                            break;
                        }
                    }
                } else {                
                    bi = ImageIO.read(url);
                }                
            }
            
            if (bi != null) {
                return bi;
            } else {
                throw new FileNotFoundException(fileName);
            }
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ResourceHelper.getBufferedImage(String) - " + e);
            return new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR) ;
        }
    }

    /**
     * Returns the instance of ResourceHelper.
     * 
     * @return 
     */
    public static ResourceHelper getInstance() {
        if (resourceHelper == null)
            resourceHelper = new ResourceHelper();
        
        return resourceHelper;
    }
    
    /**
     * Opens a text file and returns the contense as a string.
     * 
     * @param file
     * @return The file's text.
     */
    public static String getTextFromFile(File file) {
        StringBuffer sb = new StringBuffer();
        
        try {
            FileReader     fileReader = new FileReader(file);
            BufferedReader bf         = new BufferedReader(fileReader);            
        
            while (bf.ready()) 
                sb.append(bf.readLine() + "\n");   
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ResourceHelper.getTextFromFile(File) - " + e);
        }
        
        return sb.toString();
    }
    
    /**
     * Returns the URL for a given FileLocation
     * 
     * @param fileLocation
     * @return 
     */
    public URL getURL(String fileLocation) {
        String    filePath, prefix;
        URL       url;
        
        url = null;
        
        try {                        
            if (fileLocation.length() > 7) {
                prefix = fileLocation.substring(0, 7);
            } else {
                prefix = "";
            }
            
            if (prefix.equalsIgnoreCase("http://")) {
                url   = new URL(fileLocation);
            } else if (fileLocation.contains(File.separator)) {
                url   = new URL(fileLocation);
            } else {
                filePath = "resources" + File.separator + fileLocation;
                url      = getClass().getResource(filePath);                                
            }            
            
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ResourceHelper.getURL(String \"" + fileLocation + "\") - " + e);
        }   
        
        return url;
    }
    
    /**
     * Gets an image from the jar's main resource folder.
     * 
     * @param fileName
     * @return 
     */
    public ImageIcon getImage(String fileName) {
        ImageIcon image;
        String    filePath, prefix;
        URL       url;
        
        try {
            image = new ImageIcon();
            
            if (fileName.length() > 7) {
                prefix = fileName.substring(0, 7);
            } else {
                prefix = "";
            }
            
            if (prefix.equalsIgnoreCase("http://")) {
                url   = new URL(fileName);
                image = new ImageIcon(url);
            } else if (fileName.contains(File.separator)) {
                image = new ImageIcon(fileName);
            } else {
                filePath  = "resources/"  + fileName;
                url       = getClass().getResource(filePath);    
                
                if (url == null) 
                    url = getURL(fileName);
                
                if (url == null) {
                    //If no file is found check alternative resource paths
                    for (String path: ResourceHelper.getInstance().filePaths) {
                        filePath = path + fileName;
                        url = getURL(filePath);
                        
                        if (url != null) {
                            image = new ImageIcon(url);
                            break;
                        }
                    }
                } else {                
                    image = new ImageIcon(url);
                }
            }
            
            return image;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "Error in ResourceHelper.getImage(String \"" + fileName + "\") - " + e);
            return new ImageIcon();
        }
    }    
    
    /**
     * Returns a File with the given name from the jar's resource folder.
     * Returns null if the file does not exist.
     * 
     * @param fileName
     * @return 
     */
    public File getFile(String fileName) {        
        try {            
            String filePath   = "resources" + File.separator + fileName;
            URL    url        = getClass().getResource(filePath);
            File   returnFile = new File(url.toString());

            return returnFile;
        } catch (Exception e) {
            Logger.log(Logger.ERR, "File: " + fileName + " Not Found.");
            return null;
        }        
    }   

}

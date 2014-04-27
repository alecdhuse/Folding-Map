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

import co.foldingmap.UserConfig;
import static org.junit.Assert.assertEquals;
import org.junit.*;

/**
 *
 * @author Alec
 */
public class UserConfigTest {
    
    public UserConfigTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getImportDIR method, of class UserConfig.
     */
    @Test
    public void testGetImportDIR() {
        UserConfig  instance  = new UserConfig();        
        String      expResult = System.getProperty("user.home");
        String      result    = instance.getImportDIR();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of getWorkingDIR method, of class UserConfig.
     */
    @Test
    public void testGetWorkingDIR() {
        UserConfig instance  = new UserConfig();
        String     expResult = System.getProperty("user.home");
        String     result    = instance.getWorkingDIR();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of setImportDIR method, of class UserConfig.
     */
    @Test
    public void testSetImportDIR() {
        String     expResult = System.getProperty("user.home");
        String     importDIR = System.getProperty("user.home");
        UserConfig instance  = new UserConfig();
        
        instance.setImportDIR(importDIR);
        assertEquals(expResult, instance.getImportDIR());
    }

    /**
     * Test of setWorkingDIR method, of class UserConfig.
     */
    @Test
    public void testSetWorkingDIR() {
        String     expResult  = System.getProperty("user.home");
        String     workingDIR = System.getProperty("user.home");
        UserConfig instance   = new UserConfig();
        
        instance.setWorkingDIR(workingDIR);
        assertEquals(expResult, instance.getWorkingDIR());
    }
}

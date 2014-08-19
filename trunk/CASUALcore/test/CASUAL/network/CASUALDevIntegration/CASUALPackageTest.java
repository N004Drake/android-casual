/*
 * Copyright (C) 2014 adamoutler
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

package CASUAL.network.CASUALDevIntegration;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author adamoutler
 */
public class CASUALPackageTest {
    CASUALPackage cp;
    public CASUALPackageTest() {
        cp=new CASUALPackage("https://builds.casual-dev.com/files/all/testpak.zip");
    }
    
    @Before
    public void setUp() {
    }

    /**
     * Test of isValid method, of class CASUALPackage.
     */
    @Test
    public void testIsValid() {
        assert cp.isValid();
    }

    /**
     * Test of getDescritpion method, of class CASUALPackage.
     */
    @Test
    public void testGetDescritpion() {
        assert !cp.getDescription().isEmpty();
    }

    /**
     * Test of getDeveloper method, of class CASUALPackage.
     */
    @Test
    public void testGetDeveloper() {
        assert !cp.getDeveloper().isEmpty();
    }

    /**
     * Test of getDonateTo method, of class CASUALPackage.
     */
    @Test
    public void testGetDonateTo() {
        assert !cp.getDonateTo().isEmpty();
    }

    /**
     * Test of getDonateLink method, of class CASUALPackage.
     */
    @Test
    public void testGetDonateLink() {
        assert !cp.getDonateLink().contains(".");
    }

    /**
     * Test of getWindowTitle method, of class CASUALPackage.
     */
    @Test
    public void testGetWindowTitle() {
        assert !cp.getWindowTitle().isEmpty();
    }

    /**
     * Test of getDescription method, of class CASUALPackage.
     */
    @Test
    public void testGetDescription() {
        assert !cp.getDescription().isEmpty();
    }

    /**
     * Test of getRevision method, of class CASUALPackage.
     */
    @Test
    public void testGetRevision() {
        assert !cp.getRevision().isEmpty();
    }

    /**
     * Test of getSupportURL method, of class CASUALPackage.
     */
    @Test
    public void testGetSupportURL() {
        assert !cp.getSupportURL().isEmpty();
    }

    /**
     * Test of getUniqueID method, of class CASUALPackage.
     */
    @Test
    public void testGetUniqueID() {
        assert !cp.getUniqueID().isEmpty();
    }
    
}

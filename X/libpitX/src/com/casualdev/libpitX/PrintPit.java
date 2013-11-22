/*PrintPit prints the pit file provided
 *Copyright (C) 2013  Adam Outler
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.casualdev.libpitX;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author adamoutler
 */
public class PrintPit {

    /**
     * Analyzes and prints a pit file in print-pit format.
     *
     * @param args filename to analyze
     */
    public static void main(String args[]) {
        if (args.length==0)showMessage();
        try {
            System.out.println(new PitData(new File(args[0])).toString());
        } catch (FileNotFoundException ex) {
            showMessage();
        }
    }

    static void showMessage() {
        System.err.println("File Not Found");
        System.out.println("PrintPit prints the pit file provided");
        System.out.println("  usage: printpit path/to/pitFile.pit");
        System.exit(1);

    }
}

/*JavaSystem is a class for managing the jvm
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
package CASUAL;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

/**
 *
 * @author adam
 */
public class JavaSystem {

    /**
     * restarts java
     *
     * @param args args to add
     * @throws IOException
     * @throws InterruptedException
     */
    public static void restart(String[] args) throws IOException, InterruptedException {
        StringBuilder cmd = new StringBuilder();

        cmd.append("\"").append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java").append("\"").append(" ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append("\"").append(jvmArg).append("\"").append(" ");
        }
        cmd.append("-cp ").append("\"").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append("\"").append(" ");
        cmd.append("\"").append(JavaSystem.class.getName()).append("\"").append(" ");
        for (String arg : args) {
            //cmd.append("\"").append(arg).append("\"").append(" ");
        }
        Runtime.getRuntime().exec(cmd.toString());

    }
    
    public static void launch(String[] args) throws IOException, InterruptedException {
        StringBuilder cmd = new StringBuilder();

        cmd.append("\"").append(System.getProperty("java.home")).append(File.separator).append("bin").append(File.separator).append("java").append("\"").append(" ");
        for (String jvmArg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            cmd.append("\"").append(jvmArg).append("\"").append(" ");
        }
        cmd.append("-cp ").append("\"").append(ManagementFactory.getRuntimeMXBean().getClassPath()).append("\"").append(" ");
        cmd.append("\"").append(JavaSystem.class.getName()).append("\"").append(" ");
        for (String arg : args) {
            cmd.append("\"").append(arg).append("\"").append(" ");
        }
        Runtime.getRuntime().exec(cmd.toString());

    }
}

/*OSTools provides a way to determine which platform is being used and various other tools. 
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

/**
 *
 * @author adam
 */
public class OSTools {

    public static boolean isMac() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.indexOf("mac") >= 0;
    }
    //Check for Linux

    public static boolean is64bitSystem() {
        if (isWindows()) {
            return isWindows64Arch();
        } else {
            return isMacLinux64Arch();
        }
    }

    public static String checkLinuxArch() {
        Shell shell = new Shell();
        String[] Command = {"dpkg", "--help"};
        String dpkgResults = shell.silentShellCommand(Command);
        if (dpkgResults.contains("aptitude") || dpkgResults.contains("debian") || dpkgResults.contains("deb")) {
            String[] CommandArch = {"arch"};
            String rawArch = shell.silentShellCommand(CommandArch);
            if (rawArch.contains("armv6")) {
                Statics.heimdallResource = Statics.heimdallLinuxARMv6;
                return "armv6";
            } else if (rawArch.contains("i686")) {
                Statics.heimdallResource = Statics.heimdallLinuxi386;
                return "i686";
            } else if (rawArch.contains("x86_64")) {
                Statics.heimdallResource = Statics.heimdallLinuxamd64;
                return "x86_64";
            } else {
                return "Linux";
            }
        } else {
            return "Linux";
        }
    }

    public static boolean isWindows64Arch() {
        return System.getenv("ProgramFiles(x86)") != null;
    }

    public static String OSName() {
        return System.getProperty("os.name");
    }

    public static boolean isLinux() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.indexOf("nux") >= 0;
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.indexOf("win") >= 0;
    }
    //Check for Mac

    static boolean isMacLinux64Arch() {
        String[] CommandArch = {"arch"};
        return new Shell().silentShellCommand(CommandArch).contains("64");
    }

   
}

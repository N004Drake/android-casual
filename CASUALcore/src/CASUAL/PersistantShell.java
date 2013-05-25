/*PersistantShell provides a shell to be easily opened as an object
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

import java.io.*;

/**
 *
 * @author Adam
 */
public class PersistantShell {

    public BufferedInputStream OUTPUT;
    public BufferedInputStream ERROR;
    public BufferedOutputStream INPUT;

    PersistantShell() {
        INPUT = new BufferedOutputStream(p.getOutputStream());
        OUTPUT = new BufferedInputStream(p.getInputStream());
        ERROR = new BufferedInputStream(p.getErrorStream());

    }
    public Process p;
}

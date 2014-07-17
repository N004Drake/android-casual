/*
 * Copyright (C) 2013 adam
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
 * along with this program.  If not, see &lt;http://www.gnu.org/licenses/&gt;.
 */

package GUI.testing;

import CASUAL.CASUALMessageObject;
import CASUAL.caspac.Caspac;
import CASUAL.caspac.Script;
/**
 *
 * @author adam
 */
public class automatic implements CASUAL.iCASUALUI {

    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public void setReady(boolean ready) {
        
    }

    @Override
    public boolean isDummyGUI() {
        return true;
    }



    @Override
    public String displayMessage(CASUALMessageObject mo) {
        StringBuilder sb=new StringBuilder();
        String n="%n";
        sb.append(mo.title).append(n).append(mo.messageText).append(n);
        sb.append("MessageType:").append(mo.getMessageType()).append(n);
        return "";
    }

    @Override
    public void dispose() {
        
    }

    @Override
    public void StartButtonActionPerformed() {
        
    }



    @Override
    public boolean setControlStatus(boolean status) {
        return true;
    }

    @Override
    public boolean getControlStatus() {
        return true;
    }

    @Override
    public void setCASPAC(Caspac caspac) {
        
    }

    @Override
    public void setInformationScrollBorderText(String title) {
        
    }

    @Override
    public void setProgressBar(int value) {
        
    }

    @Override
    public void setProgressBarMax(int value) {
        
    }

    @Override
    public void setScript(Script s) {
        
    }

    @Override
    public void setStartButtonText(String text) {
        
    }

    @Override
    public void setStatusLabelIcon(String Icon, String Text) {
        
    }

    @Override
    public void setStatusSubTitle(String text) {
        
    }



    @Override
    public void setWindowBannerText(String text) {
        
    }

    @Override
    public void setVisible(boolean b) {
        
    }

    @Override
    public void deviceConnected(String mode) {
        
    }

    @Override
    public void deviceDisconnected() {
        
    }

    @Override
    public void deviceMultipleConnected(int numberOfDevicesConnected) {
        
    }

   
    @Override
    public void setBlocksUnzipped(String i) {
        
    }

    @Override
    public void sendString(String string) {
    }

    @Override
    public void sendProgress(String data) {
    }

    @Override
    public void setStatusTitle(String text) {
    }
    
}

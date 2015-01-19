/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.casual_dev.caspaccreator2.exception;

/**
 *
 * @author adamoutler
 */
public class MissingParameterException extends Exception{
    private static final long serialVersionUID = 753243134134134127L;
     public MissingParameterException(String missingParameterName){
         super("Mandatory parameter: \"--"+missingParameterName+ "\" missing.");
     }
}

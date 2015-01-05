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
     public MissingParameterException(String missingParameterName){
         super("Mandatory parameter: \"--"+missingParameterName+ "\" missing.");
     }
}

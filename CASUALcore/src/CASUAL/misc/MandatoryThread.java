/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CASUAL.misc;


import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author adamoutler
 */
public class MandatoryThread extends Thread{
    AtomicBoolean hasStarted = new AtomicBoolean(false);
    boolean nullThread=false;
    public MandatoryThread(Runnable r){
        super(r);
    }
    public MandatoryThread(){
        hasStarted.set(true);
        nullThread=true;
    }
 
    @Override
    public synchronized void start() {
        super.start();
        hasStarted.set(true);
        
        notify();
    }

    public synchronized void waitFor() {
        try {
            if (nullThread){
                return;
            }
            while (!hasStarted.get()){
                wait();
            }
            super.join();
        } catch (InterruptedException ex) {
            
        }
    }

};


    
  


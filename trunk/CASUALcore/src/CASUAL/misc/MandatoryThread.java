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


    /**
     * constructor for thread sets up a blank Mandatory Thread.
     */
    public MandatoryThread(){
        hasStarted.set(true);
        nullThread=true;
    }
    /**
     * constructor accepts a runnable to be used for the thread.
     * @param r to be run in a different thread
     */
    public MandatoryThread(Runnable r){
        super(r);
    }
 
    
    /**
     * starts the thread and sets the "hasStarted" boolean. 
     */
    @Override
    public synchronized void start() {
        super.start();
        hasStarted.set(true);
        notify();
    }
    
    /**
     * isComplete allows for monitoring of the progress of a thread.  If the
     * thread has started and is no longer alive this will return true.  The
     * MandatoryThread has done its job.
     * @return true if MandatoryThread is complete
     */
    public boolean isComplete(){
        return hasStarted.get() && ! super.isAlive();
    }

    /**
     * halts the current thread until the mandatoryThread has completed.  If the
     * thread has not started, it will wait for the thread to start. 
     */
    public synchronized void waitFor() {
        try {
            if (nullThread){
                return;
            }
            while (!hasStarted.get()){
                wait();
            }
            if (this.isAlive()){
                super.join();
            }
        } catch (InterruptedException ex) {
            
        }
    }

};


    
  


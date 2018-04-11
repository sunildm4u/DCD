package com.makelifesimple.duplicatedetector;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.Environment;

public class UnhandledException {

    public static void setUndhandledException(Thread thread) {
        if (thread.getUncaughtExceptionHandler() == null) {
            Thread.currentThread().setUncaughtExceptionHandler(
                    new Thread.UncaughtExceptionHandler() {
                        @Override
                        public void uncaughtException(Thread thread,
                                Throwable ex) {

                            PrintWriter pw;
                            try {
                            	
                            	
                            	File f = new File(Environment.getExternalStorageDirectory()+"/deviceexception.log");
        				    	if(!f.exists())
        				    	{
        				    		f.createNewFile();
        				    	}	
                            	
                            
        				    	pw = new PrintWriter(
        				                new FileWriter(f, true));
        				        ex.printStackTrace(pw);
        				        pw.flush();
        				        pw.close();
                                ex.printStackTrace(pw);
                                pw.flush();
                                pw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        
                    });
        }

    }}

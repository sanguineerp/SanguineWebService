package com.webservice.util;

import java.io.BufferedWriter;

public class clsFileIOUtil {

	
	public void funPrintBlankSpace(String printWord, BufferedWriter objBWriter, int actualPrintingSize) {
        try {
            int wordSize = printWord.length();
            int availableBlankSpace = actualPrintingSize - wordSize;
            
            int leftSideSpace = availableBlankSpace / 2;
            if (leftSideSpace > 0) {
                for (int i = 0; i < leftSideSpace; i++) {
                	objBWriter.write(" ");
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.calamus.common.tools;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author haerwynn
 */
public class ImageResize {
	//private static final Log log = LogFactory.getLog(ImageResize.class);
	/**
     * Resizes an image to a absolute width and height (the image may not be
     * proportional)
     * @param inputFile Original image file
     * @param scaledWidth absolute width in pixels
     * @param scaledHeight absolute height in pixels
     * @throws IOException
     */
    public static void resizeAndSave(File inputFile, File outputFile, int scaledWidth, int scaledHeight)
            throws IOException {
        // reads input image
        //File inputFile = new File(inputImagePath);
        BufferedImage inputImage = ImageIO.read(inputFile);
		if(scaledWidth<0){
			int w=inputImage.getWidth();
			int h=inputImage.getHeight();
			scaledWidth=(int)(scaledHeight*w/h);
		}else if(scaledHeight<0){
			int w=inputImage.getWidth();
			int h=inputImage.getHeight();
			scaledHeight=(int)(scaledWidth*h/w);
		}
		String inputImageName = inputFile.getName();
		String basename = inputImageName.substring(0,inputImageName.lastIndexOf("."));
        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        // extracts extension of output file
        String formatName = /*"png";*/outputFile.getName().substring(outputFile.getName().lastIndexOf(".") + 1);


       // writes to output file
		/*File old=new File(dir,oldPhotoFilename);
		if(old.exists())old.delete();
		if(ph.exists()){
			ph.renameTo(old);
			setIcon(new ImageIcon(old.getAbsolutePath()));
		}*/
        ImageIO.write(outputImage, formatName, outputFile);
		//log.debug("image resized");
		//displayPhoto();
    }

}

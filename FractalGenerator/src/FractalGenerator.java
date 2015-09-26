import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

//import com.sun.tools.javac.util.Convert;
//import com.sun.xml.internal.ws.org.objectweb.asm.Type;


public class FractalGenerator {
	// ----------------------------------------------------------------------------------------------------------
	// 2015.08.22 MD Alexis
	// This class is used for generating fractals in the mandelbrot set
	// ----------------------------------------------------------------------------------------------------------
	private static int imagePixelsX = 1920;
    private static int imagePixelsY = 1080;
    private static int MaxCount = 500;      	//100 gives lots of black
    private double[] coordinates = { 0, 0 };  	// upper left
    public int[] CountHistogram;
    public int[]imageMaxPixels = {1920,1080};
    public Color[] Colortable;
    private static double Gap = 0.000001;  		//gap determines how fine the resolution is, or how zoomed in you are on the plane
    public BufferedImage fractalImage;			// = new BufferedImage(imageMaxPixels[0], imageMaxPixels[1], BufferedImage.TYPE_4BYTE_ABGR);
    //Methods
    public void ComputeFractalImage(double[] StartCoordinate, int Zoomlevel, int[]MaxPixels)
    {
        //Run the iterations to compute the fractal image
        //TODO: How do we allow it to be interrupted - should it run in its own thread then call back when done?
        //Create data
        Color customColor = Color.BLACK;
        double X = 0;
        double Y = 0;
        int Value = 0;

        //Determine the distance between pixels in the fractal plane ("Gap")
        // Zoom level of 1 = Gap = .0000001 or 1 e-6
        // Zoom level of 2 = Gap = .00000005 oe 5 e-7, twice the zoom in therefore 1/2 the gap
        if (Zoomlevel >0)
        { Gap = (1 / (double)Zoomlevel) * 1e-3; } //Was 1 e-6
        else
        { Gap = 1e-6; }    

        //Create a new bitmap image
        fractalImage = new BufferedImage(MaxPixels[0], MaxPixels[1], BufferedImage.TYPE_4BYTE_ABGR);
        //Set up a Histogram of count values for debug and optimization
        CountHistogram = new int[(int)MaxCount+1];
        //Set up the color lookup table
        Colortable = ColorTableBuild((int) MaxCount);

        int MaxObservedCount = 0;
        //string Label = "";
        //Random Xrandom = new Random();
        //double ColorPct = 0;

        for (int i = 0; i < MaxPixels[0]; i++)
        {
            //Start inner and outer loops...this takes time
        	if((i % 100)==0)System.out.println("Calculating row = " + i);
            for (int j = 0; j < MaxPixels[1]; j++)
            {
                //Set coordinates
                //TODO: adjust this to be the center of the bitmap, not the corner as it is now.
                X = StartCoordinate[0] + (i * Gap);
                Y = StartCoordinate[1] + (j * Gap);
                //Z = 0;

                // Value = (double)GetCountAtCoordinate(X, Y);
                Value = ComputeCountAtCoordinate(X, Y);

                //Record the largest count you see
                if (Value> MaxObservedCount) MaxObservedCount = Value;
                
                //use a lookup table for the pixel color (built previously)
                customColor = Colortable[Value-1];
                                
                //Update the Bitmap image II
                int rgb = customColor.getRGB();
                fractalImage.setRGB(i, j, rgb);
            }
        }
        // Save the image to file
        SaveImageToFile(fractalImage, "Fractal.png");
    }
    public void SaveImageToFile(BufferedImage Image, String ImageFileName)
    {
    	try {
    	    File outputfile = new File(ImageFileName);
    	    ImageIO.write(Image, "png", outputfile);
    	    System.out.println("Success, image saved to " + outputfile.getName());
    	} 
    	catch (IOException e) {
    	    System.out.println("ERROR: Error saving image." + e.getMessage());
    	}
    }
    public int ComputeCountAtCoordinate(double x, double y)
    {
        //Coordinates are in the real plane
        //Count = the number of iterations before the magnitude of z >2.0
        // z = a + bi
        double az = 0;  //Real
        double bz = 0;  //Imaginary
        double a = 0;   //Placeholders for intermediate calculations
        double b = 0;
        double size = 0;    //Holds magnitude of z
        int c = 0;         //iteration counter

        //Set the coordinates of this Fractal
        coordinates[0] = x;
        coordinates[1] = y;

        //Set up a Histogram of count values for debug and optimization
        //CountHistogram = new int[(int)MaxCount+1];

        //Begin the iteration loop
        while (true)
        {
            //Mandelbrot calculation on z.. 
            //c = coordinate (x,y)
            //z= z^2 +c

            //Real part
            a = (az * az) - (bz * bz) + x;
            //Imaginary part
            b = (2 * az * bz) + y;

            //New value of z....
            az = a;
            bz = b;

            //Magnitude of the new z..
            size = Math.sqrt((az * az) + (bz * bz));
            //Increment iteration count
            c++;

            //Check results and exit loop if conditions are satisfied
            if (size > 2)
            {
                //Status.Out("sx, cnt = " + size.ToString()+ "," + c.ToString());
                break;
            }
            //Don't let the count exceed a limit, say 1000
            if (c >= (int)MaxCount) break;
        }
        //Update the histogram
        CountHistogram[c]++;
        return c;   //Result is the iteration counter value
    }
    public double[] GetFractalCoordinatesFromImage(int PixelX, int PixelY)
    {
        //Returns the coordinates in the fractal plane based on a location in the  base image
        double[] currentCoordinate = { 0, 0 };

        currentCoordinate[0] = coordinates[0] * (1 + PixelX * Gap);
        currentCoordinate[1] = coordinates[1] * (1 + PixelX * Gap); 

        return currentCoordinate;
    }
    private Color[] ColorTableBuild(int Length)
    {
        //Builds a lookup tabel for assigning colors
        Color[] ct = new Color[Length];

        for (int i = 0; i < ct.length; i++) {
        	//TODO: make this a switch via an enum
        	//Pick one:
            //ct[i] = GrayScaleColorFromCountValue(i);
            //ct[i] = AlternatingColorFromCountValue(i);
            //ct[i] = ComputeColorFromCountValue((double) i);
            //ct[i] = ColorBlackAndWhiteOnly(double) i);
            //ct[i] = HeatMapColor2((double) i,0, MaxCount);
            //ct[i] = HeatMapColor(i,0,MaxCount);
        	ct[i]= AlternatingColorScheme2(i, MaxCount);      
        }
        return ct;
    }
    public Color ComputeColorFromCountValue(int CountValue)
    {
        //Set the color of the point, based on the value
        Color customColor;
        double ColorPct = 0;
        int ColorLevel = 0;

        if (CountValue >= MaxCount)
        {
            //This is member of the mandelbrot set, it is black
            //customColor = Color.Black;
            customColor = Color.BLACK;
        }
        else
        {
            ColorPct = CountValue / MaxCount;
            ColorPct = ColorPct * (double)(0x00FFFFFF);
            ColorLevel = (int)(ColorPct);
            //ColorLevel = (int)(((Value / MaxCount) * 0x0000FF00));
            //or in the alpha value 0x7F is full opaque
            //ColorLevel = ColorLevel | 0x80000000;
            if (ColorLevel > 0x000FFFFFF) ColorLevel = 0x000FFFFFF;
            if (ColorLevel < 0) ColorLevel = 0;
            customColor = new Color(ColorLevel, true) ;//      //First param sets the alpha level           
        }

        return customColor;
    }
    public Color GrayScaleColorFromCountValue(int CountValue)
    {
        //Set the color of the point, based on the value
        Color customColor;
        int ColorLevel = 0;
        int rgbLevel = 0;

        if (CountValue >= MaxCount)
        {
            //This is member of the mandelbrot set, it is black
            customColor = Color.BLACK;
        }
        else
        {
            if (ColorLevel > 0x000FFFFFF) ColorLevel = 0x000FFFFFF;
            if (ColorLevel < 0) ColorLevel = 0;
            rgbLevel = (int)CountValue % 0xFF;
            customColor = new Color(rgbLevel, rgbLevel, rgbLevel);
        }
        return customColor;
    }
    public Color AlternatingColorFromCountValue(int CountValue)
    {
        //Set the color of the point, based on the value
        Color customColor;
        int redLevel = 0;
        int greenLevel = 0;
        int blueLevel = 0;
        int rgbLevel = 0;
        int ColorRange = 0x000FFFFFF;
        
        if (CountValue >= MaxCount)
        {
            //This is member of the mandelbrot set, it is black
            //customColor = Color.Black;
            customColor =  Color.BLACK;
        }
        else
        {
            rgbLevel = (int)((CountValue / MaxCount) * ColorRange);
            //Break it down to three parts R, G and B:
            blueLevel = (int)(rgbLevel & 0x000000FF);
            if (blueLevel > 255) blueLevel = 255;
            greenLevel = (int)(rgbLevel & 0x0000FF00)/ 0xFF;
            if (greenLevel > 255) greenLevel = 255;
            redLevel = (int)(rgbLevel & 0x00FF0000) / 0xFFFF;
            if (redLevel > 255) redLevel = 255;
            customColor = new Color(redLevel, greenLevel, blueLevel);
        }

        return customColor;
    }
    public Color AlternatingColorScheme2(int CountValue, int MaximumCount)
    {
        //Set the color of the point, based on the value
        Color customColor;
        int redLevel = 0;
        int greenLevel = 0;
        int blueLevel = 0;
  
        //Break it down to three parts R, G and B:
        if(CountValue <MaximumCount/3){
        		blueLevel = (int)(CountValue % 0xFF);
        	}
        	else if (CountValue <MaximumCount*2/3){
        		greenLevel = (int)(CountValue % 0xFF);
        	}
        	else {
        		redLevel = (int)(CountValue % 0xFF);
        	}

            customColor = new Color(redLevel, greenLevel, blueLevel);
        
        return customColor;
    }
    public Color ColorBlackAndWhiteOnly(double CountValue)
    {
        //Set the color of the point, based on the value - returns only Black or White
        Color customColor;
        int TestValue = ((int)CountValue) % 2;


        if ((TestValue) != 0)
        {
            customColor = Color.BLACK;
        }
        else
        {
            customColor = Color.WHITE;
        }
        
        return customColor;
    }
    public Color HeatMapColor2(double value, double min, double max)
    {
        double val = (value - min) / (max - min);
        int r = (int)(255 * val);
        int g = (int)(255 * (1 - val));
        int b = 0;        
        Color result = new Color(r,g,b);
        return result;
    }
    private Color HeatMapColor(double value, double min, double max)
    {
        //Returns a color based on the value, use to create "HeatMap" type coloring
        // 
        Color firstColour = Color.BLUE;        //Color.RoyalBlue;  
        Color secondColour = Color.RED;
        // Example: Take the RGB
        //135-206-250 // Light Sky Blue
        // 65-105-225 // Royal Blue
        // 70-101-25 // Delta

        int rOffset = Math.max(firstColour.getRed(), secondColour.getRed());
        int gOffset = Math.max(firstColour.getGreen(), secondColour.getGreen());
        int bOffset = Math.max(firstColour.getBlue(), secondColour.getBlue());

        int deltaR = Math.abs(firstColour.getRed() - secondColour.getRed());
        int deltaG = Math.abs(firstColour.getGreen() - secondColour.getGreen());
        int deltaB = Math.abs(firstColour.getBlue() - secondColour.getBlue());

        double val = (value - min) / (max - min);
        int r = rOffset - (int)(deltaR * (1 - val));				//was: rOffset - Convert.ToByte(deltaR * (1 - val));
        int g = gOffset - (int)(deltaG * (1 - val));
        int b = bOffset - (int)(deltaB * (1 - val));
        
        Color customColor = new Color(r,g, b);

        return customColor;
    }
}

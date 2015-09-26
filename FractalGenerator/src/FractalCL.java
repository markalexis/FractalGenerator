/**
 * 
 */

/**
 * @author ALEXISM
 *
 */
public class FractalCL {

	//private static final double[] StartCoordinate = { -0.7104, 0.2304 };
	private static final int Zoomlevel = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Pass in the following args from the command line
		// X (double) Y (double)
		// X and Y coordinates
		// Gap
		// Zoom level
		//For now, just use as a test vehicle for the FractalGenerator class
		FractalGenerator FG = new FractalGenerator();
		int[] FractalSize = {1920,1080};
		double[] StartCoordinate =  { -0.7104, 0.2304 };
		Double firstArg=0.0;
		Double secondArg=0.0;
		
		if (args.length >= 2) 
		{
		    try 
		    {
		        firstArg = Double.parseDouble(args[0]);
		        StartCoordinate[0]=firstArg;
		        secondArg = Double.parseDouble(args[1]);
		        StartCoordinate[1]=secondArg;
		    } 
		    catch (NumberFormatException e) 
		    {
		        System.err.println("Argument" + args[0] + " must be an double.");
		        System.exit(1);
		    }
		
		//Override the command line parameters - for testing
		StartCoordinate[0]=-1;  //X,  -0.7104
		StartCoordinate[1]=0;  //Y, 0.2304
		
		System.out.println("Starting Fractal at coordinates...." + firstArg.toString() + ", " + secondArg.toString());		
		FG.ComputeFractalImage(StartCoordinate, Zoomlevel, FractalSize);
		System.out.println("Fractal Calculations Completed....");

		}

	}
}

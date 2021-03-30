package aeshliman.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Matrix
{
	// Instance Variables
	private int m;
	private int n;
	private int p;
	private int maxBuffSize;
	private int splitSize;
	private int numConsumer;
	private int maxProducerSleepTime;
	private int maxConsumerSleepTime;
	
	private int[][] a;
	private int[][] b;
	
	// Constructors
	{
		// Default values
		m = 10;
		n = 10;
		p = 10;
		maxBuffSize = 5;
		splitSize = 3;
		numConsumer = 2;
		maxProducerSleepTime = 20;
		maxConsumerSleepTime = 80;
	}
	
	public Matrix(String path)
	{
		loadConfig(path);
		generateMatrices();
	}
	
	// Getters and Setters
	
	
	// Operations
	private void loadConfig(String path) // Parses a config file and updates variables as appropriate
	{
		try(Scanner scan = new Scanner(new File(path));)
		{
			while(scan.hasNext())
			{
				// splits each line on the delimiter =
				String[] line = scan.nextLine().split("\s*=\s*");
				if(line.length!=2) continue; 
				switch(line[0]) // Set value of variables associated with key
				{
				case "M":
					m = Integer.valueOf(line[1]);
					break;
				case "N":
					n = Integer.valueOf(line[1]);
					break;
				case "P":
					p = Integer.valueOf(line[1]);
					break;
				case "MaxBuffSize":
					maxBuffSize = Integer.valueOf(line[1]);
					break;
				case "SplitSize":
					splitSize = Integer.valueOf(line[1]);
					break;
				case "NumConsumer":
					numConsumer = Integer.valueOf(line[1]);
					break;
				case "MaxProducerSleepTime":
					maxProducerSleepTime = Integer.valueOf(line[1]);
					break;
				case "MaxConsumerSleepTime":
					maxConsumerSleepTime = Integer.valueOf(line[1]);
					break;
				default:
					System.err.println("Unknown key " + line[0] + " with value " + line[1]);
					break;
				}
			}
		}
		catch(FileNotFoundException e) { e.printStackTrace(); }
	}
	
	private void generateMatrices()
	{
		// Sets size of matrices A and B
		a = new int[m][n];
		b = new int[n][p];
		
		// Randomly generates values between 0 and 9 inclusively for each index in matrices A and B
		Random ran = new Random();
		for(int i=0; i<m; i++) { for(int j=0; j<n; j++) { a[i][j] = ran.nextInt(10); } }
		for(int i=0; i<n; i++) { for(int j=0; j<p; j++) { b[i][j] = ran.nextInt(10); } }
	}
	
	public void tmp()
	{
		WorkItem w = new WorkItem(a, b, 0, 2, 2, 5);
		System.out.println("\n" + w);
	}
	
	// toString
	public String toString()
	{
		String toString = "MaxBufferSize: " + maxBuffSize + "   SplitSize: " + splitSize + "   NumConsumer: " + numConsumer + 
				"   MaxProducerSleepTime: " + maxProducerSleepTime + "   MaxConsumerSleepTime: " + maxConsumerSleepTime;
		toString += "\nM: " + m + "   N: " + n + "   P: " + p + "\n";
		toString += "Matrix A\n" + matrixToString(a) + "\nMatrix B\n" + matrixToString(b);
		return toString;
	}
	
	private String matrixToString(int[][] matrix) // Returns a string representation of a matrix
	{
		String toString = "";
		for(int i=0; i<matrix.length; i++)
		{
			for(int j=0; j<matrix[i].length; j++) { toString += Integer.toString(matrix[i][j]) + ", "; }
			toString = toString.substring(0, toString.length()-2) + "\n";
		}
		return toString.trim();
	}
}

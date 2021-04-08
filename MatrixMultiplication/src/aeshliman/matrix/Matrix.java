package aeshliman.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Matrix
{
	// Instance Variables
	private int m;
	private int n;
	private int p;
	private int maxBuffSize;
	private int splitSize;
	private int numProducer;
	private int numConsumer;
	private int maxProducerSleepTime;
	private int maxConsumerSleepTime;
	private int totalSimulationTime;
	private double averageSleepTime;
	private int[] producerItemCount;
	private int[] consumerItemCount;
	private int bufferFullCount;
	private int bufferEmptyCount;
	
	private int[][] a;
	private int[][] b;
	private int[][] c;
	private int[][] d;
	
	// Constructors
	{
		// Default values
		m = 10;
		n = 10;
		p = 10;
		maxBuffSize = 5;
		splitSize = 3;
		numProducer = 1;
		numConsumer = 2;
		maxProducerSleepTime = 20;
		maxConsumerSleepTime = 80;
		totalSimulationTime = 0;
		averageSleepTime = 0;
		producerItemCount = new int[numProducer];
		consumerItemCount = new int[numConsumer];
		bufferFullCount = 0;
		bufferEmptyCount = 0;
	}
	
	public Matrix(String path)
	{
		loadConfig(path);
		generateMatrices();
		run();
	}
	
	// Getters and Setters
	
	
	// Operations
	public void run()
	{
		System.out.println(this);
		
		// Stores all producers, consumers, and threads in an arraylist
		ArrayList<Producer> producers = new ArrayList<Producer>(numProducer);
		ArrayList<Consumer> consumers = new ArrayList<Consumer>(numConsumer);
		ArrayList<Thread> producerThreads = new ArrayList<Thread>(numProducer);
		ArrayList<Thread> consumerThreads = new ArrayList<Thread>(numConsumer);
		
		// Initializes producers and consumers as threads and starts them
		AtomicBoolean cont = new AtomicBoolean(true);
		SharedBuffer buffer = new SharedBuffer(maxBuffSize);
		for(int i=0; i<numProducer; i++)
		{
			// Creates a producer object and thread adding both to associated lists
			Producer producer = new Producer(buffer, a, b, c, splitSize, maxProducerSleepTime);
			producers.add(producer);
			Thread thread = new Thread(producer);
			producerThreads.add(thread);
		}
		for(int i=0; i<numConsumer; i++)
		{
			// Creates a consumer object and thread adding both to associated lists
			Consumer consumer = new Consumer(buffer, maxConsumerSleepTime, cont);
			consumers.add(consumer);
			Thread thread = new Thread(consumer);
			consumerThreads.add(thread);
		}
		long startTime = System.currentTimeMillis();
		
		// Starts all threads
		for(Thread thread : producerThreads) thread.start();
		for(Thread thread : consumerThreads) thread.start();
		
		// Attempts to join producers and consumers
		for(int i=0; i<numProducer; i++)
		{
			try { producerThreads.get(i).join(); }
			catch(InterruptedException e) {  }
		}
		cont.set(false); // Updates the continue flag
		for(int i=0; i<numConsumer; i++)
		{
			consumerThreads.get(i).interrupt();
			try { consumerThreads.get(i).join(); }
			catch(InterruptedException e) {  }
		}
		long endTime = System.currentTimeMillis();
		
		// Calculates simulations statistics
		totalSimulationTime = (int)(endTime-startTime);
		for(Producer producer : producers) averageSleepTime += producer.getTotalSleepTime();
		for(Consumer consumer : consumers) averageSleepTime += consumer.getTotalSleepTime();
		averageSleepTime /= (double) numProducer + numConsumer;
		for(int i=0; i<numProducer; i++) producerItemCount[i] = producers.get(i).getProducedWorkItems();
		for(int i=0; i<numConsumer; i++) consumerItemCount[i] = consumers.get(i).getConsumedWorkItems();
		bufferFullCount = buffer.getCountFull();
		bufferEmptyCount = buffer.getCountEmpty();
		solve();
		
		// Prints results to console
		System.out.println("---------------------------------------------\nFinal Result of Matrix C\n" + matrixToString(c));
		System.out.println("Verified Matrix C Multiplication\n" + matrixToString(d));
		System.out.println("Verification Test Passed? " + verify(c,d) + "\n---------------------------------------------");
		System.out.println(statisticsToString());
	}
	
	private void loadConfig(String path) // Parses a config file and updates variables as appropriate
	{
		try(Scanner scan = new Scanner(new File(path));)
		{
			while(scan.hasNext())
			{
				// splits each line on the delimiter =
				String[] line = scan.nextLine().split("\\s*=\\s*");
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
		c = new int[m][p];
		
		// Randomly generates values between 0 and 9 inclusively for each index in matrices A and B
		Random ran = new Random();
		for(int i=0; i<m; i++) { for(int j=0; j<n; j++) { a[i][j] = ran.nextInt(10); } }
		for(int i=0; i<n; i++) { for(int j=0; j<p; j++) { b[i][j] = ran.nextInt(10); } }
	}
	
	private void solve() // Loops through the matrices A and B to solve
	{
		d = new int[a.length][b[0].length];
		for(int i=0; i<a.length; i++)
		{
			for(int j=0; j<b[0].length; j++)
			{
				for(int k=0; k<b.length; k++)
				{
					d[i][j] += a[i][k] * b[k][j];
				}
			}
		}
	}
	
	private boolean verify(int[][] matrixA, int[][] matrixB)
	{
		if(matrixA.length!=matrixB.length||matrixA[0].length!=matrixB[0].length) return false;
		for(int i=0; i<matrixA.length; i++)
		{
			for(int j=0; j<matrixA[i].length; j++)
			{
				if(matrixA[i][j]!=matrixB[i][j]) return false;
			}
		}
		return true;
	}
	
	// toString
	public String toString()
	{
		String toString = "MaxBufferSize: " + maxBuffSize + "   SplitSize: " + splitSize + "   NumConsumer: " + numConsumer + 
				"   MaxProducerSleepTime: " + maxProducerSleepTime + "   MaxConsumerSleepTime: " + maxConsumerSleepTime;
		toString += "\nM: " + m + "   N: " + n + "   P: " + p + "\n";
		toString += "Matrix A\n" + matrixToString(a) + "\nMatrix B\n" + matrixToString(b);
		toString += "\n---------------------------------------------";
		return toString;
	}
	
	private String matrixToString(int[][] matrix) // Returns a string representation of a matrix
	{
		String toString = "";
		for(int i=0; i<matrix.length; i++)
		{
			for(int j=0; j<matrix[i].length; j++) { toString += String.format("%-4d ", matrix[i][j]); } // Integer.toString(matrix[i][j]) + ", "; 
			toString = toString.substring(0, toString.length()-2) + "\n";
		}
		return toString.trim();
	}
	
	private String statisticsToString() // Returns a string representation of the simulations statistics
	{
		String toString = "Producer/Consumer Simulation Results\n";
		toString += String.format("Simluation Time: %32s%dms\n", "", totalSimulationTime);
		toString += String.format("Average Thread Sleep Time: %22s%.2fms\n", "", averageSleepTime);
		toString += String.format("Number of Producer Threads: %22d\n", numProducer);
		toString += String.format("Number of Consumer Threads: %22d\n", numConsumer);
		toString += String.format("Size of Buffer: %34d\n", maxBuffSize);
		
		String produced = "";
		int totalProduced = 0;
		for(int i=0; i<numProducer; i++)
		{
			produced += String.format("   Producer %d: %"+ (35-(i/10)) + "d\n", i, producerItemCount[i]);
			totalProduced += producerItemCount[i];
		}
		toString += String.format("Total Number of Items Produced: %18d\n", totalProduced);
		toString += produced;
		
		String consumed = "";
		int totalCosnumed = 0;
		for(int i=0; i<numConsumer; i++)
		{
			consumed += String.format("   Consumer %d: %"+ (35-(i/10)) + "d\n", i, consumerItemCount[i]);
			totalCosnumed += consumerItemCount[i];
		}
		toString += String.format("Total Number of Items Consumed: %18d\n", totalCosnumed);
		toString += consumed;
		
		toString += String.format("Number of Time Buffer Was Full: %18d\n", bufferFullCount);
		toString += String.format("Number of Time Buffer Was Empty: %17d\n", bufferEmptyCount);
		
		return toString;
	}
}

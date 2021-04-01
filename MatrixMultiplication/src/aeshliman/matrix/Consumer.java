package aeshliman.matrix;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class Consumer implements Runnable
{
	// Instance Variables
	private SharedBuffer buffer;
	private int maxConsumerSleepTime;
	private int consumedWorkItems;
	private int totalSleepTime;
	private AtomicBoolean cont;
	
	// Constructors
	{
		consumedWorkItems = 0;
		totalSleepTime = 0;
	}
	
	public Consumer(SharedBuffer buffer, int maxConsumerSleepTime, AtomicBoolean cont)
	{
		this.buffer = buffer;
		this.maxConsumerSleepTime = maxConsumerSleepTime;
		this.cont = cont;
	}
	
	// Getters and Setters
	public int getConsumedWorkItems() { return this.consumedWorkItems; }
	public int getTotalSleepTime() { return this.totalSleepTime; }
	
	// Operations
	public void run()
	{
		Random ran = new Random();
		while(true&&cont.get())
		{
			WorkItem item = buffer.get();
			if(cont.get() == false) break;
			consumedWorkItems++;
			item.solve();
			String result = "Submatrix A multiplied by Submatrix B equals Submatrix C\nSubmatrix A\n";
			result += matrixToString(item.getSubA()) + "\nSubmatrix B\n" + matrixToString(item.getSubB())
					+ "\nSubmatrix C\n" + matrixToString(item.getSubC());
			System.out.println(result);
			int time = ran.nextInt(maxConsumerSleepTime+1);
			totalSleepTime += time;
			synchronized(this)
			{
				try { Thread.sleep(time); }
				catch(InterruptedException e) {  }
			}
		}
	}
	
	// toString
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
}

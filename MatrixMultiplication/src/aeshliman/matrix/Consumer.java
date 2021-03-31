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
	
	// Constructors
	{
		consumedWorkItems = 0;
		totalSleepTime = 0;
	}
	
	public Consumer(SharedBuffer buffer, int maxConsumerSleepTime)
	{
		this.buffer = buffer;
		this.maxConsumerSleepTime = maxConsumerSleepTime;	
	}
	
	// Operations
	public void run()
	{
		Random ran = new Random();
		while(true)
		{
			WorkItem item = buffer.get();
			if(item == null) break;
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
				try { wait(time); }
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
			for(int j=0; j<matrix[i].length; j++) { toString += Integer.toString(matrix[i][j]) + ", "; }
			toString = toString.substring(0, toString.length()-2) + "\n";
		}
		return toString.trim();
	}
	
	
}

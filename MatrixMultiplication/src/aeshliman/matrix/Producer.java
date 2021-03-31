package aeshliman.matrix;

import java.util.Random;

public class Producer implements Runnable
{
	// Insatnce Variables
	private SharedBuffer buffer;
	private int[][] a;
	private int[][] b;
	private int[][] c;
	private WorkItem[][] workItems;
	private int splitSize;
	private int maxProducerSleepTime;
	private int producedWorkItems;
	private int totalSleepTime;
	
	// Constructors
	{
		producedWorkItems = 0;
		totalSleepTime = 0;
	}
	
	public Producer(SharedBuffer buffer, int[][] a, int[][] b, int[][] c, int splitSize, int maxProducerSleepTime)
	{
		this.buffer = buffer;
		this.a = a;
		this.b = b;
		this.c = c;
		workItems = new WorkItem[(int)Math.ceil((double)a.length/splitSize)][(int)Math.ceil((double)b[0].length/splitSize)];
		this.splitSize = splitSize;
		this.maxProducerSleepTime = maxProducerSleepTime;
	}
	
	// Operations
	public void run()
	{
		Random ran = new Random();
		for(int i=0; i<Math.ceil((double)a.length/splitSize); i++)
		{
			// Determines the low and high indexes of matrix A for sub matrix A
			int lowA = splitSize*i;
			int highA = splitSize*(i+1)-1;
			if(highA>a.length-1) highA = a.length-1;
			for(int j=0; j<Math.ceil((double)b[0].length/splitSize); j++)
			{
				// Determines the low and high indexes of matrix B for sub matrix B
				int lowB = splitSize*j;
				int highB = splitSize*(j+1)-1;
				if(highB>b[0].length-1) highB = b[0].length-1;
				
				// Creates a new WorkItem object and adds it to the buffer
				WorkItem item = new WorkItem(a, b, lowA, highA, lowB, highB);
				workItems[i][j] = item;
				buffer.put(item);
				System.out.println("Producer put rows " + lowA + "-" + highA + " of matrix A and columns "
						+ lowB + "-" + highB + " of matrix B to buffer");
				producedWorkItems++;
				
				// Sets producer to sleep for a random time between 0 and maxProducerSleepTime
				int time = ran.nextInt(maxProducerSleepTime+1);
				totalSleepTime += time;
				synchronized(this)
				{
					try { wait(time); }
					catch(InterruptedException e) { e.printStackTrace(); }
				}
				
			}
		}
		
		// Wait until all work items have been consumed
		for(WorkItem[] out : workItems)
		{
			for(WorkItem in : out)
			{
				while(!in.isDone())
				{
					try { Thread.sleep(1000); }
					catch(InterruptedException e) { e.printStackTrace(); }
				}
			}
		}
		
		// Copy submatrices C into matrix C
		for(int i=0; i<workItems.length; i++)
		{
			int offset = splitSize;
			for(int j=0; j<workItems[i].length; j++)
			{
				int[][] subC = workItems[i][j].getSubC();
				
				for(int x=0; x<subC.length; x++)
				{
					for(int y=0; y<subC[x].length; y++)
					{
						c[i*offset+x][j*offset+y] = subC[x][y];
					}
				}
				offset = subC.length;
			}
		}
		
		
		
	}
}

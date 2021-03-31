package aeshliman.matrix;

import java.util.concurrent.atomic.AtomicBoolean;

public class SharedBuffer
{
	// Instance Variables
	private final int MAXWAITTIME = 1000;
	private WorkItem[] buffer;
	private int maxBuffSize;
	private int count;
	private int in;
	private int out;
	private int countFull;
	private int countEmpty;
	private AtomicBoolean finished;
	
	// Constructors
	{
		count = 0;
		in = 0;
		out = 0;
		countFull = 0;
		countEmpty = 0;
		finished = new AtomicBoolean(false);
	}
	
	public SharedBuffer(int maxBuffSize)
	{
		this.maxBuffSize = maxBuffSize;
		this.buffer = new WorkItem[maxBuffSize];
	}
	
	// Getters and Setters
	public void setFinished(boolean finished) { this.finished.set(finished); }
	
	// Operations
	public synchronized WorkItem get()
	{
		while(count==0)
		{
			if(finished.get()) return null;
			try { wait(MAXWAITTIME); }
			catch(InterruptedException e) {  }
		}
		int tmp = out;
		out = ++out%maxBuffSize;
		if(--count==0) countEmpty++;
		notifyAll();
		return buffer[tmp];
	}
	
	public synchronized void put(WorkItem item)
	{
		while(count==maxBuffSize)
		{
			try { wait(MAXWAITTIME); }
			catch(InterruptedException e) {  }
		}
		buffer[in] = item;
		in = ++in%maxBuffSize;
		if(++count==maxBuffSize) countFull++;
		notifyAll();
	}
}

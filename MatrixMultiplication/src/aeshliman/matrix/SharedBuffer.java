package aeshliman.matrix;

import java.util.concurrent.atomic.AtomicInteger;

public class SharedBuffer
{
	// Instance Variables
	private WorkItem[] buffer;
	private int maxBuffSize;
	private AtomicInteger count;
	private int in;
	private int out;
	private int countFull;
	private int countEmpty;
	
	// Constructors
	{
		count = new AtomicInteger(0);
		in = 0;
		out = 0;
		countFull = 0;
		countEmpty = 0;
	}
	
	public SharedBuffer(int maxBuffSize)
	{
		this.maxBuffSize = maxBuffSize;
		this.buffer = new WorkItem[maxBuffSize];
	}
	
	// Getters and Setters
	public int getCountFull() { return this.countFull; }
	public int getCountEmpty() { return this.countEmpty; }
	
	// Operations
	public synchronized WorkItem get()
	{
		while(count.get()==0) // Wait until notified that a item is available
		{
			System.out.println("Buffer is empty - " + Thread.currentThread().getName() + " is waiting");
			try { wait(); }
			catch(InterruptedException e) { return null; }
		}

		// Increments relevant statistics and returns next item from buffer
		int tmp = out;
		out = ++out%maxBuffSize;
		if(count.decrementAndGet()==0) countEmpty++;
		notifyAll();
		return buffer[tmp];
	}
	
	public synchronized void put(WorkItem item)
	{
		while(count.get()==maxBuffSize) // Wait until notified that space for an item is available
		{
			System.out.println("Buffer is full - Thread " + Thread.currentThread().getId() + " is waiting");
			try { wait(); }
			catch(InterruptedException e) { return; }
		}
		
		// Increments relevant statistics and puts item into buffer
		buffer[in] = item;
		in = ++in%maxBuffSize;
		if(count.incrementAndGet()==maxBuffSize) countFull++;
		notifyAll();
	}
}

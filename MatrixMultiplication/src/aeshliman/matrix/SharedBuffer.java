package aeshliman.matrix;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedBuffer
{
	// Instance Variables
	private final int MAXWAITTIME = 1000;
	private WorkItem[] buffer;
	private int maxBuffSize;
	private AtomicInteger count;
	private int in;
	private int out;
	private int countFull;
	private int countEmpty;
	private AtomicBoolean cont;
	
	// Constructors
	{
		count = new AtomicInteger(0);
		in = 0;
		out = 0;
		countFull = 0;
		countEmpty = 0;
	}
	
	public SharedBuffer(int maxBuffSize, AtomicBoolean cont)
	{
		this.maxBuffSize = maxBuffSize;
		this.buffer = new WorkItem[maxBuffSize];
		this.cont = cont;
	}
	
	// Getters and Setters
	public int getCountFull() { return this.countFull; }
	public int getCountEmpty() { return this.countEmpty; }
	
	// Operations
	public synchronized WorkItem get()
	{
		while(count.get()==0)
		{
			System.out.println("Buffer is empty - " + Thread.currentThread().getName() + " is waiting");
			try { wait(); }
			catch(InterruptedException e) { if(!cont.get()) return null; }
		}
		int tmp = out;
		out = ++out%maxBuffSize;
		if(count.decrementAndGet()==0) countEmpty++;
		notifyAll();
		return buffer[tmp];
	}
	
	public synchronized void put(WorkItem item)
	{
		while(count.get()==maxBuffSize)
		{
			System.out.println("Buffer is full - Thread " + Thread.currentThread().getId() + " is waiting");
			try { wait(); }
			catch(InterruptedException e) {  }
		}
		buffer[in] = item;
		in = ++in%maxBuffSize;
		if(count.incrementAndGet()==maxBuffSize) countFull++;
		notifyAll();
	}
}

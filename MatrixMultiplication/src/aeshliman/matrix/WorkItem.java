package aeshliman.matrix;

public class WorkItem
{
	// Instance Variables
	private int[][] subA;
	private int[][] subB;
	private int[][] subC;
	private int lowA;
	private int highA;
	private int lowB;
	private int highB;
	private boolean done;
	
	// Constructors
	public WorkItem(int[][] a, int[][] b, int lowA, int highA, int lowB, int highB)
	{
		this.lowA = lowA;
		this.highA = highA;
		this.lowB = lowB;
		this.highB = highB;
		this.done = false;
		
		// Sets submatrices to correct dimensions
		subA = new int[highA-lowA+1][a[0].length];
		subB = new int[b.length][highB-lowB+1];
		subC = new int[highA-lowA+1][highB-lowB+1];
		
		// Copies values from matrices A and B into submatrices subA and subB
		for(int i=lowA; i<=highA; i++) for(int j=0; j<a[i].length; j++) subA[i-lowA][j] = a[i][j];
		for(int i=0; i<b.length; i++) for(int j=lowB; j<=highB; j++) subB[i][j-lowB] = b[i][j];
	}
	
	// Getters and Setters
	public int[][] getSubA() { return this.subA; }
	public int[][] getSubB() { return this.subB; }
	public int[][] getSubC() { return this.subC; }
	
	// Operations
	public boolean isDone() { return this.done; }
	
	public void solve() // Loops through the matrices A and B to solve
	{
		for(int i=0; i<subA.length; i++)
		{
			for(int j=0; j<subB[0].length; j++)
			{
				for(int k=0; k<subB.length; k++)
				{
					subC[i][j] += subA[i][k] * subB[k][j];
				}
			}
		}
		done = true;
	}
	
	// toString
	public String toString()
	{
		String toString = "LowA: " + lowA + "   HighA: " + highA + "   LowB: " + lowB + "   HighB: " + highB + "   Done: " + done + "\n";
		toString += "SubMatrix A\n" + matrixToString(subA) + "\nSubMatrix B\n" + matrixToString(subB) + "\nSubmatrix C\n" + matrixToString(subC);
		return toString;
	}
	
	public String matrixToString(int[][] matrix) // Returns a string representation of a matrix
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

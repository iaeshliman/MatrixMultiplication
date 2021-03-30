import aeshliman.matrix.Matrix;

public class Driver
{
	public static void main(String[] args)
	{
		Matrix m = new Matrix("config.txt");
		System.out.println(m);
		m.tmp();
	}
}

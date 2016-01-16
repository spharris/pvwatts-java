package io.github.spharris.data;

/**
 * A very simple matrix class that can be passed back and forth to and
 * from SSC 
 *  
 * @author spharris
 */
public class Matrix<T extends Number> {
	
	private int rows;
	private int cols;
	private Object[][] matrix;
	
	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		matrix = new Object[rows][cols];
	}
	
	public int rows() {
		return rows;
	}
	
	public int cols() {
		return cols;
	}
}

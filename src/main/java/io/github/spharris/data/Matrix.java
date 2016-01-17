package io.github.spharris.data;

import static com.google.common.base.Preconditions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A very simple matrix class that can be passed back and forth to and
 * from SSC. Once a matrix is created, its size cannot be changed.
 *  
 * @author spharris
 */
public class Matrix<T extends Number> {
	
	private int rows;
	private int cols;
	private List<T> matrix;
	
	/**
	 * Creates a matrix with the given number of <tt>rows</tt> and <tt>cols</tt>. 
	 * 
	 * @param rows The number of rows.
	 * @param cols The number of columns.
	 * 
	 * @throws {@link java.lang.IllegalArgumentException} if <tt>rows</tt> or <tt>cols</tt> less than 1.
	 */
	public Matrix(int rows, int cols) {
		checkArgument(rows >= 1, "The number of rows must be >= 1.");
		checkArgument(cols >= 1, "The number of columns must be >= 1.");
		
		this.rows = rows;
		this.cols = cols;
		matrix = new ArrayList<>(rows * cols);
		
		for (int i = 0; i < rows * cols; i++) {
			@SuppressWarnings("unchecked")
			T value = (T)Integer.valueOf(0);
			matrix.add(value);
		}
	}
	
	private void checkBounds(int row, int col) {
		checkArgument(row >= 1, "The row index must be >= 1.");
		checkArgument(col >= 1, "The col index must be >= 1.");
		
		if (row > rows) {
			throw new IndexOutOfBoundsException("row must be less than the number of rows in this matrix. There are"
					+ rows + " rows but got an input of " + row);
		}
		
		if (col > cols) {
			throw new IndexOutOfBoundsException("col must be less than the number of cols in this matrix. There are"
				+ cols + " cols but got an input of " + col);
		}
	}
	
	/**
	 * Sets the value of a entry (row, col). Note that this method uses a 1-based index.
	 *
	 * @throws {@link java.lang.IndexOutOfBoundsException if <tt>row</tt> or <tt>col</tt> is greater
	 * than the number of rows or columns, respectively.
	 */
	public void set(int row, int col, T value) {
		checkBounds(row, col);
		
		matrix.add(listIndex(row, col), value);
	}

	/**
	 * Gets the value of a entry (row, col). Note that this method uses a 1-based index.
	 *
	 * @throws {@link java.lang.IndexOutOfBoundsException if <tt>row</tt> or <tt>col</tt> is greater
	 * than the number of rows or columns, respectively.
	 */
	public T get(int row, int col) {
		checkBounds(row, col);
		
		return matrix.get(listIndex(row, col));
	}
	
	private static int listIndex(int row, int col) {
		return (row - 1) * (col - 1);
	}
	
	public int rows() {
		return rows;
	}
	
	public int cols() {
		return cols;
	}
}

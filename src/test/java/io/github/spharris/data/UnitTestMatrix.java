package io.github.spharris.data;

import static org.junit.Assert.*;

import org.junit.Before;

import static org.hamcrest.Matchers.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UnitTestMatrix {
	
	private static final int ROWS = 5;
	private static final int COLS = 5;
	private Matrix<Integer> testMtx;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void createMatrix() {
		testMtx = new Matrix<>(ROWS, COLS);
	}
	
	@Test
	public void createMatrixWithSize() {
		Matrix<Integer> mtx = new Matrix<>(5, 5);
		
		assertThat(mtx.rows(), equalTo(5));
		assertThat(mtx.cols(), equalTo(5));
	}
	
	@Test
	public void negativeRowsIsAnError() {
		thrown.expect(IllegalArgumentException.class);
		
		new Matrix<>(-1, 5);
	}
	
	@Test
	public void negativeColsIsAnError() {
		thrown.expect(IllegalArgumentException.class);
		
		new Matrix<>(5, -1);
	}
	
	@Test
	public void getRowGreaterThanSizeIsAnError() {
		thrown.expect(IndexOutOfBoundsException.class);
		
		testMtx.get(6, 5);
	}
	
	@Test
	public void getColGreaterThanSizeIsAnError() {
		thrown.expect(IndexOutOfBoundsException.class);
		
		testMtx.get(5, 6);
	}
	
	@Test
	public void setRowGreaterThanSizeIsAnError() {
		thrown.expect(IndexOutOfBoundsException.class);
		
		testMtx.set(6, 5, 1);
	}
	
	@Test
	public void setColGreaterThanSizeIsAnError() {
		thrown.expect(IndexOutOfBoundsException.class);
		
		testMtx.set(5, 6, 1);
	}
	
	@Test
	public void getNegativeColIsAnError() {
		thrown.expect(IllegalArgumentException.class);
		
		testMtx.get(-1, 5);
	}
	
	@Test
	public void getNegativeRowIsAnError() {
		thrown.expect(IllegalArgumentException.class);
		
		testMtx.get(5, -1);
	}
	
	@Test
	public void setNegativeColIsAnError() {
		thrown.expect(IllegalArgumentException.class);
		
		testMtx.set(-1, 5, 1);
	}
	
	@Test
	public void setNegativeRowIsAnError() {
		thrown.expect(IllegalArgumentException.class);
		
		testMtx.set(5, -1, 1);
	}
	
	@Test
	public void setValueWorks() {
		int val = 10;
		
		testMtx.set(1, 1, val);
		int result = testMtx.get(1, 1);
		
		assertThat(result, equalTo(val));
	}
	
	@Test
	public void setValueAtBoundaryWorks() {
		int val = 10;
		
		testMtx.set(1, 1, val);
		int result = testMtx.get(ROWS, COLS);
		
		assertThat(result, equalTo(val));
	}
	
	@Test
	public void getUnsetValueIsZero() {
		int val = testMtx.get(1, 1);
		
		assertThat(val, equalTo(0));
	}
	
	@Test
	public void getUnsetValueWithCastIsZero() {
		Matrix<Float> mtx = new Matrix<>(5, 5);
		float val = mtx.get(1, 1);
		
		assertThat(val, equalTo(0f));
	}
	
	@Test
	public void fillAllCellsWorksAsExpected() {
		int val = 1;
		for (int i = 1; i <= ROWS; i++) {
			for (int j = 1; j <= COLS; j++) {
				testMtx.set(i, j, val);
				val++;
			}
		}
		
		int expectedReturnVal = 1;
		for (int i = 1; i <= ROWS; i++) {
			for (int j = 1; j <= COLS; j++) {
				int result = testMtx.get(i, j);
				expectedReturnVal++;
				
				assertThat(result, equalTo(expectedReturnVal));
			}
		}
	}
}

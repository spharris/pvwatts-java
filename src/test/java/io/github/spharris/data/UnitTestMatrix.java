package io.github.spharris.data;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UnitTestMatrix {
	
	@Test
	public void createMatrixWithSize() {
		Matrix<Integer> mtx = new Matrix<>(5, 5);
		
		assertThat(mtx.rows(), equalTo(5));
		assertThat(mtx.cols(), equalTo(5));
	}
}

package io.github.spharris.ssc;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

import static org.hamcrest.Matchers.*;

public class IntegrationTestModule {
	
	private Module module;
	
	@Before
	public void createModule() {
		module = Module.forName("layoutarea");
	}
	
	@After
	public void freeModule() {
		module.free();
	}
	
	@Test
	public void getNumber() {
		String field = "field";
		float value = 0.5f;
		
		module.setNumber(field, value);
		Optional<Float> result = module.getNumber(field);

		assertThat(result.isPresent(), equalTo(true));
		assertThat(result.get(), equalTo(value));
	}
	
	@Test
	public void getArray() {
		String field = "field";
		float[] values = { 0.5f, 0.5f, 0.5f };
		
		module.setArray(field, values);
		Optional<Float[]> result = module.getArray(field);
		
		assertThat(result.isPresent(), equalTo(true));
		
		for (Float val : result.get()) {
			assertThat(val, equalTo(0.5f));
		}
	}
}

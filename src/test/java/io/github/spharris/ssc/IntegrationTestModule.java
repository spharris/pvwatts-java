package io.github.spharris.ssc;

import static org.junit.Assert.*;

import java.util.List;

import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

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
		Optional<float[]> result = module.getArray(field);
		
		assertThat(result.isPresent(), equalTo(true));
		assertThat(result.get(), equalTo(values));
	}
	
	@Test
	public void callsGetMatrixProperly() {
		String field = "field";
		float[][] values = {{1, 2}, {3, 4}};
		
		module.setMatrix(field, values);
		Optional<float[][]> result = module.getMatrix(field);
		
		assertThat(result.isPresent(), equalTo(true));
		assertThat(result.get(), equalTo(values));
	}
	
	@Test
	public void getsAllVariables() {
		List<Variable> vars = module.getVariables();
		
		Variable v1 = Variable.buildVariable()
				.varType(Variable.VariableType.INPUT)
				.dataType(Variable.DataType.MATRIX)
				.name("positions")
				.label("Positions within calculataed area")
				.group("layoutarea")
				.required("*")
				.units("")
				.meta("")
				.build();
		
		Variable v2 = Variable.buildVariable()
				.varType(Variable.VariableType.OUTPUT)
				.dataType(Variable.DataType.MATRIX)
				.name("convex_hull")
				.label("Convex hull bounding the region")
				.group("layoutarea")
				.required("*")
				.units("")
				.meta("")
				.build();
		
		Variable v3 = Variable.buildVariable()
				.varType(Variable.VariableType.OUTPUT)
				.dataType(Variable.DataType.NUMBER)
				.name("area")
				.label("Area inside the convex hull")
				.group("layoutarea")
				.required("*")
				.units("")
				.meta("")
				.build();
		
		assertThat(vars, hasSize(3));
		assertThat(vars, contains(v1, v2, v3));
	}
	
	@Test
	public void getAllModules() {
		List<ModuleInfo> modules = Module.getAvailableModules();
		
		assertThat(modules, hasSize(greaterThan(0)));
	}
	
	@Test
	public void executeWithHandler() {
		Module m = Module.forName("pvwattsv1");
		String weatherFile = getClass().getClassLoader().getResource("weather/23129.tm2").getPath();
		m.setString("solar_resource_file", weatherFile);
		m.setNumber("adjust:factor", 0.5f);
		m.setNumber("system_size", 1);
		m.setNumber("tilt", 20);
		m.setNumber("derate", 0.8f);
		m.setNumber("track_mode", 0);
		m.setNumber("azimuth", 180);
		
		ExecutionHandler handler = new ExecutionHandler() {

			@Override
			public boolean handleLogMessage(MessageType type, float time, String message) {
				return true;
			}

			@Override
			public boolean handleProgressUpdate(float percentComplete, float time, String text) {
				return true;
			}
		};
		
		m.execute(handler);
		
		assertThat(m.getString("location").get(), equalTo("23129"));
		assertThat(m.getString("state").get(), equalTo("CA"));
		assertThat(m.getNumber("ac_annual").isPresent(), equalTo(true));
		
		m.free();
	}
}

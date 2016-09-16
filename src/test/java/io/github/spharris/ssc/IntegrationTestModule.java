package io.github.spharris.ssc;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.base.Optional;
import com.google.inject.Guice;
import com.google.inject.Inject;

@RunWith(JUnit4.class)
public class IntegrationTestModule {

  static final float EPSILON = 0f;
  
  @Inject
  private ModuleFactory moduleFactory;

  private Module module;

  @Before
  public void createModule() {
    Guice.createInjector(new SscModule()).injectMembers(this);

    module = moduleFactory.create("layoutarea");
  }

  @After
  public void freeModule() {
    module.free();
  }

  @Test
  public void getNumber() {
    String field = "field";
    float value = 0.5f;

    module.setValue(field, value);
    Optional<Float> result = module.getNumber(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isWithin(EPSILON).of(value);
  }

  @Test
  public void getArray() {
    String field = "field";
    float[] values = {0.5f, 0.5f, 0.5f};

    module.setValue(field, values);
    Optional<float[]> result = module.getArray(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).hasValuesWithin(EPSILON).of(values);
  }

  @Test
  public void callsGetMatrixProperly() {
    String field = "field";
    float[][] values = {{1, 2}, {3, 4}};

    module.setValue(field, values);
    Optional<float[][]> result = module.getMatrix(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo(values);
  }

  @Test
  public void callsGetMatrixProperlyMoreRows() {
    String field = "field";
    float[][] values = {{1, 2}, {3, 4}, {5, 6}};

    module.setValue(field, values);
    Optional<float[][]> result = module.getMatrix(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo(values);
  }

  @Test
  public void callsGetMatrixProperlyMoreCols() {
    String field = "field";
    float[][] values = {{1, 2, 3}, {4, 5, 6}};

    module.setValue(field, values);
    Optional<float[][]> result = module.getMatrix(field);

    assertThat(result.isPresent()).isTrue();
    assertThat(result.get()).isEqualTo(values);
  }

  @Test
  public void getsAllVariables() {
    List<Variable> vars = module.getVariables();

    Variable v1 = Variable.buildVariable().varType(Variable.VariableType.INPUT)
        .dataType(Variable.DataType.MATRIX).name("positions")
        .label("Positions within calculataed area").group("layoutarea").required("*").units("")
        .meta("").build();

    Variable v2 = Variable.buildVariable().varType(Variable.VariableType.OUTPUT)
        .dataType(Variable.DataType.MATRIX).name("convex_hull")
        .label("Convex hull bounding the region").group("layoutarea").required("*").units("")
        .meta("").build();

    Variable v3 = Variable.buildVariable().varType(Variable.VariableType.OUTPUT)
        .dataType(Variable.DataType.NUMBER).name("area").label("Area inside the convex hull")
        .group("layoutarea").required("*").units("").meta("").build();

    assertThat(vars).hasSize(3);
    assertThat(vars).containsExactly(v1, v2, v3);
  }

  @Test
  public void getAllModules() {
    List<ModuleSummary> modules = Module.getAvailableModules();

    assertThat(modules).isNotEmpty();
  }

  @Test
  public void executeWithHandler() throws Exception {
    Module m = moduleFactory.create("pvsamv1");
    populateModuleWithSimData(m);

    ExecutionHandler handler = new ExecutionHandler() {

      @Override
      public boolean handleLogMessage(MessageType type, float time, String message) {
        assertThat(type).isNotNull();
        return true;
      }

      @Override
      public boolean handleProgressUpdate(float percentComplete, float time, String text) {
        assertThat(percentComplete).isAtLeast(0f);
        assertThat(time).isAtLeast(0f);
        return true;
      }
    };

    m.execute(handler);

    assertThat(m.getNumber("annual_dc_gross").get()).isGreaterThan(0f);

    m.free();
  }

  @Test
  public void simpleExecute() throws Exception {
    Module m = moduleFactory.create("pvsamv1");
    populateModuleWithSimData(m);

    m.execute();

    assertThat(m.getNumber("annual_dc_gross").get()).isGreaterThan(0f);

    m.free();
  }

  private static void populateModuleWithSimData(Module m) throws Exception {
    String weatherFile = "target/test-classes/weather/tmy2/23129.tm2";
    m.setValue("solar_resource_file", weatherFile);
    m.setValue("albedo",
        new float[] {0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f});
    m.setValue("dcoptimizer_loss", 0);

    m.setValue("system_capacity", 1);
    m.setValue("modules_per_string", 14);
    m.setValue("strings_in_parallel", 10);
    m.setValue("inverter_count", 10);

    m.setValue("ac_loss", 0.1f);
    m.setValue("acwiring_loss", 0.015f);
    m.setValue("transformer_loss", 0.02f);

    for (int i = 1; i <= 4; i++) {
      m.setValue("subarray" + i + "_tilt", 20);
      m.setValue("subarray" + i + "_track_mode", 0);
      m.setValue("subarray" + i + "_azimuth", 180);
      m.setValue("subarray" + i + "_mod_orient", 1);
      m.setValue("subarray" + i + "_nmodx", 1);
      m.setValue("subarray" + i + "_nmody", 1);
      m.setValue("subarray" + i + "_shade_mode", 1);
      m.setValue("subarray" + i + "_dcloss", .1f);
      m.setValue("subarray" + i + "_dcwiring_loss", .015f);
      m.setValue("subarray" + i + "_tracking_loss", 0);
      m.setValue("subarray" + i + "_mismatch_loss", .04f);
      m.setValue("subarray" + i + "_nameplate_loss", -0.015f);
      m.setValue("subarray" + i + "_diodeconn_loss", 0);
      m.setValue("subarray" + i + "_soiling",
          new float[] {0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f});
    }

    m.setValue("module_model", 1);
    m.setValue("cec_t_noct", 65);
    m.setValue("cec_area", 0.67f);
    m.setValue("cec_n_s", 18);
    m.setValue("cec_i_sc_ref", 7.5f);
    m.setValue("cec_v_oc_ref", 10.4f);
    m.setValue("cec_i_mp_ref", 6.6f);
    m.setValue("cec_v_mp_ref", 8.4f);
    m.setValue("cec_alpha_sc", 0.003f);
    m.setValue("cec_beta_oc", -0.04f);
    m.setValue("cec_a_ref", 0.473f);
    m.setValue("cec_i_l_ref", 7.545f);
    m.setValue("cec_i_o_ref", (float) 1.94E-09);
    m.setValue("cec_r_s", 0.094f);
    m.setValue("cec_r_sh_ref", 15.72f);
    m.setValue("cec_adjust", 10.6f);
    m.setValue("cec_gamma_r", -0.5f);
    m.setValue("cec_temp_corr_mode", 0);
    m.setValue("cec_standoff", 1);
    m.setValue("cec_height", 0);

    m.setValue("inverter_model", 1);
    m.setValue("inv_ds_paco", 225);
    m.setValue("inv_ds_eff", .965f);
    m.setValue("inv_ds_pnt", .065f);
    m.setValue("inv_ds_pso", 250);
    m.setValue("inv_ds_vdco", 27);
    m.setValue("inv_ds_vdcmax", 48);
    m.setValue("mppt_low_inverter", 25);
    m.setValue("mppt_hi_inverter", 40);

    m.setValue("adjust:factor", 1);
    m.setValue("adjust:constant", 1);
  }
}

package io.github.spharris.ssc;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;

import javax.inject.Provider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.inject.Guice;
import com.google.inject.Inject;

@RunWith(JUnit4.class)
public class IntegrationTestSscModule {

  static final float EPSILON = 0f;
  
  @Inject SscModuleFactory moduleFactory;
  @Inject Provider<DataContainer> dataContainerProvider;

  SscModule module;
  DataContainer data;

  @Before
  public void createModule() {
    Guice.createInjector(new SscGuiceModule()).injectMembers(this);

    module = moduleFactory.create("layoutarea");
    data = dataContainerProvider.get();
  }

  @After
  public void freeResources() {
    module.free();
    data.free();
  }

  @Test
  public void getsAllVariables() {
    List<Variable> vars = module.getVariables();

    Variable v1 = Variable.builder().setVariableType(Variable.VariableType.INPUT)
        .setDataType(Variable.DataType.MATRIX).setName("positions")
        .setLabel("Positions within calculataed area").setGroup("layoutarea").setRequired("*").setUnits("")
        .setMeta("").build();

    Variable v2 = Variable.builder().setVariableType(Variable.VariableType.OUTPUT)
        .setDataType(Variable.DataType.MATRIX).setName("convex_hull")
        .setLabel("Convex hull bounding the region").setGroup("layoutarea").setRequired("*").setUnits("")
        .setMeta("").build();

    Variable v3 = Variable.builder().setVariableType(Variable.VariableType.OUTPUT)
        .setDataType(Variable.DataType.NUMBER).setName("area").setLabel("Area inside the convex hull")
        .setGroup("layoutarea").setRequired("*").setUnits("").setMeta("").build();

    assertThat(vars).hasSize(3);
    assertThat(vars).containsExactly(v1, v2, v3);
  }

  @Test
  public void getAllModules() {
    List<SscModuleSummary> modules = SscModule.getAvailableModules();

    assertThat(modules).isNotEmpty();
  }

  @Test
  public void executeWithHandler() throws Exception {
    SscModule m = moduleFactory.create("pvsamv1");
    populateModuleWithSimData(data);

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

    m.execute(data, handler);

    assertThat(data.getNumber("annual_dc_gross").get()).isGreaterThan(0f);

    m.free();
  }

  @Test
  public void simpleExecute() throws Exception {
    SscModule m = moduleFactory.create("pvsamv1");
    populateModuleWithSimData(data);

    m.execute(data);

    assertThat(data.getNumber("annual_dc_gross").get()).isGreaterThan(0f);

    m.free();
  }

  private static void populateModuleWithSimData(DataContainer data) throws Exception {
    String weatherFile = "target/test-classes/weather/tmy2/23129.tm2";
    data.setValue("solar_resource_file", weatherFile);
    data.setValue("albedo",
        new float[] {0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f});
    data.setValue("dcoptimizer_loss", 0);

    data.setValue("system_capacity", 1);
    data.setValue("modules_per_string", 14);
    data.setValue("strings_in_parallel", 10);
    data.setValue("inverter_count", 10);

    data.setValue("ac_loss", 0.1f);
    data.setValue("acwiring_loss", 0.015f);
    data.setValue("transformer_loss", 0.02f);

    for (int i = 1; i <= 4; i++) {
      data.setValue("subarray" + i + "_tilt", 20);
      data.setValue("subarray" + i + "_track_mode", 0);
      data.setValue("subarray" + i + "_azimuth", 180);
      data.setValue("subarray" + i + "_mod_orient", 1);
      data.setValue("subarray" + i + "_nmodx", 1);
      data.setValue("subarray" + i + "_nmody", 1);
      data.setValue("subarray" + i + "_shade_mode", 1);
      data.setValue("subarray" + i + "_dcloss", .1f);
      data.setValue("subarray" + i + "_dcwiring_loss", .015f);
      data.setValue("subarray" + i + "_tracking_loss", 0);
      data.setValue("subarray" + i + "_mismatch_loss", .04f);
      data.setValue("subarray" + i + "_nameplate_loss", -0.015f);
      data.setValue("subarray" + i + "_diodeconn_loss", 0);
      data.setValue("subarray" + i + "_soiling",
          new float[] {0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f});
    }

    data.setValue("module_model", 1);
    data.setValue("cec_t_noct", 65);
    data.setValue("cec_area", 0.67f);
    data.setValue("cec_n_s", 18);
    data.setValue("cec_i_sc_ref", 7.5f);
    data.setValue("cec_v_oc_ref", 10.4f);
    data.setValue("cec_i_mp_ref", 6.6f);
    data.setValue("cec_v_mp_ref", 8.4f);
    data.setValue("cec_alpha_sc", 0.003f);
    data.setValue("cec_beta_oc", -0.04f);
    data.setValue("cec_a_ref", 0.473f);
    data.setValue("cec_i_l_ref", 7.545f);
    data.setValue("cec_i_o_ref", (float) 1.94E-09);
    data.setValue("cec_r_s", 0.094f);
    data.setValue("cec_r_sh_ref", 15.72f);
    data.setValue("cec_adjust", 10.6f);
    data.setValue("cec_gamma_r", -0.5f);
    data.setValue("cec_temp_corr_mode", 0);
    data.setValue("cec_standoff", 1);
    data.setValue("cec_height", 0);

    data.setValue("inverter_model", 1);
    data.setValue("inv_ds_paco", 225);
    data.setValue("inv_ds_eff", .965f);
    data.setValue("inv_ds_pnt", .065f);
    data.setValue("inv_ds_pso", 250);
    data.setValue("inv_ds_vdco", 27);
    data.setValue("inv_ds_vdcmax", 48);
    data.setValue("mppt_low_inverter", 25);
    data.setValue("mppt_hi_inverter", 40);

    data.setValue("adjust:factor", 1);
    data.setValue("adjust:constant", 1);
  }
}

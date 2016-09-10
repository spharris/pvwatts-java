package io.github.spharris.pvwatts.service.v4;

import com.google.common.collect.ImmutableList;

import io.github.spharris.ssc.utils.InputVariable;
import io.github.spharris.ssc.utils.OutputVariable;
import io.github.spharris.ssc.utils.SscVariables;

class Variables {

  private Variables() {}

  public static InputVariable<String> SOLAR_RESOURCE_FILE = 
      SscVariables.stringInput("solar_resource_file");
  public static InputVariable<Float> SYSTEM_SIZE = SscVariables.numberInput("system_size");
  public static InputVariable<Float> DERATE = SscVariables.numberInput("derate");
  public static InputVariable<Float> TRACK_MODE = SscVariables.numberInput("track_mode");
  public static InputVariable<Float> AZIMUTH = SscVariables.numberInput("azimuth");
  public static InputVariable<Float> TILT = SscVariables.numberInput("tilt");
  public static InputVariable<Float> TILT_EQ_LAT = SscVariables.numberInput("tilt_eq_lat");
  public static InputVariable<Float> INOCT = SscVariables.numberInput("inoct");
  public static InputVariable<Float> GAMMA = SscVariables.numberInput("gamma");
  public static InputVariable<Float> ADJUST_CONSTANT = SscVariables.numberInput("adjust:constant");
  
  public static OutputVariable<ImmutableList<Float>> POA_MONTHLY = 
      SscVariables.arrayOutput("poa_monthly");
  public static OutputVariable<ImmutableList<Float>> SOLRAD_MONTHLY = 
      SscVariables.arrayOutput("solrad_monthly");
  public static OutputVariable<ImmutableList<Float>> DC_MONTHLY = 
      SscVariables.arrayOutput("dc_monthly");
  public static OutputVariable<ImmutableList<Float>> AC_MONTHLY = 
      SscVariables.arrayOutput("ac_monthly");
  public static OutputVariable<ImmutableList<Float>> MONTHLY_ENERGY = 
      SscVariables.arrayOutput("monthly_energy");
  public static OutputVariable<Float> SOLRAD_ANNUAL = SscVariables.numberOutput("solrad_annual");
  public static OutputVariable<Float> AC_ANNUAL = SscVariables.numberOutput("ac_annual");
  public static OutputVariable<Float> ANNUAL_ENERGY = SscVariables.numberOutput("annual_energy");
  
  public static OutputVariable<String> LOCATION = SscVariables.stringOutput("location");
  public static OutputVariable<String> CITY = SscVariables.stringOutput("city");
  public static OutputVariable<String> STATE = SscVariables.stringOutput("state");
  public static OutputVariable<Float> LAT = SscVariables.numberOutput("lat");
  public static OutputVariable<Float> LON = SscVariables.numberOutput("lon");
  public static OutputVariable<Float> TZ = SscVariables.numberOutput("tz");
  public static OutputVariable<Float> ELEV = SscVariables.numberOutput("elev");
}

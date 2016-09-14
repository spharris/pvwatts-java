package io.github.spharris.pvwatts.service;

import com.google.common.collect.ImmutableList;

import io.github.spharris.ssc.utils.InputVariable;
import io.github.spharris.ssc.utils.SscVariable;
import io.github.spharris.ssc.utils.SscVariables;

/**
 * Input and output variables for the PVWatts4 SSC Module ("pvwattsv1")
 */
class Variables {

  private Variables() {}

  public static final InputVariable<String> SOLAR_RESOURCE_FILE = 
      SscVariables.stringInput("solar_resource_file");
    
  public static final InputVariable<Float> SYSTEM_SIZE = SscVariables.numberInput("system_size");
  public static final InputVariable<Float> DERATE = SscVariables.numberInput("derate");
  public static final InputVariable<Integer> TRACK_MODE = SscVariables.integerInput("track_mode");
  public static final InputVariable<Float> AZIMUTH = SscVariables.numberInput("azimuth");
  public static final InputVariable<Float> TILT = SscVariables.numberInput("tilt");
  public static final InputVariable<Integer> TILT_EQ_LAT = SscVariables.integerInput("tilt_eq_lat");
  public static final InputVariable<Float> INOCT = SscVariables.numberInput("inoct");
  public static final InputVariable<Float> GAMMA = SscVariables.numberInput("gamma");
  public static final InputVariable<Float> ADJUST_CONSTANT =
      SscVariables.numberInput("adjust:constant");
  
  public static final SscVariable<ImmutableList<Float>> POA_MONTHLY = 
      SscVariables.arrayOutput("poa_monthly");
  public static final SscVariable<ImmutableList<Float>> SOLRAD_MONTHLY = 
      SscVariables.arrayOutput("solrad_monthly");
  public static final SscVariable<ImmutableList<Float>> DC_MONTHLY = 
      SscVariables.arrayOutput("dc_monthly");
  public static final SscVariable<ImmutableList<Float>> AC_MONTHLY = 
      SscVariables.arrayOutput("ac_monthly");
  public static final SscVariable<ImmutableList<Float>> MONTHLY_ENERGY = 
      SscVariables.arrayOutput("monthly_energy");
  public static final SscVariable<Float> SOLRAD_ANNUAL = 
      SscVariables.numberOutput("solrad_annual");
  public static final SscVariable<Float> AC_ANNUAL = SscVariables.numberOutput("ac_annual");
  public static final SscVariable<Float> ANNUAL_ENERGY = 
      SscVariables.numberOutput("annual_energy");
  public static final SscVariable<ImmutableList<Float>> AC = SscVariables.arrayOutput("ac");
  public static final SscVariable<ImmutableList<Float>> POA = SscVariables.arrayOutput("poa");
  public static final SscVariable<ImmutableList<Float>> DN = SscVariables.arrayOutput("dn");
  public static final SscVariable<ImmutableList<Float>> DC = SscVariables.arrayOutput("dc");
  public static final SscVariable<ImmutableList<Float>> DF = SscVariables.arrayOutput("df");
  public static final SscVariable<ImmutableList<Float>> TAMB = SscVariables.arrayOutput("tamb");
  public static final SscVariable<ImmutableList<Float>> TCELL =
      SscVariables.arrayOutput("tcell");
  public static final SscVariable<ImmutableList<Float>> WSPD = SscVariables.arrayOutput("wspd");
  
  public static final SscVariable<String> LOCATION = SscVariables.stringOutput("location");
  public static final SscVariable<String> CITY = SscVariables.stringOutput("city");
  public static final SscVariable<String> STATE = SscVariables.stringOutput("state");
  public static final SscVariable<Float> LAT = SscVariables.numberOutput("lat");
  public static final SscVariable<Float> LON = SscVariables.numberOutput("lon");
  public static final SscVariable<Float> TZ = SscVariables.numberOutput("tz");
  public static final SscVariable<Float> ELEV = SscVariables.numberOutput("elev");
}

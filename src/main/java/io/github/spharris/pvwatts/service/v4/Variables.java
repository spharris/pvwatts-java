package io.github.spharris.pvwatts.service.v4;

import com.google.common.collect.ImmutableList;

import io.github.spharris.ssc.utils.InputVariable;
import io.github.spharris.ssc.utils.OutputVariable;
import io.github.spharris.ssc.utils.SscVariable;
import io.github.spharris.ssc.utils.SscVariables;

class Variables {

  private Variables() {}

  public static final InputVariable<String> SOLAR_RESOURCE_FILE = 
      SscVariables.stringInput("solar_resource_file");
  public static final InputVariable<Integer> RADIUS = 
      SscVariables.integerInput("radius");
  
  public static final InputVariable<Float> SYSTEM_SIZE = SscVariables.numberInput("system_size");
  public static final InputVariable<Float> DERATE = SscVariables.numberInput("derate");
  public static final InputVariable<Integer> TRACK_MODE = SscVariables.integerInput("track_mode");
  public static final InputVariable<Float> AZIMUTH = SscVariables.numberInput("azimuth");
  public static final InputVariable<Float> TILT = SscVariables.numberInput("tilt");
  public static final InputVariable<Integer> TILT_EQ_LAT = SscVariables.integerInput("tilt_eq_lat");
  public static final InputVariable<Float> INOCT = SscVariables.numberInput("inoct");
  public static final InputVariable<Float> GAMMA = SscVariables.numberInput("gamma");
  public static final InputVariable<Float> ADJUST_CONSTANT = SscVariables.numberInput("adjust:constant");
  
  public static final OutputVariable<ImmutableList<Float>> POA_MONTHLY = 
      SscVariables.arrayOutput("poa_monthly");
  public static final OutputVariable<ImmutableList<Float>> SOLRAD_MONTHLY = 
      SscVariables.arrayOutput("solrad_monthly");
  public static final OutputVariable<ImmutableList<Float>> DC_MONTHLY = 
      SscVariables.arrayOutput("dc_monthly");
  public static final OutputVariable<ImmutableList<Float>> AC_MONTHLY = 
      SscVariables.arrayOutput("ac_monthly");
  public static final OutputVariable<ImmutableList<Float>> MONTHLY_ENERGY = 
      SscVariables.arrayOutput("monthly_energy");
  public static final OutputVariable<Float> SOLRAD_ANNUAL = SscVariables.numberOutput("solrad_annual");
  public static final OutputVariable<Float> AC_ANNUAL = SscVariables.numberOutput("ac_annual");
  public static final OutputVariable<Float> ANNUAL_ENERGY = SscVariables.numberOutput("annual_energy");
  
  public static final OutputVariable<String> LOCATION = SscVariables.stringOutput("location");
  public static final OutputVariable<String> CITY = SscVariables.stringOutput("city");
  public static final OutputVariable<String> STATE = SscVariables.stringOutput("state");
  public static final OutputVariable<Float> LAT = SscVariables.numberOutput("lat");
  public static final OutputVariable<Float> LON = SscVariables.numberOutput("lon");
  public static final OutputVariable<Float> TZ = SscVariables.numberOutput("tz");
  public static final OutputVariable<Float> ELEV = SscVariables.numberOutput("elev");
  
  public static final ImmutableList<SscVariable> INPUT_VARIABLES = ImmutableList.<SscVariable>of(
        SOLAR_RESOURCE_FILE, RADIUS, SYSTEM_SIZE, DERATE, TRACK_MODE, AZIMUTH, TILT, TILT_EQ_LAT,
        INOCT, GAMMA, ADJUST_CONSTANT);
  
  public static final ImmutableList<SscVariable> OUTPUT_VARIABLES = ImmutableList.<SscVariable>of( 
      POA_MONTHLY, SOLRAD_MONTHLY, DC_MONTHLY, AC_MONTHLY, MONTHLY_ENERGY, SOLRAD_ANNUAL,
      AC_ANNUAL, ANNUAL_ENERGY, LOCATION, CITY, STATE, LAT, LON, TZ, ELEV);
  
  public static final ImmutableList<SscVariable> ALL_VARIABLES =
      ImmutableList.<SscVariable>builder()
        .addAll(INPUT_VARIABLES)
        .addAll(OUTPUT_VARIABLES)
        .build();
}

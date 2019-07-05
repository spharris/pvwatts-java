package io.github.spharris.pvwatts.web;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import io.github.spharris.pvwatts.service.Annotations.WeatherDirectory;
import io.github.spharris.pvwatts.web.Annotations.Port;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

final class PvWattsFlagsModule extends AbstractModule {

  private static final ImmutableMap<String, String> DEFAULTS =
      ImmutableMap.<String, String>builder()
          .put("port", "3000")
          .put("weather_directory", "weather/")
          .build();

  private CommandLine flags;

  PvWattsFlagsModule(String[] args) throws ParseException {
    flags = createFlags(args);
  }

  @Override
  protected void configure() {
    bind(Key.get(Integer.class, Port.class)).toInstance(Integer.valueOf(getFlagValue("port")));
    bind(Key.get(String.class, WeatherDirectory.class))
        .toInstance(flags.getOptionValue("weather_directory"));
  }

  private String getFlagValue(String flagName) {
    String flagValue = flags.getOptionValue(flagName);
    return flagValue != null ? flagValue : DEFAULTS.get(flagName);
  }

  private static CommandLine createFlags(String[] args) throws ParseException {
    Options helpDef =
        new Options()
            .addOption(Option.builder("help").longOpt("help").desc("Print this message").build());

    Options flagDef =
        new Options()
            .addOption(Option.builder("help").longOpt("help").desc("Print this message").build())
            .addOption(
                Option.builder("port")
                    .longOpt("port")
                    .desc("The HTTP port to run the server on")
                    .hasArg()
                    .argName("port")
                    .build())
            .addOption(
                Option.builder("weather_directory")
                    .longOpt("weather_directory")
                    .desc("The directory where weather data is stored")
                    .hasArg()
                    .argName("directory")
                    .build());

    CommandLine helpFlag = new DefaultParser().parse(helpDef, args, true);
    if (helpFlag.hasOption("help")) {
      new HelpFormatter().printHelp("pvwatts-java", flagDef, true);
      System.exit(0);
    }

    return new DefaultParser().parse(flagDef, args);
  }
}

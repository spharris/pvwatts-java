package io.github.spharris.pvwatts.service.weather;

import com.google.common.base.Optional;

/**
 * An interface for classes that retrieve a specified weather file or a given dataset. 
 */
interface WeatherRetriever {
  
  /**
   * Returns the local path of the specified weather file name, if it exists. Otherwise, returns
   * {@link Optional#absent}.
   */
  Optional<String> retrieveFile(String fileName);
}

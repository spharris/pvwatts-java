package io.github.spharris.ssc;

/** An interface for an SSC resource that needs to be freed */
public interface Freeable {

  /**
   * Frees the underlying pointers associated with this resource. Subsequent calls to other resource
   * functions will result in an {@link java.lang.IllegalStateException}
   */
  void free();

  boolean isFreed();
}

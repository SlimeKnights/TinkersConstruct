package slimeknights.tconstruct.common;

import javax.annotation.Nonnull;

/**
 * Contains base helpers for all Tinker modules
 */
public class TinkerModule {
  /**
   * This is a function that returns null, despite being nonnull. It is used on object holder fields to remove IDE warnings about constant null as it will be nonnull
   * @param <T>  Field type
   * @return  Null
   */
  @Nonnull
  @SuppressWarnings("ConstantConditions")
  public static <T> T injected() {
    return null;
  }
}

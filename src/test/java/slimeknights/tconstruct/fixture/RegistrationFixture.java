package slimeknights.tconstruct.fixture;

import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.tconstruct.test.TestHelper;

/** Helpers for generic registration tasks */
public class RegistrationFixture {
  /** Registers an object to a registry without risk of tests failing if its registered already */
  public static <T> void register(GenericLoaderRegistry<? super T> registry, String name, RecordLoadable<T> value) {
    try {
      registry.register(TestHelper.id(name), value);
    } catch (Exception e) {
      // no-op
    }
  }
}

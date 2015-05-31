package tconstruct;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

/**
 * This class contains all the base functions for server and clientside proxy that should be called.
 * Can be used when no specific handling is needed. Can be replaced with a specific implementation at any time.
 *
 * Also doubles as documentation when what should happen.
 */
public class CommonProxy {
  public void registerModels() {
    if(Loader.instance().hasReachedState(LoaderState.INITIALIZATION))
      TConstruct.log.error("Proxy.registerModels has to be called during preInit. Otherwise the models wont be found on first load.");
  }
}

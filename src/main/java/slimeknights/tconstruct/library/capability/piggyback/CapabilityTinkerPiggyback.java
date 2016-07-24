package slimeknights.tconstruct.library.capability.piggyback;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

public class CapabilityTinkerPiggyback {

  @CapabilityInject(ITinkerPiggyback.class)
  public static Capability<ITinkerPiggyback> PIGGYBACK = null;

  private CapabilityTinkerPiggyback() {
  }

  public static void register() {
    CapabilityManager.INSTANCE.register(ITinkerPiggyback.class, null, new Callable<ITinkerPiggyback>() {
      @Override
      public ITinkerPiggyback call() throws Exception {
        return new TinkerPiggybackHandler();
      }
    });
  }
}

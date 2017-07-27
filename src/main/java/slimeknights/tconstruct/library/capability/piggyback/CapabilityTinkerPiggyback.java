package slimeknights.tconstruct.library.capability.piggyback;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import java.util.concurrent.Callable;

public class CapabilityTinkerPiggyback implements Capability.IStorage<ITinkerPiggyback>  {

  @CapabilityInject(ITinkerPiggyback.class)
  public static Capability<ITinkerPiggyback> PIGGYBACK = null;

  private static final CapabilityTinkerPiggyback INSTANCE = new CapabilityTinkerPiggyback();

  private CapabilityTinkerPiggyback() {
  }

  public static void register() {
    CapabilityManager.INSTANCE.register(ITinkerPiggyback.class, INSTANCE, new Callable<ITinkerPiggyback>() {
      @Override
      public ITinkerPiggyback call() throws Exception {
        return new TinkerPiggybackHandler();
      }
    });
  }

  @Override
  public NBTBase writeNBT(Capability<ITinkerPiggyback> capability, ITinkerPiggyback instance, EnumFacing side) {
    return null;
  }

  @Override
  public void readNBT(Capability<ITinkerPiggyback> capability, ITinkerPiggyback instance, EnumFacing side, NBTBase nbt) {

  }
}

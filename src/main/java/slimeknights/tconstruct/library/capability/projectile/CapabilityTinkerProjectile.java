package slimeknights.tconstruct.library.capability.projectile;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

public class CapabilityTinkerProjectile implements Capability.IStorage<ITinkerProjectile> {

  @CapabilityInject(ITinkerProjectile.class)
  public static Capability<ITinkerProjectile> PROJECTILE_CAPABILITY = null;

  private static final CapabilityTinkerProjectile INSTANCE = new CapabilityTinkerProjectile();

  private CapabilityTinkerProjectile() {
  }

  public static void register() {
    CapabilityManager.INSTANCE.register(ITinkerProjectile.class, INSTANCE, new Callable<ITinkerProjectile>() {
      @Override
      public ITinkerProjectile call() throws Exception {
        return new TinkerProjectileHandler();
      }
    });
  }

  @Override
  public NBTBase writeNBT(Capability<ITinkerProjectile> capability, ITinkerProjectile instance, EnumFacing side) {
    return null;
  }

  @Override
  public void readNBT(Capability<ITinkerProjectile> capability, ITinkerProjectile instance, EnumFacing side, NBTBase nbt) {

  }
}

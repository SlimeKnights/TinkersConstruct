package slimeknights.tconstruct.compat.neoforged.neoforge.capabilities;

import net.minecraft.core.Direction;
import slimeknights.mantle.compat.neoforged.neoforge.capabilities.Capability;
import slimeknights.mantle.compat.neoforged.neoforge.common.util.LazyOptional;

/** Compatibility shim for old serializable capability providers. */
public interface ICapabilitySerializable<T> {
  <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side);

  T serializeNBT();

  void deserializeNBT(T nbt);
}

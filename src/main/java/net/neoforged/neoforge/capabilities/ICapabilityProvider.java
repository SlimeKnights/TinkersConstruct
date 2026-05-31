package slimeknights.tconstruct.compat.neoforged.neoforge.capabilities;

import net.minecraft.core.Direction;
import slimeknights.mantle.compat.neoforged.neoforge.capabilities.Capability;
import slimeknights.mantle.compat.neoforged.neoforge.common.util.LazyOptional;

/** Compatibility shim for old Forge capability providers. */
public interface ICapabilityProvider {
  <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side);
}

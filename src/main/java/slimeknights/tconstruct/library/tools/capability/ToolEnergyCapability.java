package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.ToolCapabilityProvider.IToolCapabilityProvider;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.CapacityStat;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.function.Supplier;

/** Standard implementation of energy capability on a tool. Not currently used in the mod directly, but should help addons have more unity. */
public record ToolEnergyCapability(Supplier<? extends IToolStackView> tool) implements IEnergyStorage {
  /** Format string to display energy amounts, used internally by the stat */
  public static final String ENERGY_FORMAT = TConstruct.makeDescriptionId("tool_stat", "energy");
  /** Stat marking the max capacity */
  public static final CapacityStat MAX_STAT = ToolStats.register(new CapacityStat(new ToolStatId(TConstruct.MOD_ID, "max_energy"), 0xa00000, ENERGY_FORMAT));
  /** Persistent data key for fetching the current energy */
  public static final ResourceLocation ENERGY_KEY = TConstruct.getResource("energy");

  /** Gets the energy capacity for the given tool */
  public static int getMaxEnergy(IToolStackView tool) {
    return tool.getStats().getInt(MAX_STAT);
  }

  /** Gets the current energy on a tool */
  public static int getEnergy(IToolStackView tool) {
    return tool.getPersistentData().getInt(ENERGY_KEY);
  }

  /** Internal method to set energy, skips a capacity check */
  private static void setEnergyRaw(IToolStackView tool, int energy) {
    if (energy == 0) {
      tool.getPersistentData().remove(ENERGY_KEY);
    } else {
      tool.getPersistentData().putInt(ENERGY_KEY, energy);
    }
  }

  /** Sets the energy on the tool */
  public static void setEnergy(IToolStackView tool, int energy) {
    setEnergyRaw(tool, Mth.clamp(energy, 0, getMaxEnergy(tool)));
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (maxReceive <= 0) {
      return 0;
    }
    IToolStackView tool = this.tool.get();
    int current = getEnergy(tool);
    int filled = Math.min(getMaxEnergy(tool) - current, maxReceive);
    if (!simulate) {
      setEnergyRaw(tool, current + filled);
    }
    return filled;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if (maxExtract <= 0) {
      return 0;
    }
    IToolStackView tool = this.tool.get();
    int current = getEnergy(tool);
    if (current <= 0) {
      return 0;
    }
    int drained = maxExtract;
    if (current < drained) {
      drained = current;
    }
    if (!simulate) {
      setEnergyRaw(tool, current - drained);
    }
    return drained;
  }

  @Override
  public int getEnergyStored() {
    return getEnergy(tool.get());
  }

  @Override
  public int getMaxEnergyStored() {
    return getMaxEnergy(tool.get());
  }

  @Override
  public boolean canExtract() {
    return true;
  }

  @Override
  public boolean canReceive() {
    return true;
  }

  /** Provider instance for a fluid cap */
  public static class Provider implements IToolCapabilityProvider {
    private final LazyOptional<IEnergyStorage> energyCap;
    public Provider(Supplier<? extends IToolStackView> toolStack) {
      this.energyCap = LazyOptional.of(() -> new ToolEnergyCapability(toolStack));
    }

    @Override
    public <T> LazyOptional<T> getCapability(IToolStackView tool, Capability<T> cap) {
      if (cap == ForgeCapabilities.ENERGY && tool.getStats().getInt(MAX_STAT) > 0) {
        return energyCap.cast();
      }
      return LazyOptional.empty();
    }
  }
}

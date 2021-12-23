package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.BiFunction;

/**
 * Read only view of {@link ModDataNBT}
 */
public interface IModDataReadOnly extends INamespacedNBTReadOnly {
  /** Empty variant of mod data */
  IModDataReadOnly EMPTY = new IModDataReadOnly() {
    @Override
    public int getSlots(SlotType type) {
      return 0;
    }

    @Override
    public <T> T get(ResourceLocation name, BiFunction<CompoundNBT,String,T> function) {
      return function.apply(new CompoundNBT(), name.toString());
    }

    @Override
    public boolean contains(ResourceLocation name, int type) {
      return false;
    }
  };

  /**
   * Gets the number of slots provided by this data
   * @param type  Type of slot to get
   * @return  Number of slots
   */
  int getSlots(SlotType type);


  /* Deprecated, to remove */

  /** @deprecated Use {@link #getSlots(SlotType)} */
  @Deprecated
  default int getUpgrades() {
    return getSlots(SlotType.UPGRADE);
  }

  /** @deprecated Use {@link #getSlots(SlotType)} */
  @Deprecated
  default int getAbilities() {
    return getSlots(SlotType.ABILITY);
  }
}

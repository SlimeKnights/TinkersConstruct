package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.tools.SlotType;

import java.util.function.BiFunction;

/**
 * Read only view of {@link ModDataNBT}
 */
public interface IModDataView extends INamespacedNBTView {
  /** Empty variant of mod data */
  IModDataView EMPTY = new IModDataView() {
    @Override
    public int getSlots(SlotType type) {
      return 0;
    }

    @Override
    public <T> T get(ResourceLocation name, BiFunction<CompoundTag,String,T> function) {
      return function.apply(new CompoundTag(), name.toString());
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
}

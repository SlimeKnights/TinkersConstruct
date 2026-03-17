package slimeknights.tconstruct.library.materials.stats;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;

import java.util.List;

/**
 * Material stats that support repairing, requires durability as part of the stats
 */
public interface IRepairableMaterialStats extends IMaterialStats {
  LoadableField<Integer,IRepairableMaterialStats> DURABILITY_FIELD = IntLoadable.FROM_ONE.requiredField("durability", IRepairableMaterialStats::durability);

  /**
   * Gets the amount of durability for this stat type
   * @return  Durability
   */
  int durability();

  /** Helper for implementing the scaled version of localized info, until we migrate to make that default. TODO 1.21: remove. */
  interface ScaledTooltip extends IRepairableMaterialStats {
    @Override
    List<Component> getLocalizedInfo(float scale);

    @Override
    default List<Component> getLocalizedInfo() {
      return getLocalizedInfo(1);
    }
  }
}

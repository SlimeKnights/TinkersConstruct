package slimeknights.tconstruct.tools.stats;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.modules.capacity.OverslimeModule;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

/**
 * Stat type for slime on armor. Intended to be used for all 4 pieces using the factor. See {@link slimeknights.tconstruct.tools.modules.ArmorModuleBuilder#MAX_DAMAGE_ARRAY} for factors.
 * @param durability
 * @param overslime
 */
public record SlimeStats(int durability, int overslime) implements IRepairableMaterialStats.ScaledTooltip {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("slime"));
  public static final MaterialStatType<SlimeStats> TYPE = new MaterialStatType<>(ID, new SlimeStats(1, 0), RecordLoadable.create(
    IRepairableMaterialStats.DURABILITY_FIELD,
    IntLoadable.FROM_ZERO.requiredField("overslime_capacity", SlimeStats::overslime),
    SlimeStats::new));
  private static final List<Component> DESCRIPTION = List.of(
    ToolStats.DURABILITY.getDescription(),
    OverslimeModule.OVERSLIME_STAT.getDescription());

  @Override
  public MaterialStatType<?> getType() {
    return TYPE;
  }

  @Override
  public void apply(ModifierStatsBuilder builder, float scale) {
    ToolStats.DURABILITY.update(builder, durability * scale); // TODO: may want to rename this to durability factor
    OverslimeModule.OVERSLIME_STAT.add(builder, durability * scale);
  }

  @Override
  public List<Component> getLocalizedInfo(float scale) {
    return List.of(
      ToolStats.DURABILITY.formatValue(durability * scale),
      OverslimeModule.OVERSLIME_STAT.formatValue(overslime * scale)
    );
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }
}

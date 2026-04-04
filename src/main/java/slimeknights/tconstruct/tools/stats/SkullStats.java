package slimeknights.tconstruct.tools.stats;

import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

/** Stats for slimeskull skulls. TODO 1.21: merge into {@link RepairStats} */
public record SkullStats(int durability, int armor) implements IRepairableMaterialStats.ScaledTooltip {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("skull"));
  public static final MaterialStatType<SkullStats> TYPE = new MaterialStatType<>(ID, new SkullStats(1, 0), RecordLoadable.create(
    IRepairableMaterialStats.DURABILITY_FIELD,
    IntLoadable.FROM_ZERO.defaultField("armor", 0, false, SkullStats::armor),
    SkullStats::new));
  // tooltip descriptions
  private static final List<Component> ARMOR_DESC = List.of(RepairStats.REPAIR_DESC, ToolStats.ARMOR.getDescription());
  private static final List<Component> NO_ARMOR_DESC = List.of(RepairStats.REPAIR_DESC);

  /** @deprecated use {@link #SkullStats(int)} */
  @Deprecated(forRemoval = true)
  public SkullStats {}

  public SkullStats(int durability) {
    this(durability, 0);
  }

  @Override
  public MaterialStatType<?> getType() {
    return TYPE;
  }

  @Override
  public List<Component> getLocalizedInfo(float scale) {
    Component durability = IToolStat.formatNumber(RepairStats.REPAIR_KEY, ToolStats.DURABILITY.getColor(), (int)(this.durability * scale / MaterialRecipe.INGOTS_PER_REPAIR));
    if (armor > 0) {
      return List.of(durability, ToolStats.ARMOR.formatValue(this.armor * scale));
    }
    return List.of(durability);
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return armor > 0 ? ARMOR_DESC : NO_ARMOR_DESC;
  }

  @Override
  public void apply(ModifierStatsBuilder builder, float scale) {
    ToolStats.ARMOR.update(builder, armor * scale);
  }
}

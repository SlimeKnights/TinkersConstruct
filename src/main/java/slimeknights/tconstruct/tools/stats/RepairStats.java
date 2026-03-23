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

/** Tool stats that simply allow repairing with this material, optionally granting its durability as a bonus. */
public record RepairStats(MaterialStatType<?> getType, int durability) implements IRepairableMaterialStats.ScaledTooltip {
  static final String REPAIR_KEY = TConstruct.makeTranslationKey("tool_stat", "durability.repair");
  static final Component REPAIR_DESC = TConstruct.makeTranslation("tool_stat", "durability.repair.description");
  private static final List<Component> DESCRIPTION = List.of(REPAIR_DESC);
  private static final RecordLoadable<RepairStats> LOADABLE = RecordLoadable.create(MaterialStatType.CONTEXT_KEY.requiredField(), IntLoadable.FROM_ONE.requiredField("repair_amount", IRepairableMaterialStats::durability), RepairStats::new);
  /* Stat types */
  /** Type for shell on slimeshell */
  public static final MaterialStatType<RepairStats> SHELL = makeType("shell");
  /** Type for laces on slime boots */
  public static final MaterialStatType<RepairStats> LACES = makeType("laces");

  @Override
  public List<Component> getLocalizedInfo(float scale) {
    return List.of(IToolStat.formatNumber(REPAIR_KEY, ToolStats.DURABILITY.getColor(), (int)(this.durability * scale / MaterialRecipe.INGOTS_PER_REPAIR)));
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  @Override
  public void apply(ModifierStatsBuilder builder, float scale) {
    // does not add durability as a stat, just uses it for repairing
  }

  /** Makes a stat type for the given ID */
  public static MaterialStatType<RepairStats> makeType(MaterialStatsId id) {
    return new MaterialStatType<RepairStats>(id, type -> new RepairStats(type, 1), LOADABLE);
  }

  /** Makes a stat type for the given name */
  private static MaterialStatType<RepairStats> makeType(String name) {
    return makeType(new MaterialStatsId(TConstruct.MOD_ID, name));
  }


  /* Constructors */

  /** Creates a new instance of the shell type */
  public static RepairStats shell(int durability) {
    return new RepairStats(SHELL, durability);
  }

  /** Creates a new instance of the laces type */
  public static RepairStats laces(int durability) {
    return new RepairStats(LACES, durability);
  }
}

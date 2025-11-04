package slimeknights.tconstruct.tools.stats;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

/** Shared code for material stats types with no stats */
public enum StatlessMaterialStats implements IMaterialStats {
  /** Bindings for melee and harvest tools. Generally supports all head or handle materials, plus strings and vines. */
  BINDING("binding"),
  /** Strings for ranged weapons. Generally supports strings and vines. */
  BOWSTRING("bowstring"),
  /** Inner layer for armor. Generally supports all plating materials plus leather and vines. */
  MAILLE("maille"),
  /** Base for shields, generally supports all woods. */
  SHIELD_CORE("shield_core"),
  /** Internal stat type that forces a repair kit to appear. Repair kits will also show if any repairable stat type is present. */
  REPAIR_KIT("repair_kit"),
  /** Leather part for travelers gear. Generally supports leather and leather composites. */
  CUIRASS("cuirass"),

  // ammo
  /** Stat type for the heads of arrows, shurikens, and throwing axes. */
  ARROW_HEAD("arrow_head"),
  /** Stat type shaft of arrows and throwing axes. */
  ARROW_SHAFT("arrow_shaft"),
  /** Stat type for the fletching of arrows, typically have negative traits. */
  FLETCHING("fletching");

  private static final List<Component> LOCALIZED = List.of(IMaterialStats.makeTooltip(TConstruct.getResource("extra.no_stats")));
  private static final List<Component> DESCRIPTION = List.of(Component.empty());
  @Getter
  private final MaterialStatType<StatlessMaterialStats> type;

  // no stats

  StatlessMaterialStats(String name) {
    this.type = MaterialStatType.singleton(new MaterialStatsId(TConstruct.getResource(name)), this);
  }

  @Override
  public List<Component> getLocalizedInfo() {
    return LOCALIZED;
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  @Override
  public void apply(ModifierStatsBuilder builder, float scale) {}
}

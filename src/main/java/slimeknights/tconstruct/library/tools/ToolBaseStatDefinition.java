package slimeknights.tconstruct.library.tools;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;
import java.util.Set;

/**
 * @deprecated Use {@link slimeknights.tconstruct.library.tools.definition.ToolDefinitionData}
 */
@Deprecated
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public final class ToolBaseStatDefinition {
  private final ToolDefinitionData data;

  /* General */

  /** @deprecated use {@link PartRequirement#getWeight()} */
  @Deprecated
  public int getPrimaryHeadWeight() {
    List<PartRequirement> parts = data.getParts();
    if (parts.isEmpty()) {
      return 1;
    }
    return parts.get(0).getWeight();
  }

  /** @deprecated use {@link ToolDefinitionData#getStartingSlots(SlotType)} */
  @Deprecated
  public int getStartingSlots(SlotType type) {
    return data.getStartingSlots(type);
  }

  /** @deprecated use {@link ToolDefinitionData#getAllBaseStats()} */
  @Deprecated
  public Set<FloatToolStat> getAllBonuses() {
    return data.getAllBaseStats();
  }

  /** @deprecated use {@link ToolDefinitionData#getBonus(FloatToolStat)} */
  @Deprecated
  public float getBonus(FloatToolStat stat) {
    return data.getBonus(stat);
  }

  /** @deprecated use {@link ToolDefinitionData#getMultiplier(FloatToolStat)} */
  @Deprecated
  public float getModifier(FloatToolStat stat) {
    return data.getMultiplier(stat);
  }

  /** @deprecated use {@link ToolDefinitionData#buildStatMultipliers(ModifierStatsBuilder)} */
  public void buildStats(ModifierStatsBuilder builder) {
    data.buildStatMultipliers(builder);
  }

  /** @deprecated use {@link ToolDefinitionData#buildSlots(ModDataNBT)} */
  public void buildSlots(ModDataNBT persistentModData) {
    data.buildSlots(persistentModData);
  }


  /* Even more deprecated than the other deprecations in this class, like sooo deprecated */

  /** @deprecated Use {@link ToolDefinitionData#getStartingSlots(SlotType)} */
  @Deprecated
  public int getDefaultUpgrades() {
    return getStartingSlots(SlotType.UPGRADE);
  }

  /** @deprecated Use {@link ToolDefinitionData#getStartingSlots(SlotType)} */
  @Deprecated
  public int getDefaultAbilities() {
    return getStartingSlots(SlotType.ABILITY);
  }

  /** @deprecated Use {@link ToolDefinitionData#getStartingSlots(SlotType)} */
  @Deprecated
  public int getDefaultTraits() {
    return getStartingSlots(SlotType.SOUL);
  }
}

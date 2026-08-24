package slimeknights.tconstruct.test;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.ToolDefinitionData;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.DummyToolStack;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.MultiplierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

/** Helpers for running tests */
public class TestHelper {
  /** Dummy mod ID for tests */
  public static final String MOD_ID = "test";

  private TestHelper() {}

  /** Creates a resource location under {@link #MOD_ID} */
  @SuppressWarnings("removal")
  public static ResourceLocation id(String name) {
    return new ResourceLocation(MOD_ID, name);
  }

  /** Helper to fetch traits from the trait hook */
  public static List<ModifierEntry> getTraits(ToolDefinitionData data) {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    data.getHook(ToolHooks.TOOL_TRAITS).addTraits(ToolDefinition.EMPTY, MaterialNBT.EMPTY, builder);
    return builder.build().getModifiers();
  }

  public record ToolDefinitionStats(StatsNBT base, MultiplierNBT multipliers) {}

  /** Computes the stats for the given tool */
  public static ToolDefinitionStats buildStats(ToolDefinitionData data) {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    data.getHook(ToolHooks.TOOL_STATS).addToolStats(new DummyToolStack(Items.AIR, ModifierNBT.EMPTY, new ModDataNBT()), builder);
    MultiplierNBT multipliers = builder.buildMultipliers();
    // cancel out multipliers on the base stats, as people expect base stats to be comparable to be usable in the modifier stats builder
    for (INumericToolStat<?> stat : multipliers.getContainedStats()) {
      stat.multiply(builder, 1 / multipliers.get(stat));
    }
    return new ToolDefinitionStats(builder.build(), multipliers);
  }
}

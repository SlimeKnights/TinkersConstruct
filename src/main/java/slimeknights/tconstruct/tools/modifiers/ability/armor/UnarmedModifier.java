package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.item.Item;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.internal.OffhandAttackModifier;

public class UnarmedModifier extends OffhandAttackModifier {
  public UnarmedModifier() {
    super(0xAA7D66);
  }

  @Override
  public void addToolStats(Item item, ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 0.6f);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }
}

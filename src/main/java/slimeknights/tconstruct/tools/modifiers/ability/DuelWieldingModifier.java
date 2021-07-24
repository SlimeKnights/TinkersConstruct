package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.item.Item;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.internal.OffhandAttackModifier;

public class DuelWieldingModifier extends OffhandAttackModifier {
  public DuelWieldingModifier() {
    super(0xA6846A);
  }

  @Override
  public void addToolStats(Item item, ToolDefinition toolDefinition, StatsNBT baseStats, IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ModifierStatsBuilder builder) {
    // on two handed tools, take a larger hit to attack damage, smaller to attack speed
    if (item.isIn(TinkerTags.Items.TWO_HANDED)) {
      ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 0.7);
      ToolStats.ATTACK_SPEED.multiplyAll(builder, 0.9);
    } else {
      // on one handed tools, 80% on both
      ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 0.8);
      ToolStats.ATTACK_SPEED.multiplyAll(builder, 0.8);
    }
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }
}

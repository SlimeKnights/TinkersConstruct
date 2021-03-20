package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.HarvestLevels;
import slimeknights.tconstruct.tools.ToolStatsModifierBuilder;

public class EmeraldModifier extends SingleUseModifier {
  public EmeraldModifier() {
    super(0x41f384);
  }

  @Override
  public void addToolStats(IModDataReadOnly persistentData, IModDataReadOnly volatileData, int level, ToolStatsModifierBuilder builder) {
    builder.multiplyDurability(1.5f);
    builder.setHarvestLevel(HarvestLevels.IRON);
  }

  @Override
  public float applyLivingDamage(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float baseDamage, float damage, boolean isCritical, boolean fullyCharged) {
    if (target.getCreatureAttribute() == CreatureAttribute.ILLAGER) {
      damage += 2.5f;
    }
    return damage;
  }
}

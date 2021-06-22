package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

public class PiercingModifier extends IncrementalModifier {
  public PiercingModifier() {
    super(0x9FA76D);
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, Hand hand, LivingEntity target, float damageDealt, boolean isCritical, float cooldown, boolean isExtraAttack) {
    // deals 0.5 pierce damage per level, scaled, half of sharpness
    DamageSource source;
    if (attacker instanceof PlayerEntity) {
      source = DamageSource.causePlayerDamage(((PlayerEntity)attacker));
    } else {
      source = DamageSource.causeMobDamage(target);
    }
    source.setDamageBypassesArmor();
    attackEntitySecondary(source, getScaledLevel(tool, level) * tool.getDefinition().getBaseStatDefinition().getModifier(ToolStats.ATTACK_DAMAGE) * 0.5f * cooldown, target, true);
    return 0;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    ScaledTypeDamageModifier.addDamageTooltip(this, tool, level, tool.getDefinition().getBaseStatDefinition().getModifier(ToolStats.ATTACK_DAMAGE) * 0.5f, tooltip);
  }
}

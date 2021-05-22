package slimeknights.tconstruct.tools.modifiers.upgrades;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.IncrementalModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.List;

public class PiercingModifier extends IncrementalModifier {
  public PiercingModifier() {
    super(0x9FA76D);
  }

  @Override
  public int afterLivingHit(IModifierToolStack tool, int level, LivingEntity attacker, LivingEntity target, float damageDealt, boolean isCritical, boolean fullyCharged) {
    // deals 0.5 pierce damage per level, scaled, half of sharpness
    DamageSource source;
    if (attacker instanceof PlayerEntity) {
      source = DamageSource.causePlayerDamage(((PlayerEntity)attacker));
    } else {
      source = DamageSource.causeMobDamage(target);
    }
    source.setDamageBypassesArmor();
    attackEntitySecondary(source, getScaledLevel(tool, level) * tool.getDefinition().getBaseStatDefinition().getDamageModifier() * 0.5f, target, true);
    return 0;
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, boolean isAdvanced, boolean detailed) {
    ScaledTypeDamageModifier.addDamageTooltip(this, tool, level, tool.getDefinition().getBaseStatDefinition().getDamageModifier() * 0.5f, tooltip);
  }
}

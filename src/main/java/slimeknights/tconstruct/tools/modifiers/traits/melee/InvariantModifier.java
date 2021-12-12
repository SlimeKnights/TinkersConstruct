package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;
import slimeknights.tconstruct.library.utils.TooltipKey;

import javax.annotation.Nullable;
import java.util.List;

public class InvariantModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;

  public InvariantModifier() {
    super(0xA3B1A8);
  }

  /** Gets the bonus for this modifier */
  private static float getBonus(LivingEntity living, int level) {
    // temperature ranges from 0 to 1.25. multiplication makes it go from 0 to 2.5
    BlockPos pos = living.getPosition();
    return (Math.abs(BASELINE_TEMPERATURE - living.world.getBiome(pos).getTemperature(pos)) * level * 2f);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    return damage + getBonus(context.getAttacker(), level);
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, @Nullable PlayerEntity player, List<ITextComponent> tooltip, TooltipKey key, TooltipFlag flag) {
    float bonus;
    if (player != null && key == TooltipKey.SHIFT) {
      bonus = getBonus(player, level);
    } else {
      bonus = level * 2.5f;
    }
    addDamageTooltip(tool, bonus, tooltip);
  }
}

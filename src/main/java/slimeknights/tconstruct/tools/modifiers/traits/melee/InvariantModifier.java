package slimeknights.tconstruct.tools.modifiers.traits.melee;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.utils.TooltipFlag;

import java.util.List;

public class InvariantModifier extends Modifier {
  private static final float BASELINE_TEMPERATURE = 0.75f;

  public InvariantModifier() {
    super(0xA3B1A8);
  }

  @Override
  public float getEntityDamage(IModifierToolStack tool, int level, ToolAttackContext context, float baseDamage, float damage) {
    LivingEntity attacker = context.getAttacker();
    BlockPos pos = attacker.getPosition();
    // temperature ranges from 0 to 1.25. multiplication makes it go from 0 to 2.5
    return damage + (Math.abs(BASELINE_TEMPERATURE - attacker.world.getBiome(pos).getTemperature(pos)) * level * 2f);
  }

  @Override
  public void addInformation(IModifierToolStack tool, int level, List<ITextComponent> tooltip, TooltipFlag flag) {
    addDamageTooltip(tool, level * 2.5f, tooltip);
  }
}

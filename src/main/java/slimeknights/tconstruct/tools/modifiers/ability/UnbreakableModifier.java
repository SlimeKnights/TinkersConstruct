package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import javax.annotation.Nullable;

public class UnbreakableModifier extends SingleUseModifier {
  public UnbreakableModifier() {
    super(0xA27CA4);
  }

  @Override
  public int onDamageTool(IModifierToolStack tool, int level, int amount, @Nullable LivingEntity holder) {
    return 0;
  }

  @Override
  public int getPriority() {
    return 125; // runs after overslime, but before reinforced
  }

  @Override
  public int getDurabilityRGB(IModifierToolStack tool, int level) {
    return 0xFFFFFF;
  }
}

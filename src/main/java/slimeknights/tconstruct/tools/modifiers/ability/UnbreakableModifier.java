package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.impl.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class UnbreakableModifier extends SingleUseModifier {
  @Override
  public int onDamageTool(IToolStackView tool, int level, int amount, @Nullable LivingEntity holder) {
    return 0;
  }

  @Override
  public int getPriority() {
    return 125; // runs after overslime, but before reinforced
  }

  @Override
  public int getDurabilityRGB(IToolStackView tool, int level) {
    return 0xFFFFFF;
  }
}

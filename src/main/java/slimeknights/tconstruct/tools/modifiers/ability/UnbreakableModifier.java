package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** @deprecated use {@link slimeknights.tconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule} and {@link slimeknights.tconstruct.library.modifiers.modules.display.DurabilityBarColorModule} */
@Deprecated
public class UnbreakableModifier extends NoLevelsModifier {
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

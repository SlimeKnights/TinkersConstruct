package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import net.minecraft.world.item.UseAnim;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.combat.BlockingModule;

/** @deprecated use {@link slimeknights.tconstruct.tools.modules.combat.BlockingModule} */
@Deprecated(forRemoval = true)
public class BlockingModifier extends NoLevelsModifier {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(BlockingModule.INSTANCE);
  }

  @Override
  public int getPriority() {
    return 50; // late as many modifiers have special blocking interactions
  }

  /** @deprecated use {@link ModifierUtil#blockWhileCharging(IToolStackView, UseAnim)} */
  @Deprecated(forRemoval = true)
  public static UseAnim blockWhileCharging(IToolStackView tool, UseAnim fallback) {
    return ModifierUtil.blockWhileCharging(tool, fallback);
  }
}

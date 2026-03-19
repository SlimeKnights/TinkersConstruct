package slimeknights.tconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.item.crafting.Ingredient;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.data.ModifierIds;
import slimeknights.tconstruct.tools.modules.interaction.FireballModule;

/** @deprecated use {@link FireballModule} */
@Deprecated
public class FirebreathModifier extends NoLevelsModifier implements KeybindInteractModifierHook {
  @Override
  public int getPriority() {
    return 40;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(FireballModule.builder().damageMultiplier(2.5f).fireball(Ingredient.of(TinkerTags.Items.FIREBALLS)).end().modifier(ModifierIds.fiery).build());
  }
}

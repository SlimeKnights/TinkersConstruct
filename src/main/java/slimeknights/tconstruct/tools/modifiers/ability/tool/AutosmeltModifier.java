package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeType;
import slimeknights.tconstruct.common.recipe.RecipeCacheInvalidator;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.tools.modules.AutosmeltModule;

/** @deprecated use {@link AutosmeltModule} */
@Deprecated(forRemoval = true)
public class AutosmeltModifier extends Modifier {
  @Override
  public Component getDisplayName(int level) {
    return ModifierLevelDisplay.PLUSES.nameForLevel(this, level);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    AutosmeltModule autosmelt = new AutosmeltModule(0.2f, RecipeType.SMELTING);
    hookBuilder.addModule(autosmelt);
    RecipeCacheInvalidator.addReloadListener(client -> {
      if (!client) {
        autosmelt.clearCache();
      }
    });
  }
}

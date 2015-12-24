package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.inventory.ContainerCraftingStation;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

  @Override
  public boolean isModLoaded() {
    return true;
  }

  @Override
  public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {

  }

  @Override
  public void onItemRegistryAvailable(IItemRegistry itemRegistry) {

  }

  @Override
  public void register(IModRegistry registry) {
    if(TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
      // Tool Tables
      //registry.addRecipeHandlers(new TableRecipeHandler());

      registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerCraftingStation.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
    }
  }

  @Override
  public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {

  }
}

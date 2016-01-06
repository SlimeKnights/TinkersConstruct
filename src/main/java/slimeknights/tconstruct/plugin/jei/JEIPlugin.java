package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IItemRegistry;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.inventory.ContainerCraftingStation;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

  public static String CATEGORY_Casting = Util.prefix("casting");

  public static IJeiHelpers jeiHelpers;

  @Override
  public boolean isModLoaded() {
    return true;
  }

  @Override
  public void onJeiHelpersAvailable(IJeiHelpers jeiHelpers) {
    JEIPlugin.jeiHelpers = jeiHelpers;
  }

  @Override
  public void onItemRegistryAvailable(IItemRegistry itemRegistry) {}

  @Override
  public void register(IModRegistry registry) {
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

    if(TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
      // crafting table shiftclicking
      registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerCraftingStation.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
    }

    // Smeltery
    if(TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId)) {
      // Smelting
      registry.addRecipeCategories(new SmeltingRecipeCategory(guiHelper),
                                   new AlloyRecipeCategory(guiHelper),
                                   new CastingRecipeCategory(guiHelper));

      registry.addRecipeHandlers(new SmeltingRecipeHandler(),
                                 new AlloyRecipeHandler(),
                                 new CastingRecipeHandler());


      // melting recipies
      registry.addRecipes(TinkerRegistry.getAllMeltingRecipies());
      registry.addRecipes(TinkerRegistry.getAlloys());
      registry.addRecipes(TinkerRegistry.getAllTableCastingRecipes());
    }
  }

  @Override
  public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {}
}

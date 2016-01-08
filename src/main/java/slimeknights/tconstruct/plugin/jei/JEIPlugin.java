package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Map;

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
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.tools.Pattern;
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
      CastingRecipeCategory castingCategory = new CastingRecipeCategory(guiHelper);
      registry.addRecipeCategories(new SmeltingRecipeCategory(guiHelper),
                                   new AlloyRecipeCategory(guiHelper),
                                   castingCategory);

      registry.addRecipeHandlers(new SmeltingRecipeHandler(),
                                 new AlloyRecipeHandler(),
                                 new CastingRecipeHandler());


      // melting recipies
      registry.addRecipes(TinkerRegistry.getAllMeltingRecipies());
      // alloys
      registry.addRecipes(TinkerRegistry.getAlloys());

      // casting
      // we collect together all casting recipes that create a cast and group them together into one recipe
      Map<Item, List<ItemStack>> castDict = Maps.newHashMap();
      for(CastingRecipe recipe : TinkerRegistry.getAllTableCastingRecipes()) {
        if(recipe.cast != null && recipe.getResult() != null && recipe.getResult().getItem() instanceof Cast) {
          Item output = Cast.getPartFromTag(recipe.getResult());
          if(!castDict.containsKey(output)) {
            // recipe for the cast doesn't exist yet. create list and recipe and add it
            List<ItemStack> list = Lists.newLinkedList();
            castDict.put(output, list);
            registry.addRecipes(ImmutableList.of(new CastingRecipeWrapper(list, recipe, castingCategory.castingTable)));
          }
          // add the item to the list
          castDict.get(output).addAll(recipe.cast.getInputs());
        }
        else {
          registry.addRecipes(ImmutableList.of(new CastingRecipeWrapper(recipe, castingCategory.castingTable)));
        }
      }
      for(CastingRecipe recipe : TinkerRegistry.getAllBasinCastingRecipes()) {
        registry.addRecipes(ImmutableList.of(new CastingRecipeWrapper(recipe, castingCategory.castingBasin)));
      }
    }
  }

  @Override
  public void onRecipeRegistryAvailable(IRecipeRegistry recipeRegistry) {}
}

package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerTools;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

  public static IJeiHelpers jeiHelpers;

  @Override
  public void register(IModRegistry registry) {
    jeiHelpers = registry.getJeiHelpers();
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

    if(TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
      // crafting table shiftclicking
      registry.getRecipeTransferRegistry().addRecipeTransferHandler(new CraftingStationRecipeTransferInfo());
    }

    // Smeltery
    if(TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId)) {
      CastingRecipeCategory castingCategory = new CastingRecipeCategory(guiHelper);
      registry.addRecipeCategories(new SmeltingRecipeCategory(guiHelper),
                                   new AlloyRecipeCategory(guiHelper),
                                   castingCategory);

      registry.addRecipeHandlers(new TableRecipeHandler(),
                                 new SmeltingRecipeHandler(),
                                 new AlloyRecipeHandler(),
                                 new CastingRecipeHandler());


      // melting recipes
      registry.addRecipes(TinkerRegistry.getAllMeltingRecipies());
      // alloys
      registry.addRecipes(TinkerRegistry.getAlloys());

      // casting
      // we collect together all casting recipes that create a cast and group them together into one recipe
      Map<Triple<Item, Item, Fluid>, List<ItemStack >> castDict = Maps.newHashMap();
      for(CastingRecipe recipe : TinkerRegistry.getAllTableCastingRecipes()) {
        if(recipe.cast != null && recipe.getResult() != null && recipe.getResult().getItem() instanceof Cast) {
          Triple<Item, Item, Fluid> output = Triple.of(recipe.getResult().getItem(), Cast.getPartFromTag(recipe.getResult()), recipe.getFluid().getFluid());
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
    
    // drying rack
    if(TConstruct.pulseManager.isPulseLoaded(TinkerGadgets.PulseId)) {
      registry.addRecipeCategories(new DryingRecipeCategory(guiHelper));
      registry.addRecipeHandlers(new DryingRecipeHandler());
      registry.addRecipes(TinkerRegistry.getAllDryingRecipes());
    }
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {

  }
}

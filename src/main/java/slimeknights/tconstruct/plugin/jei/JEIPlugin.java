package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.gui.BlankAdvancedGuiHandler;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.smeltery.Cast;
import slimeknights.tconstruct.library.smeltery.CastingRecipe;
import slimeknights.tconstruct.library.smeltery.ICastingRecipe;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.client.GuiSmeltery;
import slimeknights.tconstruct.smeltery.client.GuiTinkerTank;
import slimeknights.tconstruct.smeltery.client.IGuiLiquidTank;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {

  public static IJeiHelpers jeiHelpers;
  // crafting grid slots, integer constants from the default crafting grid implementation
  private static final int craftOutputSlot = 0;
  private static final int craftInputSlot1 = 1;
  public static ICraftingGridHelper craftingGridHelper;

  @Override
  public void registerItemSubtypes(ISubtypeRegistry registry) {
    TableSubtypeInterpreter tableInterpreter = new TableSubtypeInterpreter();
    PatternSubtypeInterpreter patternInterpreter = new PatternSubtypeInterpreter();

    // drying racks and item racks
    if(TConstruct.pulseManager.isPulseLoaded(TinkerGadgets.PulseId)) {
      registry.registerSubtypeInterpreter(Item.getItemFromBlock(TinkerGadgets.rack), tableInterpreter);
    }

    // tools
    if(TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
      // tool tables
      registry.registerSubtypeInterpreter(Item.getItemFromBlock(TinkerTools.toolTables), tableInterpreter);
      registry.registerSubtypeInterpreter(Item.getItemFromBlock(TinkerTools.toolForge), tableInterpreter);

      // tool parts
      ToolPartSubtypeInterpreter toolPartInterpreter = new ToolPartSubtypeInterpreter();
      for(IToolPart part : TinkerRegistry.getToolParts()) {
        if(part instanceof Item) {
          registry.registerSubtypeInterpreter((Item)part, toolPartInterpreter);
        }
      }

      // tool patterns
      registry.registerSubtypeInterpreter(TinkerTools.pattern, patternInterpreter);
    }

    // casts
    if(TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId)) {
      registry.registerSubtypeInterpreter(TinkerSmeltery.cast, patternInterpreter);
      registry.registerSubtypeInterpreter(TinkerSmeltery.clayCast, patternInterpreter);
    }
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registry) {

  }

  @Override
  public void register(@Nonnull IModRegistry registry) {
    jeiHelpers = registry.getJeiHelpers();
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

    // crafting helper used by the shaped table wrapper
    craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);

    if(TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
      // crafting table shiftclicking
      registry.getRecipeTransferRegistry().addRecipeTransferHandler(new CraftingStationRecipeTransferInfo());

      // add our crafting table to the list with the vanilla crafting table
      registry.addRecipeCategoryCraftingItem(new ItemStack(TinkerTools.toolTables, 1, BlockToolTable.TableTypes.CraftingStation.meta),
                                             VanillaRecipeCategoryUid.CRAFTING);
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

      registry.addRecipeCategoryCraftingItem(new ItemStack(TinkerSmeltery.smelteryController),
                                             SmeltingRecipeCategory.CATEGORY,
                                             AlloyRecipeCategory.CATEGORY);
      registry.addRecipeCategoryCraftingItem(new ItemStack(TinkerSmeltery.castingBlock, 1, BlockCasting.CastingType.TABLE.meta),
                                             CastingRecipeCategory.CATEGORY);
      registry.addRecipeCategoryCraftingItem(new ItemStack(TinkerSmeltery.castingBlock, 1, BlockCasting.CastingType.BASIN.meta),
                                             CastingRecipeCategory.CATEGORY);
      // add the seared furnace to the list with the vanilla furnace
      // note that this is just the smelting one, fuel is not relevant
      registry.addRecipeCategoryCraftingItem(new ItemStack(TinkerSmeltery.searedFurnaceController),
                                             VanillaRecipeCategoryUid.SMELTING);

      // melting recipes
      registry.addRecipes(TinkerRegistry.getAllMeltingRecipies());
      // alloys
      registry.addRecipes(TinkerRegistry.getAlloys());

      // casting
      // we collect together all casting recipes that create a cast and group them together into one recipe
      Map<Triple<Item, Item, Fluid>, List<ItemStack>> castDict = Maps.newHashMap();
      for(ICastingRecipe irecipe : TinkerRegistry.getAllTableCastingRecipes()) {
        if(irecipe instanceof CastingRecipe) {
          CastingRecipe recipe = (CastingRecipe) irecipe;
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
      }
      for(ICastingRecipe irecipe : TinkerRegistry.getAllBasinCastingRecipes()) {
        if(irecipe instanceof CastingRecipe) {
          CastingRecipe recipe = (CastingRecipe) irecipe;
          registry.addRecipes(ImmutableList.of(new CastingRecipeWrapper(recipe, castingCategory.castingBasin)));
        }
      }

      // liquid recipe lookup for smeltery and tinker tank
      registry.addAdvancedGuiHandlers(
          new TinkerGuiTankHandler<GuiTinkerTank>(GuiTinkerTank.class),
          new TinkerGuiTankHandler<GuiSmeltery>(GuiSmeltery.class));
    }

    // drying rack
    if(TConstruct.pulseManager.isPulseLoaded(TinkerGadgets.PulseId)) {
      registry.addRecipeCategories(new DryingRecipeCategory(guiHelper));
      registry.addRecipeHandlers(new DryingRecipeHandler());
      registry.addRecipes(TinkerRegistry.getAllDryingRecipes());
      registry.addRecipeCategoryCraftingItem(BlockTable.createItemstack(TinkerGadgets.rack, 1, Blocks.WOODEN_SLAB, 0),
                                             DryingRecipeCategory.CATEGORY);
    }
  }

  @Override
  public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
  }


  private static class TinkerGuiTankHandler<T extends GuiContainer & IGuiLiquidTank> extends BlankAdvancedGuiHandler<T> {
    private Class<T> clazz;

    public TinkerGuiTankHandler(Class<T> clazz) {
      this.clazz = clazz;
    }

    @Nonnull
    @Override
    public Class<T> getGuiContainerClass() {
      return clazz;
    }

    @Nullable
    @Override
    public Object getIngredientUnderMouse(T guiContainer, int mouseX, int mouseY) {
      return guiContainer.getFluidStackAtPosition(mouseX, mouseY);
    }
  }
}

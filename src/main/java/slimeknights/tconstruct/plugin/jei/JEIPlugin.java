package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.gui.IAdvancedGuiHandler;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.DryingRecipe;
import slimeknights.tconstruct.library.MaterialIntegration;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.fluid.FluidColored;
import slimeknights.tconstruct.library.smeltery.AlloyRecipe;
import slimeknights.tconstruct.library.smeltery.MeltingRecipe;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.plugin.jei.alloy.AlloyRecipeCategory;
import slimeknights.tconstruct.plugin.jei.alloy.AlloyRecipeChecker;
import slimeknights.tconstruct.plugin.jei.alloy.AlloyRecipeHandler;
import slimeknights.tconstruct.plugin.jei.casting.CastingRecipeCategory;
import slimeknights.tconstruct.plugin.jei.casting.CastingRecipeChecker;
import slimeknights.tconstruct.plugin.jei.casting.CastingRecipeHandler;
import slimeknights.tconstruct.plugin.jei.casting.CastingRecipeWrapper;
import slimeknights.tconstruct.plugin.jei.drying.DryingRecipeCategory;
import slimeknights.tconstruct.plugin.jei.drying.DryingRecipeChecker;
import slimeknights.tconstruct.plugin.jei.drying.DryingRecipeHandler;
import slimeknights.tconstruct.plugin.jei.interpreter.PatternSubtypeInterpreter;
import slimeknights.tconstruct.plugin.jei.interpreter.TableSubtypeInterpreter;
import slimeknights.tconstruct.plugin.jei.interpreter.ToolPartSubtypeInterpreter;
import slimeknights.tconstruct.plugin.jei.interpreter.ToolSubtypeInterpreter;
import slimeknights.tconstruct.plugin.jei.smelting.SmeltingRecipeCategory;
import slimeknights.tconstruct.plugin.jei.smelting.SmeltingRecipeChecker;
import slimeknights.tconstruct.plugin.jei.smelting.SmeltingRecipeHandler;
import slimeknights.tconstruct.plugin.jei.table.TableRecipeHandler;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.BlockCasting;
import slimeknights.tconstruct.smeltery.client.GuiSmeltery;
import slimeknights.tconstruct.smeltery.client.GuiTinkerTank;
import slimeknights.tconstruct.smeltery.client.IGuiLiquidTank;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.TableRecipeFactory.TableRecipe;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@mezz.jei.api.JEIPlugin
public class JEIPlugin implements IModPlugin {
  public static IJeiHelpers jeiHelpers;
  // crafting grid slots, integer constants from the default crafting grid implementation
  private static final int craftOutputSlot = 0;
  private static final int craftInputSlot1 = 1;

  public static ICraftingGridHelper craftingGridHelper;
  public static IRecipeRegistry recipeRegistry;

  public static CastingRecipeCategory castingCategory;

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

      // tool
      ToolSubtypeInterpreter toolInterpreter = new ToolSubtypeInterpreter();
      for(ToolCore tool : TinkerRegistry.getTools()) {
        registry.registerSubtypeInterpreter(tool, toolInterpreter);
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
  public void registerCategories(IRecipeCategoryRegistration registry) {
    final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
    final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();

    // Smeltery
    if(TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId)) {
      castingCategory = new CastingRecipeCategory(guiHelper);

      registry.addRecipeCategories(new SmeltingRecipeCategory(guiHelper), new AlloyRecipeCategory(guiHelper), castingCategory);
    }

    if(TConstruct.pulseManager.isPulseLoaded(TinkerGadgets.PulseId)) {
      registry.addRecipeCategories(new DryingRecipeCategory(guiHelper));
    }
  }

  @Override
  public void register(@Nonnull IModRegistry registry) {
    jeiHelpers = registry.getJeiHelpers();
    IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
    IIngredientBlacklist blacklist = registry.getJeiHelpers().getIngredientBlacklist();

    // crafting helper used by the shaped table wrapper
    craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot1, craftOutputSlot);

    // its a pain to hide this using getSubItems because how ItemEdible is coded, so just hide from JEI
    blacklist.addIngredientToBlacklist(TinkerCommons.matSlimeBallPink);

    if(TConstruct.pulseManager.isPulseLoaded(TinkerTools.PulseId)) {
      registry.handleRecipes(TableRecipe.class, new TableRecipeHandler(), VanillaRecipeCategoryUid.CRAFTING);

      // crafting table shiftclicking
      registry.getRecipeTransferRegistry().addRecipeTransferHandler(new CraftingStationRecipeTransferInfo());

      // add our crafting table to the list with the vanilla crafting table
      registry.addRecipeCatalyst(new ItemStack(TinkerTools.toolTables, 1, BlockToolTable.TableTypes.CraftingStation.meta), VanillaRecipeCategoryUid.CRAFTING);
    }

    // Smeltery
    if(TConstruct.pulseManager.isPulseLoaded(TinkerSmeltery.PulseId)) {
      registry.handleRecipes(AlloyRecipe.class, new AlloyRecipeHandler(), AlloyRecipeCategory.CATEGORY);

      registry.handleRecipes(MeltingRecipe.class, new SmeltingRecipeHandler(), SmeltingRecipeCategory.CATEGORY);

      registry.handleRecipes(CastingRecipeWrapper.class, new CastingRecipeHandler(), CastingRecipeCategory.CATEGORY);

      registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.smelteryController), SmeltingRecipeCategory.CATEGORY, AlloyRecipeCategory.CATEGORY);
      registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.castingBlock, 1, BlockCasting.CastingType.TABLE.meta), CastingRecipeCategory.CATEGORY);
      registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.castingBlock, 1, BlockCasting.CastingType.BASIN.meta), CastingRecipeCategory.CATEGORY);
      // add the seared furnace to the list with the vanilla furnace
      // note that this is just the smelting one, fuel is not relevant
      registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.searedFurnaceController), VanillaRecipeCategoryUid.SMELTING);

      // melting recipes
      registry.addRecipes(SmeltingRecipeChecker.getSmeltingRecipes(), SmeltingRecipeCategory.CATEGORY);
      // alloys
      registry.addRecipes(AlloyRecipeChecker.getAlloyRecipes(), AlloyRecipeCategory.CATEGORY);

      // casting
      registry.addRecipes(CastingRecipeChecker.getCastingRecipes(), CastingRecipeCategory.CATEGORY);

      // liquid recipe lookup for smeltery and tinker tank
      registry.addAdvancedGuiHandlers(new TinkerGuiTankHandler<>(GuiTinkerTank.class), new TinkerGuiTankHandler<>(GuiSmeltery.class));

      // hide unused fluids from JEI
      for(MaterialIntegration integration : TinkerRegistry.getMaterialIntegrations()) {
        // if it has a fluid and that fluid is one of ours, hide it
        if(!integration.isIntegrated() && integration.fluid instanceof FluidColored) {
          FluidStack stack = new FluidStack(integration.fluid, Fluid.BUCKET_VOLUME);
          blacklist.addIngredientToBlacklist(stack);
          blacklist.addIngredientToBlacklist(FluidUtil.getFilledBucket(stack));
        }
      }
    }

    // drying rack
    if(TConstruct.pulseManager.isPulseLoaded(TinkerGadgets.PulseId)) {
      registry.handleRecipes(DryingRecipe.class, new DryingRecipeHandler(), DryingRecipeCategory.CATEGORY);

      registry.addRecipes(DryingRecipeChecker.getDryingRecipes(), DryingRecipeCategory.CATEGORY);

      registry.addRecipeCatalyst(BlockTable.createItemstack(TinkerGadgets.rack, 1, Blocks.WOODEN_SLAB, 0), DryingRecipeCategory.CATEGORY);
    }
  }

  @Override
  public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
    recipeRegistry = jeiRuntime.getRecipeRegistry();
  }

  private static class TinkerGuiTankHandler<T extends GuiContainer & IGuiLiquidTank> implements IAdvancedGuiHandler<T> {
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

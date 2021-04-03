package slimeknights.tconstruct.plugin.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IModIngredientRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.tags.ITag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.modifiers.BeheadingRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.modifier.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.MaterialIdNBT;
import slimeknights.tconstruct.plugin.jei.casting.CastingBasinCategory;
import slimeknights.tconstruct.plugin.jei.casting.CastingTableCategory;
import slimeknights.tconstruct.plugin.jei.entity.BeheadingCategory;
import slimeknights.tconstruct.plugin.jei.entity.DefaultEntityMeltingRecipe;
import slimeknights.tconstruct.plugin.jei.entity.EntityIngredientHelper;
import slimeknights.tconstruct.plugin.jei.entity.EntityIngredientRenderer;
import slimeknights.tconstruct.plugin.jei.entity.EntityMeltingRecipeCategory;
import slimeknights.tconstruct.plugin.jei.melting.MeltingCategory;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.plugin.jei.modifiers.ModifierIngredientHelper;
import slimeknights.tconstruct.plugin.jei.modifiers.ModifierIngredientRenderer;
import slimeknights.tconstruct.plugin.jei.modifiers.ModifierRecipeCategory;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.client.inventory.IScreenWithFluidTank;
import slimeknights.tconstruct.smeltery.client.inventory.MelterScreen;
import slimeknights.tconstruct.smeltery.client.inventory.SmelteryScreen;
import slimeknights.tconstruct.smeltery.data.SmelteryCompat;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@JeiPlugin
public class JEIPlugin implements IModPlugin {
  @SuppressWarnings("rawtypes")
  public static final IIngredientType<EntityType> ENTITY_TYPE = () -> EntityType.class;
  public static final IIngredientType<ModifierEntry> MODIFIER_TYPE = () -> ModifierEntry.class;

  @Override
  public ResourceLocation getPluginUid() {
    return TConstructRecipeCategoryUid.pluginUid;
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    // casting
    registry.addRecipeCategories(new CastingBasinCategory(guiHelper));
    registry.addRecipeCategories(new CastingTableCategory(guiHelper));
    registry.addRecipeCategories(new MoldingRecipeCategory(guiHelper));
    // melting and casting
    registry.addRecipeCategories(new MeltingCategory(guiHelper));
    registry.addRecipeCategories(new AlloyRecipeCategory(guiHelper));
    registry.addRecipeCategories(new EntityMeltingRecipeCategory(guiHelper));
    // tinker station
    registry.addRecipeCategories(new ModifierRecipeCategory(guiHelper));
    registry.addRecipeCategories(new BeheadingCategory(guiHelper));
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {
    registration.register(ENTITY_TYPE, Collections.emptyList(), new EntityIngredientHelper(), new EntityIngredientRenderer(16));
    registration.register(MODIFIER_TYPE, Collections.emptyList(), new ModifierIngredientHelper(), new ModifierIngredientRenderer(16));
  }

  @Override
  public void registerRecipes(IRecipeRegistration register) {
    assert Minecraft.getInstance().world != null;
    RecipeManager manager = Minecraft.getInstance().world.getRecipeManager();
    // casting
    List<IDisplayableCastingRecipe> castingBasinRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.CASTING_BASIN, IDisplayableCastingRecipe.class);
    register.addRecipes(castingBasinRecipes, TConstructRecipeCategoryUid.castingBasin);
    List<IDisplayableCastingRecipe> castingTableRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.CASTING_TABLE, IDisplayableCastingRecipe.class);
    register.addRecipes(castingTableRecipes, TConstructRecipeCategoryUid.castingTable);

    // melting
    List<MeltingRecipe> meltingRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.MELTING, MeltingRecipe.class);
    register.addRecipes(meltingRecipes, TConstructRecipeCategoryUid.melting);
    MeltingFuelHandler.setMeltngFuels(RecipeHelper.getRecipes(manager, RecipeTypes.FUEL, MeltingFuel.class));

    // entity melting
    List<EntityMeltingRecipe> entityMeltingRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.ENTITY_MELTING, EntityMeltingRecipe.class);
    // generate a "default" recipe for all other entity types
    entityMeltingRecipes.add(new DefaultEntityMeltingRecipe(entityMeltingRecipes));
    register.addRecipes(entityMeltingRecipes, TConstructRecipeCategoryUid.entityMelting);

    // alloying
    List<AlloyRecipe> alloyRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.ALLOYING, AlloyRecipe.class);
    register.addRecipes(alloyRecipes, TConstructRecipeCategoryUid.alloy);

    // molding
    List<MoldingRecipe> moldingRecipes = ImmutableList.<MoldingRecipe>builder()
      .addAll(RecipeHelper.getJEIRecipes(manager, RecipeTypes.MOLDING_TABLE, MoldingRecipe.class))
      .addAll(RecipeHelper.getJEIRecipes(manager, RecipeTypes.MOLDING_BASIN, MoldingRecipe.class))
      .build();
    register.addRecipes(moldingRecipes, TConstructRecipeCategoryUid.molding);

    // modifiers
    List<IDisplayModifierRecipe> modifierRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.TINKER_STATION, IDisplayModifierRecipe.class);
    register.addRecipes(modifierRecipes, TConstructRecipeCategoryUid.modifiers);

    // beheading
    List<BeheadingRecipe> beheadingRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.BEHEADING, BeheadingRecipe.class);
    register.addRecipes(beheadingRecipes, TConstructRecipeCategoryUid.beheading);
  }

  /**
   * Adds an item as a casting catalyst, and as a molding catalyst if it has molding recipes
   * @param registry     Catalyst regisry
   * @param item         Item to add
   * @param ownCategory  Category to always add
   * @param type         Molding recipe type
   */
  private static <T extends IRecipe<C>, C extends IInventory> void addCastingCatalyst(IRecipeCatalystRegistration registry, IItemProvider item, ResourceLocation ownCategory, IRecipeType<T> type) {
    ItemStack stack = new ItemStack(item);
    registry.addRecipeCatalyst(stack, ownCategory);
    assert Minecraft.getInstance().world != null;
    if (!Minecraft.getInstance().world.getRecipeManager().getRecipes(type).isEmpty()) {
      registry.addRecipeCatalyst(stack, TConstructRecipeCategoryUid.molding);
    }
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
    addCastingCatalyst(registry, TinkerSmeltery.castingTable, TConstructRecipeCategoryUid.castingTable, RecipeTypes.MOLDING_TABLE);
    addCastingCatalyst(registry, TinkerSmeltery.castingBasin, TConstructRecipeCategoryUid.castingBasin, RecipeTypes.MOLDING_BASIN);
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.searedMelter), TConstructRecipeCategoryUid.melting);
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.smelteryController), TConstructRecipeCategoryUid.melting, TConstructRecipeCategoryUid.alloy, TConstructRecipeCategoryUid.entityMelting);
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.searedHeater), VanillaRecipeCategoryUid.FUEL);
    registry.addRecipeCatalyst(new ItemStack(TinkerTables.tinkerStation), TConstructRecipeCategoryUid.modifiers);
    registry.addRecipeCatalyst(new ItemStack(TinkerTables.tinkersAnvil), TConstructRecipeCategoryUid.modifiers);
    for (Item item : TinkerTags.Items.MELEE.getAllElements()) {
      ItemStack stack = item instanceof ToolCore ? ((ToolCore)item).buildToolForRendering() : new ItemStack(item);
      registry.addRecipeCatalyst(stack, TConstructRecipeCategoryUid.beheading);
    }
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registry) {
    // retexturable blocks
    ISubtypeInterpreter tables = new RetexturedSubtypeInterpreter();
    registry.registerSubtypeInterpreter(TinkerTables.craftingStation.asItem(), tables);
    registry.registerSubtypeInterpreter(TinkerTables.partBuilder.asItem(), tables);
    registry.registerSubtypeInterpreter(TinkerTables.tinkerStation.asItem(), tables);
    registry.registerSubtypeInterpreter(TinkerTables.tinkersAnvil.asItem(), tables);

    ISubtypeInterpreter toolPartInterpreter = itemStack -> {
      IMaterial material = IMaterialItem.getMaterialFromStack(itemStack);
      if (material == IMaterial.UNKNOWN) {
        return ISubtypeInterpreter.NONE;
      }
      return material.getIdentifier().toString();
    };

    // parts
    registry.registerSubtypeInterpreter(TinkerToolParts.pickaxeHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.hammerHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.axeHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.kamaHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.swordBlade.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.toolBinding.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.largePlate.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.toolRod.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.toughToolRod.get(), toolPartInterpreter);

    // tools
    ISubtypeInterpreter toolInterpreter = new ToolSubtypeInterpreter();
    registry.registerSubtypeInterpreter(TinkerTools.pickaxe.get(), toolInterpreter);
    registry.registerSubtypeInterpreter(TinkerTools.sledgeHammer.get(), toolInterpreter);
    registry.registerSubtypeInterpreter(TinkerTools.mattock.get(), toolInterpreter);
    registry.registerSubtypeInterpreter(TinkerTools.excavator.get(), toolInterpreter);
    registry.registerSubtypeInterpreter(TinkerTools.axe.get(), toolInterpreter);
    registry.registerSubtypeInterpreter(TinkerTools.kama.get(), toolInterpreter);
    registry.registerSubtypeInterpreter(TinkerTools.broadSword.get(), toolInterpreter);

    registry.registerSubtypeInterpreter(TinkerSmeltery.copperCan.get(), CopperCanItem::getSubtype);
  }

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    registration.addGenericGuiContainerHandler(MelterScreen.class, new GuiContainerTankHandler<>());
    registration.addGenericGuiContainerHandler(SmelteryScreen.class, new GuiContainerTankHandler<>());
  }

  @Override
  public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
    registration.addRecipeTransferHandler(new CraftingStationTransferInfo());
  }

  /**
   * Removes a fluid from JEI
   * @param manager  Manager
   * @param fluid    Fluid to remove
   * @param bucket   Fluid bucket to remove
   */
  private static void removeFluid(IIngredientManager manager, Fluid fluid, Item bucket) {
    manager.removeIngredientsAtRuntime(VanillaTypes.FLUID, Collections.singleton(new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME)));
    manager.removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singleton(new ItemStack(bucket)));
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    IIngredientManager manager = jeiRuntime.getIngredientManager();
    // hide knightslime and slimesteel until implemented
    removeFluid(manager, TinkerFluids.moltenSoulsteel.get(), TinkerFluids.moltenSoulsteel.asItem());
    removeFluid(manager, TinkerFluids.moltenKnightslime.get(), TinkerFluids.moltenKnightslime.asItem());
    // hide compat that is not present
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      ITag<Item> ingot = TagCollectionManager.getManager().getItemTags().get(new ResourceLocation("forge", "ingots/" + compat.getName()));
      if (ingot == null || ingot.getAllElements().isEmpty()) {
        removeFluid(manager, compat.getFluid(), compat.getBucket());
      }
    }
  }

  /** Class to pass {@link IScreenWithFluidTank} into JEI */
  public static class GuiContainerTankHandler<C extends Container, T extends ContainerScreen<C> & IScreenWithFluidTank> implements IGuiContainerHandler<T> {
    @Override
    @Nullable
    public Object getIngredientUnderMouse(T containerScreen, double mouseX, double mouseY) {
      return containerScreen.getIngredientUnderMouse(mouseX, mouseY);
    }
  }

  /** Subtype interpreter for tools, treats the tool as unique in ingredient list, generic in recipes */
  public static class ToolSubtypeInterpreter implements ISubtypeInterpreter {
    @Override
    public String apply(ItemStack itemStack) {
      return NONE;
    }

    @Override
    public String apply(ItemStack itemStack, UidContext context) {
      if (context == UidContext.Ingredient) {
        StringBuilder builder = new StringBuilder();
        List<MaterialId> materialList = MaterialIdNBT.from(itemStack).getMaterials();
        if (!materialList.isEmpty()) {
          // append first entry without a comma
          builder.append(materialList.get(0));
          for (int i = 1; i < materialList.size(); i++) {
            builder.append(',');
            builder.append(materialList.get(i));
          }
        }
        return builder.toString();
      }
      return NONE;
    }
  }

  public static class RetexturedSubtypeInterpreter implements ISubtypeInterpreter {
    @Override
    public String apply(ItemStack itemStack) {
      return NONE;
    }

    @Override
    public String apply(ItemStack itemStack, UidContext context) {
      if (context == UidContext.Ingredient) {
        return RetexturedBlockItem.getTextureName(itemStack);
      }
      return NONE;
    }
  }
}

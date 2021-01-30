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
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.plugin.jei.casting.CastingBasinCategory;
import slimeknights.tconstruct.plugin.jei.casting.CastingTableCategory;
import slimeknights.tconstruct.plugin.jei.entitymelting.DefaultEntityMeltingRecipe;
import slimeknights.tconstruct.plugin.jei.entitymelting.EntityIngredientHelper;
import slimeknights.tconstruct.plugin.jei.entitymelting.EntityIngredientRenderer;
import slimeknights.tconstruct.plugin.jei.entitymelting.EntityMeltingRecipeCategory;
import slimeknights.tconstruct.plugin.jei.melting.MeltingCategory;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.client.inventory.IScreenWithFluidTank;
import slimeknights.tconstruct.smeltery.client.inventory.MelterScreen;
import slimeknights.tconstruct.smeltery.client.inventory.SmelteryScreen;
import slimeknights.tconstruct.smeltery.data.SmelteryCompat;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;
import slimeknights.tconstruct.tables.inventory.table.CraftingStationContainer;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@JeiPlugin
public class JEIPlugin implements IModPlugin {
  @SuppressWarnings("rawtypes")
  public static final IIngredientType<EntityType> TYPE = () -> EntityType.class;

  @Override
  public ResourceLocation getPluginUid() {
    return TConstructRecipeCategoryUid.pluginUid;
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registry) {
    final IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
    registry.addRecipeCategories(new CastingBasinCategory(guiHelper));
    registry.addRecipeCategories(new CastingTableCategory(guiHelper));
    registry.addRecipeCategories(new MeltingCategory(guiHelper));
    registry.addRecipeCategories(new AlloyRecipeCategory(guiHelper));
    registry.addRecipeCategories(new EntityMeltingRecipeCategory(guiHelper));
    registry.addRecipeCategories(new MoldingRecipeCategory(guiHelper));
  }

  @Override
  public void registerIngredients(IModIngredientRegistration registration) {
    registration.register(TYPE, Collections.emptyList(), new EntityIngredientHelper(), new EntityIngredientRenderer(16));
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
  }

  @Override
  public void registerItemSubtypes(ISubtypeRegistration registry) {
    ISubtypeInterpreter toolPartInterpreter = itemStack -> {
      IMaterial material = IMaterialItem.getMaterialFromStack(itemStack);

      if (material == IMaterial.UNKNOWN) {
        return ISubtypeInterpreter.NONE;
      }

      return material.getIdentifier().toString();
    };

    registry.registerSubtypeInterpreter(TinkerToolParts.pickaxeHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.hammerHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.shovelHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.excavatorHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.axeHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.kamaHead.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.swordBlade.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.smallBinding.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.toughBinding.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.largePlate.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.toolRod.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.toughToolRod.get(), toolPartInterpreter);

    ISubtypeInterpreter toolInterpreter = itemStack -> {
      StringBuilder builder = new StringBuilder();

      List<IMaterial> materialList = ToolStack.from(itemStack).getMaterialsList();
      if (!materialList.isEmpty()) {
        for (int i = 0; i < materialList.size(); i++) {
          // looks nicer if there is no comma at the start
          if (i != 0) {
            builder.append(',');
          }

          builder.append(materialList.get(i));
        }
      }

      return builder.toString();
    };

    registry.registerSubtypeInterpreter(TinkerTools.pickaxe.get(), toolInterpreter);
    registry.registerSubtypeInterpreter(TinkerTools.hammer.get(), toolInterpreter);
    registry.registerSubtypeInterpreter(TinkerTools.shovel.get(), toolInterpreter);
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
    registration.addRecipeTransferHandler(CraftingStationContainer.class, VanillaRecipeCategoryUid.CRAFTING, 0, 9, 10, 36);
  }

  @Override
  public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    IIngredientManager manager = jeiRuntime.getIngredientManager();
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      ITag<Item> ingot = TagCollectionManager.getManager().getItemTags().get(new ResourceLocation("forge", "ingots/" + compat.getName()));
      if (ingot == null || ingot.getAllElements().isEmpty()) {
        manager.removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singleton(new ItemStack(compat.getBucket())));
        manager.removeIngredientsAtRuntime(VanillaTypes.FLUID, Collections.singleton(new FluidStack(compat.getFluid(), FluidAttributes.BUCKET_VOLUME)));
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
}

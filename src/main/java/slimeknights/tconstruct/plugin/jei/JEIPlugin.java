package slimeknights.tconstruct.plugin.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.fuel.MeltingFuel;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.plugin.jei.casting.CastingBasinCategory;
import slimeknights.tconstruct.plugin.jei.casting.CastingTableCategory;
import slimeknights.tconstruct.plugin.jei.melting.MeltingCategory;
import slimeknights.tconstruct.plugin.jei.melting.MeltingFuelHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
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
  }

  @Override
  public void registerRecipes(IRecipeRegistration register) {
    assert Minecraft.getInstance().world != null;
    RecipeManager manager = Minecraft.getInstance().world.getRecipeManager();
    // casting
    List<ItemCastingRecipe> castingBasinRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.CASTING_BASIN, ItemCastingRecipe.class);
    register.addRecipes(castingBasinRecipes, TConstructRecipeCategoryUid.castingBasin);
    List<ItemCastingRecipe> castingTableRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.CASTING_TABLE, ItemCastingRecipe.class);
    register.addRecipes(castingTableRecipes, TConstructRecipeCategoryUid.castingTable);

    // melting
    List<MeltingRecipe> meltingRecipes = RecipeHelper.getJEIRecipes(manager, RecipeTypes.MELTING, MeltingRecipe.class);
    register.addRecipes(meltingRecipes, TConstructRecipeCategoryUid.melting);
    MeltingFuelHandler.setMeltngFuels(RecipeHelper.getRecipes(manager, RecipeTypes.FUEL, MeltingFuel.class));
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.castingBasin), TConstructRecipeCategoryUid.castingBasin);
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.castingTable), TConstructRecipeCategoryUid.castingTable);
    registry.addRecipeCatalyst(new ItemStack(TinkerSmeltery.searedMelter), TConstructRecipeCategoryUid.melting);
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
    registry.registerSubtypeInterpreter(TinkerToolParts.largeSwordBlade.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.smallBinding.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.toughBinding.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.wideGuard.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.largePlate.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.toolRod.get(), toolPartInterpreter);
    registry.registerSubtypeInterpreter(TinkerToolParts.toughToolRod.get(), toolPartInterpreter);

    ISubtypeInterpreter toolInterpreter = itemStack -> {
      StringBuilder builder = new StringBuilder();

      List<IMaterial> materialList = ToolData.from(itemStack).getMaterials();

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
  }
}

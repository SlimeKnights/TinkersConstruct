package slimeknights.tconstruct.plugin.jei.casting;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.recipe.FluidIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.MaterialCastingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialCastingRecipeMaker {
  public static List<ItemCastingRecipe.Basin> createMaterialCastingBasinRecipes() {
    List<ItemCastingRecipe.Basin> recipes = new ArrayList<>();
    List<MaterialCastingRecipe.Basin> materialCastingRecipes = RecipeUtil.getRecipes(Minecraft.getInstance().world.getRecipeManager(), RecipeTypes.CASTING_BASIN, MaterialCastingRecipe.Basin.class);
    for (IMaterial material : MaterialRegistry.getInstance().getMaterials()) {
      if (material.getFluid() == Fluids.EMPTY) continue;
      for (MaterialCastingRecipe.Basin basin : materialCastingRecipes) {
        IMaterialItem result = basin.getResult();
        ItemStack partStack = result.getItemstackWithMaterial(material);
        String path = String.format("jei.material.casting.%s.%s", Objects.requireNonNull(result.asItem().getRegistryName()).getPath(), Objects.requireNonNull(material.getFluid().getRegistryName()).getPath());
        ResourceLocation id = new ResourceLocation(TConstruct.modID, path);
        ItemCastingRecipe.Basin tableRecipe = new ItemCastingRecipe.Basin(id, "", basin.getCast(), FluidIngredient.of(material.getFluid(), basin.getFluidAmount()), partStack, ICastingRecipe.calcCoolingTime(material.getTemperature(), basin.getFluidAmount()), basin.isConsumed(), basin.switchSlots());
        recipes.add(tableRecipe);
      }
    }
    return recipes;
  }

  public static List<ItemCastingRecipe.Table> createMaterialCastingTableRecipes() {
    List<ItemCastingRecipe.Table> recipes = new ArrayList<>();
    List<MaterialCastingRecipe.Table> materialCastingRecipes = RecipeUtil.getRecipes(Minecraft.getInstance().world.getRecipeManager(), RecipeTypes.CASTING_TABLE, MaterialCastingRecipe.Table.class);
    for (IMaterial material : MaterialRegistry.getInstance().getMaterials()) {
      if (material.getFluid() == Fluids.EMPTY) continue;
      for (MaterialCastingRecipe.Table table : materialCastingRecipes) {
        IMaterialItem result = table.getResult();
        ItemStack partStack = result.getItemstackWithMaterial(material);
        String path = String.format("jei.material.casting.%s.%s", Objects.requireNonNull(result.asItem().getRegistryName()).getPath(), Objects.requireNonNull(material.getFluid().getRegistryName()).getPath());
        ResourceLocation id = new ResourceLocation(TConstruct.modID, path);
        ItemCastingRecipe.Table tableRecipe = new ItemCastingRecipe.Table(id, "", table.getCast(), FluidIngredient.of(material.getFluid(), table.getFluidAmount()), partStack, ICastingRecipe.calcCoolingTime(material.getTemperature(), table.getFluidAmount()), table.isConsumed(), table.switchSlots());
        recipes.add(tableRecipe);
      }
    }
    return recipes;
  }
}

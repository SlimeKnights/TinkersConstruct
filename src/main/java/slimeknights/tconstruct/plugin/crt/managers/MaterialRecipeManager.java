package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.CTFluidIngredient;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zencode.java.ZenCodeType.Optional;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.MaterialRecipe")
public class MaterialRecipeManager implements IRecipeManager {
  
  @ZenCodeType.Method
  public void addItem(String name, IIngredient ingredient, int value, int needed, String materialId, @Optional ItemOutput leftover) {
    MaterialId material = CRTHelper.getMaterialId(materialId);
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    MaterialRecipe recipe = new MaterialRecipe(id, "", ingredient.asVanillaIngredient(), value, needed, material, leftover);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addMaterialFluid(String name, CTFluidIngredient fluidIngredient, String outputMaterialId, int coolingTemperature) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    FluidIngredient fluid = CRTHelper.mapFluidIngredient(fluidIngredient);
    MaterialId outputMatId = CRTHelper.getMaterialId(outputMaterialId);
    MaterialFluidRecipe recipe = new MaterialFluidRecipe(id, fluid, coolingTemperature, null, outputMatId);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Material Fluid"));
  }

  @ZenCodeType.Method
  public void addCompositeFluid(String name, String materialId, CTFluidIngredient fluidIngredient, String outputMaterialId, int coolingTemperature) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    MaterialId inputMaterialId = CRTHelper.getMaterialId(materialId);
    FluidIngredient fluid = CRTHelper.mapFluidIngredient(fluidIngredient);
    MaterialId outputMatId = CRTHelper.getMaterialId(outputMaterialId);
    MaterialFluidRecipe recipe = new MaterialFluidRecipe(id, fluid, coolingTemperature, inputMaterialId, outputMatId);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Composite Casting"));
  }
  
  @Override
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Material Recipes by an IItemStack output! Use `removeByName(String name)` instead!");
  }
  
  @Override
  public IRecipeType<MaterialRecipe> getRecipeType() {
    return RecipeTypes.MATERIAL;
  }
  
}

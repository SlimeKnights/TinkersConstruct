package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl_native.item.ExpandItem;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipe;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Material")
public class MaterialManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addMaterial(String name, IIngredient ingredient, int value, int needed, String materialId) {
    MaterialId material = MaterialId.tryCreate(materialId);
    if (material == null) {
      throw new IllegalArgumentException("Invalid ResourceLocation provided! Provided: " + materialId);
    }
    if (MaterialRegistry.getMaterial(material) == Material.UNKNOWN) {
      throw new IllegalArgumentException("Material does not exist! Provided: " + materialId);
    }
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    MaterialRecipe recipe = new MaterialRecipe(id, "", ingredient.asVanillaIngredient(), value, needed, material);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
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

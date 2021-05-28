package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.MaterialRecipe")
public class MaterialRecipeManager implements IRecipeManager {
  
  @ZenCodeType.Method
  public void addItem(String name, IIngredient ingredient, int value, int needed, String materialId) {
    MaterialId material = CRTHelper.getMaterialId(materialId);
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

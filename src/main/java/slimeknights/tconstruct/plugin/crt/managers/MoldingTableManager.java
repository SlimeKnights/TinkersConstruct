package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.ItemOutput;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.MoldingTable")
public class MoldingTableManager implements IRecipeManager {
  
  @ZenCodeType.Method
  public void addRecipe(String name, IIngredient material, IIngredient mold, boolean moldConsumed, IItemStack output) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient materialIngredient = material.asVanillaIngredient();
    Ingredient moldIngredient = mold.asVanillaIngredient();
    ItemOutput itemOutput = ItemOutput.fromStack(output.getInternal());
    MoldingRecipe.Table recipe = new MoldingRecipe.Table(id, materialIngredient, moldIngredient, moldConsumed, itemOutput);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }
  
  @Override
  public IRecipeType<MoldingRecipe> getRecipeType() {
    return RecipeTypes.MOLDING_TABLE;
  }
  
}

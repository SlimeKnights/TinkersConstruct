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
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingRecipe;
import slimeknights.tconstruct.library.recipe.molding.MoldingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.MoldingBasin")
public class MoldingBasinManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addRecipe(String name, IIngredient material, IIngredient mold, boolean moldConsumed, IItemStack output) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);

    Ingredient materialIngredient = material.asVanillaIngredient();
    Ingredient moldIngredient = mold.asVanillaIngredient();
    ItemOutput itemOutput = ItemOutput.fromStack(output.getInternal());
    MoldingRecipe.Basin recipe = new MoldingRecipe.Basin(id, materialIngredient, moldIngredient, moldConsumed, itemOutput);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }


  @Override
  public IRecipeType<MoldingRecipe> getRecipeType() {
    return RecipeTypes.MOLDING_BASIN;
  }

}

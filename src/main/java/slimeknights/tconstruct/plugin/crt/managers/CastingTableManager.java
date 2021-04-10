package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.exceptions.ScriptException;
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
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.CastingTable")
public class CastingTableManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addItemCastingRecipe(String name, IIngredient cast, IFluidStack fluid, IItemStack result, int coolingTime, boolean consumed, boolean switchSlots) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient castIngredient = cast.asVanillaIngredient();
    FluidIngredient fluidIngredient = FluidIngredient.of(fluid.getInternal());
    ItemOutput itemOutput = ItemOutput.fromStack(result.getInternal());
    ItemCastingRecipe.Table basin = new ItemCastingRecipe.Table(id, "", castIngredient, fluidIngredient, itemOutput, coolingTime, consumed, switchSlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, basin, "Item Casting"));
  }

  @ZenCodeType.Method
  public void addMaterialCastingRecipe(String name, IIngredient cast, int fluidAmount, Item result, boolean consumed, boolean switchSlots) {
    if (!(result instanceof IMaterialItem)) {
      throw new IllegalArgumentException(ExpandItem.getDefaultInstance(result).getCommandString() + " is not a valid IMaterialItem! You can use `/ct dump ticMaterialItems` to view valid items!");
    }
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient castIngredient = cast.asVanillaIngredient();
    MaterialCastingRecipe.Table recipe = new MaterialCastingRecipe.Table(id, "", castIngredient, fluidAmount, (IMaterialItem) result, consumed, switchSlots);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe, "Material Casting"));
  }


  @Override
  public IRecipeType<ICastingRecipe> getRecipeType() {
    return RecipeTypes.CASTING_TABLE;
  }

}

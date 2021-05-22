package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.entity.CTEntityIngredient;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipe;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.EntityMelting")
public class EntityMeltingManager implements IRecipeManager {
  
  @ZenCodeType.Method
  public void addRecipe(String name, CTEntityIngredient input, IFluidStack output, int damage) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    FluidStack outputFluid = output.getInternal();
    EntityMeltingRecipe recipe = new EntityMeltingRecipe(id, CRTHelper.mapEntityIngredient(input), outputFluid, damage);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }
  
  @Override
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Entity Melting Recipes by an IItemStack output as it outputs Fluids! Use `removeRecipe(Fluid output)` or `removeRecipe(MCEntityType entity)`instead!");
  }
  
  @ZenCodeType.Method
  public void removeRecipe(Fluid output) {
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if(iRecipe instanceof EntityMeltingRecipe) {
        EntityMeltingRecipe recipe = (EntityMeltingRecipe) iRecipe;
        return recipe.getOutput().getFluid() == output;
      }
      return false;
    }));
  }

  @ZenCodeType.Method
  public void removeRecipe(MCEntityType entity) {
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if(iRecipe instanceof EntityMeltingRecipe) {
        EntityMeltingRecipe recipe = (EntityMeltingRecipe) iRecipe;
        return recipe.matches(entity.getInternal());
      }
      return false;
    }));
  }

  @Override
  public IRecipeType<EntityMeltingRecipe> getRecipeType() {
    return RecipeTypes.ENTITY_MELTING;
  }
  
}

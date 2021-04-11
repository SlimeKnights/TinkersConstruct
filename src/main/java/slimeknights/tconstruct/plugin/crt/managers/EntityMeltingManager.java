package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionRemoveRecipe;
import com.blamejared.crafttweaker.impl.entity.MCEntityType;
import com.blamejared.crafttweaker.impl_native.item.ExpandItem;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.mantle.recipe.EntityIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipe;
import slimeknights.tconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.DamageableMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.OreMeltingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.EntityMelting")
public class EntityMeltingManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addRecipe(String name, MCEntityType input, IFluidStack output, int damage) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    FluidStack outputFluid = output.getInternal();
    EntityMeltingRecipe recipe = new EntityMeltingRecipe(id, EntityIngredient.of(input.getInternal()), outputFluid, damage);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @Override
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Entity Melting Recipes by an IItemStack output as it outputs Fluids! Use `removeRecipe(Fluid output)` instead!");
  }

  @ZenCodeType.Method
  public void removeRecipe(Fluid output) {
    CraftTweakerAPI.apply(new ActionRemoveRecipe(this, iRecipe -> {
      if (iRecipe instanceof EntityMeltingRecipe) {
        EntityMeltingRecipe recipe = (EntityMeltingRecipe) iRecipe;
        return recipe.getOutput().getFluid() == output;
      }
      return false;
    }));
  }

  @Override
  public IRecipeType<EntityMeltingRecipe> getRecipeType() {
    return RecipeTypes.ENTITY_MELTING;
  }

}

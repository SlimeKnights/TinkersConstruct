package slimeknights.tconstruct.plugin.crt.managers;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.managers.IRecipeManager;
import com.blamejared.crafttweaker.impl.actions.recipes.ActionAddRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.openzen.zencode.java.ZenCodeType;
import org.openzen.zencode.java.ZenCodeType.Nullable;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.melting.DamageableMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.OreMeltingRecipe;
import slimeknights.tconstruct.plugin.crt.CRTHelper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Melting")
public class MeltingManager implements IRecipeManager {

  /** Shared logic by all melting recipe variants */
  @SuppressWarnings("ConstantConditions")
  private void addMeltingRecipe(String name, IIngredient input, IFluidStack output, int temperature, int time, @Nullable List<IFluidStack> byProducts, MeltingRecipe.IFactory<?> factory) {
    name = fixRecipeName(name);
    if (byProducts == null) {
      byProducts = Collections.emptyList();
    }
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient ingredient = input.asVanillaIngredient();
    FluidStack outputFluid = output.getInternal();
    List<FluidStack> byproductStacks = byProducts.stream().map(IFluidStack::getInternal).collect(Collectors.toList());
    MeltingRecipe recipe = factory.create(id, "", ingredient, outputFluid, temperature, time, byproductStacks);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addMeltingRecipe(String name, IIngredient input, IFluidStack output, int temperature, int time, @ZenCodeType.Optional List<IFluidStack> byProducts) {
    addMeltingRecipe(name, input, output, temperature, time, byProducts, MeltingRecipe::new);
  }

  @ZenCodeType.Method
  public void addDamageableMeltingRecipe(String name, IIngredient input, IFluidStack output, int temperature, int time, @ZenCodeType.Optional List<IFluidStack> byProducts) {
    addMeltingRecipe(name, input, output, temperature, time, byProducts, DamageableMeltingRecipe::new);
  }

  @ZenCodeType.Method
  public void addOreMeltingRecipe(String name, IIngredient input, IFluidStack output, int temperature, int time, @ZenCodeType.Optional List<IFluidStack> byProducts) {
    addMeltingRecipe(name, input, output, temperature, time, byProducts, OreMeltingRecipe::new);
  }

  @ZenCodeType.Method
  public void addMaterialMeltingRecipe(String name, String inputId, IFluidStack output, int temperature) {
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    MaterialId inputMatId = CRTHelper.getMaterialId(inputId);
    MaterialMeltingRecipe recipe = new MaterialMeltingRecipe(id, inputMatId, temperature, output.getInternal());
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @Override
  public void removeRecipe(IItemStack output) {
    throw new IllegalArgumentException("Cannot remove Melting Recipes by an IItemStack output! Use `removeByName(String name)` instead!");
  }

  @Override
  public IRecipeType<IMeltingRecipe> getRecipeType() {
    return RecipeTypes.MELTING;
  }

}

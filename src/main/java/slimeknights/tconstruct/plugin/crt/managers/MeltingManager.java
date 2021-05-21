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
import net.minecraftforge.fluids.FluidStack;
import org.openzen.zencode.java.ZenCodeType;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.melting.DamageableMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.IMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipe;
import slimeknights.tconstruct.library.recipe.melting.OreMeltingRecipe;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@ZenRegister
@ZenCodeType.Name("mods.tconstruct.Melting")
public class MeltingManager implements IRecipeManager {

  @ZenCodeType.Method
  public void addMeltingRecipe(String name, IIngredient input, IFluidStack output, int temperature, int time, @ZenCodeType.Optional List<IFluidStack> byProducts) {
    name = fixRecipeName(name);
    if (byProducts == null) {
      byProducts = Collections.emptyList();
    }
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient ingredient = input.asVanillaIngredient();
    FluidStack outputFluid = output.getInternal();
    List<FluidStack> byproductStacks = byProducts.stream().map(IFluidStack::getInternal).collect(Collectors.toList());
    MeltingRecipe recipe = new MeltingRecipe(id, "", ingredient, outputFluid, temperature, time, byproductStacks);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addDamageableMeltingRecipe(String name, IIngredient input, IFluidStack output, int temperature, int time, @ZenCodeType.Optional List<IFluidStack> byProducts) {
    name = fixRecipeName(name);
    if (byProducts == null) {
      byProducts = Collections.emptyList();
    }
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient ingredient = input.asVanillaIngredient();
    FluidStack outputFluid = output.getInternal();
    List<FluidStack> byproductStacks = byProducts.stream().map(IFluidStack::getInternal).collect(Collectors.toList());
    DamageableMeltingRecipe recipe = new DamageableMeltingRecipe(id, "", ingredient, outputFluid, temperature, time, byproductStacks);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addOreMeltingRecipe(String name, IIngredient input, IFluidStack output, int temperature, int time, @ZenCodeType.Optional List<IFluidStack> byProducts) {
    name = fixRecipeName(name);
    if (byProducts == null) {
      byProducts = Collections.emptyList();
    }
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    Ingredient ingredient = input.asVanillaIngredient();
    FluidStack outputFluid = output.getInternal();
    List<FluidStack> byproductStacks = byProducts.stream().map(IFluidStack::getInternal).collect(Collectors.toList());
    OreMeltingRecipe recipe = new OreMeltingRecipe(id, "", ingredient, outputFluid, temperature, time, byproductStacks);
    CraftTweakerAPI.apply(new ActionAddRecipe(this, recipe));
  }

  @ZenCodeType.Method
  public void addMaterialMeltingRecipe(String name, Item item, int cost) {
    if (!(item instanceof IMaterialItem)) {
      throw new IllegalArgumentException(ExpandItem.getDefaultInstance(item).getCommandString() + " is not a valid IMaterialItem! You can use `/ct dump ticMaterialItems` to view valid items!");
    }
    name = fixRecipeName(name);
    ResourceLocation id = new ResourceLocation("crafttweaker", name);
    MaterialMeltingRecipe recipe = new MaterialMeltingRecipe(id, "", ((IMaterialItem) item), cost);
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

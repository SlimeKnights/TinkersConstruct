package slimeknights.tconstruct.library.recipe.casting.container;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Casting recipe that takes an arbitrary fluid for a given amount and fills a container
 */
@RequiredArgsConstructor
public class ContainerFillingRecipe implements ICastingRecipe, IMultiRecipe<DisplayCastingRecipe> {
  @Getter
  private final RecipeType<?> type;
  @Getter
  private final RecipeSerializer<?> serializer;
  @Getter
  private final ResourceLocation id;
  @Getter
  private final String group;
  @Getter
  private final int fluidAmount;
  @Getter
  private final Item container;

  @Override
  public int getFluidAmount(ICastingContainer inv) {
    Fluid fluid = inv.getFluid();
    return inv.getStack().getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
              .map(handler -> handler.fill(new FluidStack(fluid, this.fluidAmount), FluidAction.SIMULATE))
              .orElse(0);
  }

  @Override
  public boolean isConsumed() {
    return true;
  }

  @Override
  public boolean switchSlots() {
    return false;
  }

  @Override
  public int getCoolingTime(ICastingContainer inv) {
    return 5;
  }

  @Override
  public boolean matches(ICastingContainer inv, Level worldIn) {
    ItemStack stack = inv.getStack();
    Fluid fluid = inv.getFluid();
    return stack.getItem() == this.container.asItem()
           && stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                   .filter(handler -> handler.fill(new FluidStack(fluid, this.fluidAmount), FluidAction.SIMULATE) > 0)
                   .isPresent();
  }

  /** @deprecated use {@link ICastingRecipe#assemble(Container)} */
  @Override
  @Deprecated
  public ItemStack getResultItem() {
    return new ItemStack(this.container);
  }

  @Override
  public ItemStack assemble(ICastingContainer inv) {
    ItemStack stack = inv.getStack().copy();
    return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(handler -> {
      handler.fill(new FluidStack(inv.getFluid(), this.fluidAmount, inv.getFluidTag()), FluidAction.EXECUTE);
      return handler.getContainer();
    }).orElse(stack);
  }

  /* Display */
  /** Cache of items to display for this container */
  private List<DisplayCastingRecipe> displayRecipes = null;

  @Override
  public List<DisplayCastingRecipe> getRecipes() {
    if (displayRecipes == null) {
      List<ItemStack> casts = Collections.singletonList(new ItemStack(container));
      displayRecipes = ForgeRegistries.FLUIDS.getValues().stream()
                                             .filter(fluid -> fluid.getBucket() != Items.AIR && fluid.isSource(fluid.defaultFluidState()))
                                             .map(fluid -> {
                                               FluidStack fluidStack = new FluidStack(fluid, fluidAmount);
                                               ItemStack stack = new ItemStack(container);
                                               stack = FluidUtil.getFluidHandler(stack).map(handler -> {
                                                 handler.fill(fluidStack, FluidAction.EXECUTE);
                                                 return handler.getContainer();
                                               }).orElse(stack);
                                               return new DisplayCastingRecipe(getType(), casts, Collections.singletonList(fluidStack), stack, 5, true);
                                             })
                                             .toList();
    }
    return displayRecipes;
  }

  /** Serializer for {@link ContainerFillingRecipe} */
  @AllArgsConstructor
  public static class Serializer implements LoggingRecipeSerializer<ContainerFillingRecipe> {
    private final Supplier<RecipeType<ICastingRecipe>> type;

    @Override
    public ContainerFillingRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
      String group = GsonHelper.getAsString(json, "group", "");
      int fluidAmount = GsonHelper.getAsInt(json, "fluid_amount");
      Item result = GsonHelper.getAsItem(json, "container");
      return new ContainerFillingRecipe(type.get(), this, recipeId, group, fluidAmount, result);
    }

    @Nullable
    @Override
    public ContainerFillingRecipe fromNetworkSafe(ResourceLocation recipeId, FriendlyByteBuf buffer) {
      String group = buffer.readUtf(Short.MAX_VALUE);
      int fluidAmount = buffer.readInt();
      Item result = RecipeHelper.readItem(buffer);
      return new ContainerFillingRecipe(type.get(), this, recipeId, group, fluidAmount, result);
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, ContainerFillingRecipe recipe) {
      buffer.writeUtf(recipe.group);
      buffer.writeInt(recipe.fluidAmount);
      RecipeHelper.writeItem(buffer, recipe.container);
    }
  }
}

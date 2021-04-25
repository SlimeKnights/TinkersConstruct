package slimeknights.tconstruct.library.recipe.casting.container;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Casting recipe that takes an arbitrary fluid for a given amount and fills a container
 */
@RequiredArgsConstructor
public abstract class ContainerFillingRecipe implements ICastingRecipe, IMultiRecipe<DisplayCastingRecipe> {
  @Getter
  protected final IRecipeType<?> type;
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final int fluidAmount;
  @Getter
  protected final Item container;

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    Fluid fluid = inv.getFluid();
    return inv.getStack().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
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
  public int getCoolingTime(ICastingInventory inv) {
    return 5;
  }

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    ItemStack stack = inv.getStack();
    Fluid fluid = inv.getFluid();
    return stack.getItem() == this.container.asItem()
           && stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY)
                   .filter(handler -> handler.fill(new FluidStack(fluid, this.fluidAmount), FluidAction.SIMULATE) > 0)
                   .isPresent();
  }

  /** @deprecated use {@link ICastingRecipe#getCraftingResult(IInventory)}
   */
  @Override
  @Deprecated
  public ItemStack getRecipeOutput() {
    return new ItemStack(this.container);
  }

  @Override
  public ItemStack getCraftingResult(ICastingInventory inv) {
    ItemStack stack = inv.getStack().copy();
    return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(handler -> {
      handler.fill(new FluidStack(inv.getFluid(), this.fluidAmount), FluidAction.EXECUTE);
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
                                             .filter(fluid -> fluid.getFilledBucket() != Items.AIR && fluid.isSource(fluid.getDefaultState()))
                                             .map(fluid -> {
                                               FluidStack fluidStack = new FluidStack(fluid, fluidAmount);
                                               ItemStack stack = new ItemStack(container);
                                               stack = FluidUtil.getFluidHandler(stack).map(handler -> {
                                                 handler.fill(fluidStack, FluidAction.EXECUTE);
                                                 return handler.getContainer();
                                               }).orElse(stack);
                                               return new DisplayCastingRecipe(getType(), casts, Collections.singletonList(fluidStack), stack, 5, true);
                                             })
                                             .collect(Collectors.toList());
    }
    return displayRecipes;
  }

  /** Basin implementation */
  public static class Basin extends ContainerFillingRecipe {
    public Basin(ResourceLocation idIn, String groupIn, int fluidAmount, Item containerIn) {
      super(RecipeTypes.CASTING_BASIN, idIn, groupIn, fluidAmount, containerIn);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinFillingRecipeSerializer.get();
    }
  }

  /** Table implementation */
  public static class Table extends ContainerFillingRecipe {

    public Table(ResourceLocation idIn, String groupIn, int fluidAmount, Item containerIn) {
      super(RecipeTypes.CASTING_TABLE, idIn, groupIn, fluidAmount, containerIn);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableFillingRecipeSerializer.get();
    }
  }
}

package slimeknights.tconstruct.plugin.jei.util;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IIngredientManager.IIngredientListener;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import slimeknights.tconstruct.library.fluid.EmptyFluidHandlerItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Handler to remove tanks when their fluid is removed from JEI. */
public record TankHidingIngredientListener(IIngredientManager manager, List<Item> tanks) implements IIngredientListener {
  /** Gets all changed tanks from the given ingredients */
  private List<ItemStack> getTanks(Collection<? extends ITypedIngredient<?>> ingredients) {
    List<ItemStack> list = new ArrayList<>();
    for (ITypedIngredient<?> ingredient : ingredients) {
      FluidStack fluid = ingredient.getIngredient(ForgeTypes.FLUID_STACK).orElse(FluidStack.EMPTY);
      if (!fluid.isEmpty()) {
        for (Item item : tanks) {
          ItemStack tank = new ItemStack(item);
          IFluidHandlerItem handler = tank.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(EmptyFluidHandlerItem.INSTANCE);
          if (handler.getTanks() > 0 && handler.fill(fluid, FluidAction.EXECUTE) > 0) {
            list.add(handler.getContainer());
          }
        }
      }
    }
    return list;
  }

  @Override
  public <V> void onIngredientsAdded(IIngredientHelper<V> ingredientHelper, Collection<ITypedIngredient<V>> ingredients) {
    List<ItemStack> tanks = getTanks(ingredients);
    if (!tanks.isEmpty()) {
      manager.addIngredientsAtRuntime(VanillaTypes.ITEM_STACK, tanks);
    }
  }

  @Override
  public <V> void onIngredientsRemoved(IIngredientHelper<V> ingredientHelper, Collection<ITypedIngredient<V>> ingredients) {
    List<ItemStack> tanks = getTanks(ingredients);
    if (!tanks.isEmpty()) {
      manager.removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, tanks);
    }
  }
}

package slimeknights.tconstruct.plugin.jei.transfer;

import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.plugin.jei.TConstructJEIConstants;
import slimeknights.tconstruct.tables.menu.TinkerStationContainerMenu;

import java.util.ArrayList;
import java.util.List;

public class TinkerStationTransferInfo implements IRecipeTransferInfo<TinkerStationContainerMenu,IDisplayModifierRecipe> {
  @Override
  public Class<TinkerStationContainerMenu> getContainerClass() {
    return TinkerStationContainerMenu.class;
  }

  @SuppressWarnings("removal")
  @Override
  public Class<IDisplayModifierRecipe> getRecipeClass() {
    return IDisplayModifierRecipe.class;
  }

  @SuppressWarnings("removal")
  @Override
  public ResourceLocation getRecipeCategoryUid() {
    return TConstructJEIConstants.MODIFIERS.getUid();
  }

  @Override
  public RecipeType<IDisplayModifierRecipe> getRecipeType() {
    return TConstructJEIConstants.MODIFIERS;
  }

  @Override
  public boolean canHandle(TinkerStationContainerMenu container, IDisplayModifierRecipe recipe) {
    return true;
  }

  @Override
  public List<Slot> getRecipeSlots(TinkerStationContainerMenu container, IDisplayModifierRecipe recipe) {
    return container.getInputSlots();
  }

  @Override
  public List<Slot> getInventorySlots(TinkerStationContainerMenu container, IDisplayModifierRecipe recipe) {
    List<Slot> slots = new ArrayList<>();
    // skip over inputs and the output slot
    int start = container.getInputSlots().size() + 1;
    for(int i = start; i < start + 36; i++) {
      Slot slot = container.getSlot(i);
      slots.add(slot);
    }

    return slots;
  }
}

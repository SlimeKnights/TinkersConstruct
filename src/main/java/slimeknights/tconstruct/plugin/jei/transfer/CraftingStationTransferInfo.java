package slimeknights.tconstruct.plugin.jei.transfer;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraftforge.common.crafting.IShapedRecipe;
import slimeknights.mantle.client.SafeClientAccess;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.menu.CraftingStationContainerMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class to dynamically provide the right slot count to JEI
 */
public class CraftingStationTransferInfo implements IRecipeTransferInfo<CraftingStationContainerMenu, CraftingRecipe> {
  @Override
  public Class<? extends CraftingStationContainerMenu> getContainerClass() {
    return CraftingStationContainerMenu.class;
  }

  @Override
  public Optional<MenuType<CraftingStationContainerMenu>> getMenuType() {
    return Optional.of(TinkerTables.craftingStationContainer.get());
  }

  @Override
  public RecipeType<CraftingRecipe> getRecipeType() {
    return RecipeTypes.CRAFTING;
  }

  @Override
  public List<Slot> getInventorySlots(CraftingStationContainerMenu container, CraftingRecipe recipe) {
    List<Slot> slots = new ArrayList<>();

    // 36 for player inventory
    int totalSize = container.slots.size();
    int sideInventoryEnd = totalSize - 36;

    // first, add all inventory slots, ensures they are first for emptying the table
    for (int i = sideInventoryEnd; i < totalSize; i++) {
      slots.add(container.getSlot(i));
    }

    // next, add side inventory. shouldn't be a problem due to the blacklist
    // 10 slots for the crafting table
    Player player = SafeClientAccess.getPlayer();
    if (player != null) {
      for (int i = 10; i < sideInventoryEnd; i++) {
        Slot slot = container.getSlot(i);
        // skip slots with no item, we don't want JEI putting stuff in empty slots as that's confusing
        if (slot.hasItem() && slot.allowModification(player)) {
          slots.add(container.getSlot(i));
        }
      }
    }
    return slots;
  }

  @Override
  public List<Slot> getRecipeSlots(CraftingStationContainerMenu container, CraftingRecipe recipe) {
    List<Slot> slots = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      slots.add(container.getSlot(i));
    }
    return slots;
  }

  @Override
  public boolean canHandle(CraftingStationContainerMenu container, CraftingRecipe recipe) {
    if (recipe instanceof IShapedRecipe<?> shaped) {
      return shaped.getRecipeWidth() <= 3 && shaped.getRecipeHeight() <= 3;
    }
    return recipe.getIngredients().size() <= 9;
  }
}

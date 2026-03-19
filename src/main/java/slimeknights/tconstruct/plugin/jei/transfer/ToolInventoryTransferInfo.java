package slimeknights.tconstruct.plugin.jei.transfer;

import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.CraftingRecipe;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.menu.ReadOnlySlot;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Implements transfer info for crafting from a tool inventory. Based partially on {@code mezz.jei.library.transfer.PlayerRecipeTransferHandler} */
@Internal
public class ToolInventoryTransferInfo implements IRecipeTransferHandler<ToolContainerMenu, CraftingRecipe>, IRecipeTransferInfo<ToolContainerMenu, CraftingRecipe> {
  /** Indexes from the crafting recipe inputs that fit into the player crafting grid when we trim the right and bottom edges. */
  private static final IntSet PLAYER_INV_INDEXES = IntArraySet.of(0, 1, 3, 4);

  private final IRecipeTransferHandlerHelper handlerHelper;
  private final IRecipeTransferHandler<ToolContainerMenu, CraftingRecipe> handler;
  public ToolInventoryTransferInfo(IRecipeTransferHandlerHelper handlerHelper) {
    this.handlerHelper = handlerHelper;
    this.handler = handlerHelper.createUnregisteredRecipeTransferHandler(this);
  }

  @Override
  public Class<? extends ToolContainerMenu> getContainerClass() {
    return ToolContainerMenu.class;
  }

  @Override
  public Optional<MenuType<ToolContainerMenu>> getMenuType() {
    return Optional.of(TinkerTools.toolContainer.get());
  }

  @Override
  public RecipeType<CraftingRecipe> getRecipeType() {
    return RecipeTypes.CRAFTING;
  }

  @Nullable
  @Override
  public IRecipeTransferError transferRecipe(ToolContainerMenu container, CraftingRecipe recipe, IRecipeSlotsView recipeSlotsView, Player player, boolean maxTransfer, boolean doTransfer) {
    // if we have no crafting slots, nothing to do
    int slots = container.getToolInventoryStart() - 1;
    if (slots <= 0) {
      return handlerHelper.createInternalError();
    }
    // must have server support
    if (!handlerHelper.recipeTransferHasServerSupport()) {
      Component tooltipMessage = Component.translatable("jei.tooltip.error.recipe.transfer.no.server");
      return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
    }

    // if we have a full crafting grid, just do the normal transfer
    if (slots >= 9) {
      return handler.transferRecipe(container, recipe, recipeSlotsView, player, maxTransfer, doTransfer);
    }

    // if we have a small grid, recipe must fit in the 2x2
    List<IRecipeSlotView> slotViews = recipeSlotsView.getSlotViews(RecipeIngredientRole.INPUT);
    if (!validateIngredientsOutsidePlayerGridAreEmpty(slotViews)) {
      Component tooltipMessage = Component.translatable(
        "jei.tooltip.error.recipe.transfer.too.large.player.inventory"
      );
      return this.handlerHelper.createUserErrorWithTooltip(tooltipMessage);
    }

    // filter the crafting table input slots to player inventory input slots
    List<IRecipeSlotView> filteredSlotViews = filterSlots(slotViews);
    IRecipeSlotsView filteredRecipeSlots = this.handlerHelper.createRecipeSlotsView(filteredSlotViews);
    return this.handler.transferRecipe(container, recipe, filteredRecipeSlots, player, maxTransfer, doTransfer);
  }

  @Override
  public boolean canHandle(ToolContainerMenu container, CraftingRecipe recipe) {
    return true;
  }

  @Override
  public List<Slot> getInventorySlots(ToolContainerMenu container, CraftingRecipe recipe) {
    List<Slot> slots = new ArrayList<>();
    int playerStart = container.getPlayerInventoryStart();
    for (int i = container.getToolInventoryStart(); i < container.slots.size(); i++) {
      Slot slot = container.getSlot(i);
      if (!(slot instanceof ReadOnlySlot)) {
        slots.add(container.getSlot(i));
      }
    }
    return slots;
  }

  @Override
  public List<Slot> getRecipeSlots(ToolContainerMenu container, CraftingRecipe recipe) {
    // if crafting is enabled, first slot is the result. Next 4 or 9 is the grid
    // on the chance there is no grid, tool inventory start will be 0 so no slots are added
    List<Slot> slots = new ArrayList<>();
    for (int i = 1; i < container.getToolInventoryStart(); i++) {
      slots.add(container.getSlot(i));
    }
    return slots;
  }

  /** Ensures all recipe inputs are in the 2x2 grid */
  private static boolean validateIngredientsOutsidePlayerGridAreEmpty(List<IRecipeSlotView> slotViews) {
    int bound = slotViews.size();
    for (int i = 0; i < bound; i++) {
      if (!PLAYER_INV_INDEXES.contains(i)) {
        IRecipeSlotView slotView = slotViews.get(i);
        if (!slotView.isEmpty()) {
          return false;
        }
      }
    }
    return true;
  }

  /** Gets all 2x2 grid slots */
  private static List<IRecipeSlotView> filterSlots(List<IRecipeSlotView> slotViews) {
    return PLAYER_INV_INDEXES.intStream().mapToObj(slotViews::get).toList();
  }
}

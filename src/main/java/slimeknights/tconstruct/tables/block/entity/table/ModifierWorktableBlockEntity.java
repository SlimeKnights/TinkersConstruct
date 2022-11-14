package slimeknights.tconstruct.tables.block.entity.table;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.worktable.IModifierWorktableRecipe;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.inventory.LazyResultContainer;
import slimeknights.tconstruct.tables.block.entity.inventory.LazyResultContainer.ILazyCrafter;
import slimeknights.tconstruct.tables.block.entity.inventory.ModifierWorktableContainerWrapper;
import slimeknights.tconstruct.tables.menu.ModifierWorktableContainerMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// TODO: spend some time planning out data flow, its not currently doing it
public class ModifierWorktableBlockEntity extends RetexturedTableBlockEntity implements ILazyCrafter {
  /** Index containing the tool */
  public static final int TINKER_SLOT = 0;
  /** First input slot index */
  public static final int INPUT_START = 1;
  /** Number of input slots */
  public static final int INPUT_COUNT = 2;
  /** Title for the GUI */
  private static final Component NAME = TConstruct.makeTranslation("gui", "modifier_worktable");

  /** Result inventory, lazy loads results */
  @Getter
  private final LazyResultContainer craftingResult;
  /** Crafting inventory for the recipe calls */
  @Getter
  private final ModifierWorktableContainerWrapper inventoryWrapper;

  /** If true, the last recipe is the current recipe. If false, no recipe was found. If null, have not tried recipe lookup */
  private Boolean recipeValid;
  /** Cache of the last recipe, may not be the current one */
  @Nullable
  private IModifierWorktableRecipe lastRecipe;
  /* Current buttons to display */
  @Nonnull
  private List<ModifierEntry> buttons = Collections.emptyList();
  /** Index of the currently selected modifier */
  private int selectedModifierIndex = -1;

  /** Current result, may be modified again later */
  @Nullable @Getter
  private ToolStack result = null;
  /** Current message displayed on the screen */
  @Getter
  private Component currentMessage = TextComponent.EMPTY;

  public ModifierWorktableBlockEntity(BlockPos pos, BlockState state) {
    super(TinkerTables.modifierWorktableTile.get(), pos, state, NAME, 3);
    this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
    this.itemHandlerCap = LazyOptional.of(() -> this.itemHandler);
    this.inventoryWrapper = new ModifierWorktableContainerWrapper(this);
    this.craftingResult = new LazyResultContainer(this);
  }

  /**
   * Selects a modifier by index. Will fetch the buttons list if the index is non-negative
   * @param index  New index
   */
  public void selectModifier(int index) {
    result = null;
    craftingResult.clearContent();
    if (index >= 0) {
      List<ModifierEntry> list = getCurrentButtons();
      if (index < list.size()) {
        selectedModifierIndex = index;
        ModifierEntry entry = list.get(index);

        // last recipe must be nonnull for list to be non-empty
        assert lastRecipe != null;
        RecipeResult<ToolStack> recipeResult = lastRecipe.getResult(inventoryWrapper, entry);
        if (recipeResult.isSuccess()) {
          result = recipeResult.getResult();
          currentMessage = TextComponent.EMPTY;
        } else if (recipeResult.hasError()) {
          currentMessage = recipeResult.getMessage();
        } else {
          currentMessage = lastRecipe.getDescription(inventoryWrapper);
        }
        return;
      }
    }
    // index is either not valid or the list is empty, so just clear
    selectedModifierIndex = -1;
    currentMessage = recipeValid == Boolean.TRUE && lastRecipe != null
                     ? lastRecipe.getDescription(inventoryWrapper)
                     : TextComponent.EMPTY;
  }

  /** Gets the index of the selected pattern */
  public int getSelectedIndex() {
    return selectedModifierIndex;
  }

  private void syncRecipe() {
    if (level != null && !level.isClientSide) {
      syncToRelevantPlayers(this::syncScreen);
    }
  }

  /** Updates the current recipe */
  public IModifierWorktableRecipe updateRecipe(IModifierWorktableRecipe recipe) {
    lastRecipe = recipe;
    recipeValid = true;
    currentMessage = lastRecipe.getDescription(inventoryWrapper);
    buttons = recipe.getModifierOptions(inventoryWrapper);
    //        if (!level.isClientSide) {
    //          syncToRelevantPlayers(this::syncScreen);
    //        }

    // clear the active modifier
    selectModifier(-1);
    return recipe;
  }

  /** Gets the currently active recipe */
  @Nullable
  public IModifierWorktableRecipe getCurrentRecipe() {
    if (recipeValid == Boolean.TRUE) {
      return lastRecipe;
    }
    if (recipeValid == null && level != null) {
      // if the previous recipe matches, flip state to use that again
      if (lastRecipe != null && lastRecipe.matches(inventoryWrapper, level)) {
        return updateRecipe(lastRecipe);
      }
      // look for a new recipe, if it matches cache it
      Optional<IModifierWorktableRecipe> recipe = level.getRecipeManager().getRecipeFor(TinkerRecipeTypes.MODIFIER_WORKTABLE.get(), inventoryWrapper, level);
      if (recipe.isPresent()) {
        return updateRecipe(recipe.get());
      }
      recipeValid = false;
      currentMessage = TextComponent.EMPTY;
      buttons = Collections.emptyList();
      selectModifier(-1);
    }
    // level null or no recipe found
    return null;
  }

  /**
   * Gets a map of all recipes for the current inputs
   * @return  List of recipes for the current inputs
   */
  public List<ModifierEntry> getCurrentButtons() {
    if (level == null) {
      return Collections.emptyList();
    }
    // if last recipe is not fetched, the buttons may be outdated
    getCurrentRecipe();
    return buttons;
  }

  /** Called when a slot changes to clear the current result */
  public void onSlotChanged(int slot) {
    this.inventoryWrapper.refreshInput(slot);
    this.recipeValid = null;
    this.buttons = Collections.emptyList();
    selectModifier(-1);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    ItemStack original = getItem(slot);
    super.setItem(slot, stack);
    // if the stack changed, clear everything
    if (original.getCount() != stack.getCount() || !ItemStack.isSameItemSameTags(original, stack)) {
      onSlotChanged(slot);
    }
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int menuId, Inventory playerInventory, Player playerEntity) {
    return new ModifierWorktableContainerMenu(menuId, playerInventory, this);
  }

  @Override
  public ItemStack calcResult(@Nullable Player player) {
    if (selectedModifierIndex != -1) {
      IModifierWorktableRecipe recipe = getCurrentRecipe();
      if (recipe != null && result != null) {
        return result.createStack(Math.min(getItem(TINKER_SLOT).getCount(), recipe.toolResultSize()));
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  public void onCraft(Player player, ItemStack resultItem, int amount) {
    // the recipe should match if we got this far, but being null is a problem
    if (amount == 0 || this.level == null || lastRecipe == null || result == null) {
      return;
    }

    // we are definitely crafting at this point
    resultItem.onCraftedBy(this.level, player, amount);
    ForgeEventFactory.firePlayerCraftingEvent(player, resultItem, this.inventoryWrapper);
    this.playCraftSound(player);

    // run the recipe, will shrink inputs
    // run both sides for the sake of shift clicking
    this.inventoryWrapper.setPlayer(player);
    this.lastRecipe.updateInputs(result, inventoryWrapper, !level.isClientSide);
    this.inventoryWrapper.setPlayer(null);

    ItemStack tinkerable = this.getItem(TINKER_SLOT);
    if (!tinkerable.isEmpty()) {
      int shrinkToolSlot = lastRecipe.toolResultSize();
      if (tinkerable.getCount() <= shrinkToolSlot) {
        this.setItem(TINKER_SLOT, ItemStack.EMPTY);
      } else {
        this.setItem(TINKER_SLOT, ItemHandlerHelper.copyStackWithSize(tinkerable, tinkerable.getCount() - shrinkToolSlot));
      }
    }
    // screen should reset back to empty now that we crafted
//    syncRecipe();
  }
}

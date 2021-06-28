package slimeknights.tconstruct.tables.tileentity.table;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.hooks.BasicEventHooks;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.shared.inventory.ConfigurableInvWrapperCapability;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.table.PartBuilderContainer;
import slimeknights.tconstruct.tables.tileentity.table.crafting.LazyResultInventory;
import slimeknights.tconstruct.tables.tileentity.table.crafting.PartBuilderInventoryWrapper;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PartBuilderTileEntity extends RetexturedTableTileEntity implements LazyResultInventory.ILazyCrafter {
  /** First slot containing materials */
  public static final int MATERIAL_SLOT = 0;
  /** Second slot containing the patterns */
  public static final int PATTERN_SLOT = 1;

  /** Result inventory, lazy loads results */
  @Getter
  private final LazyResultInventory craftingResult;
  /** Crafting inventory for the recipe calls */
  @Getter
  private final PartBuilderInventoryWrapper inventoryWrapper;

  /* Current buttons to display */
  @Nullable
  private Map<Pattern,IPartBuilderRecipe> recipes = null;
  @Nullable
  private List<Pattern> sortedButtons = null;
  /** Currently selected recipe index */
  private Pattern selectedPattern = null;
  /** Index of the currently selected pattern */
  private int selectedPatternIndex = -2;

  public PartBuilderTileEntity() {
    super(TinkerTables.partBuilderTile.get(), "gui.tconstruct.part_builder", 3);
    this.itemHandler = new ConfigurableInvWrapperCapability(this, false, false);
    this.itemHandlerCap = LazyOptional.of(() -> this.itemHandler);
    this.inventoryWrapper = new PartBuilderInventoryWrapper(this);
    this.craftingResult = new LazyResultInventory(this);
  }

  /**
   * Gets a map of all recipes for the current inputs
   * @return  List of recipes for the current inputs
   */
  protected Map<Pattern,IPartBuilderRecipe> getCurrentRecipes() {
    if (world == null) {
      return Collections.emptyMap();
    }
    if (recipes == null) {
      // no recipes if we lack a pattern
      if (getStackInSlot(PATTERN_SLOT).isEmpty()) {
        recipes = Collections.emptyMap();
        sortedButtons = Collections.emptyList();
      } else {
        // fetch all recipes that can match these inputs, the map ensures the patterns are unique
        recipes = world.getRecipeManager().getRecipes(RecipeTypes.PART_BUILDER).values().stream()
                       .filter(r -> r instanceof IPartBuilderRecipe)
                       .map(r -> (IPartBuilderRecipe)r)
                       .filter(r -> r.partialMatch(inventoryWrapper))
                       .sorted(Comparator.comparing(IRecipe::getId))
                       .collect(Collectors.toMap(IPartBuilderRecipe::getPattern, Function.identity(), (a, b) -> a));
        sortedButtons = recipes.values().stream()
                               .sorted((a, b) -> {
                                 if (a.getCost() != b.getCost()) {
                                   return Integer.compare(a.getCost(), b.getCost());
                                 }
                                 return a.getPattern().compareTo(b.getPattern());
                               })
                               .map(IPartBuilderRecipe::getPattern).collect(Collectors.toList());
      }
    }
    return recipes;
  }

  /** Gets the list of sorted buttons */
  public List<Pattern> getSortedButtons() {
    if (world == null) {
      return Collections.emptyList();
    }
    if (sortedButtons == null) {
      getCurrentRecipes();
    }
    return sortedButtons;
  }

  /** Gets the index of the selected pattern */
  public int getSelectedIndex() {
    if (selectedPatternIndex == -2) {
      if (selectedPattern != null) {
        selectedPatternIndex = getSortedButtons().indexOf(selectedPattern);
      } else {
        selectedPatternIndex = -1;
      }
    }
    return selectedPatternIndex;
  }

  /**
   * Gets the currently selected recipe
   * @return  Selected recipe, or null if invalid or no recipe
   */
  @Nullable
  public IPartBuilderRecipe getPartRecipe() {
    if (selectedPattern != null) {
      return getCurrentRecipes().get(selectedPattern);
    }
    return null;
  }

  /**
   * Gets the material recipe for the material slot
   * @return  Material slot
   */
  @Nullable
  public MaterialRecipe getMaterialRecipe() {
    return inventoryWrapper.getMaterial();
  }

  /**
   * Refreshes the current recipe
   * @param refreshRecipeList  If true, refreshes the full recipe list too
   */
  private void refresh(boolean refreshRecipeList) {
    if (refreshRecipeList) {
      this.recipes = null;
      this.sortedButtons = null;
    }
    this.selectedPatternIndex = -2;
    this.craftingResult.clear();
    // update screen display
    if (refreshRecipeList && world != null && !world.isRemote) {
      syncToRelevantPlayers(this::syncScreen);
    }
  }

  /**
   * Selects a recipe in the table
   * @param pattern  New pattern
   */
  public void selectRecipe(@Nullable Pattern pattern) {
    if (pattern != null && getCurrentRecipes().containsKey(pattern)) {
      selectedPattern = pattern;
    } else {
      selectedPattern = null;
    }
    refresh(false);
  }

  /**
   * Selects a pattern by index
   * @param index  New index
   */
  public void selectRecipe(int index) {
    if (index < 0) {
      selectedPattern = null;
    } else {
      List<Pattern> list = getSortedButtons();
      if (index < list.size()) {
        selectedPattern = list.get(index);
      } else {
        selectedPattern = null;
      }
    }
    refresh(false);
  }

  @Override
  public void setInventorySlotContents(int slot, ItemStack stack) {
    ItemStack original = getStackInSlot(slot);
    super.setInventorySlotContents(slot, stack);
    if (stack.getItem() != original.getItem()) {
      if (slot == MATERIAL_SLOT) {
        this.inventoryWrapper.refreshMaterial();
      }
      refresh(true);
    }
  }

  @Nullable
  @Override
  public Container createMenu(int menuId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
    return new PartBuilderContainer(menuId, playerInventory, this);
  }

  @Override
  public ItemStack calcResult(@Nullable PlayerEntity player) {
    if (world != null) {
      IPartBuilderRecipe recipe = getPartRecipe();
      if (recipe != null && recipe.matches(inventoryWrapper, world)) {
        return recipe.getCraftingResult(inventoryWrapper);
      }
    }
    return ItemStack.EMPTY;
  }

  /**
   * Shrinks the given slot
   * @param slot    Slot
   * @param amount  Amount to shrink
   */
  private void shrinkSlot(int slot, int amount) {
    ItemStack stack = getStackInSlot(slot);
    if (!stack.isEmpty()) {
      if (stack.getCount() <= amount) {
        setInventorySlotContents(slot, ItemStack.EMPTY);
      } else {
        stack.shrink(amount);
      }
    }
  }

  @Override
  public ItemStack onCraft(PlayerEntity player, ItemStack result, int amount) {
    if (amount == 0 || this.world == null) {
      return ItemStack.EMPTY;
    }
    // the recipe should match if we got this far, but being null is a problem
    IPartBuilderRecipe recipe = getPartRecipe();
    if (recipe == null) {
      return ItemStack.EMPTY;
    }

    // we are definitely crafting at this point
    result.onCrafting(this.world, player, amount);
    BasicEventHooks.firePlayerCraftingEvent(player, result, this.inventoryWrapper);
    this.playCraftSound(player);

    // give the player any leftovers
    ItemStack leftover = recipe.getLeftover(inventoryWrapper);
    if (!leftover.isEmpty()) {
      ItemHandlerHelper.giveItemToPlayer(player, leftover);
    }

    // shrink the inputs
    shrinkSlot(MATERIAL_SLOT, recipe.getItemsUsed(inventoryWrapper));
    shrinkSlot(PATTERN_SLOT, 1);

    // sync display, mainly for the material value
    if (world != null && !world.isRemote) {
      syncToRelevantPlayers(this::syncScreen);
    }

    // finally, return the result
    return result;
  }
}

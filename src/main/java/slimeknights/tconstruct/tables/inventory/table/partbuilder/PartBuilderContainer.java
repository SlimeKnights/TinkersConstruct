package slimeknights.tconstruct.tables.inventory.table.partbuilder;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.Property;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.World;
import slimeknights.mantle.inventory.IContainerCraftingCustom;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.PartRecipe;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.client.inventory.table.ResultSlot;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class PartBuilderContainer extends BaseStationContainer<PartBuilderTileEntity> implements IContainerCraftingCustom {

  // recipe
  private final Property selectedRecipe = Property.create();
  @Getter
  private List<PartRecipe> partRecipes = Lists.newArrayList();
  private final Predicate<PartRecipe> partFilter;
  // inventory
  private final Inventory craftResult;
  @Getter
  private final PartBuilderInventoryWrapper craftInventory;
  // slots
  @Getter
  private final Slot patternSlot;
  @Getter
  private final Slot inputSlot;
  @Getter
  private final Slot outputSlot;
  // misc
  private final World world;

  public PartBuilderContainer(int windowIdIn, PlayerInventory playerInventoryIn, @Nullable PartBuilderTileEntity partBuilderTileEntity) {
    super(TinkerTables.partBuilderContainer.get(), windowIdIn, playerInventoryIn, partBuilderTileEntity);

    // inventories
    // TODO: what if its null?
    this.craftInventory = new PartBuilderInventoryWrapper(partBuilderTileEntity);
    this.craftResult = new CraftingResultInventory();

    // misc
    this.world = playerInventoryIn.player.world;

    // slots
    // outputs
    this.addSlot(this.outputSlot = new ResultSlot(this, playerInventoryIn.player, this.craftResult, 0, 148, 33));
    // TODO: slot for leftovers
    // inputs
    this.addSlot(this.patternSlot = new PatternSlot(this, 8, 34));
    this.addSlot(this.inputSlot = new MaterialSlot(this, 29, 34));
    this.addInventorySlots();

    // recipes
    this.addProperty(this.selectedRecipe);
    this.partFilter = (recipe) -> recipe.matchesPattern(craftInventory);

    // update initial recipes
    this.updatePattern();
    this.craftInventory.updateMaterial();
    this.updateResult();
  }

  public PartBuilderContainer(int id, PlayerInventory inv, PacketByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, PartBuilderTileEntity.class));
  }

  /**
   * Gets the currently selected part recipe
   * @return  Current part recipe
   */
  @Nullable
  public PartRecipe getPartRecipe() {
    int index = this.selectedRecipe.get();
    if (index < 0 || index >= this.partRecipes.size()) {
      return null;
    }
    return this.partRecipes.get(index);
  }

  /**
   * Gets the current material recipe based on the material slot
   * @return  Current material recipe
   */
  @Nullable
  public MaterialRecipe getMaterialRecipe() {
    return craftInventory.getMaterial();
  }

  @Override
  public void onContentChanged(Inventory inventoryIn) {
    // TODO: still needed?
  }

  /**
   * Called when a pattern button is pressed
   */
  @Override
  public boolean onButtonClick(PlayerEntity playerIn, int id) {
    if (id >= 0 && id < this.partRecipes.size()) {
      this.selectedRecipe.set(id);
      this.updateResult();
    }
    return true;
  }

  /**
   * Called when the pattern slot changes to update the list of recipes
   */
  private void updatePattern() {
    this.partRecipes.clear();
    this.selectedRecipe.set(-1);
    this.craftResult.setStack(0, ItemStack.EMPTY);
    // update the list of recipes
    if (!patternSlot.getStack().isEmpty()) {
      this.partRecipes = RecipeHelper.getUIRecipes(world.getRecipeManager(), RecipeTypes.PART_BUILDER, PartRecipe.class, partFilter);
    }
    this.updateScreen();
  }

  /**
   * Called when the recipe otherwise chances to update the output slot
   */
  private void updateResult() {
    // no pattern or input -> no output
    ItemStack output = ItemStack.EMPTY;
    if (this.patternSlot.hasStack() && this.inputSlot.hasStack()) {
      // recipe must match current inventory
      PartRecipe recipe = this.getPartRecipe();
      if (recipe != null && recipe.matches(craftInventory, world)) {
        output = recipe.getCraftingResult(craftInventory);
      }
      // TODO: consider a message if it fails to match, the recipe will need to be modified so wrong count does not error
    }
    this.craftResult.setStack(0, output);
    this.updateScreen();
  }

  @Override
  public void onCrafting(PlayerEntity playerEntity, ItemStack output, Inventory craftMatrix) {
    // TODO: who calls, and when?
    PartRecipe recipe = this.getPartRecipe();
    // output parameter is empty on shift click, just ignore it and shrink once
    if (recipe != null) {
      // TODO: probably set a flag to prevent recipe updates for a bit
      // TODO: it does not currently update? life is weird
      this.patternSlot.takeStack(1);
      this.inputSlot.takeStack(recipe.getItemsUsed(craftInventory));

      // update slots and output
      if (inputSlot.getStack().isEmpty()) {
        craftInventory.updateMaterial();
      }
      // empty means no more pattern
      if (patternSlot.getStack().isEmpty()) {
        this.updatePattern();
      } else {
        this.updateResult();
      }
    }
  }

  @Override
  public boolean canInsertIntoSlot(ItemStack stack, Slot slotIn) {
    return slotIn.inventory != this.craftResult && super.canInsertIntoSlot(stack, slotIn);
  }

  /* Methods for the screen */

  /**
   * Checks if the pattern slot has a pattern
   */
  public boolean hasPatternInPatternSlot() {
    return this.patternSlot.hasStack() && !this.partRecipes.isEmpty();
  }

  /**
   * Returns the index of the selected part recipe.
   */
  public int getSelectedPartRecipe() {
    return this.selectedRecipe.get();
  }

  /**
   * Slot that updates the material recipe on change
   */
  public static class MaterialSlot extends Slot {
    private final PartBuilderContainer container;
    @Nullable
    private Item lastItem;
    private MaterialSlot(PartBuilderContainer container, int x, int y) {
      // TODO: what if null?
      super(container.getTile(), PartBuilderTileEntity.MATERIAL_SLOT, x, y);
      this.container = container;
    }

    @Override
    public void markDirty() {
      super.markDirty();
      // TODO: this is not called from recipe update, will other players call it?
      // update material recipe if there was a change
      //ItemStack newStack = getStack();
      //if (newStack.getItem() != lastItem) {
        container.craftInventory.updateMaterial();
        //lastItem = newStack.getItem();
      //}
      container.updateResult();
    }
  }

  /**
   * Slot for the pattern, updates buttons on change
   */
  public static class PatternSlot extends Slot {
    private final PartBuilderContainer container;
    private PatternSlot(PartBuilderContainer container, int x, int y) {
      super(container.getTile(), PartBuilderTileEntity.PATTERN_SLOT, x, y);
      this.container = container;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
      // TODO: tag
      return stack.getItem() == TinkerTables.pattern.get();
    }

    @Override
    public void markDirty() {
      super.markDirty();
      // TODO: should not update if the item does not change
      container.updatePattern();
    }
  }
}

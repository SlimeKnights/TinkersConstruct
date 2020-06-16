package slimeknights.tconstruct.tables.inventory.table;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.inventory.CraftingCustomSlot;
import slimeknights.mantle.inventory.IContainerCraftingCustom;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.shared.inventory.PersistentCraftingInventory;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.chest.PatternSlot;
import slimeknights.tconstruct.tables.recipe.material.MaterialRecipe;
import slimeknights.tconstruct.tables.recipe.part.PartRecipe;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class PartBuilderContainer extends TinkerStationContainer<PartBuilderTileEntity> implements IContainerCraftingCustom {

  private final IntReferenceHolder selectedRecipe = IntReferenceHolder.single();
  private List<PartRecipe> partRecipes = Lists.newArrayList();
  private List<MaterialRecipe> materialRecipes = Lists.newArrayList();

  public IInventory craftResult;
  public PersistentCraftingInventory craftMatrix;

  private final Slot patternSlot;
  private final Slot inputSlot;

  /**
   * The {@plainlink ItemStack} set in the input slot by the player.
   */
  private ItemStack itemStackInput = ItemStack.EMPTY;

  private final PlayerEntity player;
  private final World world;

  public PartBuilderContainer(int windowIdIn, @Nullable PlayerInventory playerInventoryIn, PartBuilderTileEntity partBuilderTileEntity) {
    super(TinkerTables.partBuilderContainer.get(), windowIdIn, playerInventoryIn, partBuilderTileEntity);

    this.craftMatrix = new PersistentCraftingInventory(this, partBuilderTileEntity, 1, 3);
    this.craftResult = new CraftResultInventory();
    this.player = playerInventoryIn.player;
    this.world = playerInventoryIn.player.world;

    this.addSlot(new CraftingCustomSlot(this, playerInventoryIn.player, this.craftMatrix, this.craftResult, 0, 143, 33));

    // pattern slot
    this.addSlot(this.patternSlot = new PatternSlot(this.craftMatrix, 1, 8, 34));

    // material slots
    this.addSlot(this.inputSlot = new Slot(this.craftMatrix, 0, 30, 34) {
      /*
       * Fix issues related to the output not updating when you put a material in the slot.
       */
      @Override
      public void onSlotChanged() {
        super.onSlotChanged();
        PartBuilderContainer.this.onCraftMatrixChanged(PartBuilderContainer.this.craftMatrix);
      }
    });

    this.addInventorySlots();

    this.trackInt(this.selectedRecipe);

    this.onCraftMatrixChanged(this.craftMatrix);
  }

  public PartBuilderContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, PartBuilderTileEntity.class));
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    ItemStack itemstack = this.patternSlot.getStack();

    if (itemstack.getItem() != this.itemStackInput.getItem()) {
      this.itemStackInput = itemstack.copy();
      this.updateAvailablePartRecipes(inventoryIn, itemstack);
    }

    this.updateAvailableMaterialRecipes(inventoryIn);

    this.updateResult();
  }

  // Sets the result in the output slot depending on the input!
  public void updateResult() {
    // no pattern -> no output
    if (!this.patternSlot.getHasStack() || !this.inputSlot.getHasStack()) {
      this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
      this.updateGUI();
    } else {
      Throwable throwable = null;
      ItemStack toolPart = ItemStack.EMPTY;

      if (!this.partRecipes.isEmpty() && this.selectedRecipe.get() != -1) {
        try {
          toolPart = ToolBuildHandler.tryToBuildToolPart(this.getPartRecipe(),
            this.getMaterialRecipe(),
            this.patternSlot.getStack(),
            this.inputSlot.getStack(),
            false);
        } catch (TinkerGuiException exception) {
          throwable = exception;
        }
      }

      this.craftResult.setInventorySlotContents(0, toolPart);

      if (throwable != null) {
        this.error(throwable.getMessage());
      } else {
        this.updateGUI();
      }
    }
  }

  @Override
  public void onCrafting(PlayerEntity playerEntity, ItemStack output, IInventory craftMatrix) {
    ItemStack toolPart = ItemStack.EMPTY;

    if (!this.partRecipes.isEmpty() && this.selectedRecipe.get() != -1) {
      try {
        toolPart = ToolBuildHandler.tryToBuildToolPart(this.getPartRecipe(),
          this.getMaterialRecipe(),
          this.patternSlot.getStack(),
          this.inputSlot.getStack(),
          true);
      } catch (TinkerGuiException ignored) {
      }

      if (toolPart == ItemStack.EMPTY) {
        return;
      }
    }

    this.updateResult();
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
    return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
  }

  @OnlyIn(Dist.CLIENT)
  public List<PartRecipe> getPartRecipeList() {
    return this.partRecipes;
  }

  @OnlyIn(Dist.CLIENT)
  public int getPartRecipeListSize() {
    return this.partRecipes.size();
  }

  @OnlyIn(Dist.CLIENT)
  public boolean hasPatternInPatternSlot() {
    return this.patternSlot.getHasStack() && !this.partRecipes.isEmpty();
  }

  /**
   * Returns the index of the selected recipe.
   */
  @OnlyIn(Dist.CLIENT)
  public int getSelectedPartRecipe() {
    return this.selectedRecipe.get();
  }

  /**
   * Handles the given Button-click on the server, currently only used by enchanting. Name is for legacy.
   */
  @Override
  public boolean enchantItem(PlayerEntity playerIn, int id) {
    if (id >= 0 && id < this.partRecipes.size()) {
      this.selectedRecipe.set(id);
      this.updateResult();
    }

    return true;
  }

  private void updateAvailablePartRecipes(IInventory inventoryIn, ItemStack stack) {
    this.partRecipes.clear();
    this.selectedRecipe.set(-1);
    this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);

    if (!stack.isEmpty()) {
      this.partRecipes = this.world.getRecipeManager().getRecipes(TinkerTables.partRecipeType, inventoryIn, this.world);
    }
  }

  private void updateAvailableMaterialRecipes(IInventory inventoryIn) {
    this.materialRecipes.clear();

    this.materialRecipes = this.world.getRecipeManager().getRecipes(TinkerTables.materialRecipeType, inventoryIn, this.world);
  }

  @Nullable
  public MaterialRecipe getMaterialRecipe() {
    for (MaterialRecipe candidate : this.materialRecipes) {
      if (candidate.matches(this.craftMatrix, this.world)) {
        return candidate;
      }
    }

    return null;
  }

  public PartRecipe getPartRecipe() {
    return this.partRecipes.get(this.selectedRecipe.get());
  }
}

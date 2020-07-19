package slimeknights.tconstruct.tables.inventory.table.toolstation;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.inventory.IContainerCraftingCustom;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.SoundHelper;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.toolstation.IToolStationRecipe;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.client.inventory.table.ToolStationScreen;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.network.ToolStationSelectionPacket;
import slimeknights.tconstruct.tables.network.ToolStationTextPacket;
import slimeknights.tconstruct.tables.tileentity.table.ToolStationTileEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ToolStationContainer extends TinkerStationContainer<ToolStationTileEntity> implements IContainerCraftingCustom {

  private final PlayerEntity player;
  protected ToolStationOutSlot outSlot;
  protected ItemStack selectedTool = ItemStack.EMPTY;
  protected int activeSlots;
  public String toolName = "";
  @Getter
  private final ToolStationInventoryWrapper craftInventory;

  // misc
  private final World world;

  public ToolStationContainer(int id, @Nullable PlayerInventory playerInventoryIn, ToolStationTileEntity toolStationTileEntity) {
    super(TinkerTables.toolStationContainer.get(), id, playerInventoryIn, toolStationTileEntity);

    this.player = playerInventoryIn.player;
    this.craftInventory = new ToolStationInventoryWrapper(toolStationTileEntity);

    // misc
    this.world = playerInventoryIn.player.world;

    // input slots
    int index;

    for (index = 0; index < toolStationTileEntity.getSizeInventory(); index++) {
      this.addSlot(new ToolStationInSlot(toolStationTileEntity, index, 0, 0, this));
    }

    this.outSlot = new ToolStationOutSlot(index, 124, 38, this);

    this.addSlot(this.outSlot);

    this.addInventorySlots();

    this.updateResult();
  }

  public ToolStationContainer(int id, PlayerInventory playerInventory, PacketBuffer buf) {
    this(id, playerInventory, getTileEntityFromBuf(buf, ToolStationTileEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 92;
  }

  @Override
  protected void syncNewContainer(ServerPlayerEntity player) {
    if (this.tile != null) {
      this.activeSlots = this.tile.getSizeInventory();
      TinkerNetwork.getInstance().sendTo(new ToolStationSelectionPacket(ItemStack.EMPTY, tile.getSizeInventory()), player);
    }
  }

  @Override
  protected void syncWithOtherContainer(BaseContainer otherContainer, ServerPlayerEntity player) {
    this.syncWithOtherContainer((ToolStationContainer) otherContainer, player);
  }

  protected void syncWithOtherContainer(ToolStationContainer toolStationContainer, ServerPlayerEntity player) {
    // set same selection as other container
    this.setToolSelection(toolStationContainer.selectedTool, toolStationContainer.activeSlots);
    this.setToolName(toolStationContainer.toolName);

    // also send the data to the player
    TinkerNetwork.getInstance().sendTo(new ToolStationSelectionPacket(toolStationContainer.selectedTool, toolStationContainer.activeSlots), player);

    if (toolStationContainer.toolName != null && !toolStationContainer.toolName.isEmpty()) {
      TinkerNetwork.getInstance().sendTo(new ToolStationTextPacket(toolStationContainer.toolName), player);
    }
  }

  public void setToolSelection(ItemStack tool, int activeSlots) {
    if (activeSlots > tile.getSizeInventory()) {
      activeSlots = tile.getSizeInventory();
    }

    this.activeSlots = activeSlots;
    this.selectedTool = tool;

    for (int i = 0; i < this.tile.getSizeInventory(); i++) {
      Slot slot = this.inventorySlots.get(i);
      // set part info for the slot
      if (slot instanceof ToolStationInSlot) {
        ToolStationInSlot slotToolPart = (ToolStationInSlot) slot;

        slotToolPart.setRestriction(null);

        // deactivate not needed slots
        if (i >= activeSlots) {
          slotToolPart.deactivate();
        }
        // activate the other slots and set toolpart if possible
        else {
          slotToolPart.activate();
          if (tool != ItemStack.EMPTY && tool.getItem() instanceof ToolCore) {
            ToolCore toolCore = (ToolCore) tool.getItem();
            List<PartMaterialRequirement> requiredComponents = toolCore.getToolDefinition().getRequiredComponents();

            if (i < requiredComponents.size()) {
              slotToolPart.setRestriction(requiredComponents.get(i));
            }
          }
        }

        if (this.tile.getWorld().isRemote) {
          slotToolPart.updateIcon();
        }
      }
    }
  }

  public void setToolName(String name) {
    this.toolName = name;

    if (this.tile.getWorld().isRemote) {
      Screen screen = Minecraft.getInstance().currentScreen;

      if (screen instanceof ToolStationScreen) {
        ((ToolStationScreen) screen).textField.setText(name);
      }
    }

    ItemStack tool;

    if (!this.outSlot.getHasStack()) {
      tool = this.inventorySlots.get(ToolStationTileEntity.TOOL_SLOT).getStack();

      if (tool.isEmpty() || !(tool.getItem() instanceof ToolCore) || tool.getDisplayName().getFormattedText().equals(name)) {
        tool = ItemStack.EMPTY;
      }
      else {
        tool = tool.copy();
      }
    }
    else {
      tool = this.outSlot.getStack();
    }

    if (!name.isEmpty()) {
      tool.setDisplayName(new StringTextComponent(name));
    }
    else {
      tool.clearCustomName();
    }

    this.outSlot.inventory.setInventorySlotContents(ToolStationTileEntity.OUTPUT_SLOT, tool);

    this.updateInventory();
  }

  /**
   * Called when the recipe otherwise chances to update the output slot
   */
  protected void updateResult() {
    ItemStack output = ItemStack.EMPTY;

    Optional<IToolStationRecipe> recipe = this.world.getRecipeManager().getRecipe(RecipeTypes.TOOL_STATION, this.craftInventory, this.world);
    if (recipe.isPresent()) {
      output = recipe.get().getCraftingResult(this.craftInventory);
    }

    if (!output.isEmpty()) {
      if (!(StringUtils.isNullOrEmpty(this.toolName)) && !(output.getDisplayName().getFormattedText().equals(this.toolName)) && !(TagUtil.getNoRenameFlag(output))) {
        output.setDisplayName(new StringTextComponent(this.toolName));
      }
    }

    this.outSlot.inventory.setInventorySlotContents(ToolStationTileEntity.OUTPUT_SLOT, output);

    this.updateInventory();
  }

  public void updateInventory() {
    this.updateGUI();

    if (!this.tile.getWorld().isRemote) {
      ServerWorld serverWorld = (ServerWorld) this.tile.getWorld();

      for (PlayerEntity player : serverWorld.getPlayers()) {
        if (player.openContainer != this && player.openContainer instanceof ToolStationContainer && this.sameGui((ToolStationContainer) player.openContainer)) {
          ((ToolStationContainer) player.openContainer).outSlot.inventory.setInventorySlotContents(ToolStationTileEntity.OUTPUT_SLOT, this.outSlot.getStack());
        }
      }
    }
  }

  @Override
  public void onCrafting(PlayerEntity playerEntity, ItemStack output, IInventory craftMatrix) {
    this.setToolName("");

    NonNullList<ItemStack> itemStacks = playerEntity.world.getRecipeManager().getRecipeNonNull(RecipeTypes.TOOL_STATION, this.craftInventory, playerEntity.world);
    for (int i = 0; i < itemStacks.size(); ++i) {
      ItemStack itemstack = this.craftInventory.getStackInSlot(i);
      ItemStack itemstack1 = itemStacks.get(i);
      if (!itemstack.isEmpty()) {
        this.tile.decrStackSize(i, 1);
      }

      if (!itemstack1.isEmpty()) {
        if (itemstack.isEmpty()) {
          this.tile.setInventorySlotContents(i, itemstack1);
        }
        else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
          itemstack1.grow(itemstack.getCount());
          this.tile.setInventorySlotContents(i, itemstack1);
        }
        else if (!this.player.inventory.addItemStackToInventory(itemstack1)) {
          this.player.dropItem(itemstack1, false);
        }
      }
    }

    this.updateResult();

    this.playCraftSound(playerEntity);
  }

  protected void playCraftSound(PlayerEntity player) {
    SoundHelper.playSoundForAll(player, Sounds.SAW.getSound(), 0.8f, 0.8f + 0.4f * TConstruct.random.nextFloat());
  }

  public ItemStack getResult() {
    return this.outSlot.getStack();
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    //todo do we need this?
  }
}

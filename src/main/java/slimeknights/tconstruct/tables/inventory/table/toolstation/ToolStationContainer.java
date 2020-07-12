package slimeknights.tconstruct.tables.inventory.table.toolstation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.server.ServerWorld;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.mantle.util.ItemStackList;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.SoundHelper;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.inventory.ISingleItemInventory;
import slimeknights.tconstruct.library.tinkering.IRepairable;
import slimeknights.tconstruct.library.tinkering.PartMaterialRequirement;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.client.inventory.table.ToolStationScreen;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.network.ToolStationSelectionPacket;
import slimeknights.tconstruct.tables.network.ToolStationTextPacket;
import slimeknights.tconstruct.tables.tileentity.table.ToolStationTileEntity;
import slimeknights.tconstruct.tools.ToolRegistry;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ToolStationContainer extends TinkerStationContainer<ToolStationTileEntity> {

  private final PlayerEntity player;
  protected ToolStationOutSlot outSlot;
  protected ItemStack selectedTool = ItemStack.EMPTY;
  protected int activeSlots;
  public String toolName = "";

  public ToolStationContainer(int id, @Nullable PlayerInventory playerInventory, ToolStationTileEntity toolStationTileEntity) {
    super(TinkerTables.toolStationContainer.get(), id, playerInventory, toolStationTileEntity);

    this.player = playerInventory.player;

    // input slots
    int index;

    for (index = 0; index < toolStationTileEntity.getSizeInventory(); index++) {
      this.addSlot(new ToolStationInSlot(toolStationTileEntity, index, 0, 0, this));
    }

    this.outSlot = new ToolStationOutSlot(index, 124, 38, this);

    this.addSlot(this.outSlot);

    this.addInventorySlots();

    this.onCraftMatrixChanged(playerInventory);
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

    this.onCraftMatrixChanged(this.tile);

    if (this.outSlot.getHasStack()) {
      if (name != null && !name.isEmpty()) {
        this.outSlot.inventory.getStackInSlot(0).setDisplayName(new StringTextComponent(name));
      }
      else {
        this.outSlot.inventory.getStackInSlot(0).clearCustomName();
      }
    }
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    // reset gui state
    this.updateGUI();

    try {
      ItemStack result;
      //1. try repairing
      result = this.repairTool(false);
      // 2. try swapping tool parts
      if (result.isEmpty()) {
        result = this.replaceToolParts(false);
      }
      // 3. try modifying
      if (result.isEmpty()) {
        result = modifyTool(false);
      }
      // 4. try renaming
      if (result.isEmpty()) {
        result = this.renameTool();
      }
      // 5. try building a new tool
      if (result.isEmpty()) {
        result = this.buildTool();
      }

      this.outSlot.inventory.setInventorySlotContents(0, result);
      this.updateGUI();
    }
    catch (TinkerGuiException e) {
      // error ;(
      this.outSlot.inventory.setInventorySlotContents(0, ItemStack.EMPTY);
      this.error(e.getMessage());
    }

    // sync output with other open containers on the server
    if (!this.tile.getWorld().isRemote) {
      ServerWorld serverWorld = (ServerWorld) this.tile.getWorld();
      for (PlayerEntity player : serverWorld.getPlayers()) {
        if (player.openContainer != this && player.openContainer instanceof ToolStationContainer && this.sameGui((ToolStationContainer) player.openContainer)) {
          ((ToolStationContainer) player.openContainer).outSlot.inventory.setInventorySlotContents(0, outSlot.getStack());
        }
      }
    }
  }

  public void onResultTaken(PlayerEntity playerIn, ItemStack stack) {
    boolean resultTaken = false;

    try {
      resultTaken = !this.repairTool(true).isEmpty() || !this.replaceToolParts(true).isEmpty() || !this.renameTool().isEmpty();
    }
    catch (TinkerGuiException e) {
      // no error updating needed
      e.printStackTrace();
    }

    if (resultTaken) {
      this.updateSlotsAfterToolAction();
    }
    else {
      try {
        ItemStack tool = this.buildTool();

        // we built a tool
        if (!tool.isEmpty()) {
          // remove 1 of each in the slots
          // it's guaranteed that each slot that has an item has used exactly 1 item to build the tool
          for (int i = 0; i < this.tile.getSizeInventory(); i++) {
            this.tile.decrStackSize(i, 1);
          }

          this.setToolName("");
        }
      }
      catch (TinkerGuiException e) {
        // no error updating needed
        e.printStackTrace();
      }
    }

    this.onCraftMatrixChanged(null);

    this.playCraftSound(player);
  }

  protected void playCraftSound(PlayerEntity player) {
    SoundHelper.playSoundForAll(player, Sounds.SAW.getSound(), 0.8f, 0.8f + 0.4f * TConstruct.random.nextFloat());
  }

  /**
   * Removes the tool in the input slot and fixes all stacks that have stacksize 0 after being used up.
   */
  private void updateSlotsAfterToolAction() {
    // perfect, items already got removed but we still have to clean up 0-stacks and remove the tool
    this.tile.setInventorySlotContents(0, ItemStack.EMPTY); // slot where the tool was

    for (int i = 1; i < this.tile.getSizeInventory(); i++) {
      if (!this.tile.getStackInSlot(i).isEmpty() && this.tile.getStackInSlot(i).getCount() == 0) {
        this.tile.setInventorySlotContents(i, ItemStack.EMPTY);
      }
    }
  }

  private ItemStack renameTool() throws TinkerGuiException {
    ItemStack tool = this.getToolStack();

    // modifying possible?
    if (tool.isEmpty() || !(tool.getItem() instanceof ToolCore) || StringUtils.isNullOrEmpty(this.toolName) || tool.getDisplayName().getFormattedText().equals(this.toolName)) {
      return ItemStack.EMPTY;
    }

    ItemStack result = tool.copy();
    if (TagUtil.getNoRenameFlag(result)) {
      throw new TinkerGuiException(Util.translate("gui.error.no_rename"));
    }

    result.setDisplayName(new StringTextComponent(this.toolName));

    return result;
  }

  private ItemStack repairTool(boolean removeItems) {
    ItemStack repairable = this.getToolStack();

    // modifying possible?
    if (repairable.isEmpty() || !(repairable.getItem() instanceof IRepairable)) {
      return ItemStack.EMPTY;
    }

    List<IRecipe<ISingleItemInventory>> recipes = this.player.world.getRecipeManager().getRecipes(RecipeTypes.MATERIAL).values().stream().sorted(Comparator.comparing((recipe) -> recipe.getRecipeOutput().getTranslationKey())).collect(Collectors.toList());

    return ToolBuildHandler.tryToRepairTool(recipes, this.getInputs(), repairable, removeItems);
  }

  private ItemStack replaceToolParts(boolean removeItems) throws TinkerGuiException {
    ItemStack toolStack = this.getToolStack();

    if (toolStack.isEmpty() || !(toolStack.getItem() instanceof ToolCore)) {
      return ItemStack.EMPTY;
    }

    NonNullList<ItemStack> inputs = this.getInputs();

    ItemStack result = ToolBuildHandler.tryToReplaceToolParts(toolStack, inputs, removeItems);

    return result;
  }

  private ItemStack modifyTool(boolean remove) throws TinkerGuiException {
    ItemStack modifyable = getToolStack();
    CompoundNBT stats = TagUtil.getToolTag(modifyable);
    int[] slots = new int[4];
    slots[0] = stats.getInt(StatsNBT.TAG_FREE_UPGRADE_SLOTS);
    slots[1] = stats.getInt(StatsNBT.TAG_FREE_ABILITY_SLOTS);
    slots[2] = stats.getInt(StatsNBT.TAG_FREE_ARMOR_SLOTS);
    slots[3] = stats.getInt(StatsNBT.TAG_FREE_TRAIT_SLOTS);
    System.out.println("Modifier slots: "+slots[0]);

    // modifying possible?
    if(modifyable.isEmpty()) {
      return ItemStack.EMPTY;
    }

    ItemStack result = ToolBuildHandler.tryModifyTool(getInputs(), modifyable, remove);
    /*if(!result.isEmpty()) {
      TinkerCraftingEvent.ToolModifyEvent.fireEvent(result, player, modifyable.copy());
    }*/
    return result;
  }

  private ItemStack buildTool() throws TinkerGuiException {
    NonNullList<ItemStack> input = ItemStackList.withSize(this.tile.getSizeInventory());

    for (int i = 0; i < input.size(); i++) {
      input.set(i, this.tile.getStackInSlot(i));
    }

    NonNullList<ItemStack> test = NonNullList.from(ItemStack.EMPTY, input.stream().filter(stack -> !stack.isEmpty()).collect(Collectors.toList()).toArray(new ItemStack[0]));

    ItemStack result = ToolBuildHandler.tryToBuildTool(test, this.toolName, this.getBuildableTools());

    return result;
  }

  protected Set<ToolCore> getBuildableTools() {
    return ToolRegistry.getToolStationCrafting();
  }

  public ItemStack getResult() {
    return this.outSlot.getStack();
  }

  private ItemStack getToolStack() {
    return this.inventorySlots.get(0).getStack();
  }

  private NonNullList<ItemStack> getInputs() {
    NonNullList<ItemStack> input = NonNullList.withSize(this.tile.getSizeInventory() - 1, ItemStack.EMPTY);

    for (int i = 1; i < this.tile.getSizeInventory(); i++) {
      input.set(i - 1, this.tile.getStackInSlot(i));
    }

    return input;
  }
}

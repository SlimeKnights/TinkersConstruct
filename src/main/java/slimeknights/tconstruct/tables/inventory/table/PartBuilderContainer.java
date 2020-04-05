package slimeknights.tconstruct.tables.inventory.table;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import slimeknights.mantle.inventory.CraftingCustomSlot;
import slimeknights.mantle.inventory.IContainerCraftingCustom;
import slimeknights.mantle.inventory.OutSlot;
import slimeknights.tconstruct.containers.TableContainerTypes;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.utils.ListUtil;
import slimeknights.tconstruct.shared.inventory.PersistentCraftingInventory;
import slimeknights.tconstruct.tables.block.TinkerTableBlock;
import slimeknights.tconstruct.tables.client.inventory.table.PartBuilderScreen;
import slimeknights.tconstruct.tables.inventory.TinkerStationContainer;
import slimeknights.tconstruct.tables.inventory.chest.PatternChestContainer;
import slimeknights.tconstruct.tables.inventory.chest.StencilSlot;
import slimeknights.tconstruct.tables.tileentity.chest.PatternChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import javax.annotation.Nullable;

public class PartBuilderContainer extends TinkerStationContainer<PartBuilderTileEntity> implements IContainerCraftingCustom {

  public IInventory craftResult;

  private final Slot patternSlot;
  private final Slot secondarySlot;
  private final Slot input1;
  private final Slot input2;

  private final boolean partCrafter;
  private final PlayerEntity player;
  public final IInventory patternChest;

  public PartBuilderContainer(int id, @Nullable PlayerInventory inv, PartBuilderTileEntity partBuilderTileEntity) {
    super(TableContainerTypes.part_builder, id, inv, partBuilderTileEntity);

    PersistentCraftingInventory craftMatrix = new PersistentCraftingInventory(this, tile, 1, 3);
    this.craftResult = new CraftResultInventory();
    this.player = inv.player;

    this.addSlot(new CraftingCustomSlot(this, inv.player, craftMatrix, this.craftResult, 0, 106, 35));
    this.addSlot(this.secondarySlot = new OutSlot(tile, 3, 132, 35));


    // pattern slot
    this.addSlot(this.patternSlot = new StencilSlot(craftMatrix, 2, 26, 35, false));

    // material slots
    this.addSlot(this.input1 = new Slot(craftMatrix, 0, 48, 26));
    this.addSlot(this.input2 = new Slot(craftMatrix, 1, 48, 44));

    PatternChestTileEntity chest = this.detectTE(PatternChestTileEntity.class);
    // TE present?
    if (chest != null) {
      // crafting station and stencil table also present?
      boolean hasCraftingStation = false;
      boolean hasStencilTable = false;

      for (Pair<BlockPos, BlockState> pair : this.tinkerStationBlocks) {
        if (!(pair.getRight().getBlock() instanceof TinkerTableBlock)) {
          continue;
        }

        TinkerTableBlock tableBlock = (TinkerTableBlock) pair.getRight().getBlock();

        TinkerTableBlock.TableTypes type = tableBlock.getType();

        if (type != TinkerTableBlock.TableTypes.NoTableTypeAssigned) {
          if (type == TinkerTableBlock.TableTypes.CraftingStation) {
            hasCraftingStation = true;
          } else if (type == TinkerTableBlock.TableTypes.StencilTable) {
            hasStencilTable = true;
          }
        }
      }

      // are we a PartCrafter?
      this.partCrafter = hasStencilTable && hasCraftingStation;

      Container sideInventory = new PatternChestContainer.DynamicChestInventory(TableContainerTypes.part_builder, id, inv, chest, -6, 8, 6);
      this.addSubContainer(sideInventory, true);

      this.patternChest = chest;
    } else {
      this.partCrafter = false;
      this.patternChest = null;
    }

    this.addInventorySlots();

    this.onCraftMatrixChanged(inv);
  }

  public PartBuilderContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, PartBuilderTileEntity.class));
  }

  public boolean isPartCrafter() {
    return partCrafter;
  }

  @Override
  public void onCraftMatrixChanged(IInventory inventoryIn) {
    this.updateResult();
  }

  // Sets the result in the output slot depending on the input!
  public void updateResult() {
    // no pattern -> no output
    if(!this.patternSlot.getHasStack() || (!this.input1.getHasStack() && !this.input2.getHasStack() && !this.secondarySlot.getHasStack())) {
      this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
      this.updateGUI();
    }
    else {
      Throwable throwable = null;
      NonNullList<ItemStack> toolPart;

      try {
        toolPart = ToolBuilder.tryBuildToolPart(this.patternSlot.getStack(), ListUtil.getListFrom(this.input1.getStack(), this.input2.getStack()), false);
        if(toolPart != null && !toolPart.get(0).isEmpty()) {
          TinkerCraftingEvent.ToolPartCraftingEvent.fireEvent(toolPart.get(0), player);
        }
      } catch(TinkerGuiException e) {
        toolPart = null;
        throwable = e;
      }

      ItemStack secondary = this.secondarySlot.getStack();

      // got output?
      if(toolPart != null &&
        // got no secondary output or does it stack with the current one?
        (secondary.isEmpty() || toolPart.get(1).isEmpty() || ItemStack.areItemsEqual(secondary, toolPart.get(1)) && ItemStack.areItemStackTagsEqual(secondary, toolPart.get(1)))) {
        this.craftResult.setInventorySlotContents(0, toolPart.get(0));
      }
      else {
        this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
      }

      if(throwable != null) {
        error(throwable.getMessage());
      }
      else {
        updateGUI();
      }
    }
  }

  public void setPattern(ItemStack wanted) {
    if(this.patternChest == null) {
      return;
    }

    // check chest contents for wanted
    for(int i = 0; i < this.patternChest.getSizeInventory(); i++) {
      if(ItemStack.areItemStacksEqual(wanted, this.patternChest.getStackInSlot(i))) {
        // found it! exchange it with the pattern slot!
        ItemStack slotStack = this.patternSlot.getStack();
        this.patternSlot.putStack(this.patternChest.getStackInSlot(i));
        this.patternChest.setInventorySlotContents(i, slotStack);
        break;
      }
    }
  }

  @Override
  public void onCrafting(PlayerEntity playerEntity, ItemStack output, IInventory craftMatrix) {
    NonNullList<ItemStack> toolPart = NonNullList.create();

    try {
      toolPart = ToolBuilder.tryBuildToolPart(this.patternSlot.getStack(), ListUtil.getListFrom(this.input1.getStack(), this.input2.getStack()), true);
    } catch (TinkerGuiException e) {
      // don't need any user information at this stage
    }

    if (toolPart == null) {
      // undefined :I
      return;
    }

    ItemStack secondOutput = toolPart.get(1);
    ItemStack secondary = this.secondarySlot.getStack();

    if (secondary.isEmpty()) {
      this.putStackInSlot(this.secondarySlot.slotNumber, secondOutput);
    } else if (!secondOutput.isEmpty() && ItemStack.areItemsEqual(secondary, secondOutput) && ItemStack.areItemStackTagsEqual(secondary, secondOutput)) {
      secondary.grow(secondOutput.getCount());
    }

    this.updateResult();
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
    return slotIn.inventory != this.craftResult && super.canMergeSlot(stack, slotIn);
  }

  public String getInventoryDisplayName() {
    if (partCrafter) {
      return Util.translate("gui.partcrafter.name");
    }

    return super.getInventoryDisplayName();
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public void putStackInSlot(int slotID, ItemStack stack) {
    super.putStackInSlot(slotID, stack);

    Minecraft mc = Minecraft.getInstance();

    if (mc.currentScreen instanceof PartBuilderScreen) {
      ((PartBuilderScreen) mc.currentScreen).updateButtons();
    }
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
    ItemStack itemStack = super.slotClick(slotId, dragType, clickTypeIn, player);
    Minecraft mc = Minecraft.getInstance();

    if (mc.currentScreen instanceof PartBuilderScreen) {
      ((PartBuilderScreen) mc.currentScreen).updateButtons();
    }

    return itemStack;
  }
}

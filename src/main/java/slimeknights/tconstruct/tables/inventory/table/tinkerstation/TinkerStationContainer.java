package slimeknights.tconstruct.tables.inventory.table.tinkerstation;

import lombok.Getter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.library.tools.layout.LayoutSlot;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayout;
import slimeknights.tconstruct.library.tools.layout.StationSlotLayoutLoader;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.inventory.BaseStationContainer;
import slimeknights.tconstruct.tables.inventory.table.LazyResultSlot;
import slimeknights.tconstruct.tables.tileentity.table.TinkerStationTileEntity;
import slimeknights.tconstruct.tools.item.ArmorSlotType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TinkerStationContainer extends BaseStationContainer<TinkerStationTileEntity> {
  private static final ResourceLocation[] ARMOR_SLOT_BACKGROUNDS = new ResourceLocation[] {
    PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS,
    PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS,
    PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE,
    PlayerContainer.EMPTY_ARMOR_SLOT_HELMET
  };

  @Getter
  private final List<Slot> inputSlots;
  private final LazyResultSlot resultSlot;

  /**
   * Standard constructor
   * @param id    Window ID
   * @param inv   Player inventory
   * @param tile  Relevant tile entity
   */
  public TinkerStationContainer(int id, PlayerInventory inv, @Nullable TinkerStationTileEntity tile) {
    super(TinkerTables.tinkerStationContainer.get(), id, inv, tile);

    // unfortunately, nothing works with no tile
    if (tile != null) {
      // send the player the current recipe, as we only sync to open containers
      tile.syncRecipe(inv.player);

      inputSlots = new ArrayList<>();
      inputSlots.add(this.addSlot(new TinkerStationSlot(tile, TinkerStationTileEntity.TINKER_SLOT, 0, 0)));

      int index;
      for (index = 0; index < tile.getSizeInventory() - 1; index++) {
        inputSlots.add(this.addSlot(new TinkerStationSlot(tile, index + TinkerStationTileEntity.INPUT_SLOT, 0, 0)));
      }

      // add result slot, will fetch result cache
      this.addSlot(this.resultSlot = new LazyResultSlot(tile.getCraftingResult(), 114, 38));
      // set initial slot filters and activations
      setToolSelection(StationSlotLayoutLoader.getInstance().get(Objects.requireNonNull(tile.getBlockState().getBlock().getRegistryName())));
    }
    else {
      // requirement for final variable
      this.resultSlot = null;
      this.inputSlots = Collections.emptyList();
    }

    // add armor and offhand slots, for convenience
    for (ArmorSlotType slotType : ArmorSlotType.values()) {
      int index = slotType.getIndex();
      this.addSlot(new ArmorSlot(inv, slotType.getEquipmentSlot(), 152, 16 + (3 - index) * 18));
    }
    this.addSlot(new Slot(inv, 40, 132, 70).setBackground(PlayerContainer.LOCATION_BLOCKS_TEXTURE, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD));

    this.addInventorySlots();
  }

  /**
   * Factory constructor
   * @param id   Window ID
   * @param inv  Player inventory
   * @param buf  Buffer for fetching tile
   */
  public TinkerStationContainer(int id, PlayerInventory inv, PacketBuffer buf) {
    this(id, inv, getTileEntityFromBuf(buf, TinkerStationTileEntity.class));
  }

  @Override
  protected int getInventoryYOffset() {
    return 92;
  }

  @Override
  public boolean canMergeSlot(ItemStack stack, Slot slot) {
    return slot != this.resultSlot && super.canMergeSlot(stack, slot);
  }

  /**
   * Updates the active slots from the screen
   * @param layout     New layout
   */
  public void setToolSelection(StationSlotLayout layout) {
    assert this.tile != null;
    int maxSize = tile.getSizeInventory();
    for (int i = 0; i < maxSize; i++) {
      Slot slot = this.inventorySlots.get(i);
      if (slot instanceof TinkerStationSlot) {
        // activate or deactivate the slots, sets the filters
        TinkerStationSlot slotToolPart = (TinkerStationSlot) slot;
        LayoutSlot layoutSlot = layout.getSlot(i);
        if (layoutSlot.isHidden()) {
          slotToolPart.deactivate();
        }
        else {
          slotToolPart.activate(layoutSlot);
        }
      }
    }
  }

  public ItemStack getResult() {
    return this.resultSlot.getStack();
  }

  private static class ArmorSlot extends Slot {
    private final PlayerEntity player;
    private final EquipmentSlotType slotType;
    public ArmorSlot(PlayerInventory inv, EquipmentSlotType slotType, int xPosition, int yPosition) {
      super(inv, 36 + slotType.getIndex(), xPosition, yPosition);
      this.player = inv.player;
      this.slotType = slotType;
      setBackground(PlayerContainer.LOCATION_BLOCKS_TEXTURE, ARMOR_SLOT_BACKGROUNDS[slotType.getIndex()]);
    }

    @Override
    public int getSlotStackLimit() {
      return 1;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
      return stack.canEquip(slotType, player);
    }
  }
}

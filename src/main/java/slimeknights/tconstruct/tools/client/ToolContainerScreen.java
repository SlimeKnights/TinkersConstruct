package slimeknights.tconstruct.tools.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

import java.util.List;
import java.util.function.Function;

import static slimeknights.tconstruct.tools.menu.ToolContainerMenu.REPEAT_BACKGROUND_START;
import static slimeknights.tconstruct.tools.menu.ToolContainerMenu.SLOT_SIZE;

/** Screen for a tool inventory */
public class ToolContainerScreen extends AbstractContainerScreen<ToolContainerMenu> {
  /** The ResourceLocation containing the chest GUI texture. */
  private static final ResourceLocation TEXTURE = TConstruct.getResource("textures/gui/tool.png");

  /** Max number of rows in the repeat slots background */
  private static final int REPEAT_BACKGROUND_ROWS = 6;
  /** Start location of the player inventory */
  private static final int PLAYER_INVENTORY_START = REPEAT_BACKGROUND_START + (REPEAT_BACKGROUND_ROWS * SLOT_SIZE);
  /** Height of the player inventory texture */
  private static final int PLAYER_INVENTORY_HEIGHT = 97;
  /** Start Y location of the slot start element */
  private static final int SLOTS_START = 256 - SLOT_SIZE;
  /** Selected slot texture X position */
  private static final int SELECTED_X = 176;

  /** Total number of slots in the inventory */
  private final int slots;
  /** Number of rows in this inventory */
  private final int inventoryRows;
  /** Number of slots in the final row */
  private final int slotsInLastRow;
  /** Tool instance being rendered */
  private final IToolStackView tool;
  public ToolContainerScreen(ToolContainerMenu menu, Inventory inv, Component title) {
    super(menu, inv, title);
    int slots = menu.getItemHandler().getSlots();
    if (menu.isShowOffhand()) {
      slots++;
    }
    int inventoryRows = slots / 9;
    int slotsInLastRow = slots % 9;
    if (slotsInLastRow == 0) {
      slotsInLastRow = 9;
    } else {
      inventoryRows++;
    }
    this.slots = slots;
    this.inventoryRows = inventoryRows;
    this.slotsInLastRow = slotsInLastRow;
    this.imageHeight = 114 + this.inventoryRows * 18;
    this.inventoryLabelY = this.imageHeight - 94;
    this.tool = ToolStack.from(menu.getStack());
  }

  @Override
  protected void slotClicked(Slot slot, int slotId, int index, ClickType type) {
    // disallow swapping the tool slot
    if (type == ClickType.SWAP) {
      EquipmentSlot toolSlot = menu.getSlotType();
      if (toolSlot == EquipmentSlot.MAINHAND && index == menu.getSelectedHotbarSlot() || toolSlot == EquipmentSlot.OFFHAND && index == Inventory.SLOT_OFFHAND) {
        return;
      }
    }
    super.slotClicked(slot, slotId, index, type);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(graphics);
    super.render(graphics, mouseX, mouseY, partialTicks);
    this.renderTooltip(graphics, mouseX, mouseY);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTicks, int x, int y) {
    int xStart = (this.width - this.imageWidth) / 2;
    int yStart = (this.height - this.imageHeight) / 2;

    int yOffset; // after ifs, will be the height of the final element
    if (inventoryRows <= REPEAT_BACKGROUND_ROWS) {
      yOffset = inventoryRows * SLOT_SIZE + REPEAT_BACKGROUND_START;
      graphics.blit(TEXTURE, xStart, yStart, 0, 0, this.imageWidth, yOffset);
    } else {
      // draw top area with first 6 rows
      yOffset = REPEAT_BACKGROUND_ROWS * SLOT_SIZE + REPEAT_BACKGROUND_START;
      graphics.blit(TEXTURE, xStart, yStart, 0, 0, this.imageWidth, yOffset);

      // draw each next group of 6
      int remaining = inventoryRows - REPEAT_BACKGROUND_ROWS;
      int height = REPEAT_BACKGROUND_ROWS * SLOT_SIZE;
      for (; remaining > REPEAT_BACKGROUND_ROWS; remaining -= REPEAT_BACKGROUND_ROWS) {
        graphics.blit(TEXTURE, xStart, yStart + yOffset, 0, REPEAT_BACKGROUND_START, this.imageWidth, height);
        yOffset += height;
      }

      // draw final set of up to 6
      height = remaining * SLOT_SIZE;
      graphics.blit(TEXTURE, xStart, yStart + yOffset, 0, REPEAT_BACKGROUND_START, this.imageWidth, height);
      yOffset += height;
    }
    // draw the player inventory background
    graphics.blit(TEXTURE, xStart, yStart + yOffset, 0, PLAYER_INVENTORY_START, this.imageWidth, PLAYER_INVENTORY_HEIGHT);

    // draw slot background
    int rowLeft = xStart + 7;
    int rowStart = yStart + REPEAT_BACKGROUND_START - SLOT_SIZE;
    for (int i = 1; i < inventoryRows; i++) {
      graphics.blit(TEXTURE, rowLeft, rowStart + i * SLOT_SIZE, 0, SLOTS_START, 9 * SLOT_SIZE, SLOT_SIZE);
    }
    // last row may not have all slots
    graphics.blit(TEXTURE, rowLeft, rowStart + inventoryRows * SLOT_SIZE, 0, SLOTS_START, slotsInLastRow * SLOT_SIZE, SLOT_SIZE);

    // draw a background on the selected slot index
    int selectedSlot = menu.getSelectedHotbarSlot();
    if (selectedSlot != -1) {
      int slotIndex = slots - 1;
      if (selectedSlot != 10) {
        slotIndex += 28 + selectedSlot;
      }
      if (slotIndex < menu.slots.size()) {
        Slot slot = menu.getSlot(slotIndex);
        graphics.blit(TEXTURE, xStart + slot.x - 2, yStart + slot.y - 2, SELECTED_X, 0, SLOT_SIZE + 2, SLOT_SIZE + 2);
      }
    }


    // prepare pattern drawing
    assert this.minecraft != null;
    Function<ResourceLocation,TextureAtlasSprite> spriteGetter = this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);

    // draw slot patterns for all empty slots
    int start = 0;
    int maxSlots = menu.slots.size();

    List<ModifierEntry> modifiers = tool.getModifierList();
    modifiers:
    for (int modIndex = modifiers.size() - 1; modIndex >= 0; modIndex--) {
      ModifierEntry entry = modifiers.get(modIndex);
      InventoryModifierHook inventory = entry.getHook(ToolInventoryCapability.HOOK);
      int size = inventory.getSlots(tool, entry);
      for (int i = 0; i < size; i++) {
        if (start + i >= maxSlots) {
          break modifiers;
        }
        Slot slot = menu.getSlot(start + i);
        Pattern pattern = inventory.getPattern(tool, entry, i, slot.hasItem());
        if (pattern != null) {
          TextureAtlasSprite sprite = spriteGetter.apply(pattern.getTexture());
          graphics.blit(xStart + slot.x, yStart + slot.y, 100, 16, 16, sprite);
        }
      }
      start += size;
    }

    // offhand icon
    if (menu.isShowOffhand()) {
      Slot slot = menu.getSlot(slots - 1);
      if (!slot.hasItem()) {
        TextureAtlasSprite sprite = spriteGetter.apply(Patterns.SHIELD.getTexture());
        graphics.blit(xStart + slot.x, yStart + slot.y, 100, 16, 16, sprite);
      }
    }
  }
}

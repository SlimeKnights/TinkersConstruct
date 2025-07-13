package slimeknights.tconstruct.tools.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.fluid.SimpleFluidTank;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import slimeknights.tconstruct.library.tools.layout.Patterns;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.smeltery.client.screen.IScreenWithFluidTank;
import slimeknights.tconstruct.smeltery.client.screen.module.GuiTankModule;
import slimeknights.tconstruct.tools.menu.ToolContainerMenu;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

import static slimeknights.tconstruct.tools.menu.ToolContainerMenu.REPEAT_BACKGROUND_START;
import static slimeknights.tconstruct.tools.menu.ToolContainerMenu.SLOT_SIZE;
import static slimeknights.tconstruct.tools.menu.ToolContainerMenu.TITLE_SIZE;
import static slimeknights.tconstruct.tools.menu.ToolContainerMenu.UI_START;

/** Screen for a tool inventory */
public class ToolContainerScreen extends AbstractContainerScreen<ToolContainerMenu> implements IScreenWithFluidTank {
  /** The ResourceLocation containing the chest GUI texture. */
  private static final ResourceLocation TEXTURE = TConstruct.getResource("textures/gui/tool_inventory.png");

  /** Slot background for 3x3 crafting grid */
  private static final ElementScreen CRAFTING_SLOTS = new ElementScreen(TEXTURE, 176, 74, 54, 54, 256, 256);
  /** Result slot for 3x3 crafting grid */
  private static final ElementScreen CRAFTING_RESULT = CRAFTING_SLOTS.move(176, 20, 62, 54);
  /** Full 2x2 crafting grid */
  private static final ElementScreen INVENTORY_CRAFTING = CRAFTING_SLOTS.move(176, 128, 74, 36);
  /** Fluid bar to draw at the bottom of the UI */
  private static final ElementScreen FLUID_TANK = CRAFTING_SLOTS.move(0, 224, 176, 14);

  /** Max number of rows in the repeat slots background */
  private static final int REPEAT_BACKGROUND_SIZE = 6 * SLOT_SIZE;
  /** Start location of the player inventory */
  private static final int PLAYER_INVENTORY_START = REPEAT_BACKGROUND_START + REPEAT_BACKGROUND_SIZE;
  /** Height of the player inventory texture */
  private static final int PLAYER_INVENTORY_HEIGHT = 96;
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
  /** Tool tank rendering logic */
  @Nullable
  private final GuiTankModule tank;
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
    int craftingHeight = menu.getCraftingHeight() * SLOT_SIZE;
    this.imageHeight = UI_START + TITLE_SIZE + PLAYER_INVENTORY_HEIGHT + this.inventoryRows * SLOT_SIZE + craftingHeight;
    SimpleFluidTank tank = menu.getTank();
    if (tank.getCapacity() > 0) {
      this.imageHeight += FLUID_TANK.h;
      this.tank = new GuiTankModule(this, tank, 8, this.imageHeight - PLAYER_INVENTORY_HEIGHT - 9, 160, 8, true, null);
    } else {
      this.tank = null;
    }
    if (slots > 0) {
      this.titleLabelY += craftingHeight;
    }
    this.inventoryLabelY = this.imageHeight - 93;
  }

  @Override
  protected void slotClicked(Slot slot, int slotId, int index, ClickType type) {
    // disallow swapping the tool slot
    if (type == ClickType.SWAP && slot.container == menu.getPlayer().getInventory() && slot.getSlotIndex() == menu.getSlotIndex()) {
      return;
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

    // draw the background, repeated if we have too much content for the default size, or shrunk otherwise
    int craftingHeight = menu.getCraftingHeight();
    int slotBackground = REPEAT_BACKGROUND_START + (inventoryRows + craftingHeight) * SLOT_SIZE;
    if (slotBackground < PLAYER_INVENTORY_START) {
      // small background? draw a single segment up to the size
      graphics.blit(TEXTURE, xStart, yStart, 0, 0, this.imageWidth, slotBackground);
    } else {
      // large background? repeat as needed
      // start with the top bar + roughly 6 slots
      graphics.blit(TEXTURE, xStart, yStart, 0, 0, this.imageWidth, PLAYER_INVENTORY_START);
      int yOffset = PLAYER_INVENTORY_START;
      int remainingBackground = slotBackground - yOffset;
      // add chunks of about 6 until we run out
      for (; remainingBackground > REPEAT_BACKGROUND_SIZE; remainingBackground -= REPEAT_BACKGROUND_SIZE) {
        graphics.blit(TEXTURE, xStart, yStart + yOffset, 0, REPEAT_BACKGROUND_START, this.imageWidth, REPEAT_BACKGROUND_SIZE);
        yOffset += REPEAT_BACKGROUND_SIZE;
      }
      // draw last partial chunk
      graphics.blit(TEXTURE, xStart, yStart + yOffset, 0, REPEAT_BACKGROUND_START, this.imageWidth, remainingBackground);
    }
    // draw tank if we have capacity
    if (tank != null) {
      FLUID_TANK.draw(graphics, xStart, yStart + slotBackground);
      slotBackground += FLUID_TANK.h;
    }
    // draw the player inventory background
    graphics.blit(TEXTURE, xStart, yStart + slotBackground, 0, PLAYER_INVENTORY_START, this.imageWidth, PLAYER_INVENTORY_HEIGHT);

    // add crafting table slots
    // if we have no slots, push them below the title, otherwise above the title
    int craftingOffset = yStart + (slots == 0 ? REPEAT_BACKGROUND_START : UI_START);
    if (craftingHeight == 3) {
      CRAFTING_SLOTS.draw(graphics, xStart + 29, craftingOffset);
      CRAFTING_RESULT.draw(graphics, xStart + 83, craftingOffset);
    } else if (craftingHeight == 2) {
      INVENTORY_CRAFTING.draw(graphics, xStart + 51, craftingOffset);
    }

    // draw slot background
    if (slots > 0) {
      int rowLeft = xStart + 7;
      int rowStart = yStart + REPEAT_BACKGROUND_START - SLOT_SIZE + (craftingHeight * SLOT_SIZE);
      for (int i = 1; i < inventoryRows; i++) {
        graphics.blit(TEXTURE, rowLeft, rowStart + i * SLOT_SIZE, 0, SLOTS_START, 9 * SLOT_SIZE, SLOT_SIZE);
      }
      // last row may not have all slots
      graphics.blit(TEXTURE, rowLeft, rowStart + inventoryRows * SLOT_SIZE, 0, SLOTS_START, slotsInLastRow * SLOT_SIZE, SLOT_SIZE);
    }

    // draw a background on the selected slot index
    int slotIndex = menu.getSlotIndex();
    int playerStart = menu.getPlayerInventoryStart();
    int highlightIndex = -1;
    if (slotIndex < 9) {
      // hotbar slots are after all our slots, and after the main inventory 27
      highlightIndex = playerStart + slotIndex + 27;
    } else if (slotIndex < Inventory.INVENTORY_SIZE) {
      // main inventory 27 is after our slots, but the index is 9 too high (hotbar)
      highlightIndex = playerStart + slotIndex - 9;
    } else if (slotIndex == Inventory.SLOT_OFFHAND && menu.isShowOffhand()) {
      // offhand is the last slot, but only if the offhand is shown in the inveotry
      highlightIndex = playerStart - 1;
    }
    // armor is not shown, so that will be -1
    if (highlightIndex != -1 && highlightIndex < menu.slots.size()) {
      Slot slot = menu.getSlot(highlightIndex);
      graphics.blit(TEXTURE, xStart + slot.x - 2, yStart + slot.y - 2, SELECTED_X, 0, SLOT_SIZE + 2, SLOT_SIZE + 2);
    }

    // prepare pattern drawing
    assert this.minecraft != null;
    Function<ResourceLocation,TextureAtlasSprite> spriteGetter = this.minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);

    // draw slot patterns for all empty slots
    int start = menu.getToolInventoryStart();
    int maxSlots = menu.slots.size();

    IToolStackView tool = menu.getTool();
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
      Slot slot = menu.getSlot(menu.getPlayerInventoryStart() - 1);
      if (!slot.hasItem()) {
        TextureAtlasSprite sprite = spriteGetter.apply(Patterns.SHIELD.getTexture());
        graphics.blit(xStart + slot.x, yStart + slot.y, 100, 16, 16, sprite);
      }
    }

    if (tank != null) {
      tank.draw(graphics);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderLabels(graphics, mouseX, mouseY);
    if (tank != null) {
      tank.highlightHoveredFluid(graphics, mouseX - this.leftPos, mouseY - this.topPos);
    }
  }

  @Override
  protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
    super.renderTooltip(graphics, mouseX, mouseY);

    if (tank != null) {
      tank.renderTooltip(graphics, mouseX, mouseY);
    }
  }

  @Override
  public boolean mouseClicked(double mouseX, double mouseY, int button) {
    assert minecraft != null && minecraft.player != null && minecraft.gameMode != null;
    if (tank != null && (button == 0 || button == 1) && !menu.getCarried().isEmpty() && !minecraft.player.isSpectator()) {
      if (tank.tryClick((int)mouseX - leftPos, (int)mouseY - topPos, button, 0)) {
        return true;
      }
    }
    return super.mouseClicked(mouseX, mouseY, button);
  }

  @Nullable
  @Override
  public FluidLocation getFluidUnderMouse(int mouseX, int mouseY) {
    if (tank != null) {
      return tank.getFluidUnderMouse(mouseX - leftPos, mouseY - topPos);
    }
    return null;
  }
}

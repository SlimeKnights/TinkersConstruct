package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability.IInventoryModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.inventory.ToolContainer;

import java.util.function.Function;

import static slimeknights.tconstruct.tools.inventory.ToolContainer.REPEAT_BACKGROUND_START;
import static slimeknights.tconstruct.tools.inventory.ToolContainer.SLOT_SIZE;

/** Screen for a tool inventory */
public class ToolContainerScreen extends ContainerScreen<ToolContainer> {
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

  /** Number of rows in this inventory */
  private final int inventoryRows;
  /** Number of slots in the final row */
  private final int slotsInLastRow;
  /** Tool instance being rendered */
  private final IModifierToolStack tool;
  public ToolContainerScreen(ToolContainer container, PlayerInventory inv, ITextComponent title) {
    super(container, inv, title);
    int slots = container.getItemHandler().getSlots();
    int inventoryRows = slots / 9;
    int slotsInLastRow = slots % 9;
    if (slotsInLastRow == 0) {
      slotsInLastRow = 9;
    } else {
      inventoryRows++;
    }
    this.inventoryRows = inventoryRows;
    this.slotsInLastRow = slotsInLastRow;
    this.ySize = 114 + this.inventoryRows * 18;
    this.playerInventoryTitleY = this.ySize - 94;
    this.tool = ToolStack.from(container.getStack());
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
    this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
    RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    assert this.minecraft != null;
    this.minecraft.getTextureManager().bindTexture(TEXTURE);
    int xStart = (this.width - this.xSize) / 2;
    int yStart = (this.height - this.ySize) / 2;

    int yOffset; // after ifs, will be the height of the final element
    if (inventoryRows <= REPEAT_BACKGROUND_ROWS) {
      yOffset = inventoryRows * SLOT_SIZE + REPEAT_BACKGROUND_START;
      this.blit(matrixStack, xStart, yStart, 0, 0, this.xSize, yOffset);
    } else {
      // draw top area with first 6 rows
      yOffset = REPEAT_BACKGROUND_ROWS * SLOT_SIZE + REPEAT_BACKGROUND_START;
      this.blit(matrixStack, xStart, yStart, 0, 0, this.xSize, yOffset);

      // draw each next group of 6
      int remaining = inventoryRows - REPEAT_BACKGROUND_ROWS;
      int height = REPEAT_BACKGROUND_ROWS * SLOT_SIZE;
      for (; remaining > REPEAT_BACKGROUND_ROWS; remaining -= REPEAT_BACKGROUND_ROWS) {
        this.blit(matrixStack, xStart, yStart + yOffset, 0, REPEAT_BACKGROUND_START, this.xSize, height);
        yOffset += height;
      }

      // draw final set of up to 6
      height = remaining * SLOT_SIZE;
      this.blit(matrixStack, xStart, yStart + yOffset, 0, REPEAT_BACKGROUND_START, this.xSize, height);
      yOffset += height;
    }
    // draw the player inventory background
    this.blit(matrixStack, xStart, yStart + yOffset, 0, PLAYER_INVENTORY_START, this.xSize, PLAYER_INVENTORY_HEIGHT);

    // draw slot background
    int rowLeft = xStart + 7;
    int rowStart = yStart + REPEAT_BACKGROUND_START - SLOT_SIZE;
    for (int i = 1; i < inventoryRows; i++) {
      this.blit(matrixStack, rowLeft, rowStart + i * SLOT_SIZE, 0, SLOTS_START, 9 * SLOT_SIZE, SLOT_SIZE);
    }
    // last row may not have all slots
    this.blit(matrixStack, rowLeft, rowStart + inventoryRows * SLOT_SIZE, 0, SLOTS_START, slotsInLastRow * SLOT_SIZE, SLOT_SIZE);

    // draw a red background on the selected slot index
    int selectedSlot = container.getSelectedHotbarSlot();
    if (selectedSlot != -1) {
      int slotIndex = container.getItemHandler().getSlots() + 27 + selectedSlot;
      if (slotIndex < container.inventorySlots.size()) {
        Slot slot = container.getSlot(slotIndex);
        this.blit(matrixStack, xStart + slot.xPos - 2, yStart + slot.yPos - 2, SELECTED_X, 0, SLOT_SIZE + 2, SLOT_SIZE + 2);
      }
    }


    // prepare pattern drawing
    this.minecraft.getTextureManager().bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
    Function<ResourceLocation,TextureAtlasSprite> spriteGetter = this.minecraft.getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE);

    // draw slot patterns for all empty slots
    int start = 0;
    int maxSlots = container.inventorySlots.size();
    modifiers:
    for (ModifierEntry entry : tool.getModifierList()) {
      IInventoryModifier inventory = entry.getModifier().getModule(IInventoryModifier.class);
      if (inventory != null) {
        int level = entry.getLevel();
        int size = inventory.getSlots(tool, level);
        for (int i = 0; i < size; i++) {
          if (start + i >= maxSlots) {
            break modifiers;
          }
          Slot slot = container.getSlot(start + i);
          Pattern pattern = inventory.getPattern(tool, level, i, slot.getHasStack());
          if (pattern != null) {
            TextureAtlasSprite sprite = spriteGetter.apply(pattern.getTexture());
            blit(matrixStack, xStart + slot.xPos, yStart + slot.yPos, 100, 16, 16, sprite);
          }
        }
        start += size;
      }
    }
  }
}

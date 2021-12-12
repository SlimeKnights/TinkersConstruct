package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.events.ToolEquipmentChangeEvent;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IModDataReadOnly;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Orientation2D;
import slimeknights.tconstruct.library.utils.Orientation2D.Orientation1D;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ShieldStrapModifier;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ZoomModifier;
import slimeknights.tconstruct.tools.modifiers.upgrades.armor.ItemFrameModifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/** Modifier event hooks that run client side */
@EventBusSubscriber(modid = TConstruct.MOD_ID, value = Dist.CLIENT, bus = Bus.FORGE)
public class ModifierClientEvents {
  @SubscribeEvent
  static void onTooltipEvent(ItemTooltipEvent event) {
    // suppress durability from advanced, we display our own
    if (event.getItemStack().getItem() instanceof IModifiableDisplay) {
      event.getToolTip().removeIf(text -> {
        if (text instanceof TranslationTextComponent) {
          return ((TranslationTextComponent)text).getKey().equals("item.durability");
        }
        return false;
      });
    }
  }

  /** Determines whether to render the given hand based on modifiers */
  @SubscribeEvent
  static void renderHand(RenderHandEvent event) {
    Hand hand = event.getHand();
    PlayerEntity player = Minecraft.getInstance().player;
    if (hand != Hand.OFF_HAND || player == null) {
      return;
    }
    ItemStack mainhand = player.getHeldItemMainhand();
    ItemStack offhand = event.getItemStack();
    if (mainhand.getItem().isIn(TinkerTags.Items.TWO_HANDED)) {
      ToolStack tool = ToolStack.from(mainhand);
      // special support for replacing modifier
      IModDataReadOnly volatileData = tool.getVolatileData();
      boolean noInteraction = volatileData.getBoolean(IModifiable.NO_INTERACTION);
      if (!noInteraction && !volatileData.getBoolean(IModifiable.DEFER_OFFHAND)) {
        if (!(offhand.getItem() instanceof BlockItem) || tool.getModifierLevel(TinkerModifiers.exchanging.get()) == 0) {
          event.setCanceled(true);
          return;
        }
      }
      // don't render empty offhand if main stack does not have upgraded offhanded
      if (!noInteraction && offhand.isEmpty()) {
        return;
      }
    }

    // if the data is set, render the empty offhand
    if (offhand.isEmpty() && !player.isInvisible() && ModifierUtil.getTotalModifierLevel(player, TinkerDataKeys.SHOW_EMPTY_OFFHAND) > 0) {
      MatrixStack matrices = event.getMatrixStack();
      matrices.push();
      Minecraft.getInstance().getFirstPersonRenderer().renderArmFirstPerson(matrices, event.getBuffers(), event.getLight(), event.getEquipProgress(), event.getSwingProgress(), player.getPrimaryHand().opposite());
      matrices.pop();
      event.setCanceled(true);
    }
  }

  /** Handles the zoom modifier zooming */
  @SubscribeEvent
  static void handleZoom(FOVUpdateEvent event) {
    event.getEntity().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      Float zoom = data.get(ZoomModifier.ZOOM_MULTIPLIER);
      if (zoom != null) {
        event.setNewfov(event.getNewfov() * zoom);
      }
    });
  }


  /* Renders the next shield strap item above the offhand item */

  /** Cache of the current item to render */
  private static final int SLOT_BACKGROUND_SIZE = 22;

  @Nonnull
  private static ItemStack nextOffhand = ItemStack.EMPTY;

  /** Items to render for the item frame modifier */
  private static final List<ItemStack> itemFrames = new ArrayList<>();

  /** Update the slot in the first shield slot */
  @SubscribeEvent
  static void equipmentChange(ToolEquipmentChangeEvent event) {
    EquipmentChangeContext context = event.getContext();
    if (Config.CLIENT.renderShieldSlotItem.get()) {
      if (event.getEntityLiving() == Minecraft.getInstance().player && context.getChangedSlot() == EquipmentSlotType.LEGS) {
        IModifierToolStack tool = context.getToolInSlot(EquipmentSlotType.LEGS);
        if (tool != null) {
          ShieldStrapModifier modifier = TinkerModifiers.shieldStrap.get();
          int level = tool.getModifierLevel(modifier);
          if (level > 0) {
            nextOffhand = modifier.getStack(tool, level, 0);
            return;
          }
        }
        nextOffhand = ItemStack.EMPTY;
      }
    }

    if (Config.CLIENT.renderItemFrame.get()) {
      if (event.getEntityLiving() == Minecraft.getInstance().player && context.getChangedSlot() == EquipmentSlotType.HEAD) {
        itemFrames.clear();
        IModifierToolStack tool = context.getToolInSlot(EquipmentSlotType.HEAD);
        if (tool != null) {
          ItemFrameModifier modifier = TinkerModifiers.itemFrame.get();
          int level = tool.getModifierLevel(modifier);
          if (level > 0) {
            modifier.getAllStacks(tool, level, itemFrames);
          }
        }
      }
    }
  }

  /** Render the item in the first shield slot */
  @SubscribeEvent
  static void renderHotbar(RenderGameOverlayEvent.Post event) {
    boolean renderShield = Config.CLIENT.renderShieldSlotItem.get() && !nextOffhand.isEmpty();
    boolean renderItemFrame = Config.CLIENT.renderItemFrame.get() && !itemFrames.isEmpty();
    if (!renderItemFrame && !renderShield) {
      return;
    }
    if (event.getType() == ElementType.HOTBAR) {
      Minecraft mc = Minecraft.getInstance();
      PlayerController playerController = Minecraft.getInstance().playerController;
      if (playerController != null && playerController.getCurrentGameType() != GameType.SPECTATOR) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player == mc.getRenderViewEntity()) {
          RenderSystem.enableRescaleNormal();
          RenderSystem.enableBlend();
          RenderSystem.defaultBlendFunc();

          int scaledWidth = mc.getMainWindow().getScaledWidth();
          int scaledHeight = mc.getMainWindow().getScaledHeight();
          MatrixStack matrixStack = event.getMatrixStack();
          float partialTicks = event.getPartialTicks();

          // want just above the normal hotbar item
          if (renderShield) {
            mc.getTextureManager().bindTexture(Icons.ICONS);
            int x = scaledWidth / 2 + (player.getPrimaryHand().opposite() == HandSide.LEFT ? -117 : 101);
            int y = scaledHeight - 38;
            Screen.blit(matrixStack, x - 3, y - 3, player.getHeldItemOffhand().isEmpty() ? 211 : 189, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
            mc.ingameGUI.renderHotbarItem(x, y, partialTicks, player, nextOffhand);
          }

          if (renderItemFrame) {
            // determine how many items need to be rendered
            int columns = Config.CLIENT.itemsPerRow.get();
            int count = itemFrames.size();
            // need to split items over multiple lines potentially
            int rows = count / columns;
            int inLastRow = count % columns;
            // if we have an exact number, means we should have full in last row
            if (inLastRow == 0) {
              inLastRow = columns;
            } else {
              // we have an incomplete row that was not counted
              rows++;
            }
            // determine placement of the items
            Orientation2D location = Config.CLIENT.itemFrameLocation.get();
            Orientation1D xOrientation = location.getX();
            Orientation1D yOrientation = location.getY();
            int xStart = xOrientation.align(scaledWidth - SLOT_BACKGROUND_SIZE * columns) + Config.CLIENT.itemFrameXOffset.get();
            int yStart = yOrientation.align(scaledHeight - SLOT_BACKGROUND_SIZE * rows) + Config.CLIENT.itemFrameYOffset.get();

            // draw backgrounds
            mc.getTextureManager().bindTexture(Icons.ICONS);
            int lastRow = rows - 1;
            for (int r = 0; r < lastRow; r++) {
              for (int c = 0; c < columns; c++) {
                Screen.blit(matrixStack, xStart + c * SLOT_BACKGROUND_SIZE, yStart + r * SLOT_BACKGROUND_SIZE, 167, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
              }
            }
            // last row will be aligned in the direction of x orientation (center, left, or right)
            int lastRowOffset = xOrientation.align((columns - inLastRow) * 2) * SLOT_BACKGROUND_SIZE / 2;
            for (int c = 0; c < inLastRow; c++) {
              Screen.blit(matrixStack, xStart + c * SLOT_BACKGROUND_SIZE + lastRowOffset, yStart + lastRow * SLOT_BACKGROUND_SIZE, 167, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
            }

            // draw items
            int i = 0;
            xStart += 3; yStart += 3; // offset from item start instead of frame start
            for (int r = 0; r < lastRow; r++) {
              for (int c = 0; c < columns; c++) {
                mc.ingameGUI.renderHotbarItem(xStart + c * SLOT_BACKGROUND_SIZE, yStart + r * SLOT_BACKGROUND_SIZE, partialTicks, player, itemFrames.get(i));
                i++;
              }
            }
            // align last row
            for (int c = 0; c < inLastRow; c++) {
              mc.ingameGUI.renderHotbarItem(xStart + c * SLOT_BACKGROUND_SIZE + lastRowOffset, yStart + lastRow * SLOT_BACKGROUND_SIZE, partialTicks, player, itemFrames.get(i));
              i++;
            }
          }

          RenderSystem.disableRescaleNormal();
          RenderSystem.disableBlend();
        }
      }
    }
  }
}

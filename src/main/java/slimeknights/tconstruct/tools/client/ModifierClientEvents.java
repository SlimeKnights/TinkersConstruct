package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.FOVModifierEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.events.ToolEquipmentChangeEvent;
import slimeknights.tconstruct.library.modifiers.data.FloatMultiplier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.Orientation2D;
import slimeknights.tconstruct.library.utils.Orientation2D.Orientation1D;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ShieldStrapModifier;
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
        if (text instanceof TranslatableComponent) {
          return ((TranslatableComponent)text).getKey().equals("item.durability");
        }
        return false;
      });
    }
  }

  /** Determines whether to render the given hand based on modifiers */
  @SubscribeEvent
  static void renderHand(RenderHandEvent event) {
    InteractionHand hand = event.getHand();
    Player player = Minecraft.getInstance().player;
    if (hand != InteractionHand.OFF_HAND || player == null) {
      return;
    }
    ItemStack mainhand = player.getMainHandItem();
    ItemStack offhand = event.getItemStack();
    if (mainhand.is(TinkerTags.Items.TWO_HANDED)) {
      ToolStack tool = ToolStack.from(mainhand);
      // special support for replacing modifier
      IModDataView volatileData = tool.getVolatileData();
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
    if (offhand.isEmpty()) {
      if (!player.isInvisible() && mainhand.getItem() != Items.FILLED_MAP && ModifierUtil.getTotalModifierLevel(player, TinkerDataKeys.SHOW_EMPTY_OFFHAND) > 0) {
        PoseStack matrices = event.getPoseStack();
        matrices.pushPose();
        Minecraft.getInstance().getItemInHandRenderer().renderPlayerArm(matrices, event.getMultiBufferSource(), event.getPackedLight(), event.getEquipProgress(), event.getSwingProgress(), player.getMainArm().getOpposite());
        matrices.popPose();
        event.setCanceled(true);
      }
      // if the offhand is two handed and is not upgraded to be used
    } else if (offhand.is(TinkerTags.Items.TWO_HANDED) && !ModifierUtil.checkVolatileFlag(offhand, IModifiable.DEFER_OFFHAND)) {
      event.setCanceled(true);
    }
  }

  /** Handles the zoom modifier zooming */
  @SubscribeEvent
  static void handleZoom(FOVModifierEvent event) {
    event.getEntity().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      FloatMultiplier zoom = data.get(TinkerDataKeys.FOV_MODIFIER);
      if (zoom != null) {
        event.setNewfov(event.getNewfov() * zoom.getValue());
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
      if (event.getEntityLiving() == Minecraft.getInstance().player && context.getChangedSlot() == EquipmentSlot.LEGS) {
        IToolStackView tool = context.getToolInSlot(EquipmentSlot.LEGS);
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
      if (event.getEntityLiving() == Minecraft.getInstance().player && context.getChangedSlot() == EquipmentSlot.HEAD) {
        itemFrames.clear();
        IToolStackView tool = context.getToolInSlot(EquipmentSlot.HEAD);
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
  static void renderHotbar(RenderGameOverlayEvent.PostLayer event) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.options.hideGui) {
      return;
    }
    IIngameOverlay overlay = event.getOverlay();
    if (overlay != ForgeIngameGui.HOTBAR_ELEMENT) {
      return;
    }
    boolean renderShield = Config.CLIENT.renderShieldSlotItem.get() && !nextOffhand.isEmpty();
    boolean renderItemFrame = Config.CLIENT.renderItemFrame.get() && !itemFrames.isEmpty();
    if (!renderItemFrame && !renderShield) {
      return;
    }
    MultiPlayerGameMode playerController = Minecraft.getInstance().gameMode;
    if (playerController != null && playerController.getPlayerMode() != GameType.SPECTATOR) {
      Player player = Minecraft.getInstance().player;
      if (player != null && player == mc.getCameraEntity()) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        int scaledWidth = mc.getWindow().getGuiScaledWidth();
        int scaledHeight = mc.getWindow().getGuiScaledHeight();
        PoseStack matrixStack = event.getMatrixStack();
        float partialTicks = event.getPartialTicks();

        // want just above the normal hotbar item
        if (renderShield) {
          RenderSystem.setShaderTexture(0, Icons.ICONS);
          int x = scaledWidth / 2 + (player.getMainArm().getOpposite() == HumanoidArm.LEFT ? -117 : 101);
          int y = scaledHeight - 38;
          Screen.blit(matrixStack, x - 3, y - 3, player.getOffhandItem().isEmpty() ? 211 : 189, 0, SLOT_BACKGROUND_SIZE, SLOT_BACKGROUND_SIZE, 256, 256);
          mc.gui.renderSlot(x, y, partialTicks, player, nextOffhand, 11);
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
          RenderSystem.setShaderTexture(0, Icons.ICONS);
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
              mc.gui.renderSlot(xStart + c * SLOT_BACKGROUND_SIZE, yStart + r * SLOT_BACKGROUND_SIZE, partialTicks, player, itemFrames.get(i), i);
              i++;
            }
          }
          // align last row
          for (int c = 0; c < inLastRow; c++) {
            mc.gui.renderSlot(xStart + c * SLOT_BACKGROUND_SIZE + lastRowOffset, yStart + lastRow * SLOT_BACKGROUND_SIZE, partialTicks, player, itemFrames.get(i), i);
            i++;
          }
        }

        RenderSystem.disableBlend();
      }
    }
  }
}

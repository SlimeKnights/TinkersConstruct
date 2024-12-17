package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.Icons;
import slimeknights.tconstruct.library.events.ToolEquipmentChangeEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.data.FloatMultiplier;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Orientation2D;
import slimeknights.tconstruct.library.utils.Orientation2D.Orientation1D;
import slimeknights.tconstruct.tools.TinkerModifiers;

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

        if (text.getContents() instanceof TranslatableContents translatable) {
          return translatable.getKey().equals("item.durability");
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

    // if the data is set, render the empty offhand
    ItemStack offhand = event.getItemStack();
    if (offhand.isEmpty()) {
      if (!player.isInvisible() && player.getMainHandItem().getItem() != Items.FILLED_MAP && ArmorLevelModule.getLevel(player, TinkerDataKeys.SHOW_EMPTY_OFFHAND) > 0) {
        PoseStack matrices = event.getPoseStack();
        matrices.pushPose();
        Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer().renderPlayerArm(matrices, event.getMultiBufferSource(), event.getPackedLight(), event.getEquipProgress(), event.getSwingProgress(), player.getMainArm().getOpposite());
        matrices.popPose();
        event.setCanceled(true);
      }
    }
  }

  /** Handles the zoom modifier zooming */
  @SubscribeEvent
  static void handleZoom(ComputeFovModifierEvent event) {
    event.getPlayer().getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      float newFov = event.getNewFovModifier();

      // scaled effects only apply if we have FOV scaling, nothing to do if 0
      float effectScale = Minecraft.getInstance().options.fovEffectScale().get().floatValue();
      if (effectScale > 0) {
        FloatMultiplier scaledZoom = data.get(TinkerDataKeys.SCALED_FOV_MODIFIER);
        if (scaledZoom != null) {
          // much easier when 1, save some effort
          if (effectScale == 1) {
            newFov *= scaledZoom.getValue();
          } else {
            // unlerp the fov before multiplitying to make sure we apply the proper amount
            // we could use the original FOV, but someone else may have modified it
            float original = event.getFovModifier();
            newFov *= Mth.lerp(effectScale, 1.0F, scaledZoom.getValue() * original) / original;
          }
        }
      }

      // non-scaled effects are much easier to deal with
      FloatMultiplier constZoom = data.get(TinkerDataKeys.FOV_MODIFIER);
      if (constZoom != null) {
        newFov *= constZoom.getValue();
      }
      event.setNewFovModifier(newFov);
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
      if (event.getEntity() == Minecraft.getInstance().player && context.getChangedSlot() == EquipmentSlot.LEGS) {
        IToolStackView tool = context.getToolInSlot(EquipmentSlot.LEGS);
        if (tool != null) {
          ModifierEntry entry = tool.getModifiers().getEntry(TinkerModifiers.shieldStrap.getId());
          if (entry != ModifierEntry.EMPTY) {
            nextOffhand = entry.getHook(ToolInventoryCapability.HOOK).getStack(tool, entry, 0);
            return;
          }
        }
        nextOffhand = ItemStack.EMPTY;
      }
    }

    if (Config.CLIENT.renderItemFrame.get()) {
      if (event.getEntity() == Minecraft.getInstance().player && context.getChangedSlot() == EquipmentSlot.HEAD) {
        itemFrames.clear();
        IToolStackView tool = context.getToolInSlot(EquipmentSlot.HEAD);
        if (tool != null) {
          ModifierEntry entry = tool.getModifier(TinkerModifiers.itemFrame.getId());
          if (entry.intEffectiveLevel() > 0) {
            entry.getHook(ToolInventoryCapability.HOOK).getAllStacks(tool, entry, itemFrames);
          }
        }
      }
    }
  }

  /** Render the item in the first shield slot */
  @SubscribeEvent
  static void renderHotbar(RenderGuiOverlayEvent.Post event) {
    Minecraft mc = Minecraft.getInstance();
    if (mc.options.hideGui) {
      return;
    }
    if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) {
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
        PoseStack matrixStack = event.getPoseStack();
        float partialTicks = event.getPartialTick();

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

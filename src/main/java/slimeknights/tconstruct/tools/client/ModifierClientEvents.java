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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
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
import org.apache.commons.lang3.mutable.MutableInt;
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
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ability.armor.ZoomModifier;

import javax.annotation.Nonnull;
import java.util.List;

/** Modifier event hooks that run client side */
@EventBusSubscriber(modid = TConstruct.MOD_ID, value = Dist.CLIENT, bus = Bus.FORGE)
public class ModifierClientEvents {
  @SubscribeEvent
  static void onTooltipEvent(ItemTooltipEvent event) {
    if (event.getItemStack().getItem() instanceof IModifiableDisplay) {
      boolean isShift = Screen.hasShiftDown();
      boolean isCtrl = !isShift && ((IModifiableDisplay) event.getItemStack().getItem()).getToolDefinition().isMultipart() && Screen.hasControlDown();
      MutableInt removedWhenIn = new MutableInt(0);
      event.getToolTip().removeIf(text -> {
        // its hard to find the blank line before attributes, so shift just removes all of them
        if ((isShift || (isCtrl && removedWhenIn.intValue() > 0)) && text == StringTextComponent.EMPTY) {
          return true;
        }
        // the attack damage and attack speed ones are formatted weirdly, suppress on both tooltips
        if ((isShift || isCtrl) && " ".equals(text.getUnformattedComponentText())) {
          List<ITextComponent> siblings = text.getSiblings();
          if (!siblings.isEmpty() && siblings.get(0) instanceof TranslationTextComponent) {
            return ((TranslationTextComponent) siblings.get(0)).getKey().startsWith("attribute.modifier.equals.");
          }
        }
        if (text instanceof TranslationTextComponent) {
          String key = ((TranslationTextComponent)text).getKey();

          // we want to ignore all modifiers after "when in off hand" as its typically redundant to the main hand, you will see without shift
          if ((isCtrl || isShift) && key.startsWith("item.modifiers.")) {
            removedWhenIn.add(1);
            return true;
          }

          // suppress durability from advanced, we display our own
          return key.equals("item.durability")
                 // the "when in main hand" text, don't need on either tooltip
                 || ((isCtrl || (isShift && removedWhenIn.intValue() > 1)) && key.startsWith("attribute.modifier."));
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
  @Nonnull
  private static ItemStack nextOffhand = ItemStack.EMPTY;

  /** Update the slot in the first shield slot */
  @SubscribeEvent
  static void equipmentChange(ToolEquipmentChangeEvent event) {
    if (!Config.CLIENT.renderShieldSlotItem.get()) {
      return;
    }
    EquipmentChangeContext context = event.getContext();
    if (event.getEntityLiving() == Minecraft.getInstance().player && context.getChangedSlot() == EquipmentSlotType.LEGS) {
      IModifierToolStack tool = context.getToolInSlot(EquipmentSlotType.LEGS);
      if (tool != null) {
        int level = tool.getModifierLevel(TinkerModifiers.shieldStrap.get());
        if (level > 0) {
          nextOffhand = TinkerModifiers.shieldStrap.get().getStack(tool, level, 0);
          return;
        }
      }
      nextOffhand = ItemStack.EMPTY;
    }
  }

  /** Render the item in the first shield slot */
  @SubscribeEvent
  static void renderHotbar(RenderGameOverlayEvent.Post event) {
    if (!Config.CLIENT.renderShieldSlotItem.get()) {
      return;
    }
    if (event.getType() == ElementType.HOTBAR && !nextOffhand.isEmpty()) {
      Minecraft mc = Minecraft.getInstance();
      PlayerController playerController = Minecraft.getInstance().playerController;
      if (playerController != null && playerController.getCurrentGameType() != GameType.SPECTATOR) {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player != null && player == mc.getRenderViewEntity()) {
          RenderSystem.enableRescaleNormal();
          RenderSystem.enableBlend();
          RenderSystem.defaultBlendFunc();

          mc.getTextureManager().bindTexture(Icons.ICONS);

          // want just above the normal hotbar item
          int x = mc.getMainWindow().getScaledWidth() / 2 + (player.getPrimaryHand().opposite() == HandSide.LEFT ? -117 : 101);
          int y = mc.getMainWindow().getScaledHeight() - 38;
          Screen.blit(event.getMatrixStack(), x - 3, y - 3, player.getHeldItemOffhand().isEmpty() ? 211 : 189, 0, 22, 22, 256, 256);
          mc.ingameGUI.renderHotbarItem(x, y, event.getPartialTicks(), player, nextOffhand);

          RenderSystem.disableRescaleNormal();
          RenderSystem.disableBlend();
        }
      }
    }
  }
}

package slimeknights.tconstruct.library.events;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.IModifier;
import slimeknights.tconstruct.library.modifiers.TinkerGuiException;
import slimeknights.tconstruct.library.utils.TinkerUtil;

/**
 * These events are fired when the player crafts something using tinker blocks.
 * E.g. a pickaxe, a pattern, a toolpart,...
 * The events can be cancelled to prevent crafting. When doing so, it is advertised to give a localized
 * message to display to the player.
 */
@Cancelable
public class TinkerCraftingEvent extends TinkerEvent {
  private final ItemStack itemStack;
  private final EntityPlayer player;
  private String message;

  protected TinkerCraftingEvent(ItemStack itemStack, EntityPlayer player, String message) {
    this.itemStack = itemStack;
    this.player = player;

    message += "\n" + TextFormatting.ITALIC + "by " + Loader.instance().activeModContainer().getName();
    this.message = message;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

  public String getMessage() {
    return message;
  }

  public EntityPlayer getPlayer() {
    return player;
  }

  public void setCanceled(String localizedMessage) {
    this.message = localizedMessage;
    setCanceled(true);
  }

  /**
   * Fired when a tool is being built in a tool station/forge.
   * Cancelable.
   * Be sure to provide a proper message when cancelling, so the user know what's going on!
   */
  public static class ToolCraftingEvent extends TinkerCraftingEvent {

    private final NonNullList<ItemStack> toolParts;

    private ToolCraftingEvent(ItemStack itemStack, EntityPlayer player, NonNullList<ItemStack> toolParts) {
      super(itemStack, player, Util.translate("gui.error.craftevent.tool.default"));
      this.toolParts = toolParts;
    }

    public NonNullList<ItemStack> getToolParts() {
      return toolParts;
    }

    public static void fireEvent(ItemStack itemStack, EntityPlayer player, NonNullList<ItemStack> toolParts) throws TinkerGuiException {
      ToolCraftingEvent toolCraftingEvent = new ToolCraftingEvent(itemStack, player, toolParts);
      if(MinecraftForge.EVENT_BUS.post(toolCraftingEvent)) {
        throw new TinkerGuiException(toolCraftingEvent.getMessage());
      }
    }
  }

  /**
   * Fired when a toolpart is being replaced on a tool station/forge. Multiple parts can be exchanged at the same time
   * Cancelable.
   * Be sure to provide a proper message when cancelling, so the user know what's going on!
   */
  public static class ToolPartReplaceEvent extends TinkerCraftingEvent {

    private final NonNullList<ItemStack> toolParts;

    private ToolPartReplaceEvent(ItemStack itemStack, EntityPlayer player, NonNullList<ItemStack> toolParts) {
      super(itemStack, player, Util.translate("gui.error.craftevent.replace.default"));
      this.toolParts = toolParts;
    }

    public NonNullList<ItemStack> getToolParts() {
      return toolParts;
    }

    public static void fireEvent(ItemStack itemStack, EntityPlayer player, NonNullList<ItemStack> toolParts) throws TinkerGuiException {
      ToolPartReplaceEvent toolPartReplaceEvent = new ToolPartReplaceEvent(itemStack, player, toolParts);
      if(MinecraftForge.EVENT_BUS.post(toolPartReplaceEvent)) {
        throw new TinkerGuiException(toolPartReplaceEvent.getMessage());
      }
    }
  }

  /**
   * Fired when a tool is being modified in a tool station/forge. Multiple modifiers can be applied at once.
   * Cancelable.
   * Be sure to provide a proper message when cancelling, so the user know what's going on!
   */
  public static class ToolModifyEvent extends TinkerCraftingEvent {
    private final List<IModifier> modifiers;
    private final ItemStack toolBeforeModification;

    protected ToolModifyEvent(ItemStack itemStack, EntityPlayer player, ItemStack toolBeforeModification) {
      super(itemStack, player, Util.translate("gui.error.craftevent.modifier.default"));
      this.toolBeforeModification = toolBeforeModification;

      List<IModifier> modifiers = TinkerUtil.getModifiers(itemStack);
      modifiers.removeAll(TinkerUtil.getModifiers(toolBeforeModification));

      this.modifiers = ImmutableList.copyOf(modifiers);
    }

    public List<IModifier> getModifiers() {
      return modifiers;
    }

    public ItemStack getToolBeforeModification() {
      return toolBeforeModification;
    }

    public static void fireEvent(ItemStack itemStack, EntityPlayer player, ItemStack toolBeforeModification) throws TinkerGuiException {
      ToolModifyEvent toolModifyEvent = new ToolModifyEvent(itemStack, player, toolBeforeModification);
      if(MinecraftForge.EVENT_BUS.post(toolModifyEvent)) {
        throw new TinkerGuiException(toolModifyEvent.getMessage());
      }
    }
  }

  /**
   * Fired when a toolpart is being crafted in a partbuilder.
   * Cancelable.
   * Be sure to provide a proper message when cancelling, so the user know what's going on!
   */
  public static class ToolPartCraftingEvent extends TinkerCraftingEvent {

    private ToolPartCraftingEvent(ItemStack itemStack, EntityPlayer player) {
      super(itemStack, player, Util.translate("gui.error.craftevent.toolpart.default"));
    }

    public static void fireEvent(ItemStack itemStack, EntityPlayer player) throws TinkerGuiException {
      ToolPartCraftingEvent toolPartCraftingEvent = new ToolPartCraftingEvent(itemStack, player);
      if(MinecraftForge.EVENT_BUS.post(toolPartCraftingEvent)) {
        throw new TinkerGuiException(toolPartCraftingEvent.getMessage());
      }
    }
  }
}

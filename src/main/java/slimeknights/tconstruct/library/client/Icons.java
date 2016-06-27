package slimeknights.tconstruct.library.client;

import net.minecraft.util.ResourceLocation;

import slimeknights.mantle.client.gui.GuiElement;
import slimeknights.tconstruct.library.Util;

public interface Icons {

  ResourceLocation ICON = Util.getResource("textures/gui/icons.png");

  GuiElement ICON_Anvil = new GuiElement(18 * 3, 0, 18, 18, 256, 256);
  GuiElement ICON_Pattern = new GuiElement(18 * 0, 18 * 12, 18, 18);
  GuiElement ICON_Shard = new GuiElement(18 * 1, 18 * 12, 18, 18);
  GuiElement ICON_Block = new GuiElement(18 * 2, 18 * 12, 18, 18);
  GuiElement ICON_Pickaxe = new GuiElement(18 * 0, 18 * 13, 18, 18);
  GuiElement ICON_Dust = new GuiElement(18 * 1, 18 * 13, 18, 18);
  GuiElement ICON_Lapis = new GuiElement(18 * 2, 18 * 13, 18, 18);
  GuiElement ICON_Ingot = new GuiElement(18 * 3, 18 * 13, 18, 18);
  GuiElement ICON_Gem = new GuiElement(18 * 4, 18 * 13, 18, 18);
  GuiElement ICON_Quartz = new GuiElement(18 * 5, 18 * 13, 18, 18);
  GuiElement ICON_Button = new GuiElement(180, 216, 18, 18);
  GuiElement ICON_ButtonHover = new GuiElement(180 + 18 * 2, 216, 18, 18);
  GuiElement ICON_ButtonPressed = new GuiElement(180 - 18 * 2, 216, 18, 18);

  GuiElement ICON_PIGGYBACK_1 = new GuiElement(18 * 13, 18 * 0, 18, 18);
  GuiElement ICON_PIGGYBACK_2 = new GuiElement(18 * 13, 18 * 1, 18, 18);
  GuiElement ICON_PIGGYBACK_3 = new GuiElement(18 * 13, 18 * 2, 18, 18);
}

package slimeknights.tconstruct.library.client;

import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.Util;

public interface Icons {

  ResourceLocation ICON = Util.getResource("textures/gui/icons.png");

  ElementScreen ICON_Anvil = new ElementScreen(18 * 3, 0, 18, 18, 256, 256);
  ElementScreen ICON_Pattern = new ElementScreen(0, 18 * 12, 18, 18);
  ElementScreen ICON_Shard = new ElementScreen(18, 18 * 12, 18, 18);
  ElementScreen ICON_Block = new ElementScreen(18 * 2, 18 * 12, 18, 18);
  ElementScreen ICON_Pickaxe = new ElementScreen(0, 18 * 13, 18, 18);
  ElementScreen ICON_Dust = new ElementScreen(18, 18 * 13, 18, 18);
  ElementScreen ICON_Lapis = new ElementScreen(18 * 2, 18 * 13, 18, 18);
  ElementScreen ICON_Ingot = new ElementScreen(18 * 3, 18 * 13, 18, 18);
  ElementScreen ICON_Gem = new ElementScreen(18 * 4, 18 * 13, 18, 18);
  ElementScreen ICON_Quartz = new ElementScreen(18 * 5, 18 * 13, 18, 18);
  ElementScreen ICON_Button = new ElementScreen(180, 216, 18, 18);
  ElementScreen ICON_ButtonHover = new ElementScreen(180 + 18 * 2, 216, 18, 18);
  ElementScreen ICON_ButtonPressed = new ElementScreen(180 - 18 * 2, 216, 18, 18);

  ElementScreen ICON_PIGGYBACK_1 = new ElementScreen(18 * 13, 0, 18, 18);
  ElementScreen ICON_PIGGYBACK_2 = new ElementScreen(18 * 13, 18, 18, 18);
  ElementScreen ICON_PIGGYBACK_3 = new ElementScreen(18 * 13, 18 * 2, 18, 18);
}

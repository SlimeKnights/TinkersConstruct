package slimeknights.tconstruct.library.client;

import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.library.Util;

public interface Icons {

  ResourceLocation ICONS = Util.getResource("textures/gui/icons.png");

  ElementScreen ANVIL_ICON = new ElementScreen(18 * 3, 0, 18, 18, 256, 256);
  ElementScreen PATTERN_ICON = new ElementScreen(0, 18 * 12, 18, 18);
  ElementScreen SHARD_ICON = new ElementScreen(18, 18 * 12, 18, 18);
  ElementScreen BLOCK_ICON = new ElementScreen(18 * 2, 18 * 12, 18, 18);
  ElementScreen PICKAXE_ICON = new ElementScreen(0, 18 * 13, 18, 18);
  ElementScreen DUST_ICON = new ElementScreen(18, 18 * 13, 18, 18);
  ElementScreen LAPIS_ICON = new ElementScreen(18 * 2, 18 * 13, 18, 18);
  ElementScreen INGOT_ICON = new ElementScreen(18 * 3, 18 * 13, 18, 18);
  ElementScreen GEM_ICON = new ElementScreen(18 * 4, 18 * 13, 18, 18);
  ElementScreen QUARTZ_ICON = new ElementScreen(18 * 5, 18 * 13, 18, 18);
  ElementScreen BUTTON_ICON = new ElementScreen(180, 216, 18, 18);
  ElementScreen BUTTON_HOVERED_ICON = new ElementScreen(180 + 18 * 2, 216, 18, 18);
  ElementScreen BUTTON_PRESSED_ICON = new ElementScreen(180 - 18 * 2, 216, 18, 18);

  ElementScreen PIGGYBACK_1_ICON = new ElementScreen(18 * 13, 0, 18, 18);
  ElementScreen PIGGYBACK_2_ICON = new ElementScreen(18 * 13, 18, 18, 18);
  ElementScreen PIGGYBACK_3_ICON = new ElementScreen(18 * 13, 18 * 2, 18, 18);
}

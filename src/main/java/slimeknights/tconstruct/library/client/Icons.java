package slimeknights.tconstruct.library.client;

import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.client.screen.ElementScreen;
import slimeknights.tconstruct.TConstruct;

/** TODO: consider using {@link slimeknights.tconstruct.library.recipe.partbuilder.Pattern} instead. */
public interface Icons {
  ResourceLocation ICONS = TConstruct.getResource("textures/gui/icons.png");

  //ElementScreen ANVIL = new ElementScreen(18 * 3, 0, 18, 18, 256, 256);
  ElementScreen PATTERN = new ElementScreen(ICONS, 0, 18 * 12, 18, 18, 256, 256);
  //ElementScreen SHARD = new ElementScreen(18, 18 * 12, 18, 18);
  //ElementScreen BLOCK = new ElementScreen(18 * 2, 18 * 12, 18, 18);
  //ElementScreen PICKAXE = new ElementScreen(0, 18 * 13, 18, 18);
  //ElementScreen DUST = new ElementScreen(18, 18 * 13, 18, 18);
  //ElementScreen LAPIS = new ElementScreen(18 * 2, 18 * 13, 18, 18);
  ElementScreen INGOT = PATTERN.move(18 * 3, 18 * 13, 18, 18);
  //ElementScreen GEM = new ElementScreen(18 * 4, 18 * 13, 18, 18);
  //ElementScreen QUARTZ = new ElementScreen(18 * 5, 18 * 13, 18, 18);
  ElementScreen BUTTON = PATTERN.move(180, 216, 18, 18);
  ElementScreen BUTTON_HOVERED = PATTERN.move(180 + 18 * 2, 216, 18, 18);
  ElementScreen BUTTON_PRESSED = PATTERN.move(180 - 18 * 2, 216, 18, 18);

  ElementScreen PIGGYBACK_1 = PATTERN.move(18 * 13, 0, 18, 18);
  ElementScreen PIGGYBACK_2 = PATTERN.move(18 * 13, 18, 18, 18);
  ElementScreen PIGGYBACK_3 = PATTERN.move(18 * 13, 18 * 2, 18, 18);
}

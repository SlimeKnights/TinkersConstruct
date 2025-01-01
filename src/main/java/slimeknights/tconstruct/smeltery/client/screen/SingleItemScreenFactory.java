package slimeknights.tconstruct.smeltery.client.screen;

import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import slimeknights.mantle.client.screen.BackgroundContainerScreen;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.smeltery.menu.SingleItemContainerMenu;

import javax.annotation.Nullable;

/**
 * Screen factory for the single item container, one container for multiple backgrounds
 */
public class SingleItemScreenFactory implements ScreenConstructor<SingleItemContainerMenu,BackgroundContainerScreen<SingleItemContainerMenu>> {
  private static final int HEIGHT = 133;
  private static final ResourceLocation DEFAULT = TConstruct.getResource("textures/gui/blank.png");

  /**
   * Gets the background path for the given tile
   * @param tile  Tile
   * @return  Background path
   */
  private static ResourceLocation getBackground(@Nullable BlockEntity tile) {
    if (tile != null) {
      ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(tile.getType());
      if (id != null) {
        return new ResourceLocation(id.getNamespace(), String.format("textures/gui/%s.png", id.getPath()));
      }
    }
    return DEFAULT;
  }

  @Override
  public BackgroundContainerScreen<SingleItemContainerMenu> create(SingleItemContainerMenu container, Inventory inventory, Component name) {
    return new BackgroundContainerScreen<>(container, inventory, name, HEIGHT, getBackground(container.getTile()));
  }
}

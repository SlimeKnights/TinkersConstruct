package slimeknights.tconstruct.smeltery.client;

import net.minecraft.client.gui.ScreenManager.IScreenFactory;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import slimeknights.mantle.client.screen.BackgroundContainerScreen;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.smeltery.inventory.SingleItemContainer;

import org.jetbrains.annotations.Nullable;

/**
 * Screen factory for the single item container, one container for multiple backgrounds
 */
public class SingleItemScreenFactory implements IScreenFactory<SingleItemContainer,BackgroundContainerScreen<SingleItemContainer>> {
  private static final int HEIGHT = 133;
  private static final ResourceLocation DEFAULT = Util.getResource("textures/gui/blank.png");

  /**
   * Gets the background path for the given tile
   * @param tile  Tile
   * @return  Background path
   */
  private static ResourceLocation getBackground(@Nullable TileEntity tile) {
    if (tile != null) {
      ResourceLocation id = tile.getType().getRegistryName();
      if (id != null) {
        return new ResourceLocation(id.getNamespace(), String.format("textures/gui/%s.png", id.getPath()));
      }
    }
    return DEFAULT;
  }

  @Override
  public BackgroundContainerScreen<SingleItemContainer> create(SingleItemContainer container, PlayerInventory inventory, ITextComponent name) {
    return new BackgroundContainerScreen<>(container, inventory, name, HEIGHT, getBackground(container.getTile()));
  }
}

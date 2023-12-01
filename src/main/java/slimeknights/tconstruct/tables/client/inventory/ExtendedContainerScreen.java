package slimeknights.tconstruct.tables.client.inventory;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.apache.commons.compress.utils.Lists;
import slimeknights.mantle.client.screen.MultiModuleScreen;
import slimeknights.mantle.inventory.MultiModuleContainerMenu;
import slimeknights.tconstruct.tables.client.inventory.widget.ExtraAreaWidget;

import java.util.List;

/**
 * A helper class that handles custom widget interfaces.
 * This class could potentially be relocated to mantle.
 * @author kirderf1
 */
public abstract class ExtendedContainerScreen<CONTAINER extends MultiModuleContainerMenu<?>> extends MultiModuleScreen<CONTAINER> {

  private final List<ExtraAreaWidget> extraAreas = Lists.newArrayList();

  public ExtendedContainerScreen(CONTAINER container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
  }

  protected <T extends ExtraAreaWidget> T addExtraArea(T widget) {
    this.extraAreas.add(widget);
    return widget;
  }

  @Override
  protected void removeWidget(GuiEventListener widget) {
    if(widget instanceof ExtraAreaWidget)
      this.extraAreas.remove(widget);

    super.removeWidget(widget);
  }

  @Override
  protected void clearWidgets() {
    super.clearWidgets();
    this.extraAreas.clear();
  }

  @Override
  public List<Rect2i> getModuleAreas() {
    List<Rect2i> areas = super.getModuleAreas();
    this.extraAreas.forEach(widget -> areas.add(widget.getArea()));
    return areas;
  }

  @Override
  protected boolean hasClickedOutside(double mouseX, double mouseY, int fuiLeft, int guiTop, int mouseButton) {
    return super.hasClickedOutside(mouseX, mouseY, fuiLeft, guiTop, mouseButton)
      && this.extraAreas.stream().noneMatch(widget -> widget.isInArea(mouseX, mouseY));
  }
}

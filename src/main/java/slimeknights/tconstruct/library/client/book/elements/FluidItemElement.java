package slimeknights.tconstruct.library.client.book.elements;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.client.screen.book.element.ItemElement;
import slimeknights.mantle.fluid.tooltip.FluidTooltipHandler;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.item.CopperCanItem;

import java.util.List;

/** Creates a book element for displaying a fluid stack as a bucket */
public class FluidItemElement extends ItemElement {
  private final List<FluidStack> fluids;
  public FluidItemElement(int x, int y, float scale, List<ItemStack> items, List<FluidStack> fluids) {
    super(x, y, scale, items);
    if (items.size() != fluids.size()) {
      throw new IllegalArgumentException("Items and fluids must be same size");
    }
    this.fluids = fluids;
  }

  public FluidItemElement(int x, int y, float scale, List<FluidStack> fluids) {
    this(x, y, scale, createItemList(fluids), fluids);
  }

  @Override
  public void drawOverlay(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Font fontRenderer) {
    if (this.isHovered(mouseX, mouseY) && this.currentItem < this.fluids.size()) {
      this.drawTooltip(graphics, FluidTooltipHandler.getFluidTooltip(this.fluids.get(this.currentItem)), mouseX, mouseY, fontRenderer);
    }
  }

  /** Creates a list of items for display */
  public static List<ItemStack> createItemList(List<FluidStack> fluids) {
    return fluids.stream().map(fluidStack -> {
      // if it has a bucket, use the bucket
      Item bucket = fluidStack.getFluid().getBucket();
      if (bucket != Items.AIR) {
        return bucket.getDefaultInstance();
      }
      return CopperCanItem.setFluid(new ItemStack(TinkerSmeltery.copperCan), fluidStack.getFluid(), null);
    }).toList();
  }
}

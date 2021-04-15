package slimeknights.tconstruct.library.client.model.block;

import slimeknights.mantle.client.model.fluid.FluidCuboid;
import slimeknights.mantle.client.model.inventory.ModelItem;
import java.util.List;

import net.minecraft.client.render.model.BakedModel;

/**
 * This model contains a single fluid region that is scaled in the TESR, and a list of two items displayed in the TESR
 */
public abstract class CastingModel implements BakedModel {

  private FluidCuboid fluidCuboid;
  private List<ModelItem> items;

  public List<ModelItem> getItems() {
    return items;
  }

  public FluidCuboid getFluid() {
    return fluidCuboid;
  }
}

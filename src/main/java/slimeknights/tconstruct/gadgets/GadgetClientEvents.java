package slimeknights.tconstruct.gadgets;

import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.ToolClientEvents;

public class GadgetClientEvents {

  private static final String LOCATION_RackBlock = Util.resource("rack");

  public static final ModelResourceLocation[] locRackDrying;
  public static final ModelResourceLocation[] locRackItem;
  public static final ModelResourceLocation locRackInventory = new ModelResourceLocation(LOCATION_RackBlock, "inventory");
  //public static final ModelResourceLocation locRackItemItem = new ModelResourceLocation(Util.resource("item_rack"), "inventory");
  //public static final ModelResourceLocation locRackDryingItem = new ModelResourceLocation(Util.resource("drying_rack"), "inventory");

  static {
    locRackItem = new ModelResourceLocation[8];
    locRackDrying = new ModelResourceLocation[8];
    for(int i = 0; i < 8; i++) {
      locRackItem[i] = new ModelResourceLocation(LOCATION_RackBlock, "drying=false,facing=" + EnumOrientation.byMetadata(i).getName());
      locRackDrying[i] = new ModelResourceLocation(LOCATION_RackBlock, "drying=true,facing=" + EnumOrientation.byMetadata(i).getName());
    }
  }

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    // convert racks
    for(int i = 0; i < 8; i++) {
      ToolClientEvents.replaceTableModel(locRackItem[i], event);
      ToolClientEvents.replaceTableModel(locRackDrying[i], event);
    }
    ToolClientEvents.replaceTableModel(locRackInventory, event);
  }
}

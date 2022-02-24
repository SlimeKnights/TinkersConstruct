package slimeknights.tconstruct.gadgets;

import net.minecraft.block.BlockLever.EnumOrientation;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.tools.ToolClientEvents;

public class GadgetClientEvents {

  private static final String LOCATION_RackBlock = Util.resource("rack");

  private static final ResourceLocation MODEL_RackUp = Util.getResource("block/rack");
  private static final ResourceLocation MODEL_RackDown = Util.getResource("block/rack_down");
  private static final ResourceLocation MODEL_RackSide = Util.getResource("block/rack_side");
  // easier to loop later if we can index these by metadata
  private static final ResourceLocation[] MODEL_Rack = new ResourceLocation[]{
      MODEL_RackDown,
      MODEL_RackSide,
      MODEL_RackSide,
      MODEL_RackSide,
      MODEL_RackSide,
      MODEL_RackUp,
      MODEL_RackUp,
      MODEL_RackDown
  };

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
      ToolClientEvents.replaceTableModel(locRackItem[i], MODEL_Rack[i], event);
      ToolClientEvents.replaceTableModel(locRackDrying[i], MODEL_Rack[i], event);
    }
    ToolClientEvents.replaceTableModel(locRackInventory, MODEL_RackUp, event);
  }
}

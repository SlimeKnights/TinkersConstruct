package slimeknights.tconstruct.smeltery;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.shared.client.BakedTableModel;
import slimeknights.tconstruct.tools.ToolClientEvents;

public class SmelteryClientEvents {

  // casting table/basin
  private static final String LOCATION_CastingBlock = Util.resource("casting");
  public static final ModelResourceLocation locCastingTable = new ModelResourceLocation(LOCATION_CastingBlock, "type=table");
  public static final ModelResourceLocation locCastingBasin = new ModelResourceLocation(LOCATION_CastingBlock, "type=basin");

  // Blank Pattern
  private static final ResourceLocation MODEL_BlankCast = Util.getResource("item/cast");
  public static final ResourceLocation locBlankCast = Util.getResource("cast");

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    // convert casting table and basin to bakedTableModel for the item-rendering on/in them
    //IModel model = event.modelLoader.getModel();
    wrap(event, locCastingTable);
    wrap(event, locCastingBasin);

    // add the extra cast models. See ToolClientEvents for more info with the pattern
    ToolClientEvents.replacePatternModel(locBlankCast, MODEL_BlankCast, event, CustomTextureCreator.castLocString, TinkerRegistry.getCastItems());
  }

  private void wrap(ModelBakeEvent event, ModelResourceLocation loc) {
    IBakedModel model = event.modelRegistry.getObject(loc);
    if(model != null && model instanceof IFlexibleBakedModel) {
      event.modelRegistry.putObject(loc, new BakedTableModel((IFlexibleBakedModel)model, null));
    }
  }
}

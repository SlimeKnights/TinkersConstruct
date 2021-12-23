package slimeknights.tconstruct.smeltery;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.shared.client.BakedTableModel;
import slimeknights.tconstruct.smeltery.block.BlockTank;
import slimeknights.tconstruct.smeltery.client.TankItemModel;
import slimeknights.tconstruct.tools.ToolClientEvents;

public class SmelteryClientEvents {

  // casting table/basin
  private static final String LOCATION_CastingBlock = Util.resource("casting");
  public static final ModelResourceLocation locCastingTable = new ModelResourceLocation(LOCATION_CastingBlock, "type=table");
  public static final ModelResourceLocation locCastingBasin = new ModelResourceLocation(LOCATION_CastingBlock, "type=basin");

  // Blank Pattern
  private static final ResourceLocation MODEL_BlankCast = Util.getResource("item/cast");
  public static final ResourceLocation locBlankCast = Util.getResource("cast");
  public static final ResourceLocation locClayCast = Util.getResource("clay_cast");

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    // convert casting table and basin to bakedTableModel for the item-rendering on/in them
    wrap(event, locCastingTable);
    wrap(event, locCastingBasin);

    // add the extra cast models. See ToolClientEvents for more info with the pattern
    ToolClientEvents.replacePatternModel(locBlankCast, MODEL_BlankCast, event, CustomTextureCreator.castLocString, TinkerRegistry.getCastItems());
    ToolClientEvents.replacePatternModel(locClayCast, MODEL_BlankCast, event, CustomTextureCreator.castLocString, TinkerRegistry.getCastItems(), 0xa77498);

    for (BlockTank.TankType type : BlockTank.TankType.values()) {
      replaceTankModel(event, new ModelResourceLocation(TinkerSmeltery.searedTank.getRegistryName(), type.getName()));
    }
  }

  private void wrap(ModelBakeEvent event, ModelResourceLocation loc) {
    IBakedModel model = event.getModelRegistry().getObject(loc);
    if(model != null && model instanceof IBakedModel) {
      event.getModelRegistry().putObject(loc, new BakedTableModel(model, null, DefaultVertexFormats.ITEM));
    }
  }

  private void replaceTankModel(ModelBakeEvent event, ModelResourceLocation loc) {
    IBakedModel baked = event.getModelRegistry().getObject(loc);
    if(baked != null) {
      event.getModelRegistry().putObject(loc, new TankItemModel(baked));
    }
  }
}

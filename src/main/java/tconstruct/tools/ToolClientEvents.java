package tconstruct.tools;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

import tconstruct.library.Util;
import tconstruct.tools.client.BakedTableModel;

public class ToolClientEvents {
  private static final ResourceLocation MODEL_StencilTable = Util.getResource("block/StencilTable");
  private static final ResourceLocation MODEL_PartBuilder  = Util.getResource("block/PartBuilder");
  private static final String LOCATION_ToolTable = Util.resource("ToolTables");

  private static final ResourceLocation MODEL_ToolForge  = Util.getResource("block/ToolForge");
  private static final String LOCATION_ToolForge = Util.resource("ToolForge");

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    // replace the baked table models with smart variants

    //replaceModel(new ModelResourceLocation(LOCATION_ToolTable, "type=stenciltable"), MODEL_StencilTable, event);
    replaceModel(new ModelResourceLocation(LOCATION_ToolTable, "type=partbuilder"), MODEL_PartBuilder, event);
    replaceModel(new ModelResourceLocation(LOCATION_ToolForge, "normal"), MODEL_ToolForge, event);
    //replaceModel(new ModelResourceLocation(LOCATION_ToolTable, "type=toolstation"), MODEL_ToolStation, event);
  }

  private void replaceModel(ModelResourceLocation modelVariantLocation, ResourceLocation modelLocation, ModelBakeEvent event) {
    try {
      IModel model = event.modelLoader.getModel(modelLocation);
      if(model instanceof IRetexturableModel) {
        IRetexturableModel tableModel = (IRetexturableModel) model;
        IFlexibleBakedModel standard = (IFlexibleBakedModel)event.modelRegistry.getObject(modelVariantLocation);
        IFlexibleBakedModel finalModel = new BakedTableModel(standard, tableModel);

        event.modelRegistry.putObject(modelVariantLocation, finalModel);
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}

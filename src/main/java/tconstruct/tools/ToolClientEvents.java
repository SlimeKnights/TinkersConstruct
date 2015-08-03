package tconstruct.tools;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

import tconstruct.common.client.model.BakedTableModel;
import tconstruct.library.TinkerRegistry;
import tconstruct.library.Util;
import tconstruct.library.client.model.ModelHelper;
import tconstruct.library.tools.IToolPart;

public class ToolClientEvents {

  private Function<ResourceLocation, TextureAtlasSprite> textureGetter;

  // tool tables
  private static final ResourceLocation MODEL_CraftingStation = Util.getResource("block/CraftingStation");
  private static final ResourceLocation MODEL_StencilTable = Util.getResource("block/StencilTable");
  private static final ResourceLocation MODEL_PartBuilder = Util.getResource("block/PartBuilder");
  private static final ResourceLocation MODEL_ToolStation = Util.getResource("block/ToolStation");
  private static final ResourceLocation MODEL_ToolForge = Util.getResource("block/ToolForge");
  private static final String LOCATION_ToolTable = Util.resource("ToolTables");
  private static final String LOCATION_ToolForge = Util.resource("ToolForge");

  // the actual locations where the models are located
  public static final ModelResourceLocation locCraftingStation = new ModelResourceLocation(LOCATION_ToolTable, "type=craftingstation");
  public static final ModelResourceLocation locStencilTable = new ModelResourceLocation(LOCATION_ToolTable, "type=stenciltable");
  public static final ModelResourceLocation locPartBuilder = new ModelResourceLocation(LOCATION_ToolTable, "type=partbuilder");
  public static final ModelResourceLocation locToolStation = new ModelResourceLocation(LOCATION_ToolTable, "type=toolstation");
  public static final ModelResourceLocation locToolForge = new ModelResourceLocation(LOCATION_ToolForge, "normal");

  // Blank Pattern
  private static final ResourceLocation MODEL_BlankPattern = Util.getResource("item/Pattern");
  public static final ResourceLocation locBlankPattern = Util.getResource("Pattern");

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
      public TextureAtlasSprite apply(ResourceLocation location) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
      }
    };

    // add the models for the pattern variants
    if(TinkerTools.pattern != null) {
      replacePatternModel(locBlankPattern, MODEL_BlankPattern, event);
    }

    // replace the baked table models with smart variants

    // tool tables
    replaceTableModel(locStencilTable, MODEL_StencilTable, event);
    replaceTableModel(locPartBuilder, MODEL_PartBuilder, event);
    // tool station has no variants
    replaceTableModel(locToolForge, MODEL_ToolForge, event);

    // silence the missing-model message for the default itemblock
    event.modelRegistry.putObject(new ModelResourceLocation(LOCATION_ToolTable, "inventory"), event.modelRegistry.getObject(locToolStation));
  }

  private void replaceTableModel(ModelResourceLocation modelVariantLocation, ResourceLocation modelLocation, ModelBakeEvent event) {
    try {
      IModel model = event.modelLoader.getModel(modelLocation);
      if(model instanceof IRetexturableModel) {
        IRetexturableModel tableModel = (IRetexturableModel) model;
        IFlexibleBakedModel standard = (IFlexibleBakedModel) event.modelRegistry.getObject(modelVariantLocation);
        IFlexibleBakedModel finalModel = new BakedTableModel(standard, tableModel);

        event.modelRegistry.putObject(modelVariantLocation, finalModel);
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  private void replacePatternModel(ResourceLocation locPattern, ResourceLocation modelLocation, ModelBakeEvent event) {
    try {
      IModel model = event.modelLoader.getModel(modelLocation);
      if(model instanceof IRetexturableModel) {
        IRetexturableModel itemModel = (IRetexturableModel) model;

        for(IToolPart toolpart : TinkerRegistry.getToolParts()) {
          if(!(toolpart instanceof Item))
            continue; // WHY?!

          ResourceLocation partLocation = ToolClientProxy.getItemLocation((Item) toolpart);
          String suffix = partLocation.getResourcePath();
          // get texture
          String partPatternLocation = locPattern.toString() + "_" + suffix;
          IModel partPatternModel = itemModel.retexture(ImmutableMap.of("layer0", partPatternLocation));
          IFlexibleBakedModel baked = partPatternModel.bake(partPatternModel.getDefaultState(), Attributes.DEFAULT_BAKED_FORMAT, textureGetter);
          event.modelRegistry.putObject(new ModelResourceLocation(partPatternLocation, "inventory"), baked);
        }
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}

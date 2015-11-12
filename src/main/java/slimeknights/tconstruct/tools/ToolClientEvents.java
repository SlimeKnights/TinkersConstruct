package slimeknights.tconstruct.tools;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Locale;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.shared.client.BakedTableModel;

@SideOnly(Side.CLIENT)
public class ToolClientEvents {

  public static Function<ResourceLocation, TextureAtlasSprite> textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
    public TextureAtlasSprite apply(ResourceLocation location) {
      return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
    }
  };

  // tool tables
  private static final ResourceLocation MODEL_CraftingStation = Util.getResource("block/craftingstation");
  private static final ResourceLocation MODEL_StencilTable = Util.getResource("block/stenciltable");
  private static final ResourceLocation MODEL_PartBuilder = Util.getResource("block/partbuilder");
  private static final ResourceLocation MODEL_ToolStation = Util.getResource("block/toolstation");
  private static final ResourceLocation MODEL_ToolForge = Util.getResource("block/toolforge");
  private static final String LOCATION_ToolTable = Util.resource("tooltables");
  private static final String LOCATION_ToolForge = Util.resource("toolforge");

  // the actual locations where the models are located
  public static final ModelResourceLocation locCraftingStation = new ModelResourceLocation(LOCATION_ToolTable, "type=craftingstation");
  public static final ModelResourceLocation locStencilTable = new ModelResourceLocation(LOCATION_ToolTable, "type=stenciltable");
  public static final ModelResourceLocation locPartBuilder = new ModelResourceLocation(LOCATION_ToolTable, "type=partbuilder");
  public static final ModelResourceLocation locToolStation = new ModelResourceLocation(LOCATION_ToolTable, "type=toolstation");
  public static final ModelResourceLocation locToolForge = new ModelResourceLocation(LOCATION_ToolForge, "normal");

  // Blank Pattern
  private static final ResourceLocation MODEL_BlankPattern = Util.getResource("item/pattern");
  public static final ResourceLocation locBlankPattern = Util.getResource("pattern");

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    // add the models for the pattern variants
    replacePatternModel(locBlankPattern, MODEL_BlankPattern, event, CustomTextureCreator.patternLocString);

    // replace the baked table models with smart variants

    // tool tables
    replaceTableModel(locCraftingStation, MODEL_CraftingStation, event);
    replaceTableModel(locStencilTable, MODEL_StencilTable, event);
    replaceTableModel(locPartBuilder, MODEL_PartBuilder, event);
    replaceTableModel(locToolStation, MODEL_ToolStation, event); // tool station has no variants but we want the item support
    replaceTableModel(locToolForge, MODEL_ToolForge, event);

    // silence the missing-model message for the default itemblock
    event.modelRegistry.putObject(new ModelResourceLocation(LOCATION_ToolTable, "inventory"), event.modelRegistry.getObject(locToolStation));
    event.modelRegistry.putObject(new ModelResourceLocation(LOCATION_ToolForge, "inventory"), event.modelRegistry.getObject(locToolForge));
  }

  public static void replaceTableModel(ModelResourceLocation modelVariantLocation, ResourceLocation modelLocation, ModelBakeEvent event) {
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

  public static void replacePatternModel(ResourceLocation locPattern, ResourceLocation modelLocation, ModelBakeEvent event, String baseString) {
    try {
      IModel model = event.modelLoader.getModel(modelLocation);
      if(model instanceof IRetexturableModel) {
        IRetexturableModel itemModel = (IRetexturableModel) model;

        for(IToolPart toolpart : TinkerRegistry.getToolParts()) {
          if(!(toolpart instanceof Item))
            continue; // WHY?!

          ResourceLocation partLocation = ToolClientProxy.getItemLocation((Item) toolpart);
          String suffix = partLocation.getResourcePath().toLowerCase(Locale.US);
          // get texture
          String partPatternLocation = locPattern.toString() + "_" + suffix;
          String partPatternTexture = baseString + suffix;
          IModel partPatternModel = itemModel.retexture(ImmutableMap.of("layer0", partPatternTexture));
          IFlexibleBakedModel baked = partPatternModel.bake(partPatternModel.getDefaultState(), DefaultVertexFormats.ITEM, textureGetter);
          event.modelRegistry.putObject(new ModelResourceLocation(partPatternLocation, "inventory"), baked);
        }
      }
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}

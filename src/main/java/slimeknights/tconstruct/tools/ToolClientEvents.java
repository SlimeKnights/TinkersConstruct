package slimeknights.tconstruct.tools;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

import slimeknights.mantle.client.model.BakedSimple;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.model.ModelHelper;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.Pattern;
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
    replacePatternModel(locBlankPattern, MODEL_BlankPattern, event, CustomTextureCreator.patternLocString, TinkerRegistry.getPatternItems());

    // replace the baked table models with smart variants

    // tool tables
    replaceTableModel(locCraftingStation, MODEL_CraftingStation, event);
    replaceTableModel(locStencilTable, MODEL_StencilTable, event);
    replaceTableModel(locPartBuilder, MODEL_PartBuilder, event);
    replaceTableModel(locToolStation, MODEL_ToolStation, event); // tool station has no variants but we want the item support
    replaceTableModel(locToolForge, MODEL_ToolForge, event);

    // silence the missing-model message for the default itemblock
    event.getModelRegistry().putObject(new ModelResourceLocation(LOCATION_ToolTable, "inventory"), event.getModelRegistry().getObject(locToolStation));
    event.getModelRegistry().putObject(new ModelResourceLocation(LOCATION_ToolForge, "inventory"), event.getModelRegistry().getObject(locToolForge));
  }

  public static void replaceTableModel(ModelResourceLocation modelVariantLocation, ResourceLocation modelLocation, ModelBakeEvent event) {
    try {
      IModel model = ModelLoaderRegistry.getModel(modelLocation);
      if(model instanceof IRetexturableModel) {
        IRetexturableModel tableModel = (IRetexturableModel) model;
        IBakedModel standard = event.getModelRegistry().getObject(modelVariantLocation);
        IBakedModel finalModel = new BakedTableModel(standard, tableModel, DefaultVertexFormats.BLOCK);

        event.getModelRegistry().putObject(modelVariantLocation, finalModel);
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void replacePatternModel(ResourceLocation locPattern, ResourceLocation modelLocation, ModelBakeEvent event, String baseString, Iterable<Item> items) {
    replacePatternModel(locPattern, modelLocation, event, baseString, items, -1);
  }

  public static void replacePatternModel(ResourceLocation locPattern, ResourceLocation modelLocation, ModelBakeEvent event, String baseString, Iterable<Item> items, int color) {
    try {
      IModel model = ModelLoaderRegistry.getModel(modelLocation);
      if(model instanceof IRetexturableModel) {
        IRetexturableModel itemModel = (IRetexturableModel) model;

        for(Item item : items) {
          String suffix = Pattern.getTextureIdentifier(item);
          // get texture
          String partPatternLocation = locPattern.toString() + suffix;
          String partPatternTexture = baseString + suffix;
          IModel partPatternModel = itemModel.retexture(ImmutableMap.of("layer0", partPatternTexture));
          IBakedModel baked = partPatternModel.bake(partPatternModel.getDefaultState(), DefaultVertexFormats.ITEM, textureGetter);
          if(color > -1) {
            ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
            // ItemLayerModel.BakedModel only uses general quads
            for(BakedQuad quad : baked.getQuads(null, null, 0)) {
              quads.add(ModelHelper.colorQuad(color, quad));
            }
            baked = new BakedSimple.Wrapper(quads.build(), ((IPerspectiveAwareModel)baked));
          }
          event.getModelRegistry().putObject(new ModelResourceLocation(partPatternLocation, "inventory"), baked);
        }
      }
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  //@SubscribeEvent
  public void materialTooltip(ItemTooltipEvent event) {
    // check if the item belongs to a material
    for(Material material : TinkerRegistry.getAllMaterials()) {
      if(material.matches(event.itemStack) != null) {
        event.toolTip.add(TextFormatting.DARK_GRAY + material.getLocalizedName());
      }
    }
  }
}

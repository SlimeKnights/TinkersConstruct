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
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.model.BakedSimple;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.CustomTextureCreator;
import slimeknights.tconstruct.library.client.model.ModelHelper;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.Pattern;
import slimeknights.tconstruct.shared.client.BakedTableModel;
import slimeknights.tconstruct.tools.client.BakedChestModel;
import slimeknights.tconstruct.tools.common.block.BlockToolTable;

@SideOnly(Side.CLIENT)
public class ToolClientEvents {

  public static Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());

  // tool tables
  private static final String LOCATION_ToolTable = Util.resource("tooltables");
  private static final String LOCATION_ToolForge = Util.resource("toolforge");

  // the actual locations where the models are located
  public static final ModelResourceLocation locCraftingStation = getTableLoc(BlockToolTable.TableTypes.CraftingStation);
  public static final ModelResourceLocation locStencilTable = getTableLoc(BlockToolTable.TableTypes.StencilTable);
  public static final ModelResourceLocation locPartBuilder = getTableLoc(BlockToolTable.TableTypes.PartBuilder);
  public static final ModelResourceLocation locToolStation = getTableLoc(BlockToolTable.TableTypes.ToolStation);
  public static final ModelResourceLocation locToolForge = new ModelResourceLocation(LOCATION_ToolForge, "normal");

  public static final ModelResourceLocation locPatternChest = getTableLoc(BlockToolTable.TableTypes.PatternChest);
  public static final ModelResourceLocation locPartChest = getTableLoc(BlockToolTable.TableTypes.PartChest);

  // Blank Pattern
  private static final ResourceLocation MODEL_BlankPattern = Util.getResource("item/pattern");
  public static final ResourceLocation locBlankPattern = Util.getResource("pattern");

  private static ModelResourceLocation getTableLoc(BlockToolTable.TableTypes type) {
    return new ModelResourceLocation(LOCATION_ToolTable, String.format("%s=%s",
                                                                       BlockToolTable.TABLES.getName(),
                                                                       BlockToolTable.TABLES.getName(type)));
  }

  @SubscribeEvent
  public void onModelBake(ModelBakeEvent event) {
    // add the models for the pattern variants
    replacePatternModel(locBlankPattern, MODEL_BlankPattern, event, CustomTextureCreator.patternLocString, TinkerRegistry.getPatternItems());

    // replace the baked table models with smart variants

    // tool tables
    replaceTableModel(locCraftingStation, event);
    replaceTableModel(locStencilTable, event);
    replaceTableModel(locPartBuilder, event);
    replaceTableModel(locToolStation, event); // tool station has no variants but we want the item support
    replaceTableModel(locToolForge, event);
    replaceChestModel(locPatternChest, event); // pattern and part chest have no variants or items but we want rotation
    replaceChestModel(locPartChest, event);

    // silence the missing-model message for the default itemblock
    event.getModelRegistry().putObject(new ModelResourceLocation(LOCATION_ToolTable, "inventory"), event.getModelRegistry().getObject(locToolStation));
    event.getModelRegistry().putObject(new ModelResourceLocation(LOCATION_ToolForge, "inventory"), event.getModelRegistry().getObject(locToolForge));
  }

  public static void replaceTableModel(ModelResourceLocation location, ModelBakeEvent event) {
    try {
      IModel model = ModelLoaderRegistry.getModel(location);
      IBakedModel standard = event.getModelRegistry().getObject(location);
      IBakedModel finalModel = new BakedTableModel(standard, model, DefaultVertexFormats.BLOCK);
      event.getModelRegistry().putObject(location, finalModel);
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  public static void replaceChestModel(ModelResourceLocation location, ModelBakeEvent event) {
    try {
      IBakedModel original = event.getModelRegistry().getObject(location);
      IBakedModel finalModel = new BakedChestModel(original);
      event.getModelRegistry().putObject(location, finalModel);
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
      if(model instanceof IModel) {
        for(Item item : items) {
          String suffix = Pattern.getTextureIdentifier(item);
          // get texture
          String partPatternLocation = locPattern.toString() + suffix;
          String partPatternTexture = baseString + suffix;
          IModel partPatternModel = model.retexture(ImmutableMap.of("layer0", partPatternTexture));
          IBakedModel baked = partPatternModel.bake(partPatternModel.getDefaultState(), DefaultVertexFormats.ITEM, textureGetter);
          if(color > -1) {
            ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();
            // ItemLayerModel.BakedModel only uses general quads
            for(BakedQuad quad : baked.getQuads(null, null, 0)) {
              quads.add(ModelHelper.colorQuad(color, quad));
            }
            baked = new BakedSimple.Wrapper(quads.build(), (baked));
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
      if(material.matches(event.getItemStack()).isPresent()) {
        event.getToolTip().add(TextFormatting.DARK_GRAY + material.getLocalizedName());
      }
    }
  }
}

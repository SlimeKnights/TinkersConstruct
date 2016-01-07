package slimeknights.tconstruct.shared.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.MultiModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import slimeknights.mantle.client.model.BlockItemModelWrapper;
import slimeknights.mantle.client.model.TRSRBakedModel;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.ModelHelper;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class BakedTableModel implements ISmartBlockModel, ISmartItemModel, IFlexibleBakedModel {

  private final IFlexibleBakedModel standard;
  private final IRetexturableModel tableModel;

  private final Map<String, IFlexibleBakedModel> cache = Maps.newHashMap();
  private final Function<ResourceLocation, TextureAtlasSprite> textureGetter;

  public BakedTableModel(IFlexibleBakedModel standard, IRetexturableModel tableModel) {
    this.standard = standard;
    this.tableModel = tableModel;

    textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
      public TextureAtlasSprite apply(ResourceLocation location) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
      }
    };
  }

  @Override
  public IBakedModel handleBlockState(IBlockState state) {
    // get texture from state
    String texture = null;
    List<PropertyTableItem.TableItem> items = Collections.emptyList();
    float rotation = 0;

    if(state instanceof IExtendedBlockState) {
      IExtendedBlockState extendedState = (IExtendedBlockState) state;
      if(extendedState.getUnlistedNames().contains(BlockTable.TEXTURE)) {
        texture = extendedState.getValue(BlockTable.TEXTURE);
      }
      if(Config.renderTableItems && extendedState.getUnlistedNames().contains(BlockTable.INVENTORY)) {
        if(extendedState.getValue(BlockTable.INVENTORY) != null) {
          items = extendedState.getValue(BlockTable.INVENTORY).items;
        }
      }

      if(extendedState.getUnlistedNames().contains(BlockTable.FACING)) {
        EnumFacing face = extendedState.getValue((IUnlistedProperty<EnumFacing>) BlockTable.FACING);
        if(face != null) {
          rotation = 360 - face.getOpposite().getHorizontalIndex() * 90f;
        }
        else {
          rotation = 360;
        }
      }
    }

    // models are symmetric, no need to rotate if there's nothing on it where rotation matters, so we just use default
    if(texture == null && items == null) {
      return standard;
    }



    return getActualModel(texture, items, rotation);
  }


  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    ItemStack blockStack = ItemStack.loadItemStackFromNBT(TagUtil.getTagSafe(stack).getCompoundTag(TileTable.FEET_TAG));
    if(blockStack == null) {
      return standard;
    }

    Block block = Block.getBlockFromItem(blockStack.getItem());

    String texture = ModelHelper.getTextureFromBlock(block, blockStack.getItemDamage()).getIconName();

    return getActualModel(texture, Collections.<PropertyTableItem.TableItem>emptyList(), 0);
  }

  protected IFlexibleBakedModel getActualModel(String texture, List<PropertyTableItem.TableItem> items, float rotation) {
    if(cache.containsKey(texture)) {
      //return cache.get(texture);
    }

    IFlexibleBakedModel bakedModel = standard;

    if(texture != null && tableModel != null) {
      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      builder.put("bottom", texture);
      builder.put("leg", texture);
      builder.put("legBottom", texture);
      IModel retexturedModel = tableModel.retexture(builder.build());


      bakedModel = retexturedModel.bake(retexturedModel.getDefaultState(), standard.getFormat(), textureGetter);
    }

    // add all the items to display on the table
    if(items != null && !items.isEmpty()) {
      ImmutableMap.Builder<String, IFlexibleBakedModel> pb = ImmutableMap.builder();
      int i = 0;
      for(PropertyTableItem.TableItem item : items) {
        pb.put(String.valueOf(i++), new TRSRBakedModel(item.model, item.x, item.y + 1f, item.z, item.r, (float) (Math.PI), 0, item.s));
      }
      bakedModel = new MultiModel.Baked(bakedModel, pb.build());
    }

    if(rotation < 360)
      bakedModel = new TRSRBakedModel(bakedModel, 0, 0, 0, 0, (float)Math.PI * (rotation/180f), 0, 1);
    bakedModel = new BlockItemModelWrapper(bakedModel);

    cache.put(texture, bakedModel);

    return bakedModel;
  }

  @Override
  public List<BakedQuad> getFaceQuads(EnumFacing p_177551_1_) {
    return standard.getFaceQuads(p_177551_1_);
  }

  @Override
  public List<BakedQuad> getGeneralQuads() {
    return standard.getGeneralQuads();
  }

  @Override
  public VertexFormat getFormat() {
    return standard.getFormat();
  }

  @Override
  public boolean isAmbientOcclusion() {
    return standard.isAmbientOcclusion();
  }

  @Override
  public boolean isGui3d() {
    return standard.isGui3d();
  }

  @Override
  public boolean isBuiltInRenderer() {
    return standard.isBuiltInRenderer();
  }

  @Override
  public TextureAtlasSprite getParticleTexture() {
    return standard.getParticleTexture();
  }

  @Override
  public ItemCameraTransforms getItemCameraTransforms() {
    return standard.getItemCameraTransforms();
  }
}

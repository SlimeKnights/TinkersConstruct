package slimeknights.tconstruct.shared.client;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import slimeknights.mantle.client.model.BakedCompositeModel;
import slimeknights.mantle.client.model.TRSRBakedModel;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.model.ModelHelper;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class BakedTableModel implements IPerspectiveAwareModel {

  private final IPerspectiveAwareModel standard;
  private final IRetexturableModel tableModel;

  private final Map<String, IBakedModel> cache = Maps.newHashMap();
  private final Function<ResourceLocation, TextureAtlasSprite> textureGetter;
  private final VertexFormat format;
  private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

  public BakedTableModel(IPerspectiveAwareModel standard, IRetexturableModel tableModel, VertexFormat format) {
    this.standard = standard;
    this.tableModel = tableModel;

    this.textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
      @Override
      public TextureAtlasSprite apply(ResourceLocation location) {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
      }
    };
    this.format = format;
    this.transforms = ModelHelper.getTransforms(standard);
  }

  protected IBakedModel getActualModel(String texture, List<PropertyTableItem.TableItem> items, EnumFacing facing) {
    IBakedModel bakedModel = standard;

    if(texture != null) {
      if(cache.containsKey(texture)) {
        bakedModel = cache.get(texture);
      }
      else if(tableModel != null) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.put("bottom", texture);
        builder.put("leg", texture);
        builder.put("legBottom", texture);
        IModel retexturedModel = tableModel.retexture(builder.build());
        IModelState modelState = new SimpleModelState(transforms);

        bakedModel = retexturedModel.bake(modelState, format, textureGetter);
        cache.put(texture, bakedModel);
      }
    }

    // add all the items to display on the table
    if(items != null && !items.isEmpty()) {
      ImmutableList.Builder<IBakedModel> pb = ImmutableList.builder();
      for(PropertyTableItem.TableItem item : items) {
        pb.add(new TRSRBakedModel(item.model, item.x, item.y + 1f, item.z, item.r, (float) (Math.PI), 0, item.s));
      }

      bakedModel = new BakedCompositeModel(bakedModel, pb.build());
    }

    if(facing != null) {
      bakedModel = new TRSRBakedModel(bakedModel, facing);
    }

    return bakedModel;
  }

  @Override
  public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
    // get texture from state
    String texture = null;
    List<PropertyTableItem.TableItem> items = Collections.emptyList();
    EnumFacing face = EnumFacing.SOUTH;

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
        face = extendedState.getValue((IUnlistedProperty<EnumFacing>) BlockTable.FACING);
      }
    }

    // models are symmetric, no need to rotate if there's nothing on it where rotation matters, so we just use default
    if(texture == null && items == null) {
      return standard.getQuads(state, side, rand);
    }

    // the model returned by getActualModel should be a simple model with no special handling
    return getActualModel(texture, items, face).getQuads(state, side, rand);
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

  @Override
  public ItemOverrideList getOverrides() {
    return TableItemOverrideList.INSTANCE;
  }

  @Override
  public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
    Pair<? extends IBakedModel, Matrix4f> pair = standard.handlePerspective(cameraTransformType);
    return Pair.of(this, pair.getRight());
  }

  private static class TableItemOverrideList extends ItemOverrideList {

    static TableItemOverrideList INSTANCE = new TableItemOverrideList();

    private TableItemOverrideList() {
      super(ImmutableList.<ItemOverride>of());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
      if(originalModel instanceof BakedTableModel) {
        // read out the data on the itemstack
        ItemStack blockStack = ItemStack
            .loadItemStackFromNBT(TagUtil.getTagSafe(stack).getCompoundTag(TileTable.FEET_TAG));
        if(blockStack != null) {
          // get model from data
          Block block = Block.getBlockFromItem(blockStack.getItem());
          String texture = ModelHelper.getTextureFromBlock(block, blockStack.getItemDamage()).getIconName();
          return ((BakedTableModel) originalModel)
              .getActualModel(texture, Collections.<PropertyTableItem.TableItem>emptyList(), null);
        }
      }

      return originalModel;
    }
  }
}

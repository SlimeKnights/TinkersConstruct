package slimeknights.tconstruct.shared.client;

import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4f;

import slimeknights.mantle.client.model.BakedCompositeModel;
import slimeknights.mantle.client.model.TRSRBakedModel;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.model.ModelHelper;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.block.BlockTable;
import slimeknights.tconstruct.shared.block.PropertyTableItem;
import slimeknights.tconstruct.shared.tileentity.TileTable;

public class BakedTableModel extends BakedModelWrapper<IBakedModel> {

  static final Logger log = Util.getLogger("Table Model");

  private final IModel tableModel;

  private final Map<String, IBakedModel> cache = Maps.newHashMap();
  private static final Function<ResourceLocation, TextureAtlasSprite> textureGetter = location -> {
    assert location != null;
    return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
  };
  private final VertexFormat format;
  private final LoadingCache<PropertyTableItem.TableItem, IBakedModel> tableItemCache = CacheBuilder
      .newBuilder()
      .maximumSize(250)
      .build(new CacheLoader<PropertyTableItem.TableItem, IBakedModel>() {
        @Override
        public IBakedModel load(PropertyTableItem.TableItem key) throws Exception {
          return BakedTableModel.this.getModelForTableItem(key);
        }
      });

  private final Cache<TableItemCombinationCacheKey, IBakedModel> tableItemCombinedCache = CacheBuilder
      .newBuilder()
      .maximumSize(20)
      .build();

  public BakedTableModel(IBakedModel standard, IModel tableModel, VertexFormat format) {
    super(standard);
    this.tableModel = tableModel;
    this.format = format;
  }

  protected IBakedModel getActualModel(String texture, List<PropertyTableItem.TableItem> items, EnumFacing facing) {
    IBakedModel bakedModel = originalModel;

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
        IModelState modelState = retexturedModel.getDefaultState();

        bakedModel = retexturedModel.bake(modelState, format, textureGetter);
        cache.put(texture, bakedModel);
      }
    }

    final IBakedModel parentModel = bakedModel;
    try {
      bakedModel = tableItemCombinedCache.get(new TableItemCombinationCacheKey(items, bakedModel, facing), () -> getCombinedBakedModel(items, facing, parentModel));
    }
    catch(ExecutionException e) {
      log.error(e);
    }

    return bakedModel;
  }

  private IBakedModel getCombinedBakedModel(List<PropertyTableItem.TableItem> items, EnumFacing facing, IBakedModel parentModel) {
    IBakedModel out = parentModel;
    // add all the items to display on the table
    if(items != null && !items.isEmpty()) {
      BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
      // use a null render layer while grabbing items so they give us all layers
      // primarily affects chisel CTM rendering
      if(Config.renderInventoryNullLayer) {
        ForgeHooksClient.setRenderLayer(null);
      }
      BakedCompositeModel.Builder builder = new BakedCompositeModel.Builder();
      builder.add(parentModel, null, 0);
      for(PropertyTableItem.TableItem item : items) {
        try {
          builder.add(tableItemCache.get(item), null, 0);
        }
        catch(ExecutionException e) {
          log.error(e);
        }
      }

      out = builder.build(parentModel);
      // restore the original layer
      if(Config.renderInventoryNullLayer) {
        ForgeHooksClient.setRenderLayer(layer);
      }
    }

    if(facing != null) {
      out = new TRSRBakedModel(out, facing);
    }
    return out;
  }

  private IBakedModel getModelForTableItem(PropertyTableItem.TableItem item) {
    return new TRSRBakedModel(item.model, item.x, item.y + 1f, item.z, item.r, (float) (Math.PI), 0, item.s);
  }

  @Nonnull
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

      // remove all world specific data
      // This is so that the next call to getQuads from the transformed TRSRModel doesn't do this again
      // otherwise table models inside table model items recursively calls this with the state of the original table
      state = extendedState.withProperty(BlockTable.INVENTORY, PropertyTableItem.TableItems.EMPTY).withProperty((IUnlistedProperty<EnumFacing>) BlockTable.FACING, null);
    }

    // models are symmetric, no need to rotate if there's nothing on it where rotation matters, so we just use default
    if(texture == null && items == null) {
      return originalModel.getQuads(state, side, rand);
    }

    // the model returned by getActualModel should be a simple model with no special handling
    return getActualModel(texture, items, face).getQuads(state, side, rand);
  }

  @Nonnull
  @Override
  public ItemOverrideList getOverrides() {
    return TableItemOverrideList.INSTANCE;
  }

  @Override
  public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
    Pair<? extends IBakedModel, Matrix4f> pair = originalModel.handlePerspective(cameraTransformType);
    return Pair.of(this, pair.getRight());
  }

  private static class TableItemOverrideList extends ItemOverrideList {

    static TableItemOverrideList INSTANCE = new TableItemOverrideList();

    private TableItemOverrideList() {
      super(ImmutableList.of());
    }

    @Nonnull
    @Override
    public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
      if(originalModel instanceof BakedTableModel) {
        // read out the data on the itemstack
        ItemStack blockStack = new ItemStack(TagUtil.getTagSafe(stack).getCompoundTag(TileTable.FEET_TAG));
        if(!blockStack.isEmpty()) {
          // get model from data
          Block block = Block.getBlockFromItem(blockStack.getItem());
          String texture = ModelHelper.getTextureFromBlock(block, blockStack.getItemDamage()).getIconName();
          return ((BakedTableModel) originalModel)
              .getActualModel(texture, Collections.emptyList(), null);
        }
      }

      return originalModel;
    }
  }

  private static class TableItemCombinationCacheKey {
    private final List<PropertyTableItem.TableItem> tableItems;
    private final IBakedModel bakedBaseModel;
    private final EnumFacing facing;

    public TableItemCombinationCacheKey(List<PropertyTableItem.TableItem> tableItems, IBakedModel bakedBaseModel, EnumFacing facing) {
      this.tableItems = tableItems;
      this.bakedBaseModel = bakedBaseModel;
      this.facing = facing;
    }

    @Override
    public boolean equals(Object o) {
      if(this == o) {
        return true;
      }
      if(o == null || getClass() != o.getClass()) {
        return false;
      }

      TableItemCombinationCacheKey that = (TableItemCombinationCacheKey) o;

      if(tableItems != null ? !tableItems.equals(that.tableItems) : that.tableItems != null) {
        return false;
      }
      if(bakedBaseModel != null ? !bakedBaseModel.equals(that.bakedBaseModel) : that.bakedBaseModel != null) {
        return false;
      }
      return facing == that.facing;
    }

    @Override
    public int hashCode() {
      int result = tableItems != null ? tableItems.hashCode() : 0;
      result = 31 * result + (bakedBaseModel != null ? bakedBaseModel.hashCode() : 0);
      result = 31 * result + (facing != null ? facing.hashCode() : 0);
      return result;
    }
  }
}

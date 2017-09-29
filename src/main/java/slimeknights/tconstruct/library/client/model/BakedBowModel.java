package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Map;

import javax.annotation.Nonnull;

import slimeknights.mantle.client.model.TRSRBakedModel;
import slimeknights.tconstruct.library.client.model.format.AmmoPosition;
import slimeknights.tconstruct.library.tools.IAmmoUser;
import slimeknights.tconstruct.library.utils.TagUtil;

public class BakedBowModel extends BakedToolModel {

  protected final AmmoPosition ammoPosition;

  public BakedBowModel(IBakedModel parent,
                       BakedMaterialModel[] parts,
                       BakedMaterialModel[] brokenParts,
                       Map<String, IBakedModel> modifierParts,
                       ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transform,
                       ImmutableList<BakedToolModelOverride> overrides,
                       AmmoPosition ammoPosition) {
    super(parent, parts, brokenParts, modifierParts, transform, overrides);
    this.ammoPosition = ammoPosition;
  }

  @Nonnull
  @Override
  public ItemOverrideList getOverrides() {
    return BowItemOverrideList.INSTANCE;
  }

  protected static class BowItemOverrideList extends ToolItemOverrideList {

    static BowItemOverrideList INSTANCE = new BowItemOverrideList();

    @Override
    protected CacheKey getCacheKey(ItemStack stack, BakedToolModel original, World world, EntityLivingBase entityLivingBase) {
      CacheKey key = super.getCacheKey(stack, original, world, entityLivingBase);

      if(original instanceof BakedBowModel && stack.getItem() instanceof IAmmoUser) {
        ItemStack ammo = ((IAmmoUser) stack.getItem()).getAmmoToRender(stack, entityLivingBase);
        if(!ammo.isEmpty()) {
          key = new CacheKeyAmmo(original, stack, ammo);
        }
      }

      return key;
    }

    @Override
    protected void addExtraQuads(ItemStack stack, BakedToolModel original, ImmutableList.Builder<BakedQuad> quads, World world, EntityLivingBase entityLivingBase) {
      if(original instanceof BakedBowModel && stack.getItem() instanceof IAmmoUser) {
        ItemStack ammo = ((IAmmoUser) stack.getItem()).getAmmoToRender(stack, entityLivingBase);
        if(!ammo.isEmpty()) {
          AmmoPosition pos = ((BakedBowModel) original).ammoPosition;
          // ammo found, render it
          IBakedModel ammoModel = ModelHelper.getBakedModelForItem(ammo, world, entityLivingBase);
          ammoModel = new TRSRBakedModel(ammoModel,
                                         pos.pos[0], pos.pos[1], pos.pos[2],
                                         (pos.rot[0]/180f)*(float)Math.PI, (pos.rot[1]/180f)*(float)Math.PI, (pos.rot[2]/180f)*(float)Math.PI,
                                         1f);
          quads.addAll(ammoModel.getQuads(null, null, 0));
        }
      }
    }
  }

  protected static class CacheKeyAmmo extends CacheKey {

    final Item ammoItem;
    final int ammoMeta;
    final NBTTagCompound ammoData;

    private CacheKeyAmmo(IBakedModel parent, ItemStack stack, ItemStack ammo) {
      super(parent, stack);
      ammoItem = ammo.getItem();
      ammoMeta = ammo.getMetadata();
      ammoData = TagUtil.getTagSafe(ammo);
    }

    @Override
    public boolean equals(Object o) {
      if(this == o) {
        return true;
      }
      if(o == null || getClass() != o.getClass()) {
        return false;
      }
      if(!super.equals(o)) {
        return false;
      }

      CacheKeyAmmo that = (CacheKeyAmmo) o;
      // if we have an item
      if(ammoItem != null) {
        // it must be equal and have equal meta
        if(!ammoItem.equals(that.ammoItem) || ammoMeta != that.ammoMeta) {
          return false;
        }
      } else {
        // if no item, both must have no item
        if(that.ammoItem != null) {
          return false;
        }
      }
      return ammoData != null ? ammoData.equals(that.ammoData) : that.ammoData == null;

    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + (ammoItem != null ? ammoItem.hashCode() : 0);
      result = 31 * result + (ammoData != null ? ammoData.hashCode() : 0);
      result = 31 * result + ammoMeta;
      return result;
    }
  }
}

package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import slimeknights.mantle.client.model.BakedSimple;
import slimeknights.mantle.client.model.BakedWrapper;
import slimeknights.mantle.util.ImmutableConcatList;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class BakedToolModel extends BakedWrapper.Perspective {

  protected BakedMaterialModel[] parts;
  protected BakedMaterialModel[] brokenParts;
  protected Map<String, IBakedModel> modifierParts;
  protected final ImmutableMap<TransformType, TRSRTransformation> transforms;
  protected final ImmutableMap<TransformType, TRSRTransformation> blockingTransforms;

  /**
   * The length of brokenParts has to match the length of parts. If a part does not have a broken texture, the entry in
   * the array simply is null.
   */
  public BakedToolModel(IBakedModel parent, BakedMaterialModel[] parts, BakedMaterialModel[] brokenParts,
                        Map<String, IBakedModel> modifierParts,
                        ImmutableMap<TransformType, TRSRTransformation> transform,
                        ImmutableMap<TransformType, TRSRTransformation> blockingTransform) {
    super(parent, transform);
    if(parts.length != brokenParts.length) {
      throw new RuntimeException("TinkerModel: Length of Parts and BrokenParts Array has to match");
    }

    this.parts = parts;
    this.brokenParts = brokenParts;
    this.modifierParts = modifierParts;
    this.transforms = transform;
    this.blockingTransforms = blockingTransform;
  }

  @Override
  public ItemOverrideList getOverrides() {
    return ToolItemOverrideList.INSTANCE;
  }

  private static class ToolItemOverrideList extends ItemOverrideList {

    static ToolItemOverrideList INSTANCE = new ToolItemOverrideList();

    private ToolItemOverrideList() {
      super(ImmutableList.<ItemOverride>of());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
      NBTTagCompound baseTag = TagUtil.getBaseTag(stack);
      if(!baseTag.hasNoTags()) {
        BakedToolModel original = (BakedToolModel)originalModel;
        BakedMaterialModel parts[] = original.parts;
        BakedMaterialModel brokenParts[] = original.brokenParts;
        Map<String, IBakedModel> modifierParts = original.modifierParts;

        NBTTagCompound toolTag = TagUtil.getToolTag(stack);
        NBTTagList materials = TagUtil.getBaseMaterialsTagList(stack);
        NBTTagList modifiers = TagUtil.getBaseModifiersTagList(stack);

        // get the texture for each part
        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

        boolean broken = toolTag.getBoolean(Tags.BROKEN);

        // the model for the part of the given material. Broken or not-broken
        for(int i = 0; i < parts.length; i++) {
          String id = materials.getStringTagAt(i);

          IBakedModel partModel;
          if(broken && brokenParts[i] != null) {
            partModel = brokenParts[i].getModelByIdentifier(id);
          }
          else {
            partModel = parts[i].getModelByIdentifier(id);
          }

          quads.addAll(partModel.getQuads(null, null, 0));
        }

        // modifiers
        for(int i = 0; i < modifiers.tagCount(); i++) {
          String modId = modifiers.getStringTagAt(i);
          IBakedModel modModel = modifierParts.get(modId);
          if(modModel != null) {
            quads.addAll(modModel.getQuads(null, null, 0));
          }
        }

        ImmutableMap<TransformType, TRSRTransformation> transform = original.transforms;
        if(entity != null && entity.isHandActive() && entity.getActiveItemStack() == stack) {
          transform = original.blockingTransforms;
        }

        return new BakedSimple(quads.build(), transform, original);
      }
      return originalModel;
    }
  }
}

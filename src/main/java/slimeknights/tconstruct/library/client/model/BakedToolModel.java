package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Map;

import javax.annotation.Nonnull;

import slimeknights.mantle.client.model.BakedSimple;
import slimeknights.mantle.client.model.BakedWrapper;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class BakedToolModel extends BakedWrapper.Perspective {

  protected BakedMaterialModel[] parts;
  protected BakedMaterialModel[] brokenParts;
  protected Map<String, IBakedModel> modifierParts;
  protected final ImmutableMap<TransformType, TRSRTransformation> transforms;
  protected final ImmutableList<BakedToolModelOverride> overrides;

  /**
   * The length of brokenParts has to match the length of parts. If a part does not have a broken texture, the entry in
   * the array simply is null.
   */
  public BakedToolModel(IBakedModel parent,
                        BakedMaterialModel[] parts,
                        BakedMaterialModel[] brokenParts,
                        Map<String, IBakedModel> modifierParts,
                        ImmutableMap<TransformType, TRSRTransformation> transform,
                        ImmutableList<BakedToolModelOverride> overrides) {
    super(parent, transform);
    if(parts.length != brokenParts.length) {
      throw new RuntimeException("TinkerModel: Length of Parts and BrokenParts Array has to match");
    }

    this.parts = parts;
    this.brokenParts = brokenParts;
    this.modifierParts = modifierParts;
    this.transforms = transform;
    this.overrides = overrides;
  }

  @Nonnull
  @Override
  public ItemOverrideList getOverrides() {
    return ToolItemOverrideList.INSTANCE;
  }

  protected static class ToolItemOverrideList extends ItemOverrideList {

    static ToolItemOverrideList INSTANCE = new ToolItemOverrideList();

    protected ToolItemOverrideList() {
      super(ImmutableList.<ItemOverride>of());
    }

    @Nonnull
    @Override
    public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity) {
      NBTTagCompound baseTag = TagUtil.getBaseTag(stack);
      if(!baseTag.hasNoTags()) {
        BakedToolModel original = getBaseModel((BakedToolModel) originalModel, stack, world, entity);

        // get the texture for each part
        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

        addPartQuads(stack, original, quads);
        addModifierQuads(stack, original, quads);
        addExtraQuads(stack, original, quads, world, entity);

        return new BakedSimple(quads.build(), original.transforms, original);
      }
      return originalModel;
    }

    private BakedToolModel getBaseModel(@Nonnull BakedToolModel originalModel, ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity) {
      BakedToolModel original = originalModel;

      // check for an override
      for(BakedToolModelOverride override : original.overrides) {
        if(override.matches(stack, world, entity)) {
          original = override.bakedToolModel;
        }
      }
      return original;
    }

    private void addPartQuads(ItemStack stack, BakedToolModel original, ImmutableList.Builder<BakedQuad> quads) {
      NBTTagList materials = TagUtil.getBaseMaterialsTagList(stack);
      boolean broken = ToolHelper.isBroken(stack);

      BakedMaterialModel parts[] = original.parts;
      BakedMaterialModel brokenParts[] = original.brokenParts;

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
    }

    private void addModifierQuads(ItemStack stack, BakedToolModel original, ImmutableList.Builder<BakedQuad> quads) {
      NBTTagList modifiers = TagUtil.getBaseModifiersTagList(stack);
      Map<String, IBakedModel> modifierParts = original.modifierParts;
      for(int i = 0; i < modifiers.tagCount(); i++) {
        String modId = modifiers.getStringTagAt(i);
        IBakedModel modModel = modifierParts.get(modId);
        if(modModel != null) {
          quads.addAll(modModel.getQuads(null, null, 0));
        }
      }
    }


    protected void addExtraQuads(ItemStack stack, BakedToolModel original, ImmutableList.Builder<BakedQuad> quads, World world, EntityLivingBase entity) {
      // for custom stuff
    }
  }
}

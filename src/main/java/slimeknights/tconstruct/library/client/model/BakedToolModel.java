package slimeknights.tconstruct.library.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartItemModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.TRSRTransformation;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;

import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.tools.TinkerMaterials;

public class BakedToolModel extends ItemLayerModel.BakedModel implements ISmartItemModel {

  protected BakedMaterialModel[] parts;
  protected BakedMaterialModel[] brokenParts;
  protected Map<String, IFlexibleBakedModel> modifierParts;
  protected final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;

  /**
   * The length of brokenParts has to match the length of parts. If a part does not have a broken texture, the entry in
   * the array simply is null.
   */
  public BakedToolModel(IFlexibleBakedModel parent, BakedMaterialModel[] parts, BakedMaterialModel[] brokenParts,
                        Map<String, IFlexibleBakedModel> modifierParts, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transform) {
    super((ImmutableList<BakedQuad>) parent.getGeneralQuads(), parent.getTexture(), parent.getFormat(), transform);

    if(parts.length != brokenParts.length) {
      throw new RuntimeException("TinkerModel: Length of Parts and BrokenParts Array has to match");
    }

    this.parts = parts;
    this.brokenParts = brokenParts;
    this.modifierParts = modifierParts;
    this.transforms = transform;
  }

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    NBTTagCompound baseTag = TagUtil.getBaseTag(stack);
    NBTTagCompound toolTag = TagUtil.getToolTag(stack);

    if(baseTag.hasNoTags()) {
      return this;
    }

    NBTTagList materials = TagUtil.getBaseMaterialsTagList(stack);
    NBTTagList modifiers = TagUtil.getBaseModifiersTagList(stack);

    // get the texture for each part
    ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

    boolean broken = toolTag.getBoolean(Tags.BROKEN);

    for(int i = 0; i < parts.length; i++) {
      String id = materials.getStringTagAt(i);

      IBakedModel partModel;
      if(broken && brokenParts[i] != null) {
        partModel = brokenParts[i].getModelByIdentifier(id);
      }
      else {
        partModel = parts[i].getModelByIdentifier(id);
      }

      quads.addAll(partModel.getGeneralQuads()); // todo: use an efficient collection for this. Preferably a List-List
    }

    for(int i = 0; i < modifiers.tagCount(); i++) {
      String modId = modifiers.getStringTagAt(i);
      IFlexibleBakedModel modModel = modifierParts.get(modId);
      if(modModel != null) {
        if(modModel instanceof BakedMaterialModel) {
          modModel = ((BakedMaterialModel) modModel).getModelByIdentifier(TinkerMaterials.netherrack.identifier);
        }
        quads.addAll(modModel.getGeneralQuads());
      }
    }

    IFlexibleBakedModel model = new ItemLayerModel.BakedModel(quads.build(), this.getTexture(), this.getFormat(), transforms);

    return model;
  }

  private static final List<List<BakedQuad>> empty_face_quads;
  private static final List<BakedQuad> empty_list;

  static {
    empty_list = Collections.emptyList();
    empty_face_quads = Lists.newArrayList();
    for(int i = 0; i < 6; i++) {
      empty_face_quads.add(empty_list);
    }
  }

  @Override
  public Pair<IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
    return null;
  }
}

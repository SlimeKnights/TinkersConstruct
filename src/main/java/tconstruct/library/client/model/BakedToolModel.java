package tconstruct.library.client.model;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.ISmartItemModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;
import tconstruct.tools.TinkerMaterials;
import tconstruct.tools.TinkerTools;

public class BakedToolModel extends IFlexibleBakedModel.Wrapper implements ISmartItemModel {

  protected BakedMaterialModel[] parts;
  protected BakedMaterialModel[] brokenParts;
  protected Map<String, IFlexibleBakedModel> modifiers;

  /**
   * The length of brokenParts has to match the length of parts. If a part does not have a broken texture, the entry in
   * the array simply is null.
   */
  public BakedToolModel(IBakedModel parent, BakedMaterialModel[] parts, BakedMaterialModel[] brokenParts,
                        Map<String, IFlexibleBakedModel> modifiers) {
    super(parent, Attributes.DEFAULT_BAKED_FORMAT);

    if (parts.length != brokenParts.length) {
      throw new RuntimeException("TinkerModel: Length of Parts and BrokenParts Array has to match");
    }

    this.parts = parts;
    this.brokenParts = brokenParts;
    this.modifiers = modifiers;
  }

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    NBTTagCompound tag = TagUtil.getBaseTag(stack);

    if (tag == null) {
      return this;
    }

    // get the texture for each part
    List<BakedQuad> quads = new ArrayList<>();

    boolean broken = tag.getBoolean(Tags.BROKEN);

    for (int i = 0; i < parts.length; i++) {
      String id = tag.getString(String.valueOf(i));
      int meta = TinkerRegistry.getMaterial(id).metadata;
      IBakedModel partModel;
      if (broken && brokenParts[i] != null) {
        partModel = brokenParts[i].getModelByMetadata(meta);
      } else {
        partModel = parts[i].getModelByMetadata(meta);
      }

      quads.addAll(partModel.getGeneralQuads()); // todo: use an efficient collection for this. Preferably a List-List
    }

    IFlexibleBakedModel modifier = modifiers.get(TinkerTools.fortifyMod.getIdentifier());
    if (modifier != null) {
      if (modifier instanceof BakedMaterialModel) {
        modifier = ((BakedMaterialModel) modifier).getModelByMetadata(1);
      }
      quads.addAll(modifier.getGeneralQuads());
    }

    SimpleBakedModel
        model =
        new SimpleBakedModel(quads, empty_face_quads, this.isAmbientOcclusion(), this.isGui3d(), this.getTexture(),
                             this.getItemCameraTransforms());
    return model;
  }

  private static final List<List<BakedQuad>> empty_face_quads;
  private static final List<BakedQuad> empty_list;

  static {
    empty_list = Collections.emptyList();
    empty_face_quads = Lists.newArrayList();
    for (int i = 0; i < 6; i++) {
      empty_face_quads.add(empty_list);
    }
  }
}

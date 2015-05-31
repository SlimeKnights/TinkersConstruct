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

import tconstruct.library.TinkerRegistry;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;
import tconstruct.tools.TinkerMaterials;

public class BakedToolModel extends IFlexibleBakedModel.Wrapper implements ISmartItemModel {

  protected BakedMaterialModel[] parts;
  protected BakedMaterialModel[] brokenParts;

  /**
   * The length of brokenParts has to match the length of parts. If a part does not have a broken texture, the entry in
   * the array simply is null.
   */
  public BakedToolModel(IBakedModel parent, BakedMaterialModel[] parts, BakedMaterialModel[] brokenParts) {
    super(parent, Attributes.DEFAULT_BAKED_FORMAT);

    if (parts.length != brokenParts.length) {
      throw new RuntimeException("TinkerModel: Length of Parts and BrokenParts Array has to match");
    }

    this.parts = parts;
    this.brokenParts = brokenParts;
  }

  @Override
  public IBakedModel handleItemState(ItemStack stack) {
    NBTTagCompound tag = TagUtil.getToolTag(stack);
/*
    if(tag == null || !tag.hasKey(Tags.TINKER_DATA))
      return this;

    tag = tag.getCompoundTag(Tags.TINKER_DATA);
    */

    tag = new NBTTagCompound();
    tag.setString("0", TinkerMaterials.netherrack.identifier);
    tag.setString("1", TinkerMaterials.wood.identifier);
    tag.setString("2", TinkerMaterials.stone.identifier);
    tag.setBoolean("Broken", false);

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

package slimeknights.tconstruct.library.client.modifiers.block.model;

import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.ClientLoadables;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.function.Function;

import com.mojang.math.Transformation;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel.Builder;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;

/** Modifier model that requires transforms */
public record TransformationBlockModifierModel(int color, int luminosity, Transformation transform, BlockModifierModel model) implements BlockModifierModel {
  public static final RecordLoadable<TransformationBlockModifierModel> LOADER = RecordLoadable.create(
      ColorLoadable.ALPHA.defaultField("color", false, TransformationBlockModifierModel::color),
      IntLoadable.range(0, 15).defaultField("luminosity", 0, false, TransformationBlockModifierModel::luminosity),
      ClientLoadables.TRANSFORMATION.defaultField("transform", Transformation.identity(), TransformationBlockModifierModel::transform),
      BlockModifierModelLoadable.DEFAULT.requiredField("model", m -> m.model),
      TransformationBlockModifierModel::new);

  @Override
  public void addParts(IToolStackView tool, ModifierEntry modifier, ModifierBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, IQuadTransformer transforms, Builder builder) {
    transforms = transforms.andThen(QuadTransformers.applyingColor(color)).andThen(QuadTransformers.settingEmissivity(luminosity)).andThen(QuadTransformers.applying(transform));
    model.addParts(tool, modifier, context, spriteGetter, transforms, builder);
  }

  @Override
  public RecordLoadable<? extends BlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate() {
    model.validate();
  }
}

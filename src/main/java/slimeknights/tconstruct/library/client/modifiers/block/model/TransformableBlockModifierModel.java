package slimeknights.tconstruct.library.client.modifiers.block.model;

import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;

import javax.annotation.Nonnull;

import com.mojang.datafixers.util.Function3;
import com.mojang.math.Transformation;

/** Modifier model that requires transforms */
public interface TransformableBlockModifierModel extends BlockModifierModel {
  /** Fields */
  LoadableField<Integer, TransformableBlockModifierModel> COLOR_FIELD = ColorLoadable.ALPHA.defaultField("color", false, TransformableBlockModifierModel::color);
  LoadableField<Integer, TransformableBlockModifierModel> LUMINOSITY_FIELD = IntLoadable.range(0, 15).defaultField("luminosity", 0, false, TransformableBlockModifierModel::luminosity);
  LoadableField<Transformation, TransformableBlockModifierModel> TRANSFORM_FIELD = TinkerLoadables.TRANSFORMATION.defaultField("transform", Transformation.identity(), TransformableBlockModifierModel::transform);

  /** Color to apply to the texture */
  int color();
  /** Luminosity to apply to the texture */
  int luminosity();
  /** Transform to apply to the model */
  @Nonnull
  Transformation transform();

  /** Creates a simple loader */
  static <T extends TransformableBlockModifierModel> RecordLoadable<T> loader(Function3<Integer, Integer, Transformation, T> constructor) {
    return RecordLoadable.create(COLOR_FIELD, LUMINOSITY_FIELD, TRANSFORM_FIELD, constructor);
  }
}

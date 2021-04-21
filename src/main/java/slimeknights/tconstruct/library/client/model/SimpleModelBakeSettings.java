package slimeknights.tconstruct.library.client.model;

import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.util.math.Matrix4f;

public class SimpleModelBakeSettings implements ModelBakeSettings {

  private final ModelBakeSettings first;
  private final ModelBakeSettings second;
  private final boolean uvLock;

  public SimpleModelBakeSettings(ModelBakeSettings first, ModelBakeSettings second, boolean uvLock) {
    this.first = first;
    this.second = second;
    this.uvLock = uvLock;
  }

  @Override
  public boolean isShaded() {
    return uvLock;
  }

  @Override
  public AffineTransformation getRotation() {
    return compose(first.getRotation(), second.getRotation());
  }

  private AffineTransformation compose(AffineTransformation first, AffineTransformation other) {
    if (first.equals(AffineTransformation.identity())) return other;
    if (other.equals(AffineTransformation.identity())) return first;
    Matrix4f m = first.getMatrix();
    m.multiply(other.getMatrix());
    return new AffineTransformation(m);
  }
}

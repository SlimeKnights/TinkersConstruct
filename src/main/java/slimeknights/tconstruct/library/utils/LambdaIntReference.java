package slimeknights.tconstruct.library.utils;

import lombok.AllArgsConstructor;
import net.minecraft.util.IntReferenceHolder;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

@AllArgsConstructor
public class LambdaIntReference extends IntReferenceHolder {
  private final IntSupplier getter;
  private final IntConsumer setter;

  @Override
  public int get() {
    return getter.getAsInt();
  }

  @Override
  public void set(int value) {
    setter.accept(value);
  }
}

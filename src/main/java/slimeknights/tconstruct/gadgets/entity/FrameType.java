package slimeknights.tconstruct.gadgets.entity;

import lombok.Getter;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

import java.util.Locale;

public enum FrameType implements IStringSerializable {
  JEWEL,
  COBALT,
  MANYULLYN,
  GOLD,
  CLEAR;

  private static final FrameType[] VALUES = values();
  @Getter
  private final int id = ordinal();

  public static FrameType byId(int id) {
    if (id < 0 || id >= VALUES.length) {
      id = 0;
    }

    return VALUES[id];
  }

  @Override
  public String getString() {
    return this.toString().toLowerCase(Locale.US);
  }

  public static Item getFrameFromType(FrameType type) {
        return TinkerGadgets.itemFrame.get(type);
  }
}

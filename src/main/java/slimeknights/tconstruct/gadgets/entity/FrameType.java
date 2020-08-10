package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.gadgets.TinkerGadgets;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public enum FrameType implements IStringSerializable {
  JEWEL(0),
  COBALT(1),
  ARDITE(2),
  MANYULLYN(3),
  GOLD(4),
  CLEAR(5);

  private static final FrameType[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(FrameType::getId)).toArray(FrameType[]::new);
  private final int id;

  private FrameType(int idIn) {
    this.id = idIn;
  }

  public int getId() {
    return this.id;
  }

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

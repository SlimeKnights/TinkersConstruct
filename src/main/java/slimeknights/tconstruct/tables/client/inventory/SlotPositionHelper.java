package slimeknights.tconstruct.tables.client.inventory;

import net.minecraft.world.inventory.Slot;

import java.lang.reflect.Field;

/** Compatibility helper for moving slots now that vanilla slot coordinates are final. */
public final class SlotPositionHelper {
  private static final Field SLOT_X = slotField("x");
  private static final Field SLOT_Y = slotField("y");

  private SlotPositionHelper() {}

  public static void move(Slot slot, int x, int y) {
    try {
      SLOT_X.setInt(slot, x);
      SLOT_Y.setInt(slot, y);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to move slot", e);
    }
  }

  private static Field slotField(String name) {
    try {
      Field field = Slot.class.getField(name);
      field.setAccessible(true);
      return field;
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("Failed to access slot field " + name, e);
    }
  }
}

package slimeknights.tconstruct.tools.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMaterials extends Item {

  public static final ItemMaterials INSTANCE = new ItemMaterials();

  public static ItemStack slimeCrystal = INSTANCE.addVariant(0, "SlimeCrystal");
  public static ItemStack slimeCrystalBlue = INSTANCE.addVariant(1, "SlimeCrystalBlue");

  private ItemMaterials() {
    setHasSubtypes(true);
  }

  protected ItemStack addVariant(int meta, String name) {
    return new ItemStack(this, 1, meta);
  }

  @Override
  public int getMetadata(int damage) {
    return damage;
  }
}

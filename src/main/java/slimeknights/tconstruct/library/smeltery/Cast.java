package slimeknights.tconstruct.library.smeltery;

import net.minecraft.item.Item;

import java.util.Collection;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tools.Pattern;

public class Cast extends Pattern implements ICast {

  public Cast() {
    this.setCreativeTab(TinkerRegistry.tabSmeltery);
  }

  @Override
  protected Collection<Item> getSubItemToolparts() {
    return TinkerRegistry.getCastItems();
  }

  @Override
  protected boolean isValidSubitemMaterial(Material material) {
    return material.isCastable();
  }
}

package slimeknights.tconstruct.library.materials;

import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;

@Getter
public class Material implements IMaterial {

  // todo config
//  public static int VALUE_Ore() {
//    return (int) (VALUE_Ingot * Config.oreToIngotRatio);
//  }
  // todo default stats. figure out how to hadnle them. maybe have to register stat types and handle it there?
/*
  static {
    UNKNOWN.addStats(new HeadMaterialStats(1, 1, 1, 0));
    UNKNOWN.addStats(new HandleMaterialStats(1f, 0));
    UNKNOWN.addStats(new ExtraMaterialStats(0));
    UNKNOWN.addStats(new BowMaterialStats(1f, 1f, 0f));
    UNKNOWN.addStats(new BowStringMaterialStats(1f));
    UNKNOWN.addStats(new ArrowShaftMaterialStats(1f, 0));
    UNKNOWN.addStats(new FletchingMaterialStats(1f, 1f));
    UNKNOWN.addStats(new ProjectileMaterialStats());
  }*/

  /**
   * This String uniquely identifies a material.
   */
  private final MaterialId identifier;

  /**
   * The fluid associated with this material, can not be null, but Fluids.EMPTY.
   * If non-null also indicates that the material can be cast.
   */
  protected final Fluid fluid;

  /**
   * Material can be crafted into parts in the PartBuilder
   */
  private final boolean craftable;

  /**
   * This item will be used instead of the generic shard item when returning leftovers.
   */
  private final ItemStack shardItem;

  /**
   * Key used for localizing the material
   */
  private final String translationKey;

  /**
   * Materials should only be created by the MaterialManager.
   * They're synced over the network and other classes might lead to unexpected behaviour.
   */
  public Material(ResourceLocation identifier, Fluid fluid, boolean craftable, ItemStack shardItem) {
    // lowercases and removes whitespaces
    this.identifier = new MaterialId(identifier);
    this.fluid = fluid;
    this.craftable = craftable;
    this.shardItem = shardItem;
    this.translationKey = Util.makeTranslationKey("material", identifier);
  }

  @Override
  public ItemStack getShard() {
    if (shardItem != ItemStack.EMPTY) {
      return shardItem.copy();
    }
    return ItemStack.EMPTY;
  }

  @Override
  public String getTranslationKey() {
    return translationKey;
  }
}

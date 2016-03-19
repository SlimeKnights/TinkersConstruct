package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemSpade;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.tools.TinkerTools;

public class Shovel extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.grass,
                      net.minecraft.block.material.Material.ground,
                      net.minecraft.block.material.Material.sand,
                      net.minecraft.block.material.Material.craftedSnow,
                      net.minecraft.block.material.Material.snow,
                      net.minecraft.block.material.Material.clay,
                      net.minecraft.block.material.Material.cake);

  public Shovel() {
    this(PartMaterialType.handle(TinkerTools.toolRod),
         PartMaterialType.head(TinkerTools.shovelHead),
         PartMaterialType.extra(TinkerTools.binding));
  }

  protected Shovel(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.HARVEST);

    setHarvestLevel("shovel", 0);
  }

  @Override
  public boolean isEffective(IBlockState block) {
    return effective_materials.contains(block.getMaterial()) || ItemSpade.EFFECTIVE_ON.contains(block);
  }

  @Override
  public float damagePotential() {
    return 0.9f;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    return buildDefaultTag(materials).get();
  }
}

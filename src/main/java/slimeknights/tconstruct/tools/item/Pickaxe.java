package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.tools.TinkerTools;

public class Pickaxe extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.iron,
                      net.minecraft.block.material.Material.anvil,
                      net.minecraft.block.material.Material.rock,
                      net.minecraft.block.material.Material.ice,
                      net.minecraft.block.material.Material.glass,
                      net.minecraft.block.material.Material.packedIce,
                      net.minecraft.block.material.Material.piston);

  // Pick-head, binding, tool-rod
  public Pickaxe() {
    this(PartMaterialType.handle(TinkerTools.toolRod),
         PartMaterialType.head(TinkerTools.pickHead),
         PartMaterialType.extra(TinkerTools.binding));
  }

  public Pickaxe(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.HARVEST);

    // set the toolclass, actual harvestlevel is done by the overridden callback
    this.setHarvestLevel("pickaxe", 0);
  }

  @Override
  public boolean isEffective(Block block) {
    return effective_materials.contains(block.getMaterial()) || ItemPickaxe.EFFECTIVE_ON.contains(block);
  }

  @Override
  public float damagePotential() {
    return 1f;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    return buildDefaultTag(materials).get();
  }
}

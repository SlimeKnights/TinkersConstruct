package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Excavator extends Shovel {

  public Excavator() {
    super(PartMaterialType.handle(TinkerTools.toughToolRod),
          PartMaterialType.head(TinkerTools.excavatorHead),
          PartMaterialType.head(TinkerTools.largePlate),
          PartMaterialType.extra(TinkerTools.toughBinding));
  }

  @Override
  public float miningSpeedModifier() {
    return 0.28f; // a bit faster than hammers to make terraforming easier
  }

  @Override
  public float damagePotential() {
    return 0.3f;
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    if(!ToolHelper.isToolEffective2(stack, world.getBlockState(origin))) {
      return ImmutableList.of();
    }
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 1);
  }

  @Override
  public int[] getRepairParts() {
    return new int[] {1,2};
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(HandleMaterialStats.TYPE);
    HeadMaterialStats head     = materials.get(1).getStatsOrUnknown(HeadMaterialStats.TYPE);
    HeadMaterialStats plate    = materials.get(2).getStatsOrUnknown(HeadMaterialStats.TYPE);
    ExtraMaterialStats binding = materials.get(3).getStatsOrUnknown(ExtraMaterialStats.TYPE);

    ToolNBT data = new ToolNBT();
    data.head(head, plate);
    data.extra(binding);
    data.handle(handle);

    data.durability *= 1.75f;

    data.modifiers = 2;

    return data.get();
  }
}

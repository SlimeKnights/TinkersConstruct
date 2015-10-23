package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.IAoeTool;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolBuilder;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Hammer extends Pickaxe {

  public Hammer() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toughToolRod),
          new PartMaterialType.ToolPartType(TinkerTools.hammerHead),
          new PartMaterialType.ToolPartType(TinkerTools.largePlate),
          new PartMaterialType.ToolPartType(TinkerTools.largePlate));

    addCategory(Category.WEAPON);
  }

  @Override
  public float damagePotential() {
    return 1.0f;
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityPlayer player, EntityLivingBase entity, float damage) {
    // bonus damage vs. undead!
    if(entity.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
      damage += 3 + TConstruct.random.nextInt(4);
    }
    return super.dealDamage(stack, player, entity, damage);
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 1);
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats plate1 = materials.get(2).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats plate2 = materials.get(3).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);

    data.durability += plate1.durability * plate1.extraQuality + plate2.durability * plate2.extraQuality;
    data.durability *= 1.5f * handle.handleQuality;
    data.durability += 0.05f * handle.durability;

    data.speed *= 0.3f + 0.4f * head.extraQuality;
    data.speed += 0.3f * handle.miningspeed * handle.handleQuality;

    data.attack += plate1.attack * plate2.attack * (plate1.extraQuality * plate2.extraQuality * 0.5f);

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data.get();
  }
}

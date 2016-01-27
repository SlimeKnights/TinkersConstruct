package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Hammer extends Pickaxe {

  public Hammer() {
    super(PartMaterialType.handle(TinkerTools.toughToolRod),
          PartMaterialType.head(TinkerTools.hammerHead),
          PartMaterialType.head(TinkerTools.largePlate),
          PartMaterialType.head(TinkerTools.largePlate));

    addCategory(Category.WEAPON);
  }

  @Override
  public float miningSpeedModifier() {
    return 0.33f;
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
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(HandleMaterialStats.TYPE);
    HeadMaterialStats head     = materials.get(1).getStatsOrUnknown(HeadMaterialStats.TYPE);
    HeadMaterialStats plate1   = materials.get(2).getStatsOrUnknown(HeadMaterialStats.TYPE);
    HeadMaterialStats plate2   = materials.get(3).getStatsOrUnknown(HeadMaterialStats.TYPE);

    ToolNBT data = new ToolNBT();
    data.head(head, plate1, plate2);
    data.handle(handle);

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data.get();
  }
}

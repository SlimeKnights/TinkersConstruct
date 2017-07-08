package slimeknights.tconstruct.tools.tools;

import com.google.common.collect.ImmutableList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Hammer extends Pickaxe {

  public static final float DURABILITY_MODIFIER = 2.5f;

  public Hammer() {
    super(PartMaterialType.handle(TinkerTools.toughToolRod),
          PartMaterialType.head(TinkerTools.hammerHead),
          PartMaterialType.head(TinkerTools.largePlate),
          PartMaterialType.head(TinkerTools.largePlate));

    addCategory(Category.WEAPON);
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      addDefaultSubItems(subItems);
      addInfiTool(subItems, "InfiMiner");
    }
  }

  @Override
  public float miningSpeedModifier() {
    return 0.4f;
  }

  @Override
  public float damagePotential() {
    return 1.2f;
  }

  @Override
  public double attackSpeed() {
    return 0.8f;
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
    // bonus damage vs. undead!
    if(entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD) {
      damage += 3 + TConstruct.random.nextInt(4);
    }
    boolean hit = super.dealDamage(stack, player, entity, damage);

    if(hit && readyForSpecialAttack(player)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.HAMMER_ATTACK, player, 0.8d);
    }
    return hit;
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 3, 3, 1);
  }

  @Override
  public int[] getRepairParts() {
    return new int[] { 1, 2, 3 };
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.6f;
  }

  @Override
  public ToolNBT buildTagData(List<Material> materials) {
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(MaterialTypes.HANDLE);
    HeadMaterialStats head = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    HeadMaterialStats plate1 = materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD);
    HeadMaterialStats plate2 = materials.get(3).getStatsOrUnknown(MaterialTypes.HEAD);

    ToolNBT data = new ToolNBT();
    data.head(head, head, plate1, plate2); // head itself counts for more
    data.handle(handle);

    // harvestlevel is always determined by the head
    data.harvestLevel = head.harvestLevel;

    data.durability *= DURABILITY_MODIFIER;

    return data;
  }
}

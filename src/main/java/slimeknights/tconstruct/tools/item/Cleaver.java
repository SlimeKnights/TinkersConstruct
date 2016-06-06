package slimeknights.tconstruct.tools.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.modifiers.ModBeheading;

import static slimeknights.tconstruct.tools.item.BroadSword.effective_materials;

public class Cleaver extends ToolCore {

  public Cleaver() {
    super(PartMaterialType.handle(TinkerTools.toughToolRod),
          PartMaterialType.head(TinkerTools.largeSwordBlade),
          PartMaterialType.head(TinkerTools.largePlate),
          PartMaterialType.extra(TinkerTools.toughToolRod));

    addCategory(Category.WEAPON);
  }

  // no offhand for you
  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStackIn, World worldIn, EntityPlayer player, EnumHand hand) {
    return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
  }

  @Override
  public float damagePotential() {
    return 1.2f;
  }

  @Override
  public double attackSpeed() {
    return 0.7d;
  }

  @Override
  public float damageCutoff() {
    return 25f;
  }

  @Override
  public int[] getRepairParts() {
    return new int[] {1,2};
  }

  @Override
  public boolean isEffective(IBlockState block) {
    return effective_materials.contains(block.getMaterial());
  }

  @Override
  public float miningSpeedModifier() {
    return 0.2f;
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, EntityLivingBase entity, float damage) {
    boolean hit = super.dealDamage(stack, player, entity, damage);

    // cleaver slash particle
    if(hit && readyForSpecialAttack(player)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.CLEAVER_ATTACK, player, 0.85d);
    }

    return hit;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(HandleMaterialStats.TYPE);
    HeadMaterialStats head     = materials.get(1).getStatsOrUnknown(HeadMaterialStats.TYPE);
    HeadMaterialStats shield   = materials.get(2).getStatsOrUnknown(HeadMaterialStats.TYPE);
    ExtraMaterialStats guard   = materials.get(3).getStatsOrUnknown(ExtraMaterialStats.TYPE);

    ToolNBT data = new ToolNBT();
    data.head(head, shield);
    data.extra(guard);
    data.handle(handle);

    data.attack *= 1.4f;
    data.attack += 2f;

    // triple durability!
    data.durability *= 2f;
    data.modifiers = 3;

    return data.get();
  }

  @Override
  public void addMaterialTraits(NBTTagCompound root, List<Material> materials) {
    super.addMaterialTraits(root, materials);

    // beheading "trait", 2 level -> 2 applications
    ModBeheading.CLEAVER_BEHEADING_MOD.apply(root);
    ModBeheading.CLEAVER_BEHEADING_MOD.apply(root);
  }
}

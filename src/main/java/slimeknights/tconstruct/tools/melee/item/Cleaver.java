package slimeknights.tconstruct.tools.melee.item;

import net.minecraft.entity.Entity;
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
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.SwordCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.modifiers.ModBeheading;

public class Cleaver extends SwordCore {

  public static final float DURABILITY_MODIFIER = 2f;

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
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
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
    return new int[]{1, 2};
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
    boolean hit = super.dealDamage(stack, player, entity, damage);

    // cleaver slash particle
    if(hit && readyForSpecialAttack(player)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.CLEAVER_ATTACK, player, 0.85d);
    }

    return hit;
  }

  @Override
  public float getRepairModifierForPart(int index) {
    return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
  }

  @Override
  public ToolNBT buildTagData(List<Material> materials) {
    HandleMaterialStats handle = materials.get(0).getStatsOrUnknown(MaterialTypes.HANDLE);
    HeadMaterialStats head = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    HeadMaterialStats shield = materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD);
    ExtraMaterialStats guard = materials.get(3).getStatsOrUnknown(MaterialTypes.EXTRA);

    ToolNBT data = new ToolNBT();
    data.head(head, shield);
    data.extra(guard);
    data.handle(handle);

    data.attack *= 1.3f;
    data.attack += 3f;

    // triple durability!
    data.durability *= DURABILITY_MODIFIER;

    return data;
  }

  @Override
  public void addMaterialTraits(NBTTagCompound root, List<Material> materials) {
    super.addMaterialTraits(root, materials);

    // beheading "trait", 2 level -> 2 applications
    ModBeheading.CLEAVER_BEHEADING_MOD.apply(root);
    ModBeheading.CLEAVER_BEHEADING_MOD.apply(root);
  }
}

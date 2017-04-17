package slimeknights.tconstruct.tools.ranged.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.entity.EntityShuriken;

public class Shuriken extends ProjectileCore {

  private static PartMaterialType shurikenPMT = new PartMaterialType(TinkerTools.knifeBlade, MaterialTypes.HEAD, MaterialTypes.EXTRA, MaterialTypes.PROJECTILE);

  public Shuriken() {
    super(shurikenPMT, shurikenPMT, shurikenPMT, shurikenPMT);

    addCategory(Category.NO_MELEE, Category.PROJECTILE);
  }

  @Override
  public int[] getRepairParts() {
    return new int[]{0, 1, 2, 3};
  }

  @Override
  public float damagePotential() {
    return 0.7f;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if(ToolHelper.isBroken(itemStackIn)) {
      return ActionResult.newResult(EnumActionResult.FAIL, itemStackIn);
    }
    playerIn.getCooldownTracker().setCooldown(itemStackIn.getItem(), 4);

    if(!worldIn.isRemote) {
      boolean usedAmmo = useAmmo(itemStackIn, playerIn);
      EntityProjectileBase projectile = getProjectile(itemStackIn, itemStackIn, worldIn, playerIn, 2.1f, 0f, 1f, usedAmmo);
      worldIn.spawnEntity(projectile);
    }

    return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
  }

  @Override
  public ProjectileNBT buildTagData(List<Material> materials) {
    ProjectileNBT data = new ProjectileNBT();
    data.head(materials.get(0).getStatsOrUnknown(MaterialTypes.HEAD),
              materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD),
              materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD),
              materials.get(3).getStatsOrUnknown(MaterialTypes.HEAD));

    data.extra(materials.get(0).getStatsOrUnknown(MaterialTypes.EXTRA),
               materials.get(1).getStatsOrUnknown(MaterialTypes.EXTRA),
               materials.get(2).getStatsOrUnknown(MaterialTypes.EXTRA),
               materials.get(3).getStatsOrUnknown(MaterialTypes.EXTRA));

    data.attack += 1f;
    data.accuracy = 1f;

    return data;
  }

  @Override
  public EntityProjectileBase getProjectile(ItemStack stack, ItemStack launcher, World world, EntityPlayer player, float speed, float inaccuracy, float progress, boolean usedAmmo) {
    inaccuracy *= ProjectileNBT.from(stack).accuracy;
    return new EntityShuriken(world, player, speed, inaccuracy, getProjectileStack(stack, world, player, usedAmmo), launcher);
  }
}

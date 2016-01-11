package slimeknights.tconstruct.tools.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.tools.TinkerTools;

public class Cleaver extends BroadSword {

  public Cleaver() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toughToolRod),
          new PartMaterialType.ToolPartType(TinkerTools.largeSwordBlade),
          new PartMaterialType.ToolPartType(TinkerTools.largePlate),
          new PartMaterialType.ToolPartType(TinkerTools.toughToolRod));
  }

  @Override
  public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
    // no blocking for you
    return itemStackIn;
  }

  @Override
  public float damagePotential() {
    return 1.8f;
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    target.hurtResistantTime += 7;
    return super.hitEntity(stack, target, attacker);
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
    if (entity instanceof EntityPlayer)
    {
      EntityPlayer player = (EntityPlayer) entity;
      ItemStack equipped = player.getCurrentEquippedItem();
      if (equipped == stack)
      {
        player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 2, 2, true, false));
      }
    }
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats shield = materials.get(2).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats guard = materials.get(3).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);
    data.handle(handle).extra(shield, guard);

    data.durability *= 1f + 0.1f * (guard.extraQuality - 0.5f);
    data.speed *= 1f + 0.05f * (handle.handleQuality * handle.miningspeed);

    data.attack += 5f;
    data.attack += shield.attack/3f;
    data.attack *= 1f + 0.15f * handle.handleQuality * guard.extraQuality;
/*
    data.durability += 0.5f * shield.durability;
    data.durability *= 0.3f + 0.8f * handle.handleQuality;
    data.durability += 0.5f * guard.extraQuality * shield.durability;

    data.attack += 0.2f * shield.attack;
    data.attack *= 0.33f + 0.22f * handle.handleQuality + 0.45f * guard.extraQuality;
*/
    data.modifiers = 2;

    return data.get();
  }
}

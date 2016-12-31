package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;

import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.modifiers.ModifierNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ModifierTagHolder;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.tools.TinkerTools;

public class TraitShocking extends AbstractTrait {

  public TraitShocking() {
    super("shocking", 0xffffff);
  }

  @Override
  public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    if(player.getEntityWorld().isRemote) {
      return;
    }

    ModifierTagHolder modtag = ModifierTagHolder.getModifier(tool, getModifierIdentifier());
    Data data = modtag.getTagData(Data.class);

    if(data.charge >= 100f) {
      if(attackEntitySecondary(new EntityDamageSource("lightningBolt", player), 5f, target, false, true, false)) {
        TinkerTools.proxy.spawnEffectParticle(ParticleEffect.Type.HEART_ELECTRO, target, 5);
        if(player instanceof EntityPlayerMP) {
          Sounds.playSoundForAll(player, Sounds.shocking_discharge, 2f, 1f);
        }
        data.charge = 0;

        modtag.save();

        TagUtil.setEnchantEffect(tool, false);
      }
    }
  }

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
    if(!isSelected || world.isRemote || world.getTotalWorldTime() % 5 > 0) {
      return;
    }
    if(entity instanceof EntityPlayer) {
      ItemStack stackInUse = ((EntityPlayer) entity).getActiveItemStack();
      // "same" item
      if(stackInUse != null && !tool.getItem().shouldCauseBlockBreakReset(tool, stackInUse)) {
        return;
      }
    }
    ModifierTagHolder modtag = ModifierTagHolder.getModifier(tool, getModifierIdentifier());
    Data data = modtag.getTagData(Data.class);

    // fully charged
    if(data.charge >= 100) {
      return;
    }

    // how far did we move?
    double dx = entity.posX - data.x;
    double dy = entity.posY - data.y;
    double dz = entity.posZ - data.z;

    double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
    if(dist < 0.1f) {
      return;
    }
    else if(dist > 5f) {
      dist = 5f;
    }
    data.charge += dist * 2f;

    // play sound when fully charged
    if(data.charge >= 100f) {
      TagUtil.setEnchantEffect(tool, true);
      // send only to the player that is charged
      if(entity instanceof EntityPlayerMP) {
        Sounds.PlaySoundForPlayer(entity, Sounds.shocking_charged, 1f, 0.8f + 0.2f * random.nextFloat());
      }
    }

    data.x = entity.posX;
    data.y = entity.posY;
    data.z = entity.posZ;
    modtag.save();
  }

  public static class Data extends ModifierNBT {

    float charge;
    double x;
    double y;
    double z;

    @Override
    public void read(NBTTagCompound tag) {
      super.read(tag);
      charge = tag.getFloat("charge");
      x = tag.getDouble("x");
      y = tag.getDouble("y");
      z = tag.getDouble("z");
    }

    @Override
    public void write(NBTTagCompound tag) {
      super.write(tag);
      tag.setFloat("charge", charge);
      tag.setDouble("x", x);
      tag.setDouble("y", y);
      tag.setDouble("z", z);
    }
  }
}

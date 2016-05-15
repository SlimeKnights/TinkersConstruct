package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.world.World;

import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.shared.client.ParticleEffect;
import slimeknights.tconstruct.tools.TinkerTools;

public class TraitShocking extends AbstractTrait {
  public TraitShocking() {
    super("shocking", 0xffffff);
  }

  @Override
  public void onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, boolean isCritical) {
    if(player.worldObj.isRemote) {
      return;
    }
    NBTTagCompound tag = TinkerUtil.getModifierTag(tool, identifier);
    Data data = Data.read(tag);
    if(data.charge >= 100f) {
      if(attackEntitySecondary(new EntityDamageSource("lightningBolt", player), 5f, target, false, true, false)) {
        TinkerTools.proxy.spawnEffectParticle(ParticleEffect.Type.HEART_ELECTRO, target, 5);
        if(player instanceof EntityPlayerMP) {
          Sounds.playSoundForAll(player, Sounds.shocking_discharge, 2f, 1f);
        }
        data.charge = 0;

        NBTTagList tagList = TagUtil.getModifiersTagList(tool);
        int index = TinkerUtil.getIndexInCompoundList(tagList, identifier);
        data.write(tag);
        tagList.set(index, tag);
        TagUtil.setModifiersTagList(tool, tagList);
        TagUtil.setEnchantEffect(tool, false);
      }
    }
  }

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
    if(!isSelected || world.isRemote) {
      return;
    }
    NBTTagList tagList = TagUtil.getModifiersTagList(tool);
    int index = TinkerUtil.getIndexInCompoundList(tagList, identifier);
    NBTTagCompound tag = tagList.getCompoundTagAt(index);

    Data data = Data.read(tag);

    // fully charged
    if(data.charge >= 100) {
      return;
    }

    // how far did we move?
    double dx = entity.posX - data.x;
    double dy = entity.posY - data.y;
    double dz = entity.posZ - data.z;

    double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
    if(dist < 0.1f) {
      return;
    }
    else if(dist > 5f) {
      dist = 5f;
    }
    data.charge += dist*2f;

    // play sound when fully charged
    if(data.charge >= 100f) {
      TagUtil.setEnchantEffect(tool, true);
      // send only to the player that is charged
      if(entity instanceof EntityPlayerMP) {
        Sounds.PlaySoundForPlayer(entity, Sounds.shocking_charged, 1f,  0.8f + 0.2f * random.nextFloat());
      }
    }

    data.x = entity.posX;
    data.y = entity.posY;
    data.z = entity.posZ;
    data.write(tag);

    tagList.set(index, tag);
    TagUtil.setModifiersTagList(tool, tagList);
  }

  public static class Data {
    float charge;
    double x;
    double y;
    double z;

    public static Data read(NBTTagCompound tag) {
      Data data = new Data();
      data.charge = tag.getFloat("charge");
      data.x = tag.getDouble("x");
      data.y = tag.getDouble("y");
      data.z = tag.getDouble("z");
      return data;
    }

    public void write(NBTTagCompound tag) {
      tag.setFloat("charge", charge);
      tag.setDouble("x", x);
      tag.setDouble("y", y);
      tag.setDouble("z", z);
    }
  }
}

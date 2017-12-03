package slimeknights.tconstruct.tools.traits;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;

import java.util.List;

import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitHellish extends AbstractTrait {

  private static final float bonusDamage = 4f;

  public TraitHellish() {
    super("hellish", 0xff0000);
  }

  @Override
  public float damage(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    for(EnumCreatureType creatureType : EnumCreatureType.values()) {
      for(Biome.SpawnListEntry spawnListEntry : Biome.REGISTRY.getObject(new ResourceLocation("hell")).getSpawnableList(creatureType)) {
        if(spawnListEntry.entityClass.equals(target.getClass())) {
          // nether mob
          return super.damage(tool, player, target, damage, newDamage, isCritical);
        }
      }
    }

    return super.damage(tool, player, target, damage, newDamage + bonusDamage, isCritical);
  }

  @Override
  public List<String> getExtraInfo(ItemStack tool, NBTTagCompound modifierTag) {
    String loc = String.format(LOC_Extra, getModifierIdentifier());

    return ImmutableList.of(Util.translateFormatted(loc, Util.df.format(bonusDamage)));
  }
}

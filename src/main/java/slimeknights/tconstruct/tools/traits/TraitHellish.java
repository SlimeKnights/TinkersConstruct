package slimeknights.tconstruct.tools.traits;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;

import slimeknights.tconstruct.library.traits.AbstractTrait;

public class TraitHellish extends AbstractTrait {

  public TraitHellish() {
    super("hellish", 0xff0000);
  }

  @Override
  public float onHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damage, float newDamage, boolean isCritical) {
    for(EnumCreatureType creatureType : EnumCreatureType.values()) {
      for(BiomeGenBase.SpawnListEntry spawnListEntry : BiomeGenBase.hell.getSpawnableList(creatureType)) {
        if(spawnListEntry.entityClass.equals(target.getClass())) {
          // nether mob
          return super.onHit(tool, player, target, damage, newDamage, isCritical);
        }
      }
    }

    return super.onHit(tool, player, target, damage, newDamage + 2f, isCritical);
  }
}

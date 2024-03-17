package slimeknights.tconstruct.world.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import slimeknights.tconstruct.world.TinkerWorld;

/**
 * Clay based slime cube
 */
public class TerracubeEntity extends ArmoredSlimeEntity {
  public TerracubeEntity(EntityType<? extends TerracubeEntity> type, Level worldIn) {
    super(type, worldIn);
  }

  /**
   * Checks if a slime can spawn at the given location
   */
  public static boolean canSpawnHere(EntityType<? extends Slime> entityType, ServerLevelAccessor world, MobSpawnType reason, BlockPos pos, RandomSource random) {
    if (world.getDifficulty() == Difficulty.PEACEFUL) {
      return false;
    }
    if (reason == MobSpawnType.SPAWNER) {
      return true;
    }
    BlockPos down = pos.below();
    if (world.getFluidState(pos).is(FluidTags.WATER) && world.getFluidState(down).is(FluidTags.WATER)) {
      return true;
    }
    return world.getBlockState(down).isValidSpawn(world, down, entityType) && Monster.isDarkEnoughToSpawn(world, pos, random);
  }

  @Override
  protected float getJumpPower() {
    return 0.5f * this.getBlockJumpFactor();
  }

  @Override
  protected float getAttackDamage() {
    return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) + 2;
  }

  @Override
  protected ParticleOptions getParticleType() {
    return TinkerWorld.terracubeParticle.get();
  }

  @Override
  protected int calculateFallDamage(float distance, float damageMultiplier) {
    return 0;
  }

  @Override
  protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
    // earth slime spawns with vanilla armor, but unlike zombies turtle shells are fair game
    // vanilla logic but simplified down to just helmets
    float multiplier = difficulty.getSpecialMultiplier();
    if (this.random.nextFloat() < 0.15F * multiplier) {
      int armorQuality = this.random.nextInt(3);
      if (this.random.nextFloat() < 0.25F) {
        ++armorQuality;
      }
      if (this.random.nextFloat() < 0.25F) {
        ++armorQuality;
      }
      if (this.random.nextFloat() < 0.25F) {
        ++armorQuality;
      }

      ItemStack current = this.getItemBySlot(EquipmentSlot.HEAD);
      if (current.isEmpty()) {
        Item item = armorQuality == 5 ? Items.TURTLE_HELMET : getEquipmentForSlot(EquipmentSlot.HEAD, armorQuality);
        if (item != null) {
          this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(item));
          this.enchantSpawnedArmor(random, multiplier, EquipmentSlot.HEAD);
        }
      }
    }
  }
}

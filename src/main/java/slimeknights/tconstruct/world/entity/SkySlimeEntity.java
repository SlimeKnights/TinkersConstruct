package slimeknights.tconstruct.world.entity;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.world.TinkerWorld;

import java.util.List;

public class SkySlimeEntity extends ArmoredSlimeEntity {
  private double bounceAmount = 0f;
  public SkySlimeEntity(EntityType<? extends SkySlimeEntity> type, Level worldIn) {
    super(type, worldIn);
  }

  @Override
  protected float getJumpPower() {
    return (float)Math.sqrt(this.getSize()) * this.getBlockJumpFactor() / 2;
  }

  @Override
  protected ParticleOptions getParticleType() {
    return TinkerWorld.skySlimeParticle.get();
  }

  @Override
  public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
    if (isSuppressingBounce()) {
      return super.causeFallDamage(distance, damageMultiplier * 0.2f, source);
    }
    float[] ret = ForgeHooks.onLivingFall(this, distance, damageMultiplier);
    if (ret == null) {
      return false;
    }
    distance = ret[0];
    if (distance > 2) {
      // invert Y motion, boost X and Z slightly
      Vec3 motion = getDeltaMovement();
      setDeltaMovement(motion.x / 0.95f, motion.y * -0.9, motion.z / 0.95f);
      bounceAmount = getDeltaMovement().y;
      fallDistance = 0f;
      hasImpulse = true;
      setOnGround(false);
      playSound(Sounds.SLIMY_BOUNCE.getSound(), 1f, 1f);
    }
    return false;
  }

  @Override
  public void move(MoverType typeIn, Vec3 pos) {
    super.move(typeIn, pos);
    if (bounceAmount > 0) {
      Vec3 motion = getDeltaMovement();
      setDeltaMovement(motion.x, bounceAmount, motion.z);
      bounceAmount = 0;
    }
  }

  @Override
  protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
    // sky slime spawns with tinkers armor, high chance of travelers, low chance of plate
    // vanilla logic but simplified down to just helmets
    float multiplier = difficulty.getSpecialMultiplier();
    if (this.random.nextFloat() < 0.15F * multiplier) {
      // 2.5% chance of plate
      boolean isPlate = this.random.nextFloat() < (0.05f * multiplier);
      // TODO: allow adding more helmet types, unfortunately tags don't let me add chances
      // TODO: randomize plate materials
      ItemStack helmet = new ItemStack((isPlate ? TinkerTools.plateArmor : TinkerTools.travelersGear).get(ArmorItem.Type.HELMET));

      // for plate, just init stats
      ToolStack tool = ToolStack.from(helmet);
      tool.ensureHasData();
      ModifierNBT modifiers = tool.getUpgrades();
      ModDataNBT persistentData = tool.getPersistentData();
      if (!isPlate) {
        // travelers dyes a random color
        persistentData.putInt(TinkerModifiers.dyed.getId(), this.random.nextInt(0xFFFFFF+1));
        modifiers = modifiers.withModifier(TinkerModifiers.dyed.getId(), 1);
      }

      // add some random defense modifiers
      int max = tool.getFreeSlots(SlotType.DEFENSE);
      for (int i = 0; i < max; i++) {
        if (this.random.nextFloat() > 0.5f * multiplier) {
          break;
        }
        persistentData.addSlots(SlotType.DEFENSE, -1);
        modifiers = modifiers.withModifier(randomModifier(this.random, TinkerTags.Modifiers.SLIME_DEFENSE), 1);
      }
      // chance of diamond or emerald
      if (tool.getFreeSlots(SlotType.UPGRADE) > 0 && this.random.nextFloat() < 0.5f * multiplier) {
        persistentData.addSlots(SlotType.UPGRADE, -1);
        modifiers = modifiers.withModifier(randomModifier(this.random, TinkerTags.Modifiers.GEMS), 1);
      }

      // triggers stat rebuild
      tool.setUpgrades(modifiers);

      // finally, give the slime the helmet
      this.setItemSlot(EquipmentSlot.HEAD, helmet);
    }
  }

  /** Gets a random defense modifier from the tag */
  private static ModifierId randomModifier(RandomSource random, TagKey<Modifier> tag) {
    List<Modifier> options = ModifierManager.getTagValues(tag);
    return options.get(random.nextInt(options.size())).getId();
  }
}

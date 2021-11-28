package slimeknights.tconstruct.tools.modifiers.defense;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.tools.logic.ModifierMaxLevel;
import slimeknights.tconstruct.tools.modifiers.defense.BlastProtectionModifier.BlastData;

public class BlastProtectionModifier extends AbstractProtectionModifier<BlastData> {
  /** Entity data key for the data associated with this modifier */
  private static final TinkerDataKey<BlastData> BLAST_DATA = TConstruct.createKey("blast_protection");
  public BlastProtectionModifier() {
    super(0x17DD62, BLAST_DATA);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, ExplosionEvent.Detonate.class, BlastProtectionModifier::onExplosionDetonate);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingUpdateEvent.class, BlastProtectionModifier::livingTick);
  }

  @Override
  public float getProtectionModifier(IModifierToolStack tool, int level, EquipmentContext context, EquipmentSlotType slotType, DamageSource source, float modifierValue) {
    if (!source.isDamageAbsolute() && !source.canHarmInCreative() && source.isExplosion()) {
      modifierValue += getScaledLevel(tool, level) * 2;
    }
    return modifierValue;
  }

  @Override
  protected BlastData createData() {
    return new BlastData();
  }

  @Override
  protected void reset(BlastData data) {
    data.wasKnockback = false;
  }

  /** On explosion, checks if any blast protected entity is involved, if so marks them for knockback update next tick */
  private static void onExplosionDetonate(ExplosionEvent.Detonate event) {
    Explosion explosion = event.getExplosion();
    Vector3d center = explosion.getPosition();
    float diameter = explosion.size * 2;
    // search the entities for someone protection by blast protection
    for (Entity entity : event.getAffectedEntities()) {
      if (!entity.isImmuneToExplosions()) {
        entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
          // if the entity has blast protection and the blast protection level is bigger than vanilla, time to process
          BlastData blastData = data.get(BLAST_DATA);
          if (blastData != null && blastData.getMax() > 0) {
            // explosion is valid as long as the entity's eye is not directly on the explosion
            double x = entity.getPosX() - center.x;
            double z = entity.getPosZ() - center.z;
            if (x != 0 || z != 0 || (entity.getPosYEye() - center.y) != 0) {
              // we need two numbers to calculate the knockback: distance to explosion and block density
              double y = entity.getPosY() - center.y;
              double distance = MathHelper.sqrt(x*x + y*y + z*z) / diameter;
              if (distance <= 1) {
                blastData.wasKnockback = true;
              }
            }
          }
        });
      }
    }
  }

  /** If the entity is marked for knockback update, adjust velocity */
  private static void livingTick(LivingUpdateEvent event) {
    LivingEntity living = event.getEntityLiving();
    if (!living.getEntityWorld().isRemote) {
      living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        BlastData blastData = data.get(BLAST_DATA);
        if (blastData != null && blastData.wasKnockback) {
          blastData.wasKnockback = false;
          float max = blastData.getMax();
          if (max > 0) {
            // due to MC-198809, vanilla does not actually reduce the knockback except on levels higher than obtainable in survival (blast prot VII)
            // thus, we only care about our own level for reducing
            double scale = 1 - (blastData.getMax() * 0.15f);
            if (scale <= 0) {
              living.setMotion(Vector3d.ZERO);
            } else {
              living.setMotion(living.getMotion().mul(scale, scale, scale));
            }
            living.velocityChanged = true;
          }
        }
      });
    }
  }

  /** Data object for the modifier */
  protected static class BlastData extends ModifierMaxLevel {
    /** If true, the entity was knocked back and needs their velocity adjusted */
    boolean wasKnockback = false;
  }
}

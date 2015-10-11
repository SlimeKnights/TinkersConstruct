package slimeknights.tconstruct.tools.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.network.EntityMovementChangePacket;

// BattleSign Ability: Blocks more damage and can reflect projectiles. The ultimate defensive weapon.
public class BattleSign extends BroadSword {

  public BattleSign() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.wideBoard));
  }

  @Override
  public int attackSpeed() {
    return 0;
  }

  @Override
  public float damagePotential() {
    return 0.86f;
  }

  // Extra damage reduction when blocking with a battlesign
  @SubscribeEvent(priority = EventPriority.LOW) // lower priority so we get called later since we change tool NBT
  public void reducedDamageBlocked(LivingHurtEvent event) {
    // don't affect unblockable or magic damage or explosion damage
    // projectiles are handled in LivingAttackEvent
    if(event.source.isUnblockable() || event.source.isMagicDamage() || event.source.isExplosion() || event.source.isProjectile() || event.isCanceled()) {
      return;
    }
    if(!shouldBlockDamage(event.entityLiving)) {
      return;
    }

    EntityPlayer player = (EntityPlayer) event.entityLiving;
    ItemStack battlesign = player.getCurrentEquippedItem();

    // got hit by something: reduce damage
    int damage = event.ammount < 2f ? 1 : Math.round(event.ammount/2f);
    // reduce damage. After this event the damage will be halved again because we're blocking so we have to factor this in
    event.ammount *= 0.7f;

    // reflect damage
    if(event.source.getEntity() != null) {
      event.source.getEntity().attackEntityFrom(DamageSource.causeThornsDamage(player), event.ammount/2f);
      damage = damage * 3 / 2;
    }
    ToolHelper.damageTool(battlesign, damage, player);
  }

  @SubscribeEvent
  public void reflectProjectiles(LivingAttackEvent event) {
    // only blockable projectile damage
    if(event.source.isUnblockable() || !event.source.isProjectile()) {
      return;
    }
    if(!shouldBlockDamage(event.entityLiving)) {
      return;
    }

    EntityPlayer player = (EntityPlayer) event.entityLiving;
    ItemStack battlesign = player.getCurrentEquippedItem();

    // ensure the player is looking at the projectile (aka not getting shot into the back)
    Entity projectile = event.source.getSourceOfDamage();
    Vec3 motion = new Vec3(projectile.motionX, projectile.motionY, projectile.motionZ);
    Vec3 look = player.getLookVec();

    // this gives a factor of how much we're looking at the incoming arrow
    double strength = -look.dotProduct(motion.normalize());
    // we're looking away. oh no.
    if(strength < 0.1)
      return;

    // caught that bastard! block it!
    event.setCanceled(true);

    // and return it to the sender
    // calc speed of the projectile
    double speed = projectile.motionX*projectile.motionX + projectile.motionY*projectile.motionY + projectile.motionZ*projectile.motionZ;
    speed = Math.sqrt(speed);
    speed += 0.2f; // we add a bit speed

    // and redirect it to where the player is looking
    projectile.motionX = look.xCoord * speed;
    projectile.motionY = look.yCoord * speed;
    projectile.motionZ = look.zCoord * speed;

    projectile.rotationYaw = (float)(Math.atan2(projectile.motionX, projectile.motionZ) * 180.0D / Math.PI);
    projectile.rotationPitch = (float)(Math.atan2(projectile.motionY, speed) * 180.0D / Math.PI);

    // notify clients from change, otherwise people will get veeeery confused
    TinkerNetwork.sendToAll(new EntityMovementChangePacket(projectile));

    // special treatement for arrows
    if(projectile instanceof EntityArrow) {
      ((EntityArrow) projectile).shootingEntity = player;

      // the inverse is done when the event is cancelled in arrows etc.
      // we reverse it so it has no effect. yay
      projectile.motionX /= -0.10000000149011612D;
      projectile.motionY /= -0.10000000149011612D;
      projectile.motionZ /= -0.10000000149011612D;
    }

    // use durability equal to the damage prevented
    ToolHelper.damageTool(battlesign, (int)event.ammount, player);
  }

  protected boolean shouldBlockDamage(Entity entity) {
    // hit entity is a player?
    if(!(entity instanceof EntityPlayer)) {
      return false;
    }
    EntityPlayer player = (EntityPlayer) entity;
    // needs to be blocking with a battlesign
    if(!player.isBlocking() || player.getCurrentEquippedItem().getItem() != this) {
      return false;
    }

    // broken battlesign.
    if(ToolHelper.isBroken(player.getCurrentEquippedItem())) {
      return false;
    }

    return true;
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head = materials.get(1).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);

    data.durability += (0.1f + 0.2f * handle.handleQuality) * head.durability;
    data.durability += (0.1f + 0.2f * head.extraQuality) * handle.durability;
    data.speed *= 0.4f + 0.6f * handle.extraQuality;

    data.attack += 2.0f;

    data.modifiers = 4;

    return data.get();
  }
}

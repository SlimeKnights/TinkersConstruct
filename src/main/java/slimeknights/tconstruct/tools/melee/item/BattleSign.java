package slimeknights.tconstruct.tools.melee.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.TinkerToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

import javax.annotation.Nonnull;
import java.util.List;

// BattleSign Ability: Blocks more damage and can reflect projectiles. The ultimate defensive weapon.
public class BattleSign extends TinkerToolCore {

  public BattleSign() {
    super(PartMaterialType.handle(TinkerTools.toolRod),
          PartMaterialType.head(TinkerTools.signHead));

    addCategory(Category.WEAPON);

    this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
      @Override
      @SideOnly(Side.CLIENT)
      public float apply(@Nonnull ItemStack stack, World worldIn, EntityLivingBase entityIn) {
        return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
      }
    });
  }

  @Override
  public double attackSpeed() {
    return 1.2;
  }

  @Override
  public float damagePotential() {
    return 0.86f;
  }

  @Nonnull
  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BLOCK;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 72000;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    if(!ToolHelper.isBroken(itemStackIn)) {
      playerIn.setActiveHand(hand);
      return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
    }
    return new ActionResult<>(EnumActionResult.FAIL, itemStackIn);
  }

  // Extra damage reduction when blocking with a battlesign
  @SubscribeEvent(priority = EventPriority.LOW) // lower priority so we get called later since we change tool NBT
  public void reducedDamageBlocked(LivingHurtEvent event) {
    // don't affect unblockable or magic damage or explosion damage
    // projectiles are handled in LivingAttackEvent
    if(event.getSource().isUnblockable() ||
       event.getSource().isMagicDamage() ||
       event.getSource().isExplosion() ||
       event.getSource().isProjectile() ||
       event.isCanceled()) {
      return;
    }
    if(!shouldBlockDamage(event.getEntityLiving())) {
      return;
    }

    EntityPlayer player = (EntityPlayer) event.getEntityLiving();
    ItemStack battlesign = player.getActiveItemStack();

    // got hit by something: reduce damage
    int damage = event.getAmount() < 2f ? 1 : Math.round(event.getAmount() / 2f);
    // reduce damage. After this event the damage will be halved again because we're blocking so we have to factor this in
    event.setAmount(event.getAmount() * 0.7f);

    // reflect damage
    if(event.getSource().getTrueSource() != null) {
      event.getSource().getTrueSource().attackEntityFrom(DamageSource.causeThornsDamage(player), event.getAmount() / 2f);
      damage = damage * 3 / 2;
    }
    ToolHelper.damageTool(battlesign, damage, player);
  }

  @SubscribeEvent
  public void reflectProjectiles(LivingAttackEvent event) {
    // only blockable projectile damage
    if(event.getSource().isUnblockable() || !event.getSource().isProjectile() || event.getSource().getImmediateSource() == null) {
      return;
    }
    if(!shouldBlockDamage(event.getEntityLiving())) {
      return;
    }

    EntityPlayer player = (EntityPlayer) event.getEntityLiving();
    ItemStack battlesign = player.getActiveItemStack();

    // ensure the player is looking at the projectile (aka not getting shot into the back)
    Entity projectile = event.getSource().getImmediateSource();
    Vec3d motion = new Vec3d(projectile.motionX, projectile.motionY, projectile.motionZ);
    Vec3d look = player.getLookVec();

    // this gives a factor of how much we're looking at the incoming arrow
    double strength = -look.dotProduct(motion.normalize());
    // we're looking away. oh no.
    if(strength < 0.1) {
      return;
    }

    // caught that bastard! block it!
    event.setCanceled(true);

    // and return it to the sender
    // calc speed of the projectile
    double speed = projectile.motionX * projectile.motionX + projectile.motionY * projectile.motionY + projectile.motionZ * projectile.motionZ;
    speed = Math.sqrt(speed);
    speed += 0.2f; // we add a bit speed

    // and redirect it to where the player is looking
    projectile.motionX = look.x * speed;
    projectile.motionY = look.y * speed;
    projectile.motionZ = look.z * speed;

    projectile.rotationYaw = (float) (Math.atan2(projectile.motionX, projectile.motionZ) * 180.0D / Math.PI);
    projectile.rotationPitch = (float) (Math.atan2(projectile.motionY, speed) * 180.0D / Math.PI);

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
    ToolHelper.damageTool(battlesign, (int) event.getAmount(), player);
  }

  protected boolean shouldBlockDamage(Entity entity) {
    // hit entity is a player?
    if(!(entity instanceof EntityPlayer)) {
      return false;
    }
    EntityPlayer player = (EntityPlayer) entity;
    // needs to be blocking with a battlesign
    if(!player.isActiveItemStackBlocking() || player.getActiveItemStack().getItem() != this) {
      return false;
    }

    // broken battlesign.
    return !ToolHelper.isBroken(player.getActiveItemStack());

  }

  @Override
  public ToolNBT buildTagData(List<Material> materials) {
    return buildDefaultTag(materials);
  }
  
  @Override
  public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
    return false;
  }
}

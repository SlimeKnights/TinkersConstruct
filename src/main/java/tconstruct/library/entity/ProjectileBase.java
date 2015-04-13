package tconstruct.library.entity;

import tconstruct.library.weaponry.AmmoItem;
import tconstruct.util.Reference;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.*;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.weaponry.entity.ArrowEntity;

import java.util.List;

// have to base this on EntityArrow, otherwise minecraft does derp things because everything is handled based on class.
public abstract class ProjectileBase extends EntityArrow implements IEntityAdditionalSpawnData {
    public final static String woodSound = Reference.resource("woodHit");
    public final static String stoneSound = Reference.resource("stoneHit");

    public ItemStack returnStack;

    public boolean bounceOnNoDamage = true;
    public boolean defused = false; // if this is true it wont hit any entities anymore

    public ProjectileBase(World world) {
        super(world);
    }

    public ProjectileBase(World world, double d, double d1, double d2) {
        this(world);
        this.setPosition(d, d1, d2);
    }

    public ProjectileBase(World world, EntityPlayer player, float speed, float accuracy, ItemStack stack) {
        this(world);

        this.shootingEntity = player;

        canBePickedUp = 1;

        // stuff from the arrow
        this.setLocationAndAngles(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, player.rotationYaw, player.rotationPitch);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0F;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI);
        this.motionZ = +MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, speed, accuracy);

        // our stuff
        returnStack = stack;

/*
        // fix upward default angle
        motionY = -MathHelper.sin((rotationPitch / 180F) * 3.141593F - 0.2f);
        setArrowHeading(motionX, motionY, motionZ, speed, 0.0f);

        // set better position and move it a tad so we don't hit ourselves.
        this.setPosition(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        */
        /*
        double l = Math.sqrt(motionX*motionX + motionY*motionY + motionZ*motionZ);
        double vx = motionX/l;
        double vy = motionY/l;
        double vz = motionZ/l;

        posX += vx*1.5f;
        posY += vy*1.5f;
        posZ += vz*1.5f;
        */

        //init();
    }

    public ItemStack getEntityItem ()
    {
        return returnStack;
    }

    protected void playHitBlockSound(int x, int y, int z)
    {
        Block block = worldObj.getBlock(x,y,z);
        if(block != null && block.blockMaterial == Material.wood)
            worldObj.playSoundAtEntity(this, woodSound, 1.0f, 1.0f);
        else {
            worldObj.playSoundAtEntity(this, stoneSound, 1.0f, 1.0f);
        }

        if(block != null)
            worldObj.playSoundAtEntity(this, block.stepSound.getBreakSound(), 0.7f, 1.0f);
    }

    protected void playHitEntitySound()
    {

    }

    /**
     * How deep the item enters stuff it hits. Should be bigger for bigger objects, and smaller for smaller objects.
     * 1.0f is exactly halfway in.
     */
    protected double getStuckDepth() { return 0.5f; }

    protected void doLivingHit(EntityLivingBase entityHit)
    {
        float knockback = returnStack.getTagCompound().getCompoundTag("InfiTool").getFloat("Knockback");
        if(shootingEntity instanceof EntityLivingBase)
            knockback += EnchantmentHelper.getKnockbackModifier((EntityLivingBase) shootingEntity, entityHit);

        if (!this.worldObj.isRemote)
        {
            entityHit.setArrowCountInEntity(entityHit.getArrowCountInEntity() + 1);
        }

        if (knockback > 0)
        {
            double horizontalSpeed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (horizontalSpeed > 0.0F)
            {
                entityHit.addVelocity(this.motionX * (double) knockback * 0.6000000238418579D / horizontalSpeed, 0.1D, this.motionZ * (double) knockback * 0.6000000238418579D / (double) horizontalSpeed);
            }
        }

        if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase)
        {
            EnchantmentHelper.func_151384_a(entityHit, this.shootingEntity);
            EnchantmentHelper.func_151385_b((EntityLivingBase)this.shootingEntity, entityHit);
        }

        if (this.shootingEntity != null && entityHit != this.shootingEntity && entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP)
        {
            ((EntityPlayerMP)this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0.0F));
        }
    }

    public void onHitBlock(MovingObjectPosition movingobjectposition) {
        this.field_145791_d = movingobjectposition.blockX;
        this.field_145792_e = movingobjectposition.blockY;
        this.field_145789_f = movingobjectposition.blockZ;
        this.field_145790_g = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);
        this.inData = this.worldObj.getBlockMetadata(this.field_145791_d, this.field_145792_e, this.field_145789_f);
        this.motionX = movingobjectposition.hitVec.xCoord - this.posX;
        this.motionY = movingobjectposition.hitVec.yCoord - this.posY;
        this.motionZ = movingobjectposition.hitVec.zCoord - this.posZ;
        double speed = getStuckDepth() * MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
        this.posX -= this.motionX / speed * 0.05000000074505806D;
        this.posY -= this.motionY / speed * 0.05000000074505806D;
        this.posZ -= this.motionZ / speed * 0.05000000074505806D;

        playHitBlockSound(movingobjectposition.blockX, movingobjectposition.blockY, movingobjectposition.blockZ);

        this.inGround = true;
        this.arrowShake = 7;
        this.setIsCritical(false);
        this.defused = true; // defuse it so it doesn't hit stuff anymore, being weird

        if (this.field_145790_g.getMaterial() != Material.air)
        {
            this.field_145790_g.onEntityCollidedWithBlock(this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f, this);
        }
    }

    public void onHitEntity(MovingObjectPosition movingobjectposition) {
        NBTTagCompound tags = returnStack.getTagCompound().getCompoundTag("InfiTool");
        float speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);

        // absolute distance travelled minus the current tick
        float distance = speed * (this.ticksInAir-1);
        // distance travelled in the current tick
        float dist2 = 0;
        dist2 += MathHelper.abs((float)movingobjectposition.entityHit.lastTickPosX - (float)this.lastTickPosX);
        dist2 += MathHelper.abs((float)movingobjectposition.entityHit.lastTickPosY - (float)this.lastTickPosY);
        dist2 += MathHelper.abs((float)movingobjectposition.entityHit.lastTickPosZ - (float)this.lastTickPosZ);
        dist2 = MathHelper.sqrt_double(dist2);

        distance += dist2;

        if(!tags.hasKey("BaseAttack"))
        {
            // quickly calculate the base attack damage from the actual attack damage and quartz modifiers
            // yes, this relies on quartz being the only modifier that modifiers damage, but at the point of writing this
            // that is the case, and later tools should have the tag
            int atk = tags.getInteger("Attack");
            if(tags.hasKey("ModAttack"))
            {
                int bonusDmg = tags.getIntArray("ModAttack")[0]/24 + 1;
                atk -= bonusDmg;
            }
            tags.setInteger("BaseAttack", atk);
        }

        float baseAttack = tags.getInteger("BaseAttack");
        float totalAttack = tags.getInteger("Attack");
        float damage = speed * baseAttack; // todo: potentially change this back to MathHelper.ceiling_float_int to get 1/2 heart steps back

        // add quartz damage
        damage += (totalAttack - baseAttack);

        // this is needed so really high scaling bow&arrow combinations don't get out of hand
        if(this instanceof ArrowEntity)
            damage = Math.max(0, damage - totalAttack/2f);

        boolean shotByPlayer = this.shootingEntity != null && this.shootingEntity instanceof EntityPlayer;

        // Damage calculations and stuff. For reference see AbilityHelper.onLeftClickEntity
        ToolCore ammo = (ToolCore)returnStack.getItem();

        // factor in modified stuff for base damage
        // basically we pass the base damage to all modifiers and take the highest one
        int baseDamage = 0;
        if(shotByPlayer) // prevent crashes with other things reflecting/shooting them
            for(ActiveToolMod toolmod : TConstructRegistry.activeModifiers) {
                int dmg = toolmod.baseAttackDamage(baseDamage, (int)damage, ammo, returnStack.getTagCompound(), tags, returnStack, (EntityPlayer)this.shootingEntity, movingobjectposition.entityHit);
                if(dmg > baseDamage)
                    baseDamage = dmg;
            }
        damage += baseDamage;

        // damage modification from the weapon itself
        damage *= ammo.getDamageModifier();

        // unlike the regular weapons, we do no gain more damage from potion of strength/weakness since we don't hit stuff directly

        // stonebound decreases, jagged increases damage too
        float bonusDamage = -AbilityHelper.calcStoneboundBonus(ammo, tags);
        // enchantments might add too
        if(shootingEntity != null && movingobjectposition.entityHit instanceof EntityLivingBase)
            bonusDamage += EnchantmentHelper.getEnchantmentModifierLiving((EntityLivingBase)this.shootingEntity, (EntityLivingBase)movingobjectposition.entityHit);
        damage += bonusDamage;

        // ensure we still have damage
        if(damage < 1)
            damage = 1;

        // the regular tool modifier damage stuff
        int modDamage = 0;
        if(shotByPlayer)
            for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
            {
                modDamage += mod.attackDamage(modDamage, (int)damage, ammo, returnStack.getTagCompound(), tags, returnStack, (EntityPlayer)this.shootingEntity, movingobjectposition.entityHit);
            }
        damage += modDamage;

        // calculate critical damaaage
        if (this.getIsCritical())
            damage += (this.rand.nextFloat()/4f + Math.min(0.75f, distance/25f)) * (damage / 2f + 2f);

        // and now we come to the part where we actually deal the damage!
        if(!dealDamage(damage, ammo, tags, movingobjectposition.entityHit))
        {
            if(!bounceOnNoDamage)
                this.setDead();

            // bounce off if we didn't deal damage
            this.motionX *= -0.10000000149011612D;
            this.motionY *= -0.10000000149011612D;
            this.motionZ *= -0.10000000149011612D;
            this.rotationYaw += 180.0F;
            this.prevRotationYaw += 180.0F;
            this.ticksInAir = 0;
            return;
        }
        // we hit it, BURNNNNNN
        else
            AbilityHelper.processFiery(shootingEntity, movingobjectposition.entityHit, tags);


        if (movingobjectposition.entityHit instanceof EntityLivingBase)
            doLivingHit((EntityLivingBase)movingobjectposition.entityHit);

        playHitEntitySound();

        if (!(movingobjectposition.entityHit instanceof EntityEnderman))
        {
            // reinforced helps for them to not break!
            if(rand.nextInt(10)+1 > tags.getInteger("Reinforced"))
                this.setDead();
            else
            {
                this.motionX = Math.max(-0.1,Math.min(0.1, -motionX));
                this.motionY = 0.2;
                this.motionZ = Math.max(-0.1,Math.min(0.1, -motionZ));
                this.ticksInAir = 0;
                this.posX = movingobjectposition.entityHit.posX;
                this.posY = movingobjectposition.entityHit.posY + movingobjectposition.entityHit.height/2d;
                this.posZ = movingobjectposition.entityHit.posZ;
                this.defused = true;
                //this.shootingEntity = movingobjectposition.entityHit;
            }
        }
    }

    // returns true if it was successful
    public boolean dealDamage(float damage, ToolCore ammo, NBTTagCompound tags, Entity entityHit)
    {
        boolean dealtDamage = false;

        // deal regular damage
        DamageSource damagesource;
        if (this.shootingEntity == null)
            damagesource = DamageSource.causeArrowDamage(this, this);
        else
            damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
        dealtDamage = entityHit.attackEntityFrom(damagesource, damage);

        return dealtDamage;
    }

    @Override
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
        // don't do anything, we set it ourselves at spawn
        // Mojangs code has a hard cap of 3.9 speed, but our projectiles can go faster, which desyncs client and server speeds
        // Packet that's causing it: S12PacketEntityVelocity
    }

    @Override
    // this function is the same as the vanilla EntityArrow
    public void onUpdate() {
        // call the entity update routine
        // luckily we can call this directly and take the arrow-code, since we'd have to call super.onUpdate otherwise. Which would not work.
        onEntityUpdate();

        // boioioiooioing
        if (this.arrowShake > 0)
            --this.arrowShake;

        // If we don't have our rotation set correctly, infer it from our motion direction
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f) * 180.0D / Math.PI);
        }

        // we previously hit something. Check if the block is still there.
        Block block = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);
        if (block.getMaterial() != Material.air)
        {
            block.setBlockBoundsBasedOnState(this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f);
            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f);

            // we are stuck in a block. yay.
            if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)))
                this.inGround = true;
        }

        if (this.inGround)
            updateInGround();
        else
            updateInAir();
    }

    // Update while we're stuck in a block
    protected void updateInGround()
    {
        Block block = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);
        int j = this.worldObj.getBlockMetadata(this.field_145791_d, this.field_145792_e, this.field_145789_f);

        // check if it's still the same block
        if (block == this.field_145790_g && j == this.inData)
        {
            ++this.ticksInGround;

            if (this.ticksInGround == 1200)
            {
                this.setDead();
            }
        }
        else
        {
            this.inGround = false;
            this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
            this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
        }
    }

    // update while traveling
    protected void updateInAir()
    {
        // tick tock
        ++this.ticksInAir;

        // do a raytrace from old to new position
        Vec3 curPos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
        Vec3 newPos = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(curPos, newPos, false, true, false);

        // raytrace messes with the positions. get new ones!
        curPos = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);

        // if we hit something, the collision point is our new position
        if (movingobjectposition != null)
            newPos = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
        else
            newPos = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);


        if(!defused) {
            // Check all the entities we on our way
            Entity entity = null;
            List<Entity> list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double distance = 0.0D;
            float f1;

            for (Entity ent : list) {
                if (!ent.canBeCollidedWith())
                    continue;
                // we don't shoot ourselves into the foot.
                if (ent == this.shootingEntity && this.ticksInAir < 5)
                    continue;

                f1 = 0.3F;
                AxisAlignedBB axisalignedbb1 = ent.boundingBox.expand((double) f1, (double) f1, (double) f1);
                MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(curPos, newPos);

                // did we actually collide with the entity, or was it just really close nearby?
                if (movingobjectposition1 != null) {
                    // check if this entity is closer than the other one we already hit
                    double otherDistance = curPos.distanceTo(movingobjectposition1.hitVec);

                    if (otherDistance < distance || distance == 0.0D) {
                        entity = ent;
                        distance = otherDistance;
                    }
                }
            }

            // if we hit something, new collision point!
            if (entity != null)
                movingobjectposition = new MovingObjectPosition(entity);

            // did we hit a player?
            if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;

                // can we attack said player?
                if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer))
                    movingobjectposition = null;

                // this check should probably done inside of the loop for accuracy..
            }
        }


        // time to hit the object
        if (movingobjectposition != null) {
            if (movingobjectposition.entityHit != null)
                onHitEntity(movingobjectposition);
            else
                onHitBlock(movingobjectposition);
        }

        // crithit particles
        if (this.getIsCritical())
            drawCritParticles();

        // MOVEMENT! yay.
        doMoveUpdate();
        double slowdown = 1.0d - getSlowdown();

        // bubblez
        if (this.isInWater())
        {
            for (int l = 0; l < 4; ++l)
            {
                float f4 = 0.25F;
                this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f4, this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX, this.motionY, this.motionZ);
            }

            // more slowdown in water
            slowdown = 1d - 20d*getSlowdown();
        }

        // phshshshshshs
        if (this.isWet())
            this.extinguish();

        // minimalistic slowdown!
        this.motionX *= slowdown;
        this.motionY *= slowdown;
        this.motionZ *= slowdown;
        // gravity
        this.motionY -= getGravity();
        this.setPosition(this.posX, this.posY, this.posZ);

        // tell blocks we collided with, that we collided with them!
        this.func_145775_I();
    }

    public void drawCritParticles()
    {
        for (int i = 0; i < 4; ++i)
        {
            this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)i / 4.0D, this.posY + this.motionY * (double)i / 4.0D, this.posZ + this.motionZ * (double)i / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
        }
    }

    protected void doMoveUpdate()
    {
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        double f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
        this.rotationPitch = (float)(Math.atan2(this.motionY, f2) * 180.0D / Math.PI);

        // normalize rotations
        while (this.rotationPitch - this.prevRotationPitch < -180.0F)
            this.prevRotationPitch -= 360.0F;

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            this.prevRotationPitch += 360.0F;

        while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            this.prevRotationYaw -= 360.0F;

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            this.prevRotationYaw += 360.0F;

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
    }

    /**
     * Factor for the slowdown. 0 = no slowdown, >0 = (1-slowdown)*speed slowdown, <0 = speedup
     */
    protected double getSlowdown() { return 0.01; }

    /**
     * Added to the y-velocity as gravitational pull. Otherwise stuff would simply float midair.
     */
    protected double getGravity() { return 0.05; }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer player)
    {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0)
        {
            boolean flag = this.canBePickedUp == 1 || this.canBePickedUp == 2 && player.capabilities.isCreativeMode;

            if (this.canBePickedUp == 1)
            {
                // ammo pickup
                if(this.returnStack != null && this.returnStack.getItem() instanceof AmmoItem)
                {
                    if(!((AmmoItem) this.returnStack.getItem()).pickupAmmo(returnStack, null, player))
                        flag = false;
                }
                // regular pickup.
                // Note that you can't pick up ammo items without the proper itemstack in your inventory to prevent dupes
                else if(returnStack != null && !player.inventory.addItemStackToInventory(returnStack))
                {
                    flag = false;
                }
            }

            if (flag)
            {
                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }


    /** NBT stuff **/

    @Override
    public void writeEntityToNBT (NBTTagCompound tags)
    {
        super.writeEntityToNBT(tags);

        tags.setTag("Throwable", this.returnStack.writeToNBT(new NBTTagCompound()));
        tags.setByte("onGround", (byte) (onGround ? 1 : 0));
        //tags.setBoolean("Retrieval", doNotRetrieve);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tags) {
        super.readEntityFromNBT(tags);

        this.returnStack = ItemStack.loadItemStackFromNBT(tags.getCompoundTag("Throwable"));
        onGround = tags.getByte("onGround") == 1;
        //doNotRetrieve = tags.getBoolean("Retrieval");
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        ByteBufUtils.writeItemStack(data, returnStack);
        data.writeFloat(rotationYaw);

        // shooting entity
        int id = shootingEntity == null ? this.getEntityId() : shootingEntity.getEntityId();
        data.writeInt(id);

        // motion stuff. This has to be sent separately since MC seems to do hardcoded stuff to arrows with this
        data.writeDouble(this.motionX);
        data.writeDouble(this.motionY);
        data.writeDouble(this.motionZ);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        returnStack = ByteBufUtils.readItemStack(data);
        rotationYaw = data.readFloat();
        shootingEntity = worldObj.getEntityByID(data.readInt());

        this.motionX = data.readDouble();
        this.motionY = data.readDouble();
        this.motionZ = data.readDouble();
        
        // position offset so it doesn't shoot from the player's eyes
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
        this.posY -= 0.10000000149011612D;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F;
    }
}

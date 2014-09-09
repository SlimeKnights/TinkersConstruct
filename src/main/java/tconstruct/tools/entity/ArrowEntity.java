package tconstruct.tools.entity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.crash.*;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.*;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ArrowEntity extends EntityArrow implements IEntityAdditionalSpawnData
{
    public ItemStack returnStack;
    public float mass;
    public int baseDamage;
    private float knockbackStrengthMod;
    Random random = new Random();

    public ArrowEntity(World par1World)
    {
        super(par1World);
    }

    public ArrowEntity(World world, EntityLivingBase living, float baseSpeed, ItemStack stack)
    {
        super(world, living, baseSpeed);
        this.returnStack = stack;
        NBTTagCompound toolTag = stack.getTagCompound().getCompoundTag("InfiTool");
        this.mass = toolTag.getFloat("Mass");
        this.baseDamage = toolTag.getInteger("Attack");
    }

    public ArrowEntity(World world, double x, double y, double z, ItemStack stack)
    {
        super(world, x, y, z);
        this.returnStack = stack;
        NBTTagCompound toolTag = stack.getTagCompound().getCompoundTag("InfiTool");
        this.mass = toolTag.getFloat("Mass");
        this.baseDamage = toolTag.getInteger("Attack");
    }

    @Override
    public void onCollideWithPlayer (EntityPlayer par1EntityPlayer)
    {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0)
        {
            boolean flag = this.canBePickedUp == 1 || this.canBePickedUp == 2 && par1EntityPlayer.capabilities.isCreativeMode;

            if (this.canBePickedUp == 1 && !par1EntityPlayer.inventory.addItemStackToInventory(returnStack))
            {
                flag = false;
            }

            if (flag)
            {
                this.playSound("random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                par1EntityPlayer.onItemPickup(this, 1);
                this.setDead();
            }
        }
    }

    @Override
    public void onUpdate ()
    {
        this.onEntityUpdate();
        if (returnStack == null || returnStack.stackSize < 1)
        {
            this.setDead();
        }

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f) * 180.0D / Math.PI);
        }

        Block i = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);

        i.setBlockBoundsBasedOnState(this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f);
        AxisAlignedBB axisalignedbb = i.getCollisionBoundingBoxFromPool(this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f);

        if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)))
        {
            this.inGround = true;
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }

        if (this.inGround)
        {
            if (!worldObj.isRemote)
            {
                Block j = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);
                int k = this.worldObj.getBlockMetadata(this.field_145791_d, this.field_145792_e, this.field_145789_f);

                if (j == this.field_145790_g && k == this.inData)
                {
                    ++this.ticksInGround;

                    if (this.ticksInGround == 120000)
                    {
                        this.setDead();
                    }
                }
                else
                {
                    this.inGround = false;
                    this.motionX *= (double) (this.rand.nextFloat() * 0.2F);
                    this.motionY *= (double) (this.rand.nextFloat() * 0.2F);
                    this.motionZ *= (double) (this.rand.nextFloat() * 0.2F);
                    this.ticksInGround = 0;
                    this.ticksInAir = 0;
                }
            }
        }
        else
        {
            ++this.ticksInAir;
            Vec3 vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec3, vec31, false, true, false);
            vec3 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            vec31 = Vec3.createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null)
            {
                vec31 = Vec3.createVectorHelper(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }

            Entity entity = null;
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            int l;
            float ySpeed;

            for (l = 0; l < list.size(); ++l)
            {
                Entity entity1 = (Entity) list.get(l);

                if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5))
                {
                    ySpeed = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double) ySpeed, (double) ySpeed, (double) ySpeed);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.calculateIntercept(vec3, vec31);

                    if (movingobjectposition1 != null)
                    {
                        double d1 = vec3.distanceTo(movingobjectposition1.hitVec);

                        if (d1 < d0 || d0 == 0.0D)
                        {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null)
            {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null && movingobjectposition.entityHit != null && movingobjectposition.entityHit instanceof EntityPlayer)
            {
                EntityPlayer entityplayer = (EntityPlayer) movingobjectposition.entityHit;

                if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer))
                {
                    movingobjectposition = null;
                }
            }

            float speed;
            float f3;

            if (movingobjectposition != null)
            {
                if (movingobjectposition.entityHit != null)
                {
                    speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    double damageSpeed = (double) speed * this.getDamage();
                    damageSpeed *= baseDamage;
                    damageSpeed /= 5D;
                    int damageInflicted = MathHelper.ceiling_double_int(damageSpeed);

                    if (this.getIsCritical())
                    {
                        damageInflicted += this.rand.nextInt(damageInflicted / 2 + 2);
                    }

                    DamageSource damagesource = null;

                    if (this.shootingEntity == null)
                    {
                        damagesource = DamageSource.causeArrowDamage(this, this);
                    }
                    else
                    {
                        damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
                    }

                    if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman))
                    {
                        movingobjectposition.entityHit.setFire(5);
                    }

                    if (returnStack.hasTagCompound())
                    {
                        int fireAspect = 0;
                        NBTTagCompound toolTags = returnStack.getTagCompound().getCompoundTag("InfiTool");
                        if (toolTags.hasKey("Fiery") || toolTags.hasKey("Lava"))
                        {
                            fireAspect *= 4;
                            if (toolTags.hasKey("Fiery"))
                            {
                                fireAspect += toolTags.getInteger("Fiery") / 5 + 1;
                            }
                            if (toolTags.getBoolean("Lava"))
                            {
                                fireAspect += 3;
                            }
                            entity.setFire(fireAspect);
                        }

                        int drain = toolTags.getInteger("Necrotic") * 2;
                        if (drain > 0 && shootingEntity != null && shootingEntity instanceof EntityLiving)
                            ((EntityLiving) shootingEntity).heal(random.nextInt(drain + 1));
                    }

                    if (movingobjectposition.entityHit.attackEntityFrom(damagesource, damageInflicted))
                    {
                        if (movingobjectposition.entityHit instanceof EntityLivingBase)
                        {
                            EntityLivingBase entityliving = (EntityLivingBase) movingobjectposition.entityHit;

                            if (!this.worldObj.isRemote)
                            {
                                entityliving.setArrowCountInEntity(entityliving.getArrowCountInEntity() + 1);
                            }

                            if (this.knockbackStrength > 0 || this.knockbackStrengthMod > 0)
                            {
                                f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

                                if (f3 > 0.0F)
                                {
                                    float knockback = knockbackStrength + knockbackStrengthMod;
                                    movingobjectposition.entityHit.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6000000238418579D / (double) f3, 0.1D, this.motionZ * (double) knockback * 0.6000000238418579D / (double) f3);
                                }
                            }

                            if (this.shootingEntity != null)
                            {
                                damagesource = DamageSource.causeArrowDamage(this, this);
                            }
                            else
                            {
                                damagesource = DamageSource.causeArrowDamage(this, this.shootingEntity);
                            }

                            if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity && movingobjectposition.entityHit instanceof EntityPlayer && this.shootingEntity instanceof EntityPlayerMP)
                            {
                                ((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(6, 0));
                                // TConstruct.packetPipeline.sendTo(new
                                // S2BPacketChangeGameState(6, 0),
                                // (EntityPlayerMP) this.shootingEntity);
                            }
                        }

                        this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));

                        if (!(movingobjectposition.entityHit instanceof EntityEnderman))
                        {
                            this.motionX = 0;
                            this.motionZ = 0;
                            if (movingobjectposition.entityHit instanceof EntityPlayer)
                            {
                                EntityPlayer player = (EntityPlayer) movingobjectposition.entityHit;
                                if (canBePickedUp == 2 || player.inventory.addItemStackToInventory(returnStack))
                                    this.setDead();
                            }
                            else if (movingobjectposition.entityHit instanceof EntityLivingBase)
                            {
                                EntityLivingBase living = (EntityLivingBase) movingobjectposition.entityHit;
                                if (canBePickedUp == 2 || addItemStackToInventory(returnStack, living))
                                    this.setDead();
                            }
                        }
                    }
                    else
                    {
                        this.motionX *= -0.10000000149011612D;
                        this.motionY *= -0.10000000149011612D;
                        this.motionZ *= -0.10000000149011612D;
                        this.rotationYaw += 180.0F;
                        this.prevRotationYaw += 180.0F;
                        this.ticksInAir = 0;
                    }
                }
                else
                {
                    this.field_145791_d = movingobjectposition.blockX;
                    this.field_145792_e = movingobjectposition.blockY;
                    this.field_145789_f = movingobjectposition.blockZ;
                    this.field_145790_g = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);
                    this.inData = this.worldObj.getBlockMetadata(this.field_145791_d, this.field_145792_e, this.field_145789_f);
                    this.motionX = (double) ((float) (movingobjectposition.hitVec.xCoord - this.posX));
                    this.motionY = (double) ((float) (movingobjectposition.hitVec.yCoord - this.posY));
                    this.motionZ = (double) ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
                    speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / (double) speed * 0.05000000074505806D;
                    this.posY -= this.motionY / (double) speed * 0.05000000074505806D;
                    this.posZ -= this.motionZ / (double) speed * 0.05000000074505806D;
                    this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.arrowShake = 0;
                    this.setIsCritical(false);

                    if (this.field_145790_g != Blocks.air)
                    {
                        this.field_145790_g.onEntityCollidedWithBlock(this.worldObj, this.field_145791_d, this.field_145792_e, this.field_145789_f, this);
                    }
                }
            }

            if (this.getIsCritical())
            {
                for (l = 0; l < 4; ++l)
                {
                    this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double) l / 4.0D, this.posY + this.motionY * (double) l / 4.0D, this.posZ + this.motionZ * (double) l / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            speed = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) speed) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
            {
                ;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F)
            {
                this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F)
            {
                this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F)
            {
                this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float dropSpeed = 0.99F;
            float speedMod = 1.05f + (mass / 1.26F) / 6F;
            ySpeed = 0.05F * speedMod;

            if (this.isInWater())
            {
                for (int j1 = 0; j1 < 4; ++j1)
                {
                    f3 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) f3, this.posY - this.motionY * (double) f3, this.posZ - this.motionZ * (double) f3, this.motionX, this.motionY, this.motionZ);
                }

                dropSpeed = 0.8F;
            }

            this.motionX *= (double) dropSpeed;
            this.motionY *= (double) dropSpeed;
            this.motionZ *= (double) dropSpeed;
            this.motionY -= (double) ySpeed;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();
        }
    }

    public boolean addItemStackToInventory (ItemStack par1ItemStack, EntityLivingBase living)
    {
        if (par1ItemStack == null)
        {
            return false;
        }
        else
        {
            try
            {
                int stackSize;

                /*
                 * if (par1ItemStack.isItemDamaged()) { slotID =
                 * this.getFirstEmptyStack(living);
                 * 
                 * if (slotID >= 0) { living.setCurrentItemOrArmor(slotID,
                 * par1ItemStack); living.setEquipmentDropChance(slotID, 2.0f);
                 * par1ItemStack.stackSize = 0; return true; } else { return
                 * false; } } else
                 */
                {
                    do
                    {
                        stackSize = par1ItemStack.stackSize;
                        par1ItemStack.stackSize = this.storePartialItemStack(par1ItemStack, living);
                    } while (par1ItemStack.stackSize > 0 && par1ItemStack.stackSize < stackSize);

                    return par1ItemStack.stackSize < stackSize;
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                // TODO is this needed???
                // crashreportcategory.addCrashSection("Item ID",
                // Integer.valueOf(par1ItemStack.itemID));
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(par1ItemStack.getItemDamage()));
                // crashreportcategory.addCrashSectionCallable("Item name", new
                // CallableItemName(this, par1ItemStack));
                throw new ReportedException(crashreport);
            }
        }
    }

    public int getFirstEmptyStack (EntityLivingBase living)
    {
        for (int i = 0; i < 5; ++i)
        {
            if (living.getEquipmentInSlot(i) == null)
            {
                return i;
            }
        }

        return -1;
    }

    private int storePartialItemStack (ItemStack par1ItemStack, EntityLivingBase living)
    {
        Item i = par1ItemStack.getItem();
        int j = par1ItemStack.stackSize;
        int slotID;

        if (par1ItemStack.getMaxStackSize() == 1)
        {
            slotID = this.getFirstEmptyStack(living);

            if (slotID < 0)
            {
                return j;
            }
            else
            {
                /*
                 * if (this.mainInventory[k] == null) { this.mainInventory[k] =
                 * par1ItemStack; }
                 */
                if (living.getEquipmentInSlot(slotID) == null)
                {
                    living.setCurrentItemOrArmor(slotID, par1ItemStack);
                    if (living instanceof EntityLiving)
                        ((EntityLiving) living).setEquipmentDropChance(slotID, 2.0f);
                }

                return 0;
            }
        }
        else
        {
            slotID = this.storeItemStack(par1ItemStack, living);

            if (slotID < 0)
            {
                slotID = this.getFirstEmptyStack(living);
            }

            if (slotID < 0)
            {
                return j;
            }
            else
            {
                ItemStack stack = living.getEquipmentInSlot(slotID);
                if (stack == null)
                {
                    living.setCurrentItemOrArmor(slotID, par1ItemStack);
                    if (living instanceof EntityLiving)
                        ((EntityLiving) living).setEquipmentDropChance(slotID, 2.0f);
                    return par1ItemStack.stackSize;
                }
                else
                {

                    int l = j;

                    if (j > stack.getMaxStackSize() - stack.stackSize)
                    {
                        l = stack.getMaxStackSize() - stack.stackSize;
                    }

                    if (l > 64 - stack.stackSize)
                    {
                        l = 64 - stack.stackSize;
                    }

                    if (l == 0)
                    {
                        return j;
                    }
                    else
                    {
                        j -= l;
                        stack.stackSize++;
                        living.setCurrentItemOrArmor(slotID, stack);
                        if (living instanceof EntityLiving)
                            ((EntityLiving) living).setEquipmentDropChance(slotID, 2.0f);
                        return j;
                    }
                }
            }
        }
    }

    private int storeItemStack (ItemStack par1ItemStack, EntityLivingBase living)
    {
        for (int slotID = 0; slotID < 5; ++slotID)
        {
            ItemStack stack = living.getEquipmentInSlot(slotID);
            if (stack != null && ItemStack.areItemStacksEqual(stack, par1ItemStack) && stack.isStackable() && stack.stackSize < stack.getMaxStackSize())
            {
                return slotID;
            }
        }

        return -1;
    }

    public void setKnockbackModStrength (float par1)
    {
        this.knockbackStrengthMod = par1;
    }

    @Override
    public void writeEntityToNBT (NBTTagCompound tags)
    {
        super.writeEntityToNBT(tags);
        tags.setTag("Throwable", this.returnStack.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readEntityFromNBT (NBTTagCompound tags)
    {
        super.readEntityFromNBT(tags);
        this.returnStack = ItemStack.loadItemStackFromNBT(tags.getCompoundTag("Throwable"));
    }

    @Override
    public void writeSpawnData (ByteBuf data)
    {
        if (!returnStack.hasTagCompound())
            this.kill();
        NBTTagCompound tags = returnStack.getTagCompound().getCompoundTag("InfiTool");
        ByteBufUtils.writeItemStack(data, returnStack);
        data.writeFloat(rotationYaw);
        data.writeFloat(mass);
        data.writeInt(tags.getInteger("RenderHandle"));
        data.writeInt(tags.getInteger("RenderHead"));
        data.writeInt(tags.getInteger("RenderAccessory"));

        int effects = 0;
        if (tags.hasKey("Effect1"))
            effects++;
        if (tags.hasKey("Effect2"))
            effects++;
        if (tags.hasKey("Effect3"))
            effects++;
        if (tags.hasKey("Effect4"))
            effects++;
        if (tags.hasKey("Effect5"))
            effects++;
        if (tags.hasKey("Effect6"))
            effects++;
        data.writeInt(effects);

        switch (effects)
        {
        case 6:
            data.writeInt(tags.getInteger("Effect6"));
        case 5:
            data.writeInt(tags.getInteger("Effect5"));
        case 4:
            data.writeInt(tags.getInteger("Effect4"));
        case 3:
            data.writeInt(tags.getInteger("Effect3"));
        case 2:
            data.writeInt(tags.getInteger("Effect2"));
        case 1:
            data.writeInt(tags.getInteger("Effect1"));
        }
    }

    @Override
    public void readSpawnData (ByteBuf data)
    {
        returnStack = ByteBufUtils.readItemStack(data);
        rotationYaw = data.readFloat();
        mass = data.readFloat();
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound toolTag = new NBTTagCompound();
        toolTag.setInteger("RenderHandle", data.readInt());
        toolTag.setInteger("RenderHead", data.readInt());
        toolTag.setInteger("RenderAccessory", data.readInt());
        switch (data.readInt())
        {
        case 6:
            toolTag.setInteger("Effect6", data.readInt());
        case 5:
            toolTag.setInteger("Effect5", data.readInt());
        case 4:
            toolTag.setInteger("Effect4", data.readInt());
        case 3:
            toolTag.setInteger("Effect3", data.readInt());
        case 2:
            toolTag.setInteger("Effect2", data.readInt());
        case 1:
            toolTag.setInteger("Effect1", data.readInt());
        }
        compound.setTag("InfiTool", toolTag);
        returnStack.setTagCompound(compound);
    }

    public ItemStack getEntityItem ()
    {
        return returnStack;
    }
}

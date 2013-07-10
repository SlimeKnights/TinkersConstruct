package mods.tinker.tconstruct.entity.projectile;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class ArrowEntity extends EntityArrow implements IEntityAdditionalSpawnData
{
    public ItemStack returnStack;

    public ArrowEntity(World par1World)
    {
        super(par1World);
    }

    public ArrowEntity(World world, EntityLiving living, float baseSpeed, ItemStack stack)
    {
        super(world, living, baseSpeed);
        this.returnStack = stack;
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
        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F)
        {
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f) * 180.0D / Math.PI);
        }

        int i = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);

        if (i > 0)
        {
            Block.blocksList[i].setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
            AxisAlignedBB axisalignedbb = Block.blocksList[i].getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);

            if (axisalignedbb != null && axisalignedbb.isVecInside(this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ)))
            {
                this.inGround = true;
            }
        }

        if (this.arrowShake > 0)
        {
            --this.arrowShake;
        }

        if (this.inGround)
        {
            int j = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
            int k = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);

            if (j == this.inTile && k == this.inData)
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
        else
        {
            ++this.ticksInAir;
            Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            Vec3 vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.rayTraceBlocks_do_do(vec3, vec31, false, true);
            vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
            vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

            if (movingobjectposition != null)
            {
                vec31 = this.worldObj.getWorldVec3Pool().getVecFromPool(movingobjectposition.hitVec.xCoord, movingobjectposition.hitVec.yCoord, movingobjectposition.hitVec.zCoord);
            }

            Entity entity = null;
            List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;
            int l;
            float f1;

            for (l = 0; l < list.size(); ++l)
            {
                Entity entity1 = (Entity) list.get(l);

                if (entity1.canBeCollidedWith() && (entity1 != this.shootingEntity || this.ticksInAir >= 5))
                {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.boundingBox.expand((double) f1, (double) f1, (double) f1);
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

                if (entityplayer.capabilities.disableDamage || this.shootingEntity instanceof EntityPlayer && !((EntityPlayer) this.shootingEntity).func_96122_a(entityplayer))
                {
                    movingobjectposition = null;
                }
            }

            float f2;
            float f3;

            if (movingobjectposition != null)
            {
                if (movingobjectposition.entityHit != null)
                {
                    f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    int i1 = MathHelper.ceiling_double_int((double) f2 * this.damage);

                    if (this.getIsCritical())
                    {
                        i1 += this.rand.nextInt(i1 / 2 + 2);
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

                    if (movingobjectposition.entityHit.attackEntityFrom(damagesource, i1))
                    {
                        if (movingobjectposition.entityHit instanceof EntityLiving)
                        {
                            EntityLiving entityliving = (EntityLiving) movingobjectposition.entityHit;

                            if (!this.worldObj.isRemote)
                            {
                                entityliving.setArrowCountInEntity(entityliving.getArrowCountInEntity() + 1);
                            }

                            if (this.knockbackStrength > 0)
                            {
                                f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);

                                if (f3 > 0.0F)
                                {
                                    movingobjectposition.entityHit.addVelocity(this.motionX * (double) this.knockbackStrength * 0.6000000238418579D / (double) f3, 0.1D, this.motionZ
                                            * (double) this.knockbackStrength * 0.6000000238418579D / (double) f3);
                                }
                            }

                            if (this.shootingEntity != null)
                            {
                                EnchantmentThorns.func_92096_a(this.shootingEntity, entityliving, this.rand);
                            }

                            if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity && movingobjectposition.entityHit instanceof EntityPlayer
                                    && this.shootingEntity instanceof EntityPlayerMP)
                            {
                                ((EntityPlayerMP) this.shootingEntity).playerNetServerHandler.sendPacketToPlayer(new Packet70GameEvent(6, 0));
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
                                if (player.inventory.addItemStackToInventory(returnStack))
                                    this.setDead();
                            }
                            else if (movingobjectposition.entityHit instanceof EntityLiving)
                            {
                                EntityLiving living = (EntityLiving) movingobjectposition.entityHit;
                                if (addItemStackToInventory(returnStack, living))
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
                    this.xTile = movingobjectposition.blockX;
                    this.yTile = movingobjectposition.blockY;
                    this.zTile = movingobjectposition.blockZ;
                    this.inTile = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
                    this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
                    this.motionX = (double) ((float) (movingobjectposition.hitVec.xCoord - this.posX));
                    this.motionY = (double) ((float) (movingobjectposition.hitVec.yCoord - this.posY));
                    this.motionZ = (double) ((float) (movingobjectposition.hitVec.zCoord - this.posZ));
                    f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / (double) f2 * 0.05000000074505806D;
                    this.posY -= this.motionY / (double) f2 * 0.05000000074505806D;
                    this.posZ -= this.motionZ / (double) f2 * 0.05000000074505806D;
                    this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.arrowShake = 7;
                    this.setIsCritical(false);

                    if (this.inTile != 0)
                    {
                        Block.blocksList[this.inTile].onEntityCollidedWithBlock(this.worldObj, this.xTile, this.yTile, this.zTile, this);
                    }
                }
            }

            if (this.getIsCritical())
            {
                for (l = 0; l < 4; ++l)
                {
                    this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double) l / 4.0D, this.posY + this.motionY * (double) l / 4.0D, this.posZ + this.motionZ * (double) l / 4.0D,
                            -this.motionX, -this.motionY + 0.2D, -this.motionZ);
                }
            }

            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            f2 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

            for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) f2) * 180.0D / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
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
            float f4 = 0.99F;
            f1 = 0.05F;

            if (this.isInWater())
            {
                for (int j1 = 0; j1 < 4; ++j1)
                {
                    f3 = 0.25F;
                    this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double) f3, this.posY - this.motionY * (double) f3, this.posZ - this.motionZ * (double) f3, this.motionX,
                            this.motionY, this.motionZ);
                }

                f4 = 0.8F;
            }

            this.motionX *= (double) f4;
            this.motionY *= (double) f4;
            this.motionZ *= (double) f4;
            this.motionY -= (double) f1;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.doBlockCollisions();
        }
    }

    public boolean addItemStackToInventory (ItemStack par1ItemStack, EntityLiving living)
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

                /*if (par1ItemStack.isItemDamaged())
                {
                    slotID = this.getFirstEmptyStack(living);

                    if (slotID >= 0)
                    {
                        living.setCurrentItemOrArmor(slotID, par1ItemStack);
                        living.func_96120_a(slotID, 1.0f);
                        par1ItemStack.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else*/
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
                crashreportcategory.addCrashSection("Item ID", Integer.valueOf(par1ItemStack.itemID));
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(par1ItemStack.getItemDamage()));
                //crashreportcategory.addCrashSectionCallable("Item name", new CallableItemName(this, par1ItemStack));
                throw new ReportedException(crashreport);
            }
        }
    }

    public int getFirstEmptyStack (EntityLiving living)
    {
        for (int i = 0; i < 5; ++i)
        {
            if (living.getCurrentItemOrArmor(i) == null)
            {
                return i;
            }
        }

        return -1;
    }

    private int storePartialItemStack (ItemStack par1ItemStack, EntityLiving living)
    {
        int i = par1ItemStack.itemID;
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
                /*if (this.mainInventory[k] == null)
                {
                    this.mainInventory[k] = par1ItemStack;
                }*/
                if (living.getCurrentItemOrArmor(slotID) == null)
                {
                    living.setCurrentItemOrArmor(slotID, par1ItemStack);
                    living.func_96120_a(slotID, 1.0f);
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
                ItemStack stack = living.getCurrentItemOrArmor(slotID);
                if (stack == null)
                {
                    living.setCurrentItemOrArmor(slotID, par1ItemStack);
                    living.func_96120_a(slotID, 1.0f);
                    return par1ItemStack.stackSize;
                    /*this.mainInventory[slotID] = new ItemStack(i, 0, par1ItemStack.getItemDamage());

                    if (par1ItemStack.hasTagCompound())
                    {
                        this.mainInventory[slotID].setTagCompound((NBTTagCompound) par1ItemStack.getTagCompound().copy());
                    }*/
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
                        living.func_96120_a(slotID, 1.0f);
                        //this.mainInventory[slotID].stackSize += l;
                        //this.mainInventory[slotID].animationsToGo = 5;
                        return j;
                    }
                }
            }
        }
    }

    private int storeItemStack (ItemStack par1ItemStack, EntityLiving living)
    {
        for (int slotID = 0; slotID < 5; ++slotID)
        {
            ItemStack stack = living.getCurrentItemOrArmor(slotID);
            if (stack != null && stack.itemID == par1ItemStack.itemID && stack.isStackable() && stack.stackSize < stack.getMaxStackSize() && stack.stackSize < 64
                    && (!stack.getHasSubtypes() || stack.getItemDamage() == par1ItemStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, par1ItemStack))
            {
                return slotID;
            }
        }

        return -1;
    }

    public void writeEntityToNBT (NBTTagCompound tags)
    {
        super.writeEntityToNBT(tags);
        tags.setCompoundTag("Throwable", this.returnStack.writeToNBT(new NBTTagCompound()));
    }

    public void readEntityFromNBT (NBTTagCompound tags)
    {
        super.readEntityFromNBT(tags);
        this.returnStack = ItemStack.loadItemStackFromNBT(tags.getCompoundTag("Throwable"));
    }

    @Override
    public void writeSpawnData (ByteArrayDataOutput data)
    {
        if (!returnStack.hasTagCompound())
            this.kill();
        NBTTagCompound tags = returnStack.getTagCompound().getCompoundTag("InfiTool");
        data.writeShort(returnStack.itemID);
        data.writeFloat(rotationYaw);
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
    public void readSpawnData (ByteArrayDataInput data)
    {
        returnStack = new ItemStack(data.readShort(), 1, 0);
        rotationYaw = data.readFloat();
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
        compound.setCompoundTag("InfiTool", toolTag);
        returnStack.setTagCompound(compound);
    }

    public ItemStack getEntityItem ()
    {
        return returnStack;
    }
}

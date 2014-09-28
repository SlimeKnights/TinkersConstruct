package tconstruct.world.entity;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.*;
import io.netty.buffer.ByteBuf;
import java.util.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class CartEntity extends Entity implements IInventory, IEntityAdditionalSpawnData
{
    /** Array of item stacks stored in minecart (for storage minecarts). */
    protected ItemStack[] cargoItems;
    protected int fuel;
    protected boolean isInReverse;

    /** The type of minecart, 2 for powered, 1 for storage. */
    private int pullcartType;
    public double pushX;
    public double pushZ;
    protected boolean field_82345_h;

    /** Minecart rotational logic matrix */
    protected static final int[][][] matrix = new int[][][] { { { 0, 0, -1 }, { 0, 0, 1 } }, { { -1, 0, 0 }, { 1, 0, 0 } }, { { -1, -1, 0 }, { 1, 0, 0 } }, { { -1, 0, 0 }, { 1, -1, 0 } }, { { 0, 0, -1 }, { 0, -1, 1 } }, { { 0, -1, -1 }, { 0, 0, 1 } }, { { 0, 0, 1 }, { 1, 0, 0 } }, { { 0, 0, 1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { -1, 0, 0 } }, { { 0, 0, -1 }, { 1, 0, 0 } } };

    /** appears to be the progress of the turn */
    protected int turnProgress;
    protected double minecartX;
    protected double minecartY;
    protected double minecartZ;
    protected double minecartYaw;
    protected double minecartPitch;
    @SideOnly(Side.CLIENT)
    protected double velocityX;
    @SideOnly(Side.CLIENT)
    protected double velocityY;
    @SideOnly(Side.CLIENT)
    protected double velocityZ;

    /* Forge: Minecart Compatibility Layer Integration. */
    public static float defaultMaxSpeedRail = 0.4f;
    public static float defaultMaxSpeedGround = 0.4f;
    public static float defaultMaxSpeedAirLateral = 0.4f;
    public static float defaultMaxSpeedAirVertical = -1f;
    public static double defaultDragRidden = 0.996999979019165D;
    public static double defaultDragEmpty = 0.9599999785423279D;
    public static double defaultDragAir = 0.94999998807907104D;
    protected boolean canUseRail = true;
    protected boolean canBePushed = true;

    /* Instance versions of the above physics properties */
    protected float maxSpeedRail;
    protected float maxSpeedGround;
    protected float maxSpeedAirLateral;
    protected float maxSpeedAirVertical;
    protected double dragAir;

    Entity entityFollowing;

    public CartEntity(World par1World)
    {
        super(par1World);
        this.cargoItems = new ItemStack[36];
        this.fuel = 0;
        this.isInReverse = false;
        this.field_82345_h = true;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.7F);
        this.yOffset = this.height / 2.0F;
        this.stepHeight = 1.0f;

        maxSpeedRail = defaultMaxSpeedRail;
        maxSpeedGround = defaultMaxSpeedGround;
        maxSpeedAirLateral = defaultMaxSpeedAirLateral;
        maxSpeedAirVertical = defaultMaxSpeedAirVertical;
        dragAir = defaultDragAir;
    }

    public CartEntity(World world, int type)
    {
        this(world);
        pullcartType = type;
    }

    public CartEntity(World world, double x, double y, double z, int type)
    {
        this(world);
        this.setPosition(x, y + (double) this.yOffset, z);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.pullcartType = type;
        this.stepHeight = 1.0f;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they
     * walk on. used for spiders and wolves to prevent them from trampling crops
     */
    @Override
    protected boolean canTriggerWalking ()
    {
        return false;
    }

    @Override
    protected void entityInit ()
    {
        this.dataWatcher.addObject(16, new Byte((byte) 0));
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(1));
        this.dataWatcher.addObject(19, new Integer(0));
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and
     * blocks. This enables the entity to be pushable on contact, like boats or
     * minecarts.
     */
    @Override
    public AxisAlignedBB getCollisionBox (Entity par1Entity)
    {
        return par1Entity.boundingBox;
    }

    /**
     * returns the bounding box for this entity
     */
    @Override
    public AxisAlignedBB getBoundingBox ()
    {
        return this.boundingBox;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities
     * when colliding.
     */
    @Override
    public boolean canBePushed ()
    {
        return canBePushed;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding
     * this one.
     */
    @Override
    public double getMountedYOffset ()
    {
        return (double) this.height * 0.0D - 0.30000001192092896D;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom (DamageSource par1DamageSource, int par2)
    {
        if (!this.worldObj.isRemote && !this.isDead)
        {
            if (this.isEntityInvulnerable())
            {
                return false;
            }
            else
            {
                this.setRollingDirection(-this.getRollingDirection());
                this.setRollingAmplitude(10);
                this.setBeenAttacked();
                this.setDamage(this.getDamage() + par2 * 10);

                if (par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer) par1DamageSource.getEntity()).capabilities.isCreativeMode)
                {
                    this.setDamage(100);
                }

                if (this.getDamage() > 40)
                {
                    if (this.riddenByEntity != null)
                    {
                        this.riddenByEntity.mountEntity(this);
                    }

                    this.setDead();
                    dropCartAsItem();
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation ()
    {
        this.setRollingDirection(-this.getRollingDirection());
        this.setRollingAmplitude(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10);
    }

    /**
     * Returns true if other Entities should be prevented from moving through
     * this Entity.
     */
    @Override
    public boolean canBeCollidedWith ()
    {
        return !this.isDead;
    }

    /**
     * Will get destroyed next tick.
     */
    @Override
    public void setDead ()
    {
        if (this.field_82345_h)
        {
            for (int var1 = 0; var1 < this.getSizeInventory(); ++var1)
            {
                ItemStack var2 = this.getStackInSlot(var1);

                if (var2 != null)
                {
                    float var3 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float var4 = this.rand.nextFloat() * 0.8F + 0.1F;
                    float var5 = this.rand.nextFloat() * 0.8F + 0.1F;

                    while (var2.stackSize > 0)
                    {
                        int var6 = this.rand.nextInt(21) + 10;

                        if (var6 > var2.stackSize)
                        {
                            var6 = var2.stackSize;
                        }

                        var2.stackSize -= var6;
                        EntityItem entityitem = new EntityItem(this.worldObj, this.posX + (double) var3, this.posY + (double) var4, this.posZ + (double) var5, new ItemStack(var2.getItem(), var6, var2.getItemDamage()));

                        if (var2.hasTagCompound())
                        {
                            entityitem.getEntityItem().setTagCompound((NBTTagCompound) var2.getTagCompound().copy());
                        }

                        float var8 = 0.05F;
                        entityitem.motionX = (double) ((float) this.rand.nextGaussian() * var8);
                        entityitem.motionY = (double) ((float) this.rand.nextGaussian() * var8 + 0.2F);
                        entityitem.motionZ = (double) ((float) this.rand.nextGaussian() * var8);
                        this.worldObj.spawnEntityInWorld(entityitem);
                    }
                }
            }
        }

        super.setDead();
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to
     * teleport to
     */
    @Override
    public void travelToDimension (int par1)
    {
        this.field_82345_h = false;
        super.travelToDimension(par1);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void onUpdate ()
    {
        if (this.getRollingAmplitude() > 0)
        {
            this.setRollingAmplitude(this.getRollingAmplitude() - 1);
        }

        if (this.getDamage() > 0)
        {
            this.setDamage(this.getDamage() - 1);
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        if (this.isMinecartPowered() && this.rand.nextInt(4) == 0 && pullcartType == 2 && getClass() == CartEntity.class)
        {
            this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        int var2;

        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer)
        {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer var1 = ((WorldServer) this.worldObj).func_73046_m();
            var2 = this.getMaxInPortalTime();

            if (this.inPortal)
            {
                if (var1.getAllowNether())
                {
                    /*
                     * if (this.ridingEntity == null && this.timeInPortal++ >=
                     * var2) { this.timeInPortal = var2; this.timeUntilPortal =
                     * this.getPortalCooldown(); byte var3;
                     * 
                     * if (this.worldObj.provider.dimensionId == -1) { var3 = 0;
                     * } else { var3 = -1; }
                     * 
                     * this.travelToDimension(var3); }
                     * 
                     * this.inPortal = false;
                     */
                }
            }
            else
            {
                /*
                 * if (this.timeInPortal > 0) { this.timeInPortal -= 4; }
                 * 
                 * if (this.timeInPortal < 0) { this.timeInPortal = 0; }
                 */
            }

            if (this.timeUntilPortal > 0)
            {
                --this.timeUntilPortal;
            }

            this.worldObj.theProfiler.endSection();
        }

        if (this.worldObj.isRemote)
        {
            if (this.turnProgress > 0)
            {
                double var46 = this.posX + (this.minecartX - this.posX) / (double) this.turnProgress;
                double var48 = this.posY + (this.minecartY - this.posY) / (double) this.turnProgress;
                double var5 = this.posZ + (this.minecartZ - this.posZ) / (double) this.turnProgress;
                double var7 = MathHelper.wrapAngleTo180_double(this.minecartYaw - (double) this.rotationYaw);
                this.rotationYaw = (float) ((double) this.rotationYaw + var7 / (double) this.turnProgress);
                this.rotationPitch = (float) ((double) this.rotationPitch + (this.minecartPitch - (double) this.rotationPitch) / (double) this.turnProgress);
                --this.turnProgress;
                this.setPosition(var46, var48, var5);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
            else
            {
                this.setPosition(this.posX, this.posY, this.posZ);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            }
        }
        else
        {
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            this.motionY -= 0.03999999910593033D;
            int var45 = MathHelper.floor_double(this.posX);
            var2 = MathHelper.floor_double(this.posY);
            int var47 = MathHelper.floor_double(this.posZ);

            if (BlockRail.func_150049_b_(this.worldObj, var45, var2 - 1, var47))
            {
                --var2;
            }

            double var4 = 0.4D;
            double var6 = 0.0078125D;
            Block var8 = this.worldObj.getBlock(var45, var2, var47);

            moveMinecartOffRail(var45, var2, var47);
            if (entityFollowing != null)
                moveTowardsEntity(entityFollowing);

            this.func_145775_I();
            this.rotationPitch = 0.0F;
            double var49 = this.prevPosX - this.posX;
            double var50 = this.prevPosZ - this.posZ;

            if (var49 * var49 + var50 * var50 > 0.001D)
            {
                this.rotationYaw = (float) (Math.atan2(var50, var49) * 180.0D / Math.PI);

                if (this.isInReverse)
                {
                    this.rotationYaw += 180.0F;
                }
            }

            double var51 = (double) MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);

            if (var51 < -170.0D || var51 >= 170.0D)
            {
                this.rotationYaw += 180.0F;
                this.isInReverse = !this.isInReverse;
            }

            this.setRotation(this.rotationYaw, this.rotationPitch);

            AxisAlignedBB box = boundingBox.expand(0.2D, 0.0D, 0.2D);

            List var15 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, box);

            if (var15 != null && !var15.isEmpty())
            {
                for (int var52 = 0; var52 < var15.size(); ++var52)
                {
                    Entity var17 = (Entity) var15.get(var52);

                    if (var17 != this.riddenByEntity && var17.canBePushed() && var17 instanceof CartEntity)
                    {
                        var17.applyEntityCollision(this);
                    }
                }
            }

            if (this.riddenByEntity != null && this.riddenByEntity.isDead)
            {
                if (this.riddenByEntity.ridingEntity == this)
                {
                    this.riddenByEntity.ridingEntity = null;
                }

                this.riddenByEntity = null;
            }

            updateFuel();
        }
    }

    void moveTowardsEntity (Entity entity)
    {
        // TConstruct.logger.info("Moving towards entity");
        /*
         * double distX = this.posX - entity.posX; double distZ = this.posZ -
         * entity.posZ; //posY = posY + 0.1;
         * 
         * if (entity.posY > this.posY) motionY += 0.1; if (distX > 0.5 && distZ
         * > 0.5) { motionX = -distX; motionZ = -distZ; }
         */

        double var15 = entity.posX;
        double var19 = entity.posZ;

        if (this.calculateDistance(this.prevPosX, var15) > 1.5D || this.calculateDistance(this.prevPosZ, var19) > 1.5D)
        {
            double var28 = this.prevPosX;
            double var30 = this.prevPosZ;
            double var36 = this.calculateDistance(var28, var15);
            double var38 = this.calculateDistance(var30, var19);
            double var40 = 1.0D;
            double var42 = 1.0D;
            double var46;
            double var44;

            if (var36 > var38)
            {
                var46 = 1.0D;
                var44 = var38 / var36;
                var42 = var44 / 3.0D;
                var40 = var46 / 3.0D;
            }
            else if (var38 > var36)
            {
                var44 = 1.0D;
                var46 = var36 / var38;
                var40 = var46 / 3.0D;
                var42 = var44 / 3.0D;
            }

            if (var15 > var28)
            {
                this.motionX = var40;// * var26;
            }
            else
            {
                this.motionX = -var40;// * var26;
            }

            if (var19 > var30)
            {
                this.motionZ = var42;// * var26;
            }
            else
            {
                this.motionZ = -var42;// * var26;
            }
        }

        /*
         * if (entity.posY > this.posY) motionY += 0.1;
         */
    }

    public double calculateDistance (double pos1, double pos2)
    {
        double distance = Math.abs(pos1 - pos2); // 0.0D;
        /*
         * double absPos1 = Math.abs(pos1); double absPos2 = Math.abs(pos2);
         * 
         * if ((pos1 <= 0.0D || pos2 <= 0.0D) && (pos1 >= 0.0D || pos2 >= 0.0D))
         * { distance = absPos1 + absPos2; } else if (absPos1 > absPos2) {
         * distance = absPos1 - absPos2; } else if (absPos2 > absPos1) {
         * distance = absPos2 - absPos1; }
         */

        return distance;
    }

    @SideOnly(Side.CLIENT)
    public Vec3 func_70495_a (double par1, double par3, double par5, double par7)
    {
        int var9 = MathHelper.floor_double(par1);
        int var10 = MathHelper.floor_double(par3);
        int var11 = MathHelper.floor_double(par5);

        if (BlockRail.func_150049_b_(this.worldObj, var9, var10 - 1, var11))
        {
            --var10;
        }

        Block var12 = this.worldObj.getBlock(var9, var10, var11);

        return null;
    }

    public Vec3 func_70489_a (double par1, double par3, double par5)
    {
        int var7 = MathHelper.floor_double(par1);
        int var8 = MathHelper.floor_double(par3);
        int var9 = MathHelper.floor_double(par5);

        if (BlockRail.func_150049_b_(this.worldObj, var7, var8 - 1, var9))
        {
            --var8;
        }

        Block var10 = this.worldObj.getBlock(var7, var8, var9);

        if (BlockRail.func_150051_a(var10))
        {
            int var11 = 0;
            par3 = (double) var8;

            if (var11 >= 2 && var11 <= 5)
            {
                par3 = (double) (var8 + 1);
            }

            int[][] var12 = matrix[var11];
            double var13 = 0.0D;
            double var15 = (double) var7 + 0.5D + (double) var12[0][0] * 0.5D;
            double var17 = (double) var8 + 0.5D + (double) var12[0][1] * 0.5D;
            double var19 = (double) var9 + 0.5D + (double) var12[0][2] * 0.5D;
            double var21 = (double) var7 + 0.5D + (double) var12[1][0] * 0.5D;
            double var23 = (double) var8 + 0.5D + (double) var12[1][1] * 0.5D;
            double var25 = (double) var9 + 0.5D + (double) var12[1][2] * 0.5D;
            double var27 = var21 - var15;
            double var29 = (var23 - var17) * 2.0D;
            double var31 = var25 - var19;

            if (var27 == 0.0D)
            {
                par1 = (double) var7 + 0.5D;
                var13 = par5 - (double) var9;
            }
            else if (var31 == 0.0D)
            {
                par5 = (double) var9 + 0.5D;
                var13 = par1 - (double) var7;
            }
            else
            {
                double var33 = par1 - var15;
                double var35 = par5 - var19;
                var13 = (var33 * var27 + var35 * var31) * 2.0D;
            }

            par1 = var15 + var27 * var13;
            par3 = var17 + var29 * var13;
            par5 = var19 + var31 * var13;

            if (var29 < 0.0D)
            {
                ++par3;
            }

            if (var29 > 0.0D)
            {
                par3 += 0.5D;
            }

            return Vec3.createVectorHelper(par1, par3, par5);
        }
        else
        {
            return null;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    protected void writeEntityToNBT (NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("Type", pullcartType);

        if (isPoweredCart())
        {
            par1NBTTagCompound.setDouble("PushX", this.pushX);
            par1NBTTagCompound.setDouble("PushZ", this.pushZ);
            par1NBTTagCompound.setInteger("Fuel", this.fuel);
        }

        if (getSizeInventory() > 0)
        {
            NBTTagList var2 = new NBTTagList();

            for (int var3 = 0; var3 < this.cargoItems.length; ++var3)
            {
                if (this.cargoItems[var3] != null)
                {
                    NBTTagCompound var4 = new NBTTagCompound();
                    var4.setByte("Slot", (byte) var3);
                    this.cargoItems[var3].writeToNBT(var4);
                    var2.appendTag(var4);
                }
            }

            par1NBTTagCompound.setTag("Items", var2);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    protected void readEntityFromNBT (NBTTagCompound par1NBTTagCompound)
    {
        pullcartType = par1NBTTagCompound.getInteger("Type");

        if (isPoweredCart())
        {
            this.pushX = par1NBTTagCompound.getDouble("PushX");
            this.pushZ = par1NBTTagCompound.getDouble("PushZ");
            try
            {
                this.fuel = par1NBTTagCompound.getInteger("Fuel");
            }
            catch (ClassCastException e)
            {
                this.fuel = par1NBTTagCompound.getShort("Fuel");
            }
        }

        if (getSizeInventory() > 0)
        {
            NBTTagList var2 = par1NBTTagCompound.getTagList("Items", 10);
            this.cargoItems = new ItemStack[this.getSizeInventory()];

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                NBTTagCompound var4 = (NBTTagCompound) var2.getCompoundTagAt(var3);
                int var5 = var4.getByte("Slot") & 255;

                if (var5 >= 0 && var5 < this.cargoItems.length)
                {
                    this.cargoItems[var5] = ItemStack.loadItemStackFromNBT(var4);
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getShadowSize ()
    {
        return 0.5F;
    }

    /**
     * Applies a velocity to each of the entities pushing them away from each
     * other. Args: entity
     */
    @Override
    public void applyEntityCollision (Entity par1Entity)
    {
        if (!this.worldObj.isRemote)
        {
            if (par1Entity != this.riddenByEntity)
            {
                if (par1Entity instanceof EntityLiving && !(par1Entity instanceof EntityPlayer) && !(par1Entity instanceof EntityIronGolem) && canBeRidden() && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D && this.riddenByEntity == null && par1Entity.ridingEntity == null)
                {
                    par1Entity.mountEntity(this);
                }

                double var2 = par1Entity.posX - this.posX;
                double var4 = par1Entity.posZ - this.posZ;
                double var6 = var2 * var2 + var4 * var4;

                if (var6 >= 9.999999747378752E-5D)
                {
                    var6 = (double) MathHelper.sqrt_double(var6);
                    var2 /= var6;
                    var4 /= var6;
                    double var8 = 1.0D / var6;

                    if (var8 > 1.0D)
                    {
                        var8 = 1.0D;
                    }

                    var2 *= var8;
                    var4 *= var8;
                    var2 *= 0.10000000149011612D;
                    var4 *= 0.10000000149011612D;
                    var2 *= (double) (1.0F - this.entityCollisionReduction);
                    var4 *= (double) (1.0F - this.entityCollisionReduction);
                    var2 *= 0.5D;
                    var4 *= 0.5D;

                    if (par1Entity instanceof CartEntity)
                    {
                        double var10 = par1Entity.posX - this.posX;
                        double var12 = par1Entity.posZ - this.posZ;
                        Vec3 var14 = Vec3.createVectorHelper(var10, 0.0D, var12).normalize();
                        Vec3 var15 = Vec3.createVectorHelper((double) MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F), 0.0D, (double) MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F)).normalize();
                        double var16 = Math.abs(var14.dotProduct(var15));

                        if (var16 < 0.8D)
                        {
                            return;
                        }

                        double var18 = par1Entity.motionX + this.motionX;
                        double var20 = par1Entity.motionZ + this.motionZ;

                        if (((CartEntity) par1Entity).isPoweredCart() && !isPoweredCart())
                        {
                            this.motionX *= 0.20000000298023224D;
                            this.motionZ *= 0.20000000298023224D;
                            this.addVelocity(par1Entity.motionX - var2, 0.0D, par1Entity.motionZ - var4);
                            par1Entity.motionX *= 0.949999988079071D;
                            par1Entity.motionZ *= 0.949999988079071D;
                        }
                        else if (!((CartEntity) par1Entity).isPoweredCart() && isPoweredCart())
                        {
                            par1Entity.motionX *= 0.20000000298023224D;
                            par1Entity.motionZ *= 0.20000000298023224D;
                            par1Entity.addVelocity(this.motionX + var2, 0.0D, this.motionZ + var4);
                            this.motionX *= 0.949999988079071D;
                            this.motionZ *= 0.949999988079071D;
                        }
                        else
                        {
                            var18 /= 2.0D;
                            var20 /= 2.0D;
                            this.motionX *= 0.20000000298023224D;
                            this.motionZ *= 0.20000000298023224D;
                            this.addVelocity(var18 - var2, 0.0D, var20 - var4);
                            par1Entity.motionX *= 0.20000000298023224D;
                            par1Entity.motionZ *= 0.20000000298023224D;
                            par1Entity.addVelocity(var18 + var2, 0.0D, var20 + var4);
                        }
                    }
                    else
                    {
                        this.addVelocity(-var2, 0.0D, -var4);
                        par1Entity.addVelocity(var2 / 4.0D, 0.0D, var4 / 4.0D);
                    }
                }
            }
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory ()
    {
        return (pullcartType == 1 && getClass() == CartEntity.class ? 27 : 0);
    }

    /**
     * Returns the stack in slot i
     */
    @Override
    public ItemStack getStackInSlot (int par1)
    {
        return this.cargoItems[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number
     * (second arg) of items and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize (int par1, int par2)
    {
        if (this.cargoItems[par1] != null)
        {
            ItemStack var3;

            if (this.cargoItems[par1].stackSize <= par2)
            {
                var3 = this.cargoItems[par1];
                this.cargoItems[par1] = null;
                return var3;
            }
            else
            {
                var3 = this.cargoItems[par1].splitStack(par2);

                if (this.cargoItems[par1].stackSize == 0)
                {
                    this.cargoItems[par1] = null;
                }

                return var3;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem - like when you close a workbench
     * GUI.
     */
    @Override
    public ItemStack getStackInSlotOnClosing (int par1)
    {
        if (this.cargoItems[par1] != null)
        {
            ItemStack var2 = this.cargoItems[par1];
            this.cargoItems[par1] = null;
            return var2;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents (int par1, ItemStack par2ItemStack)
    {
        this.cargoItems[par1] = par2ItemStack;

        if (par2ItemStack != null && par2ItemStack.stackSize > this.getInventoryStackLimit())
        {
            par2ItemStack.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory.
     */
    public String getInvName ()
    {
        return "container.minecart";
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be
     * 64, possibly will be extended. *Isn't this more of a set than a get?*
     */
    @Override
    public int getInventoryStackLimit ()
    {
        return 64;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    @Override
    public void markDirty ()
    {
    }

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow,
     * gets into the saddle on a pig.
     */
    public boolean interact (EntityPlayer player)
    {
        if (player.isSneaking())
        {
            if (entityFollowing == null)
            {
                entityFollowing = player;
                // if (!worldObj.isRemote)
                // player.addChatMessage("The cart is following you");
            }
            else
            {
                entityFollowing = null;
                // if (!worldObj.isRemote)
                // player.addChatMessage("The cart has stopped following you");
            }
            return true;
        }
        if (canBeRidden())
        {
            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != player)
            {
                return true;
            }

            if (!this.worldObj.isRemote)
            {
                player.mountEntity(this);
            }
        }
        else if (getSizeInventory() > 0)
        {
            if (!this.worldObj.isRemote)
            {
                player.displayGUIChest(this);
            }
        }
        else if (this.pullcartType == 2 && getClass() == CartEntity.class)
        {
            ItemStack var2 = player.inventory.getCurrentItem();

            if (var2 != null && var2.getItem() == Items.coal)
            {
                if (--var2.stackSize == 0)
                {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack) null);
                }

                this.fuel += 3600;
            }

            this.pushX = this.posX - player.posX;
            this.pushZ = this.posZ - player.posZ;
        }

        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2 (double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.minecartX = par1;
        this.minecartY = par3;
        this.minecartZ = par5;
        this.minecartYaw = (double) par7;
        this.minecartPitch = (double) par8;
        this.turnProgress = par9 + 2;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity (double par1, double par3, double par5)
    {
        this.velocityX = this.motionX = par1;
        this.velocityY = this.motionY = par3;
        this.velocityZ = this.motionZ = par5;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes
     * with Container
     */
    @Override
    public boolean isUseableByPlayer (EntityPlayer par1EntityPlayer)
    {
        return this.isDead ? false : par1EntityPlayer.getDistanceSqToEntity(this) <= 64.0D;
    }

    /**
     * Is this minecart powered (Fuel > 0)
     */
    public boolean isMinecartPowered ()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    /**
     * Set if this minecart is powered (Fuel > 0)
     */
    protected void setMinecartPowered (boolean par1)
    {
        if (par1)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (this.dataWatcher.getWatchableObjectByte(16) | 1)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (this.dataWatcher.getWatchableObjectByte(16) & -2)));
        }
    }

    public void openChest ()
    {
    }

    public void closeChest ()
    {
    }

    /**
     * Sets the current amount of damage the minecart has taken. Decreases over
     * time. The cart breaks when this is over 40.
     */
    public void setDamage (int par1)
    {
        this.dataWatcher.updateObject(19, Integer.valueOf(par1));
    }

    /**
     * Gets the current amount of damage the minecart has taken. Decreases over
     * time. The cart breaks when this is over 40.
     */
    public int getDamage ()
    {
        return this.dataWatcher.getWatchableObjectInt(19);
    }

    public void setRollingAmplitude (int par1)
    {
        this.dataWatcher.updateObject(17, Integer.valueOf(par1));
    }

    public int getRollingAmplitude ()
    {
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    public void setRollingDirection (int par1)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(par1));
    }

    public int getRollingDirection ()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    /**
     * Drops the cart as a item. The exact item dropped is defined by
     * getItemDropped().
     */
    public void dropCartAsItem ()
    {
        for (ItemStack item : getItemsDropped())
        {
            entityDropItem(item, 0);
        }
    }

    /**
     * Override this to define which items your cart drops when broken. This
     * does not include items contained in the inventory, that is handled
     * elsewhere.
     * 
     * @return A list of items dropped.
     */
    public List<ItemStack> getItemsDropped ()
    {
        /*
         * List<ItemStack> items = new ArrayList<ItemStack>(); items.add(new
         * ItemStack(Item.minecartEmpty));
         * 
         * switch(cartType) { case 1: items.add(new ItemStack(Block.chest));
         * break; case 2: items.add(new ItemStack(Block.stoneOvenIdle)); break;
         * } return items;
         */
        return new ArrayList<ItemStack>();
    }

    /**
     * This function returns an ItemStack that represents this cart. This should
     * be an ItemStack that can be used by the player to place the cart. This is
     * the item that was registered with the cart via the registerMinecart
     * function, but is not necessary the item the cart drops when destroyed.
     * 
     * @return An ItemStack that can be used to place the cart.
     */
    public ItemStack getCartItem ()
    {
        return null;
        // return MinecartRegistry.getItemForCart(this);
    }

    /**
     * Returns true if this cart is self propelled.
     * 
     * @return True if powered.
     */
    public boolean isPoweredCart ()
    {
        return pullcartType == 2 && getClass() == CartEntity.class;
    }

    /**
     * Returns true if this cart is a storage cart Some carts may have
     * inventories but not be storage carts and some carts without inventories
     * may be storage carts.
     * 
     * @return True if this cart should be classified as a storage cart.
     */
    public boolean isStorageCart ()
    {
        return pullcartType == 1 && getClass() == CartEntity.class;
    }

    /**
     * Returns true if this cart can be ridden by an Entity.
     * 
     * @return True if this cart can be ridden.
     */
    public boolean canBeRidden ()
    {
        if (pullcartType == 0 && getClass() == CartEntity.class)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns true if this cart can currently use rails. This function is
     * mainly used to gracefully detach a minecart from a rail.
     * 
     * @return True if the minecart can use rails.
     */
    public boolean canUseRail ()
    {
        return false;
    }

    /**
     * Set whether the minecart can use rails. This function is mainly used to
     * gracefully detach a minecart from a rail.
     * 
     * @param use
     *            Whether the minecart can currently use rails.
     */
    public void setCanUseRail (boolean use)
    {
        canUseRail = use;
    }

    /**
     * Return false if this cart should not call IRail.onMinecartPass() and
     * should ignore Powered Rails.
     * 
     * @return True if this cart should call IRail.onMinecartPass().
     */
    public boolean shouldDoRailFunctions ()
    {
        return true;
    }

    /**
     * Simply returns the minecartType variable.
     * 
     * @return minecartType
     */
    public int getCartType ()
    {
        return pullcartType;
    }

    /**
     * Carts should return their drag factor here
     * 
     * @return The drag rate.
     */
    protected double getDrag ()
    {
        return riddenByEntity != null ? defaultDragRidden : defaultDragEmpty;
    }

    /**
     * Moved to allow overrides. This code applies drag and updates push forces.
     */
    protected void applyDragAndPushForces ()
    {
        if (isPoweredCart())
        {
            double d27 = MathHelper.sqrt_double(pushX * pushX + pushZ * pushZ);
            if (d27 > 0.01D)
            {
                pushX /= d27;
                pushZ /= d27;
                double d29 = 0.04;
                motionX *= 0.8D;
                motionY *= 0.0D;
                motionZ *= 0.8D;
                motionX += pushX * d29;
                motionZ += pushZ * d29;
            }
            else
            {
                motionX *= 0.9D;
                motionY *= 0.0D;
                motionZ *= 0.9D;
            }
        }
        motionX *= getDrag();
        motionY *= 0.0D;
        motionZ *= getDrag();
    }

    /**
     * Moved to allow overrides. This code updates push forces.
     */
    protected void updatePushForces ()
    {
        if (isPoweredCart())
        {
            double push = MathHelper.sqrt_double(pushX * pushX + pushZ * pushZ);
            if (push > 0.01D && motionX * motionX + motionZ * motionZ > 0.001D)
            {
                pushX /= push;
                pushZ /= push;
                if (pushX * motionX + pushZ * motionZ < 0.0D)
                {
                    pushX = 0.0D;
                    pushZ = 0.0D;
                }
                else
                {
                    pushX = motionX;
                    pushZ = motionZ;
                }
            }
        }
    }

    /**
     * Moved to allow overrides. This code handles minecart movement and speed
     * capping when not on a rail.
     */
    protected void moveMinecartOffRail (int i, int j, int k)
    {
        double d2 = getMaxSpeedGround();
        if (!onGround)
        {
            d2 = getMaxSpeedAirLateral();
        }
        if (motionX < -d2)
            motionX = -d2;
        if (motionX > d2)
            motionX = d2;
        if (motionZ < -d2)
            motionZ = -d2;
        if (motionZ > d2)
            motionZ = d2;
        double moveY = motionY;
        if (getMaxSpeedAirVertical() > 0 && motionY > getMaxSpeedAirVertical())
        {
            moveY = getMaxSpeedAirVertical();
            if (Math.abs(motionX) < 0.3f && Math.abs(motionZ) < 0.3f)
            {
                moveY = 0.15f;
                motionY = moveY;
            }
        }
        if (onGround)
        {
            motionX *= 0.5D;
            motionY *= 0.5D;
            motionZ *= 0.5D;
        }
        moveEntity(motionX, moveY, motionZ);
        if (!onGround)
        {
            motionX *= getDragAir();
            motionY *= getDragAir();
            motionZ *= getDragAir();
        }
    }

    /**
     * Moved to allow overrides. This code applies fuel consumption.
     */
    protected void updateFuel ()
    {
        if (fuel > 0)
            fuel--;
        if (fuel <= 0)
            pushX = pushZ = 0.0D;
        setMinecartPowered(fuel > 0);
    }

    /**
     * Moved to allow overrides, This code handle slopes affecting velocity.
     * 
     * @param metadata
     *            The blocks position metadata
     */
    protected void adjustSlopeVelocities (int metadata)
    {
        double acceleration = 0.0078125D;
        if (metadata == 2)
        {
            motionX -= acceleration;
        }
        else if (metadata == 3)
        {
            motionX += acceleration;
        }
        else if (metadata == 4)
        {
            motionZ += acceleration;
        }
        else if (metadata == 5)
        {
            motionZ -= acceleration;
        }
    }

    /**
     * Getters/setters for physics variables
     */

    /**
     * Returns the carts max speed. Carts going faster than 1.1 cause issues
     * with chunk loading. Carts cant traverse slopes or corners at greater than
     * 0.5 - 0.6. This value is compared with the rails max speed to determine
     * the carts current max speed. A normal rails max speed is 0.4.
     * 
     * @return Carts max speed.
     */
    public float getMaxSpeedRail ()
    {
        return maxSpeedRail;
    }

    public void setMaxSpeedRail (float value)
    {
        maxSpeedRail = value;
    }

    public float getMaxSpeedGround ()
    {
        return maxSpeedGround;
    }

    public void setMaxSpeedGround (float value)
    {
        maxSpeedGround = value;
    }

    public float getMaxSpeedAirLateral ()
    {
        return maxSpeedAirLateral;
    }

    public void setMaxSpeedAirLateral (float value)
    {
        maxSpeedAirLateral = value;
    }

    public float getMaxSpeedAirVertical ()
    {
        return maxSpeedAirVertical;
    }

    public void setMaxSpeedAirVertical (float value)
    {
        maxSpeedAirVertical = value;
    }

    public double getDragAir ()
    {
        return dragAir;
    }

    public void setDragAir (double value)
    {
        dragAir = value;
    }

    @Override
    public void writeSpawnData (ByteBuf data)
    {
        data.writeInt(pullcartType);

    }

    @Override
    public void readSpawnData (ByteBuf data)
    {
        pullcartType = data.readInt();

    }

    @Override
    public boolean isItemValidForSlot (int i, ItemStack itemstack)
    {
        return false;
    }

    @Override
    public String getInventoryName ()
    {
        // TODO get inventory name
        return null;
    }

    @Override
    public boolean hasCustomInventoryName ()
    {
        return false;
    }

    @Override
    public void openInventory ()
    {
    }

    @Override
    public void closeInventory ()
    {
    }
}

package tinker.tconstruct.entity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.IMinecartCollisionHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.minecart.MinecartCollisionEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityWagon extends Entity implements IInventory
{
    /** Array of item stacks stored in minecart (for storage minecarts). */
    protected ItemStack[] cargoItems;
    protected int fuel;
    protected boolean field_70499_f;

    /** The type of minecart, 2 for powered, 1 for storage. */
    public int minecartType;
    public double pushX;
    public double pushZ;
    protected boolean field_82345_h;

    /** Minecart rotational logic matrix */
    protected static final int[][][] matrix = new int[][][] {{{0, 0, -1}, {0, 0, 1}}, {{ -1, 0, 0}, {1, 0, 0}}, {{ -1, -1, 0}, {1, 0, 0}}, {{ -1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, { -1, 0, 0}}, {{0, 0, -1}, { -1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};

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
    private static IMinecartCollisionHandler collisionHandler = null;

    /* Instance versions of the above physics properties */
    protected float maxSpeedRail;
    protected float maxSpeedGround;
    protected float maxSpeedAirLateral;
    protected float maxSpeedAirVertical;
    protected double dragAir;

    public EntityWagon(World par1World)
    {
        super(par1World);
        this.cargoItems = new ItemStack[36];
        this.fuel = 0;
        this.field_70499_f = false;
        this.field_82345_h = true;
        this.preventEntitySpawning = true;
        this.setSize(0.98F, 0.7F);
        this.yOffset = this.height / 2.0F;

        maxSpeedRail = defaultMaxSpeedRail;
        maxSpeedGround = defaultMaxSpeedGround;
        maxSpeedAirLateral = defaultMaxSpeedAirLateral;
        maxSpeedAirVertical = defaultMaxSpeedAirVertical;
        dragAir = defaultDragAir;
    }

    public EntityWagon(World world, int type)
    {
        this(world);
        minecartType = type;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking()
    {
        return false;
    }

    protected void entityInit()
    {
        this.dataWatcher.addObject(16, new Byte((byte)0));
        this.dataWatcher.addObject(17, new Integer(0));
        this.dataWatcher.addObject(18, new Integer(1));
        this.dataWatcher.addObject(19, new Integer(0));
    }

    /**
     * Returns a boundingBox used to collide the entity with other entities and blocks. This enables the entity to be
     * pushable on contact, like boats or minecarts.
     */
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return par1Entity.boundingBox;
    }

    /**
     * returns the bounding box for this entity
     */
    public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return canBePushed;
    }

    public EntityWagon(World par1World, double par2, double par4, double par6, int par8)
    {
        this(par1World);
        this.setPosition(par2, par4 + (double)this.yOffset, par6);
        this.motionX = 0.0D;
        this.motionY = 0.0D;
        this.motionZ = 0.0D;
        this.prevPosX = par2;
        this.prevPosY = par4;
        this.prevPosZ = par6;
        this.minecartType = par8;
    }

    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height * 0.0D - 0.30000001192092896D;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource par1DamageSource, int par2)
    {
        if (!this.worldObj.isRemote && !this.isDead)
        {
            if (this.isEntityInvulnerable())
            {
                return false;
            }
            else
            {
                this.func_70494_i(-this.func_70493_k());
                this.func_70497_h(10);
                this.setBeenAttacked();
                this.setDamage(this.getDamage() + par2 * 10);

                if (par1DamageSource.getEntity() instanceof EntityPlayer && ((EntityPlayer)par1DamageSource.getEntity()).capabilities.isCreativeMode)
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

    @SideOnly(Side.CLIENT)

    /**
     * Setups the entity to do the hurt animation. Only used by packets in multiplayer.
     */
    public void performHurtAnimation()
    {
        this.func_70494_i(-this.func_70493_k());
        this.func_70497_h(10);
        this.setDamage(this.getDamage() + this.getDamage() * 10);
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return !this.isDead;
    }

    /**
     * Will get destroyed next tick.
     */
    public void setDead()
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
                        EntityItem var7 = new EntityItem(this.worldObj, this.posX + (double)var3, this.posY + (double)var4, this.posZ + (double)var5, new ItemStack(var2.itemID, var6, var2.getItemDamage()));

                        if (var2.hasTagCompound())
                        {
                            var7.func_92014_d().setTagCompound((NBTTagCompound)var2.getTagCompound().copy());
                        }

                        float var8 = 0.05F;
                        var7.motionX = (double)((float)this.rand.nextGaussian() * var8);
                        var7.motionY = (double)((float)this.rand.nextGaussian() * var8 + 0.2F);
                        var7.motionZ = (double)((float)this.rand.nextGaussian() * var8);
                        this.worldObj.spawnEntityInWorld(var7);
                    }
                }
            }
        }

        super.setDead();
    }

    /**
     * Teleports the entity to another dimension. Params: Dimension number to teleport to
     */
    public void travelToDimension(int par1)
    {
        this.field_82345_h = false;
        super.travelToDimension(par1);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate()
    {

        if (this.func_70496_j() > 0)
        {
            this.func_70497_h(this.func_70496_j() - 1);
        }

        if (this.getDamage() > 0)
        {
            this.setDamage(this.getDamage() - 1);
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        if (this.isMinecartPowered() && this.rand.nextInt(4) == 0 && minecartType == 2 && getClass() == EntityWagon.class)
        {
            this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
        }

        int var2;

        if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer)
        {
            this.worldObj.theProfiler.startSection("portal");
            MinecraftServer var1 = ((WorldServer)this.worldObj).getMinecraftServer();
            var2 = this.getMaxInPortalTime();

            if (this.inPortal)
            {
                if (var1.getAllowNether())
                {
                    if (this.ridingEntity == null && this.field_82153_h++ >= var2)
                    {
                        this.field_82153_h = var2;
                        this.timeUntilPortal = this.getPortalCooldown();
                        byte var3;

                        if (this.worldObj.provider.dimensionId == -1)
                        {
                            var3 = 0;
                        }
                        else
                        {
                            var3 = -1;
                        }

                        this.travelToDimension(var3);
                    }

                    this.inPortal = false;
                }
            }
            else
            {
                if (this.field_82153_h > 0)
                {
                    this.field_82153_h -= 4;
                }

                if (this.field_82153_h < 0)
                {
                    this.field_82153_h = 0;
                }
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
                double var46 = this.posX + (this.minecartX - this.posX) / (double)this.turnProgress;
                double var48 = this.posY + (this.minecartY - this.posY) / (double)this.turnProgress;
                double var5 = this.posZ + (this.minecartZ - this.posZ) / (double)this.turnProgress;
                double var7 = MathHelper.wrapAngleTo180_double(this.minecartYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + var7 / (double)this.turnProgress);
                this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) / (double)this.turnProgress);
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

            if (BlockRail.isRailBlockAt(this.worldObj, var45, var2 - 1, var47))
            {
                --var2;
            }

            double var4 = 0.4D;
            double var6 = 0.0078125D;
            int var8 = this.worldObj.getBlockId(var45, var2, var47);

            /*if (canUseRail() && BlockRail.isRailBlock(var8))
            {
                this.fallDistance = 0.0F;
                Vec3 var9 = this.func_70489_a(this.posX, this.posY, this.posZ);
                int var10 = ((BlockRail)Block.blocksList[var8]).getBasicRailMetadata(worldObj, this, var45, var2, var47);
                this.posY = (double)var2;
                boolean var11 = false;
                boolean var12 = false;

                if (var8 == Block.railPowered.blockID)
                {
                    var11 = (worldObj.getBlockMetadata(var45, var2, var47) & 8) != 0;
                    var12 = !var11;
                }

                if (((BlockRail)Block.blocksList[var8]).isPowered())
                {
                    var10 &= 7;
                }

                if (var10 >= 2 && var10 <= 5)
                {
                    this.posY = (double)(var2 + 1);
                }

                adjustSlopeVelocities(var10);

                int[][] var13 = matrix[var10];
                double var14 = (double)(var13[1][0] - var13[0][0]);
                double var16 = (double)(var13[1][2] - var13[0][2]);
                double var18 = Math.sqrt(var14 * var14 + var16 * var16);
                double var20 = this.motionX * var14 + this.motionZ * var16;

                if (var20 < 0.0D)
                {
                    var14 = -var14;
                    var16 = -var16;
                }

                double var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                this.motionX = var22 * var14 / var18;
                this.motionZ = var22 * var16 / var18;
                double var24;
                double var26;

                if (this.riddenByEntity != null)
                {
                    var24 = this.riddenByEntity.motionX * this.riddenByEntity.motionX + this.riddenByEntity.motionZ * this.riddenByEntity.motionZ;
                    var26 = this.motionX * this.motionX + this.motionZ * this.motionZ;

                    if (var24 > 1.0E-4D && var26 < 0.01D)
                    {
                        this.motionX += this.riddenByEntity.motionX * 0.1D;
                        this.motionZ += this.riddenByEntity.motionZ * 0.1D;
                        var12 = false;
                    }
                }

                if (var12 && shouldDoRailFunctions())
                {
                    var24 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                    if (var24 < 0.03D)
                    {
                        this.motionX *= 0.0D;
                        this.motionY *= 0.0D;
                        this.motionZ *= 0.0D;
                    }
                    else
                    {
                        this.motionX *= 0.5D;
                        this.motionY *= 0.0D;
                        this.motionZ *= 0.5D;
                    }
                }

                var24 = 0.0D;
                var26 = (double)var45 + 0.5D + (double)var13[0][0] * 0.5D;
                double var28 = (double)var47 + 0.5D + (double)var13[0][2] * 0.5D;
                double var30 = (double)var45 + 0.5D + (double)var13[1][0] * 0.5D;
                double var32 = (double)var47 + 0.5D + (double)var13[1][2] * 0.5D;
                var14 = var30 - var26;
                var16 = var32 - var28;
                double var34;
                double var36;

                if (var14 == 0.0D)
                {
                    this.posX = (double)var45 + 0.5D;
                    var24 = this.posZ - (double)var47;
                }
                else if (var16 == 0.0D)
                {
                    this.posZ = (double)var47 + 0.5D;
                    var24 = this.posX - (double)var45;
                }
                else
                {
                    var34 = this.posX - var26;
                    var36 = this.posZ - var28;
                    var24 = (var34 * var14 + var36 * var16) * 2.0D;
                }

                this.posX = var26 + var14 * var24;
                this.posZ = var28 + var16 * var24;
                this.setPosition(this.posX, this.posY + (double)this.yOffset, this.posZ);

                moveMinecartOnRail(var45, var2, var47);

                if (var13[0][1] != 0 && MathHelper.floor_double(this.posX) - var45 == var13[0][0] && MathHelper.floor_double(this.posZ) - var47 == var13[0][2])
                {
                    this.setPosition(this.posX, this.posY + (double)var13[0][1], this.posZ);
                }
                else if (var13[1][1] != 0 && MathHelper.floor_double(this.posX) - var45 == var13[1][0] && MathHelper.floor_double(this.posZ) - var47 == var13[1][2])
                {
                    this.setPosition(this.posX, this.posY + (double)var13[1][1], this.posZ);
                }

                applyDragAndPushForces();

                Vec3 var54 = this.func_70489_a(this.posX, this.posY, this.posZ);

                if (var54 != null && var9 != null)
                {
                    double var39 = (var9.yCoord - var54.yCoord) * 0.05D;
                    var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                    if (var22 > 0.0D)
                    {
                        this.motionX = this.motionX / var22 * (var22 + var39);
                        this.motionZ = this.motionZ / var22 * (var22 + var39);
                    }

                    this.setPosition(this.posX, var54.yCoord, this.posZ);
                }

                int var53 = MathHelper.floor_double(this.posX);
                int var55 = MathHelper.floor_double(this.posZ);

                if (var53 != var45 || var55 != var47)
                {
                    var22 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                    this.motionX = var22 * (double)(var53 - var45);
                    this.motionZ = var22 * (double)(var55 - var47);
                }

                double var41;

                updatePushForces();

                if(shouldDoRailFunctions())
                {
                    ((BlockRail)Block.blocksList[var8]).onMinecartPass(worldObj, this, var45, var2, var47);
                }

                if (var11 && shouldDoRailFunctions())
                {
                    var41 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

                    if (var41 > 0.01D)
                    {
                        double var43 = 0.06D;
                        this.motionX += this.motionX / var41 * var43;
                        this.motionZ += this.motionZ / var41 * var43;
                    }
                    else if (var10 == 1)
                    {
                        if (this.worldObj.isBlockNormalCube(var45 - 1, var2, var47))
                        {
                            this.motionX = 0.02D;
                        }
                        else if (this.worldObj.isBlockNormalCube(var45 + 1, var2, var47))
                        {
                            this.motionX = -0.02D;
                        }
                    }
                    else if (var10 == 0)
                    {
                        if (this.worldObj.isBlockNormalCube(var45, var2, var47 - 1))
                        {
                            this.motionZ = 0.02D;
                        }
                        else if (this.worldObj.isBlockNormalCube(var45, var2, var47 + 1))
                        {
                            this.motionZ = -0.02D;
                        }
                    }
                }
            }*/
            moveMinecartOffRail(var45, var2, var47);

            this.doBlockCollisions();
            this.rotationPitch = 0.0F;
            double var49 = this.prevPosX - this.posX;
            double var50 = this.prevPosZ - this.posZ;

            if (var49 * var49 + var50 * var50 > 0.001D)
            {
                this.rotationYaw = (float)(Math.atan2(var50, var49) * 180.0D / Math.PI);

                if (this.field_70499_f)
                {
                    this.rotationYaw += 180.0F;
                }
            }

            double var51 = (double)MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);

            if (var51 < -170.0D || var51 >= 170.0D)
            {
                this.rotationYaw += 180.0F;
                this.field_70499_f = !this.field_70499_f;
            }

            this.setRotation(this.rotationYaw, this.rotationPitch);

            AxisAlignedBB box = boundingBox.expand(0.2D, 0.0D, 0.2D);

            List var15 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, box);

            if (var15 != null && !var15.isEmpty())
            {
                for (int var52 = 0; var52 < var15.size(); ++var52)
                {
                    Entity var17 = (Entity)var15.get(var52);

                    if (var17 != this.riddenByEntity && var17.canBePushed() && var17 instanceof EntityWagon)
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

    @SideOnly(Side.CLIENT)
    public Vec3 func_70495_a(double par1, double par3, double par5, double par7)
    {
        int var9 = MathHelper.floor_double(par1);
        int var10 = MathHelper.floor_double(par3);
        int var11 = MathHelper.floor_double(par5);

        if (BlockRail.isRailBlockAt(this.worldObj, var9, var10 - 1, var11))
        {
            --var10;
        }

        int var12 = this.worldObj.getBlockId(var9, var10, var11);

        return null;
    }
    
    public Vec3 func_70489_a(double par1, double par3, double par5)
    {
        int var7 = MathHelper.floor_double(par1);
        int var8 = MathHelper.floor_double(par3);
        int var9 = MathHelper.floor_double(par5);

        if (BlockRail.isRailBlockAt(this.worldObj, var7, var8 - 1, var9))
        {
            --var8;
        }

        int var10 = this.worldObj.getBlockId(var7, var8, var9);

        if (BlockRail.isRailBlock(var10))
        {
            int var11 = 0;
            par3 = (double)var8;

            if (var11 >= 2 && var11 <= 5)
            {
                par3 = (double)(var8 + 1);
            }

            int[][] var12 = matrix[var11];
            double var13 = 0.0D;
            double var15 = (double)var7 + 0.5D + (double)var12[0][0] * 0.5D;
            double var17 = (double)var8 + 0.5D + (double)var12[0][1] * 0.5D;
            double var19 = (double)var9 + 0.5D + (double)var12[0][2] * 0.5D;
            double var21 = (double)var7 + 0.5D + (double)var12[1][0] * 0.5D;
            double var23 = (double)var8 + 0.5D + (double)var12[1][1] * 0.5D;
            double var25 = (double)var9 + 0.5D + (double)var12[1][2] * 0.5D;
            double var27 = var21 - var15;
            double var29 = (var23 - var17) * 2.0D;
            double var31 = var25 - var19;

            if (var27 == 0.0D)
            {
                par1 = (double)var7 + 0.5D;
                var13 = par5 - (double)var9;
            }
            else if (var31 == 0.0D)
            {
                par5 = (double)var9 + 0.5D;
                var13 = par1 - (double)var7;
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

            return this.worldObj.getWorldVec3Pool().getVecFromPool(par1, par3, par5);
        }
        else
        {
            return null;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound)
    {
        par1NBTTagCompound.setInteger("Type", this.minecartType);

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
                    var4.setByte("Slot", (byte)var3);
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
    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        this.minecartType = par1NBTTagCompound.getInteger("Type");

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
            NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
            this.cargoItems = new ItemStack[this.getSizeInventory()];

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
                int var5 = var4.getByte("Slot") & 255;

                if (var5 >= 0 && var5 < this.cargoItems.length)
                {
                    this.cargoItems[var5] = ItemStack.loadItemStackFromNBT(var4);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize()
    {
        return 0.0F;
    }

    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(Entity par1Entity)
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
                    var6 = (double)MathHelper.sqrt_double(var6);
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
                    var2 *= (double)(1.0F - this.entityCollisionReduction);
                    var4 *= (double)(1.0F - this.entityCollisionReduction);
                    var2 *= 0.5D;
                    var4 *= 0.5D;

                    if (par1Entity instanceof EntityWagon)
                    {
                        double var10 = par1Entity.posX - this.posX;
                        double var12 = par1Entity.posZ - this.posZ;
                        Vec3 var14 = this.worldObj.getWorldVec3Pool().getVecFromPool(var10, 0.0D, var12).normalize();
                        Vec3 var15 = this.worldObj.getWorldVec3Pool().getVecFromPool((double)MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F), 0.0D, (double)MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F)).normalize();
                        double var16 = Math.abs(var14.dotProduct(var15));

                        if (var16 < 0.800000011920929D)
                        {
                            return;
                        }

                        double var18 = par1Entity.motionX + this.motionX;
                        double var20 = par1Entity.motionZ + this.motionZ;

                        if (((EntityWagon)par1Entity).isPoweredCart() && !isPoweredCart())
                        {
                            this.motionX *= 0.20000000298023224D;
                            this.motionZ *= 0.20000000298023224D;
                            this.addVelocity(par1Entity.motionX - var2, 0.0D, par1Entity.motionZ - var4);
                            par1Entity.motionX *= 0.949999988079071D;
                            par1Entity.motionZ *= 0.949999988079071D;
                        }
                        else if (!((EntityWagon)par1Entity).isPoweredCart() && isPoweredCart())
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
    public int getSizeInventory()
    {
        return (minecartType == 1 && getClass() == EntityWagon.class ? 27 : 0);
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int par1)
    {
        return this.cargoItems[par1];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int par1, int par2)
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
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int par1)
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
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
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
    public String getInvName()
    {
        return "container.minecart";
    }

    /**
     * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
     * this more of a set than a get?*
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged() {}

    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {

        if (canBeRidden())
        {
            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != par1EntityPlayer)
            {
                return true;
            }

            if (!this.worldObj.isRemote)
            {
                par1EntityPlayer.mountEntity(this);
            }
        }
        else if (getSizeInventory() > 0)
        {
            if (!this.worldObj.isRemote)
            {
                par1EntityPlayer.displayGUIChest(this);
            }
        }
        else if (this.minecartType == 2 && getClass() == EntityWagon.class)
        {
            ItemStack var2 = par1EntityPlayer.inventory.getCurrentItem();

            if (var2 != null && var2.itemID == Item.coal.itemID)
            {
                if (--var2.stackSize == 0)
                {
                    par1EntityPlayer.inventory.setInventorySlotContents(par1EntityPlayer.inventory.currentItem, (ItemStack)null);
                }

                this.fuel += 3600;
            }

            this.pushX = this.posX - par1EntityPlayer.posX;
            this.pushZ = this.posZ - par1EntityPlayer.posZ;
        }

        return true;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the position and rotation. Only difference from the other one is no bounding on the rotation. Args: posX,
     * posY, posZ, yaw, pitch
     */
    public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
    {
        this.minecartX = par1;
        this.minecartY = par3;
        this.minecartZ = par5;
        this.minecartYaw = (double)par7;
        this.minecartPitch = (double)par8;
        this.turnProgress = par9 + 2;
        this.motionX = this.velocityX;
        this.motionY = this.velocityY;
        this.motionZ = this.velocityZ;
    }

    @SideOnly(Side.CLIENT)

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double par1, double par3, double par5)
    {
        this.velocityX = this.motionX = par1;
        this.velocityY = this.motionY = par3;
        this.velocityZ = this.motionZ = par5;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer par1EntityPlayer)
    {
        return this.isDead ? false : par1EntityPlayer.getDistanceSqToEntity(this) <= 64.0D;
    }

    /**
     * Is this minecart powered (Fuel > 0)
     */
    public boolean isMinecartPowered()
    {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    /**
     * Set if this minecart is powered (Fuel > 0)
     */
    protected void setMinecartPowered(boolean par1)
    {
        if (par1)
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(this.dataWatcher.getWatchableObjectByte(16) | 1)));
        }
        else
        {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte)(this.dataWatcher.getWatchableObjectByte(16) & -2)));
        }
    }

    public void openChest() {}

    public void closeChest() {}

    /**
     * Sets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
     * 40.
     */
    public void setDamage(int par1)
    {
        this.dataWatcher.updateObject(19, Integer.valueOf(par1));
    }

    /**
     * Gets the current amount of damage the minecart has taken. Decreases over time. The cart breaks when this is over
     * 40.
     */
    public int getDamage()
    {
        return this.dataWatcher.getWatchableObjectInt(19);
    }

    public void func_70497_h(int par1)
    {
        this.dataWatcher.updateObject(17, Integer.valueOf(par1));
    }

    public int func_70496_j()
    {
        return this.dataWatcher.getWatchableObjectInt(17);
    }

    public void func_70494_i(int par1)
    {
        this.dataWatcher.updateObject(18, Integer.valueOf(par1));
    }

    public int func_70493_k()
    {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    /**
     * Drops the cart as a item. The exact item dropped is defined by getItemDropped().
     */
    public void dropCartAsItem()
    {
        for(ItemStack item : getItemsDropped())
        {
            entityDropItem(item, 0);
        }
    }

    /**
     * Override this to define which items your cart drops when broken.
     * This does not include items contained in the inventory,
     * that is handled elsewhere.
     * @return A list of items dropped.
     */
    public List<ItemStack> getItemsDropped()
    {
        List<ItemStack> items = new ArrayList<ItemStack>();
        items.add(new ItemStack(Item.minecartEmpty));

        switch(minecartType)
        {
            case 1:
                items.add(new ItemStack(Block.chest));
                break;
            case 2:
                items.add(new ItemStack(Block.stoneOvenIdle));
                break;
        }
        return items;
    }

    /**
     * This function returns an ItemStack that represents this cart.
     * This should be an ItemStack that can be used by the player to place the cart.
     * This is the item that was registered with the cart via the registerMinecart function,
     * but is not necessary the item the cart drops when destroyed.
     * @return An ItemStack that can be used to place the cart.
     */
    public ItemStack getCartItem()
    {
    	return null;
        //return MinecartRegistry.getItemForCart(this);
    }

    /**
     * Returns true if this cart is self propelled.
     * @return True if powered.
     */
    public boolean isPoweredCart()
    {
        return minecartType == 2 && getClass() == EntityWagon.class;
    }

    /**
     * Returns true if this cart is a storage cart
     * Some carts may have inventories but not be storage carts
     * and some carts without inventories may be storage carts.
     * @return True if this cart should be classified as a storage cart.
     */
    public boolean isStorageCart()
    {
        return minecartType == 1 && getClass() == EntityWagon.class;
    }

    /**
     * Returns true if this cart can be ridden by an Entity.
     * @return True if this cart can be ridden.
     */
    public boolean canBeRidden()
    {
        if(minecartType == 0 && getClass() == EntityWagon.class)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns true if this cart can currently use rails.
     * This function is mainly used to gracefully detach a minecart from a rail.
     * @return True if the minecart can use rails.
     */
    public boolean canUseRail()
    {
        return canUseRail;
    }

    /**
     * Set whether the minecart can use rails.
     * This function is mainly used to gracefully detach a minecart from a rail.
     * @param use Whether the minecart can currently use rails.
     */
    public void setCanUseRail(boolean use)
    {
        canUseRail = use;
    }

    /**
     * Return false if this cart should not call IRail.onMinecartPass() and should ignore Powered Rails.
     * @return True if this cart should call IRail.onMinecartPass().
     */
    public boolean shouldDoRailFunctions()
    {
        return true;
    }

    /**
     * Simply returns the minecartType variable.
     * @return minecartType
     */
    public int getMinecartType()
    {
        return minecartType;
    }

    /**
     * Carts should return their drag factor here
     * @return The drag rate.
     */
    protected double getDrag()
    {
        return riddenByEntity != null ? defaultDragRidden : defaultDragEmpty;
    }

    /**
     * Moved to allow overrides.
     * This code applies drag and updates push forces.
     */
    protected void applyDragAndPushForces()
    {
        if(isPoweredCart())
        {
            double d27 = MathHelper.sqrt_double(pushX * pushX + pushZ * pushZ);
            if(d27 > 0.01D)
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
     * Moved to allow overrides.
     * This code updates push forces.
     */
    protected void updatePushForces()
    {
        if(isPoweredCart())
        {
            double push = MathHelper.sqrt_double(pushX * pushX + pushZ * pushZ);
            if(push > 0.01D && motionX * motionX + motionZ * motionZ > 0.001D)
            {
                pushX /= push;
                pushZ /= push;
                if(pushX * motionX + pushZ * motionZ < 0.0D)
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
     * Moved to allow overrides.
     * This code handles minecart movement and speed capping when not on a rail.
     */
    protected void moveMinecartOffRail(int i, int j, int k)
    {
        double d2 = getMaxSpeedGround();
        if(!onGround)
        {
            d2 = getMaxSpeedAirLateral();
        }
        if(motionX < -d2) motionX = -d2;
        if(motionX >  d2) motionX =  d2;
        if(motionZ < -d2) motionZ = -d2;
        if(motionZ >  d2) motionZ =  d2;
        double moveY = motionY;
        if(getMaxSpeedAirVertical() > 0 && motionY > getMaxSpeedAirVertical())
        {
            moveY = getMaxSpeedAirVertical();
            if(Math.abs(motionX) < 0.3f && Math.abs(motionZ) < 0.3f)
            {
                moveY = 0.15f;
                motionY = moveY;
            }
        }
        if(onGround)
        {
            motionX *= 0.5D;
            motionY *= 0.5D;
            motionZ *= 0.5D;
        }
        moveEntity(motionX, moveY, motionZ);
        if(!onGround)
        {
            motionX *= getDragAir();
            motionY *= getDragAir();
            motionZ *= getDragAir();
        }
    }

    /**
     * Moved to allow overrides.
     * This code applies fuel consumption.
     */
    protected void updateFuel()
    {
        if (fuel > 0) fuel--;
        if (fuel <= 0) pushX = pushZ = 0.0D;
        setMinecartPowered(fuel > 0);
    }

    /**
     * Moved to allow overrides, This code handle slopes affecting velocity.
     * @param metadata The blocks position metadata
     */
    protected void adjustSlopeVelocities(int metadata)
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
     * Returns the carts max speed.
     * Carts going faster than 1.1 cause issues with chunk loading.
     * Carts cant traverse slopes or corners at greater than 0.5 - 0.6.
     * This value is compared with the rails max speed to determine
     * the carts current max speed. A normal rails max speed is 0.4.
     * @return Carts max speed.
     */
    public float getMaxSpeedRail()
    {
        return maxSpeedRail;
    }

    public void setMaxSpeedRail(float value)
    {
        maxSpeedRail = value;
    }

    public float getMaxSpeedGround()
    {
        return maxSpeedGround;
    }

    public void setMaxSpeedGround(float value)
    {
        maxSpeedGround = value;
    }

    public float getMaxSpeedAirLateral()
    {
        return maxSpeedAirLateral;
    }

    public void setMaxSpeedAirLateral(float value)
    {
        maxSpeedAirLateral = value;
    }

    public float getMaxSpeedAirVertical()
    {
        return maxSpeedAirVertical;
    }

    public void setMaxSpeedAirVertical(float value)
    {
        maxSpeedAirVertical = value;
    }

    public double getDragAir()
    {
        return dragAir;
    }

    public void setDragAir(double value)
    {
        dragAir = value;
    }
}

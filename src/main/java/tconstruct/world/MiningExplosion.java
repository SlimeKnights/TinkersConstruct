package tconstruct.world;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.world.*;

public class MiningExplosion extends Explosion
{
    World world;
    private Random random = new Random();
    private int field_77289_h = 16;
    private Map field_77288_k = new HashMap();

    public MiningExplosion(World par1World, Entity par2Entity, double par3, double par5, double par7, float par9)
    {
        super(par1World, par2Entity, par3, par5, par7, par9);
        world = par1World;
    }

    @Override
    public void doExplosionA ()
    {
        float f = this.explosionSize;
        HashSet hashset = new HashSet();
        int i;
        int j;
        int k;
        double d0;
        double d1;
        double d2;

        for (i = 0; i < this.field_77289_h; ++i)
        {
            for (j = 0; j < this.field_77289_h; ++j)
            {
                for (k = 0; k < this.field_77289_h; ++k)
                {
                    if (i == 0 || i == this.field_77289_h - 1 || j == 0 || j == this.field_77289_h - 1 || k == 0 || k == this.field_77289_h - 1)
                    {
                        double d3 = (double) ((float) i / ((float) this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                        double d4 = (double) ((float) j / ((float) this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                        double d5 = (double) ((float) k / ((float) this.field_77289_h - 1.0F) * 2.0F - 1.0F);
                        double d6 = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
                        d3 /= d6;
                        d4 /= d6;
                        d5 /= d6;
                        float f1 = this.explosionSize * (0.7F + this.world.rand.nextFloat() * 0.6F);
                        d0 = this.explosionX;
                        d1 = this.explosionY;
                        d2 = this.explosionZ;

                        for (float f2 = 0.3F; f1 > 0.0F; f1 -= f2 * 0.75F)
                        {
                            int l = MathHelper.floor_double(d0);
                            int i1 = MathHelper.floor_double(d1);
                            int j1 = MathHelper.floor_double(d2);
                            Block k1 = this.world.getBlock(l, i1, j1);

                            if (k1 != Blocks.air)
                            {
                                Block block = k1;
                                float f3 = this.exploder != null ? this.exploder.func_145772_a(this, this.world, l, i1, j1, block) : block.getExplosionResistance(this.exploder, world, l, i1, j1, explosionX, explosionY, explosionZ);
                                f1 -= (f3 + 0.8F) * f2 * 0.25f;
                            }

                            if (f1 > 0.0F && (this.exploder == null || this.exploder.func_145774_a(this, this.world, l, i1, j1, k1, f1)))
                            {
                                hashset.add(new ChunkPosition(l, i1, j1));
                            }

                            d0 += d3 * (double) f2;
                            d1 += d4 * (double) f2;
                            d2 += d5 * (double) f2;
                        }
                    }
                }
            }
        }

        this.affectedBlockPositions.addAll(hashset);
        this.explosionSize *= 2.0F;
        i = MathHelper.floor_double(this.explosionX - (double) this.explosionSize - 1.0D);
        j = MathHelper.floor_double(this.explosionX + (double) this.explosionSize + 1.0D);
        k = MathHelper.floor_double(this.explosionY - (double) this.explosionSize - 1.0D);
        int l1 = MathHelper.floor_double(this.explosionY + (double) this.explosionSize + 1.0D);
        int i2 = MathHelper.floor_double(this.explosionZ - (double) this.explosionSize - 1.0D);
        int j2 = MathHelper.floor_double(this.explosionZ + (double) this.explosionSize + 1.0D);
        List list = this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBox((double) i, (double) k, (double) i2, (double) j, (double) l1, (double) j2));
        Vec3 vec3 = Vec3.createVectorHelper(this.explosionX, this.explosionY, this.explosionZ);

        for (int k2 = 0; k2 < list.size(); ++k2)
        {
            Entity entity = (Entity) list.get(k2);
            double d7 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ) / (double) this.explosionSize;

            if (d7 <= 1.0D)
            {
                d0 = entity.posX - this.explosionX;
                d1 = entity.posY + (double) entity.getEyeHeight() - this.explosionY;
                d2 = entity.posZ - this.explosionZ;
                double d8 = (double) MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);

                if (d8 != 0.0D)
                {
                    d0 /= d8;
                    d1 /= d8;
                    d2 /= d8;
                    double d9 = (double) this.world.getBlockDensity(vec3, entity.boundingBox);
                    double d10 = (1.0D - d7) * d9;
                    if (!(entity instanceof EntityItem))
                        entity.attackEntityFrom(DamageSource.setExplosionSource(this), (float) ((int) ((d10 * d10 + d10) / 2.0D * 8.0D * (double) this.explosionSize + 1.0D)));
                    double d11 = EnchantmentProtection.func_92092_a(entity, d10);
                    entity.motionX += d0 * d11;
                    entity.motionY += d1 * d11;
                    entity.motionZ += d2 * d11;

                    if (entity instanceof EntityPlayer)
                    {
                        this.field_77288_k.put((EntityPlayer) entity, Vec3.createVectorHelper(d0 * d10, d1 * d10, d2 * d10));
                    }
                }
            }
        }

        this.explosionSize = f;
    }

    @Override
    public void doExplosionB (boolean par1)
    {
        this.world.playSoundEffect(this.explosionX, this.explosionY, this.explosionZ, "random.explode", 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);

        if (this.explosionSize >= 2.0F && this.isSmoking)
        {
            this.world.spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
        }
        else
        {
            this.world.spawnParticle("largeexplode", this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);
        }

        Iterator iterator;
        ChunkPosition chunkposition;
        int i;
        int j;
        int k;
        Block l;

        if (this.isSmoking)
        {
            iterator = this.affectedBlockPositions.iterator();

            while (iterator.hasNext())
            {
                chunkposition = (ChunkPosition) iterator.next();
                i = chunkposition.chunkPosX;
                j = chunkposition.chunkPosY;
                k = chunkposition.chunkPosZ;
                l = this.world.getBlock(i, j, k);

                if (par1)
                {
                    double d0 = (double) ((float) i + this.world.rand.nextFloat());
                    double d1 = (double) ((float) j + this.world.rand.nextFloat());
                    double d2 = (double) ((float) k + this.world.rand.nextFloat());
                    double d3 = d0 - this.explosionX;
                    double d4 = d1 - this.explosionY;
                    double d5 = d2 - this.explosionZ;
                    double d6 = (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5D / (d6 / (double) this.explosionSize + 0.1D);
                    d7 *= (double) (this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
                    d3 *= d7;
                    d4 *= d7;
                    d5 *= d7;
                    this.world.spawnParticle("explode", (d0 + this.explosionX * 1.0D) / 2.0D, (d1 + this.explosionY * 1.0D) / 2.0D, (d2 + this.explosionZ * 1.0D) / 2.0D, d3, d4, d5);
                    this.world.spawnParticle("smoke", d0, d1, d2, d3, d4, d5);
                }

                if (l != Blocks.air)
                {
                    Block block = l;

                    if (block.canDropFromExplosion(this))
                    {
                        block.dropBlockAsItemWithChance(this.world, i, j, k, this.world.getBlockMetadata(i, j, k), 1.0F, 0);
                    }

                    block.onBlockExploded(this.world, i, j, k, this);
                }
            }
        }

        if (this.isFlaming)
        {
            iterator = this.affectedBlockPositions.iterator();

            while (iterator.hasNext())
            {
                chunkposition = (ChunkPosition) iterator.next();
                i = chunkposition.chunkPosX;
                j = chunkposition.chunkPosY;
                k = chunkposition.chunkPosZ;
                l = this.world.getBlock(i, j, k);
                Block i1 = this.world.getBlock(i, j - 1, k);

                if (l == Blocks.air && i1.isOpaqueCube() && this.random.nextInt(3) == 0)
                {
                    this.world.setBlock(i, j, k, Blocks.fire);
                }
            }
        }
    }
}

package tconstruct.blocks;

import mantle.blocks.MantleBlock;
import tconstruct.blocks.logic.GolemCoreLogic;
import tconstruct.common.TContent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class GolemHeadBlock extends MantleBlock
{
    /*public static int headSideTex = ModLoader.addOverride("/terrain.png", "/GGE/golemheadside.png");
    public static int headTopTex = ModLoader.addOverride("/terrain.png", "/GGE/golemheadtop&bottom.png");
    public static int face;*/

    public GolemHeadBlock()
    {
        super(Material.ground);
        //setTickRandomly(true);
    }

    public IIcon[] icons;
    public String[] textureNames = new String[] { "golemhead_face", "golemhead_side", "golemhead_top" };

    @Override
    public void registerIcons (IIconRegister iconRegister)
    {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    public void onNeighborBlockChange (World world, int x, int y, int z, int blockID)
    {
        if (buildGolem(world, x, y, z, world.getBlockMetadata(x, y, z)))
        {
            //mod_Golems.trigger("headachievement");
        }
    }

    /*public int getIcon(int i, int j)
    {
        if (i == 1)
        {
            return headTopTex;
        }
        if (i == 0)
        {
            return headTopTex;
        }
        int k = face;
        if (j == 2 && i == 2)
        {
            return k;
        }
        if (j == 3 && i == 5)
        {
            return k;
        }
        if (j == 0 && i == 3)
        {
            return k;
        }
        if (j == 1 && i == 4)
        {
            return k;
        }
        else
        {
            return headSideTex;
        }
    }*/

    /*public int getBlockTextureFromSide(int i)
    {
        int j = face;
        if (i == 1)
        {
            return blockIndexInTexture;
        }
        if (i == 0)
        {
            return blockIndexInTexture;
        }
        if (i == 3)
        {
            return j;
        }
        else
        {
            return blockIndexInTexture + 16;
        }
    }*/

    @Override
    public void onBlockPlacedBy (World world, int i, int j, int k, EntityLivingBase entityliving, ItemStack par6ItemStack)
    {
        int l = MathHelper.floor_double((double) ((entityliving.rotationYaw * 4F) / 360F) + 2.5D) & 3;
        world.setBlockMetadataWithNotify(i, j, k, l, 3);
        if (!buildGolem(world, i, j, k, l))
        {
            for (int i1 = 0; (double) i1 < Math.random() * 5D + 3D; i1++)
            {
                world.spawnParticle("largesmoke", (double) i + Math.random(), (double) j + 1.2D + Math.random() / 2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
            }
        }
        /*else
        {
            mod_Golems.trigger("headachievement");
        }*/
    }

    private boolean idIsInvalid (int i)
    {
        //return Block.blocksList[i] != null && (!Block.blocksList[i].renderAsNormalBlock() && i != TContent.golemCore.blockID || i == 43 || i == 44 || i == Block.chest.blockID);
        return false;
    }

    private boolean buildGolem (World world, int x, int y, int z, int metadata)
    {
        /*int pedestalHeight = 0;
        for (int yPos = 1; yPos <= 3; yPos++)
        {
        	if (world.getBlockId(x, y - yPos, z) == TContent.golemPedestal.blockID)
        	{
        		pedestalHeight = yPos;
        	}
        }

        if (pedestalHeight == 0)
        {
        	return false;
        }
        for (int height = pedestalHeight; height >= 0; height--)
        {
        	if (world.getBlockId(x, y - height, z) == 0)
        	{
        		return false;
        	}
        }

        int l1 = 0;
        int i2 = 0;
        ItemStack itemstack = null;
        GolemCoreLogic coreLogic = null;
        int golemBlockIDs[][] = new int[3][3];
        int golemBlockMetas[][] = new int[3][3];
        for (int arrX = 0; arrX < 3; arrX++)
        {
        	for (int arrY = 0; arrY < 3; arrY++)
        	{
        		golemBlockIDs[arrX][arrY] = 0;
        	}
        }

        for (int iterX = -1; iterX <= 1; iterX++)
        {
        	for (int iterY = 0; iterY < 3; iterY++)
        	{
        		if (iterY + (3 - pedestalHeight) >= 3 || iterY + (3 - pedestalHeight) == 0 && iterX != 0)
        		{
        			continue;
        		}
        		if (metadata == 0)
        		{
        			int bID = world.getBlockId(x - iterX, y - iterY, z);
        			if (idIsInvalid(bID))
        			{
        				bID = 0;
        				return false;
        			}
        			if (bID == blockID)
        			{
        				i2++;
        			}
        			golemBlockIDs[iterX + 1][iterY + (3 - pedestalHeight)] = bID;
        			golemBlockMetas[iterX + 1][iterY + (3 - pedestalHeight)] = bID == blockID ? 0 : world.getBlockMetadata(x - iterX, y - iterY, z);
        		}
        		if (metadata == 1)
        		{
        			int l3 = world.getBlockId(x, y - iterY, z - iterX);
        			if (idIsInvalid(l3))
        			{
        				l3 = 0;
        				return false;
        			}
        			if (l3 == blockID)
        			{
        				i2++;
        			}
        			golemBlockIDs[iterX + 1][iterY + (3 - pedestalHeight)] = l3;
        			golemBlockMetas[iterX + 1][iterY + (3 - pedestalHeight)] = l3 == blockID ? 0 : world.getBlockMetadata(x, y - iterY, z - iterX);
        		}
        		if (metadata == 2)
        		{
        			int i4 = world.getBlockId(x + iterX, y - iterY, z);
        			if (idIsInvalid(i4))
        			{
        				i4 = 0;
        				return false;
        			}
        			if (i4 == blockID)
        			{
        				i2++;
        			}
        			golemBlockIDs[iterX + 1][iterY + (3 - pedestalHeight)] = i4;
        			golemBlockMetas[iterX + 1][iterY + (3 - pedestalHeight)] = i4 == blockID ? 0 : world.getBlockMetadata(x + iterX, y - iterY, z);
        		}
        		if (metadata == 3)
        		{
        			int bID = world.getBlockId(x, y - iterY, z + iterX);
        			if (idIsInvalid(bID))
        			{
        				bID = 0;
        				return false;
        			}
        			if (bID == blockID)
        			{
        				i2++;
        			}
        			golemBlockIDs[iterX + 1][iterY + (3 - pedestalHeight)] = bID;
        			golemBlockMetas[iterX + 1][iterY + (3 - pedestalHeight)] = bID == blockID ? 0 : world.getBlockMetadata(x, y - iterY, z + iterX);
        		}
        		if (i2 > 1)
        		{
        			return false;
        		}
        		if (golemBlockIDs[iterX + 1][iterY + (3 - pedestalHeight)] != 0 && Block.blocksList[golemBlockIDs[iterX + 1][iterY]] != null
        				&& !Block.blocksList[golemBlockIDs[iterX + 1][iterY]].renderAsNormalBlock() && (iterX + 1 == 0 || iterX + 1 == 2) && iterY == 2
        				&& golemBlockIDs[iterX + 1][iterY] != TContent.golemCore.blockID)
        		{
        			return false;
        		}
        		if (golemBlockIDs[iterX + 1][iterY + (3 - pedestalHeight)] == TContent.golemCore.blockID)
        		{
        			l1++;
        			if (metadata == 0)
        			{
        				coreLogic = (GolemCoreLogic) world.getBlockTileEntity(x - iterX, y - iterY, z);
        			}
        			if (metadata == 1)
        			{
        				coreLogic = (GolemCoreLogic) world.getBlockTileEntity(x, y - iterY, z - iterX);
        			}
        			if (metadata == 2)
        			{
        				coreLogic = (GolemCoreLogic) world.getBlockTileEntity(x + iterX, y - iterY, z);
        			}
        			if (metadata == 3)
        			{
        				coreLogic = (GolemCoreLogic) world.getBlockTileEntity(x, y - iterY, z + iterX);
        			}
        			itemstack = coreLogic.getKey();
        		}
        		if (l1 > 1)
        		{
        			return false;
        		}
        	}
        }

        boolean flag = false;
        for (int j3 = 0; j3 < 3; j3++)
        {
        	for (int k4 = 0; k4 < 3; k4++)
        	{
        		if (golemBlockIDs[k4][j3] == this.blockID)
        		{
        			flag = true;
        		}
        	}
        }

        if (!flag)
        {
        	return false;
        }
        if (golemBlockIDs[0][2] != 0 && Block.blocksList[golemBlockIDs[0][2]].renderAsNormalBlock() || golemBlockIDs[2][2] != 0 && Block.blocksList[golemBlockIDs[2][2]].renderAsNormalBlock())
        {
        	return false;
        }
        if (l1 > 0
        		&& (golemBlockIDs[0][0] == TContent.golemCore.blockID || golemBlockIDs[0][1] == TContent.golemCore.blockID || golemBlockIDs[2][0] == TContent.golemCore.blockID || golemBlockIDs[2][1] == TContent.golemCore.blockID))
        {
        	return false;
        }
        TileEntity tileentity = world.getBlockTileEntity(x, y - pedestalHeight, z);
        if (!(tileentity instanceof TileEntityGolemPedestal))
        {
        	return false;
        }
        TileEntityGolemPedestal tileentitygolempedestal = (TileEntityGolemPedestal) tileentity;
        if (tileentitygolempedestal.subtractSoul(l1 == 1 ? 8 : 1) && world.getBlockId(x, y - pedestalHeight, z) == TContent.golemPedestal.blockID)
        {
        	EntityGenericGolem entitygenericgolem = new EntityGenericGolem(world);
        	EntityPlayerSP entityplayersp = ModLoader.getMinecraftInstance().thePlayer;
        	double d = ((EntityPlayer) (entityplayersp)).posX - (double) (float) ((double) x + 0.5D);
        	double d1 = ((EntityPlayer) (entityplayersp)).posZ - (double) (float) ((double) z + 0.5D);
        	entitygenericgolem.setBody(golemBlockIDs, golemBlockMetas, y, pedestalHeight);
        	entitygenericgolem.setPositionAndRotation((double) x + 0.5D, (y - pedestalHeight) + 1, (double) z + 0.5D, (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F, 0.0F);
        	if (l1 > 0)
        	{
        		entitygenericgolem.setCore(itemstack);
        	}
        	if (coreLogic != null)
        	{
        		//((GolemCoreBlock) mod_Golems.golemCore).killDisplayItem(world, coreLogic.xCoord, coreLogic.yCoord, coreLogic.zCoord);
        		//coreLogic.key = null;
        	}
        	if (!buildInterrupt(world, x, y, z, metadata, golemBlockIDs, golemBlockMetas, pedestalHeight, coreLogic != null, itemstack, coreLogic) && world.spawnEntityInWorld(entitygenericgolem))
        	{
        		delBlocks(world, x, y, z, metadata, golemBlockIDs, pedestalHeight);
        		world.spawnParticle("hugeexplosion", (double) x + Math.random(), (double) y + 1.2D, (double) z + Math.random(), 0.0D, 0.0D, 0.0D);
        		return true;
        	}
        	else
        	{
        		return false;
        	}
        }
        else
        {
        	return false;
        }*/
        return false;
    }

    public boolean buildInterrupt (World world, int i, int j, int k, int l, int ai[][], int ai1[][], int i1, boolean flag, ItemStack itemstack, GolemCoreLogic tileentitygolemcore)
    {
        /*EntityPlayerSP entityplayersp = ModLoader.getMinecraftInstance().thePlayer;
        double d = ((EntityPlayer) (entityplayersp)).posX - (double) (float) ((double) i + 0.5D);
        double d1 = ((EntityPlayer) (entityplayersp)).posZ - (double) (float) ((double) k + 0.5D);
        int j1 = world.difficultySetting;
        int k1 = 0;
        for (int l1 = 0; l1 < 3; l1++)
        {
        	for (int i2 = 0; i2 < 3; i2++)
        	{
        		if (ai[l1][i2] != 0)
        		{
        			k1++;
        		}
        	}
        }

        boolean flag1 = ai[0][1] != 0 || ai[2][1] != 0;
        if (isAll(ai, ai1, Block.tnt.blockID, -1) || i1 == 3 && ai[1][1] == Block.tnt.blockID && ai[1][2] == Block.cloth.blockID && ai1[1][2] == 5 && !flag1 && j1 >= 1)
        {
        	EntityCreeper entitycreeper = new EntityCreeper(world);
        	entitycreeper.setPositionAndRotation((double) i + 0.5D, (j - i1) + 1, (double) k + 0.5D, (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F, 0.0F);
        	world.spawnEntityInWorld(entitycreeper);
        	delBlocks(world, i, j, k, l, ai, i1);
        	world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        	mod_Golems.trigger("creeper");
        	return true;
        }
        if (isAll(ai, ai1, Block.netherrack.blockID, -1) && i1 == 3 && j1 >= 1)
        {
        	EntityBlaze entityblaze = new EntityBlaze(world);
        	entityblaze.setPositionAndRotation((double) i + 0.5D, (j - i1) + 1, (double) k + 0.5D, (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F, 0.0F);
        	world.spawnEntityInWorld(entityblaze);
        	delBlocks(world, i, j, k, l, ai, i1);
        	world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        	mod_Golems.trigger("blaze");
        	return true;
        }
        if (isAll(ai, ai1, Block.obsidian.blockID, -1) && i1 == 3 && countBlock(ai, ai1, Block.obsidian.blockID, -1) == 2 && j1 >= 1)
        {
        	EntityEnderman entityenderman = new EntityEnderman(world);
        	entityenderman.setPositionAndRotation((double) i + 0.5D, (j - i1) + 1, (double) k + 0.5D, (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F, 0.0F);
        	world.spawnEntityInWorld(entityenderman);
        	delBlocks(world, i, j, k, l, ai, i1);
        	world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        	mod_Golems.trigger("enderman");
        	return true;
        }
        if (hasBlock(ai, ai1, Block.tnt.blockID, -1) && hasBlock(ai, ai1, Block.netherrack.blockID, -1))
        {
        	boolean flag2 = false;
        	Entity aentity[][] = new Entity[3][3];
        	for (int j2 = 0; j2 < 3; j2++)
        	{
        		for (int l2 = 0; l2 < 3; l2++)
        		{
        			aentity[j2][l2] = null;
        			if (ai[j2][l2] == Block.tnt.blockID)
        			{
        				EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world);
        				entitytntprimed.fuse = 80;
        				aentity[j2][l2] = entitytntprimed;
        			}
        		}
        	}

        	replEntity(world, i, j, k, l, ai, aentity, i1);
        	i1 = updateBody(ai, ai1);
        	EntityGenericGolem entitygenericgolem = new EntityGenericGolem(world);
        	entitygenericgolem.setBody(ai, ai1, l, i1);
        	entitygenericgolem.setPositionAndRotation((double) i + 0.5D, (j - i1) + 1, (double) k + 0.5D, (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F, 0.0F);
        	if (flag)
        	{
        		entitygenericgolem.setCore(itemstack);
        	}
        	world.spawnEntityInWorld(entitygenericgolem);
        	world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        	mod_Golems.trigger("boom");
        	return true;
        }
        if (i1 == 3 && !flag1 && ai[1][2] == Block.blockIron.blockID && ai[1][1] == Block.blockIron.blockID)
        {
        	List list = world.getEntitiesWithinAABB(EntityLiving.class, AxisAlignedBB.getAABBPool().getAABB(i, j, k, (double) i + 1.0D, (double) j + 1.0D, (double) k + 1.0D).expand(4D, 16D, 4D));
        	Entity entity;
        	for (Iterator iterator = list.iterator(); iterator.hasNext(); world.addWeatherEffect(new EntityLightningBolt(world, entity.posX, entity.posY, entity.posZ)))
        	{
        		Object obj = iterator.next();
        		entity = (Entity) obj;
        	}

        	mod_Golems.trigger("zap");
        }
        else
        {
        	if (isAll(ai, ai1, Block.cloth.blockID, 0) && countBlock(ai, ai1, Block.cloth.blockID, 0) == 4)
        	{
        		EntitySheep entitysheep = new EntitySheep(world);
        		entitysheep.setPositionAndRotation((double) i + 0.5D, (j - i1) + 1, (double) k + 0.5D, (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F, 0.0F);
        		world.spawnEntityInWorld(entitysheep);
        		delBlocks(world, i, j, k, l, ai, i1);
        		world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        		mod_Golems.trigger("sheep");
        		return true;
        	}
        	if (countBlock(ai, ai1, Block.furnaceIdle.blockID, -1) >= (int) (Math.random() * 2D + 2D))
        	{
        		ItemStack aitemstack[][] = new ItemStack[3][3];
        		aitemstack[0][1] = new ItemStack(Block.lavaMoving, 1);
        		aitemstack[2][1] = new ItemStack(Block.lavaMoving, 1);
        		aitemstack[1][0] = new ItemStack(Block.lavaMoving, 1);
        		aitemstack[1][1] = new ItemStack(Block.lavaMoving, 1);
        		aitemstack[1][2] = new ItemStack(Block.lavaMoving, 1);
        		replBlocks(world, i, j, k, l, ai, aitemstack, i1);
        		mod_Golems.trigger("meltdown");
        		return true;
        	}
        	if (hasBlock(ai, ai1, Block.stoneBrick.blockID, -1) && Math.random() <= 0.40000000000000002D)
        	{
        		boolean flag3 = false;
        		Entity aentity1[][] = new Entity[3][3];
        		for (int k2 = 0; k2 < 3; k2++)
        		{
        			for (int i3 = 0; i3 < 3; i3++)
        			{
        				aentity1[k2][i3] = null;
        				if (ai[k2][i3] == Block.stoneBrick.blockID)
        				{
        					aentity1[k2][i3] = Math.random() <= 0.59999999999999998D ? ((Entity) (new EntitySilverfish(world))) : null;
        				}
        			}
        		}

        		replEntity(world, i, j, k, l, ai, aentity1, i1);
        		i1 = updateBody(ai, ai1);
        		EntityGenericGolem entitygenericgolem1 = new EntityGenericGolem(world);
        		entitygenericgolem1.setBody(ai, ai1, l, i1);
        		entitygenericgolem1.setPositionAndRotation((double) i + 0.5D, (j - i1) + 1, (double) k + 0.5D, (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F, 0.0F);
        		if (flag)
        		{
        			entitygenericgolem1.setCore(itemstack);
        		}
        		world.spawnEntityInWorld(entitygenericgolem1);
        		world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        		mod_Golems.trigger("silverfish");
        		return true;
        	}
        	if (countBlock(ai, ai1, Block.melon.blockID, -1) == 4)
        	{
        		delBlocks(world, i, j, k, l, ai, i1);
        		EntityGSpawn entitygspawn = new EntityGSpawn(world);
        		entitygspawn.setPosition(i, j, k);
        		entitygspawn.skin = "http://s3.amazonaws.com/MinecraftSkins/loligator704.png";
        		world.spawnEntityInWorld(entitygspawn);
        		mod_Golems.trigger("loligator");
        		world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        		return true;
        	}
        	if (countBlock(ai, ai1, Block.cloth.blockID, 13) == 3 && ai[1][1] == Block.cloth.blockID && ai1[1][1] == 0)
        	{
        		delBlocks(world, i, j, k, l, ai, i1);
        		EntityGSpawn entitygspawn1 = new EntityGSpawn(world);
        		entitygspawn1.setPosition(i, j, k);
        		entitygspawn1.skin = "http://s3.amazonaws.com/MinecraftSkins/coalheartly.png";
        		world.spawnEntityInWorld(entitygspawn1);
        		mod_Golems.trigger("bitterman");
        		world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        		return true;
        	}
        	if (countBlock(ai, ai1, Block.cloth.blockID, 11) == 3 && ai[1][1] == Block.blockLapis.blockID)
        	{
        		delBlocks(world, i, j, k, l, ai, i1);
        		EntityGSpawn entitygspawn2 = new EntityGSpawn(world);
        		entitygspawn2.setPosition(i, j, k);
        		entitygspawn2.skin = "http://s3.amazonaws.com/MinecraftSkins/BILLYTG101.png";
        		world.spawnEntityInWorld(entitygspawn2);
        		mod_Golems.trigger("yourstruly");
        		return true;
        	}
        	if (countBlock(ai, ai1, Block.cloth.blockID, 12) == 2 && ai[1][1] == Block.blockGold.blockID && ai[1][2] == Block.cloth.blockID && ai1[1][2] == 0)
        	{
        		delBlocks(world, i, j, k, l, ai, i1);
        		EntityGSpawn entitygspawn3 = new EntityGSpawn(world);
        		entitygspawn3.setPosition(i, j, k);
        		entitygspawn3.skin = "http://s3.amazonaws.com/MinecraftSkins/Notch.png";
        		world.spawnEntityInWorld(entitygspawn3);
        		mod_Golems.trigger("king");
        		world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        		return true;
        	}

        	if (isAll(ai, ai1, Block.blockClay.blockID, -1) && i1 == 3 && countBlock(ai, ai1, Block.blockClay.blockID, -1) == 2 && j1 >= 1)
        	{
        		EntityZombie entityzombie = new EntityZombie(world);
        		entityzombie.setPositionAndRotation((double) i + 0.5D, (j - i1) + 1, (double) k + 0.5D, (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F, 0.0F);
        		world.spawnEntityInWorld(entityzombie);
        		delBlocks(world, i, j, k, l, ai, i1);
        		world.spawnParticle("hugeexplosion", (double) i + Math.random(), (double) j + 1.2D, (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
        		mod_Golems.trigger("zombie");
        		return true;
        	}
        }*/
        return false;
    }

    private void delBlocks (World world, int i, int j, int k, int l, int ai[][], int i1)
    {
        for (int j1 = -1; j1 <= 1; j1++)
        {
            for (int k1 = 0; k1 < i1; k1++)
            {
                if (k1 == 0 && j1 != 0 || ai[j1 + 1][(k1 - i1) + 3] == 0)
                {
                    continue;
                }
                if (l == 0)
                {
                    world.setBlock(i - j1, j - k1, k, 0);
                }
                if (l == 1)
                {
                    world.setBlock(i, j - k1, k - j1, 0);
                }
                if (l == 2)
                {
                    world.setBlock(i + j1, j - k1, k, 0);
                }
                if (l == 3)
                {
                    world.setBlock(i, j - k1, k + j1, 0);
                }
            }
        }

        if (i1 == 2 && (ai[0][1] != 0 || ai[2][1] != 0))
        {
            if (l == 0)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i + 1, j, k, 0);
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i - 1, j, k, 0);
                }
            }
            if (l == 1)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i, j, k + 1, 0);
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i, j, k - 1, 0);
                }
            }
            if (l == 2)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i - 1, j, k, 0);
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i + 1, j, k, 0);
                }
            }
            if (l == 3)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i, j, k - 1, 0);
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i, j, k + 1, 0);
                }
            }
        }
    }

    private boolean hasBlock (int ai[][], int ai1[][], int i, int j)
    {
        for (int k = 0; k < 3; k++)
        {
            for (int l = 0; l < 3; l++)
            {
                if (ai[k][l] == i && (ai1[k][l] == j || j == -1))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private void replBlocks (World world, int i, int j, int k, int l, int ai[][], ItemStack aitemstack[][], int i1)
    {
        for (int j1 = -1; j1 <= 1; j1++)
        {
            for (int k1 = 0; k1 < i1; k1++)
            {
                if (aitemstack[j1 + 1][(k1 - i1) + 3] == null)
                {
                    aitemstack[j1 + 1][(k1 - i1) + 3] = new ItemStack(0, 0, 0);
                }
                if (k1 == 0 && j1 != 0 || ai[j1 + 1][(k1 - i1) + 3] == 0)
                {
                    continue;
                }
                if (l == 0)
                {
                    world.setBlock(i - j1, j - k1, k, aitemstack[j1 + 1][(k1 - i1) + 3].itemID, aitemstack[j1 + 1][(k1 - i1) + 3].getItemDamage(), 3);
                }
                if (l == 1)
                {
                    world.setBlock(i, j - k1, k - j1, aitemstack[j1 + 1][(k1 - i1) + 3].itemID, aitemstack[j1 + 1][(k1 - i1) + 3].getItemDamage(), 3);
                }
                if (l == 2)
                {
                    world.setBlock(i + j1, j - k1, k, aitemstack[j1 + 1][(k1 - i1) + 3].itemID, aitemstack[j1 + 1][(k1 - i1) + 3].getItemDamage(), 3);
                }
                if (l == 3)
                {
                    world.setBlock(i, j - k1, k + j1, aitemstack[j1 + 1][(k1 - i1) + 3].itemID, aitemstack[j1 + 1][(k1 - i1) + 3].getItemDamage(), 3);
                }
            }
        }

        if (i1 == 2 && (ai[0][1] != 0 || ai[2][1] != 0))
        {
            if (l == 0)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i + 1, j, k, aitemstack[0][1].itemID, aitemstack[0][1].getItemDamage(), 3);
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i - 1, j, k, aitemstack[2][1].itemID, aitemstack[2][1].getItemDamage(), 3);
                }
            }
            if (l == 1)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i, j, k + 1, aitemstack[0][1].itemID, aitemstack[0][1].getItemDamage(), 3);
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i, j, k - 1, aitemstack[2][1].itemID, aitemstack[2][1].getItemDamage(), 3);
                }
            }
            if (l == 2)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i - 1, j, k, aitemstack[0][1].itemID, aitemstack[0][1].getItemDamage(), 3);
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i + 1, j, k, aitemstack[2][1].itemID, aitemstack[2][1].getItemDamage(), 3);
                }
            }
            if (l == 3)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i, j, k - 1, aitemstack[0][1].itemID, aitemstack[0][1].getItemDamage(), 3);
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i, j, k + 1, aitemstack[2][1].itemID, aitemstack[2][1].getItemDamage(), 3);
                }
            }
        }
    }

    private void replEntity (World world, int i, int j, int k, int l, int ai[][], Entity aentity[][], int i1)
    {
        for (int j1 = -1; j1 <= 1; j1++)
        {
            for (int k1 = 0; k1 < i1; k1++)
            {
                if (k1 == 0 && j1 != 0)
                {
                    continue;
                }
                Entity entity8 = aentity[j1 + 1][(k1 - i1) + 3];
                if (ai[j1 + 1][(k1 - i1) + 3] == 0)
                {
                    if (entity8 != null)
                    {
                        entity8.setDead();
                    }
                    continue;
                }
                if (l == 0)
                {
                    world.setBlock(i - j1, j - k1, k, 0);
                    if (entity8 != null)
                    {
                        entity8.setPosition(((float) i - (float) j1) + 0.5F, ((float) j - (float) k1) + 0.5F, (float) k + 0.5F);
                        world.spawnEntityInWorld(entity8);
                        ai[j1 + 1][(k1 - i1) + 3] = 0;
                    }
                }
                if (l == 1)
                {
                    world.setBlock(i, j - k1, k - j1, 0);
                    if (entity8 != null)
                    {
                        entity8.setPosition(((float) i - (float) j1) + 0.5F, ((float) j - (float) k1) + 0.5F, (float) k + 0.5F);
                        world.spawnEntityInWorld(entity8);
                        ai[j1 + 1][(k1 - i1) + 3] = 0;
                    }
                }
                if (l == 2)
                {
                    world.setBlock(i + j1, j - k1, k, 0);
                    if (entity8 != null)
                    {
                        entity8.setPosition(((float) i - (float) j1) + 0.5F, ((float) j - (float) k1) + 0.5F, (float) k + 0.5F);
                        world.spawnEntityInWorld(entity8);
                        ai[j1 + 1][(k1 - i1) + 3] = 0;
                    }
                }
                if (l != 3)
                {
                    continue;
                }
                world.setBlock(i, j - k1, k + j1, 0);
                if (entity8 != null)
                {
                    entity8.setPosition(((float) i - (float) j1) + 0.5F, ((float) j - (float) k1) + 0.5F, (float) k + 0.5F);
                    world.spawnEntityInWorld(entity8);
                    ai[j1 + 1][(k1 - i1) + 3] = 0;
                }
            }
        }

        if (i1 == 2 && (ai[0][1] != 0 || ai[2][1] != 0))
        {
            Object obj = null;
            if (l == 0)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i + 1, j, k, 0);
                    Entity entity;
                    if ((entity = aentity[0][1]) != null)
                    {
                        entity.setPosition((float) i + 1.0F + 0.5F, (float) j + 0.5F, (float) k + 0.5F);
                        world.spawnEntityInWorld(aentity[0][1]);
                        ai[2][1] = 0;
                    }
                }
                else if (aentity[0][1] != null)
                {
                    aentity[0][1].setDead();
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i - 1, j, k, 0);
                    Entity entity1;
                    if ((entity1 = aentity[2][1]) != null)
                    {
                        entity1.setPosition(((float) i - 1.0F) + 0.5F, (float) j + 0.5F, (float) k + 0.5F);
                        world.spawnEntityInWorld(aentity[2][1]);
                        ai[2][1] = 0;
                    }
                }
                else if (aentity[2][1] != null)
                {
                    aentity[2][1].setDead();
                }
            }
            if (l == 1)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i, j, k + 1, 0);
                    Entity entity2;
                    if ((entity2 = aentity[0][1]) != null)
                    {
                        entity2.setPosition((float) i + 0.5F, (float) j + 0.5F, (float) k + 1.0F + 0.5F);
                        world.spawnEntityInWorld(aentity[0][1]);
                        ai[2][1] = 0;
                    }
                }
                else if (aentity[0][1] != null)
                {
                    aentity[0][1].setDead();
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i, j, k - 1, 0);
                    Entity entity3;
                    if ((entity3 = aentity[2][1]) != null)
                    {
                        entity3.setPosition((float) i + 0.5F, (float) j + 0.5F, ((float) k - 1.0F) + 0.5F);
                        world.spawnEntityInWorld(aentity[2][1]);
                        ai[2][1] = 0;
                    }
                }
                else if (aentity[2][1] != null)
                {
                    aentity[2][1].setDead();
                }
            }
            if (l == 2)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i - 1, j, k, 0);
                    Entity entity4;
                    if ((entity4 = aentity[0][1]) != null)
                    {
                        entity4.setPosition(((float) i - 1.0F) + 0.5F, (float) j + 0.5F, (float) k + 0.5F);
                        world.spawnEntityInWorld(aentity[0][1]);
                        ai[2][1] = 0;
                    }
                }
                else if (aentity[0][1] != null)
                {
                    aentity[0][1].setDead();
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i + 1, j, k, 0);
                    Entity entity5;
                    if ((entity5 = aentity[2][1]) != null)
                    {
                        entity5.setPosition((float) i + 1.0F + 0.5F, (float) j + 0.5F, (float) k + 0.5F);
                        world.spawnEntityInWorld(aentity[2][1]);
                        ai[2][1] = 0;
                    }
                }
                else if (aentity[2][1] != null)
                {
                    aentity[2][1].setDead();
                }
            }
            if (l == 3)
            {
                if (ai[0][1] != 0)
                {
                    world.setBlock(i, j, k - 1, 0);
                    Entity entity6;
                    if ((entity6 = aentity[0][1]) != null)
                    {
                        entity6.setPosition((float) i + 0.5F, (float) j + 0.5F, (float) k + 1.0F + 0.5F);
                        world.spawnEntityInWorld(aentity[0][1]);
                    }
                }
                else if (aentity[0][1] != null)
                {
                    aentity[0][1].setDead();
                }
                if (ai[2][1] != 0)
                {
                    world.setBlock(i, j, k + 1, 0);
                    Entity entity7;
                    if ((entity7 = aentity[2][1]) != null)
                    {
                        entity7.setPosition((float) i + 0.5F, (float) j + 0.5F, (float) k + 1.0F + 0.5F);
                        world.spawnEntityInWorld(aentity[2][1]);
                    }
                }
                else if (aentity[2][1] != null)
                {
                    aentity[2][1].setDead();
                }
            }
        }
    }

    private int updateBody (int ai[][], int ai1[][])
    {
        for (int i = 0; ai[1][2] == 0 && i < 3;)
        {
            i++;
            for (int i1 = 0; i1 < 3; i1++)
            {
                int j2 = ai[i1][0];
                int l2 = ai[i1][1];
                ai[i1][0] = ai[i1][2];
                ai[i1][1] = j2;
                ai[i1][2] = l2;
            }

            int j1 = 0;
            while (j1 < 3)
            {
                int k2 = ai1[j1][0];
                int i3 = ai1[j1][1];
                ai1[j1][0] = ai1[j1][2];
                ai1[j1][1] = k2;
                ai1[j1][2] = i3;
                j1++;
            }
        }

        if (ai[1][1] == 0)
        {
            for (int j = 0; j < 3; j++)
            {
                int k1 = ai[j][0];
                ai[j][0] = ai[j][1];
                ai[j][1] = k1;
            }

            for (int k = 0; k < 3; k++)
            {
                int l1 = ai1[k][0];
                ai1[k][0] = ai1[k][1];
                ai1[k][1] = l1;
            }
        }
        int l = 0;
        for (int i2 = 0; i2 < 3; i2++)
        {
            if (ai[1][i2] != 0)
            {
                l = 3 - i2;
            }
        }

        if (l == 1)
        {
            ai[0][1] = 0;
            ai[2][1] = 0;
            ai1[0][1] = 0;
            ai1[2][1] = 0;
        }
        return 0;
    }

    private boolean isAll (int ai[][], int ai1[][], int i, int j)
    {
        boolean flag = false;
        for (int k = 0; k < 3; k++)
        {
            for (int l = 0; l < 3; l++)
            {
                if (ai[k][l] != i && ai[k][l] != 0 && ai[k][l] != this.blockID && (ai1[k][l] != j || j == -1))
                {
                    return false;
                }
                if (ai[k][l] == i && (ai1[k][l] == j || j == -1))
                {
                    flag = true;
                }
            }
        }

        return flag;
    }

    private int countBlock (int ai[][], int ai1[][], int i, int j)
    {
        int k = 0;
        for (int l = 0; l < 3; l++)
        {
            for (int i1 = 0; i1 < 3; i1++)
            {
                if (ai[l][i1] == i && (ai1[l][i1] == j || j == -1))
                {
                    k++;
                }
            }
        }

        return k;
    }
}

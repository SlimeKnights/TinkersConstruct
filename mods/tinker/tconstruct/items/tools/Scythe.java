package mods.tinker.tconstruct.items.tools;

import java.util.List;

import mods.tinker.tconstruct.common.TContent;
import mods.tinker.tconstruct.library.ActiveToolMod;
import mods.tinker.tconstruct.library.TConstructRegistry;
import mods.tinker.tconstruct.library.tools.AbilityHelper;
import mods.tinker.tconstruct.library.tools.Weapon;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Scythe extends Weapon
{
	public Scythe(int itemID)
	{
		super(itemID, 4);
		this.setUnlocalizedName("InfiTool.Scythe");
	}
	
	/*@Override
	protected String getHarvestType()
	{
		return "sword";
	}*/

	@Override
	protected Material[] getEffectiveMaterials()
	{
		return materials;
	}

	static Material[] materials = new Material[] { Material.web, Material.cactus, Material.pumpkin, Material.plants, Material.vine, Material.leaves };

	@Override
	public Item getHeadItem ()
	{
		return  TContent.scytheBlade;
	}
    
    @Override
    public Item getHandleItem ()
    {
        return TContent.toughRod;
    }

	@Override
	public Item getAccessoryItem ()
	{
		return  TContent.toughBinding;
	}

	@Override
	public Item getExtraItem ()
	{
		return  TContent.toughRod;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderPasses (int metadata)
	{
		return 10;
	}
	
	@Override
	public int getPartAmount()
	{
		return 4;
	}
	
	@Override
	public String getIconSuffix (int partType)
	{
		switch (partType)
		{
		case 0:
			return "_scythe_head";
		case 1:
			return "_scythe_head_broken";
		case 2:
			return "_scythe_handle";
		case 3:
			return "_scythe_binding";
		case 4:
			return "_scythe_accessory";
		default:
			return "";
		}
	}
	
	public float getDurabilityModifier ()
	{
		return 3.0f;
	}

    @Override
    public float getRepairCost ()
    {
        return 4.0f;
    }

	@Override
	public String getEffectSuffix ()
	{
		return "_scythe_effect";
	}

	@Override
	public String getDefaultFolder ()
	{
		return "scythe";
	}
	
	@Override
	public int durabilityTypeAccessory ()
	{
		return 1;
	}

	@Override
	public int durabilityTypeExtra ()
	{
		return 1;
	}

    @Override
    public float getDamageModifier ()
    {
        return 0.75f;
    }
    
	@Override
    public String[] toolCategories()
    {
        return new String[] { "weapon", "melee", "harvest" };
    }
	
	/* Scythe Specific */
	
	@Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        World world = player.worldObj;
        int blockID = 0;
        int meta = 0;
        if (!stack.hasTagCompound())
            return false;
        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int yPos = y - 1; yPos <= y + 1; yPos++)
            {
                for (int zPos = z - 1; zPos <= z + 1; zPos++)
                {
                    if (!(tags.getBoolean("Broken")))
                    {
                        boolean cancelHarvest = false;
                        for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                        {
                            if (mod.beforeBlockBreak(this, stack, xPos, yPos, zPos, player))
                                cancelHarvest = true;
                        }

                        if (!cancelHarvest)
                        {
                            int localblockID = world.getBlockId(xPos, yPos, zPos);
                            Block block = Block.blocksList[localblockID];
                            if (block != null)// && (block.blockMaterial == Material.leaves || block.isLeaves(world, xPos, yPos, zPos)))
                            {
                                for (int iter = 0; iter < materials.length; iter++)
                                {
                                    if (materials[iter] == block.blockMaterial)
                                    {
                                        meta = world.getBlockMetadata(xPos, yPos, zPos);
                                        /*world.setBlockToAir(xPos, yPos, zPos);
                                        if (!player.capabilities.isCreativeMode)
                                        {
                                            block.harvestBlock(world, player, xPos, yPos, zPos, meta);
                                            onBlockDestroyed(stack, world, localblockID, xPos, yPos, zPos, player);
                                        }
                                        blockID = localblockID;*/
                                        if (!player.capabilities.isCreativeMode)
                                        {
                                            block.harvestBlock(world, player, xPos, yPos, zPos, meta);
                                            block.onBlockHarvested(world, x, y, z, meta, player);
                                            onBlockDestroyed(stack, world, localblockID, xPos, yPos, zPos, player);
                                        }
                                        world.setBlockToAir(xPos, yPos, zPos);
                                        blockID = localblockID;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (!world.isRemote)
        world.playAuxSFX(2001, x, y, z, blockID + (meta << 12));
        return super.onBlockStartBreak(stack, x, y, z, player);
    }
	

    public boolean onLeftClickEntity (ItemStack stack, EntityPlayer player, Entity entity)
    {
        AxisAlignedBB box = AxisAlignedBB.getAABBPool().getAABB(entity.posX, entity.posY, entity.posZ, entity.posX + 1.0D, entity.posY + 1.0D, entity.posZ + 1.0D).expand(1.0D, 1.0D, 1.0D);
        List list = player.worldObj.getEntitiesWithinAABBExcludingEntity(player, box);
        for (Object o : list)
        {
            AbilityHelper.onLeftClickEntity(stack, player, (Entity) o, this);
        }
        return true;
    }
	
}

package tconstruct.items.tools;

import java.util.ArrayList;
import java.util.List;

import tconstruct.common.TContent;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.crafting.ToolBuilder;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.HarvestTool;

import mods.battlegear2.items.ItemShield;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Hammer extends HarvestTool
{
    public Hammer(int itemID)
    {
        super(itemID, 2);
        this.setUnlocalizedName("InfiTool.Hammer");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderPasses (int metadata)
    {
        return 10;
    }

    @Override
    public int getPartAmount ()
    {
        return 4;
    }

    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    public int durabilityTypeExtra ()
    {
        return 2;
    }

    @Override
    protected String getHarvestType ()
    {
        return "pickaxe";
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    static Material[] materials = new Material[] { Material.rock, Material.iron, Material.ice, Material.glass, Material.piston, Material.anvil };

    @Override
    public Item getHeadItem ()
    {
        return TContent.hammerHead;
    }

    @Override
    public Item getHandleItem ()
    {
        return TContent.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TContent.largePlate;
    }

    @Override
    public Item getExtraItem ()
    {
        return TContent.largePlate;
    }

    public float getDurabilityModifier ()
    {
        return 4.5f;
    }

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_hammer_head";
        case 1:
            return "_hammer_handle_broken";
        case 2:
            return "_hammer_handle";
        case 3:
            return "_hammer_front";
        case 4:
            return "_hammer_back";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_hammer_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "hammer";
    }

    @Override
    public Icon getIcon (ItemStack stack, int renderPass)
    {
        NBTTagCompound tags = stack.getTagCompound();

        if (tags != null)
        {
            tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (renderPass < getPartAmount())
            {
                if (renderPass == 0) // Handle
                {
                    if (tags.getBoolean("Broken"))
                        return (brokenIcons.get(tags.getInteger("RenderHandle")));
                    return handleIcons.get(tags.getInteger("RenderHandle"));
                }

                else if (renderPass == 1) // Head
                {
                    return (headIcons.get(tags.getInteger("RenderHead")));
                }

                else if (renderPass == 2) // Accessory
                {
                    return (accessoryIcons.get(tags.getInteger("RenderAccessory")));
                }

                else if (renderPass == 3) // Extra
                {
                    return (extraIcons.get(tags.getInteger("RenderExtra")));
                }
            }

            else
            {
                if (renderPass == getPartAmount())
                {
                    if (tags.hasKey("Effect1"))
                        return (effectIcons.get(tags.getInteger("Effect1")));
                }

                else if (renderPass == getPartAmount() + 1)
                {
                    if (tags.hasKey("Effect2"))
                        return (effectIcons.get(tags.getInteger("Effect2")));
                }

                else if (renderPass == getPartAmount() + 2)
                {
                    if (tags.hasKey("Effect3"))
                        return (effectIcons.get(tags.getInteger("Effect3")));
                }

                else if (renderPass == getPartAmount() + 3)
                {
                    if (tags.hasKey("Effect4"))
                        return (effectIcons.get(tags.getInteger("Effect4")));
                }

                else if (renderPass == getPartAmount() + 4)
                {
                    if (tags.hasKey("Effect5"))
                        return (effectIcons.get(tags.getInteger("Effect5")));
                }

                else if (renderPass == getPartAmount() + 5)
                {
                    if (tags.hasKey("Effect6"))
                        return (effectIcons.get(tags.getInteger("Effect6")));
                }
            }
            return blankSprite;
        }
        return emptyIcon;
    }

    @Override
    public void getSubItems (int id, CreativeTabs tab, List list)
    {
        super.getSubItems(id, tab, list);

        ItemStack tool = ToolBuilder.instance.buildTool(new ItemStack(getHeadItem(), 1, 10), new ItemStack(getHandleItem(), 1, 8), new ItemStack(getAccessoryItem(), 1, 11), new ItemStack(
                getExtraItem(), 1, 11), "InfiMiner");

        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setInteger("Modifiers", 0);
        tags.setInteger("Attack", Integer.MAX_VALUE / 100);
        tags.setInteger("TotalDurability", Integer.MAX_VALUE / 100);
        tags.setInteger("BaseDurability", Integer.MAX_VALUE / 100);
        tags.setInteger("MiningSpeed", Integer.MAX_VALUE / 100);
        tags.setInteger("Unbreaking", 10);

        tags.setBoolean("Built", true);
        list.add(tool);
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        if (!stack.hasTagCompound())
            return false;

        World world = player.worldObj;
        final int blockID = world.getBlockId(x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);
        final Block block = Block.blocksList[blockID];
        if (!stack.hasTagCompound())
            return false;

        if (block == null)
            return super.onBlockStartBreak(stack, x, y, z, player);

        float blockHardness = block.getBlockHardness(world, x, y, z);

        boolean validStart = false;
        for (int iter = 0; iter < materials.length; iter++)
        {
            if (materials[iter] == block.blockMaterial)
            {
                validStart = true;
                break;
            }
        }

        if (block == Block.silverfish)
            validStart = true;

        MovingObjectPosition mop = AbilityHelper.raytraceFromEntity(world, player, true, 4.5D);
        if (mop == null || !validStart)
            return super.onBlockStartBreak(stack, x, y, z, player);

        int xRange = 1;
        int yRange = 1;
        int zRange = 1;
        switch (mop.sideHit)
        {
        case 0:
        case 1:
            yRange = 0;
            break;
        case 2:
        case 3:
            zRange = 0;
            break;
        case 4:
        case 5:
            xRange = 0;
            break;
        }

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        int toolLevel = tags.getInteger("HarvestLevel");
        for (int xPos = x - xRange; xPos <= x + xRange; xPos++)
        {
            for (int yPos = y - yRange; yPos <= y + yRange; yPos++)
            {
                for (int zPos = z - zRange; zPos <= z + zRange; zPos++)
                {
                    if (!(tags.getBoolean("Broken")))
                    {
                        int localblockID = world.getBlockId(xPos, yPos, zPos);
                        Block localBlock = Block.blocksList[localblockID];
                        int localMeta = world.getBlockMetadata(xPos, yPos, zPos);
                        int hlvl = MinecraftForge.getBlockHarvestLevel(localBlock, localMeta, getHarvestType());
                        float localHardness = localBlock == null ? Float.MAX_VALUE : localBlock.getBlockHardness(world, xPos, yPos, zPos);

                        if (hlvl <= toolLevel && localHardness - 1.5 <= blockHardness)
                        {
                            boolean cancelHarvest = false;
                            for (ActiveToolMod mod : TConstructRegistry.activeModifiers)
                            {
                                if (mod.beforeBlockBreak(this, stack, xPos, yPos, zPos, player))
                                    cancelHarvest = true;
                            }

                            if (!cancelHarvest)
                            {
                                if (localBlock != null && !(localHardness < 0))
                                {
                                    for (int iter = 0; iter < materials.length; iter++)
                                    {
                                        if (materials[iter] == localBlock.blockMaterial || localBlock == Block.silverfish)
                                        {
                                            if (!player.capabilities.isCreativeMode)
                                            {
                                                if (localBlock.removeBlockByPlayer(world, player, xPos, yPos, zPos))
                                                {
                                                    localBlock.onBlockDestroyedByPlayer(world, xPos, yPos, zPos, localMeta);
                                                }
                                                localBlock.harvestBlock(world, player, xPos, yPos, zPos, localMeta);
                                                localBlock.onBlockHarvested(world, xPos, yPos, zPos, localMeta, player);
                                                if (blockHardness > 0f)
                                                    onBlockDestroyed(stack, world, localblockID, xPos, yPos, zPos, player);
                                            }
                                            else
                                            {
                                                world.setBlockToAir(xPos, yPos, zPos);
                                            }
                                        }
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
        return true;
    }

    @Override
    public float getStrVsBlock (ItemStack stack, Block block, int meta)
    {
        if (!stack.hasTagCompound())
            return 1.0f;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return 0.1f;

        Material[] materials = getEffectiveMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.blockMaterial)
            {
                return getblockSpeed(tags, block, meta);
            }
        }

        /*if (block == Block.silverfish)
            return getblockSpeed(tags, block, meta);*/

        return super.getStrVsBlock(stack, block, meta);
    }

    float getblockSpeed (NBTTagCompound tags, Block block, int meta)
    {
        float mineSpeed = tags.getInteger("MiningSpeed");
        int heads = 1;
        if (tags.hasKey("MiningSpeed2"))
        {
            mineSpeed += tags.getInteger("MiningSpeed2");
            heads++;
        }
        if (tags.hasKey("MiningSpeedHandle"))
        {
            mineSpeed += tags.getInteger("MiningSpeedHandle");
            heads++;
        }

        if (tags.hasKey("MiningSpeedExtra"))
        {
            mineSpeed += tags.getInteger("MiningSpeedExtra");
            heads++;
        }
        float trueSpeed = mineSpeed / (heads * 300f);
        int hlvl = MinecraftForge.getBlockHarvestLevel(block, meta, getHarvestType());
        int durability = tags.getInteger("Damage");

        float stonebound = tags.getFloat("Shoddy");
        float bonusLog = (float) Math.log(durability / 216f + 1) * 2 * stonebound;
        trueSpeed += bonusLog;

        if (hlvl <= tags.getInteger("HarvestLevel"))
            return trueSpeed;
        return 0.1f;
    }

    /*@Override
    public void onUpdate (ItemStack stack, World world, Entity entity, int par4, boolean par5)
    {
        super.onUpdate(stack, world, entity, par4, par5);
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack equipped = player.getCurrentEquippedItem();
            if (equipped == stack)
            {
                player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 1, 1));
            }
        }
    }*/

    @Override
    public String[] toolCategories ()
    {
        return new String[] { "weapon", "harvest", "melee", "bludgeoning" };
    }

	//1.6.4 start
	@Override
    public boolean allowOffhand(ItemStack mainhand, ItemStack offhand)
    {
    	try
    	{
    		if (offhand.getItem() instanceof ItemShield)
    		{
    			return true;
    		}
    		else return false;
    	}
    	catch (Exception e)
    	{
    		return false;
    	}
    }

    @Override
    public boolean isOffhandHandDual(ItemStack off)
    {
        return false;
    }

    @Override
    public boolean sheatheOnBack(ItemStack item)
    {
        return true;
    }
    //1.6.4 end
    
    //1.6.2 start
    @Override
	public boolean willAllowOffhandWeapon() {
		return false;
	}

	@Override
	public boolean isOffhandHandDualWeapon() {
		return false;
	}

	@Override
	public boolean sheatheOnBack() {
		return true;
	}
	//1.6.2 end
}

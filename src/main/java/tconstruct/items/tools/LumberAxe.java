package tconstruct.items.tools;

import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.HarvestTool;
import tconstruct.tools.TinkerTools;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LumberAxe extends HarvestTool
{
    public LumberAxe()
    {
        super(0);
        this.setUnlocalizedName("InfiTool.LumberAxe");
    }

    @Override
    protected Material[] getEffectiveMaterials ()
    {
        return materials;
    }

    @Override
    protected String getHarvestType ()
    {
        return "axe";
    }

    @Override
    public float getRepairCost ()
    {
        return 4.0f;
    }

    @Override
    public float getDurabilityModifier ()
    {
        return 2.5f;
    }

    @Override
    public String getToolName ()
    {
        return "Lumber Axe";
    }

    @Override
    public boolean onBlockDestroyed (ItemStack itemstack, World world, Block block, int x, int y, int z, EntityLivingBase player)
    {
        if (block != null && block.getMaterial() == Material.leaves)
            return false;

        return AbilityHelper.onBlockChanged(itemstack, world, block, x, y, z, player, random);
    }

    static Material[] materials = { Material.wood, Material.vine, Material.circuits, Material.cactus };// TODO find this//,
                                                                                                       // Material.pumpkin };

    /* Lumber axe specific */

    /*
     * @Override public void onUpdate (ItemStack stack, World world, Entity
     * entity, int par4, boolean par5) { super.onUpdate(stack, world, entity,
     * par4, par5); if (entity instanceof EntityPlayer) { EntityPlayer player =
     * (EntityPlayer) entity; ItemStack equipped =
     * player.getCurrentEquippedItem(); if (equipped == stack) {
     * player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, 1, 1)); }
     * } }
     */

    @Override
    public float getDigSpeed (ItemStack stack, Block block, int meta)
    {
        if (!stack.hasTagCompound())
            return 1.0f;

        NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
        if (tags.getBoolean("Broken"))
            return 0.1f;

        Material[] materials = getEffectiveMaterials();
        for (int i = 0; i < materials.length; i++)
        {
            if (materials[i] == block.getMaterial())
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
                int hlvl = block.getHarvestLevel(meta);
                int durability = tags.getInteger("Damage");

                float stonebound = tags.getFloat("Shoddy");
                float bonusLog = (float) Math.log(durability / 72f + 1) * 2 * stonebound;
                trueSpeed += bonusLog;

                if (hlvl <= tags.getInteger("HarvestLevel"))
                    return trueSpeed;
                return 0.1f;
            }
        }
        return super.getDigSpeed(stack, block, meta);
    }

    @Override
    public boolean onBlockStartBreak (ItemStack stack, int x, int y, int z, EntityPlayer player)
    {
        if (!stack.hasTagCompound())
            return false;

        if (player instanceof EntityPlayerMP)
        {
            EntityPlayerMP mplayer = (EntityPlayerMP) player;
            NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");
            if (!tags.hasKey("AOEBreaking") || !tags.getBoolean("AOEBreaking"))
            {
                tags.setBoolean("AOEBreaking", true);
                World world = player.worldObj;
                Block block = world.getBlock(x, y, z);
                if (block.isWood(world, x, y, z))
                {
                    int height = y;
                    while (true)
                    {
                        height++;
                        if (block != world.getBlock(x, height, z))
                        {
                            height--;
                            break;
                        }
                    }

                    int numLeaves = 0;
                    if (height - y < 50)
                    {
                        for (int xPos = x - 1; xPos <= x + 1; xPos++)
                        {
                            for (int zPos = z - 1; zPos <= z + 1; zPos++)
                            {
                                for (int yPos = height - 1; yPos <= height + 1; yPos++)
                                {
                                    Block leaf = world.getBlock(xPos, yPos, zPos);
                                    if (leaf != null && leaf.isLeaves(world, xPos, yPos, zPos))
                                        numLeaves++;
                                }
                            }
                        }
                    }

                    if (numLeaves > 3)
                        breakTree(world, x, y, z, stack, tags, block, world.getBlockMetadata(x, y, z), mplayer);
                    else
                        destroyWood(world, x, y, z, stack, tags, mplayer);
                }
                else if (block.getMaterial() == Material.wood)
                {
                    destroyWood(world, x, y, z, stack, tags, mplayer);
                }
                tags.setBoolean("AOEBreaking", false);
            }
        }
        return super.onBlockStartBreak(stack, x, y, z, player);
    }

    void breakTree (World world, int x, int y, int z, ItemStack stack, NBTTagCompound tags, Block bID, int meta, EntityPlayerMP player)
    {
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int yPos = y; yPos <= y + 1; yPos++)
            {
                for (int zPos = z - 1; zPos <= z + 1; zPos++)
                {
                    Block block = world.getBlock(xPos, yPos, zPos);
                    if (bID == block && world.getBlockMetadata(xPos, yPos, zPos) == meta)
                    {
                        if (block.getPlayerRelativeBlockHardness(player, world, x, yPos, z) > 0)
                            player.theItemInWorldManager.tryHarvestBlock(x, yPos, z);
                        breakTree(world, xPos, yPos, zPos, stack, tags, bID, meta, player);
                    }
                }
            }
        }
    }

    void destroyWood (World world, int x, int y, int z, ItemStack stack, NBTTagCompound tags, EntityPlayerMP player)
    {
        for (int xPos = x - 1; xPos <= x + 1; xPos++)
        {
            for (int yPos = y - 1; yPos <= y + 1; yPos++)
            {
                for (int zPos = z - 1; zPos <= z + 1; zPos++)
                {
                    Block block = world.getBlock(xPos, yPos, zPos);
                    if (block != null && block.getMaterial() == Material.wood
                        && block.getPlayerRelativeBlockHardness(player, world, xPos, yPos, zPos) > 0)
                        player.theItemInWorldManager.tryHarvestBlock(xPos, yPos, zPos);
                }
            }
        }
    }

    @Override
    public Item getHeadItem ()
    {
        return TinkerTools.broadAxeHead;
    }

    @Override
    public Item getHandleItem ()
    {
        return TinkerTools.toughRod;
    }

    @Override
    public Item getAccessoryItem ()
    {
        return TinkerTools.largePlate;
    }

    @Override
    public Item getExtraItem ()
    {
        return TinkerTools.toughBinding;
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

    @Override
    public String getIconSuffix (int partType)
    {
        switch (partType)
        {
        case 0:
            return "_lumberaxe_head";
        case 1:
            return "_lumberaxe_head_broken";
        case 2:
            return "_lumberaxe_handle";
        case 3:
            return "_lumberaxe_shield";
        case 4:
            return "_lumberaxe_binding";
        default:
            return "";
        }
    }

    @Override
    public String getEffectSuffix ()
    {
        return "_lumberaxe_effect";
    }

    @Override
    public String getDefaultFolder ()
    {
        return "lumberaxe";
    }

    @Override
    public int durabilityTypeAccessory ()
    {
        return 2;
    }

    @Override
    public int durabilityTypeExtra ()
    {
        return 1;
    }
}

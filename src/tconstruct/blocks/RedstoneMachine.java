package tconstruct.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.AdvancedDrawbridgeLogic;
import tconstruct.blocks.logic.DrawbridgeLogic;
import tconstruct.blocks.logic.FirestarterLogic;
import tconstruct.client.block.MachineRender;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.blocks.InventoryBlock;
import tconstruct.library.util.CoordTuple;
import tconstruct.library.util.IActiveLogic;
import tconstruct.library.util.IFacingLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RedstoneMachine extends InventoryBlock
{
    public RedstoneMachine(int id)
    {
        super(id, Material.iron);
        this.setCreativeTab(TConstructRegistry.blockTab);
        setHardness(12);
        setStepSound(soundMetalFootstep);
    }

    @Override
    public int getLightValue (IBlockAccess world, int x, int y, int z)
    {
        if (world.getBlockMetadata(x, y, z) == 0 || world.getBlockMetadata(x, y, z) == 2)
        {
            TileEntity logic = world.getBlockTileEntity(x, y, z);

            if (logic != null && logic instanceof DrawbridgeLogic)
            {
                if (((DrawbridgeLogic) logic).getStackInSlot(1) != null)
                {
                    ItemStack stack = ((DrawbridgeLogic) logic).getStackInSlot(1);
                    if (stack.itemID < 4096 && Block.blocksList[stack.itemID] != null)
                        return lightValue[stack.itemID];
                }
            }

            if (logic != null && logic instanceof AdvancedDrawbridgeLogic)
            {
                if (((AdvancedDrawbridgeLogic) logic).camoInventory.getCamoStack() != null)
                {
                    ItemStack stack = ((AdvancedDrawbridgeLogic) logic).camoInventory.getCamoStack();
                    if (stack.itemID < 4096 && Block.blocksList[stack.itemID] != null)
                        return lightValue[stack.itemID];
                }
            }
        }
        return super.getLightValue(world, x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int colorMultiplier (IBlockAccess world, int x, int y, int z)
    {
        if (world.getBlockMetadata(x, y, z) == 0 && world.getBlockMetadata(x, y, z) == 2)
        {
            TileEntity logic = world.getBlockTileEntity(x, y, z);

            if (logic != null && logic instanceof DrawbridgeLogic)
            {
                ItemStack stack = ((DrawbridgeLogic) logic).getStackInSlot(1);
                if (stack != null && stack.itemID < 4096 && Block.blocksList[stack.itemID] != null && stack.itemID != this.blockID)
                    return Block.blocksList[stack.itemID].colorMultiplier(world, x, y, z);
            }
            else if (logic != null && logic instanceof AdvancedDrawbridgeLogic)
            {
                ItemStack stack = ((AdvancedDrawbridgeLogic) logic).camoInventory.getCamoStack();
                if (stack != null && stack.itemID < 4096 && Block.blocksList[stack.itemID] != null && stack.itemID != this.blockID)
                    return Block.blocksList[stack.itemID].colorMultiplier(world, x, y, z);
            }
        }

        return 0xffffff;
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        switch (metadata)
        {
        case 0:
            return new DrawbridgeLogic();
        case 1:
            return new FirestarterLogic();
        case 2:
            return new AdvancedDrawbridgeLogic();
        default:
            return null;
        }
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        int meta = world.getBlockMetadata(x, y, z);
        switch (meta)
        {
        case 0:
            return TConstruct.proxy.drawbridgeID;
        case 2:
            return TConstruct.proxy.advDrawbridgeID;
        }
        return null;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    /* Rendering */

    @Override
    public String[] getTextureNames ()
    {
        String[] textureNames = { "drawbridge_top", "drawbridge_side", "drawbridge_bottom", "drawbridge_top_face", "drawbridge_side_face", "drawbridge_bottom_face", "firestarter_top",
                "firestarter_side", "firestarter_bottom" };

        return textureNames;
    }

    @Override
    public void registerIcons (IconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon("tinker:machines/" + textureNames[i]);
        }
    }

    @Override
    public Icon getIcon (int side, int meta)
    {
        if (meta == 0 || meta == 2)
        {
            if (side == 5)
                return icons[5];
            return icons[getTextureIndex(side)];
        }
        if (meta == 1)
        {
            return icons[getTextureIndex(side) + 6];
        }
        return icons[0];
    }

    public Icon getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity logic = world.getBlockTileEntity(x, y, z);
        short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0)
        {
            DrawbridgeLogic drawbridge = (DrawbridgeLogic) logic;
            ItemStack stack = drawbridge.getStackInSlot(1);
            if (stack != null && stack.itemID < 4096)
            {
                Block block = Block.blocksList[stack.itemID];
                if (block != null && block.renderAsNormalBlock())
                    return block.getIcon(side, stack.getItemDamage());
            }
            if (side == direction)
            {
                return icons[getTextureIndex(side) + 3];
            }
            else
            {
                return icons[getTextureIndex(side)];
            }
        }

        if (meta == 2)
        {
            AdvancedDrawbridgeLogic drawbridge = (AdvancedDrawbridgeLogic) logic;
            ItemStack stack = drawbridge.camoInventory.getCamoStack();
            if (stack != null && stack.itemID < 4096)
            {
                Block block = Block.blocksList[stack.itemID];
                if (block != null && block.renderAsNormalBlock())
                    return block.getIcon(side, stack.getItemDamage());
            }
            if (side == direction)
            {
                return icons[getTextureIndex(side) + 3];
            }
            else
            {
                return icons[getTextureIndex(side)];
            }
        }

        if (meta == 1)
        {
            if (side == direction)
            {
                return icons[6];
            }
            else if (side / 2 == direction / 2)
            {
                return icons[8];
            }
            return icons[7];
        }
        return icons[0];
    }

    public int getTextureIndex (int side)
    {
        if (side == 0)
            return 2;
        if (side == 1)
            return 0;

        return 1;
    }

    public int getRenderType ()
    {
        return MachineRender.model;
    }

    public boolean isFireSource (World world, int x, int y, int z, int metadata, ForgeDirection side)
    {
        if (metadata == 1)
            return side == ForgeDirection.UP;
        return false;
    }

    @Override
    public void getSubBlocks (int id, CreativeTabs tab, List list)
    {
        for (int iter = 0; iter < 3; iter++)
        {
            list.add(new ItemStack(id, 1, iter));
        }
    }

    /* Redstone */
    public void onNeighborBlockChange (World world, int x, int y, int z, int neighborBlockID)
    {
        IActiveLogic logic = (IActiveLogic) world.getBlockTileEntity(x, y, z);
        IFacingLogic facing = (IFacingLogic) logic;
        int direction = facing.getRenderDirection();
        boolean active = false;
        for (int i = 0; i < 6; i++)
        {
            if (direction == i)
                continue;

            CoordTuple coord = directions.get(i);
            if (this.getIndirectPowerLevelTo(world, x + coord.x, y + coord.y, z + coord.z, i) > 0 || activeRedstone(world, coord.x, y + coord.y, z + coord.z))
            {
                active = true;
                break;
            }
        }
        logic.setActive(active);
    }

    public int getIndirectPowerLevelTo (World world, int x, int y, int z, int side)
    {
        if (world.isBlockNormalCube(x, y, z))
        {
            return world.getBlockPowerInput(x, y, z);
        }
        else
        {
            int i1 = world.getBlockId(x, y, z);
            return i1 == 0 ? 0 : Block.blocksList[i1].isProvidingWeakPower(world, x, y, z, side);
        }
    }

    boolean activeRedstone (World world, int x, int y, int z)
    {
        Block wire = Block.blocksList[world.getBlockId(x, y, z)];
        if (wire != null && wire.blockID == Block.redstoneWire.blockID)
            return world.getBlockMetadata(x, y, z) > 0;

        return false;
    }

    /* Keep inventory */
    @Override
    public boolean removeBlockByPlayer (World world, EntityPlayer player, int x, int y, int z)
    {
        player.addExhaustion(0.025F);
        int meta = world.getBlockMetadata(x, y, z);
        if (meta == 0)
        {
            ItemStack stack = new ItemStack(this.blockID, 1, meta);
            DrawbridgeLogic logic = (DrawbridgeLogic) world.getBlockTileEntity(x, y, z);
            NBTTagCompound tag = new NBTTagCompound();

            boolean hasTag = false;
            ItemStack contents = logic.getStackInSlot(0);
            if (contents != null)
            {
                NBTTagCompound contentTag = new NBTTagCompound();
                contents.writeToNBT(contentTag);
                tag.setCompoundTag("Contents", contentTag);
                hasTag = true;
            }

            ItemStack camo = logic.getStackInSlot(1);
            if (camo != null)
            {
                NBTTagCompound camoTag = new NBTTagCompound();
                camo.writeToNBT(camoTag);
                tag.setCompoundTag("Camoflauge", camoTag);
                hasTag = true;
            }

            if (logic.getPlacementDirection() != 4)
            {
                tag.setByte("Placement", logic.getPlacementDirection());
                hasTag = true;
            }
            if (hasTag == true)
                stack.setTagCompound(tag);

            dropDrawbridgeLogic(world, x, y, z, stack);
        }

        return world.setBlockToAir(x, y, z);
    }

    protected void dropDrawbridgeLogic (World world, int x, int y, int z, ItemStack stack)
    {
        if (!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops"))
        {
            float f = 0.7F;
            double d0 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
            EntityItem entityitem = new EntityItem(world, (double) x + d0, (double) y + d1, (double) z + d2, stack);
            entityitem.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(entityitem);
        }
    }

    @Override
    public void harvestBlock (World world, EntityPlayer player, int x, int y, int z, int meta)
    {
        if (meta != 0)
            super.harvestBlock(world, player, x, y, z, meta);
    }

    @Override
    public void onBlockPlacedBy (World world, int x, int y, int z, EntityLivingBase living, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, living, stack);
        if (stack.hasTagCompound())
        {
            DrawbridgeLogic logic = (DrawbridgeLogic) world.getBlockTileEntity(x, y, z);
            NBTTagCompound contentTag = stack.getTagCompound().getCompoundTag("Contents");
            if (contentTag != null)
            {
                ItemStack contents = ItemStack.loadItemStackFromNBT(contentTag);
                logic.setInventorySlotContents(0, contents);
            }

            NBTTagCompound camoTag = stack.getTagCompound().getCompoundTag("Camoflauge");
            if (camoTag != null)
            {
                ItemStack camoflauge = ItemStack.loadItemStackFromNBT(camoTag);
                logic.setInventorySlotContents(1, camoflauge);
            }

            if (stack.getTagCompound().hasKey("Placement"))
            {
                logic.setPlacementDirection(stack.getTagCompound().getByte("Placement"));
            }
        }
    }

    /* Redstone connections */

    public boolean canConnectRedstone (IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    static ArrayList<CoordTuple> directions = new ArrayList<CoordTuple>(6);

    static
    {
        directions.add(new CoordTuple(0, -1, 0));
        directions.add(new CoordTuple(0, 1, 0));
        directions.add(new CoordTuple(0, 0, -1));
        directions.add(new CoordTuple(0, 0, 1));
        directions.add(new CoordTuple(-1, 0, 0));
        directions.add(new CoordTuple(1, 0, 0));
    }
}

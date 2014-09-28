package tconstruct.tools.blocks;

import cpw.mods.fml.relauncher.*;
import java.util.Random;
import mantle.blocks.abstracts.InventorySlab;
import mantle.blocks.iface.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.tools.ToolProxyCommon;
import tconstruct.tools.logic.FurnaceLogic;

public class FurnaceSlab extends InventorySlab
{

    public FurnaceSlab(Material material)
    {
        super(material);
        this.setCreativeTab(TConstructRegistry.blockTab);
        this.setHardness(3.5f);
    }

    @Override
    public TileEntity createTileEntity (World world, int metadata)
    {
        switch (metadata % 8)
        {
        case 0:
            return new FurnaceLogic();
        }
        return null;
    }

    @Override
    public Integer getGui (World world, int x, int y, int z, EntityPlayer entityplayer)
    {
        return ToolProxyCommon.furnaceID;
    }

    @Override
    public Object getModInstance ()
    {
        return TConstruct.instance;
    }

    @Override
    public String[] getTextureNames ()
    {
        String[] textureNames = { "furnaceslab_front", "furnaceslab_front_active", "furnaceslab_side", "furnace_top" };

        return textureNames;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (int side, int meta)
    {
        return icons[(meta % 8) * 3 + getTextureIndex(side)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon (IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity logic = world.getTileEntity(x, y, z);
        short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
        int meta = world.getBlockMetadata(x, y, z) % 8;

        if (meta == 0)
        {
            if (side == direction)
            {
                if (((IActiveLogic) logic).getActive())
                    return icons[1];
                else
                    return icons[0];
            }
            else if (side > 1)
            {
                return icons[2];
            }
            return icons[3];
        }
        return icons[0];
    }

    public int getTextureIndex (int side)
    {
        if (side == 0 || side == 1)
            return 3;
        if (side == 3)
            return 0;

        return 2;
    }

    @Override
    public String getTextureDomain (int textureNameIndex)
    {
        return "tinker";
    }

    @Override
    public void registerBlockIcons (IIconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            if (i == 3)
                this.icons[i] = iconRegister.registerIcon("minecraft:" + textureNames[i]);
            else
                this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    public TileEntity createNewTileEntity (World var1, int metadata)
    {
        switch (metadata % 8)
        {
        case 0:
            return new FurnaceLogic();
        }
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick (World world, int x, int y, int z, Random random)
    {
        TileEntity logic = world.getTileEntity(x, y, z);
        short direction = (logic instanceof IFacingLogic) ? ((IFacingLogic) logic).getRenderDirection() : 0;
        int meta = world.getBlockMetadata(x, y, z);
        int metaType = meta % 8;
        int metaPos = meta / 8;

        if (metaType == 0)
        {
            if (((IActiveLogic) logic).getActive())
            {
                float offset = random.nextFloat() * 0.6F - 0.3F;
                float offsetY = random.nextFloat() * 6.0F / 16.0F;

                if (metaPos == 1)
                {
                    offsetY += 0.5F;
                }

                if (direction == 4)
                {
                    world.spawnParticle("smoke", x - 0.02F, y + offsetY, z + offset + 0.5F, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", x - 0.02F, y + offsetY, z + offset + 0.5F, 0.0D, 0.0D, 0.0D);
                }
                else if (direction == 5)
                {
                    world.spawnParticle("smoke", x + 1.02F, y + offsetY, z + offset + 0.5F, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", x + 1.02F, y + offsetY, z + offset + 0.5F, 0.0D, 0.0D, 0.0D);
                }
                else if (direction == 2)
                {
                    world.spawnParticle("smoke", x + offset + 0.5F, y + offsetY, z - 0.02F, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", x + offset + 0.5F, y + offsetY, z - 0.02F, 0.0D, 0.0D, 0.0D);
                }
                else if (direction == 3)
                {
                    world.spawnParticle("smoke", x + offset + 0.5F, y + offsetY, z + 1.02F, 0.0D, 0.0D, 0.0D);
                    world.spawnParticle("flame", x + offset + 0.5F, y + offsetY, z + 1.02F, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}

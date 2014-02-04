package tconstruct.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.FurnaceLogic;
import tconstruct.common.TProxyCommon;
import tconstruct.library.TConstructRegistry;
import mantle.blocks.abstracts.InventorySlab;
import mantle.blocks.iface.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;

public class FurnaceSlab extends InventorySlab
{

    public FurnaceSlab(Material material)
    {
        super(material);
        this.func_149647_a(TConstructRegistry.blockTab);
        this.func_149711_c(3.5f);
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
        return TProxyCommon.furnaceID;
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
    public IIcon func_149691_a (int side, int meta)
    {
        return icons[(meta % 8) * 3 + getTextureIndex(side)];
    }

    public IIcon func_149673_e (IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity logic = world.func_147438_o(x, y, z);
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
    public void func_149651_a (IIconRegister iconRegister)
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
    public TileEntity func_149915_a (World var1, int metadata)
    {
        switch (metadata % 8)
        {
        case 0:
            return new FurnaceLogic();
        }
        return null;
    }
}

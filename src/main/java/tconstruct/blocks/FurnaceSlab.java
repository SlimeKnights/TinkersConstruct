package tconstruct.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.TConstruct;
import tconstruct.blocks.logic.FurnaceLogic;
import tconstruct.common.TProxyCommon;
import tconstruct.library.TConstructRegistry;
import mantle.blocks.abstracts.InventorySlab;
import tconstruct.library.util.IActiveLogic;
import mantle.blocks.iface.IFacingLogic;

public class FurnaceSlab extends InventorySlab
{

    public FurnaceSlab(int id, Material material)
    {
        super(id, material);
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
    public Icon getIcon (int side, int meta)
    {
        return icons[(meta % 8) * 3 + getTextureIndex(side)];
    }

    public Icon getBlockTexture (IBlockAccess world, int x, int y, int z, int side)
    {
        TileEntity logic = world.getBlockTileEntity(x, y, z);
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
    public void registerIcons (IconRegister iconRegister)
    {
        String[] textureNames = getTextureNames();
        this.icons = new Icon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            if (i == 3)
                this.icons[i] = iconRegister.registerIcon("minecraft:" + textureNames[i]);
            else
                this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }
}

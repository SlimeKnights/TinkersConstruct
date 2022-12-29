package tconstruct.smeltery.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import tconstruct.library.TConstructRegistry;
import tconstruct.smeltery.TinkerSmeltery;
import tconstruct.smeltery.logic.CastingChannelLogic;
import tconstruct.smeltery.model.BlockRenderCastingChannel;

/**
 * @author BluSunrize
 */
public class CastingChannelBlock extends BlockContainer {

    public CastingChannelBlock() {
        super(Material.rock);
        this.setHardness(1F);
        this.setResistance(10);
        this.stepSound = soundTypeStone;
        setCreativeTab(TConstructRegistry.blockTab);
    }

    public String[] textureNames = new String[] {"searedstone", "nether_searedstone"};
    public IIcon[] icons;

    /* Rendering */
    public String[] getTextureNames() {
        return textureNames;
    }

    @Override
    public boolean onBlockActivated(
            World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getCurrentEquippedItem();
        CastingChannelLogic tile = (CastingChannelLogic) world.getTileEntity(x, y, z);

        if (stack != null && stack.getItem() == Item.getItemFromBlock(TinkerSmeltery.castingChannel)) return false;
        else {
            tile.changeOutputs(player, side, hitX, hitY, hitZ);
            return true;
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        CastingChannelLogic tile = (CastingChannelLogic) world.getTileEntity(x, y, z);
        float minX = 0.3125F;
        float maxX = 0.6875F;
        float minZ = 0.3125F;
        float maxZ = 0.6875F;
        minZ = 0F;
        maxZ = 1F;
        minX = 0F;
        maxX = 1F;
        this.setBlockBounds(minX, 0.375F, minZ, maxX, 0.625F, maxZ);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        return true;
    }

    @Override
    public int getRenderType() {
        return BlockRenderCastingChannel.renderID;
    }

    public void registerBlockIcons(IIconRegister iconRegister) {
        this.icons = new IIcon[textureNames.length];

        for (int i = 0; i < this.icons.length; ++i) {
            this.icons[i] = iconRegister.registerIcon("tinker:" + textureNames[i]);
        }
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int metadata) {
        return new CastingChannelLogic();
    }

    @Override
    public void getSubBlocks(Item id, CreativeTabs tab, List list) {
        list.add(new ItemStack(id, 1, 0));
        list.add(new ItemStack(id, 1, 1));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if (meta == 0) return icons[0];
        else return icons[1];
    }
}

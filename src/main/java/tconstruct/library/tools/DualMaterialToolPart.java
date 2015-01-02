package tconstruct.library.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.tools.DynamicToolPart;
import tconstruct.library.util.TextureHelper;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;

import java.util.List;
import java.util.Map;

public class DualMaterialToolPart extends DynamicToolPart {
    public IIcon defaultIcon2;
    public IIcon[] icons2;

    public DualMaterialToolPart(String textureType, String name) {
        super(textureType, name);
    }

    public static ItemStack createDualMaterial(Item item, int mat1, int mat2)
    {
        ItemStack stack = new ItemStack(item, 1, mat1);
        NBTTagCompound tags = new NBTTagCompound();
        NBTTagCompound subtag = new NBTTagCompound();
        subtag.setInteger("Material2", mat2);

        tags.setTag("DualMat", subtag);
        stack.setTagCompound(tags);

        return stack;
    }

    public int getMaterialID2(ItemStack stack)
    {
        if(!stack.hasTagCompound())
            return -1;

        int id = stack.getTagCompound().getCompoundTag("DualMat").getInteger("Material2");

        if (TConstructRegistry.toolMaterials.keySet().contains(id))
            return id;

        return -1;
    }

    @Override
    public void getSubItems (Item item, CreativeTabs tab, List list)
    {
        // material id == metadata
        for(Integer matID : TConstructRegistry.toolMaterials.keySet())
            list.add(createDualMaterial(item, matID, TinkerTools.MaterialID.Iron));
    }

    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        super.registerIcons(iconRegister);

        // same as in super, secondary icon :I
        this.icons2 = new IIcon[icons.length];

        // register icon for each material that has one
        if(!PHConstruct.minimalTextures)
            for(Map.Entry<Integer, tconstruct.library.tools.ToolMaterial> entry : TConstructRegistry.toolMaterials.entrySet())
            {
                String tex = modTexPrefix + ":" + folder + entry.getValue().materialName.toLowerCase() + texture + "_2";
                if(TextureHelper.itemTextureExists(tex))
                    this.icons2[entry.getKey()] = iconRegister.registerIcon(tex);
            }

        this.defaultIcon2 = iconRegister.registerIcon(modTexPrefix + ":" + folder + texture + "_2");
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int meta, int pass) {
        // secondary item
        if(pass > 0) {
            if(meta > icons.length)
                return defaultIcon2;

            if(icons2[meta] == null)
                return defaultIcon2;

            return icons2[meta];
        }
        return super.getIconFromDamageForRenderPass(meta, pass);
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderpass) {
        if(renderpass > 0)
        {
            int matId = getMaterialID2(stack);
            if(matId > icons2.length || matId < 0)
                return super.getColorFromItemStack(stack, renderpass);

            if(icons[matId] == null)
                return TConstructRegistry.getMaterial(matId).primaryColor();

            return super.getColorFromItemStack(stack, renderpass);
        }
        return super.getColorFromItemStack(stack, renderpass);
    }
}

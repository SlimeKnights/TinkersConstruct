package tconstruct.library.tools;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mantle.items.abstracts.CraftingItem;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import tconstruct.TConstruct;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.util.IToolPart;
import tconstruct.library.util.TextureHelper;
import tconstruct.tools.TinkerTools;
import tconstruct.util.config.PHConstruct;

import java.util.List;
import java.util.Map;

public class DynamicToolPart extends CraftingItem implements IToolPart
{
    public String partName;
    public String texture;
    public IIcon defaultIcon;

    private boolean hidden = false;

    public DynamicToolPart(String texture, String name)
    {
        this(texture, name, "tinker");
    }

    public DynamicToolPart(String texture, String name, String domain)
    {
        super(null, null, "parts/", domain, TConstructRegistry.partTab);
        this.setUnlocalizedName("tconstruct." + name);
        this.partName = name;
        this.texture = texture;
    }

    /**
     * Doesn't add the item to creative tabs
     */
    public DynamicToolPart hide()
    {
        hidden = true;

        return this;
    }

    // item meta = material id
    @Override
    public int getMaterialID (ItemStack stack)
    {
        if (TConstructRegistry.toolMaterials.keySet().contains(stack.getItemDamage()))
            return stack.getItemDamage();

        return -1;
    }

    @Override
    public String getItemStackDisplayName (ItemStack stack)
    {
        tconstruct.library.tools.ToolMaterial toolmat = TConstructRegistry.getMaterial(getMaterialID(stack));
        if(toolmat == null)
            return super.getItemStackDisplayName(stack);

        String material = toolmat.localizationString.substring(9); // :(

        // custom name
        if (StatCollector.canTranslate("toolpart." + partName + "." + material))
        {
            return StatCollector.translateToLocal("toolpart." + partName + "." + material);
        }
        // general name
        else
        {
            return StatCollector.translateToLocal("toolpart." + partName).replaceAll("%%material", toolmat.prefixName());
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        int id = getMaterialID(stack);
        if(id == -1)
            return getUnlocalizedName();

        return "toolpart." + partName + "." + TConstructRegistry.getMaterial(id).materialName;
    }

    @Override
    public void getSubItems (Item item, CreativeTabs tab, List list)
    {
        if(hidden)
            return;

        // material id == metadata
        for(Integer matID : TConstructRegistry.toolMaterials.keySet())
            list.add(new ItemStack(item, 1, matID));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons (IIconRegister iconRegister)
    {
        // get the biggest material index
        int max = -1;
        for(Integer id : TConstructRegistry.toolMaterials.keySet())
            if(id > max)
                max = id;

        this.icons = new IIcon[max+1];

        // register icon for each material that has one
        if(!PHConstruct.minimalTextures)
            for(Map.Entry<Integer, tconstruct.library.tools.ToolMaterial> entry : TConstructRegistry.toolMaterials.entrySet())
            {
                String tex = modTexPrefix + ":" + folder + entry.getValue().materialName.toLowerCase() + texture;
                if(TextureHelper.itemTextureExists(tex))
                    this.icons[entry.getKey()] = iconRegister.registerIcon(tex);
            }

        // default texture
        this.defaultIcon = iconRegister.registerIcon(modTexPrefix + ":" + folder + texture);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage (int meta)
    {
        if(meta > icons.length)
            return defaultIcon;

        if(icons[meta] == null)
            return defaultIcon;

        return icons[meta];
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int renderpass) {
        int matId = getMaterialID(stack);
        if(matId > icons.length)
            return super.getColorFromItemStack(stack, renderpass);

        if(icons[matId] == null)
            return TConstructRegistry.getMaterial(matId).primaryColor();

        return super.getColorFromItemStack(stack, renderpass);
    }
}

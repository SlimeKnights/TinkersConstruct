package common.darkknight.jewelrycraft.client;

import net.minecraft.util.ResourceLocation;

import common.darkknight.jewelrycraft.CommonProxy;
import common.darkknight.jewelrycraft.renders.TileEntityDisplayerRender;
import common.darkknight.jewelrycraft.renders.TileEntityJewelrsCraftingTableRender;
import common.darkknight.jewelrycraft.renders.TileEntityMolderRender;
import common.darkknight.jewelrycraft.renders.TileEntitySmelterRender;
import common.darkknight.jewelrycraft.tileentity.TileEntityDisplayer;
import common.darkknight.jewelrycraft.tileentity.TileEntityJewelrsCraftingTable;
import common.darkknight.jewelrycraft.tileentity.TileEntityMolder;
import common.darkknight.jewelrycraft.tileentity.TileEntitySmelter;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderers()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySmelter.class, new TileEntitySmelterRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMolder.class, new TileEntityMolderRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityJewelrsCraftingTable.class, new TileEntityJewelrsCraftingTableRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisplayer.class, new TileEntityDisplayerRender());
        VillagerRegistry.instance().registerVillagerSkin(3000, new ResourceLocation("jewelrycraft", "textures/entities/jeweler.png"));
    }
}

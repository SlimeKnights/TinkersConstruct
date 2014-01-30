package tconstruct.worldgen.village;

import java.util.List;
import java.util.Random;

import tconstruct.blocks.logic.CraftingStationLogic;
import tconstruct.blocks.logic.PatternChestLogic;
import tconstruct.common.TRepo;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.MapGenStructureIO;


public class ComponentToolWorkshop extends MapGenVillage
{
    private int averageGroundLevel = -1;

    public ComponentToolWorkshop()
    {
    }

    public ComponentToolWorkshop(Start villagePiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
    {
        super();
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
    }

    public static ComponentToolWorkshop buildComponent (Start villagePiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
    {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 7, 6, 7, p4);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new ComponentToolWorkshop(villagePiece, p5, random,
                structureboundingbox, p4) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs, Mob Spawners, it closes Mineshafts at
     * the end, it adds Fences...
     */
    public boolean addComponentParts (World world, Random random, StructureBoundingBox sbb)
    {
        if (this.averageGroundLevel < 0)
        {
            this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);

            if (this.averageGroundLevel < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4, 0);
        }

        /**
         * arguments: (World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
         * maxZ, int placeBlockId, int replaceBlockId, boolean alwaysreplace)
         */

        this.fillWithBlocks(world, sbb, 0, 0, 0, 6, 0, 6, Blocks.cobblestone, Blocks.cobblestone, false); //Base
        this.fillWithBlocks(world, sbb, 0, 5, 0, 6, 5, 6, Blocks.fence, Blocks.fence, false);
        this.fillWithBlocks(world, sbb, 1, 0, 1, 5, 0, 5, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, sbb, 2, 0, 2, 4, 0, 4, Blocks.wool, Blocks.wool, false);

        //this.fillWithBlocks(world, sbb, 0, 5, 0, 6, 5, 6, Blocks.log, Blocks.log, false);

        this.fillWithBlocks(world, sbb, 0, 1, 0, 0, 4, 0, Blocks.log, Blocks.log, false); //Edges
        this.fillWithBlocks(world, sbb, 0, 1, 6, 0, 4, 6, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, sbb, 6, 1, 0, 6, 4, 0, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, sbb, 6, 1, 6, 6, 4, 6, Blocks.log, Blocks.log, false);

        this.fillWithBlocks(world, sbb, 0, 1, 1, 0, 1, 5, Blocks.planks, Blocks.planks, false); //Walls
        this.fillWithBlocks(world, sbb, 1, 1, 0, 5, 1, 0, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, sbb, 6, 1, 1, 6, 1, 5, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, sbb, 1, 1, 6, 5, 1, 6, Blocks.planks, Blocks.planks, false);

        this.fillWithBlocks(world, sbb, 0, 3, 1, 0, 3, 5, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, sbb, 1, 3, 0, 5, 3, 0, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, sbb, 6, 3, 1, 6, 3, 5, Blocks.planks, Blocks.planks, false);
        this.fillWithBlocks(world, sbb, 1, 3, 6, 5, 3, 6, Blocks.planks, Blocks.planks, false);

        this.fillWithBlocks(world, sbb, 0, 4, 1, 0, 4, 5, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, sbb, 1, 4, 0, 5, 4, 0, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, sbb, 6, 4, 1, 6, 4, 5, Blocks.log, Blocks.log, false);
        this.fillWithBlocks(world, sbb, 1, 4, 6, 5, 4, 6, Blocks.log, Blocks.log, false);

        this.fillWithBlocks(world, sbb, 1, 1, 1, 5, 5, 5, 0, 0, false);
        this.fillWithBlocks(world, sbb, 1, 4, 1, 5, 4, 5, Blocks.planks, Blocks.planks, false);

        //world, blockID, metadata, x, y, z, bounds
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 1, 2, 0, sbb);//Glass and door
        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 2, 2, 0, sbb);
        this.placeDoorAtCurrentPosition(world, sbb, random, 3, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 4, 2, 0, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 2, 0, sbb);

        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 1, 2, 6, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 2, 2, 6, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 3, 2, 6, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 4, 2, 6, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 5, 2, 6, sbb);

        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 1, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 2, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 0, 2, 3, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 4, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 0, 2, 5, sbb);

        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 1, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 2, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.planks, 0, 6, 2, 3, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 4, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.glass_pane, 0, 6, 2, 5, sbb);

        int i = this.getMetadataWithOffset(Blocks.ladder, 3); //Ladders
        this.placeBlockAtCurrentPosition(world, Blocks.ladder, i, 3, 1, 5, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.ladder, i, 3, 2, 5, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.ladder, i, 3, 3, 5, sbb);
        this.placeBlockAtCurrentPosition(world, Blocks.ladder, i, 3, 4, 5, sbb);

        this.placeBlockAtCurrentPosition(world, TRepo.toolStationWood, 0, 1, 1, 1, sbb); //Inside
        this.generateStructurePatternChestContents(world, sbb, random, 1, 1, 2, TRepo.tinkerHousePatterns.getItems(random), TRepo.tinkerHousePatterns.getCount(random));
        //this.placeBlockAtCurrentPosition(world, TRepo.toolStationWood, 5, 1, 1, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TRepo.toolStationWood, 1, 1, 1, 3, sbb);
        this.generateStructureCraftingStationContents(world, sbb, random, 1, 1, 4, TRepo.tinkerHouseChest.getItems(random), TRepo.tinkerHouseChest.getCount(random));
       // this.placeBlockAtCurrentPosition(world, TRepo.craftingStationWood, 0, 1, 1, 4, sbb);
        this.placeBlockAtCurrentPosition(world, TRepo.toolStationWood, 10, 1, 1, 5, sbb);

        //ChestGenHooks info = ChestGenHooks.getInfo("TinkerHouse");

        this.generateStructureChestContents(world, sbb, random, 4, 1, 5, TRepo.tinkerHouseChest.getItems(random), TRepo.tinkerHouseChest.getCount(random));
        //this.placeBlockAtCurrentPosition(world, Block.chest, i, 4, 1, 5, sbb);
        i = this.getMetadataWithOffset(Blocks.piston, 3);
        this.placeBlockAtCurrentPosition(world, Blocks.piston, i, 5, 1, 5, sbb);

        for (int l = 0; l < 6; ++l)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
                this.fillCurrentPositionBlocksDownwards(world, Blocks.cobblestone, 0, i1, -1, l, sbb);
            }
        }
        this.spawnVillagers(world, sbb, 3, 1, 3, 1);

        return true;
    }

    protected boolean generateStructureCraftingStationContents (World world, StructureBoundingBox par2StructureBoundingBox, Random random, int x, int y, int z, WeightedRandomChestContent[] content,
            int par8)
    {
        int posX = this.getXWithOffset(x, z);
        int posY = this.getYWithOffset(y);
        int posZ = this.getZWithOffset(x, z);

        if (par2StructureBoundingBox.isVecInside(posX, posY, posZ) && world.getBlock(posX, posY, posZ) != Blocks.chest)
        {
            world.func_147465_d(posX, posY, posZ, TRepo.craftingStationWood, 5, 2);
            CraftingStationLogic logic = (CraftingStationLogic) world.func_147438_o(posX, posY, posZ);

            if (logic != null)
            {
                WeightedRandomChestContent.generateChestContents(random, content, logic, par8);
            }

            return true;
        }
        else
        {
            return false;
        }
    }
    protected boolean generateStructurePatternChestContents (World world, StructureBoundingBox par2StructureBoundingBox, Random random, int x, int y, int z, WeightedRandomChestContent[] content,
            int par8)
    {
        int posX = this.getXWithOffset(x, z);
        int posY = this.getYWithOffset(y);
        int posZ = this.getZWithOffset(x, z);

        if (par2StructureBoundingBox.isVecInside(posX, posY, posZ) && world.getBlock(posX, posY, posZ) != Blocks.chest)
        {
            world.func_147465_d(posX, posY, posZ, TRepo.toolStationWood, 5, 2);
            PatternChestLogic logic = (PatternChestLogic) world.func_147438_o(posX, posY, posZ);

            if (logic != null)
            {
                WeightedRandomChestContent.generateChestContents(random, content, logic, par8);
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns the villager type to spawn in this component, based on the number of villagers already spawned.
     */
    protected int getVillagerType (int par1)
    {
        return 78943;
    }
}

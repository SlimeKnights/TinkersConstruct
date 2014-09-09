package tconstruct.world.village;

import java.util.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import tconstruct.smeltery.TinkerSmeltery;

public class ComponentSmeltery extends StructureVillagePieces.House1
{
    private int averageGroundLevel = -1;

    public ComponentSmeltery()
    {
    }

    public ComponentSmeltery(Start villagePiece, int par2, Random par3Random, StructureBoundingBox par4StructureBoundingBox, int par5)
    {
        super();
        this.coordBaseMode = par5;
        this.boundingBox = par4StructureBoundingBox;
    }

    public static ComponentSmeltery buildComponent (Start villagePiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5)
    {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 9, 3, 7, p4);
        return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new ComponentSmeltery(villagePiece, p5, random, structureboundingbox, p4) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs,
     * Mob Spawners, it closes Mineshafts at the end, it adds Fences...
     */
    @Override
    public boolean addComponentParts (World world, Random random, StructureBoundingBox sbb)
    {
        if (this.averageGroundLevel < 0)
        {
            this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);

            if (this.averageGroundLevel < 0)
            {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 2, 0);
        }

        /**
         * arguments: (World worldObj, StructureBoundingBox structBB, int minX, int minY, int minZ, int maxX, int maxY, int
         * maxZ, int placeBlockId, int replaceBlockId, boolean alwaysreplace)
         */

        this.fillWithBlocks(world, sbb, 1, 0, 0, 7, 0, 6, Blocks.stonebrick, Blocks.stonebrick, false); //Base
        this.fillWithBlocks(world, sbb, 0, 0, 1, 0, 0, 5, Blocks.stonebrick, Blocks.stonebrick, false);
        this.fillWithBlocks(world, sbb, 8, 0, 1, 8, 0, 5, Blocks.stonebrick, Blocks.stonebrick, false);
        this.fillWithBlocks(world, sbb, 0, 1, 0, 9, 3, 7, Blocks.air, Blocks.air, false);

        this.fillWithMetaBlocks(world, sbb, 2, 0, 1, 6, 2, 5, TinkerSmeltery.smeltery, 2, TinkerSmeltery.smeltery, 2, false); //Basin
        this.fillWithBlocks(world, sbb, 3, 1, 2, 5, 2, 4, Blocks.air, Blocks.air, false);

        this.placeBlockAtCurrentPosition(world, TinkerSmeltery.searedBlock, 0, 1, 1, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TinkerSmeltery.searedBlock, 2, 1, 1, 4, sbb);
        this.placeBlockAtCurrentPosition(world, TinkerSmeltery.searedBlock, 0, 7, 1, 2, sbb);
        this.placeBlockAtCurrentPosition(world, TinkerSmeltery.searedBlock, 2, 7, 1, 4, sbb);

        for (int l = 1; l < 6; ++l)
        {
            for (int i1 = 0; i1 < 9; ++i1)
            {
                this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
                this.func_151554_b(world, Blocks.stonebrick, 0, i1, -1, l, sbb);
            }
        }

        for (int l = 0; l < 7; ++l)
        {
            for (int i1 = 1; i1 < 8; ++i1)
            {
                this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
                this.func_151554_b(world, Blocks.stonebrick, 0, i1, -1, l, sbb);
            }
        }
        return true;
    }

    protected void fillWithMetaBlocks (World par1World, StructureBoundingBox par2StructureBoundingBox, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, Block placeBlockID, int placeBlockMeta, Block replaceBlockID, int replaceBlockMeta, boolean alwaysReplace)
    {
        Block i2 = this.func_151558_b(placeBlockID, placeBlockMeta);
        int j2 = this.func_151557_c(placeBlockID, placeBlockMeta);
        Block k2 = this.func_151558_b(replaceBlockID, replaceBlockMeta);
        int l2 = this.func_151557_c(replaceBlockID, replaceBlockMeta);
        super.fillWithMetadataBlocks(par1World, par2StructureBoundingBox, minX, minY, minZ, maxX, maxY, maxZ, i2, j2, k2, l2, alwaysReplace);
    }
}

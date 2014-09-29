package tconstruct.world.entity;

import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.world.World;

import java.util.Random;

public class Guardian extends EntityIronGolem {
    public int type;

    public Guardian(World world) {
        super(world);

        this.type = (new Random()).nextInt(3);
    }
}

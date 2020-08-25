package slimeknights.tconstruct.test;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class TestPlayerEntity extends PlayerEntity {

  public TestPlayerEntity(World worldIn) {
    super(worldIn, BlockPos.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "test"));
  }

  @Override
  public boolean isSpectator() {
    return false;
  }

  @Override
  public boolean isCreative() {
    return false;
  }
}

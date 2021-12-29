package slimeknights.tconstruct.test;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.UUID;

public class TestPlayerEntity extends Player {

  public TestPlayerEntity(Level level) {
    super(level, BlockPos.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "test"));
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

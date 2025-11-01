package com.matibi.gaiasbreath.mixin;

import com.matibi.gaiasbreath.GaiasBreath;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CropBlock.class)
public class CropBlockMixin {

    @Inject(method = "randomTick", at = @At("TAIL"))
    private void gb$onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
        if (!world.isRaining() || !world.isSkyVisible(pos.up())) return;

        if (!(state.getBlock() instanceof CropBlock crop)) return;

        int currentAge = state.get(CropBlock.AGE);
        int maxAge = crop.getMaxAge();
        var cfg = GaiasBreath.CONFIG;

        if (currentAge < maxAge && random.nextFloat() < cfg.RAIN_GROWTH_CHANCE)
            world.setBlockState(pos, crop.withAge(currentAge + 1), 2);
    }
}

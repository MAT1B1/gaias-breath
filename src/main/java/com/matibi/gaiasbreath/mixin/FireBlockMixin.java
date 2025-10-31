package com.matibi.gaiasbreath.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FireBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {

    @Unique private static final float CHARCOAL_DROP_CHANCE = 0.5f;

    @Inject(method = "trySpreadingFire", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"
    ))
    private void gb$dropCharcoalOnBurn(
            World world, BlockPos pos, int spreadFactor, Random random, int currentAge, CallbackInfo ci
    ) {
        if (world.isClient()) return;

        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (isWood(block) && random.nextFloat() < CHARCOAL_DROP_CHANCE)
            Block.dropStack(world, pos, new ItemStack(Items.CHARCOAL));
    }

    @Unique
    private boolean isWood(Block block) {
        return block.getDefaultState().isIn(BlockTags.LOGS)
                || block.getDefaultState().isIn(BlockTags.PLANKS)
                || block.getDefaultState().isIn(BlockTags.WOODEN_FENCES)
                || block.getDefaultState().isIn(BlockTags.WOODEN_SLABS)
                || block.getDefaultState().isIn(BlockTags.WOODEN_STAIRS);
    }
}
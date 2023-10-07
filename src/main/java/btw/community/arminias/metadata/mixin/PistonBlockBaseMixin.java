package btw.community.arminias.metadata.mixin;

import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;
import btw.community.arminias.metadata.PistonHelper;
import btw.community.arminias.metadata.extension.WorldExtension;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PistonBlockBase.class)
public abstract class PistonBlockBaseMixin extends BlockPistonBase {
    @Shadow protected abstract int getPistonShovelEjectionDirection(World world, int i, int j, int k, int iToFacing);

    @Shadow protected abstract void onShovelEjectIntoBlock(World world, int i, int j, int k);

    public PistonBlockBaseMixin(int par1, boolean par2) {
        super(par1, par2);
    }

    @Redirect(method = "validatePistonState", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/World;SetBlockMetadataWithNotify(IIIII)Z"))
    private boolean validatePistonStateRedirect(World world, int x, int y, int z, int metadata, int notify) {
        return ((WorldExtension) world).setBlockMetadataAndExtraWithNotify(x, y, z, metadata, notify, ((WorldExtension) world).getBlockExtraMetadata(x, y, z));
    }

    /**
     * @author Arminias
     */
    @Override
    @Overwrite
    public boolean tryExtend(World world, int x, int y, int z, int facingTo) {
        int offsetX = x + Facing.offsetsXForSide[facingTo];
        int offsetY = y + Facing.offsetsYForSide[facingTo];
        int offsetZ = z + Facing.offsetsZForSide[facingTo];

        int distance = 0;

        while (true) {
            if (distance < 13) {
                if (offsetY <= 0 || offsetY >= 255) {
                    return false;
                }

                int movingBlockID = world.getBlockId(offsetX, offsetY, offsetZ);
                Block movingBlock = blocksList[movingBlockID];

                if (movingBlock != null) {
                    if (!movingBlock.canBlockBePushedByPiston(world, offsetX, offsetY, offsetZ, facingTo)) {
                        return false;
                    }
                    else {
                        int mobilityFlag = movingBlock.getMobilityFlag();

                        int shovelEjectDirection = getPistonShovelEjectionDirection(world, offsetX, offsetY, offsetZ, facingTo);

                        if (mobilityFlag != 1 && shovelEjectDirection < 0) {
                            if (distance == 12) {
                                return false;
                            }

                            offsetX += Facing.offsetsXForSide[facingTo];
                            offsetY += Facing.offsetsYForSide[facingTo];
                            offsetZ += Facing.offsetsZForSide[facingTo];
                            ++distance;

                            continue;
                        }

                        int movingBlockMetadata = world.getBlockMetadata(offsetX, offsetY, offsetZ);
                        int movingBlockExtraMetadata = ((WorldExtension) world).getBlockExtraMetadata(offsetX, offsetY, offsetZ);

                        if (shovelEjectDirection >= 0) {
                            movingBlockMetadata = movingBlock.adjustMetadataForPistonMove(movingBlockMetadata);

                            int ejectX = offsetX + Facing.offsetsXForSide[shovelEjectDirection];
                            int ejectY = offsetY + Facing.offsetsYForSide[shovelEjectDirection];
                            int ejectZ = offsetZ + Facing.offsetsZForSide[shovelEjectDirection];

                            onShovelEjectIntoBlock(world, ejectX, ejectY, ejectZ);

                            ((WorldExtension) world).setBlockWithExtra(ejectX, ejectY, ejectZ, Block.pistonMoving.blockID, movingBlockMetadata, 4, movingBlockExtraMetadata);

                            world.setBlockTileEntity(ejectX, ejectY, ejectZ,
                                    PistonHelper.getShoveledTileEntity(movingBlockID, movingBlockMetadata, shovelEjectDirection, movingBlockExtraMetadata));
                        }
                        else {
                            movingBlock.onBrokenByPistonPush(world, offsetX, offsetY, offsetZ, movingBlockMetadata);
                        }

                        world.setBlockToAir(offsetX, offsetY, offsetZ);
                    }
                }
            }

            int previousOffsetX = offsetX;
            int previousOffsetY = offsetY;
            int previousOffsetZ = offsetZ;

            int blockCounter = 0;
            int[] blockIDList;
            int movingX = 0;
            int movingY = 0;
            int movingZ = 0;

            for (blockIDList = new int[13]; offsetX != x || offsetY != y || offsetZ != z; offsetZ = movingZ) {
                movingX = offsetX - Facing.offsetsXForSide[facingTo];
                movingY = offsetY - Facing.offsetsYForSide[facingTo];
                movingZ = offsetZ - Facing.offsetsZForSide[facingTo];
                int movingBlockID = world.getBlockId(movingX, movingY, movingZ);
                int movingBlockMetadata = world.getBlockMetadata(movingX, movingY, movingZ);
                int movingBlocExtraMetadata = ((WorldExtension) world).getBlockExtraMetadata(movingX, movingY, movingZ);

                NBTTagCompound tileEntityData = getBlockTileEntityData(world, movingX, movingY, movingZ);
                world.removeBlockTileEntity(movingX, movingY, movingZ);

                if (movingBlockID == this.blockID && movingX == x && movingY == y && movingZ == z) {
                    world.setBlock(offsetX, offsetY, offsetZ, Block.pistonMoving.blockID, facingTo | (this.isSticky ? 8 : 0), 4);
                    world.setBlockTileEntity(offsetX, offsetY, offsetZ,
                            PistonHelper.getTileEntity(Block.pistonExtension.blockID, facingTo | (this.isSticky ? 8 : 0), facingTo, true, false, 0));
                }
                else {
                    if (Block.blocksList[movingBlockID] != null) {
                        movingBlockMetadata = Block.blocksList[movingBlockID].adjustMetadataForPistonMove(movingBlockMetadata);
                    }

                    ((WorldExtension) world).setBlockWithExtra(offsetX, offsetY, offsetZ, Block.pistonMoving.blockID, movingBlockMetadata, 4, movingBlocExtraMetadata);
                    world.setBlockTileEntity(offsetX, offsetY, offsetZ, PistonHelper.getTileEntity(movingBlockID, movingBlockMetadata, facingTo, true, false, movingBlocExtraMetadata));
                    if (tileEntityData != null) {
                        ((TileEntityPiston) world.getBlockTileEntity(offsetX, offsetY, offsetZ)).storeTileEntity(tileEntityData);
                    }
                }

                blockIDList[blockCounter++] = movingBlockID;
                offsetX = movingX;
                offsetY = movingY;
            }

            offsetX = previousOffsetX;
            offsetY = previousOffsetY;
            offsetZ = previousOffsetZ;

            for (blockCounter = 0; offsetX != x || offsetY != y || offsetZ != z; offsetZ = movingZ) {
                movingX = offsetX - Facing.offsetsXForSide[facingTo];
                movingY = offsetY - Facing.offsetsYForSide[facingTo];
                movingZ = offsetZ - Facing.offsetsZForSide[facingTo];
                world.notifyBlocksOfNeighborChange(movingX, movingY, movingZ, blockIDList[blockCounter++]);
                offsetX = movingX;
                offsetY = movingY;
            }

            return true;
        }
    }
}

package net.tropicraft.world.worldgen;

import java.util.Random;

import scala.collection.generic.BitOperations.Int;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.tropicraft.block.tileentity.TileEntityBambooChest;
import net.tropicraft.block.tileentity.TileEntityTropicraftFlowerPot;
import net.tropicraft.registry.TCBlockRegistry;
import net.tropicraft.registry.TCItemRegistry;
import net.tropicraft.world.biomes.BiomeGenTropicraft;

public class WorldGenForestAltarRuin extends TCGenBase {

	private int x, z;
	
	private final int DIR;
	
	public WorldGenForestAltarRuin(World world, Random random) {
		super(world, random);
		DIR = this.rand.nextInt(4);
	}

	@Override
	public boolean generate(int i, int j, int k) {
		x = i;
		j = Integer.MAX_VALUE;
		z = k;
		
		final int width = (rand.nextInt(2) + 3) * 2;
		final int length = rand.nextInt(6) + 10;
		final int halfWidth = width / 2;

		boolean hasGennedTunnel = false;
		
		for(int z = 0; z < length; z++) {
			for(int x = 0; x < width; x++) {
				if(this.getTerrainHeightWithDir(x, z) < j) {
					j = this.getTerrainHeightWithDir(x, z);
				}
			}
		}
		
		for(int z = 0; z < length; z++) {
			for(int x = 0; x < width; x++) {
				for(int y = 0; y < 4; y++) {
					this.placeBlockWithDir(x, y + j, z, Blocks.air, 0);
				}
				int y = j;
				this.placeBlockWithDir(x, y, z, TCBlockRegistry.logs, 1);
				
				if(z == 0) {
					if(x == 0 || x == width - 1) {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.palmFence, 0);
						this.placeBlockWithDir(x, y + 2, z, TCBlockRegistry.palmFence, 0);
					} else {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.singleSlabs, 2);
						this.placeBlockWithDir(x, y + 2, z, TCBlockRegistry.palmFence, 0);
					}
				} else if(z == 1) {
					if(x == 0 || x == width - 1) {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.palmFence, 0);
					} else if(x == 1 || x == width - 2) {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.chunkOHead, 0);
						this.placeBlockWithDir(x, y + 2, z, TCBlockRegistry.flowerPot, 0);
						
						TileEntityTropicraftFlowerPot pot = (TileEntityTropicraftFlowerPot)this.getTEWithDir(x, y + 2, z);
						if(pot != null) {
							pot.setFlowerID((short)(this.rand.nextInt(13) + 1));
						}
					} else if(x == halfWidth - 1 || x == halfWidth) {
						this.placeBlockWithDir(x, y, z, Blocks.netherrack, 0);
						this.placeBlockWithDir(x, y + 1, z, Blocks.fire, 0);
					} else {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.singleSlabs, 2);
					}
				} else if(z == 2) {
					if(x == 0 || x == width - 1) {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.palmFence, 0);
					} else {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.singleSlabs, 2);
					}
				} else if(z % 2 == 1) {
					if(x == 0 || x == width - 1) {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.palmFence, 0);
					}
				} else {
					if(x == 0 || x == width - 1) {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.tikiTorch, 1);
						this.placeBlockWithDir(x, y + 2, z, TCBlockRegistry.tikiTorch, 1);
						this.placeBlockWithDir(x, y + 3, z, TCBlockRegistry.tikiTorch, 0);
					} else if(x != halfWidth - 1 && x != halfWidth) {
						this.placeBlockWithDir(x, y + 1, z, TCBlockRegistry.palmStairs, this.DIR);
						if(!hasGennedTunnel) {
							generateTunnel(x, y, z);
							hasGennedTunnel = true;
						}
					}
				}
			}
		}
		
		return true;
	}
	
	private void generateTunnel(int i, int j, int k) {
		int depth = rand.nextInt(5) + 8;
		for(int y = 0; y < depth; y++)
		{
			this.placeBlockWithDir(i, j - y, k, Blocks.air, 0);
		}
		
		j -= depth;
		
		int length = rand.nextInt(20) + 30;
		int dir = rand.nextInt(4);
		
		for(int x = 0; x < length; x++) {
			switch(dir) {
				case 0:
					i++;
					break;
				case 1:
					k++;
					break;
				case 2:
					i--;
					break;
				case 3:
					k--;
					break;
			}
			if(rand.nextInt(3) == 0) {
				j += rand.nextInt(3) - 1;
			}
			
			this.placeBlockWithDir(i, j, k, Blocks.air, 0);
			this.placeBlockWithDir(i, j + 1, k, Blocks.air, 0);
			this.placeBlockWithDir(i, j + 2, k, Blocks.air, 0);
			
			if(rand.nextInt(5) == 0) {
				dir = rand.nextInt(4);
			}
		}
		
		this.placeBlockWithDir(i, j, k, TCBlockRegistry.bambooChest, 0);
		
		TileEntityBambooChest chest = (TileEntityBambooChest)this.getTEWithDir(i, j, k);
		
		if(chest != null) {
			chest.setInventorySlotContents(0, this.randLoot());
		}
	}

	public ItemStack randLoot()
	{
		int picker = rand.nextInt(18);
		if(picker < 6)
		{
			return new ItemStack(TCBlockRegistry.bambooChute, rand.nextInt(20) + 1);
		}
		//else if(picker < 8)
		//{
		//	return new ItemStack(TCItemRegistry.coconutBomb, rand.nextInt(3) + 1); TODO
		//}
		else if(picker < 10)
		{
			return new ItemStack(TCItemRegistry.scale, rand.nextInt(3) + 1);
		}
		else if(picker < 12)
		{
			return new ItemStack(TCBlockRegistry.thatchBundle, rand.nextInt(20) + 1);
		}
		else if(picker < 14)
		{
			return new ItemStack(TCItemRegistry.cookedFrogLeg, rand.nextInt(4) + 1);
		}
		/*else if(picker == 14)
		{
			return new ItemStack(TCItemRegistry.ashenMasks, 1, rand.nextInt(7));
		}
		else if(picker == 15)
		{
			return new ItemStack(TCItemRegistry.recordTradeWinds, 1);
		}
		else if(picker == 16)
		{
			return new ItemStack(TCItemRegistry.recordEasternIsles, 1);
		}*/
		else
		{
			return new ItemStack(TCItemRegistry.blowGun, 1);
		}
	}
	
	private void placeBlockWithDir(int i, int j, int k, Block block, int meta) {
		switch(this.DIR) {
			case 2:
				this.worldObj.setBlock(this.x + i, j, this.z + k, block, meta, 3);
				return;
			case 0:
				this.worldObj.setBlock(this.x + k, j, this.z + i, block, meta, 3);
				return;
			case 3:
				this.worldObj.setBlock(this.x - i, j, this.z - k, block, meta, 3);
				return;
			case 1:
				this.worldObj.setBlock(this.x - k, j, this.z - i, block, meta, 3);
				return;
		}
	}
	
	private TileEntity getTEWithDir(int i, int j, int k) {
		switch(this.DIR) {
			case 2:
				return this.worldObj.getTileEntity(this.x + i, j, this.z + k);
			case 0:
				return this.worldObj.getTileEntity(this.x + k, j, this.z + i);
			case 3:
				return this.worldObj.getTileEntity(this.x - i, j, this.z - k);
			case 1:
				return this.worldObj.getTileEntity(this.x - k, j, this.z - i);
		}
		return null;
	}
	
	private int getTerrainHeightWithDir(int i, int k) {
		switch(this.DIR) {
			case 2:
				return this.getTerrainHeightAt(this.x + i, this.z + k);
			case 0:
				return this.getTerrainHeightAt(this.x + k, this.z + i);
			case 3:
				return this.getTerrainHeightAt(this.x - i, this.z - k);
			case 1:
				return this.getTerrainHeightAt(this.x - k, this.z - i);
		}
		return 64;
	}
	
}

package com.simibubi.create.content.decoration.copycat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.simibubi.create.foundation.model.BakedModelHelper;
import com.simibubi.create.foundation.model.BakedQuadHelper;
import com.simibubi.create.foundation.utility.Iterate;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.IModelData;

public class CopycatPlateModel extends CopycatModel {

	protected static final AABB CUBE_AABB = new AABB(BlockPos.ZERO);

	public CopycatPlateModel(BakedModel originalModel) {
		super(originalModel);
	}

	@Override
	protected List<BakedQuad> getCroppedQuads(BlockState state, Direction side, Random rand, BlockState material,
		IModelData wrappedData) {
		Direction facing = state.getOptionalValue(CopycatPanelBlock.FACING)
			.orElse(Direction.UP);

		BakedModel model = getModelOf(material);
		List<BakedQuad> templateQuads = model.getQuads(material, side, rand, wrappedData);
		int size = templateQuads.size();

		List<BakedQuad> quads = new ArrayList<>();

		Vec3 normal = Vec3.atLowerCornerOf(facing.getNormal());
		AABB bb = CUBE_AABB.contract(normal.x * 15 / 16, normal.y * 15 / 16, normal.z * 15 / 16);

		for (int i = 0; i < size; i++) {
			BakedQuad quad = templateQuads.get(i);

			quads.add(BakedQuadHelper.cloneWithCustomGeometry(quad,
				BakedModelHelper.cropAndMove(quad.getVertices(), quad.getSprite(), bb, Vec3.ZERO)));
		}

		return quads;
	}

}

package com.simibubi.create.content.kinetics.belt;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSpriteShifts;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity.CasingType;
import com.simibubi.create.foundation.block.render.SpriteShiftEntry;
import com.simibubi.create.foundation.model.BakedQuadHelper;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;

public class BeltModel extends BakedModelWrapper<BakedModel> {

	public static final ModelProperty<CasingType> CASING_PROPERTY = new ModelProperty<>();
	public static final ModelProperty<Boolean> COVER_PROPERTY = new ModelProperty<>();

	public BeltModel(BakedModel template) {
		super(template);
	}
	
	@Override
	public TextureAtlasSprite getParticleIcon(IModelData data) {
		if (!data.hasProperty(CASING_PROPERTY))
			return super.getParticleIcon(data);
		CasingType type = data.getData(CASING_PROPERTY);
		if (type == CasingType.NONE || type == CasingType.BRASS)
			return super.getParticleIcon(data);
		if (type == CasingType.COPPER)
			return AllSpriteShifts.COPPER_CASING.getOriginal();
		return AllSpriteShifts.ANDESITE_CASING.getOriginal();
	}

	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand, IModelData extraData) {
		List<BakedQuad> quads = super.getQuads(state, side, rand, extraData);
		if (!extraData.hasProperty(CASING_PROPERTY))
			return quads;
		
		boolean cover = extraData.getData(COVER_PROPERTY);
		CasingType type = extraData.getData(CASING_PROPERTY);
		boolean brassCasing = type == CasingType.BRASS;
		
		if (type == CasingType.NONE || brassCasing && !cover)
			return quads;

		quads = new ArrayList<>(quads);

		if (cover) {
			boolean alongX = state.getValue(BeltBlock.HORIZONTAL_FACING)
				.getAxis() == Axis.X;

			var coverModelPartial = switch (type) {
				case ANDESITE -> alongX ? AllPartialModels.ANDESITE_BELT_COVER_X : AllPartialModels.ANDESITE_BELT_COVER_Z;
				case BRASS -> alongX ? AllPartialModels.BRASS_BELT_COVER_X : AllPartialModels.BRASS_BELT_COVER_Z;
				case COPPER -> alongX ? AllPartialModels.COPPER_BELT_COVER_X : AllPartialModels.COPPER_BELT_COVER_Z;
				default -> throw new IllegalStateException("Invalid casing type " + type);
			};

			BakedModel coverModel = coverModelPartial.get();

			quads.addAll(coverModel.getQuads(state, side, rand, extraData));
		}

		if (brassCasing)
			return quads;

		var spriteShift = switch(type) {
			case COPPER -> AllSpriteShifts.COPPER_BELT_CASING;
			default -> AllSpriteShifts.ANDESIDE_BELT_CASING;
		};

		for (int i = 0; i < quads.size(); i++) {
			BakedQuad quad = quads.get(i);
			TextureAtlasSprite original = quad.getSprite();
			if (original != spriteShift.getOriginal())
				continue;

			BakedQuad newQuad = BakedQuadHelper.clone(quad);
			int[] vertexData = newQuad.getVertices();

			for (int vertex = 0; vertex < 4; vertex++) {
				float u = BakedQuadHelper.getU(vertexData, vertex);
				float v = BakedQuadHelper.getV(vertexData, vertex);
				BakedQuadHelper.setU(vertexData, vertex, spriteShift.getTargetU(u));
				BakedQuadHelper.setV(vertexData, vertex, spriteShift.getTargetV(v));
			}

			quads.set(i, newQuad);
		}

		return quads;
	}

}

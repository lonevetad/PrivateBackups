package dataStructures.isom.matrixBased;

import dataStructures.isom.NodeIsom;
import tools.NumberManager;

public class MISOMImpl extends MatrixInSpaceObjectsManager<Double> {
	private static final long serialVersionUID = -222301487L;

	public MISOMImpl(boolean isLazyNodeInstancing, int width, int height, NumberManager<Double> weightManager) {
		super(isLazyNodeInstancing, width, height, weightManager);
	}

	public MISOMImpl(boolean isLazyNodeInstancing, int width, int height) {
		super(isLazyNodeInstancing, width, height, NumberManager.getDoubleManager());
	}

	@Override
	public NodeIsom newNodeMatrix(int x, int y) { return new NodeIsomSingleObj(x, y); }
}
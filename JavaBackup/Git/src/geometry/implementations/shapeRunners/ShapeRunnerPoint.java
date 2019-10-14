package geometry.implementations.shapeRunners;

import geometry.AbstractShape2D;
import geometry.ShapeRunnersImplemented;
import geometry.implementations.AbstractShapeRunnerImpl;
import geometry.implementations.shapes.ShapePoint;
import geometry.pointTools.PointConsumer;

public class ShapeRunnerPoint extends AbstractShapeRunnerImpl {
	private static final long serialVersionUID = 72014502659886L;
	public static final ShapeRunnerPoint SINGLETON = new ShapeRunnerPoint();

	@Override
	public ShapeRunnersImplemented getShapeRunnersImplemented() {
		return ShapeRunnersImplemented.Point;
	}

	@Override
	protected boolean runShapeImpl(AbstractShape2D shape, PointConsumer action) {
		ShapePoint sp;
		sp = (ShapePoint) shape;
		if (action.canContinue()) {
			action.accept(sp.getCenter());
			return true;
		}
		return false;
	}
}
package geometry;

import java.awt.Point;
import java.util.function.Predicate;

import dataStructures.isom.InSpaceObjectsManagerImpl;
import dataStructures.isom.IsomConsumer;
import geometry.pointTools.impl.PointConsumerRestartable;
import tools.PathFinder;

public interface PathFinderIsom<NodeType, NodeContent, Distance extends Number>
		extends PathFinder<NodeType, NodeContent, Distance> { // NodeContent extends Point2D not needed

	public InSpaceObjectsManagerImpl<Distance> getSpaceToRunThrough();

//

	public static class WholeShapeWalkableChecker<Distance extends Number>
			implements IsomConsumer<Distance>, PointConsumerRestartable {
		private static final long serialVersionUID = -7895211411002L;
		protected boolean isWalkable;
		protected InSpaceObjectsManagerImpl<Distance> isom;
		protected Predicate<ObjectLocated> isWalkableTester;

		public WholeShapeWalkableChecker(InSpaceObjectsManagerImpl<Distance> isom,
				Predicate<ObjectLocated> isWalkableTester) {
			this.isom = isom;
			this.isWalkable = true;// assumption
			this.isWalkableTester = isWalkableTester;
		}

		@Override
		public InSpaceObjectsManagerImpl<Distance> getInSpaceObjectsManager() {
			return isom;
		}

		public boolean isWalkable() {
			return isWalkable;
		}

		public Predicate<ObjectLocated> getIsWalkableTester() {
			return isWalkableTester;
		}

		//

		@Override
		public void setInSpaceObjectsManager(InSpaceObjectsManagerImpl<Distance> isom) {
			this.isom = isom;
		}

		public void setIsWalkableTester(Predicate<ObjectLocated> isWalkableTester) {
			this.isWalkableTester = isWalkableTester;
		}

		@Override
		public boolean canContinue() {
			return this.isWalkable;
		}

		@Override
		public void accept(Point t) {
			this.isWalkable &= this.isom.getNodeAt(t).isWalkable(isWalkableTester);
		}

		@Override
		public void restart() {
			this.isWalkable = true;
		}
	}
}
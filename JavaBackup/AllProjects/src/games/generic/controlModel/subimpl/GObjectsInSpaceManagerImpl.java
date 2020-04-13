package games.generic.controlModel.subimpl;

import java.util.Set;

import dataStructures.isom.InSpaceObjectsManager;
import dataStructures.isom.matrixBased.MISOMImpl;
import dataStructures.isom.matrixBased.MatrixInSpaceObjectsManager;
import dataStructures.isom.matrixBased.pathFinders.PathFinderBFS_Matrix;
import games.generic.controlModel.GModality;
import games.generic.controlModel.GObjectsInSpaceManager;
import games.generic.controlModel.gObj.ObjectInSpace;
import tools.NumberManager;
import tools.ObjectWithID;

public class GObjectsInSpaceManagerImpl implements GObjectsInSpaceManager {

	public GObjectsInSpaceManagerImpl() {
		objWID = null;
		this.isom = new MISOMImpl(false, 1, 1, NumberManager.getDoubleManager());
		this.isom.setPathFinder(new PathFinderBFS_Matrix<Double>((MatrixInSpaceObjectsManager<Double>) this.isom));
		// TODO Auto-generated constructor stub
	}

	protected Set<ObjectWithID> objWID;
	protected InSpaceObjectsManager<Double> isom;
	protected GModality gameModality;

	@Override
	public InSpaceObjectsManager<Double> getOIMManager() {
		return isom;
	}

	@Override
	public GModality getGameModality() {
		return gameModality;
	}

	@Override
	public Set<ObjectWithID> getObjects() {
		if (this.objWID == null) {
			this.objWID = GObjectsInSpaceManager.super.getObjects();
		}
		return this.objWID;
	}

	@Override
	public void setGameModality(GModality gameModality) {
		this.gameModality = gameModality;
	}

	@Override
	public boolean contains(ObjectWithID o) {
		return (o == null) ? false : this.getObjects().contains(o);
	}

	@Override
	public boolean containsObject(ObjectInSpace o) {
		return contains(o);
	}
}
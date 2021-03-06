package games.theRisingAngel.abilities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import dataStructures.MapTreeAVL;
import games.generic.controlModel.GModality;
import games.generic.controlModel.damage.DamageDealerGeneric;
import games.generic.controlModel.damage.DamageGeneric;
import games.generic.controlModel.damage.DamageReceiverGeneric;
import games.generic.controlModel.gObj.CreatureSimple;
import games.generic.controlModel.gObj.MovingObject;
import games.generic.controlModel.gObj.MovingObjectDelegatingMovement;
import games.generic.controlModel.gObj.OrbitingInteractiveObject;
import games.generic.controlModel.gObj.TimedObject;
import games.generic.controlModel.inventoryAbil.abilitiesImpl.AbilityTargetingGObjInMap;
import games.generic.controlModel.misc.GObjMovement;
import games.generic.controlModel.subimpl.movements.GObjLinearMovement;
import games.theRisingAngel.GModalityTRAn;
import geometry.AbstractShape2D;
import geometry.ObjectLocated;
import geometry.ObjectShaped;
import geometry.implementations.shapes.ShapeCircle;
import tools.Comparators;
import tools.MathUtilities;
import tools.ObjectWithID;
import tools.UniqueIDProvider;

// emula il "Storm Burst" di "path of Exile"
/**
 * Generates a series of orbs that scatters and deals damage upon hitting
 * someone while moving or exploding.
 */
public abstract class ARandomScatteringOrbs implements AbilityTargetingGObjInMap, TimedObject {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Scattering Bombs";
	protected static final boolean STATUS_TRAVELLING = true, STATUS_STATIONARY_FOR_EXPLOSION = !STATUS_TRAVELLING;
	protected static final int MILLIS_STATIONARY_BEFORE_EXPLOSION = 500, MILLIS_TRAVELLING = 1000;

	public ARandomScatteringOrbs(GModalityTRAn gameModality) {
		this.gameModality = gameModality;
		orbsIDProvider = UniqueIDProvider.newBasicIDProvider();
		random = new Random();
		timeSpawningOrbs = tempSpawning = 0;
	}

	protected GModalityTRAn gameModality;
	protected transient int tempSpawning;
	protected int timeSpawningOrbs;
	protected transient UniqueIDProvider orbsIDProvider;
	protected Integer ID;
	protected transient Random random;
	protected transient ObjectWithID owner;
	protected Set<ScatteringOrb> orbs;

	@Override
	public Integer getID() {
		return ID;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public ObjectWithID getOwner() {
		return owner;
	}

	public int getTimeSpawningOrbs() {
		return timeSpawningOrbs;
	}

	public Random getRandom() {
		return random;
	}

	public Set<ScatteringOrb> getOrbs() {
		return orbs;
	}

	//

	public void setTimeSpawningOrbs(int timeSpawningOrbs) {
		this.timeSpawningOrbs = timeSpawningOrbs;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	@Override
	public void setOwner(ObjectWithID owner) {
		this.owner = owner;
	}

	//

	/** Get the radius of each single orb */
	public int getRadiusOrbs() {
		return gameModality.getSpaceSubunitsEveryUnit(); // just 1 unit
	}

	/**
	 * Returns an upper bound of amount of scattering orbs. A negative or zero
	 * number is considered as no upper bound.
	 */
	public abstract int getMaxAmountScatteringOrbs();

	public abstract DamageGeneric getDamageOrbs(ScatteringOrb orb, DamageReceiverGeneric target);

	/**
	 * Orbs scatters randomly in a circle as large as this value. Greater-than-zero
	 * values are strongly recommended.<br>
	 */
	public abstract int getRadiusScattering();

	/**
	 * Get the center at which orbs are scattering.<br>
	 * Could follow the user's mouse's pointer.
	 */
	public abstract Point getCenterOfScattering();

	protected abstract ScatteringOrb newOrb();

	@Override
	public void performAbility(GModality gm) {
		// TODO Auto-generated method stub

	}

	@Override
	public void act(GModality modality, int timeUnits) {
		int tso;
		tso = getTimeSpawningOrbs();
		if (this.orbs.size() < getMaxAmountScatteringOrbs()) {
			if ((this.tempSpawning += timeUnits) >= tso) {
				do {
					tempSpawning -= tso;
					this.orbs.add(newOrb()); // spawn
				} while(tempSpawning >= tso && this.orbs.size() < getMaxAmountScatteringOrbs());
			}
		}
		orbs.forEach(so -> so.act(modality, timeUnits));
	}

	protected void performDamage(GModality gm, ScatteringOrb orb, CreatureSimple target) {
		GModalityTRAn gmtran;
		gmtran = (GModalityTRAn) gm;
		gmtran.dealsDamageTo(orb, target, getDamageOrbs(orb, target));
	}

	//

	//

	protected abstract class ScatteringOrb
			implements MovingObject, ObjectShaped, MovingObjectDelegatingMovement, DamageDealerGeneric {
		private static final long serialVersionUID = 1L;
		protected transient boolean status; // if false, then it's stationary
		protected transient int tempTime;
		protected transient Integer id;
		protected GObjLinearMovement movementLinear;
		protected AbstractShape2D shape;
		protected transient Set<ObjectLocated> targetsDamagedOnTravel;
		protected transient List<Point> path;

		protected ScatteringOrb() {
			Point p;
			MapTreeAVL<Integer, ObjectLocated> backmap;
			tempTime = 0;
			id = orbsIDProvider.getNewID();
			p = getCenterOfScattering();
			shape = new ShapeCircle(p.x, p.y, true, getRadiusOrbs());
			movementLinear = new GObjLinearMovement();
			movementLinear.setObjectToMove(this);
			movementLinear.resetStartingPoint();
			status = STATUS_STATIONARY_FOR_EXPLOSION;
			backmap = MapTreeAVL.newMap(MapTreeAVL.Optimizations.Lightweight, Comparators.INTEGER_COMPARATOR);
			targetsDamagedOnTravel = backmap.toSetValue(ObjectLocated.KEY_EXTRACTOR);
			path = new ArrayList<>(2);
		}

		@Override
		public Integer getID() {
			return id;
		}

		@Override
		public GObjMovement getMovementImplementation() {
			return movementLinear;
		}

		@Override
		public void setMovementImplementation(GObjMovement movementImplementation) {
			this.movementLinear = (GObjLinearMovement) movementImplementation;
		}

		@Override
		public AbstractShape2D getShape() {
			return shape;
		}

		@Override
		public void setShape(AbstractShape2D shape) {
			this.shape = shape;
		}

		/**
		 * Perform blast explosion, calling
		 * {@link ARandomScatteringOrbs#performDamage(GModality, ScatteringOrb, CreatureSimple)}.
		 */
		public abstract void explode(GModality modality);

		/**
		 * SHOULD call GModality methods for handling damages, i.e.
		 * {@link ARandomScatteringOrbs#performDamage(GModality, ScatteringOrb, CreatureSimple)}.
		 */
		public abstract void damageOnTraveling(GModality modality, ObjectLocated target);

		@Override
		public void act(GModality modality, int timeUnits) {
			if (status) {
				// traveling
				move(modality, timeUnits);
				if ((tempTime += timeUnits) >= MILLIS_TRAVELLING) {
					tempTime = 0;
					status = STATUS_STATIONARY_FOR_EXPLOSION;
				}
			} else if ((tempTime += timeUnits) >= MILLIS_TRAVELLING) {
				int radius;
				double ang;
				Point dest, centre;
				tempTime = 0;
				status = STATUS_TRAVELLING;
				targetsDamagedOnTravel.clear();
				explode(modality);
				// set next destination
				ang = random.nextDouble() * OrbitingInteractiveObject.RHO;
				radius = random.nextInt(getRadiusScattering());
				centre = getCenterOfScattering();
				dest = new Point(0, 0);
				ang = Math.cos(ang);// recycle it
				dest.x = centre.x * ((int) ang * radius);
				ang = 1.0 - ang * ang;// == sin
				dest.y = centre.y * ((int) ang * radius);
				// must cover the whole distance within time
				movementLinear
						.setVelocity((int) (MathUtilities.distance(dest, getLocation()) / getTimeUnitSuperscale()));
				movementLinear.setDestination(dest);
				path.clear();
				path.add(shape.getCenter());
				path.add(dest);// just fill the hole
			}
		}

		@Override
		public void move(GModality modality, int timeUnits) {
			Point start;
			Set<ObjectLocated> collected;
			start = new Point(this.shape.getCenter());
			this.movementLinear.act(modality, timeUnits);
			path.set(0, start);
			path.set(1, this.shape.getCenter());
			collected = modality.getGameObjectsManager().getGObjectInSpaceManager().findInPath(shape,
					getTargetsFilter(), path);
			// damages everyone
			collected.forEach(ol -> {
				if (targetsDamagedOnTravel.contains(ol))
					return;
				damageOnTraveling(modality, ol);
				targetsDamagedOnTravel.add(ol);
			});
		}
	}
}
package tools.predicatesExpressions.filters;

import tools.predicatesExpressions.MyPredicate;

public class OrPredicate<E> extends TwoPredicatesJoined<E> {
	private static final long serialVersionUID = 21059L;

	protected OrPredicate() {
		super();
	}

	public OrPredicate(MyPredicate<E> f1, MyPredicate<E> f2) {
		super(f1, f2);
	}

	@Override
	public boolean test(E ao) {
		if (ao == null// || filter1 == null || filter2 == null
		)
			return false;
		// if (this.filter1.test(ao)) return true;
		return this.filter1.test(ao) || this.filter2.test(ao);
	}
}
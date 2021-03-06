package tools.predicatesExpressions.filters;

import tools.predicatesExpressions.MyPredicate;

public class XnorPredicate<E> extends TwoPredicatesJoined<E> {
	private static final long serialVersionUID = -9652018108L;

	protected XnorPredicate() {
		super();
	}

	public XnorPredicate(MyPredicate<E> f1, MyPredicate<E> f2) {
		super(f1, f2);
	}

	@Override
	public boolean test(E ao) {
		if (ao == null // || filter1 == null || filter2 == null
		)
			return false;
		// return !(this.filter1.test(ao) ^ this.filter2.test(ao));
		// equivalent
		return (this.filter1.test(ao) == this.filter2.test(ao));
	}

}
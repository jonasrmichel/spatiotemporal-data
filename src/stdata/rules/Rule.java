package pervasivedata.rules;

public abstract class Rule {
	protected IRuleDelegate delegate;
	
	public Rule(IRuleDelegate delegate) {
		this.delegate = delegate;
	}
}

package stdata.rules;

import java.util.List;

import stdata.datamodel.vertices.Datum;

public interface IRuleRegistry {

	public void registerRule(Rule rule);

	public void registerRule(Datum datum, Rule rule);
	
	public void registerRules(List<Rule> rules);

	public void registerRules(Datum datum, List<Rule> rules);
}

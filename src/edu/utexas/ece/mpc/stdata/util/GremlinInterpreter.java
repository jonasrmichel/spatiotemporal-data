package edu.utexas.ece.mpc.stdata.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.tinkerpop.gremlin.Tokens;
import com.tinkerpop.gremlin.java.GremlinFluentPipeline;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class GremlinInterpreter {

	private static Map<String, List<Method>> stepMap = null;

	public static String[] supportedSteps = {
		"_", // public abstract PipesFluentPipeline<S, E> _()
		"aggregate", // public abstract PipesFluentPipeline<S, E> aggregate()
//		"aggregate", // public abstract PipesFluentPipeline<S, E> aggregate(PipeFunction<E, ?>)
//		"aggregate", // public abstract PipesFluentPipeline<S, E> aggregate(Collection<E>)
//		"aggregate", // public abstract PipesFluentPipeline<S, E> aggregate(Collection,PipeFunction<E, ?>)
//		"and", // public abstract PipesFluentPipeline<S, E> and(Pipe<E, ?>[])
		"as", // public abstract PipesFluentPipeline<S, E> as(String)
		"back", // public abstract PipesFluentPipeline<S, ?> back(int)
		"back", // public abstract PipesFluentPipeline<S, ?> back(String)
		"both", // public abstract GremlinFluentPipeline<S, Vertex> both(String[])
		"bothE", // public abstract GremlinFluentPipeline<S, Edge> bothE(String[])
		"bothV", // public abstract GremlinFluentPipeline<S, Vertex> bothV()
		"cap", // public abstract PipesFluentPipeline<S, ?> cap()
//		"copySplit", // public abstract PipesFluentPipeline<S, ?> copySplit(Pipe<E, ?>[])
//**		"count", // public abstract long count()
		"dedup", // public abstract PipesFluentPipeline<S, E> dedup()
//		"dedup", // public abstract PipesFluentPipeline<S, E> dedup(PipeFunction<E, ?>)
//		"enablePath", // public abstract PipesFluentPipeline<S, E> enablePath()
//		"except", // public abstract PipesFluentPipeline<S, E> except(Collection<E>)
		"exhaustMerge", // public abstract PipesFluentPipeline<S, ?> exhaustMerge()
		"fairMerge", // public abstract PipesFluentPipeline<S, ?> fairMerge()
//**		"fill", // public abstract Collection<E> fill(Collection<E>)
//		"filter", // public abstract PipesFluentPipeline<S, E> filter(PipeFunction<E, Boolean>)
		"gather", // public abstract PipesFluentPipeline<S, List> gather()
//		"gather", // public abstract PipesFluentPipeline<S, ?> gather(PipeFunction<List, ?>)
//		"groupBy", // public abstract PipesFluentPipeline<S, E> groupBy(PipeFunction,PipeFunction)
//		"groupBy", // public abstract PipesFluentPipeline<S, E> groupBy(PipeFunction,PipeFunction,PipeFunction)
//		"groupBy", // public abstract PipesFluentPipeline<S, E> groupBy(Map<?, List<?>>,PipeFunction,PipeFunction)
//		"groupBy", // public abstract PipesFluentPipeline<S, E> groupBy(Map,PipeFunction,PipeFunction,PipeFunction)
		"groupCount", // public abstract PipesFluentPipeline<S, E> groupCount()
//		"groupCount", // public abstract PipesFluentPipeline<S, E> groupCount(PipeFunction)
//		"groupCount", // public abstract PipesFluentPipeline<S, E> groupCount(PipeFunction,PipeFunction<Pair<?, Number>, Number>)
//		"groupCount", // public abstract PipesFluentPipeline<S, E> groupCount(Map<?, Number>)
//		"groupCount", // public abstract PipesFluentPipeline<S, E> groupCount(Map<?, Number>,PipeFunction)
//		"groupCount", // public abstract PipesFluentPipeline<S, E> groupCount(Map<?, Number>,PipeFunction,PipeFunction<Pair<?, Number>, Number>)
		"has", // public abstract GremlinFluentPipeline<S, ? extends Element> has(String,Object)
		"has", // public abstract GremlinFluentPipeline<S, ? extends Element> has(String,T,Object)
		"hasNot", // public abstract GremlinFluentPipeline<S, ? extends Element> hasNot(String,Object)
		"hasNot", // public abstract GremlinFluentPipeline<S, ? extends Element> hasNot(String,T,Object)
		"id", // public abstract GremlinFluentPipeline<S, Object> id()
//		"idEdge", // public abstract GremlinFluentPipeline<S, Edge> idEdge(Graph)
//		"idVertex", // public abstract GremlinFluentPipeline<S, Vertex> idVertex(Graph)
//		"ifThenElse", // public abstract PipesFluentPipeline<S, ?> ifThenElse(PipeFunction<E, Boolean>,PipeFunction<E, ?>,PipeFunction<E, ?>)
		"in", // public abstract GremlinFluentPipeline<S, Vertex> in(String[])
		"inE", // public abstract GremlinFluentPipeline<S, Edge> inE(String[])
		"inV", // public abstract GremlinFluentPipeline<S, Vertex> inV()
		"interval", // public abstract GremlinFluentPipeline<S, ? extends Element> interval(String,Object,Object)
//		"iterate", // public abstract void iterate()
		"label", // public abstract GremlinFluentPipeline<S, String> label()
//		"loop", // public abstract PipesFluentPipeline<S, E> loop(int,PipeFunction<LoopBundle<E>, Boolean>)
//		"loop", // public abstract PipesFluentPipeline<S, E> loop(int,PipeFunction<LoopBundle<E>, Boolean>,PipeFunction<LoopBundle<E>, Boolean>)
//		"loop", // public abstract PipesFluentPipeline<S, E> loop(String,PipeFunction<LoopBundle<E>, Boolean>)
//		"loop", // public abstract PipesFluentPipeline<S, E> loop(String,PipeFunction<LoopBundle<E>, Boolean>,PipeFunction<LoopBundle<E>, Boolean>)
		"map", // public abstract GremlinFluentPipeline<S, Map<String, Object>> map()
		"memoize", // public abstract PipesFluentPipeline<S, E> memoize(int)
//		"memoize", // public abstract PipesFluentPipeline<S, E> memoize(int,Map)
		"memoize", // public abstract PipesFluentPipeline<S, E> memoize(String)
//		"memoize", // public abstract PipesFluentPipeline<S, E> memoize(String,Map)
//		"next", // public abstract List<E> next(int)
		"optional", // public abstract PipesFluentPipeline<S, ?> optional(int)
		"optional", // public abstract PipesFluentPipeline<S, ?> optional(String)
//		"or", // public abstract PipesFluentPipeline<S, E> or(Pipe<E, ?>[])
		"order", // public abstract PipesFluentPipeline<S, E> order()
//		"order", // public abstract PipesFluentPipeline<S, E> order(PipeFunction<Pair<E, E>, Integer>)
		"out", // public abstract GremlinFluentPipeline<S, Vertex> out(String[])
		"outE", // public abstract GremlinFluentPipeline<S, Edge> outE(String[])
		"outV", // public abstract GremlinFluentPipeline<S, Vertex> outV()
//		"path", // public abstract PipesFluentPipeline<S, List> path(PipeFunction[])
		"property", // public abstract GremlinFluentPipeline<S, Object> property(String)
		"random", // public abstract PipesFluentPipeline<S, E> random(Double)
		"range", // public abstract PipesFluentPipeline<S, E> range(int,int)
//		"retain", // public abstract PipesFluentPipeline<S, E> retain(Collection<E>)
		"scatter", // public abstract PipesFluentPipeline<S, ?> scatter()
		"select", // public abstract PipesFluentPipeline<S, Row> select()
//		"select", // public abstract PipesFluentPipeline<S, Row> select(PipeFunction[])
//		"select", // public abstract PipesFluentPipeline<S, Row> select(Collection<String>,PipeFunction[])
//		"sideEffect", // public abstract PipesFluentPipeline<S, E> sideEffect(PipeFunction<E, ?>)
		"simplePath", // public abstract PipesFluentPipeline<S, E> simplePath()
//		"start", // public abstract PipesFluentPipeline<S, S> start(S)
//		"step", // public abstract <T> PipesFluentPipeline<S, T> step(Pipe<E, T>)
//		"step", // public abstract PipesFluentPipeline<S, ?> step(PipeFunction)
		"store", // public abstract PipesFluentPipeline<S, E> store()
//		"store", // public abstract PipesFluentPipeline<S, E> store(PipeFunction<E, ?>)
		"store", // public abstract PipesFluentPipeline<S, E> store(Collection<E>)
//		"store", // public abstract PipesFluentPipeline<S, E> store(Collection,PipeFunction<E, ?>)
		"table", // public abstract PipesFluentPipeline<S, E> table()
//		"table", // public abstract PipesFluentPipeline<S, E> table(PipeFunction[])
//		"table", // public abstract PipesFluentPipeline<S, E> table(Table)
//		"table", // public abstract PipesFluentPipeline<S, E> table(Table,PipeFunction[])
//		"table", // public abstract PipesFluentPipeline<S, E> table(Table,Collection<String>,PipeFunction[])
//**		"toList", // public abstract List<E> toList()
//		"transform", // public abstract <T> PipesFluentPipeline<S, T> transform(PipeFunction<E, T>)
//		"tree", // public abstract PipesFluentPipeline<S, E> tree(PipeFunction[])
//		"tree", // public abstract PipesFluentPipeline<S, E> tree(Tree,PipeFunction[])
	};

	public static void load() {
		if (stepMap != null)
			return;

		stepMap = new HashMap<String, List<Method>>();
		Method[] methods = GremlinFluentPipeline.class.getMethods();
		String name;
		for (Method method : methods) {
			name = method.getName();
			if (!stepMap.containsKey(name))
				stepMap.put(name, new ArrayList<Method>());

			stepMap.get(name).add(method);

			Log.d("\"" + method.getName() + "\"",
					"// " + method.toGenericString());
		}
	}

	public static <S, E> GremlinPipeline<S, E> compile(String script)
			throws UnsupportedOperationException {
		GremlinPipeline<S, E> pipeline = eval(new GremlinPipeline<S, E>(),
				script);

		return pipeline;
	}

	private static GremlinPipeline eval(GremlinPipeline pipeline, String script)
			throws UnsupportedOperationException {
		String[] steps = script.split("\\.");
		String step = steps[0];

		pipeline = apply(pipeline, step);

		if (steps.length > 1) {
			String stepsRemaining = join(
					Arrays.copyOfRange(steps, 1, steps.length), ".");

			return eval(pipeline, stepsRemaining);

		} else {
			return pipeline;

		}
	}

	private static String join(String[] arr, String j) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < arr.length; i++) {
			if (i > 0)
				sb.append(j);

			sb.append(arr[i]);
		}

		return sb.toString();
	}

	private static String[] getStepParts(String step) {
		String name = step.substring(0, step.indexOf("("));
		String argsStr = step.substring(step.indexOf("(") + 1,
				step.indexOf(")")).trim();
		
		String[] args;
		if (argsStr.equals(""))
			args = new String[0];
		else
			args = step.substring(step.indexOf("(") + 1, step.indexOf(")"))
					.split(",");

		String[] parts = new String[args.length + 1];
		parts[0] = name;
		for (int i = 0; i < args.length; i++)
			parts[i + 1] = args[i].trim();

		return parts;
	}

	private static GremlinPipeline apply(GremlinPipeline pipeline, String step)
			throws UnsupportedOperationException {
		String[] stepParts = getStepParts(step);
		String stepName = stepParts[0];

		String[] args = null;
		if (stepParts.length > 1)
			args = Arrays.copyOfRange(stepParts, 1, stepParts.length);

		if (stepName.equals("_"))
			return _(pipeline, args);
		else if (stepName.equals("aggregate"))
			return aggregate(pipeline, args);
		else if (stepName.equals("as"))
			return as(pipeline, args);
		else if (stepName.equals("back"))
			return back(pipeline, args);
		else if (stepName.equals("both"))
			return both(pipeline, args);
		else if (stepName.equals("bothE"))
			return bothE(pipeline, args);
		else if (stepName.equals("bothV"))
			return bothV(pipeline, args);
		else if (stepName.equals("cap"))
			return cap(pipeline, args);
		else if (stepName.equals("dedup"))
			return dedup(pipeline, args);
		else if (stepName.equals("exhaustMerge"))
			return exhaustMerge(pipeline, args);
		else if (stepName.equals("fairMerge"))
			return fairMerge(pipeline, args);
		else if (stepName.equals("gather"))
			return gather(pipeline, args);
		else if (stepName.equals("groupCount"))
			return groupCount(pipeline, args);
		else if (stepName.equals("has"))
			return has(pipeline, args);
		else if (stepName.equals("hasNot"))
			return hasNot(pipeline, args);
		else if (stepName.equals("id"))
			return id(pipeline, args);
		else if (stepName.equals("in"))
			return in(pipeline, args);
		else if (stepName.equals("inE"))
			return inE(pipeline, args);
		else if (stepName.equals("inV"))
			return inV(pipeline, args);
		else if (stepName.equals("interval"))
			return interval(pipeline, args);
		else if (stepName.equals("label"))
			return label(pipeline, args);
		else if (stepName.equals("map"))
			return map(pipeline, args);
		else if (stepName.equals("memoize"))
			return memoize(pipeline, args);
		else if (stepName.equals("optional"))
			return optional(pipeline, args);
		else if (stepName.equals("order"))
			return order(pipeline, args);
		else if (stepName.equals("out"))
			return out(pipeline, args);
		else if (stepName.equals("outE"))
			return outE(pipeline, args);
		else if (stepName.equals("outV"))
			return outV(pipeline, args);
		else if (stepName.equals("property"))
			return property(pipeline, args);
		else if (stepName.equals("random"))
			return random(pipeline, args);
		else if (stepName.equals("range"))
			return range(pipeline, args);
		else if (stepName.equals("scatter"))
			return scatter(pipeline, args);
		else if (stepName.equals("select"))
			return select(pipeline, args);
		else if (stepName.equals("simplePath"))
			return simplePath(pipeline, args);
		else if (stepName.equals("store"))
			return store(pipeline, args);
		else if (stepName.equals("table"))
			return table(pipeline, args);
		else
			throw new UnsupportedOperationException();

	}

	private static String parseString(String arg) {
		String parsed = arg;
		if (arg.startsWith("'"))
			parsed = parsed.substring(1, parsed.length());
		if (arg.endsWith("'"))
			parsed = parsed.substring(0, parsed.length() - 1);

		return parsed;
	}

	private static String[] parseStrings(String[] args) {
		String[] parsed = new String[args.length];
		for (int i = 0; i < args.length; i++)
			parsed[i] = parseString(args[i]);

		return parsed;
	}

	private static Object parseObject(String arg) {
		// try to parse as a String
		String argStr = parseString(arg);
		if (!arg.equals(argStr))
			return argStr;

		// try to parse as a numerical value
		if (Character.isLetter(arg.charAt(arg.length() - 1))) {
			String value = arg.substring(0, arg.length() - 1);
			char suffix = arg.toLowerCase().charAt(arg.length() - 1);

			if (suffix == 'f')
				return Float.parseFloat(value);
			else if (suffix == 'd')
				return Double.parseDouble(value);
			else if (suffix == 'l')
				return Long.parseLong(value);
		}

		// try to parse as a double
		if (arg.contains("."))
			return Double.parseDouble(arg);

		// try to parse as an int
		return Integer.parseInt(arg);
	}

	private static Tokens.T parseToken(String arg) {
		Tokens.T t = null;

		if (arg.equals("T.gt"))
			t = Tokens.T.gt;
		else if (arg.equals("T.lt"))
			t = Tokens.T.lt;
		else if (arg.equals("T.eq"))
			t = Tokens.T.eq;
		else if (arg.equals("T.gte"))
			t = Tokens.T.gte;
		else if (arg.equals("T.lte"))
			t = Tokens.T.lte;
		else if (arg.equals("T.neq"))
			t = Tokens.T.neq;

		return t;
	}

	private static GremlinPipeline _(GremlinPipeline pipeline, String[] args) {
		return pipeline._();
	}

	private static GremlinPipeline aggregate(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.aggregate();
	}

	private static GremlinPipeline as(GremlinPipeline pipeline, String[] args) {
		return pipeline.as(parseString(args[0]));
	}

	private static GremlinPipeline back(GremlinPipeline pipeline, String[] args) {
		try {
			return pipeline.back(Integer.parseInt(args[0]));

		} catch (NumberFormatException e) {
			return pipeline.back(parseString(args[0]));

		}
	}

	private static GremlinPipeline both(GremlinPipeline pipeline, String[] args) {
		return pipeline.both(parseStrings(args));
	}

	private static GremlinPipeline bothE(GremlinPipeline pipeline, String[] args) {
		return pipeline.bothE(parseStrings(args));
	}

	private static GremlinPipeline bothV(GremlinPipeline pipeline, String[] args) {
		return pipeline.bothV();
	}

	private static GremlinPipeline cap(GremlinPipeline pipeline, String[] args) {
		return pipeline.cap();
	}

	private static GremlinPipeline dedup(GremlinPipeline pipeline, String[] args) {
		return pipeline.dedup();
	}

	private static GremlinPipeline exhaustMerge(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.exhaustMerge();
	}

	private static GremlinPipeline fairMerge(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.fairMerge();
	}

	private static GremlinPipeline gather(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.gather();
	}

	private static GremlinPipeline groupCount(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.groupCount();
	}

	private static GremlinPipeline has(GremlinPipeline pipeline, String[] args) {
		if (args.length == 2) {
			return pipeline.has(parseString(args[0]), parseObject(args[1]));

		} else if (args.length == 3) {
			return pipeline.has(parseString(args[0]), parseToken(args[1]),
					parseObject(args[2]));

		} else {
			throw new UnsupportedOperationException();

		}
	}

	private static GremlinPipeline hasNot(GremlinPipeline pipeline,
			String[] args) {
		if (args.length == 2) {
			return pipeline.hasNot(parseString(args[0]), parseObject(args[1]));

		} else if (args.length == 3) {
			return pipeline.hasNot(parseString(args[0]), parseToken(args[1]),
					parseObject(args[2]));

		} else {
			throw new UnsupportedOperationException();

		}
	}

	private static GremlinPipeline id(GremlinPipeline pipeline, String[] args) {
		return pipeline.id();
	}

	private static GremlinPipeline in(GremlinPipeline pipeline, String[] args) {
		return pipeline.in(parseStrings(args));
	}

	private static GremlinPipeline inE(GremlinPipeline pipeline, String[] args) {
		return pipeline.inE(parseStrings(args));
	}

	private static GremlinPipeline inV(GremlinPipeline pipeline, String[] args) {
		return pipeline.inV();
	}

	private static GremlinPipeline interval(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.interval(parseString(args[0]), parseObject(args[1]),
				parseObject(args[2]));
	}

	private static GremlinPipeline label(GremlinPipeline pipeline, String[] args) {
		return pipeline.label();
	}

	private static GremlinPipeline map(GremlinPipeline pipeline, String[] args) {
		return pipeline.map();
	}

	private static GremlinPipeline memoize(GremlinPipeline pipeline,
			String[] args) {
		try {
			return pipeline.memoize(Integer.parseInt(args[0]));

		} catch (NumberFormatException e) {
			return pipeline.memoize(parseString(args[0]));

		}
	}

	private static GremlinPipeline optional(GremlinPipeline pipeline,
			String[] args) {
		try {
			return pipeline.optional(Integer.parseInt(args[0]));

		} catch (NumberFormatException e) {
			return pipeline.optional(parseString(args[0]));

		}
	}

	private static GremlinPipeline order(GremlinPipeline pipeline, String[] args) {
		return pipeline.order();
	}

	private static GremlinPipeline out(GremlinPipeline pipeline, String[] args) {
		return pipeline.out(parseStrings(args));
	}

	private static GremlinPipeline outE(GremlinPipeline pipeline, String[] args) {
		return pipeline.outE(parseStrings(args));
	}

	private static GremlinPipeline outV(GremlinPipeline pipeline, String[] args) {
		return pipeline.outV();
	}

	private static GremlinPipeline property(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.property(parseString(args[0]));
	}

	private static GremlinPipeline random(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.random(Double.parseDouble(args[0]));
	}

	private static GremlinPipeline range(GremlinPipeline pipeline, String[] args) {
		return pipeline.range(Integer.parseInt(args[0]),
				Integer.parseInt(args[1]));
	}

	private static GremlinPipeline scatter(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.scatter();
	}

	private static GremlinPipeline select(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.select();
	}

	private static GremlinPipeline simplePath(GremlinPipeline pipeline,
			String[] args) {
		return pipeline.simplePath();
	}

	private static GremlinPipeline store(GremlinPipeline pipeline, String[] args) {
		return pipeline.store();
	}

	private static GremlinPipeline table(GremlinPipeline pipeline, String[] args) {
		return pipeline.table();
	}
	
}

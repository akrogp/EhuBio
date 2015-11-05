package es.ehubio.cli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgParser {
	private final static String TAB = "        ";
	private final static int WIDTH = 80;
	private final String command, description;	
	private final List<Argument> opts = new ArrayList<Argument>();
	private final Map<Integer, Argument> mapArgs = new HashMap<Integer, Argument>();
	
	public ArgParser( String command ) {
		this(command, null);
	}
	
	public ArgParser( String command, String description ) {
		this.command = command;
		this.description = description;
	}
	
	public void addOption( Argument opt ) {
		opts.add(opt);
	}
	
	public List<Argument> parseArgs( String[] args ) throws ArgException {
		List<Argument> opts = parseOpts(args);
		int check = checkMandatory(opts);
		if( check != 0 )
			throw new ArgException(String.format("%d mandatory argument(s) missing", check));
		return opts;
	}
	
	public Argument getArgument( int id ) {
		return mapArgs.get(id);
	}
	
	private List<Argument> parseOpts( String[] args ) throws ArgException {
		mapArgs.clear();
		List<Argument> opts = new ArrayList<Argument>();
		for( int i = 0; i < args.length; i++ ) {
			Argument opt = null;
			if( args[i].startsWith("--") )
				opt = findLongOpt(args[i]);
			else if( args[i].startsWith("-") ) {
				for( int j = 1; j < args[i].length(); j++ ) {
					char arg = args[i].charAt(j);
					opt = findShortOpt(arg);
					if( opt == null )
						throw new ArgException(String.format("Invalid option '%c'", arg));
					if( opt.getParam() != null && j < args[i].length()-1 )
						throw new ArgException(String.format("Parameter required for option '%c'", arg));
				}
			} else if( args[i].length() == 1 )
				opt = findShortOpt(args[i].charAt(0));
			else
				opt = findLongOpt(args[i]);
			if( opt == null )
				throw new ArgException(String.format("Invalid option '%s'", args[i]));
			if( opt.getParam() != null ) {
				i++;
				if( i == args.length || args[i].startsWith("-") )
					throw new ArgException(String.format("Parameter required for option '%s'", args[i-1]));
				opt.setParam(args[i]);
			}
			opts.add(opt);
			mapArgs.put(opt.getId(), opt);
		}
		return opts;
	}	
	
	private int checkMandatory(List<Argument> opts) {
		int count = 0;
		for( Argument opt : this.opts )
			if( !opt.isOptional() )
				count++;
		for( Argument opt : opts )
			if( !opt.isOptional() )
				count--;
		return count;
	}
	
	public String getUsage() {
		StringBuilder str = new StringBuilder();
		str.append("Prototype:\n");
		addLines(str, buildPrototype(), 1, true);
		if( description != null ) {
			str.append("\nDescription:\n");
			addLines(str, description, 1);
		}
		str.append("\nOptions:\n");
		for( Argument opt : opts ) {
			str.append(TAB);
			if( opt.getShortOption() != null ) {
				str.append(String.format("-%c", opt.getShortOption()));
				if( opt.getLongOption() != null )
					str.append(", ");
			}
			if( opt.getLongOption() != null )
				str.append(String.format("--%s", opt.getLongOption()));
			if( opt.getParam() != null )
				str.append(String.format(" <%s>", opt.getParam()));
			str.append('\n');
			if( opt.getDescription() != null )
				addLines(str, opt.getDescription(), 2);
		}
		return str.toString();
	}
	
	private void addLines(StringBuilder str, String text, int tabs) {
		addLines(str, text, tabs, false);
	}
	
	private void addLines(StringBuilder str, String text, int tabs, boolean indent ) {
		for( String line : Terminal.splitLine(text, WIDTH-tabs*TAB.length(),indent?TAB:null) ) {
			for( int i = 0; i < tabs; i++ )
				str.append(TAB);
			str.append(line);
			str.append('\n');
		}
	}
	
	private String buildPrototype() {
		List<Argument> shortOptional = new ArrayList<Argument>();
		List<Argument> shortMandatory = new ArrayList<Argument>();
		List<Argument> expanded = new ArrayList<Argument>();
		for( Argument opt : opts ) {
			if( opt.getParam() != null || opt.getShortOption() == null )
				expanded.add(opt);
			else if( opt.isOptional() )
				shortOptional.add(opt);
			else
				shortMandatory.add(opt);
		}
		StringBuilder str = new StringBuilder(command);
		if( !shortOptional.isEmpty() ) {
			str.append(" [-");
			for( Argument opt : shortOptional )
				str.append(opt.getShortOption());
			str.append("] ");
		}
		if( !shortMandatory.isEmpty() ) {
			str.append(" -");
			for( Argument opt : shortMandatory )
				str.append(opt.getShortOption());
		}
		for( Argument opt : expanded ) {
			str.append(' ');
			if( opt.isOptional() )
				str.append('[');
			if( opt.getShortOption() != null ) {
				str.append("-");
				str.append(opt.getShortOption());
			} else {
				str.append("--");
				str.append(opt.getLongOption());
			}
			if( opt.getParam() != null ) {
				str.append(" <");
				str.append(opt.getParam());
				str.append('>');
			}
			if( opt.isOptional() )
				str.append(']');
		}
		return str.toString();
	}
	
	private Argument findLongOpt( String arg ) {
		for( Argument opt : opts )
			if( opt.getLongOption() != null && opt.getLongOption().equals(arg) )
				return opt;
		return null;
	}
	
	private Argument findShortOpt( char arg ) {
		for( Argument opt : opts )
			if( opt.getShortOption() != null && opt.getShortOption() == arg )
				return opt;
		return null;
	}
}

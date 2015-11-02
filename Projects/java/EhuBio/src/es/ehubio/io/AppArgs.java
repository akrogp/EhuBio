package es.ehubio.io;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppArgs {
	public static class Arg {
		public Arg( boolean mandatory, Character ch, String name, String param, String desc) {
			this.mandatory = mandatory;
			this.ch = ch;
			this.name = name;
			this.param = param;
			this.desc = desc;
		}
		public Character getCh() {
			return ch;
		}
		public String getName() {
			return name;
		}
		public String getDesc() {
			return desc;
		}
		public String getParam() {
			return param;
		}
		public boolean isMandatory() {
			return mandatory;
		}
		@Override
		public String toString() {
			StringBuilder str = new StringBuilder();
			if( ch != null )
				str.append(String.format("-%s", ch));
			if( name != null )
				str.append(String.format("%s--%s", ch!=null?", ":"",name));
			if( desc != null ) {
				str.append('\t');
				str.append(desc);
			}
			return str.toString();
		}
		private final boolean mandatory;
		private final Character ch;
		private final String name;
		private final String desc;
		private final String param;
	}	
	
	private final List<Arg> args = new ArrayList<>();

	public List<Arg> getArgs() {
		return args;
	}
	
	public void add( Arg arg ) {
		args.add(arg);
	}
	
	public String getUsage() {
		StringBuilder str = new StringBuilder();
		for( Arg arg : args ) {
			if( str.length() != 0 )
				str.append(' ');
			if( !arg.isMandatory() )
				str.append('[');
			if( arg.getCh() != null )
				str.append(String.format("-%s", arg.getCh()));
			else
				str.append(String.format("--%s", arg.getName()));
			if( arg.getParam() != null )
				str.append(String.format(" %s", arg.getParam()));
			if( !arg.isMandatory() )
				str.append(']');
		}
		return str.toString();
	}
	
	public List<Arg> parse( String[] mainArgs ) {
		List<Arg> results = new ArrayList<>();
		Set<Arg> used = new HashSet<>();
		Arg found;
		for( int i = 0; i < mainArgs.length; i++ ) {
			found = null;
			for( Arg arg : args )
				if( arg.getCh() != null && mainArgs[i].equals(String.format("-%s", arg.getCh())) ||
					arg.getName() != null && mainArgs[i].equals(String.format("--%s", arg.getName())) ) {
					found = arg;
					break;
				}
			if( found == null ) {
				System.out.println(String.format("Argument %s not recognized", mainArgs[i]));
				return null;
			}
			if( found.getParam() != null ) {
				if( i==mainArgs.length-1 || mainArgs[i+1].startsWith("-") ) {
					System.out.println(String.format("Argument %s requires %s parameter", mainArgs[i], found.getParam()));
					return null;
				}
				i++;
			}
			results.add(new Arg(found.isMandatory(), found.getCh(), found.getName(), found.getParam()!=null?mainArgs[i]:null, found.getDesc()));
			used.add(found);
		}
		for( Arg arg : args )
			if( arg.isMandatory() && !used.contains(arg) ) {
				System.out.println(String.format("Mandatory argument '%s' is missing!", arg.getName()!=null?arg.getName():arg.getCh()));
				return null;
			}
		return results;
	}
}
package es.ehubio.tools;

import java.util.Arrays;

public class Command {
	public interface Interface {
		public String getUsage();

		public int getMinArgs();

		public int getMaxArgs();
		
		public void run( String[] args ) throws Exception;
	}
	
	public static void run( String pkg, String[] args ) {
		if( args.length == 0 ) {
			System.out.println( "Usage:\n\tCommand <command> [args]" );
			return;
		}		
		try {
			Interface cmd = (Interface)Class.forName(String.format("%s.%s", pkg,args[0])).newInstance();
			int nargs = args.length-1;
			if( nargs < cmd.getMinArgs() || (cmd.getMaxArgs() >= 0 && nargs > cmd.getMaxArgs()) ) {
				System.out.println( "Usage:\n\tCommand "+args[0]+" "+cmd.getUsage() );
				return;
			}
			cmd.run(Arrays.copyOfRange(args, 1, args.length));
		} catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public static void main( String[] args ) {
		run("es.ehubio.tools",args);
	}
}

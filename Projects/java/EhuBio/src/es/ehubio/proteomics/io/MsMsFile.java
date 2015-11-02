package es.ehubio.proteomics.io;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import es.ehubio.io.Streams;
import es.ehubio.proteomics.MsMsData;
import es.ehubio.proteomics.Protein;

public abstract class MsMsFile {
	private final static Logger logger = Logger.getLogger(MsMsFile.class.getName());
	private File originalFile;
	protected MsMsData data;
	
	public static MsMsFile autoDetect( String path ) throws Exception {
		MsMsFile result = null;
		for( MsMsFile file : getParsers() )
			if( file.checkSignature(path) ) {
				result = file;
				break;
			}
		if( result == null )
			logger.warning("File format not detected");
		else
			logger.info(String.format("Detected %s file format",result.getClass().getSimpleName()));
		return result;
	}
	
	private static List<MsMsFile> getParsers() {
		List<MsMsFile> list = new ArrayList<>();
		//File dir = new File(MsMsFile.class.getResource("/es/ehubio/proteomics/io").getFile());
		//if( !dir.exists() ) { // Inside jar ...
			list.add(new Mzid());
			list.add(new ProteomeDiscovererMsf());
			list.add(new ProteomeDiscovererTxt());
			list.add(new Plgs());
			list.add(new XTandemXml());
			list.add(new MascotDat());
		/*} else {
			for( String name : dir.list() ) {
				if( !name.endsWith(".class") )
					continue;
				try {
					Class<?> cls = Class.forName("es.ehubio.proteomics.io."+name.substring(0, name.length()-6));
					if( MsMsFile.class.isAssignableFrom(cls) )
						list.add((MsMsFile)cls.newInstance());
				} catch (Exception e) {
					continue;
				}			
			}
		}*/
		return list;
	}
	
	public static MsMsData autoLoad( String path, boolean loadFragments ) throws Exception {
		File dir = new File(path);		
		if( !dir.isDirectory() )
			return autoLoadFile(path, loadFragments);
		
		MsMsData data = null;
		for( File file : dir.listFiles() ) {
			MsMsData tmp = autoLoadFile(file.getAbsolutePath(), loadFragments);
			if( data == null )
				data = tmp;
			else
				data.mergeFromPeptide(tmp);
		}
		return data;
	}
	
	private static MsMsData autoLoadFile( String path, boolean loadFragments ) throws Exception {
		MsMsFile file = autoDetect(path);
		if( file == null )
			return null;
		return file.load(path, loadFragments);
	}
	
	public final MsMsData load( String path, boolean loadFragments ) throws Exception {
		logger.info(String.format("Loading '%s' ...", path));
		data = loadPath(path, loadFragments);
		if( data == null ) {
			InputStream input = Streams.getBinReader(path);
			data = loadStream(input, loadFragments);
			input.close();
		}
		originalFile = new File(path);
		solveIssues();
		if( data == null )
			logger.warning("Not loaded!");
		else {
			logger.info(String.format("Loaded! %s",data.toString()));
			if( data.getTitle() == null )
				data.setTitle(new File(path).getName());
		}
		return data;
	}
	
	protected MsMsData loadStream( InputStream input, boolean loadFragments ) throws Exception {
		return null;
	}
	
	protected MsMsData loadPath( String path, boolean loadFragments ) throws Exception {
		return null;
	}
	
	private void solveIssues() {
		for( Protein protein : data.getProteins() ) {
			String acc = protein.getAccession();
			int i = acc.indexOf(' ');
			if( i == -1 )
				continue;			
			protein.setAccession(acc.substring(0,i));
			protein.setDescription(acc.substring(i+1, acc.length()));
		}
	}	

	public final void save( String path ) throws Exception {
		File file = new File(path);
		boolean addExt = true;
		if( file.isDirectory() )
			if( originalFile != null )
				path = new File(file,originalFile.getName()).getAbsolutePath();
			else
				addExt = false;
		if( addExt && path.indexOf('.') == -1 )
			path = String.format("%s.%s", path, getFilenameExtension());
		logger.info(String.format("Saving into '%s' ...", path));
		
		boolean ok = savePath(path); 
		if( !ok ) {
			OutputStream output = Streams.getBinWriter(path);
			ok = saveStream(output);
			output.close();
		}
		
		if( !ok )
			logger.warning("Not saved!");
		else
			logger.info("Saved!");
	}
	
	protected boolean saveStream( OutputStream output ) throws Exception {
		return false;
	}
	
	protected boolean savePath( String path ) throws Exception {
		return false;
	}
	
	public List<File> getPeakRefs( String optionalPath ) throws Exception {
		return null;
	}
	
	public abstract String getFilenameExtension();
	
	public final boolean checkSignature( String path ) throws Exception {
		if( checkSignaturePath(path) )
			return true;
		
		InputStream input = Streams.getBinReader(path);
		boolean res = checkSignatureStream(input);
		input.close();
		return res;
	}

	protected boolean checkSignatureStream(InputStream input) throws Exception {
		return false;
	}
	
	protected boolean checkSignaturePath(String path) throws Exception {
		return false;
	}
}

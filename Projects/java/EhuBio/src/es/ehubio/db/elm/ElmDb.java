package es.ehubio.db.elm;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.ehubio.io.UnixCfgReader;


public class ElmDb {
	private String version;
	private List<ElmClass> elmClasses;
	private List<ElmInstance> elmInstances;
	
	public void load(String path) throws IOException {
		File dir = new File(path);
		if( dir.isFile() ) {
			elmClasses = loadClasses(dir);
			return;
		}
		
		File classesFile = new File(dir, "elm_classes.tsv");
		if( classesFile.isFile() )
			elmClasses = loadClasses(classesFile);
		
		File instancesFile = new File(dir, "elm_instances.tsv");
		if( instancesFile.isFile() )
			elmInstances = loadInstances(instancesFile);
	}
	
	private List<ElmClass> loadClasses(File elmFile) throws IOException {
		List<ElmClass> result = new ArrayList<>();
		try(UnixCfgReader rd = new UnixCfgReader(new FileReader(elmFile))) {
			String line;
			String[] fields;
			boolean first = true;
			while( (line=rd.readLine()) != null ) {
				if( first == true ) {
					first = false;
					continue;
				}
				if( !rd.getComment("ELM_Classes_Download_Version").contains("1.4") )
					throw new IOException("ELM file version not supported");
				fields = line.replaceAll("\"","").split("\t");
				ElmClass item = new ElmClass();
				item.setAcc(fields[0]);
				item.setId(fields[1]);
				item.setName(fields[2]);
				item.setDesc(fields[3]);
				item.setRegex(fields[4]);
				item.setProb(Double.parseDouble(fields[5]));
				item.setInstances(Integer.parseInt(fields[6]));
				item.setPdb(Integer.parseInt(fields[7]));
				result.add(item);
			}
			String version = rd.getComment("ELM_Classes_Download_Date");
			if( version != null )
				this.version = version.split(" ")[1];
		}
		return result;
	}

	private List<ElmInstance> loadInstances(File elmFile) throws IOException {
		List<ElmInstance> result = new ArrayList<>();
		try(UnixCfgReader rd = new UnixCfgReader(new FileReader(elmFile))) {
			String line;
			String[] fields;
			boolean first = true;
			while( (line=rd.readLine()) != null ) {
				if( first == true ) {
					first = false;
					continue;
				}
				if( !rd.getComment("ELM_Instance_Download_Version").contains("1.4") )
					throw new IOException("ELM file version not supported");
				fields = line.replaceAll("\"","").split("\t");
				ElmInstance item = new ElmInstance();
				item.setAcc(fields[0]);
				item.setType(fields[1]);
				item.setCls(fields[2]);
				item.setProtName(fields[3]);
				item.setProtAcc(fields[4]);
				item.setProtAccs(fields[5]);
				item.setStart(Integer.parseInt(fields[6]));
				item.setEnd(Integer.parseInt(fields[7]));
				item.setRefs(fields[8]);
				item.setMethods(fields[9]);
				item.setLogic(fields[10]);
				item.setPdb(fields[11]);
				item.setOrganism(fields[12]);
				result.add(item);
			}
		}
		return result;
	}
	
	public String getVersion() {
		return version;
	}
	
	public List<ElmClass> getElmClasses() {
		if( elmClasses == null )
			elmClasses = new ArrayList<>();
		return elmClasses;
	}
	
	public List<ElmInstance> getElmInstances() {
		if( elmInstances == null )
			elmInstances = new ArrayList<>();
		return elmInstances;
	}
}

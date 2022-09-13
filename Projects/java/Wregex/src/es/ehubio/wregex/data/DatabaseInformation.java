package es.ehubio.wregex.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "database")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatabaseInformation implements Serializable {
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlTransient
	public String getFullName() {			
		String v = getVersion();
		if( v == null )
			return getName();
		else
			return getName() + " (" + getVersion() + ")";
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getHint() {
		return hint;
	}
	
	public void setHint(String hint) {
		this.hint = hint;
	}
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
		File file = new File(path);
		if( file.exists() )
			lastModified = file.lastModified();
		else
			lastModified = -1;
	}
	
	public String getExtPattern() {
		return extPattern;
	}
	
	public void setExtPattern(String extPattern) {
		this.extPattern = extPattern;
	}
	
	public String getVersionFile() {
		return versionFile;
	}
	
	public void setVersionFile(String versionFile) {
		this.versionFile = versionFile;
		version = null;
	}		
	
	public String getVersion() {
		if( versionFile == null )
			return version;
		File v = new File(versionFile);
		if( lastModifiedVersion != v.lastModified() ) {
			reloadVersion();
			lastModifiedVersion = v.lastModified();
		}
		return version;
	}
	
	private void reloadVersion() {
		version = null;
		try {
			BufferedReader rd;
			rd = new BufferedReader(new FileReader(getVersionFile()));
			version = rd.readLine();
			rd.close();
		} catch(IOException e) {
		}
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	@Override
	public String toString() {
		return getFullName();
	}

	public long getLastModified() {
		return lastModified;
	}
	
	public boolean exists() {
		File file = new File(path);
		return file.exists();
	}

	public Integer getWregexVersion() {
		return wregexVersion;
	}

	public void setWregexVersion(Integer wregexVersion) {
		this.wregexVersion = wregexVersion;
	}

	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private String hint;
	private String path;
	private String extPattern;
	private String version;
	private String versionFile;
	private Integer wregexVersion;
	private long lastModifiedVersion = -1;
	private long lastModified = -1;
}

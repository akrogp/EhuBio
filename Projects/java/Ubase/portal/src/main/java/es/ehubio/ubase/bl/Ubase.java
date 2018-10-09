package es.ehubio.ubase.bl;

import static es.ehubio.ubase.Constants.EXP_PREFIX;
import static es.ehubio.ubase.Constants.META_FILE;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;

import es.ehubio.ubase.Locator;
import es.ehubio.ubase.dl.input.Metadata;
import es.ehubio.ubase.dl.input.Metafile;
import es.ehubio.ubase.dl.providers.Provider; 

@LocalBean
@Stateless
public class Ubase implements Serializable {
	private static final long serialVersionUID = 1L;
	@PersistenceContext
	private EntityManager em;

	public void submit(Metadata metadata, Provider provider, File data) throws Exception {
		metadata.setData(data);
		metadata.setSubDate(new Date());
		Metafile.save(metadata, new File(data, META_FILE));
	}
	
	public List<Metadata> getPendingSubmissions() throws Exception {
		List<Metadata> results = new ArrayList<>();
		File dir = new File(Locator.getConfiguration().getSubmissionPath());
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith(EXP_PREFIX);
			}
		});
		for( File file : files ) {
			File metafile = new File(file,META_FILE);
			if( !file.exists() )
				continue;
			Metadata metadata = Metafile.load(metafile);
			metadata.setData(file);
			results.add(metadata);
		}
		return results;
	}

	public void publish(Metadata metadata) throws Exception {
		File dst = new File(Locator.getConfiguration().getArchivePath(), metadata.getData().getName());
		FileUtils.moveDirectory(metadata.getData(), dst);
		metadata.setPubDate(new Date());
		Metafile.save(metadata, new File(dst, META_FILE));
	}
}

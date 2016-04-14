package edu.ucdenver.cse.GRID;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class GRIDutils {
	
	public static String chooseFile() {
		JFileChooser chooser = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "xml config files", "xml");
	    
	    chooser.setFileFilter(filter);
	    
	    int returnVal = chooser.showOpenDialog(null);
	        
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	            
	        return chooser.getSelectedFile().toString();
	    }
	    
	    else {
	    	return "";
	    }
	}
}

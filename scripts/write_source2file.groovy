/* :name=       Write source to file :description=
 * #Purpose:	Write all source segments to a file
 * #Files:	Writes 'allsource.txt' in the current project's root
 * 
 * @author:	Kos Ivantsov
 * @date:	2013-07-16
 * @version:	0.2
 */

/* change "includefilenames" to anything but 'yes' (with quotes)
 * if you don't need filenames to be included in the file */

def includefilenames = 'no'
def includerepetitions = 'no'

import static javax.swing.JOptionPane.*
import static org.omegat.util.Platform.*

// abort if a project is not opened yet
def prop = project.projectProperties
if (!prop) {
	final def title = 'Source to File'
	final def msg   = 'Please try again after you open a project.'
	showMessageDialog null, msg, title, INFORMATION_MESSAGE
	return
}

def folder = prop.projectRoot+'/script_output'
def fileloc = folder+'/allsource.txt'
writefile = new File(fileloc)
if (! (new File(folder)).exists()) {
	(new File(folder)).mkdir()
	}

writefile.write("", 'UTF-8')
def count = 0
def uniqline

if (includefilenames == 'yes') {
	files = project.projectFiles;
	for (i in 0 ..< files.size())
	{
		fi = files[i];
		marker = "+${'='*fi.filePath.size()}+\n"
		writefile.append("$marker|$fi.filePath|\n$marker", 'UTF-8')
		for (j in 0 ..< fi.entries.size())
		{
		ste = fi.entries[j];
		source = ste.getSrcText();
		writefile.append source +"\n", 'UTF-8'
		count++;
		}
	}
} else {
	project.allEntries.each { ste ->
	source = ste.getSrcText();
	writefile.append source+"\n",'UTF-8'
	count++
		}
	console.println "$count segments found in all files"
	if (includerepetitions != 'yes') {
		count = 0
		uniqline = writefile.readLines().unique()
		//console.println uniqline
		writefile.write("",'UTF-8')
		uniqline.each {
		writefile.append "$it\n\n",'UTF8';
		count++
				}
			}
	}

console.println count +" segments written to "+ writefile
final def title = 'Source to File'
final def msg   = count +" segments"+"\n"+"written to \n"+ writefile
showMessageDialog null, msg, title, INFORMATION_MESSAGE
return

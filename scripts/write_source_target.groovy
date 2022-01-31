/* :name=Write Source and Target to TXT :description=
 * #Purpose:	Write all source segments to a file
 * #Files:	Writes 'source_target.txt' in the 'script_output' subfolder
 *	in the current project's root
 * 
 * @author:	Kos Ivantsov
 * @date:	2013-07-16
 * @version:	0.2
 */
 
/*
 * Here you can specify delimiters for source and target, and segments
 */
 
source_target_delim='\t' //one line break
segment_delim="\n"*1 //three line breaks


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
def fileloc = folder+'/source_target.txt'
writefile = new File(fileloc)
if (! (new File(folder)).exists()) {
	(new File(folder)).mkdir()
	}

writefile.write("", 'UTF-8')
def count = 0

files = project.projectFiles;
for (i in 0 ..< files.size())
{
	fi = files[i];
	marker = "+${'='*fi.filePath.size()}+\n"
	/* writefile.append("$marker|$fi.filePath|\n$marker", 'UTF-8') */
	writefile.append("$fi.filePath\t$fi.filePath\n", 'UTF-8')
	for (j in 0 ..< fi.entries.size())
	{
	ste = fi.entries[j]
	source = ste.getSrcText()
	target = project.getTranslationInfo(ste) ? project.getTranslationInfo(ste).translation : null;
		if (target == null)
		target = ''
		if (target.size() == 0 )
		target = "<EMPTY>"
	writefile.append source +source_target_delim+target+segment_delim, 'UTF-8'
	count++;
	}
}
console.println count +" segments written to "+ writefile
final def title = 'Source and Target to File'
final def msg   = count +" segments"+"\n"+"written to \n"+ writefile
showMessageDialog null, msg, title, INFORMATION_MESSAGE
return

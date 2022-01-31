/* :name = Write Selected Files to TMX :description=
 * Purpose:	Export source and translation segments of user selected 
 *		files into TMX-file
 * #Files:	Writes 'selected_files_<date_time>.tmx'
 *		in 'script_output/tmx_export' subfolder of the current project's root
 * 		If selected files don't contain translations, the newly written file
 * 		will be deleted.
 * #Format:	TMX v.1.4
 * #Details:	http://wp.me/p3fHEs-6g
 *
 * @author	Kos Ivantsov
 * @date	2017-09-17
 * @version	0.7
 */

import java.lang.String
import java.nio.file.Paths
import javax.swing.filechooser.FileFilter
import javax.swing.JFileChooser
import org.omegat.util.StringUtil
import org.omegat.util.TMXReader2
import org.omegat.util.TMXWriter
import static javax.swing.JOptionPane.*
import static org.omegat.util.Platform.*

// when 'levelTwo' is 'false', then OmegaT-compatible TMX is written,
// when 'levelTwo' is 'true', then TMX is similar to Level 2 TMX created by OmegaT
// (alternative translations are not treated as such by this script)

def levelTwo = false

def prop = project.projectProperties
if (!prop) {
	final def title = 'Export TMX from selected files'
	final def msg   = 'Please try again after you open a project.'
	showMessageDialog null, msg, title, INFORMATION_MESSAGE
	return
}
def sourceroot = Paths.get(prop.getSourceRoot()).normalize()
srcroot = new File(prop.getSourceRoot())

//select files to export
JFileChooser fc = new JFileChooser(
	currentDirectory: srcroot,
	dialogTitle: "Choose files to export",
	fileSelectionMode: JFileChooser.FILES_ONLY, 
	//the file filter must show also directories, in order to be able to look into them
	multiSelectionEnabled: true)

if(fc.showOpenDialog() != JFileChooser.APPROVE_OPTION) {
console.println "Canceled"
return
}

selfiles = Paths.get(fc.selectedFiles[0].toString()).normalize()
//~ if (!(fc.selectedFiles[0].toString().contains(sourceroot.toString()))) {
if (!(selfiles.startsWith(sourceroot))) {
		console.println "Selection outside of ${prop.getSourceRoot()} folder"
		final def title = 'Wrong file(s) selected'
		final def msg   = "Files must be in ${prop.getSourceRoot()} folder."
		showMessageDialog null, msg, title, INFORMATION_MESSAGE
		return
	}

//create folder
def foldercreate
def folder = prop.projectRoot+'script_output' + File.separator + 'tmx_export' + File.separator
if (! (new File (folder)).exists()) {
	(new File(folder)).mkdirs()
	foldercreate = true
	}
if (foldercreate) {console.println("Folder \"$folder\" created")}
def curtime = new Date().format("MMM-dd-yyyy_HH.mm")

def fileloc = folder+'selected_files_'+curtime+'.tmx'
//exportfile = new File(fileloc)


//choose where to save the TMX
JFileChooser imptmx = new JFileChooser(
		currentDirectory: new File(folder),
		dialogTitle: "Save TMX as...",
		approveButtonText: "Save",
		multiSelectionEnabled: false,
		fileFilter: [getDescription: {-> "*.tmx"}, accept:{file-> file ==~ /.*?\.[Tt][Mm][Xx]/ || file.isDirectory() }] as FileFilter)
		imptmx.setSelectedFile(new File(fileloc))
		if(imptmx.showSaveDialog() != JFileChooser.APPROVE_OPTION) {
		console.println "Canceled"
			if (foldercreate)
			(new File(folder)).deleteDir()
			console.println("Folder \"$folder\" deleted")
		return
		}
exportfile = imptmx.selectedFile
console.println exportfile

if (prop.isSentenceSegmentingEnabled())
	segmenting = TMXReader2.SEG_SENTENCE
	else
	segmenting = TMXReader2.SEG_PARAGRAPH

def sourceLocale = prop.getSourceLanguage().toString()
def targetLocale = prop.getTargetLanguage().toString()

exportfile.write("""\
<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n
<!DOCTYPE tmx SYSTEM \"tmx14.dtd\">
<tmx version=\"1.4\">
 <header
  creationtool=\"OmegaTScripting\"
  segtype=\"$segmenting\"
  o-tmf=\"OmegaT TMX\"
  adminlang=\"EN-US\"
  srclang=\"$sourceLocale"\
  datatype=\"plaintext\"
 >
""", 'UTF-8')
fc.selectedFiles.each{
	fl = "${it.toString()}" - "${sourceroot.toString()}"
	fl = StringUtil.makeValidXML(fl)
	exportfile.append("  <prop type=\"Filename\">" + fl + "</prop>\n", 'UTF-8')
}
exportfile.append("""\
 </header>
   <body>
""", 'UTF-8')

def count = 0
fc.selectedFiles.each{
	fl = "${it.toString()}" - "${sourceroot.toString()}" 
	fl = fl.substring(1, fl.length())
	fl = Paths.get(fl).normalize()
	files = project.projectFiles
	files.each{
		projfile = Paths.get(it.filePath).normalize()
		if ( "$projfile" != "$fl" ) {
		println "Skipping to the next file"
		}else{
	it.entries.each {
	def info = project.getTranslationInfo(it)
	def changeId = info.changer
	def changeDate = info.changeDate
	def creationId = info.creator
	def creationDate = info.creationDate
	def alt = 'unknown'
	if (info.isTranslated()) {		
		source = StringUtil.makeValidXML(it.srcText)
		target = StringUtil.makeValidXML(info.translation)
		if (levelTwo) {
			source = TMXWriter.makeLevelTwo(source)
			target = TMXWriter.makeLevelTwo(target)
		}

		exportfile.append("""\
    <tu>
      <tuv xml:lang=\"$sourceLocale\">
        <seg>$source</seg>
      </tuv>
      <tuv xml:lang=\"$targetLocale\"\
 changeid=\"${changeId ?: alt }\"\
 changedate=\"${ changeDate > 0 ? new Date(changeDate).format("yyyyMMdd'T'HHmmss'Z'") : alt }\"\
 creationid=\"${creationId ?: alt }\"\
 creationdate=\"${ creationDate > 0 ? new Date(creationDate).format("yyyyMMdd'T'HHmmss'Z'") : alt }\"\
>
        <seg>$target</seg>
      </tuv>
    </tu>
""", 'UTF-8')
		count++
				}
			}
		}
	}
}
exportfile.append("  </body>\n</tmx>", 'UTF-8')
final def title
final def msg
if (count == 0) {
	new File(exportfile.toString()).delete()
	if (foldercreate) {
		(new File(folder)).deleteDir()
	}
	title = 'TMX file not written'
	msg   = "Selected file(s) contained $count TU's"
}else{
	title = 'TMX file written'
	msg   = "$count TU's written to " + exportfile.toString()
}

console.println msg
showMessageDialog null, msg, title, INFORMATION_MESSAGE
return
/* :name = Write Project to TMX :description=
 * Purpose: Export source and translation segments of user selected 
 *          files into TMX-file
 * #Files:  Writes '<date_time>.tmx'
 *          in 'script_output/tmx_export' subfolder of the current project's root.
 *
 * #Format: TMX v.1.4
 *
 * @author  Kos Ivantsov
 * @date    2019-04-15
 * @version 0.1
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
    final def title = 'Export TMX for the whole project'
    final def msg   = 'Please try again after you open a project.'
    showMessageDialog null, msg, title, INFORMATION_MESSAGE
    return
}

def sourceLocale = prop.getSourceLanguage().toString()
def targetLocale = prop.getTargetLanguage().toString()
curtime = new Date().format("yyyyMMddHHmm")
folder = new File(prop.projectRoot + "script_output" + File.separator + "tmx_export")
def foldercreate
if (! folder.exists()) {
    folder.mkdirs()
    foldercreate = true
}

exportfile = new File(folder.toString() + File.separator + sourceLocale + "_" + targetLocale + "_" + curtime + ".tmx")
if (prop.isSentenceSegmentingEnabled()) {
    segmenting = TMXReader2.SEG_SENTENCE
} else {
    segmenting = TMXReader2.SEG_PARAGRAPH
}
count = 0
tmxContents = new StringWriter()
tmxContents << """\
<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n
<!DOCTYPE tmx SYSTEM \"tmx14.dtd\">
<tmx version=\"1.4\">
 <header
  creationtool=\"OmegaTScripting\"
  segtype=\"$segmenting\"
  o-tmf=\"OmegaT TMX\"
  adminlang=\"EN-US\"
  srclang=\"$sourceLocale"\
  datatype=\"plaintext\"/>
  <body>
"""
files = project.projectFiles
Thread.start {
    files.each{
        it.entries.each {
            def info = project.getTranslationInfo(it)
            def changeId = info.changer
            def changeDate = info.changeDate
            def creationId = info.creator
            def creationDate = info.creationDate
            def alt = 'unknown'
            source = StringUtil.makeValidXML(it.srcText)
            if (info.isTranslated()) {      
                target = StringUtil.makeValidXML(info.translation)
            } else {
                target = ""
            }
                if (levelTwo) {
                    source = TMXWriter.makeLevelTwo(source)
                    target = TMXWriter.makeLevelTwo(target)
                }
            tmxContents << """
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
    """
            count++
        }
    }
    
    tmxContents << "  </body>\n</tmx>"
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
        exportfile.write(tmxContents.toString(), "UTF-8")
        title = 'TMX file written'
        msg   = "$count TU's written to " + exportfile.toString()
    }
    
    console.println msg
    //showMessageDialog null, msg, title, INFORMATION_MESSAGE
    return
}
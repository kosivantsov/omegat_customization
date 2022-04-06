/* :name = MT@CLI - DeepL :description=foo
 * 
 * @author      Manuel Souto Pico, Kos Ivantsov
 * @date        2022-04-06
 * @version     0.0.5
 */

/* 
 * @versions: 
 * 0.0.1:   first version
 * 0.0.2:   get api key from text file
 * 0.0.3:   get languages from project settings
 * 0.0.4:   runs on the command line
 * 0.0.5:   writes to the internal TM and to backup file project_mt.tmx
 */

// dependencies
@Grapes([
	@Grab(group='com.github.groovy-wslite', module='groovy-wslite', version='1.1.3'),
    @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1')
])


import org.omegat.util.StaticUtils
import org.omegat.util.StringUtil
import groovyx.net.http.RESTClient
import static org.omegat.core.events.IProjectEventListener.PROJECT_CHANGE_TYPE.*
import org.omegat.core.data.PrepareTMXEntry
import org.omegat.core.data.TMXEntry
import javax.swing.JOptionPane;
import org.omegat.util.OConsts;
import java.io.*
import java.nio.file.Files
import java.nio.file.*
import java.nio.file.Path
import java.nio.file.Paths
import org.omegat.gui.main.ProjectUICommands
import org.omegat.core.data.ProjectTMX
import org.omegat.util.OConsts
 

// constants

def get_api_key() {

    config_dir = StaticUtils.getConfigDir()
    api_key_file = new File(config_dir + "deepl_api_key.txt")
    if (! api_key_file.exists()) {
        console.println("API key file (deepl_api_key.txt) not found in the configuration folder.")
        return
    }
    String api_key = api_key_file.text
    return api_key
}


def get_transl2(segm_list, source_lang, target_lang) {
    return segm_list
}

def get_transl(segm_list, source_lang, target_lang) {

    api_key = get_api_key()
    
    try{
        def rest = new RESTClient('https://api.deepl.com/')

        def resp = rest.post(
            path: 'v2/translate',
            body: [
                auth_key: api_key, 
                text: segm_list, 
                source_lang: source_lang,
                target_lang: target_lang
            ],
            requestContentType: 'application/x-www-form-urlencoded' )
        data = resp.responseData // translations
    } catch(Exception ex) {
        log.warn "Error: ${ex.message}"
    }

    return data.translations.collect { it.text }
}

// only run the script when the project is loading (by default it runs three times)
if (eventType != LOAD) {
    return
}

console.println(">>>>> PROJECT LOADING")

// https://josdem.io/techtalk/groovy/groovy_restclient/

// prop: also sometimes called config in the code base
def timestamp = new Date().format("YYYYMMddHHmm")

def prop = project.projectProperties
def project_root =  prop.projectRootDir
def source_lang =   prop.getSourceLanguage()
def target_lang =   prop.getTargetLanguage()
def proj_name =     prop.projectName
def tmdir_fs =      prop.getTMRoot() // fs = forward slash
def omegat_dir = prop.getProjectInternal() // same as prop.projectInternal
// tmdir = new File(tmdir_fs) // not needed here

String final_omegat_tmx_fpath = prop.getProjectRoot() + prop.getProjectName() + OConsts.OMEGAT_TMX + OConsts.TMX_EXTENSION;
def project_save_fobj = new File(prop.projectInternal, OConsts.STATUS_EXTENSION)
tmxsave = prop.getProjectInternal() + OConsts.STATUS_EXTENSION
// def project_mt = new File(omegat_dir + "project_mt.tmx")
String project_mt_fpath = omegat_dir + "project_mt.tmx"


config_dir = StaticUtils.getConfigDir()
config_file = new File(config_dir + "containers_config.properties")

console.println("============================================")
console.println("")

console.println("final_omegat_tmx_fpath:" + final_omegat_tmx_fpath)
console.println("project_mt_fpath:      " + project_mt_fpath)
console.println("project_save_fobj:    " + project_save_fobj)

console.println("")
console.println("============================================")


x = final_omegat_tmx_fpath.getClass() // class java.lang.String
console.println(x)
x = project_mt_fpath.getClass() // class java.lang.String
console.println(x) 
x = tmdir_fs.getClass() // class java.lang.String
console.println(x) 
x = project_save_fobj.getClass() // class java.io.File
console.println(x) 
x = tmxsave.getClass() // class java.lang.String
console.println(x) 


console.println("###### SCRIPT IS RUNNING!!")



def segm_list = project.allEntries.collect { it.getSrcText() } // SourceTextEntry
def translations = get_transl(segm_list, source_lang, target_lang)


project.allEntries.each { ste ->

    // editor.gotoEntry(ste.entryNum()) 

    def index = ste.entryNum()-1
    def target = translations[index]

    def te = new PrepareTMXEntry()
    te.source = ste.getSrcText()
    te.translation = target
            
    // editor.replaceEditText(target)

    project.setTranslation(ste, te, true, TMXEntry.ExternalLinked.xAUTO)
    // setTranslation(ste, e, isDefault, TMXEntry.ExternalLinked.xAUTO)

}

// the true flag stands for project modified (@question: not sure why that matters)
project.ProjectTMX.save(prop, tmxsave, true)

// project.ProjectTMX.save(prop, project_save, true)
console.println(">>> Translated with DeepL")


// String sourceFilePath = final_omegat_tmx
// String destinationFilePath = project_mt2
// move of copy file
src_fpath_str = final_omegat_tmx_fpath  // string
tgt_fpath_str = project_mt_fpath        // string
(new AntBuilder()).copy(file: src_fpath_str, tofile: tgt_fpath_str)


// does not work
// Files.copy(final_omegat_tmx_fpath, project_mt_fpath, java.nio.file.StandardCopyOption.REPLACE_EXISTING)

// move export <proj>-omegat.tmx to omegat/project_save.tmx 
// move export <proj>-omegat.tmx to omegat/project_mt.tmx 
// Files.move(Paths.get(final_omegat_tmx), Paths.get(project_save), StandardCopyOption.REPLACE_EXISTING)
// Files.move(final_omegat_tmx, project_save, StandardCopyOption.REPLACE_EXISTING)
// Files.copy(src_fpath_str.toPath(), tgt_fpath_str.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)
Files.move(src_fpath_str.toPath(), tgt_fpath_str.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING)



// garbage collector...

return

/* @todo:
- overwrite only empty entries (using xmlParser.parse(projectSave))
- add it to scripts/cli in the customization
- send project_mt.tmx and excel to xdiff when MT'ing?? 
*/



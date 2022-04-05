/* :name = MT - DeepL :description=foo
 * 
 * @author      Manuel Souto Pico
 * @date        2022-04-04
 * @version     0.0.1
 */

/* 
 * @versions: 
 * 0.0.1:   first version
 * 0.0.2:   get api key from text file
 * 0.0.3:   get languages from project settings
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

// constants
config_dir = StaticUtils.getConfigDir()
api_key_file = new File(config_dir + "deepl_api_key.txt")
if (! api_key_file.exists()) {
    console.println("API key file (deepl_api_key.txt) not found in the configuration folder.")
    return
}
String api_key = api_key_file.text

def get_transl(segm_list, source_lang, target_lang) {
    
    String api_key = "4bf91305-0c1b-5a43-1a27-35af98ccdf55"

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



if (eventType == LOAD) {
    console.println(">>>>> PROJECT LOADING")

    // https://josdem.io/techtalk/groovy/groovy_restclient/

    def prop = project.projectProperties
    def source_lang = prop.getSourceLanguage()
    def target_lang = prop.getTargetLanguage()

    console.println("###### SCRIPT IS RUNNING!!")

    def segm_list = project.allEntries.collect { it.getSrcText() } // SourceTextEntry
    def translations = get_transl(segm_list, source_lang, target_lang)

    project.allEntries.each { seg ->

        // editor.gotoEntry(seg.entryNum())

        def index = seg.entryNum()-1
        def target = translations[index]
                
        // editor.replaceEditText(target)

        project.getTranslationInfo(seg) = target

    }
    console.println(">>> Translated with DeepL")
}
return
/* @todo:
- test in the command line 
- 
*/

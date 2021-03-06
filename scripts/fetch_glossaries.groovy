/* :name = Fetch Glossaries :description =Download langauge assets for a specific container and language pair
 *
 *  @version: 0.3.1
 *  @author: Manuel Souto Pico, thanks to Kos Ivantsov and Briac Pilpré for advice and review
 *  @date: 2020.05.29
 *  @properties: https://cat.capstan.be/OmegaT/project-assets.json
 */

/*
 Changes:
 * version 0.3.0 (2020.05.29): basic authentication added
 * version 0.3.1 (2020.11.25): fetch only glossaries (TMs removed)
*/

// in the properties file, instead of literal paths, use patterns with placeholders (*)
// in the properties file, add filters (e.g. "en2021" for PISA 2024, etc.)

/* todo:
 * - merge TMs and put them in TM/_Merged or TM/OmegaT
 * - download glossaries
 * - download style guide data
 */

/*
 *  Installation (automated with cApStAn's customization)
 *  - From OmegaT, go to Options > Access Configuration Folder
 *  - Navigate to the "scripts" folder, "project_changed" subfolder
 *  - Put the script (this file) in the "project_changed" folder
 *  e.g. C:\Users\manuel\AppData\Roaming\OmegaT\scripts\project_changed\container_customizer.groovy
 *
 */

// first check: is the project being opened?
import org.omegat.core.events.IProjectEventListener.PROJECT_CHANGE_TYPE
title = "Language assets management (manual)"
// if (eventType != null && eventType != '') {
// 	console.println("eventType is " + eventType)
// } else {
// 	console.println("no eventType found")
// }
// console.println("eventType: " + eventType)
// // 	// the different stages of the project changes are CLOSE, COMPILE, CREATE, LOAD, SAVE and MODIFIED
// if (eventType != PROJECT_CHANGE_TYPE.LOAD) {
// 	console.println("This script only runs when the project is loading.")
// 	return
// }
// console.println("eventType: " + eventType)
// else eventType is LOAD: continue...

// manual:
// first check: is a project open?
if ( !project.projectProperties ) {
	noprj_prompt = "No project is open, a project needs to be open to run this script."
	console.println(noprj_prompt)
	noprj_prompt.alert()
	return
}

// modules
import groovy.util.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
import org.omegat.util.StaticUtils
import java.security.MessageDigest
import static groovy.io.FileType.FILES


import org.omegat.core.events.IApplicationEventListener

import groovy.swing.SwingBuilder
import groovy.util.XmlSlurper
//import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.FlowLayout
import java.awt.GridBagConstraints as GBC
import java.awt.GridBagLayout as GBL
import java.nio.file.Files
import java.nio.file.Paths
import java.util.zip.ZipFile
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JProgressBar
import javax.swing.WindowConstants
import org.apache.commons.io.FileUtils
import org.omegat.CLIParameters
import org.omegat.util.Preferences
import org.omegat.util.StaticUtils
import org.omegat.util.StringUtil
import static javax.swing.JOptionPane.*
import static org.omegat.util.Platform.*

// unused
utils = (StringUtil.getMethods().toString().findAll("makeValidXML")) ? StringUtil : StaticUtils

String.metaClass.confirm = { ->
    showConfirmDialog null, delegate, title, YES_NO_OPTION
}

String.metaClass.alert = { ->
    showMessageDialog null, delegate, title, INFORMATION_MESSAGE
}


// the script starts here if a project is open
console.println("="*40 + "\n" + " "*5 + "Container assets management\n" + "="*40)

// constants
digest = MessageDigest.getInstance("MD5") // not using def gives the variable global scope
prop = project.getProjectProperties()
proj_name = prop.projectName
// The container is the first entity in the project name (before the first underscore)
container = (proj_name =~ /^[^_]+(?=_)/)[0]
tgtlang_iso = (proj_name =~ /(?<=_)[a-z]{3}-[A-Z]{3}(?=_)/)[0]
// def root = prop.getProjectRoot()
omegat_dir = prop.getProjectInternal()
glossary_dir = prop.getGlossaryRoot()
tmdir_fs = prop.getTMRoot() // fs = forward slash
tmdir = new File(tmdir_fs)
reftm_dir_str = tmdir_fs + "ref"
reftm_dir = new File(reftm_dir_str) // the File object includes backslashes on Windows


timestamp = new Date().format("YYYYMMddHHmm")
config_dir = StaticUtils.getConfigDir()
config_file = new File(config_dir + "containers_config.properties")

// def tgt_code = 			project.projectProperties.targetLanguage
// def src_code = 			project.projectProperties.sourceLanguage
// def tgt_lang_subtag = 	project.projectProperties.targetLanguage.languageCode
// def src_lang_subtag = 	project.projectProperties.sourceLanguage.languageCode
// def tgt_region_subtag = project.projectProperties.targetLanguage.countryCode
// def src_region_subtag = project.projectProperties.sourceLanguage.countryCode

// functions
log_echo = { msg ->
    if (log_file.exists()) {
		// todo: if it exists and is not empty, do nothing
        log_file.append(msg + "\n", "UTF-8")
        console.println(msg)
    } else {
		// otherwise, document when the asset was downloaded (just once, at the end of the script)
        log_file.write(msg + "\n", "UTF-8")
        console.println(msg)
    }
}

// https://www.baeldung.com/groovy-file-read
String read_file_string(String path_to_file) {
    File file = new File(path_to_file)
    String file_content = file.text
    return file_content
}

def download_asset(remote_asset_name, domain, assets_dir) {
	try {
		def url_to_remote_asset = domain + "files/" + remote_asset_name
		def dest_file = new File(assets_dir.toString() + File.separator + remote_asset_name)
		dest_file.withOutputStream { output_stream  ->
			def url = new URL(url_to_remote_asset).openConnection()
			def remote_auth = "Basic " + "${username}:${password}".bytes.encodeBase64()
			url.setRequestProperty("Authorization", remote_auth)
			output_stream << url.inputStream
			// FileUtils.copyInputStreamToFile(url_to_remote_asset.toURL().openStream(), dest_file)
		}
	} catch (IOException e) {
		// unable to download asset
		console.println("Unable to download asset: " + e.message)
		return
	}
}

def update_assets(domain, tgtlang, dest, ext) {

	// get credentials
	path_to_creds = config_dir + "assets" + File.separator + "creds.txt"
	creds = read_file_string(path_to_creds)
	username = creds.split(':')[0]
	password = creds.split(':')[1]

	try {
		url_to_hash_list = domain + hash_filename
		// def local_path = new File(config_dir.toString() + File.separator + "asset_hashlist.txt")
		def url = new URL(url_to_hash_list).openConnection()
		def remote_auth = "Basic " + "${username}:${password}".bytes.encodeBase64()
		url.setRequestProperty("Authorization", remote_auth)
		hash_list = url.inputStream.readLines()
	}  catch (IOException e) {
		// last_modif will stay an empty array
		console.println("Unable to download hash list: " + e.message)
		return // stop script if list of hashes not available?? or just download everything found?
	}

	// return
	//
	// // get remote list of hashes for glossaries
	// try {
	// 	def url_to_hash_list = domain + hash_filename
	// 	console.println(url_to_hash_list)
	// 	// @Kos: check to make sure there's internet connection
	// 	url_to_hash_list.toURL().openStream()
	// 	hash_list = url_to_hash_list.toURL().readLines()
	// 	// to download the file
	// 	// def destination = new File(omegat_dir.toString() + File.separator + "asset_hashlist.txt")
	// 	// FileUtils.copyInputStreamToFile(url_to_hash_list.toURL().openStream(), destination)
	// } catch (IOException e) {
	// 	// last_modif will stay an empty array
	// 	console.println("List of hashes not found in server: " + e.message)
	//     return // stop script if list of hashes not available?? or just download everything found?
	// }

	if (!hash_list) {
		console.println("No hash list found, unable to continue.")
		return
	}
	hash_list.each {
	}

	// put remote list of hashes in array (filename -> hash)
	//
	// Pattern re = ~/${container}_(?:.*?)_Glossary_${tgt_code}/
	// def match = re.matcher(str).asBoolean()
	// filtered by target language
	hash_list = hash_list.findAll { it.contains("${tgtlang}") }
	// filtered by container
	hash_list = hash_list.findAll { it.contains("${container}")}
	// filtered by extension / file type
	hash_list = hash_list.findAll { it.contains("${ext}")}
	// moveing the list to a map
	def remote_file_hash_map = hash_list.collectEntries {
		def hash = it.split(':')[0]
		def file = it.split(':')[1]
		[(file): hash]
	}
	remote_file_hash_map.each {
	}
	// get local assets and their hashes
	def assets_dir = new File(dest) // either glossary_dir or tm_dir
	def assets_in_project = []
	def local_file_hash_map = [:]
	def ext_re = ~/${ext}/ // @todo: match container and langauge

	assets_dir.traverse(type: FILES, maxDepth: 0) {
		// @todo: should this be done asynchronously perhaps?
		// create object of Path
		Path path = Paths.get(it.path)
		// call getFileName() and get FileName as a string
		String asset_name = path.getFileName()
		def is_glossary = asset_name =~ ext_re
		// assert m instanceof Matcher
		if (is_glossary) {
			assets_in_project.add(it)
			// https://128bit.io/2011/02/17/md5-hashing-in-python-ruby-and-groovy/
			def asset_hash = new BigInteger(1,digest.digest(it.getBytes())).toString(16).padLeft(32,"0")
			local_file_hash_map.put(asset_name, asset_hash)
		}
	}

	// compare remote to local and get updated assets
	remote_file_hash_map.each {
		remote_asset_name = it.key
		remote_asset_hash = it.value


		def message
		// if the remote asset is found in the assets folder
		def downloaded = local_file_hash_map.containsKey(remote_asset_name)
		if (downloaded) {
			def local_asset_hash = local_file_hash_map.find{ it.key == remote_asset_name }?.value
			if (local_asset_hash) {
				if (local_asset_hash == remote_asset_hash) {
					message = "Remote asset ${remote_asset_name} hasn't changed, the local copy is up to date."
				} else {
					message = "Remote asset ${remote_asset_name} has been updated, a new download is needed."
					// download
					download_asset(remote_asset_name, domain, assets_dir)
				}
			}
		} else {
			// download
			// check if it exists locally, if it does, then msg:
			message = "Remote asset ${remote_asset_name} is new or had not been downloaded, will be downloaded now."
			// download
			download_asset(remote_asset_name, domain, assets_dir)
		}

		console.println(message)
	}
	console.println("Done!\n")
}

// glossaries
// function parameters: domain, container, tgtlang, dest, ext
tb_domain = "https://capps.capstan.be/test_assets/" // @todo: get from properties file
tgtlang = tgtlang_iso
// def list_url = 			tb_domain + "list_contents.php"
hash_filename = "hash_list.txt"
destination = glossary_dir
extension = "utf8"
update_assets(tb_domain, tgtlang, destination, extension)

reftm_dir.mkdirs()
// tm_domain = tb_domain
tm_domain = "https://capps.capstan.be/TM/"
destination = reftm_dir_str
extension = "tmx"
// update_assets(tm_domain, tgtlang, destination, extension)
//

// console.println("eventType: " + eventType)
// console.println("Collecting garbage...")
System.gc()
// console.println("All garbage collected. Ready for next run.")
return

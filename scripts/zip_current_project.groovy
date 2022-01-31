/* :name=       Zip OmegaT project :description=
 * 
 * 
 *              THIS SCRIPT WAS SPONSORED BY
 *                 ==   cApStAn sprl   ==
 *                     www.capstan.be
 * 
 * 
 * #Purpose:    Close the current project and pack it as a zip file named as the project. 
 * #Files:      Writes zip file in the current project's root folder containing all the files 
 *              included in the current project's root folder.
 * #Steps:      1. Get the current project name/location
 *              2. Close the project
 *              3. Pack the directory found in p.1, into a zip file.
 *              4. Open the project again.
 *
 * 
 * @author:     Kos Ivantsov
 * @date:       2019-02-15
 * @version:    0.4
 */
 
import java.awt.Desktop
import java.util.zip.ZipFile
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.util.regex.Pattern
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.omegat.core.Core
import org.omegat.util.Preferences
import org.omegat.util.StringUtil
import org.omegat.core.CoreEvents

import static javax.swing.JOptionPane.*
import static org.omegat.util.Platform.*

utils = (StringUtil.getMethods().toString().findAll("format")) ? StringUtil : StaticUtils
saveInProject = true
name="Zip OmegaT project"
description="Zip current OmegaT project"
noProject="No project loaded."
createZip="Creating {0}..."
createdZip="{0} created."
deleteOldZip="Removing previously saved {0}..."
moveZip="{0} moved to {1}"
loadProject= "Loading the project again..."
finalMessage="""The project has been zipped and is ready for upload.\n\
<html>You can find in by clicking <b>Project</b> → <b>Access Project Contents</b> → <b>Root</b>.</html>\n\
Do you want to open the project root folder now?"""

resBundle = { k,v ->
    try {
        v = res.getString(k)
    }
    catch (MissingResourceException e) {
        v
    }
}

title = resBundle("name", name)

String.metaClass.confirm = { ->
    showConfirmDialog null, delegate, title, YES_NO_OPTION
}
String.metaClass.alert = { ->
        showMessageDialog null, delegate, title, INFORMATION_MESSAGE
        false
}

prop = project.projectProperties
if (!prop) {
    message = resBundle("noProject", noProject)
    console.clear()
    console.println(title + "\n" + "="*title.size() + "\n" + message)
    message.alert()
    return
}

def dir = prop.getProjectRoot() 
def folder = new File(dir)
def name = folder.getName()
def parent = folder.getAbsoluteFile().getParent()
def zipFileName = saveInProject ? parent + File.separator + name + ".zip+" : parent + File.separator + name + ".zip"
def inFileName = dir + name + ".zip"
def zipFile = new File(zipFileName)
def inFile = new File(inFileName)

public class NonEmpty {
    private void putDummy(File folder) {
        folder.listFiles().each {
            try {
                if (it.isDirectory()) {
                    def subfiles = it.listFiles()
                    if (it.directorySize() == 0) {
                	    putDummy(it)
                        }
                    if ( subfiles.length == 0) {
                        new File (it, ".empty") << ''
                    }
                } 
            } catch (IOException e) {
                e.printStackTrace()
                }
        }
    }
}

public class ZipFiles {
    List<String> filesListInDir = new ArrayList<String>()

    private void zipDirectory(File dir, String zipDirName) {
        try {
            populateFilesList(dir)
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipDirName)
            ZipOutputStream zos = new ZipOutputStream(fos)
            for(String filePath : filesListInDir){
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()).replace("\\", "/"))
                zos.putNextEntry(ze)
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath)
                byte[] buffer = new byte[1024]
                int len
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len)
                }
                zos.closeEntry()
                fis.close()
            }
            zos.close()
            fos.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
    }

    private void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles()
        for(File file : files){
            if(file.isFile()) filesListInDir.add(file.getAbsolutePath())
            else populateFilesList(file)
        }
    }
}

if (saveInProject && inFile.exists()) {
	inFile.delete()
	console.println(utils.format(resBundle("deleteOldZip", deleteOldZip), inFileName))
}

new NonEmpty().putDummy(folder)
org.omegat.gui.main.ProjectUICommands.projectClose()

message = utils.format(resBundle("finalMessage", finalMessage))
Timer timer = new Timer().schedule({
    console.clear()
    console.println(utils.format(resBundle("createZip", createZip), zipFileName))
    sleep 5000
    new ZipFiles().zipDirectory(folder, zipFileName)
    console.println(utils.format(resBundle("createdZip", createdZip), zipFileName))
    org.omegat.gui.main.ProjectUICommands.projectOpen(folder)
    if (saveInProject) {
        zipFile.renameTo(inFileName)
        console.println(utils.format(resBundle("moveZip", moveZip), zipFileName, inFileName))
    }
    console.println(resBundle("loadProject", loadProject))
    if (message.confirm() == 0) {
        Desktop.getDesktop().open(new File(dir))
    }
    console.println("\n-----")
} as TimerTask, 1000)

return

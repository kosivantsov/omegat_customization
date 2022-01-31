/* :name=Clear Volatile and Backup Files :description=
 * @author  Kos Ivantsov
 * @date    2019-01-31
 * @version 0.3
 */

import javax.swing.JOptionPane
import org.omegat.util.StaticUtils
import org.omegat.util.StringUtil
import static javax.swing.JOptionPane.*
import static org.omegat.gui.main.ProjectUICommands.projectReload

name="Clear Volatile and Backup Files; No Bundle"
description="Removes backups of project_save.tmx file, export TMX files and created target files"
noproject="Please try again after you open a project"
deletemsg="""The contents of target folder\n\
<html><u><i>{0}</i></u>;</html>\n\
all the project's memory backup files\n\
<html><u><i>{1}omegat{2}project_save.tmx.*.bak</i></u></html>;\n\
<html><u><i>{1}omegat{2}project_save.tmx.#oldbased_on_*</i></u></html>;\n\
all the export memory files\n\
<html><u><i>{3}-*.tmx</i></u>;</html>\n\
as well as old copies of writable glossary\n\
<html><u><i>{4}#oldbased_on_*</i></u></html>\n\
will be deleted. Proceed?"""
notdel="Nothing deleted"
done="Files deleted!"

resBundle = { k,v ->
    try {
        v = res.getString(k)
    }
    catch (MissingResourceException e) {
        v
    }
}

utils = (StringUtil.getMethods().toString().findAll("format")) ? StringUtil : StaticUtils
title = resBundle("name", name)
String.metaClass.confirm = { ->
    showConfirmDialog null, delegate, title, YES_NO_OPTION
}
String.metaClass.alert = { ->
    showMessageDialog null, delegate, title, INFORMATION_MESSAGE
    false
}

prop = project.projectProperties
if ( ! prop ) {
    message = resBundle("noproject", noproject)
    console.clear()
    console.println(message)
    message.alert()
    return
}
dir = delTrg = {
    it.eachDir(dir)
    it.eachFile{ it.delete() }
}
omtfolder = prop.getProjectRoot() + "omegat"
rootfolder = prop.getProjectRoot()
glosfolder =  prop.getGlossaryRoot()
projname = new File(rootfolder).getName()
message = utils.format(resBundle("deletemsg", deletemsg), prop.getTargetRoot(), prop.getProjectRoot(), File.separator, "${prop.getProjectRoot()}$projname${File.separator}$projname", prop.getWriteableGlossary())
if ( message.confirm() == 0 ) {
    delTrg new File(prop.getTargetRoot())
    for(File f: new File(omtfolder).listFiles())
        if(f.getName().startsWith("project_save.tmx") && (f.getName().endsWith(".bak")) || f.getName().contains("#oldbased_on_"))
         f.delete()
    for(File f: new File(rootfolder).listFiles())
        if(f.getName().startsWith(projname) && f.getName().endsWith(".tmx"))
         f.delete()
    for(File f: new File(glosfolder).listFiles())
        if (f.getName().contains("#oldbased_on_"))
        f.delete()
    console.clear()
    console.println(resBundle("name", name) + "\n" + resBundle("done", done))
}else{
    console.clear()
    console.println(resBundle("name", name) + "\n" + resBundle("notdel", notdel))
}
return
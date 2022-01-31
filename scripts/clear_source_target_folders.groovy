/* :name=Clear Source and Target Folders :description=
 * @author  Kos Ivantsov
 * @date    2016-04-05
 * @version 0.1
 */

import javax.swing.JOptionPane
import static javax.swing.JOptionPane.*
import static org.omegat.gui.main.ProjectUICommands.projectReload

title = "Delete files"
String.metaClass.confirm = { ->
        showConfirmDialog null, delegate, title, YES_NO_OPTION
}
String.metaClass.alert = { ->
        showMessageDialog null, delegate, title, INFORMATION_MESSAGE
        false
}

prop = project.projectProperties
if ( ! prop ) {
    message = "Please try again after you open a project."
    console.clear()
    console.println(message)
    message.alert()
    return
}
dir = delSrc = delTrg = {
	it.eachDir(dir)
	it.eachFile{ it.delete() }
}

message = "The contents of source\n[${prop.getSourceRoot()}]\n\
and target\n\
[${prop.getTargetRoot()}]\n\
folders will be deleted. Proceed?"
if ( message.confirm() == 0 ) {
    delSrc new File(prop.getSourceRoot())
    delTrg new File(prop.getTargetRoot())
    projectReload()
}else{
    console.println("Nothing deleted, the project is not going to be reloaded.")
    return
}

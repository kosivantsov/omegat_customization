/* :name=Strip HTML Tags :description=Remove formatting HTML tags in the current target or in selection
 * 
 * @author   Manuel Souto Pico (based on Strip tags)
 * @date     2018-11-30
 * @version  0.1
 */

import static javax.swing.JOptionPane.*
import static org.omegat.util.Platform.*

def gui() {
// abort if a project is not opened yet
def prop = project.projectProperties
if (!prop) {
  final def title = res.getString("title")
  final def msg   = res.getString("msg")
  console.clear()
  console.println(res.getString("name") + "\n${"-"*15}\n" + msg)
  showMessageDialog null, msg, title, INFORMATION_MESSAGE
  return
}

if (editor.selectedText){
	target = editor.selectedText
	}else{
	target = editor.getCurrentTranslation()
	}
if (target != null) {
target = target.replaceAll(/(<|&lt;)\/?(a|br|em|sup|u)[^&;<>]*?(>|&gt;)/, '')
}

if (editor.selectedText){
	editor.insertText(target)
	}else{
	editor.replaceEditText(target)
	}
}
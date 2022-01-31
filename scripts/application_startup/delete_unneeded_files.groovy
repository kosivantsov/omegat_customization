/* :name = Delete Unneeded Files :description =
 *
 * @author:  Manuel Souto Pico
 * @date:    2021-11-22
 * @version: 0.0.1
 *
 */

import org.omegat.util.StaticUtils

def delete_files(list_f) {
	// def files = new File(config_dir + File.separator + "files_to_delete.txt") as String[]
	def files = list_f as String[]

	files.each {
		f = new File(config_dir + File.separator + it)
		if (f.exists()) {
			console.println(">> File '" + it + "' deleted")
			f.delete()
		}
	}		
}

// let customization script run first
sleep(60000)

config_dir = StaticUtils.getConfigDir()

fname = "files_to_delete.txt"
list_f = new File(config_dir + File.separator + fname)
if (list_f.exists()) { delete_files(list_f) }
else { console.println("File " + fname + " not found") }

return

/* :name = Merge 2 TMX with different source  :description=
 * Purpose:     To merge two TMX files in the /tm folder, taking the source text of the first one
 *              and the target text of the second one, and combining them in one third TMX file.
 * #Files:      Expectes first file to be called Translator1.tmx and the second one to be called Translator2.tmx
 *              Renames Translator2.tmx as Translator2.tmx.fra-bak and outputs new Translator2.tmx
 * #Format:     TMX v.1.4
 *
 * @author      Manuel Souto Pico, Kos Ivantsov
 * @date        2019-04-15
 * @version     0.2.2
 */
import org.omegat.util.StringUtil

def prop = project.projectProperties
def targetLang = prop.getTargetLanguage()
def tmDir = prop.getTMRoot()

tmxOne = new File(tmDir.toString() + File.separator + "Translator1.tmx")
tmxTwo = new File(tmDir.toString() + File.separator + "Translator2.tmx")

console.println('Merging source text from\n' + tmxOne + '\nwith target text from\n' + tmxTwo + '...\n')

parser=new XmlSlurper()
parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false) 
parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)

if (tmxOne.exists()) {
        tmxOneEntries = parser.parse(tmxOne)
}
if (tmxTwo.exists()) {
        tmxTwoEntries = parser.parse(tmxTwo)
}

finalTMX_contents = new StringWriter()
preamble = """<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE tmx SYSTEM "tmx11.dtd">
<tmx version="1.1">
  <header creationtool="OmegaT" o-tmf="OmegaT TMX" adminlang="EN-US" datatype="plaintext" creationtoolversion="4.1.5_3_10485" segtype="sentence" srclang="en-ZZ"/>
  <body>
"""
end = """  </body>
</tmx>"""

def count = 0
while (count < tmxOneEntries.body.tu.size()) {
    def src_node = tmxOneEntries.body.tu[count].tuv[0].seg
    //def src_str = new groovy.xml.StreamingMarkupBuilder().bindNode(src_node) as String
    def src_str = StringUtil.makeValidXML(src_node.toString())
    def tgt_node = tmxTwoEntries.body.tu[count].tuv[1].seg
    //def tgt_str = new groovy.xml.StreamingMarkupBuilder().bindNode(tgt_node) as String
    def tgt_str = StringUtil.makeValidXML(tgt_node.toString())
    finalTMX_contents << """    <tu>
      <tuv lang="en-ZZ">
        <seg>${src_str}</seg>
      </tuv>
      <tuv lang="${targetLang}">
        <seg>${tgt_str}</seg>
      </tuv>
    </tu>
"""
    count++
}

String origPath = tmxTwo
tmxTwo.renameTo origPath.concat(".fra-bak")

finalTMX =  new File(tmDir + File.separator + "Translator2.tmx")
finalTMX.write(preamble + finalTMX_contents + end, "UTF-8")

console.println("Translator2.tmx has been re-built now with English as the source language.")

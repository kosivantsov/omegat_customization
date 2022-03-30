# OmegaT (cApStAn) customization

## Update 74_cs0 (2022-03-28)

* Scripts: Added `write_project2excel.groovy` again (which was gone for some reason).
* Scripts: Added `write_xliff.groovy` script, which exports the project as XLIFF.
* Config: Updated OMT packing/unpacking settings to prevent including Word files in the project package.
* Config: Updated match template: more compact and more informative (includes ID and filename now)
* Config: Updated autotext entries (Mathematical italic small x and some ordinal number superscripts)

## Update 73_cs0 (2021-11-29)

* Scripts: Added `goto_next_nonunique.groovy` script to go to the next repeated segment in the project.
* Config: Added `goto_next_nonunique.groovy` script to slow 10.

## Update 72_cs0 (2021-11-23)

* Scripts: Added `delete_unneeded_files.groovy` script to remove custom files that should not be there (anymore)

## Update 70_cs0 (2021-11-05)

* Scripts: Fixed bug with project not reloading after modifying XLIFF files (this reverts change in `pisaconv.groovy` made in 26_csp).

## Update 69_c0p (2021-08-18)

* Plugins: Updated OMT plugin for compatibility with OmegaT 5.x

## Update 68_c00 (2021-07-12)

* Config: Updated custom tags to protect '^inserts$'.

## Update 67_c00 (2021-04-12)

* Config: Enabled option to replace glossary matches in insert
* Config: Enabled option to notify glossary hits

## Update 66_c00 (2021-03-29)

* Config: Added filter settings to disable the standard OpenXML filter by default.

## Update 65_c00 (2021-03-23)

* Config: Updated custom pattern for tag validation (which were too agressive and locked all text in square or curly brackets)

## Update 64_c00 (2021-03-22)

* Config: Updated custom pattern for tag validation (to avoid locking the text inside the "a" tag)

## Update 63_c00 (2021-03-15)

* Config: Updated remove pattern for tag validation (to avoid matching standard tags for HTML with the remove pattern)

## Update 62_c00 (2021-02-18)

* Config: Updated remove pattern for tag validation

## Update 61_c00 (2021-02-16)

* Config: Updated custom tag and remove tag definitions: {COUNTRY} and [COUNTRY] now locked

## Update 60_cs0 (2020-01-04)

* Scripts: Added script 'Remove Redundant IDs in TMs' [remove_redundant_ids_in_tm.groovy]

## Update 59_cs0 (2020-11-30)

* Scripts: Customization script fix, v. 0.5.3 (remove folder <file.jar>)

## Update 58_csp (2020-11-30)

* Scripts: Customization script fix, v. 0.5.2
* Plugin: OMT package plugin, harmonized messages to user
* Plugin: OMT package plugin version 1.6.3, fixed bug not including log
* Plugin: Okapi filters updated to version 1.8-1.40.0-capstan
* Config: Added notification for alternative translations in Segment Properties
* Config: Added notification for repeated segments in Segment Properties

## Update 57_cs0 (2020-11-25)

* Scripts: Updated `pisaconv.groovy` to turn ETS's XLIFF files compatible with OmegaT to remove DOCTYPE
* Scripts: Fixed customization script (more of Lev's fixes), v. 0.5.1

## Update 56_cs0 (2020-11-24)

* Updated customization URL (removed dev/ from team branch)

## Update 55_cs0 (2020-09-18)

* Fixed customization script (Lev's fixes), v. 0.5.0

## Update 54_cs0 (skipped)

## Update 53_cs0 (2020-07-06)

* Script: Container assets manager reads credentials from config files

## Update 52_c0p (2020-07-03)

* OMT packages: Updated names: "Unpack/Pack project" instead of "Export/Import  OMT file" to avoid confusion with "Import source/Export target files".

## Update 51_c0p (2020-07-01)

* OMT packages: Improved logs to the OMT plugin project for every packing action: including user ID, plugin version, project and package name, excluded content and packed content.

## Update 50_cs0 (2020-06-24)

* Config: Credentials for authenticated access to cApps added to config files
* Script: Container assets manager includes URL for cApps glossaries

## Update 49_cs0 (2020-06-23)

* Script "Container assets management (manual)": load event type check disabled on manual run
* Script "Container assets management (auto)": Automated execution from `scripts\project_changed\container_assets.groovy` disabled (temporarily)

## Update 48_cs0 (2020-06-23)

* Container assets management script now runs only on project load
* Script `container_assets.groovy` moved (hopefully temporarily) to manual mode, due to conflict in the `project_changed` folder (bug 860)

## Update 47_cs0 (2020-06-17)

* Updated version_notes.txt

## Update 46_0s0 (2020-06-17)

* New script for fetching container-specific language assets [`container_assets.groovy`]

## Update 45_cs0 (2020-06-11)

* Customization script: Updated to account for mk headers in the version_notes.txt file.

## Update 44_c0p (2020-06-11_dev)

* OMT packages: Added progress message for unpacking projects in the status bar (OMT plugin v. 1.6.0)
* OMT packages: Added logs to the OMT plugin project for every packing action: `${timestamp}: Packed with OMT plugin version X.X.X.`
* OMT packages: Re-enabled prompt to remove OMT after unpacking project
* OMT packages: Updated configuration to avoid including Mac's hidden files (starting with `._`) when packing the project
* Autotext: Added new `\minus` autotext shortcut for n-dash (U+2013)
* View: Temporarily disabled default bidi marks (overlapping the new bidimarkers plugin)
* Search: Include orphan segments in searches
* Tags: Disallowed tag definitions updated to account for `<i0>`-like Okapi tags

## Update 43_c00 (2020-05-04_dev)

* Autotext: Added Arabic percentage symbol to the autotext table

## Update 42_csp (2020-04-27_dev)

* Updated custom tag to avoid matching Okapi Open XML filter's tags as disallowed text.
* Added plugin `omegat-bidimarkers-0.2.0-all.jar` to improve painting of bidirectionality embeddings
* Updated Okapi plugin to `okapiFiltersForOmegaT-1.6-m40-custom.jar` to include Okapi's Open XML filter
* Updated OMT plugin `plugin-omt-package-1.4.1.jar` to add option "Pack and delete project".
* Added script "Restore files order" [`restore_files_order.groovy`] that restores original file order on project load to keep segment numbers unchanged

## Update 41_0s0 (2020-04-14_dev)

* Added customization script [`updateConfigBundle.groovy`] v0.4.8 with autoLaunch to install_dir/plugins/application_startup

## Update 40_0s0 (2020-04-14_dev)

* Updated customization script [`updateConfigBundle.groovy`] to version 0.4.8 to include `autoLaunch` and removing `install_dir/plugins`

## Update 39_csp (2020-04-30)

* Updated script **Xliterate Serbian Project (Latin to Cyrillic)**  [`xliter8_latn2cyrl.groovy`] to accomodate Serbian spelling specificities

## Update 38_c00 (2020-03-11)

* Updated OMT plugin settings to exclude target files but not target folder
* Added script Copy Source to slot #8 in the list of scripts

## Update 37_c00 (2020-03-10)

* Updated custom tags to remove some incorrect forbidden patterns (e.g. `<<t0/>O'zbekiston<t1/>>`, `48 < 57 so less than half`, etc.).
* Updated OMT plugin settings to exclude .7z and .rar packages inside the project when packing

## Update 36_c00 (2020-03-09)

* Updated OMT plugin settings to exclude .empty files from the source folder

## Update 35_csp (2020-03-04)

* Fixed bug in transliteration script (it used source, not target)
* Fixed bug in transliteration script (segments containing only tags were left untranslated)
* Updated OMT plugin to version 1.4.1 to include logs
* Updated OMT plugin settings to exclude target files and keep active TM's backups
* Added script (copy_source.groovy) to populate the target segment with the source text, if untranslated.

## Update 34_c00 (2020-01-29)

* Fixed non-breaking space autotext entry

## Update 33_csp (2020-01-24)

* OMT plugin 0.4.0: new version to have relative paths in exclude patterns
* OMT plugin settings: final project TMs at project root excluded from package

## Update 32_c00 (2020-01-24)

* OMT plugin settings: all final project TMs NOT excluded from package

## Update 31_cs0 (2020-01-22)

* Added script to pseudo-translate (pseudoxlate.groovy)
* OMT plugin settings: all final project TMs excluded from package

## Update 30_csp (2020-01-09)

* Updated script updateConfigBundle.groovy (to version 0.4.6) to update portable installations.

## Update 29_csp (2019-12-23)

* Disabled prompt asking to remove OMT file after import
* Update updateConfigBundle.groovy script to:
  - allow custom update in config dir for participants.
  - include URL rather than ask the user.

## Update 28_csp (2019-12-19)

* Added script to transliterate Latin to Cyrillic (xliter8_latn2cyrl.groovy)
* Updated Okapi plugin to force approval of all segments on project load (to version 1.6-m36-capstan.jar)

## Update 27_csp (2019-11-18)

* Updated OMT plugin to v. 1.3.0, now compatible with new versions of OmegaT. Users should not update or download OmegaT from links other than the one in the UG, but if they do that shouldn't break the plugin.

## Update 26_csp (2019-10-04)

* Added autotext shortcut to insert non-breaking hyphen
* Tweaked Okapi plugin to approve all segments on load
* Tweaked pisaconv.groovy script does not force reload any more
* Added FlushUneditedEntries.groovy script to restore alternative translations in bilingual XLIFF files

## Update 25_cs0 (2019-09-17)

* Updated URL to download hunspell dictionaries
* Added language kk-MN / kaz-MNG to conversion script
* Added language bs-RS / bos-SRB to conversion script
* Updated language az-GE / azj-GEO to conversion script

## Update 24_c00 (2019-08-08)

* Fixed bug with Word files getting removed from the source folder

## Update 23_c00 (2019-08-01)

* Added some new international languages (-ZZZ) for PIAAC C2 Doorstep Interview

## Update 22_csp (2019-07-03)

* Teak custom tags to avoid protecting some <adaptations>
* Korean currency symbol added to the character map (₩)

## Update 21_csp (2019-06-21)

* Updated validation patterns (custom tags, fragments to be removed)
* New version (1.1.0) of OMT plugin, can now be run on the command line
* Updated path to spell checking files (to fix bug: button not active)
* Updated script to export in Excel (now called write_project2excel.groovy) which now includes tu's ID

## Update 20_csp (2019-05-31)

* Protection added for numeric character entities (&#x203A)
* Protection added for localization placeholders (%s)
* Orphan segments not shown anymore in concordance searches
* Tagwipe script added
* New version of OMT plugin, fixes .empty issue

## Update 10_csp (2019-05-07)

* New shortcut to insert apostrophes
* <strong> now matched as tag and not as forced adaptation
* Added script to convert Western to Arabic-Indic numbers globally
* Added Eastern Arabic numbers to Autotext and Character Map
* Customized colors for x-auto (pink) and x-enforced (orange)

## Update 9_csp (2019-04-15)

* URL to spellchecking files tweaked
* OMT package plugin updated to v. 1.0.5 (errors on CLI fixed)
* Deletes file config/MainMenuShortcuts.properties
* Added script write_project2TMX.groovy to export the whole project as TMX (including repetitions)

## Update 8_cs0 (2019-04-02)

* Translated tags out of order not allowed

## Update 7_csp (2019-04-01)

* Path in the zipped entries contains "/" instead of "\"
* OMT package plugin saves project before packing
* Customization script added

## Update 6_csp (2019-03-25)

* MT auto fetch disabled
* Issue with spaces in username fixed
* Custom tags updated (added: sub, span, var)
* Color of remove pattern changed to purple (e.g. forced adaptations)
* OMT plugin will ask if imported OMT package is to be deleted

## Update 5_c00 (2019-03-20)

* Custom chartable fixed
* Search dialog sync options disabled
* Tags not counted in statistics
* Shortcut issues fixed
* Custom tags updated

https://cat.capstan.be/OmegaT/index.php

#!/usr/bin/env bash

prefs_file="omegat.prefs"
file_templ="omegat.prefs.template"

wget -0 $file_templ https://gist.githubusercontent.com/msoutopico/ff9b52d2d10f165fa9fdc3f4000559b8/raw/9df5b039007f0998eacba84d8c62ca4f316053a2/omegat.prefs.template

for node_path in $(xmlstarlet el $file_templ | grep -v -P 'omegat$|preference$')
do
    #node="$(echo "$node_path" | cut -d'/' -f3)"
    value="$(xmlstarlet sel -t -v "$node_path" $file_templ | xmlstarlet unesc)"
    xmlstarlet ed --inplace -u $node_path -v "$value" $prefs_file
done

# double quotes are necessary in value variables to avoid "failed to load external entity" error
#perl -pi -e 's/&amp;/&/g' $prefs_file
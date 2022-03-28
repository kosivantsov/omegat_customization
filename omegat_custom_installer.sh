#!/usr/bin/env bash

#  This script is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This script is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with cApps.  If not, see <https://www.gnu.org/licenses/>.

# ############# AUTHORSHIP INFO ###########################################

# __author__ = "Adrien Mathot, Manuel Souto Pico"
# __copyright__ = "Copyright 2022, cApps/cApStAn"
# __license__ = "GPL"
# __version__ = "0.2.0"
# __maintainer__ = "Manuel Souto Pico"
# __email__ = "manuel.souto@capstan.be"
# __status__ = "Testing"

# Script to install and customize OmegaT in both desktops and servers


# changelog

#v1.5 26/08/2019 Have it download the VER zip files + checking if OMT is installed or not before downloading it
#v1.4 22/05/2019 Tweaks for Ubuntu 19.04
#v1.3 25/04/2019 Update to OMT 4.2
#v1.2 29/03/2019 Update for scripts in the /scripts folder
#v1.1 18/03/2019 Change of some paths on cat.capstan.be
#v1.0 22/02/2019 First version

## execution

## bash -c "$(curl -fsSL https://raw.githubusercontent.com/capstanlqc/omegat_customization/master/omegat_custom_installer.sh)" 

# constants
VERSION="5.7.1"

# functions
die() { echo "$*" 1>&2 ; exit 1; }

function write_to_file() {
  # text <- $1
  # file <- $2
	touch $2
	echo $1 >> $2
}

echo ""
echo "===================================================="
echo ":: OMEGAT ${VERSION} :: INSTALLATION AND CUSTOMIZATION ::"
echo "===================================================="
echo ""

echo "OmegaT ${VERSION} will be installed and customized in this Linux machine." #@todo: better introduction, options -s (silent)
# @ inform of dependencies: git
# check: is_installed=$(git --version | wc -l) -> must be 1
# @todo: choose desktop or server 



if [ -d /opt/omegat/OmegaT_$VERSION ]
then
  echo ""
  echo "An installation of OmegaT $VERSION is alredy detected in this machine."
  echo "Would you like to re-install it (overwriting the existing installation) and customize it?"
  echo ""
  PS3="
Please enter your choice: "
  options=( "Yes, please re-install and customize" 
            "No, do not re-install but do customize"
            "Quit")

  select opt in "${options[@]}"
  do
      case $opt in
          "Yes, please re-install and customize")
              echo "OmegaT will be re-installed customized on top of the existing installation"
              break
              ;;
          "No, do not re-install but do customize")
              echo "OmegaT will not be re-installed but will be customized"
              break
              ;;
          "Quit")
              die "You have stopped the installation and customization process."
              break
              ;;
          *) echo "! Invalid option $REPLY";;
      esac
  done
fi
#echo $opt
REINSTALL_CHOICE=$REPLY

if [ ! -d /opt/omegat/OmegaT_$VERSION ]
then
  echo ""
  echo "I would like to know how you will be using OmegaT in this machine."
  echo "Could you please tell me what kind of machine this is?"
  echo ""
  PS3="
Please enter your choice: "
  options=( "Desktop" 
            "Server"
            "Quit")

  select opt in "${options[@]}"
  do
      case $opt in
          "Desktop")
              echo "OmegaT will be installed for usage with a graphical interface."
              break
              ;;
          "Server")
              echo "OmegaT will be optimized for execution on the command line."
              break
              ;;
          "Quit")
              die "You have stopped the installation and customization process."
              break
              ;;
          *) echo "! Iinvalid option $REPLY";;
      esac
  done
fi

INTERFACE_CHOICE=$REPLY

#Create a temporary folder in /home/, .capstan folder will contain the config
echo "Creating a temporary folder to download stuff..."
mkdir -p /home/$USER/.omegat/tmp
cd /home/$USER/.omegat/tmp


#check if OmegaT is already installed or not skipping the installation if it is the case
#Taken from OmegaT install script


#This is where the part taken for the OMT installer ends

#Download OmegaT
echo "Downloading and installing OmegaT..."
arch=`uname -m` #detect if computer is 32 or 64 bits

if [[ "$arch" == "x86_64" ]]
then
  # 64-bit 
  wget -O omegat.tar.bz2 https://downloads.sourceforge.net/project/omegat/OmegaT%20-%20Latest/OmegaT%20${VERSION}/OmegaT_${VERSION}_Beta_Linux_64.tar.bz2
  
  #extract OmegaT archive
  echo "Extracting OmegaT..."
  tar -jxf omegat.tar.bz2
else
  # 32-bit
  echo "The OmegaT installer for 32-bit architectures does not include a Linux x86 JRE. A JRE must have been installed independently in your machine, without which OmegaT will not be able to run."
  wget -0 omegat.zip https://downloads.sourceforge.net/project/omegat/OmegaT%20-%20Latest/OmegaT%20${VERSION}/OmegaT_${VERSION}_Beta_Without_JRE.zip

  #extract OmegaT archive
  echo "Extracting OmegaT..."
  unzip -qq omegat.zip
  #wget -0 omegat.tar.bz2 https://downloads.sourceforge.net/project/omegat/OmegaT%20-%20Latest/OmegaT%20${VERSION}/OmegaT_${VERSION}_Beta_Without_JRE.zip
fi

# cd to the extracted folder
cd OmegaT_${VERSION}*

#Run omegat installer
echo "Installing OmegaT..."
bash linux-install.sh
cd ..

# we are in ~/.omegat/tmp

# get custom files
echo "Downloading customization files..."
git clone https://github.com/capstanlqc/omegat_customization.git
cd omegat_customization

# remove stuff we don't need
rm todo.md
#@todo: rm this script, files_to_delete..

echo "Applying the customization..."
if [[ "$INTERFACE_CHOICE" == 1 ]] # gui
then 
  # update path to scripts directory
  SCRIPTS_DIR="/home/$USER/.omegat/scripts" #|| SCRIPTS_DIR="/opt/omegat/OmegaT_${VERSION}/scripts"
  perl -i -pe "s~(?<=<scripts_dir>)scripts~${SCRIPTS_DIR}~" omegat.prefs

  mkdir -p $SCRIPTS_DIR
  sudo cp -r /opt/omegat/scripts/* $SCRIPTS_DIR # move standar scripts to user config dir
  cp -r * /home/$USER/.omegat

else
  sudo cp -rf plugins scripts /opt/omegat/OmegaT_$VERSION # add add-ons to install dir
fi

#Create a launch command
#echo 'alias omegat_pisa="omegat --config-dir=/home/'$USER'/.capstan/config/ --config-file=/home/'$USER'/.capstan/config/omegat.prefs"' >> .bashrc
# source /home/$USER/.bashrc

#Clean up tmp folder
rm -rf /home/$USER/.omegat/tmp

echo "OmegaT customization has been installed!"


# --------------

# @todo
# remove index.php, list_files.txt, list_paths.txt 
# remove assets/creds.txt  
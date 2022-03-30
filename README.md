# OmegaT customization

For historical reasons, OmegaT is very opinionated in the factory settings it offers as regards user interface and some behaviour. Tweaking the default preferences by hand one by one is tedious and error-prone, especially for large teams where preferences are expected to be homogeneous across the team. This automated customization removes the burden of doing that manually from translators working in a team.

## What is customization

On the one hand, OmegaT provides certain preferences, where the option selected by default is not the most convenient one (as regard modern CAT tool best practices). On the other hand, OmegaT is very extensible by means of scritps and plugins. 

The customization consists of tweaked preferences and configuration files as well as script and plugins.

## Installing and customizing OmegaT

### Linux (both desktop and servers)

```
bash -c "$(curl -fsSL https://raw.githubusercontent.com/capstanlqc/omegat_customization/master/custo/omtlinux_custom_installer.sh)"
```

### Windows

Please follow [this guide](https://slides.com/capstan/omegat5-installation-and-customization-guide).

<!-- @TODO: write instructions for chocolatey in PowerShell -->

### macOS

Please follow [this guide](https://slides.com/capstan/omegat-installation-and-customization-guide-macos) (originally written for OmegaT version 4.3.2 but it should work too with latest versions).
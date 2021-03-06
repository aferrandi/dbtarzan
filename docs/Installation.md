---

layout: default
title: Installation
resource: true
categories: [Other]

---

## Installation

DBTarzan works in these [environments](Tested-databases-and-operating-systems).

To install DBTarzan is very easy, since it is one executable file. 
[Download](https://github.com/aferrandi/dbtarzan/releases);

### **DBTarzan-Install-XXX.exe** for Windows. 

An **installer** for Windows. Download it and execute it. If you get the message "Windows Smartscreen Defender prevented an unrecognized app from starting" click on **MoreInfo** and then **Run anyway** to run it.
The DBTarzan application is built directly from the code in https://github.com/aferrandi/dbtarzan and does not attempt to violate the security of your system.
This installation file was created with [Launch4j](http://launch4j.sourceforge.net/index.html) and with [NSIS](https://nsis.sourceforge.io).

### **dbTarzan_X.XX_all.deb** for debian based Linuxes. 

This installs as a Debian package automatically insuring that all the dependencies are instaled as well. 

### **DBTarzan-xxx.app.zip** for MacOS.

The file is not signed, therefore you need to confirm you want to open it. Move the downloaded file to the directory where it is supposed to be executed; in the [finder](https://www.lifewire.com/use-mac-finder-2260739) open the context menu with the right mouse button on the DBTarzan app file and press **Open**. When the file is opened in this way, the dialog that appears needs you to confirm that you want to open the file, but allows you to open it. After this it will be possible to open it just by double-clicking on it. 

The installation file is a simple zip file containing the DBtarzan app directory. The directory contains the DBTarzan jar (dbtarzan-XXX-assembly.jar), a Java 11 jre installation, the [launcher script universalJavaApplicationStub](https://github.com/tofi86/universalJavaApplicationStub) and a configuration file (Info.plist). The app is built using [jar2app](https://github.com/Jorl17/jar2app).

### **dbtarzan-XXX-assembly_YYY.jar** file for the other OSes. 

XXX is the version and YYY is the OS (windows, linux, mac).
Create a directory and move the downloaded file to it. In this case you need to [install](https://www.java.com/en/download/manual.jsp) Java yourself if it is not installed. Being this a simple jar file, it should be just a matter of double-clicking on it to let it start.
It can also be used to [solve problems](Troubleshooting) connecting to databases.


### Snap 

It is also possible to install it as a [snap](https://snapcraft.io/dbtarzan) on all the Linuxes where snap is available.



Once you installed DBTarzan, you can [connect to a database](Connect-to-database).

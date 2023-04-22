# TomcatExternalPropertyLoader
TomcatExternalPropertyLoader is a Tomcat Listener to read properties from one or more external property files.
Parameters:
fileList is a list of property files with absolute path
overwrite can be set to True(default) or False, and determines an already existing property needs to be overwrite with a value from the external property files 
## Example configuration
```
<Listener className="com.deejay.PropertyFileListener" fileList="C:\data\apps\Tomcat9\conf\config1.properties, C:\data\apps\Tomcat9\conf\config2.properties" overwrite="True"/>
```

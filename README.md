phraseapp-ant
=============

Ant Task for Uploading and Downloading Translation Files (Java *.properties, Google Web Toolkit [GWT] format) to and from PhraseApp

The "PhraseApp Ant Task" is an open source Ant Task to upload master translation files in the Java *.properties format to PhraseApp projects from your Ant environment and download translation files for all locales and tags in the Java *.properties format as well.

The Ant Task depends on Apache's common-lang3 library.

Note: 1. SSL verification against PhraseApp is disabled.
      2. The translation keys are prepended by the file name in order to avoid namespace conflicts, since PhraseApp has only one namespace per project. If the file name is too long, only the last part is taken. E.g. if a translation key 'foo' is in com.example.project.module.properties, then it will appear in PhraseApp as module.foo. When downloading the translations the prefix is removed again.
      3. When downloading the translation properties files from PhraseApp, specicial characters are encoded using the \u notation for UTF-8. Since GWT can handle UTF-8 properties files, the \u notation is unescaped before the translation files are stored on the local disk. 

Usage of Upload Target
----------------------

1. Install Ant, if you haven't already.


2. Then you need a PhraseApp account and project. In the "uploadTranslation" Ant task you need to define:
 - projectAuthToken: your PhraseApp project Auth Token, in order to find this out, login to PhraseApp, go to https://phraseapp.com/projects and copy your project's Auth Token
 - source: the location where your master translation files (Java *.properties files) are located, e.g. translation/en


3. In your build.xml add the target as seen below and replace the "uploadTranslation" task parameters with your own values (see above).

build.xml:

```xml
<?xml version="1.0" encoding="utf-8" ?>
<project name="MyProject" default="UploadTranslations" basedir=".">
	<target name="UploadTranslations" description="Upload Translations (PhraseApp)">
		<taskdef name="uploadTranslation" classname="com.mambu.ant.PhraseAppUpload" 
			classpath="lib/build/phraseapp-1.0.jar:lib/build/commons-lang3-3.1.jar" />
		<uploadTranslation source="relative/path/to/properties-files-directory" 
			projectAuthToken="yourproject-auth-token" />
	</target>
</project>
```

4. In order to run your Ant target open your console and go to the directory where your build.xml is located, run:

    $ ant UploadTranslations   

5. Alternatively you can add that Ant target to your Eclipse External Tool Configurations and trigger the upload right from you IDE.


Usage of Download Target
----------------------

1. Install Ant, if you haven't already.


2. Then you need a PhraseApp account and project. In the "downloadTranslation" Ant task you need to define:
 - projectAuthToken: your PhraseApp project Auth Token, in order to find this out, login to PhraseApp, go to https://phraseapp.com/projects and copy your project's Auth Token
 - destination: the absolute location where your translation files (Java *.properties files) should be copied to, e.g. '/users/username/project/src', for every sub-package in a tag a subdirectory will be created (e.g. src/com/orgname/module) and the locale code will be appended (e.g. a Spanish locale with the locale code 'es' and a tag with the name 'com.orgname.module.translations.properties' will be placed in src/com/orgname/module/translations_es.properties) 


3. In your build.xml add the target as seen below and replace the "downloadTranslation" task parameters with your own values (see above).

build.xml:

```xml
<?xml version="1.0" encoding="utf-8" ?>
<project name="MyProject" default="DownloadTranslations" basedir=".">
	<target name="DownloadTranslations" description="Download Translations (PhraseApp)">
		<taskdef name="downloadTranslation" classname="com.mambu.ant.PhraseAppDownload" 
			classpath="lib/build/phraseapp-1.0.jar:lib/build/commons-lang3-3.1.jar" />
		<property name="absolute.path.dir" location="relative/path/to/translations-directory"/>
		<downloadTranslation destination="${absolute.path.dir}" 
			projectAuthToken="yourproject-auth-token" />
	</target>
</project>
```

4. In order to run your Ant target open your console and go to the directory where your build.xml is located, run:

    $ ant DownloadTranslations   

5. Alternatively you can add that Ant target to your Eclipse External Tool Configurations and trigger the download right from you IDE.



Installation
------------

Add the jar from [lib/](https://github.com/mambu-gmbh/phraseapp-ant/tree/master/lib) and [build/](https://github.com/mambu-gmbh/phraseapp-ant/tree/master/build) into the lib/build/ folder in your project.


Developed By
-------------

The Ant Tasks were developed by Thomas Bachmann for Mambu GmbH.

License
--------
Copyright 2013 Mambu GmbH

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

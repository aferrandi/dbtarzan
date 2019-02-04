---

layout: default
title: Languages
resource: true
categories: [Code]

---

## Languages

DBTarzan supports multiple languages for the UI (menus, buttons, log).

To add a new language, for example **German**:

- Copy the file dbtarzan/localization/English.java to the new file **dbtarzan/localization/German.java** and change the name of the English class in the new file to **German**  Change all the quoted text from English to German.
- In the **dbtarzan/localization/Languages.java** file add to the **Languages** singleton object the line 
    
    ```val GERMAN = Language("german")```

  and in the line
    ```val languages = List(ENGLISH,...```

  add the GERMAN language.
- In the file **dbtarzan/localization/Localizations.java**, in the **match** clause in the **Localizations** singleton object add  the following line:
    ```case Languages.GERMAN => new German()```

That's all. After the application has been compiled and started, the German language will appear in the [Global Settings](Global-settings) **Languages** combo box.

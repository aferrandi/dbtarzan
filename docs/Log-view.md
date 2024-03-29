---

layout: default
title: Log View
resource: true
categories: [GUI]

---

## Log View

On the bottom is the log view.
It shows all the application messages, **informations**, **warnings** and **errors**.

![Log View](images/logView.png)

It is a scrollable view, showing the last messages on the top.
**Double-clicking** on a row opens a **dialog** showing the message details.

![Log Dialog](images/logDialog.png)

In the case of an error, only the error message is displayed.
If you want more information, press the Advanced button that shows the message with the complete stack trace.

![Advanced](images/logDialogAdvanced.png)

All the messages in this view together with the **debug** messages are written in a log file, called **dbtarzan.log**, 
located in the same directory as the configuration files. 


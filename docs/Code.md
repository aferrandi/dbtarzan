---

layout: default
title: Code
resource: true
categories: [Code]

---

## Code

DBTarzan is written in [Scala](https://www.scala-lang.org/), uses [JavaFX 8](https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm) via [ScalaFX](http://www.scalafx.org/) for its GUI, uses the actors of [Akka](http://akka.io/), Spray (now part of Akka) to read the configurations written in JSON. 

JavaFX does a wonderful job, giving the application a professional look and integrating it with the OS where it runs.

Using Akka, DBTarzan [does not block](Internal-Structure) waiting for the queries response, allowing the user to maintain always the control.

Thanks to these tools, the amount of code is very limited, which makes it easy to understand and to mantain.

One of the principles of DBTarzan is to avoid code bloating, keeping the code simple and compact.  
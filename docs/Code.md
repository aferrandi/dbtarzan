---

layout: default
title: Code
resource: true
categories: [Code]

---

## Code

DBTarzan is released under the [Apache License 2.0](https://github.com/aferrandi/dbtarzan/blob/master/LICENSE).
Its code is published in [Github](https://github.com/aferrandi/dbtarzan).

DBTarzan is written in [Scala](https://www.scala-lang.org/), uses [JavaFX](https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm) via [ScalaFX](http://www.scalafx.org/) for its GUI, uses the actors of [Pekko](https://pekko.apache.org/), [Grapple](https://github.com/losizm/grapple) to read the configurations written in JSON. 

JavaFX does a wonderful job, giving the application a professional look and integrating it with the OS where it runs.

Using Pekko, DBTarzan [does not block](Internal-Structure) waiting for the queries response, allowing the user to maintain always the control.

Thanks to these tools, the amount of code is very limited, which makes it easy to understand and to mantain.

One of the principles of DBTarzan is to avoid code bloating, keeping the code simple and compact.

If you want, you can [build the application yourself](Building).
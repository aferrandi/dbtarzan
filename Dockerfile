FROM java:jdk
COPY ./deploy/* /dbtarzan/
WORKDIR /dbtarzan
RUN java -cp \* dbtarzan.gui.Main
CMD ["java, "Main"]

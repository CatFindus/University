FROM tomcat:10.1

EXPOSE 8080
COPY ./build/libs/ROOT.war /usr/local/tomcat/webapps/
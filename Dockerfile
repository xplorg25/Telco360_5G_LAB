# Use an official Tomcat image from Docker Hub
FROM tomcat:9.0

# Set environment variables
ENV JAVA_OPTS="-Dspring.profiles.active=default"

# Copy the WAR file to the webapps directory
COPY target/Telco360_5G.war /usr/local/tomcat/webapps/

# Expose port 8080
EXPOSE 8080

# Start Tomcat server
CMD ["catalina.sh", "run"]

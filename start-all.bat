start /D microservices\support\discovery-server             gradlew bootRun
start /D microservices\support\edge-server                  gradlew bootRun

start /D microservices\core\patient-service                 gradlew bootRun
start /D microservices\core\observation-service          gradlew bootRun
start /D microservices\core\episode-service                  gradlew bootRun
start /D microservices\composite\patient-composite-service  gradlew bootRun 

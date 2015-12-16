pushd microservices\core\patient-service &                call gradlew clean publishToMavenLocal & popd
pushd microservices\core\observation-service &         call gradlew clean publishToMavenLocal & popd
pushd microservices\core\episode-service &                 call gradlew clean publishToMavenLocal & popd
pushd microservices\composite\patient-composite-service & call gradlew clean build & popd

pushd microservices\support\discovery-server &            call gradlew clean build & popd
pushd microservices\support\edge-server &                 call gradlew clean build & popd

pushd microservices\core\patient-service &                call gradlew clean publishToMavenLocal & popd
pushd microservices\core\recommendation-service &         call gradlew clean publishToMavenLocal & popd
pushd microservices\core\review-service &                 call gradlew clean publishToMavenLocal & popd
pushd microservices\composite\patient-composite-service & call gradlew clean build & popd
pushd microservices\api\patient-api-service &             call gradlew clean build & popd

pushd microservices\support\auth-server &                 call gradlew clean build & popd
pushd microservices\support\discovery-server &            call gradlew clean build & popd
pushd microservices\support\edge-server &                 call gradlew clean build & popd
pushd microservices\support\monitor-dashboard &           call gradlew clean build & popd
pushd microservices\support\turbine &                     call gradlew clean build & popd

#!/bin/bash

MYCUJOO_ENVIRONMENT=ping-pong-conference-talk
export GOOGLE_COMPUTE_ZONE=${GOOGLE_COMPUTE_ZONE_DEVELOPMENT}
export GOOGLE_PROJECT_NAME=${GOOGLE_PROJECT_NAME_DEVELOPMENT}
export GOOGLE_CLUSTER_NAME=${GOOGLE_CLUSTER_NAME_DEVELOPMENT}

echo "include \"application.${MYCUJOO_ENVIRONMENT}.conf\"" > src/main/resources/application.conf
cat src/main/resources/application.conf

sed -i 's/NAMESPACE_TO_REPLACE/'${MYCUJOO_ENVIRONMENT}'/g' src/main/resources/cluster.xml
sed -i 's/discovery-strategy enabled="false"/discovery-strategy enabled="true"/g' src/main/resources/cluster.xml
sed -i 's/tcp-ip enabled="true"/tcp-ip enabled="false"/g' src/main/resources/cluster.xml
sed -i 's/hazelcast.discovery.enabled">false/hazelcast.discovery.enabled">true/g' src/main/resources/cluster.xml

cat src/main/resources/cluster.xml | grep "vertx-discover\|discovery-strategy enabled=\|tcp-ip enabled=\|hazelcast.discovery.enabled"

curl https://dl.google.com/dl/cloudsdk/channels/rapid/downloads/google-cloud-sdk-230.0.0-linux-x86_64.tar.gz | tar xz
CLOUDSDK_CORE_DISABLE_PROMPTS=1 ./google-cloud-sdk/install.sh


# configure google cloud
echo ${GCLOUD_SERVICE_KEY} | base64 -d > ${HOME}/gcp-key.json
gcloud auth activate-service-account --key-file ${HOME}/gcp-key.json
gcloud --quiet config set compute/zone ${GOOGLE_COMPUTE_ZONE}
gcloud --quiet config set project ${GOOGLE_PROJECT_NAME}
gcloud --quiet container clusters get-credentials ${GOOGLE_CLUSTER_NAME}

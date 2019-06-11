Ping-pong


Distributed, clusterized, shipped as Docker images, deployed to Kubernetes, with its metrics collected via Micrometer and exposed to Prometheus for Grafana visualization

Parts of the project:
1) ping-verticle-demo (Java) https://github.com/gingerkirsch/ping-verticle-demo
A GraphQL endpoint that sends "ping" to Vert.x event bus via POST on https://<host>/graphql with following JSON body:

{
	"query": "query($secure: Boolean) { ping(secureOnly: $secure) }",
	"variables": {
		"secure": true
	}
}

2) pong-verticle-demo (Scala) https://github.com/gingerkirsch/pong-verticle-demo
Reads "ping" event from Vert.x event bus and if everything goes fine, responds with 200 OK and following JSON body:

{
    "data": {
        "ping": "pong from <id>"
    }
}

where id will demonstrate load balancer in action.

3) ping-pong-snitch (Java) https://github.com/gingerkirsch/ping-pong-snitch
Hooks for Vert.x instances that expose metrics using Micrometrics which are further exposed to Grafana


Prepared for Hopperx1 London and LX Scala conferences
2019

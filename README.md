# API Monitor
API Monitor is a distributed API monitoring to monitor the connectivity and correctness of HTTP/PRC APIs.

## Core functionality (To Be Developed)
- [x] Monitor the connectivity of HTTP GET APIs.
- [x] Support all Standalone, MQ, Rest API deployments.
- [ ] Support HTTP POST/PUT methods
- [ ] Validate the response of APIs with predefined expected result (compatible with OpenAPI standard)
- [ ] Visualise the result on a static website or in JSON/Markdown file.
- [ ] Support for chain of requests (for authentication chain or business chain)
- [ ] Support for RPC calls

## Compilation and Containerization

You can use the `build.sh` to build the docker images for the orchestrator and the runner, or use the commands to build yourself.

## Deployment

It enables four deployment ways:
- **Standalone**: Deployed on a single machine to monitor APIs from public or internal network
- **Orchestrator-Runner Orchestration with Message Queue**: Deployed one orchestrator and multiple runners to monitor APIs, recommend for internal network (due to the safety concern of MQ).
  - Support MQ: Kafka
- **Orchestrator-Runner Orchestration with Rest API**: Deployed one orchestrator and multiple runners to monitor APIs for public or internal network.
- **CI/CD pipeline**: Deployed on GitHub Actions and use cron trigger to regularly run it for occasional monitoring.

### Standalone

After building the image you can use the following command to start the standalone mode

```bash
docker run -d --name api-monitor-standalone -p 8080:8080 api-monitor/api-monitor-standalone
```



### Orchestration with Rest API

Firstly, start the orchestrator

```
docker run -d --name api-monitor-orchestrator -p 8080:8080 -e "SPRING_PROFILES_ACTIVE=http" api-monitor/api-monitor-orchestrator 
```

After that, use `docker logs ` to check the runner registration token of the orchestrator

```bash
docker logs api-monitor-orchestrator
#You will see something like:
#The register token for runner is: d362b17b-50a9-4d8b-8e14-75756934f8fc
```

Finally, use the token and the address of your orchestrator to start the runner

```bash
docker run -d --name api-monitor-runner -p 8081:8081 -e "SPRING_PROFILES_ACTIVE=http" -e "api-monitor_communication_rest-api_register-token=<YOUR-REGISTRATION-TOKEN>" -e "api-monitor_communication_rest-api_orchestrator=http://<YOUR-ORCHESTRATOR-DOMAIN>:8080" api-monitor/api-monitor-runner
```

